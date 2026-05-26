import React, {Component} from "react";
import "./previewComponent.css";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import "leaflet-draw";
import {ProgressComponent} from "../../../../progress";
import "leaflet-mouse-position/src/L.Control.MousePosition.css";
import "leaflet-fullscreen/dist/leaflet.fullscreen.css";
import {getDimensionDisplayName} from "../dimensionPruningTab";
import {worldBoundsForAutoFit} from "./mapBin";
import {computeMinZoom} from "./autoFit";
import {ChunkerPreviewLayer} from "./chunkerPreviewLayer";
import {CenterCoordsControl} from "./centerCoordsControl";
import {ZoomIndicator} from "./zoomIndicator";
import {LoadingIndicator} from "./loadingIndicator";
import api from "../../../../../api";

require("leaflet-mouse-position/src/L.Control.MousePosition");
require("leaflet-fullscreen/dist/Leaflet.fullscreen");

// Fix leaflet
delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl: require("leaflet/dist/images/marker-icon-2x.png"),
    iconUrl: require("leaflet/dist/images/marker-icon.png"),
    shadowUrl: require("leaflet/dist/images/marker-shadow.png"),
});

export class PreviewComponent extends Component {
    app = this.props.app;

    render() {
        const settingsDone = this.app.settingsProgress.isComplete();
        const genDone = this.app.previewProgress.isComplete();
        const data = this.app.state.previewData;
        const tilesDone = this.app.previewTileProgress.isComplete();
        return (
            <React.Fragment>
                {!settingsDone &&
                    <div className="main_content main_content_progress">
                        <ProgressComponent progress={this.app.settingsProgress}/>
                    </div>
                }
                {settingsDone && !genDone &&
                    <div className="main_content main_content_progress">
                        <ProgressComponent progress={this.app.previewProgress}/>
                    </div>
                }
                {/* Covers the gap between generation completing and previewData arriving from
                 * the map.bin fetch, as well as the actual tile pre-load phase. */}
                {settingsDone && genDone && !tilesDone &&
                    <div className="main_content main_content_progress">
                        <ProgressComponent progress={this.app.previewTileProgress}/>
                    </div>
                }
                {settingsDone && genDone && tilesDone && data !== undefined &&
                    <Map
                        session={this.props.session} data={data} app={this.app}
                        clientTileCache={this.app.clientTileCache}
                        pruningSettings={this.app.state.pruningSettings}/>
                }
            </React.Fragment>
        );
    }
}

export class Map extends Component {
    mymap = undefined;
    app = this.props.app;

    componentDidMount() {
        const self = this;
        // this.props.data is the already-parsed map.bin (Task 14).
        this._mapBin = this.props.data;
        // App owns the cache so tiles pre-loaded before mount survive into the layer here.
        this._cache = this.props.clientTileCache;

        const dimensions = this.app.state.settings.dimensions;
        const defaultIdentifier = (this.app.state.previewState && this.app.state.previewState.layer) || dimensions[0];
        const minZoom = computeMinZoom(worldBoundsForAutoFit(this._mapBin, defaultIdentifier));

        // Resolve the initial view BEFORE constructing the map so we can pass it to L.map().
        // Without this, the layer's addTo() runs while the map is at its default state
        // (no view set) and triggers tile loads at the wrong zoom, then setView() reshuffles
        // everything — that race left some tiles in flight at the old LOD that never get
        // their tile_ready honoured, manifesting as "2 of 4 tiles missing on first open".
        let initialCenter;
        let initialZoom;
        let defaultLayerIdentifier;
        if (this.app.state.previewState !== undefined) {
            initialCenter = this.app.state.previewState.center;
            initialZoom = this.app.state.previewState.zoom;
            defaultLayerIdentifier = this.app.state.previewState.layer;
        } else {
            defaultLayerIdentifier = dimensions[0];
            const bounds = worldBoundsForAutoFit(this._mapBin, defaultLayerIdentifier);
            if (bounds) {
                // Frame the map on the world bounds. Centring on SpawnX/SpawnZ at a fixed
                // zoom of 2 leaves worlds with content built far from spawn opening onto an
                // empty viewport — the tiles exist, just off-screen. Auto-fit puts the actual
                // content in front of the user from the start.
                const centerX = (bounds.minX + bounds.maxX + 1) * 8;
                const centerZ = (bounds.minZ + bounds.maxZ + 1) * 8;
                initialCenter = xy(centerX, centerZ);
                initialZoom = computeMinZoom(bounds);
            } else {
                const centerX = self.app.state.settings.settings["World Settings"].filter(a => a.name === "SpawnX")[0].value;
                const centerZ = self.app.state.settings.settings["World Settings"].filter(a => a.name === "SpawnZ")[0].value;
                initialCenter = xy(centerX, centerZ);
                initialZoom = 2;
            }
        }

        this.mymap = L.map("map", {
            crs: L.CRS.Simple,
            minZoom,
            maxZoom: 5,
            attributionControl: false,
            center: initialCenter,
            zoom: initialZoom
        });

        const worlds = dimensions.map((identifier, k) => new ChunkerPreviewLayer({
            id: "blocks",
            identifier,
            world: identifier.replace(":", "_"),
            session: self.props.session,
            index: k,
            continuousWorld: true,
            mapBin: this._mapBin,
            tileCache: this._cache,
            ipc: {
                requestTiles: (req) => api.send({type: "flow", method: "request_preview_tiles", ...req}, () => {}),
                cancelTiles: (req) => api.send({type: "flow", method: "cancel_preview_tiles", ...req}, () => {}),
                onTileReady: (cb) => api.addListener("tile_ready", cb),
                onTileError: (cb) => api.addListener("tile_error", cb)
            }
        }));

        const defaultWorld = worlds.find(a => a.options.identifier === defaultLayerIdentifier) || worlds[0];
        if (defaultWorld) defaultWorld.addTo(this.mymap);

        this.renderPruningRegion();

        const baseMaps = {};
        worlds.forEach(a => {
            baseMaps[getDimensionDisplayName(a.options.identifier)] = a;
        });

        L.control.mousePosition({
            emptyString: "0, 0 (0, 0)",
            formatter: this.formatCoords,
            wrapLng: false
        }).addTo(this.mymap);

        this.mymap.zoomControl.setPosition("bottomleft");
        L.control.fullscreen({position: "bottomright"}).addTo(this.mymap);
        L.control.layers(baseMaps, null, {position: "bottomright"}).addTo(this.mymap);

        new CenterCoordsControl({
            getBounds: () => worldBoundsForAutoFit(this._mapBin, this._currentWorld())
        }).addTo(this.mymap);

        // Adding the zoom indicator after the (relocated) zoom control causes it to stack
        // above the +/- buttons in the bottom-left corner.
        new ZoomIndicator().addTo(this.mymap);

        new LoadingIndicator().addTo(this.mymap);

        this.mymap.on("baselayerchange", function (e) {
            self.renderPruningRegion();
        });

        this.mymap.on("fullscreenchange", () => {
            // Aggressive evict before the renderer holds two viewports' worth of tiles at once.
            self._cache.evictAllExcept([]);
        });

        // Leaflet measures the container size at construction time and only loads tiles for
        // that viewport. If the #map element wasn't fully laid out yet (mid-React-mount, or the
        // preview tab was hidden when the layer was added) the initial viewport can be 0px
        // wide and almost no tiles get requested. Force one recomputation now, then keep
        // watching for size changes so the same fix applies when the user switches tabs and
        // comes back, resizes the window, or toggles fullscreen. `_update()` is the additive
        // version of `redraw()`: it requests any tiles that are currently missing from the
        // viewport without disturbing the ones already on screen — no visible flicker.
        const mapEl = document.getElementById("map");
        const refreshTiles = () => {
            if (!this.mymap) return;
            this.mymap.invalidateSize({pan: false});
            this.mymap.eachLayer((layer) => {
                if (layer instanceof L.GridLayer && typeof layer._update === "function") {
                    layer._update();
                }
            });
        };
        setTimeout(refreshTiles, 0);
        setTimeout(refreshTiles, 200);
        if (typeof ResizeObserver !== "undefined" && mapEl) {
            this._resizeObserver = new ResizeObserver(refreshTiles);
            this._resizeObserver.observe(mapEl);
        }
    }

    _currentWorld() {
        let id;
        this.mymap.eachLayer((layer) => {
            if (layer.options && layer.options.identifier) id = layer.options.identifier;
        });
        return id || this.app.state.settings.dimensions[0];
    }

    formatCoords = (long, lat) => {
        const x = long;
        const z = -lat;
        return Math.floor(x) + ", " + Math.floor(z) + " (" + Math.floor(x / 16) + ", " + Math.floor(z / 16) + ")";
    };

    componentWillUnmount() {
        let currentLayer = "minecraft:overworld";
        this.mymap.eachLayer(function (layer) {
            if (layer.options.identifier !== undefined) {
                currentLayer = layer.options.identifier;
            }
        });

        if (this.mymap._loaded) {
            this.app.savePreviewState({
                zoom: this.mymap.getZoom(),
                center: this.mymap.getCenter(),
                layer: currentLayer
            });
        }

        if (this._resizeObserver) {
            this._resizeObserver.disconnect();
            this._resizeObserver = null;
        }
    }

    moveRegion = (world, regionIndex, minX, minZ, maxX, maxZ) => {
        this.app.setState((prevState) => {
            const pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));
            pruningSettingsClone[world].regions[regionIndex] = {
                ...pruningSettingsClone[world].regions[regionIndex],
                minChunkX: Math.floor(Math.min(minX, maxX)),
                minChunkZ: Math.floor(Math.min(minZ, maxZ)),
                maxChunkX: Math.ceil(Math.max(minX, maxX)) - 1,
                maxChunkZ: Math.ceil(Math.max(minZ, maxZ)) - 1
            };
            return {pruningSettings: pruningSettingsClone};
        }, () => {
            this.renderPruningRegion();
        });
    }

    renderPruningRegion = () => {
        const self = this;
        this.mymap.eachLayer(function (layer) {
            if (layer.options.identifier !== undefined) {
                self.renderWorldPruningRegion(layer.options.identifier);
            }
            if (layer.options.region) {
                self.mymap.removeLayer(layer);
            }
        });
    };

    renderWorldPruningRegion = (world) => {
        if (!(this.app.state.pruningSettings[world] && this.app.state.pruningSettings[world].regions)) return;

        const pruningRegions = this.app.state.pruningSettings[world];

        pruningRegions.regions.forEach((region, index) => {
            const minX = Math.min(region.minChunkX, region.maxChunkX) * 16;
            const minZ = Math.min(region.minChunkZ, region.maxChunkZ) * 16;

            const maxX = (Math.max(region.minChunkX, region.maxChunkX) * 16) + 16;
            const maxZ = (Math.max(region.minChunkZ, region.maxChunkZ) * 16) + 16;

            const rect = L.rectangle([xy(minX, minZ), xy(minX, maxZ), xy(maxX, maxZ), xy(maxX, minZ)], {
                color: pruningRegions.include ? "green" : "red",
                region: true
            });

            if (pruningRegions.regions.length > 1 || region.name) {
                rect.bindTooltip(region.name ?? ("Region " + (index + 1)), {
                    direction: "center",
                    opacity: 0.8
                });
            }

            const self = this;
            rect.on("edit", function (e) {
                const max = rect.getBounds().getNorthEast();
                const min = rect.getBounds().getSouthWest();
                self.moveRegion(world, index, min.lng / 16, -min.lat / 16, max.lng / 16, -max.lat / 16);
            });
            rect.addTo(this.mymap);
            rect.editing.enable();
        });
    };

    render() {
        return (<div id="map"/>);
    }
}

const xy = function (x, y) {
    if (L.Util.isArray(x)) {
        return xy(x[0], x[1]);
    }
    return [-y, x];
};
