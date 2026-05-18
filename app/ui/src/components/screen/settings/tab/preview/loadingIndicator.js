import L from "leaflet";

/**
 * Tiny "Loading tiles…" badge in the corner of the map. Reflects whether any GridLayer
 * currently has outstanding tiles. We listen on the map's `layeradd` so the badge stays
 * in sync as the user switches dimensions via the layer picker.
 */
export const LoadingIndicator = L.Control.extend({
    options: {position: "topleft"},

    initialize(options) {
        L.setOptions(this, options);
        this._activeLoaders = 0;
    },

    onAdd(map) {
        const container = L.DomUtil.create("div", "chunker-loading-indicator");
        container.textContent = "Loading tiles…";
        L.DomEvent.disableClickPropagation(container);

        this._container = container;
        this._map = map;
        this._refreshVisibility();

        // Attach to any GridLayer already on the map, and to any added later.
        map.eachLayer((l) => this._attachIfGridLayer(l));
        map.on("layeradd", (e) => this._attachIfGridLayer(e.layer), this);
        map.on("layerremove", (e) => this._detachIfGridLayer(e.layer), this);

        return container;
    },

    onRemove(map) {
        map.off("layeradd", null, this);
        map.off("layerremove", null, this);
        map.eachLayer((l) => this._detachIfGridLayer(l));
    },

    _attachIfGridLayer(layer) {
        if (!(layer instanceof L.GridLayer)) return;
        if (layer._chunkerLoadingHooked) return;
        layer._chunkerLoadingHooked = true;

        const onLoading = () => {
            this._activeLoaders++;
            this._refreshVisibility();
        };
        const onLoad = () => {
            this._activeLoaders = Math.max(0, this._activeLoaders - 1);
            this._refreshVisibility();
        };
        layer._chunkerLoadingHandlers = {onLoading, onLoad};
        layer.on("loading", onLoading);
        layer.on("load", onLoad);
    },

    _detachIfGridLayer(layer) {
        if (!layer || !layer._chunkerLoadingHooked) return;
        const handlers = layer._chunkerLoadingHandlers;
        if (handlers) {
            layer.off("loading", handlers.onLoading);
            layer.off("load", handlers.onLoad);
        }
        layer._chunkerLoadingHooked = false;
        layer._chunkerLoadingHandlers = null;
        // If the removed layer was mid-load, our counter would never decrement; clamp.
        this._refreshVisibility();
    },

    _refreshVisibility() {
        if (this._activeLoaders > 0) {
            this._container.classList.add("chunker-loading-indicator-visible");
        } else {
            this._container.classList.remove("chunker-loading-indicator-visible");
        }
    }
});
