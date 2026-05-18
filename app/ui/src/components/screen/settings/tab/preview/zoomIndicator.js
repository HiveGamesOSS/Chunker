import L from "leaflet";

export const ZoomIndicator = L.Control.extend({
    options: {position: "topright"},

    initialize(options) {
        L.setOptions(this, options);
    },

    onAdd(map) {
        const container = L.DomUtil.create("div", "chunker-zoom-indicator");
        L.DomEvent.disableClickPropagation(container);
        L.DomEvent.disableScrollPropagation(container);

        const label = L.DomUtil.create("span", "chunker-zoom-indicator-label", container);
        const track = L.DomUtil.create("div", "chunker-zoom-indicator-track", container);
        const fill = L.DomUtil.create("div", "chunker-zoom-indicator-fill", track);

        this._map = map;
        this._label = label;
        this._fill = fill;

        this._update();
        map.on("zoom zoomend", this._update, this);
        return container;
    },

    onRemove(map) {
        map.off("zoom zoomend", this._update, this);
    },

    _update() {
        const z = this._map.getZoom();
        const minZ = this._map.getMinZoom();
        const maxZ = this._map.getMaxZoom();
        this._label.textContent = z >= 0 ? "+" + z : String(z);
        const span = maxZ - minZ;
        const frac = span > 0 ? (z - minZ) / span : 0;
        this._fill.style.height = (Math.max(0, Math.min(1, frac)) * 100) + "%";
    }
});
