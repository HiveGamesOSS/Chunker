import L from "leaflet";
import {isTileEmpty} from "./mapBin";
import {MIN_ZOOM_FLOOR} from "./autoFit";

const PADDING_FACTOR = 1.5;
const DISPATCH_DEBOUNCE_MS = 50;
const TRANSPARENT_PIXEL = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";

export const ChunkerPreviewLayer = L.GridLayer.extend({
    initialize(options) {
        L.setOptions(this, {
            tileSize: 512,
            noWrap: true,
            // L.GridLayer's defaults hide the layer at zoom < 0. Explicitly extend the visible
            // zoom range to match the map's autoFit floor; otherwise createTile is never called
            // for negative zoom levels and the user sees an empty viewport on dezoom.
            minZoom: MIN_ZOOM_FLOOR,
            maxZoom: 5,
            // Native tiles only exist at LOD 0 down to the auto-fit floor. For zoom > 0 Leaflet
            // stretches LOD 0 tiles client-side; for zoom < MIN_ZOOM_FLOOR it stretches the floor tiles.
            // Without these, Leaflet would call createTile for any zoom and isTileEmpty would
            // be invoked with positive lod, where `1 << -lod` wraps to a huge value and hangs.
            maxNativeZoom: 0,
            minNativeZoom: MIN_ZOOM_FLOOR,
            // Keep more off-viewport tiles in DOM so a fast pan or zoom-out doesn't reveal blank
            // areas while the new tiles are still being generated.
            keepBuffer: 4,
            ...options
        });
        this._mapBin = options.mapBin;
        this._sessionId = options.session;
        this._ipc = options.ipc;
        this._cache = options.tileCache;
        this._pending = new Map();
        this._zoomTransitionActive = false;
        this._dispatchTimer = null;
        this._unsubReady = this._ipc.onTileReady((e) => this._handleTileReady(e));
        this._unsubError = this._ipc.onTileError((e) => this._handleTileError(e));
    },

    onAdd(map) {
        L.GridLayer.prototype.onAdd.call(this, map);
        map.on("zoomstart", this._onZoomStart, this);
        map.on("zoomend", this._onZoomEnd, this);
        return this;
    },

    onRemove(map) {
        if (this._unsubReady) this._unsubReady();
        if (this._unsubError) this._unsubError();
        map.off("zoomstart", this._onZoomStart, this);
        map.off("zoomend", this._onZoomEnd, this);
        if (this._dispatchTimer) {
            clearTimeout(this._dispatchTimer);
            this._dispatchTimer = null;
        }
        this._ipc.cancelTiles({world: this.options.identifier});
        L.GridLayer.prototype.onRemove.call(this, map);
        return this;
    },

    createTile(coords, done) {
        const tx = coords.x;
        const tz = coords.y;
        const lod = coords.z;
        const cacheKey = this._cache.keyFor(this.options.identifier, lod, tx, tz);

        if (isTileEmpty(this._mapBin, this.options.identifier, lod, tx, tz)) {
            const div = document.createElement("div");
            div.className = "chunker-tile chunker-tile-empty";
            setTimeout(() => done(null), 0);
            return div;
        }

        const cached = this._cache.get(cacheKey);
        if (cached && cached.blobUrl) {
            const img = new Image();
            img.src = cached.blobUrl;
            img.className = "chunker-tile";
            setTimeout(() => done(null), 0);
            return img;
        }

        const placeholder = new Image();
        placeholder.src = TRANSPARENT_PIXEL;
        placeholder.className = "chunker-tile chunker-tile-pending";
        this._pending.set(cacheKey, {tile: placeholder, done, coords: {tx, tz, lod}});
        this._scheduleDispatch();
        return placeholder;
    },

    _scheduleDispatch() {
        if (this._dispatchTimer) return;
        this._dispatchTimer = setTimeout(() => {
            this._dispatchTimer = null;
            this._dispatch();
        }, DISPATCH_DEBOUNCE_MS);
    },

    _dispatch() {
        if (this._pending.size === 0) return;
        const grouped = new Map();
        for (const [, p] of this._pending) {
            const groupKey = `${p.coords.lod}`;
            const g = grouped.get(groupKey) || {
                lod: p.coords.lod,
                minTx: Infinity, minTz: Infinity,
                maxTx: -Infinity, maxTz: -Infinity
            };
            g.minTx = Math.min(g.minTx, p.coords.tx);
            g.maxTx = Math.max(g.maxTx, p.coords.tx);
            g.minTz = Math.min(g.minTz, p.coords.tz);
            g.maxTz = Math.max(g.maxTz, p.coords.tz);
            grouped.set(groupKey, g);
        }
        const factor = this._zoomTransitionActive ? 0 : PADDING_FACTOR;
        for (const g of grouped.values()) {
            const width = g.maxTx - g.minTx + 1;
            const height = g.maxTz - g.minTz + 1;
            const padX = Math.floor((width * factor) / 2);
            const padZ = Math.floor((height * factor) / 2);
            this._ipc.requestTiles({
                world: this.options.identifier,
                lod: g.lod,
                minTx: g.minTx - padX,
                minTz: g.minTz - padZ,
                maxTx: g.maxTx + padX,
                maxTz: g.maxTz + padZ
            });
        }
    },

    _handleTileReady(event) {
        if (event.world !== this.options.identifier) return;
        const cacheKey = this._cache.keyFor(event.world, event.lod, event.tx, event.tz);
        const pending = this._pending.get(cacheKey);
        const resolvedUrl = event.path.startsWith("session://")
            ? event.path
            : `session://${this._sessionId}/preview/${event.path}`;
        const layer = this;

        const img = new Image();
        img.className = "chunker-tile";
        img.src = resolvedUrl;
        img.onload = () => {
            layer._cache.put(cacheKey, {img, blobUrl: resolvedUrl, sizeBytes: 512 * 512 * 4});
            if (pending) {
                pending.tile.src = resolvedUrl;
                pending.tile.className = "chunker-tile";
                layer._pending.delete(cacheKey);
                pending.done(null);
                return;
            }
            // No pending entry for this tile. Either createTile hasn't run yet, OR Leaflet pruned
            // our placeholder during initial layout but still tracks the tile in its registry.
            // Look it up in `_tiles` (Leaflet uses `x:y:z` as the key) and refresh the live DOM
            // element so the image becomes visible without waiting for a zoom/pan to retrigger.
            const leafletKey = event.tx + ":" + event.tz + ":" + event.lod;
            const tile = layer._tiles && layer._tiles[leafletKey];
            if (tile && tile.el) {
                if (tile.el.tagName === "IMG") {
                    tile.el.src = resolvedUrl;
                    tile.el.className = "chunker-tile";
                }
                if (!tile.loaded && typeof layer._tileReady === "function") {
                    layer._tileReady(tile.coords, null, tile.el);
                }
            }
        };
        img.onerror = () => {
            if (pending) {
                pending.tile.className = "chunker-tile chunker-tile-error";
                layer._pending.delete(cacheKey);
                pending.done(null);
            }
        };
    },

    _handleTileError(event) {
        if (event.world !== this.options.identifier) return;
        const cacheKey = this._cache.keyFor(event.world, event.lod, event.tx, event.tz);
        const pending = this._pending.get(cacheKey);
        if (pending) {
            pending.tile.className = "chunker-tile chunker-tile-error";
            this._pending.delete(cacheKey);
            pending.done(null);
        }
    },

    _onZoomStart() {
        this._zoomTransitionActive = true;
    },

    _onZoomEnd() {
        this._zoomTransitionActive = false;
    }
});
