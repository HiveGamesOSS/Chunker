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
        map.on("moveend", this._onMoveEnd, this);
        // Initial warm: fires once the layer has its tile range settled. setTimeout(0) defers
        // past L.GridLayer.onAdd's internal _resetView so this._tileZoom and the px bounds
        // are valid when we read them.
        setTimeout(() => this._warmAdjacentLods(), 0);
        return this;
    },

    onRemove(map) {
        if (this._unsubReady) this._unsubReady();
        if (this._unsubError) this._unsubError();
        map.off("zoomstart", this._onZoomStart, this);
        map.off("zoomend", this._onZoomEnd, this);
        map.off("moveend", this._onMoveEnd, this);
        if (this._dispatchTimer) {
            clearTimeout(this._dispatchTimer);
            this._dispatchTimer = null;
        }
        this._ipc.cancelTiles({world: this.options.identifier});
        L.GridLayer.prototype.onRemove.call(this, map);
        return this;
    },

    // Keep placeholders alive while their tile_ready hasn't arrived yet — but only at the
    // CURRENT zoom level. Leaflet's default _pruneTiles evicts any tile whose viewport
    // rectangle is no longer current, which would orphan a pending placeholder and turn it
    // into a permanent hole in the map. Protecting current-zoom pending tiles lets the
    // tile_ready handler swap the real image in-place without flicker.
    //
    // For old-zoom pending tiles (left over from before the user zoomed), we delete the
    // pending entry and let Leaflet remove them normally. Otherwise the upcoming tile_ready
    // would update a placeholder that's positioned at the wrong scale, briefly flashing the
    // image in the wrong place before the next prune cycle disposes of it.
    _removeTile(key) {
        const tile = this._tiles && this._tiles[key];
        if (tile && tile.coords) {
            const cacheKey = this._cache.keyFor(
                this.options.identifier, tile.coords.z, tile.coords.x, tile.coords.y
            );
            if (this._pending.has(cacheKey)) {
                if (tile.coords.z === this._tileZoom) {
                    return;
                }
                this._pending.delete(cacheKey);
            }
        }
        L.GridLayer.prototype._removeTile.call(this, key);
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

        if (pending) {
            // Update the placeholder directly. Browser fetches the PNG asynchronously and
            // fires onload on the <img> in the DOM; that's when we mark the tile loaded for
            // Leaflet. Previously we routed through a separate internal Image() and only
            // updated the placeholder once THAT had loaded — which created a race where the
            // internal image could fail or fire out of order, leaving the live tile stuck
            // on its transparent placeholder src.
            const tileEl = pending.tile;
            const done = pending.done;
            layer._pending.delete(cacheKey);
            tileEl.onload = () => {
                tileEl.className = "chunker-tile";
                layer._cache.put(cacheKey, {img: tileEl, blobUrl: resolvedUrl, sizeBytes: 512 * 512 * 4});
                done(null);
            };
            tileEl.onerror = () => {
                tileEl.className = "chunker-tile chunker-tile-error";
                done(null);
            };
            tileEl.src = resolvedUrl;
            return;
        }

        // No pending entry: cache for future createTile lookups, and if Leaflet still tracks
        // this tile (the placeholder was created but pruned mid-load, leaving a stale entry in
        // its `_tiles` registry), refresh that DOM element so the image becomes visible
        // without waiting for a zoom/pan to retrigger createTile.
        const cached = new Image();
        cached.className = "chunker-tile";
        cached.src = resolvedUrl;
        cached.onload = () => {
            layer._cache.put(cacheKey, {img: cached, blobUrl: resolvedUrl, sizeBytes: 512 * 512 * 4});
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
    },

    _onMoveEnd() {
        // moveend fires after both pan and zoom completion (Leaflet emits move events even
        // during a zoom), so one handler covers both cases for the adjacent-LOD warm.
        this._warmAdjacentLods();
    },

    // Background-renders the lod ± 1 tiles for the current viewport so the next user zoom
    // step paints from a warm cache instead of placeholders. Same IPC plumbing as the
    // foreground dispatch — tile_ready lands in _handleTileReady's else branch, which
    // populates the shared clientTileCache without disturbing any visible tile.
    _warmAdjacentLods() {
        if (!this._map || this._zoomTransitionActive) return;
        const currentLod = this._tileZoom;
        if (currentLod === undefined || currentLod === null) return;
        const pxBounds = this._getTiledPixelBounds(this._map.getCenter());
        if (!pxBounds) return;
        const range = this._pxBoundsToTileRange(pxBounds);
        if (!range) return;
        const sourceMinTx = range.min.x;
        const sourceMaxTx = range.max.x;
        const sourceMinTz = range.min.y;
        const sourceMaxTz = range.max.y;
        this._dispatchAdjacentLod(currentLod, sourceMinTx, sourceMaxTx, sourceMinTz, sourceMaxTz, currentLod + 1);
        this._dispatchAdjacentLod(currentLod, sourceMinTx, sourceMaxTx, sourceMinTz, sourceMaxTz, currentLod - 1);
    },

    _dispatchAdjacentLod(sourceLod, sourceMinTx, sourceMaxTx, sourceMinTz, sourceMaxTz, targetLod) {
        if (targetLod > this.options.maxNativeZoom) return;
        if (targetLod < this.options.minNativeZoom) return;
        // Tile coords scale with LOD: each step toward 0 (finer) doubles the tile count per
        // side over the same world area; each step away from 0 (coarser) halves it.
        let minTx, maxTx, minTz, maxTz;
        if (targetLod > sourceLod) {
            const f = 1 << (targetLod - sourceLod);
            minTx = sourceMinTx * f;
            maxTx = (sourceMaxTx + 1) * f - 1;
            minTz = sourceMinTz * f;
            maxTz = (sourceMaxTz + 1) * f - 1;
        } else {
            const f = 1 << (sourceLod - targetLod);
            minTx = Math.floor(sourceMinTx / f);
            maxTx = Math.floor(sourceMaxTx / f);
            minTz = Math.floor(sourceMinTz / f);
            maxTz = Math.floor(sourceMaxTz / f);
        }
        this._ipc.requestTiles({
            world: this.options.identifier,
            lod: targetLod,
            minTx, minTz, maxTx, maxTz
        });
    }
});
