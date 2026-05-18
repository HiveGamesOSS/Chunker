import L from "leaflet";

export const ZoomIndicator = L.Control.extend({
    options: {position: "bottomleft"},

    initialize(options) {
        L.setOptions(this, options);
    },

    onAdd(map) {
        const container = L.DomUtil.create("div", "chunker-zoom-indicator");
        L.DomEvent.disableClickPropagation(container);
        L.DomEvent.disableScrollPropagation(container);

        const track = L.DomUtil.create("div", "chunker-zoom-indicator-track", container);
        const fill = L.DomUtil.create("div", "chunker-zoom-indicator-fill", track);
        const handle = L.DomUtil.create("div", "chunker-zoom-indicator-handle", track);

        this._map = map;
        this._track = track;
        this._fill = fill;
        this._handle = handle;

        this._update();
        map.on("zoom zoomend", this._update, this);

        // Click on the track jumps to the zoom level at that position.
        L.DomEvent.on(track, "mousedown", this._onPointerDown, this);

        return container;
    },

    onRemove(map) {
        map.off("zoom zoomend", this._update, this);
        L.DomEvent.off(this._track, "mousedown", this._onPointerDown, this);
    },

    _update() {
        const z = this._map.getZoom();
        const minZ = this._map.getMinZoom();
        const maxZ = this._map.getMaxZoom();
        const span = maxZ - minZ;
        const frac = span > 0 ? (z - minZ) / span : 0;
        const pct = Math.max(0, Math.min(1, frac)) * 100;
        this._fill.style.height = pct + "%";
        // Handle visually sits at the same fraction.
        this._handle.style.bottom = pct + "%";
    },

    _zoomFromClientY(clientY) {
        const rect = this._track.getBoundingClientRect();
        const fromBottom = rect.bottom - clientY;
        const frac = Math.max(0, Math.min(1, fromBottom / rect.height));
        const minZ = this._map.getMinZoom();
        const maxZ = this._map.getMaxZoom();
        return Math.round(minZ + frac * (maxZ - minZ));
    },

    _onPointerDown(e) {
        L.DomEvent.preventDefault(e);
        L.DomEvent.stopPropagation(e);
        const moveHandler = (ev) => this._onPointerMove(ev);
        const upHandler = () => {
            document.removeEventListener("mousemove", moveHandler);
            document.removeEventListener("mouseup", upHandler);
        };
        document.addEventListener("mousemove", moveHandler);
        document.addEventListener("mouseup", upHandler);
        this._applyClientY(e.clientY);
    },

    _onPointerMove(e) {
        this._applyClientY(e.clientY);
    },

    _applyClientY(clientY) {
        const target = this._zoomFromClientY(clientY);
        if (target !== this._map.getZoom()) {
            this._map.setZoom(target, {animate: false});
        }
    }
});
