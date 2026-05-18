import L from "leaflet";

export const CenterCoordsControl = L.Control.extend({
    options: {position: "topleft"},

    initialize(options) {
        L.setOptions(this, options);
        this._getBounds = options.getBounds; // optional () => {minX, minZ, maxX, maxZ}
    },

    onAdd(map) {
        const container = L.DomUtil.create("div", "leaflet-bar chunker-coords-control");
        L.DomEvent.disableClickPropagation(container);
        L.DomEvent.disableScrollPropagation(container);

        const labelX = L.DomUtil.create("span", "chunker-coords-label", container);
        labelX.textContent = "X:";
        this._inputX = L.DomUtil.create("input", "chunker-coords-input", container);
        this._inputX.type = "number";

        const labelZ = L.DomUtil.create("span", "chunker-coords-label", container);
        labelZ.textContent = "Z:";
        this._inputZ = L.DomUtil.create("input", "chunker-coords-input", container);
        this._inputZ.type = "number";

        this._map = map;
        // `move` fires continuously during a drag, so the inputs update in real time
        // rather than only when the user releases the mouse.
        map.on("move", this._syncFromMap, this);

        const onCommit = () => this._commit();
        this._inputX.addEventListener("keydown", (e) => { if (e.key === "Enter") onCommit(); });
        this._inputZ.addEventListener("keydown", (e) => { if (e.key === "Enter") onCommit(); });
        this._inputX.addEventListener("blur", onCommit);
        this._inputZ.addEventListener("blur", onCommit);

        this._syncFromMap();
        return container;
    },

    onRemove(map) {
        map.off("move", this._syncFromMap, this);
    },

    _syncFromMap() {
        if (document.activeElement === this._inputX || document.activeElement === this._inputZ) return;
        const c = this._map.getCenter();
        const x = Math.round(c.lng);
        const z = Math.round(-c.lat);
        this._inputX.value = x;
        this._inputZ.value = z;
    },

    _commit() {
        const x = parseInt(this._inputX.value, 10);
        const z = parseInt(this._inputZ.value, 10);
        if (Number.isNaN(x) || Number.isNaN(z)) {
            this._syncFromMap();
            return;
        }
        const bounds = this._getBounds ? this._getBounds() : null;
        let clampedX = x;
        let clampedZ = z;
        if (bounds) {
            clampedX = Math.max(bounds.minX * 16, Math.min((bounds.maxX + 1) * 16 - 1, x));
            clampedZ = Math.max(bounds.minZ * 16, Math.min((bounds.maxZ + 1) * 16 - 1, z));
        }
        this._map.setView([-clampedZ, clampedX], this._map.getZoom(), {animate: false});
    }
});
