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
import {ClientTileCache} from "./clientTileCache";
import {ChunkerPreviewLayer} from "./chunkerPreviewLayer";
import {CenterCoordsControl} from "./centerCoordsControl";
import {ZoomIndicator} from "./zoomIndicator";
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
        return (
            <React.Fragment>
                {!this.app.previewProgress.isComplete() &&
                    <div className="main_content main_content_progress">
                        <ProgressComponent progress={this.app.previewProgress}/>
                    </div>
                }
                {this.app.previewProgress.isComplete() && this.app.state.previewData !== undefined &&
                    <Map
                        session={this.props.session} data={this.app.state.previewData} app={this.app}
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
        this._cache = new ClientTileCache();

        const dimensions = this.app.state.settings.dimensions;
        const defaultIdentifier = (this.app.state.previewState && this.app.state.previewState.layer) || dimensions[0];
        const minZoom = computeMinZoom(worldBoundsForAutoFit(this._mapBin, defaultIdentifier));

        this.mymap = L.map("map", {
            crs: L.CRS.Simple,
            minZoom,
            maxZoom: 5,
            attributionControl: false
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

        if (this.app.state.previewState === undefined) {
            const defaultWorld = worlds.length > 0 ? worlds[0] : undefined;
            if (defaultWorld !== undefined) {
                defaultWorld.addTo(this.mymap);
                const centerX = self.app.state.settings.settings["World Settings"].filter(a => a.name === "SpawnX")[0].value;
                const centerZ = self.app.state.settings.settings["World Settings"].filter(a => a.name === "SpawnZ")[0].value;
                this.mymap.setView(xy(centerX, centerZ), 2);
            }
        } else {
            const defaultWorld = worlds.filter(a => this.app.state.previewState.layer === a.options.identifier)[0];
            defaultWorld.addTo(this.mymap);
            this.mymap.setView(this.app.state.previewState.center, this.app.state.previewState.zoom, {animate: false});
        }

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

        new ZoomIndicator().addTo(this.mymap);

        this.mymap.on("baselayerchange", function (e) {
            self.renderPruningRegion();
        });

        this.mymap.on("fullscreenchange", () => {
            // Aggressive evict before the renderer holds two viewports' worth of tiles at once.
            self._cache.evictAllExcept([]);
        });
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
