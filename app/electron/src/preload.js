const {contextBridge, ipcRenderer, webUtils} = require("electron");
const versionInfo = ipcRenderer.sendSync("versionInfo");

contextBridge.exposeInMainWorld("chunker", {
    getPathForFile: webUtils.getPathForFile,
    version: versionInfo.version,
    gitVersion: versionInfo.git,
    platform: versionInfo.platform,
    connect: (handlers) => {
        let sessionID = crypto.randomUUID();

        // Connection Obj is used as an API for the connection, similar to WebSocket
        let connectionObj = {
            send: function (data) {
                ipcRenderer.send("message-" + sessionID, JSON.stringify({type: "message", data: data}));
            },
            close: function (code, reason) {
                ipcRenderer.send("message-" + sessionID, JSON.stringify({type: "close", code: code, reason: reason}));
            }
        };

        // Register a handler for messages
        ipcRenderer.on("message-" + sessionID, (_event, data) => {
            let event = JSON.parse(data);
            switch (event.type) {
                case "close":
                    handlers.onclose({code: event.code});

                    // Remove listeners
                    ipcRenderer.removeAllListeners("message-" + sessionID);
                    break;
                case "open":
                    handlers.onopen();
                    break;
                case "message":
                    console.info("msg", event.data);
                    handlers.onmessage({data: event.data});
                    break;
            }
        });

        // Send the connect request
        ipcRenderer.send("connect", sessionID);
        return connectionObj;
    }
});