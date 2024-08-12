let api = {
    connection: undefined,
    replyHandlers: {},
    connect: function (connectHandler) {
        let handlers = {};

        // Connection open handler
        handlers.onopen = function () {
            connectHandler();
        };

        // Connection close handler
        handlers.onclose = function (e) {
            api.connection = undefined;
            if (e.code === 1000) return; // Don't show error, as it was completed cleanly (and successful!)
            if (e.code === 200) {
                // Connection closed intentionally (eg. inactive)
            } else {
                // Connection closed for other reason, should reconnect?
            }
            connectHandler(e.code);
        };

        // Connection message handler
        handlers.onmessage = function (e) {
            let msg = JSON.parse(e.data);
            let requestId = msg.requestId;
            if (api.replyHandlers[requestId] === undefined) {
                console.warn("No reply handler found: ", msg);
            } else {
                let handler = api.replyHandlers[requestId];

                // Check if handler needs removing
                if (msg.continue === undefined || msg.continue === false) {
                    // Remove as it's done
                    delete api.replyHandlers[requestId];
                }

                // Call handler
                handler(msg);
            }
        };

        // Set connection
        this.connection = window.chunker.connect(handlers);
    },
    send: function (obj, replyHandler) {
        obj.requestId = crypto.randomUUID();// Generate random id
        if (this.connection !== undefined) {
            this.replyHandlers[obj.requestId] = replyHandler;
            this.connection.send(JSON.stringify(obj));
        } else {
            throw Error("Not connected!");
        }
    },
    isConnected: function () {
        return this.connection !== undefined;
    },
    close: function () {
        if (this.isConnected()) {
            this.connection.close();
        }
    }
};

export default api;