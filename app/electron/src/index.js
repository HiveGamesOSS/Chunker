import {app, BrowserWindow, dialog, ipcMain, net, protocol, shell} from "electron"
import url from "url"
import path from "path"
import {Session} from "./session.js";
import fs from "fs";
import log from "electron-log"
import versionInfo from "./version.json" with {type: "json"};
import * as os from "node:os";

const __filename = url.fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Start logging
log.transports.file.level = "info";
log.transports.file.writeOptions
log.eventLogger.startLogging();

const createWindow = () => {
    const window = new BrowserWindow({
        title: "Chunker...",
        width: 1280,
        height: 800,
        minWidth: 560,
        webPreferences: {
            devTools: true,
            preload: path.join(__dirname, "preload.js")
        }
    });

    // Holder for sessions this window has
    let sessions = new Map();

    // Setup IPC used for sending session data
    window.webContents.ipc.on("connect", (_event, sessionID) => {
        let session;
        try {
            // Create a session
            session = new Session(sessions, sessionID, window, _event.sender);
        } catch (e) {
            log.warn("Session failed to be made ", e);

            // Mark as error
            _event.reply("message-" + sessionID, JSON.stringify({type: "close", code: -100, error: e.message}));
            return;
        }

        // Setup a handler
        _event.sender.ipc.on("message-" + sessionID, (_event, data) => {
            log.debug("Got message " + data);
            session.onEvent(JSON.parse(data))
                .catch((e) => log.error("Failed to process data for process", e));
        });
        log.debug("Marked as open session");

        // Mark as open
        _event.reply("message-" + sessionID, JSON.stringify({type: "open"}));
    });

    // Setup data protocol, used for accessing mapping data
    window.webContents.session.protocol.handle("static", request => {
        let {host, pathname} = new URL(request.url);
        const basePath = path.join(__dirname, "..", "data");

        // Only blocks is supported
        if (host !== "blocks") {
            return new Response("Not Found", {
                status: 404,
                headers: {"content-type": "text/html"}
            })
        }

        // Transform FORMAT_VERSION into format/version.json
        let separator = pathname.indexOf("_");
        pathname = pathname.substring(0, separator).toLowerCase() + "/" + pathname.substring(separator + 1) + ".json";

        // Ensure the path is safe
        const inputPath = path.join(basePath, pathname);
        const relative = path.relative(basePath, inputPath);
        const isSafe = relative && !relative.startsWith("..") && !path.isAbsolute(relative);
        if (!isSafe) {
            return new Response("Bad Request", {
                status: 400,
                headers: {"content-type": "text/html"}
            })
        }

        // Lookup the URL / path
        let fetched;
        if (!app.isPackaged && process.env.DEV_UI_URL) {
            // Fetch the file
            fetched = net.fetch(process.env.DEV_UI_URL + "/data" + pathname);
        } else {
            // Check the file exists (and it is a file)
            if (!fs.existsSync(inputPath) || fs.lstatSync(inputPath).isDirectory()) {
                return new Response("Not Found", {
                    status: 404,
                    headers: {"content-type": "text/html"}
                })
            }

            // Fetch the file
            fetched = net.fetch(url.pathToFileURL(inputPath).toString());
        }

        // Pre-process the contents
        return fetched.then(response => {
            if (response.status !== 200) return response; // Forward response
            return response.json();
        }).then(responseOrJSON => {
            if (responseOrJSON instanceof Response) {
                return responseOrJSON;
            } else {
                let blocks = [];

                // Apply pre-processing to sort it into states
                if (responseOrJSON instanceof Array) {
                    // Legacy Java format
                    for (let entry of responseOrJSON) {
                        blocks.push({
                            name: "minecraft:" + entry.name,
                            states: {
                                data: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15],
                            }
                        })
                    }
                } else {
                    // Either Java or Bedrock format
                    if (responseOrJSON.hasOwnProperty("blocks")) {
                        // Bedrock palette
                        let blockMap = {};

                        // Loop through the entries and build the block map
                        for (let entry of responseOrJSON.blocks) {
                            let block = blockMap[entry.name];

                            // Create a new entry
                            if (!block) {
                                block = {
                                    waterlogged: new Set([false, true])
                                }
                                blockMap[entry.name] = block;
                            }

                            // Merge any state values
                            if (entry.hasOwnProperty("states")) {
                                for (let state of entry.states) {
                                    let states = block[state.name];

                                    // Create a new state entry
                                    if (!states) {
                                        states = new Set();
                                        block[state.name] = states;
                                    }

                                    // Add the known states (special case for byte)
                                    states.add(state.type === "byte" ? (state.value === 1) : state.value);
                                }
                            }
                        }

                        // Finally convert the blocks into an array
                        for (let name of Object.keys(blockMap)) {
                            // Turn the states into arrays
                            let states = blockMap[name];
                            for (let name in states) {
                                states[name] = Array.from(states[name]);
                            }

                            // Add the block
                            blocks.push({
                                name: name,
                                states: states
                            });
                        }
                    } else {
                        // Java blocks report
                        for (let name of Object.keys(responseOrJSON)) {
                            blocks.push({
                                name: name,
                                states: responseOrJSON[name].properties
                            })
                        }
                    }
                }

                // Sort blocks by name
                blocks.sort((a, b) => a.name.localeCompare(b.name));

                // Return the JSON
                return new Response(JSON.stringify(blocks), {
                    status: 200,
                    headers: {"content-type": "text/json"}
                })
            }
        })
    });

    // Setup session protocol, used for accessing session data
    window.webContents.session.protocol.handle("session", request => {
        const {host, pathname} = new URL(request.url);

        // Ensure host is a valid session
        let session = sessions.get(host);
        if (!session) {
            return new Response("Unauthorized", {
                status: 401,
                headers: {"content-type": "text/html"}
            })
        }

        // Ensure the path is safe
        const inputPath = path.join(session._sessionPath, pathname);
        const relative = path.relative(session._sessionPath, inputPath);
        const isSafe = relative && !relative.startsWith("..") && !path.isAbsolute(relative);
        if (!isSafe) {
            return new Response("Bad Request", {
                status: 400,
                headers: {"content-type": "text/html"}
            })
        }

        // Check the file exists (and it is a file)
        if (!fs.existsSync(inputPath) || fs.lstatSync(inputPath).isDirectory()) {
            return new Response("Not Found", {
                status: 404,
                headers: {"content-type": "text/html"}
            })
        }

        // Fetch the file
        return net.fetch(url.pathToFileURL(inputPath).toString());
    });

    // Setup before quit behaviour to ensure sessions are cleaned up
    app.on("before-quit", async (event) => {
        if (sessions.size > 0) {
            // Ensure all the sessions have been closed
            event.preventDefault();

            // Loop through each session, close it then await the future
            await Promise.all(Array.from(sessions.values()).map((session) => {
                // Call the close method (this ensures it always gets called)
                session.close(1);

                // Return the internal promise for closing
                return session._closePromise;
            }));

            // Once done, recall quit
            app.quit();
        }
    });

    window.removeMenu(); // No menu bar

    // Depending on the environment open the main page
    if (!app.isPackaged && process.env.DEV_UI_URL) {
        // Load from dev
        window.loadURL(process.env.DEV_UI_URL).catch((reason) => {
            dialog.showErrorBox("Something went wrong", reason.toString());
        });
    } else {
        // Load the index
        let index = url.format({
            pathname: path.join(__dirname, "..", "index.html"),
            protocol: "file",
            slashes: true
        });
        window.loadURL(index).catch((reason) => {
            dialog.showErrorBox("Something went wrong", reason.toString());
        });
    }

    // Add various useful hooks

    // Allow dev tools on F12
    window.webContents.on("before-input-event", (_, input) => {
        if (input.type === "keyDown" && input.key === "F12") {
            window.webContents.toggleDevTools();
        }
    });

    // For new windows open them in the main browser
    window.webContents.setWindowOpenHandler(({url}) => {
        // Otherwise open in the users default browser
        shell.openExternal(url);
        return {action: "deny"};
    });
}

// Allow session:// and data:// to be accessed bypassing content security policies
protocol.registerSchemesAsPrivileged([
    {scheme: "session", privileges: {bypassCSP: true, supportFetchAPI: true}}, // Bypass CSP to allow it to be loaded from
    {scheme: "static", privileges: {bypassCSP: true, supportFetchAPI: true}} // Bypass CSP to allow it to be loaded from
]);

app.whenReady().then(() => {
    // Initialize logging
    log.info("Started chunker-electron");

    // Create window
    createWindow();
})

// Quit when all windows are closed.
app.on("window-all-closed", () => {
    // Keep open on Mac
    if (process.platform !== "darwin") {
        app.quit();
    }
});

// Ensure versionInfo has OS
versionInfo.platform = os.platform() + "-" + os.arch() + "-" + os.release();
ipcMain.on("versionInfo", (e) => {
    e.returnValue = versionInfo;
})