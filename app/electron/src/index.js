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

// List of arguments which are forwarded to the backend java process by default
const JVM_ARGUMENTS = [
    "-Xmx",
    "-Xms",
];

// This flag indicates arguments should be forwarded to the JVM
const ADDITIONAL_JAVA_OPTIONS = "--java-options";

// Start logging
log.transports.file.level = "info";
log.transports.file.writeOptions
log.eventLogger.startLogging();

// Apply fix for issue with GTK version on linux - https://github.com/electron/electron/issues/46538
if (process.platform === "linux" && !app.commandLine.hasSwitch("gtk-version")) {
    app.commandLine.appendSwitch("gtk-version", "3");
}

/**
 * Get the options to use for the Chunker JVM backend.
 * @returns a string which should be used as the value for JAVA_OPTIONS.
 */
const getJavaOptions = () => {
    // Get args, we need to remove the process (depending on if it's launched directly or not)
    let rawArgs = process.argv.slice(process.defaultApp ? 2 : 1);

    // Find JVM arguments that are forwarded by default
    const jvmArgs = rawArgs.filter(arg =>
        JVM_ARGUMENTS.some(jvmArg => arg.startsWith(jvmArg))
    );

    // Loop through the arguments looking for any arguments which have --java-options before it
    // or in the format --java-options="-Xmx5G -Xms2G"
    rawArgs.forEach((arg, index) => {
        if (arg.startsWith(ADDITIONAL_JAVA_OPTIONS)) {
            // If it has an equals we need to consume the arguments after
            let equalsIndex = arg.indexOf("=");
            if (equalsIndex !== -1) {
                let value = arg.substring(equalsIndex + 1);

                // Remove quotes if present
                if ((value.startsWith('"') && value.endsWith('"')) ||
                    (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.slice(1, -1);
                }

                // Add the value (can be multiple options)
                jvmArgs.push(value);
            } else if (arg === ADDITIONAL_JAVA_OPTIONS && index + 1 < rawArgs.length) {
                // Add the next argument
                jvmArgs.push(rawArgs[index + 1]);
            }
        }
    });

    // Join the arguments to make the JAVA_OPTIONS
    return jvmArgs.join(' ');
}

const createWindow = () => {
    // Get launch parameters (these are forwarded to the backend process)
    const javaOptions = getJavaOptions();

    // Start the window
    const window = new BrowserWindow({
        title: "Chunker...",
        width: 1280,
        height: 800,
        minWidth: 560,
        webPreferences: {
            devTools: true,
            partition: "window-" + crypto.randomUUID(),
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
            session = new Session(sessions, sessionID, window, _event.sender, javaOptions);
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
                // Return the JSON
                return new Response(JSON.stringify(responseOrJSON), {
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

    // Handle creating more windows
    app.on("activate", function () {
        createWindow();
    });
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