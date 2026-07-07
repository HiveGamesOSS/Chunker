let api = {
    connection: undefined,
    replyHandlers: {},
    listeners: {},
    connect: function (connectHandler) {
        let handlers = {};

        handlers.onopen = function () {
            connectHandler();
        };

        handlers.onclose = function (e) {
            api.connection = undefined;
            if (e.code === 1000) return;
            if (e.code === 200) {
                // intentional close
            }
            connectHandler(e.code);
        };

        handlers.onmessage = function (e) {
            let msg = JSON.parse(e.data);
            let requestId = msg.requestId;
            let handler = api.replyHandlers[requestId];
            if (handler !== undefined) {
                if (msg.continue === undefined || msg.continue === false) {
                    delete api.replyHandlers[requestId];
                }
                handler(msg);
                return;
            }

            let typed = api.listeners[msg.type];
            if (typed && typed.length > 0) {
                for (let cb of typed) cb(msg);
                return;
            }

            console.warn("No reply handler or listener found: ", msg);
        };

        this.connection = window.chunker.connect(handlers);
    },
    send: function (obj, replyHandler) {
        obj.requestId = crypto.randomUUID();
        if (this.connection !== undefined) {
            this.replyHandlers[obj.requestId] = replyHandler;
            this.connection.send(JSON.stringify(obj));
        } else {
            throw Error("Not connected!");
        }
    },
    addListener: function (type, callback) {
        if (!this.listeners[type]) this.listeners[type] = [];
        this.listeners[type].push(callback);
        return () => {
            this.listeners[type] = (this.listeners[type] || []).filter(cb => cb !== callback);
        };
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
