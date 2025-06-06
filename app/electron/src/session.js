import {spawn} from "child_process"
import {freemem, totalmem} from "os"
import {app} from "electron"
import path from "path"
import fs from "fs-extra"
import jszip from "jszip"
import {copyRecursive, countFiles, zipRecursive} from "./util.js";
import {download} from "electron-dl";
import log from "electron-log";

export class Session {
    _sessions = null;
    _sessionID = null;
    _window = null;
    _webContents = null;
    _connected = true;
    _closePromise = null;

    // Paths
    _sessionPath = null;

    // Settings
    _worldSettings = null;
    _pruningSettings = null;
    _finalName = null;
    _dimensionMappings = null;
    _blockMappings = null;

    // Handlers
    _asyncResponseMappers = {};

    // Constructor
    constructor(sessions, sessionID, window, webContents, javaOptions) {
        this._sessions = sessions;
        this._sessionID = sessionID;
        this._window = window;
        this._webContents = webContents;

        // Save the session
        sessions.set(sessionID, this);

        // Create the backing process
        let executable = process.env.DEV_CLI_EXECUTABLE;

        // Find the right file to use (also do this if the ENV is a path)
        if (!executable || executable.endsWith("/")) {
            let cliDirectory = executable ?? path.join(path.dirname(app.getAppPath()), "chunker-cli");

            // If the cli directory isn't found, try using .app on the end
            if (!fs.existsSync(cliDirectory) && fs.existsSync(cliDirectory + ".app")) {
                cliDirectory = path.join(cliDirectory + ".app", "Contents", "MacOS");
            }

            // Use the bin directory if it's present for the executable
            if (fs.existsSync(path.join(cliDirectory, "bin"))) {
                cliDirectory = path.join(cliDirectory, "bin");
            }

            // Find the executable for this platform
            let files = fs.readdirSync(cliDirectory, {withFileTypes: true})
                .filter(file => file.isFile() && file.name.startsWith("chunker-cli") && !file.name.endsWith(".ico") && !file.name.endsWith("-unshaded.jar"));

            // Close if there is no executable (this is a packaging issue)
            if (files.length === 0) {
                throw new Error("chunker-cli executable is missing!");
            }
            executable = path.join(cliDirectory, files[0].name);
        }

        // Attach JVM options (calculate memory if not set)
        if (javaOptions.indexOf("-Xm") === -1) {
            let maximumMB;
            if (process.platform !== "darwin") {
                // Use 75% of available memory (but ensure there is at least 1024MB free for the system)
                const freeMemoryMB = freemem() / (1024 * 1024);
                const desiredMB = freeMemoryMB * 0.75;

                // Ensure we leave at least 1GB free in memory for the system
                const reservedMB = 1024;

                // Ensure the VM gets at least 512MB of ram
                const requiredMB = 512;
                maximumMB = Math.max(Math.min(freeMemoryMB - reservedMB, desiredMB), requiredMB);
            } else {
                // Use 75% of total memory (but ensure there is 4096MB free for the system on mac)
                // Note: This is because on MacOS freemem() can be lower due to file caching etc
                const totalMemoryMB = totalmem() / (1024 * 1024);

                // Ensure the VM gets at least 512MB of ram
                const requiredMB = 512;
                maximumMB = Math.max(Math.min(totalMemoryMB - 4096, totalMemoryMB * 0.75), requiredMB);
            }

            let generatedOptions = "-Xmx" + Math.floor(maximumMB) + "M";
            javaOptions = javaOptions + (javaOptions.length > 0 ? " " : "") + generatedOptions;
        }

        // Execute as process or a jar
        if (executable.endsWith(".jar")) {
            this._process = spawn("java", ["-jar", executable, "messenger"], {
                env: {
                    ...process.env,
                    _JAVA_OPTIONS: javaOptions
                }
            });
        } else {
            this._process = spawn(executable, ["messenger"], {
                env: {
                    ...process.env,
                    _JAVA_OPTIONS: javaOptions
                }
            });
        }

        let buffer = "";
        this._process.stdout.on("data", (data) => {
            (async () => {
                buffer += data.toString();
                let lines = buffer.split("\n");
                for (let i = 0; i < lines.length - 1; i++) {
                    let line = lines[i];
                    if (line.length === 0) continue; // Skip empty lines
                    log.debug("Process output: ", line.trim());
                    try {
                        let obj = JSON.parse(line);

                        // If it's a progress message then it should continue listening for further updates
                        if (obj.type === "progress" || obj.type === "progress_state") {
                            obj.continue = true;
                        } else {
                            if (obj.type === "response" && obj.requestId && this._asyncResponseMappers[obj.requestId] !== undefined) {
                                // Apply response mapper if it's present
                                obj = await this._asyncResponseMappers[obj.requestId](obj);
                            }

                            // Ensure the mapper is removed
                            delete this._asyncResponseMappers[obj.requestId];
                        }

                        // Send to the client
                        this.sendMessage(obj);
                    } catch (e) {
                        // Error parsing
                        log.error(`Error parsing output from process: ${data}`, e)
                    }
                }

                // Update buffer with remaining content
                buffer = lines[lines.length - 1];
            })().catch((e) => log.error("Failed to process data for process", e));
        });
        this._process.stderr.on("data", (data) => {
            let value = data.toString().trim();

            // Ensure the JVM indicating what options it has is info and not an error
            if (value.startsWith("Picked up")) {
                log.info(`Info from process: ${value}`)
            } else {
                log.error(`Error from process: ${value}`)
            }
        });
        this._process.on("close", (code) => {
            // Close the session
            log.debug("Process exited with error code", code)
            this.close(code).catch((e) => log.error("Failed to close process", e));
        });

        // Setup output path
        this._sessionPath = path.join(app.getPath("temp"), "chunker-electron", this._sessionID);
        fs.mkdirSync(this._sessionPath, {recursive: true});
    }

    close(errorCode) {
        if (!this._connected) return Promise.resolve(); // Don't close twice

        // Run the close code inside a promise, so it can be monitored
        this._closePromise = (async () => {
            // Send the close message
            this.sendRaw({type: "close", code: errorCode});

            // Mark as not connected
            this._connected = false;

            // Remove our message listener
            this._webContents.ipc.removeAllListeners("message-" + this._sessionID);

            // Stop the process
            if (this._process) {
                this._process.kill();
            }

            // Call onClose
            await this.onClose(errorCode);
        })();

        // Return the promise
        return this._closePromise;
    }

    sendToProcess(obj, asyncResponseMapper) {
        // Save the response mapper (used to mutate the response)
        if (obj.requestId && asyncResponseMapper) {
            this._asyncResponseMappers[obj.requestId] = asyncResponseMapper;
        }

        // Write the request
        log.debug("Writing to process ", JSON.stringify(obj) + "\n")
        this._process.stdin.write(JSON.stringify(obj) + "\n");
    }

    sendMessage(obj) {
        this.sendRaw({type: "message", data: JSON.stringify(obj)});
    }

    sendRaw(obj) {
        if (!this._connected) return; // Don't send
        this._webContents.send("message-" + this._sessionID, JSON.stringify(obj));
    }

    async onEvent(event) {
        if (!this._connected) return; // Don't handle if not connected
        switch (event.type) {
            case "close":
                // Handle close
                await this.close(event.code);
                break;
            case "message":
                await this.onMessage(JSON.parse(event.data));
                break;
        }
    }

    async onClose(errorCode) {
        // Remove output
        if (this._sessionPath) {
            log.log("Deleting session data: ", this._sessionID);
            await fs.rm(this._sessionPath, {recursive: true, force: true});
        }

        // Remove the session
        this._sessions.delete(this._sessionID);
    }

    async onMessage(data) {
        switch (data.type) {
            case "flow":
                await this.onFlow(data);
                break;
            case "settings":
                await this.onSettings(data);
                break;
            case "mappings":
                await this.onMappings(data);
                break;
            default:
                log.log("Unhandled message type ", data.type, ", full message: ", data);
                break;
        }
    }

    async onFlow(data) {
        switch (data.method) {
            case "cancel":
                await this.cancelTask(data.requestId);
                break;
            case "save":
                await this.save(data.url, data.requestId);
                break;
            case "select_world":
                await this.selectWorld(data.path, data.requestId);
                break;
            case "generate_settings":
                await this.generateSettings(data.requestId);
                break;
            case "generate_preview":
                await this.generatePreview(data.requestId);
                break;
            case "convert":
                await this.convertWorld(data.outputType, data.requestId, data);
                break;
            default:
                log.log("Unhandled flow message ", data.method, ", full message: ", data);
                break;
        }
    }

    async save(url, requestId) {
        let responded = false;
        try {
            await download(this._window, url, {
                saveAs: true,
                onProgress: progress => {
                    if (isNaN(progress.percent)) return;

                    this.sendMessage({
                        requestId: requestId,
                        type: "progress",
                        percentage: progress.percent,
                        continue: true
                    });
                },
                onCancel: item => {
                    if (!responded) {
                        this.sendMessage({
                            requestId: requestId,
                            type: "error",
                            error: "Saving was cancelled."
                        });
                        responded = true;
                    }
                },
                onCompleted: completed => {
                    // Reply with success
                    if (!responded) {
                        this.sendMessage({requestId: requestId, type: "response"});
                        responded = true;
                    }
                }
            });
        } catch (e) {
            log.info("Saving failed", e);

            // Ensure we only response once
            if (!responded) {
                this.sendMessage({
                    requestId: requestId,
                    type: "error",
                    error: "Failed to save file.",
                    stackTrace: e.stack.toString() + "\n"
                });
                responded = true;
            }
        }
    }

    async onSettings(data) {
        switch (data.method) {
            case "set_world_settings":
                this._worldSettings = data.settings;

                // Reply with success
                this.sendMessage({requestId: data.requestId, type: "response"});
                break;
            case "set_pruning_settings":
                this._pruningSettings = data.settings;

                // Reply with success
                this.sendMessage({requestId: data.requestId, type: "response"});
                break;
            case "set_output_name":
                this._finalName = data.name;

                // Reply with success
                this.sendMessage({requestId: data.requestId, type: "response"});
                break;
        }
    }

    async onMappings(data) {
        switch (data.method) {
            case "set_block_mappings":
                this._blockMappings = data.mappings;

                // Reply with success
                this.sendMessage({requestId: data.requestId, type: "response"});
                break;
            case "set_dimension_mappings":
                this._dimensionMappings = data.dimensions;

                // Reply with success
                this.sendMessage({requestId: data.requestId, type: "response"});
                break;
        }
    }

    async cancelTask(requestId) {
        let request = {
            type: "kill",
            requestId: requestId,
            anonymousId: this._sessionID
        }
        this.sendToProcess(request);
    }

    async selectWorld(inputPath, requestId) {
        // Create the input directory
        let worldInputPath = path.join(this._sessionPath, "input");
        await fs.mkdir(worldInputPath);

        // Copy / Extract the world (if it's a zip)
        let pathStat = await fs.stat(inputPath);
        if (pathStat.isFile()) {
            // Extract zip
            try {
                let zipContents = await fs.readFile(inputPath);
                let zip = await jszip.loadAsync(zipContents);

                // Find the level.dat
                let levelDataFiles = zip.file(/level\.dat$/g);
                if (levelDataFiles.length === 0) {
                    // Reply with error
                    this.sendMessage({
                        requestId: requestId,
                        type: "error",
                        error: "Provided file does not contain a Minecraft world."
                    });
                    return;
                }

                let selectedDat = levelDataFiles[0];
                let pathPrefix = selectedDat.name.substring(0, selectedDat.name.lastIndexOf("/") + 1);
                let filesExtracted = 0;
                let totalFiles = Object.keys(zip.files).length;
                let lastProgress = 0;

                // Extract everything below the level.dat
                await Promise.all(Object.keys(zip.files).map(async (filename) => {
                    if (!filename.startsWith(pathPrefix)) return;

                    // Grab the file
                    const file = zip.files[filename];

                    // Join the paths to get the output path
                    const outputPath = path.join(worldInputPath, filename.substring(pathPrefix.length));

                    // Ignores paths which aren't safe
                    if (!path.normalize(outputPath).startsWith(worldInputPath)) {
                        return;
                    }

                    // Create the directory if it's one otherwise copy the file data
                    if (file.dir) {
                        await fs.mkdir(outputPath, {recursive: true});
                    } else {
                        // Ensure the directory is present
                        let outputPathFolder = path.dirname(outputPath);
                        await fs.mkdir(outputPathFolder, {recursive: true});

                        // Otherwise, write the file contents to the output path
                        const fileData = await file.async('nodebuffer');
                        await fs.writeFile(outputPath, fileData);
                    }

                    // Update progress
                    filesExtracted++;
                    let progress = filesExtracted / totalFiles;

                    // Only update the client if the progress differs by 1%
                    if (progress - lastProgress > 0.01) {
                        // Update client
                        this.sendMessage({
                            requestId: requestId,
                            type: "progress",
                            percentage: progress,
                            continue: true
                        });
                        lastProgress = progress;
                    }
                }));
            } catch (e) {
                log.error("Failed to read input zip", e);

                // Specific handling for file too large
                if (e.code === "ERR_FS_FILE_TOO_LARGE") {
                    this.sendMessage({
                        requestId: requestId,
                        type: "error",
                        error: "This zip file is too large to open, please unzip the file and try opening it as a folder.",
                        stackTrace: e.stack.toString() + "\n"
                    });
                    return;
                }

                // Reply with error
                this.sendMessage({
                    requestId: requestId,
                    type: "error",
                    error: "Failed to open selected file, please ensure you don't have it open anywhere else.",
                    stackTrace: e.stack.toString() + "\n"
                });
                return;
            }
        } else if (pathStat.isDirectory()) {
            // Copy files to the output path
            let totalFiles = await countFiles(inputPath);
            let filesCopied = 0;
            let lastProgress = 0;
            try {
                await copyRecursive(inputPath, worldInputPath, (file) => {
                    // Update progress
                    filesCopied++;
                    let progress = filesCopied / totalFiles;

                    // Only update the client if the progress differs by 1%
                    if (progress - lastProgress > 0.01) {
                        // Update client
                        this.sendMessage({
                            requestId: requestId,
                            type: "progress",
                            percentage: progress,
                            continue: true
                        });
                        lastProgress = progress;
                    }
                });
            } catch (e) {
                log.error("Failed to read input directory", e);

                // Reply with error
                this.sendMessage({
                    requestId: requestId,
                    type: "error",
                    error: "Failed to open selected folder, please ensure you don't have it open anywhere else.",
                    stackTrace: e.stack.toString() + "\n"
                });
                return;
            }
        } else {
            // Failed
            log.error("Failed to find input", inputPath);

            // Reply with error
            this.sendMessage({
                requestId: requestId,
                type: "error",
                error: "Failed to find input world."
            });
            return;
        }

        // Tell the user that we're detecting the world
        this.sendMessage({
            requestId: requestId,
            type: "progress_state",
            percentage: 0.999,
            animated: true,
            continue: true
        });

        // Create the detect version request
        let request = {
            type: "detect_version",
            requestId: requestId,
            anonymousId: this._sessionID,
            inputPath: worldInputPath
        }

        // Load preloaded data
        let preloaded_settings = {};

        // Loop through each file ending in .chunker.json
        let files = await fs.readdir(worldInputPath, {withFileTypes: true});
        for (let file of files) {
            if (file.isFile() && file.name.endsWith(".chunker.json")) {
                // Read the file
                try {
                    let contents = await fs.readFile(path.join(worldInputPath, file.name), {encoding: "utf8"});
                    preloaded_settings[file.name.replace(".chunker.json", "")] = JSON.parse(contents);
                } catch (err) {
                    // Reply with error
                    this.sendMessage({
                        requestId: requestId,
                        type: "error",
                        error: "Failed to parse " + file.name + " as preloaded data.",
                        stackTrace: err.stack.toString() + "\n"
                    });
                    return;
                }
            }
        }

        // Send the detect version request
        this.sendToProcess(request, (response) => {
            if (response.type !== "response") return response; // Forward errors

            // Add our preloaded settings
            return {
                type: "response",
                requestId: requestId,
                output: {
                    version: response.output,
                    preloaded_settings: preloaded_settings,
                    session: this._sessionID
                }
            }
        });
    }

    async generateSettings(requestId) {
        let worldInputPath = path.join(this._sessionPath, "input");
        let settingsOutputPath = path.join(this._sessionPath, "settings");

        // Ensure the world directory doesn't exist
        await fs.rm(settingsOutputPath, {recursive: true, force: true});

        // Make the directory for output
        await fs.mkdir(settingsOutputPath);

        let request = {
            type: "settings",
            requestId: requestId,
            anonymousId: this._sessionID,
            inputPath: worldInputPath,
            outputPath: settingsOutputPath
        }

        // Send the generate settings request
        this.sendToProcess(request, async (response) => {
            if (response.type !== "response") return response; // Forward errors

            let data = await fs.readFile(path.join(settingsOutputPath, "data.json"));
            Object.assign(response.output, JSON.parse(data.toString()));

            // Sort maps by ID
            response.output.maps.sort((a, b) => a.id - b.id);

            // Only provide dimension names for dimensions
            response.output.dimensions = Object.keys(response.output.dimensions);
            return response;
        });
    }

    async generatePreview(requestId) {
        let worldInputPath = path.join(this._sessionPath, "input");
        let previewOutputPath = path.join(this._sessionPath, "preview");

        // Ensure the preview directory doesn't exist
        await fs.rm(previewOutputPath, {recursive: true, force: true});

        // Make the directory for output
        await fs.mkdir(previewOutputPath);

        // Process request
        let request = {
            type: "preview",
            requestId: requestId,
            anonymousId: this._sessionID,
            inputPath: worldInputPath,
            outputPath: previewOutputPath
        }

        // Send the preview request
        this.sendToProcess(request, async (response) => {
            // Use the base64 of the map.bin for output
            response.output = (await fs.readFile(path.join(previewOutputPath, "map.bin"))).toString("base64");

            return response;
        });
    }

    async convertWorld(outputType, requestId, data) {
        let worldInputPath = path.join(this._sessionPath, "input");
        let worldOutputPath = path.join(this._sessionPath, "output");
        let copyNbt = data.hasOwnProperty("editing") && data["editing"];

        // Ensure the world directory doesn't exist
        await fs.rm(worldOutputPath, {recursive: true, force: true});

        // Make the directory for output
        await fs.mkdir(worldOutputPath);

        if (copyNbt) {
            // Copy all the files but exclude level.dat, region, entities, data/map_.dat/idcounts.dat
            await fs.copy(worldInputPath, worldOutputPath, {
                filter: (src) => {
                    let relativePath = path.relative(worldInputPath, src);
                    let parts = relativePath.split(path.sep);

                    // Don't include any block data / entities (these are passed through Chunker)
                    if (parts.includes("region") || parts.includes("entities")) {
                        return false;
                    }

                    // Don't include in-game map data
                    if (parts.includes("data") && parts.length === 2) {
                        let fileName = parts[1];
                        if (fileName === "idcounts.dat" || fileName.startsWith("map_") && fileName.endsWith(".dat")) {
                            return false;
                        }
                    }

                    // Don't include level.dat / session.lock
                    if (parts.length === 1 && (parts[0] === "level.dat" || parts[0] === "session.lock")) {
                        return false;
                    }

                    // Otherwise include the file
                    return true;
                }
            });
        }

        // Process request
        let request = {
            type: "convert",
            requestId: requestId,
            anonymousId: this._sessionID,
            inputPath: worldInputPath,
            outputPath: worldOutputPath,
            outputType: outputType,
            inputToOutputDimension: this._dimensionMappings,
            mappings: this._blockMappings,
            nbtSettings: this._worldSettings,
            pruningList: this._pruningSettings,
            copyNbt: copyNbt,
            skipMaps: data.hasOwnProperty("mapConversion") && !data["mapConversion"],
            skipLootTables: data.hasOwnProperty("lootTableConversion") && !data["lootTableConversion"],
            skipItemConversion: data.hasOwnProperty("itemConversion") && !data["itemConversion"],
            customIdentifiers: !data.hasOwnProperty("customIdentifiers") || data["customIdentifiers"],
            skipBlockConnections: data.hasOwnProperty("blockConnections") && !data["blockConnections"],
            enableCompact: !data.hasOwnProperty("enableCompact") || data["enableCompact"],
            discardEmptyChunks: data.hasOwnProperty("discardEmptyChunks") && data["discardEmptyChunks"],
            preventYBiomeBlending: data.hasOwnProperty("preventYBiomeBlending") && data["preventYBiomeBlending"]
        }

        // Send the convert version request
        this.sendToProcess(request, async (response) => {
            if (response.type !== "response") return response; // Forward errors

            // Tell the user that we're zipping the output
            this.sendMessage({
                requestId: requestId,
                type: "progress_state",
                percentage: 0.999,
                animated: true,
                name: "Zipping output",
                continue: true
            });

            // Create a zip of the output using archiver as the file size may be larger than available ram
            try {
                // Use the user provided file name
                let outputFileName = (this._finalName ?? "output").replaceAll(/[^A-Za-z0-9_\-@]/g, "_");

                // Ensure that there are not too many underscores from replacement
                outputFileName = outputFileName.replace(/_{2,}/g, '_');

                // If the filename is empty or over 128 characters use "output"
                if (outputFileName.length === 0 || outputFileName.length >= 128) {
                    outputFileName = "output";
                }
                outputFileName = outputFileName + (outputType.startsWith("BEDROCK") ? ".mcworld" : ".zip");

                // Create the zip
                let outputZipPath = path.join(this._sessionPath, outputFileName);
                await zipRecursive(worldOutputPath, outputZipPath);

                // Add the file name
                response.output.download = "session://" + this._sessionID + "/" + outputFileName;

                // Return the response
                return response;
            } catch (e) {
                return {
                    requestId: response.requestId,
                    type: "error",
                    error: "Failed to create output ZIP.",
                    stackTrace: e.stack.toString() + "\n"
                };
            }
        });
    }
}