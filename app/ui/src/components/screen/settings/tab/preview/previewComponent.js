import React, {Component} from "react";
import "./previewComponent.css"
import "leaflet/dist/leaflet.css"
import L from "leaflet";
import "leaflet-draw";
import {ProgressComponent} from "../../../../progress";
import "leaflet-mouse-position/src/L.Control.MousePosition.css";
import "leaflet-fullscreen/dist/leaflet.fullscreen.css";
import {DIMENSIONS} from "../dimensionPruningTab";

require("leaflet-mouse-position/src/L.Control.MousePosition"); // As it adds new controls, need to be required
require("leaflet-fullscreen/dist/Leaflet.fullscreen"); // As it adds new controls, need to be required

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
        let self = this;
        // Render map
        this.mymap = L.map("map", {
            crs: L.CRS.Simple,
            minZoom: -5,
            maxZoom: 5,
            attributionControl: false
        });

        let worlds = this.app.state.settings.dimensions.map((a, k) => {
            return L.tileLayer("session://{session}/preview/{world}.{x}.{y}.png", {
                maxNativeZoom: 0,
                minNativeZoom: 0,
                minZoom: -5,
                maxZoom: 5,
                world: a,
                id: "blocks",
                session: self.props.session,
                tileSize: 512,
                noWrap: true,
                index: k,
                continuousWorld: true
            });
        });

        if (this.app.state.previewState === undefined) {
            // Default
            let defaultWorld = worlds.length > 0 ? worlds[0] : undefined;
            if (defaultWorld !== undefined) {
                defaultWorld.addTo(this.mymap);
                let centerX = self.app.state.settings.settings["World Settings"].filter(a => a.name === "SpawnX")[0].value;
                let centerZ = self.app.state.settings.settings["World Settings"].filter(a => a.name === "SpawnZ")[0].value;

                // L.marker(xy(centerX * 16, centerZ * 16)).addTo(mymap).bindPopup("Center (" + (centerX * 16) + "," + (centerZ * 16) + ")");
                this.mymap.setView(xy(centerX, centerZ), 2);
            }
        } else {
            let defaultWorld = worlds.filter(a => this.app.state.previewState.layer === a.options.world)[0];
            defaultWorld.addTo(this.mymap);
            this.mymap.setView(this.app.state.previewState.center, this.app.state.previewState.zoom, {animate: false});
        }

        this.renderPruningRegion();

        // Add controls
        let baseMaps = {};
        worlds.forEach(a => {
            let name = a.options.world;
            let niceName;
            switch (name) {
                case "NETHER":
                    niceName = "Nether";
                    break;
                case "OVERWORLD":
                    niceName = "Overworld";
                    break;
                case "THE_END":
                    niceName = "The End";
                    break;
                default:
                    niceName = "?";
                    break;
            }

            baseMaps[niceName] = a;
        });

        // Mouse position
        L.control.mousePosition({
            emptyString: "0, 0 (0, 0)",
            formatter: this.formatCoords,
            wrapLng: false
        }).addTo(this.mymap);

        // Zoom + Layers
        this.mymap.zoomControl.setPosition("bottomleft");
        L.control.fullscreen({position: "bottomright"}).addTo(this.mymap);
        L.control.layers(baseMaps, null, {position: "bottomright"}).addTo(this.mymap);

        // Add handler for pruning render
        this.mymap.on("baselayerchange", function (e) {
            self.renderPruningRegion();
        });
    }

    formatCoords = (long, lat) => {
        let x = long;
        let z = -lat;
        return Math.floor(x) + ", " + Math.floor(z) + " (" + Math.floor(x / 16) + ", " + Math.floor(z / 16) + ")"
    };

    componentWillUnmount() {
        let currentLayer = "OVERWORLD";
        this.mymap.eachLayer(function (layer) {
            if (layer.options.world !== undefined) {
                currentLayer = layer.options.world;
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
        let dimensionIndex = DIMENSIONS.indexOf(world);
        this.app.setState((prevState) => {
            let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));

            // Add new state
            pruningSettingsClone[dimensionIndex].regions[regionIndex] = {
                minChunkX: Math.floor(Math.min(minX, maxX)),
                minChunkZ: Math.floor(Math.min(minZ, maxZ)),
                maxChunkX: Math.ceil(Math.max(minX, maxX)) - 1,
                maxChunkZ: Math.ceil(Math.max(minZ, maxZ)) - 1
            };

            return {pruningSettings: pruningSettingsClone};
        }, () => {
            // Force re-render
            this.renderPruningRegion();
        });
    }
    renderPruningRegion = () => {
        let self = this;

        // Get selected map
        this.mymap.eachLayer(function (layer) {
            if (layer.options.world !== undefined) {
                self.renderWorldPruningRegion(layer.options.world);
            }
            if (layer.options.region) {
                // Remove old polygons
                self.mymap.removeLayer(layer);
            }
        });
    };

    renderWorldPruningRegion = (world) => {
        let dimensionIndex = DIMENSIONS.indexOf(world);
        if (!(this.app.state.pruningSettings[dimensionIndex] && this.app.state.pruningSettings[dimensionIndex].regions)) return; // No regions

        let pruningRegions = this.app.state.pruningSettings[dimensionIndex];

        pruningRegions.regions.forEach((region, index) => {
            // Drawing the region
            let minX = Math.min(region.minChunkX, region.maxChunkX) * 16;
            let minZ = Math.min(region.minChunkZ, region.maxChunkZ) * 16;

            let maxX = (Math.max(region.minChunkX, region.maxChunkX) * 16) + 16;
            let maxZ = (Math.max(region.minChunkZ, region.maxChunkZ) * 16) + 16;

            // Draw rectangle
            let rect = L.rectangle([xy(minX, minZ), xy(minX, maxZ), xy(maxX, maxZ), xy(maxX, minZ)], {
                color: pruningRegions.include ? "green" : "red",
                region: true
            });

            // Add a label for the region if there are multiple
            if (pruningRegions.regions.length > 1) {
                rect.bindTooltip("Region " + (index + 1), {
                    direction: "center",
                    opacity: 0.8
                });
            }

            // Add event for edit
            let self = this;
            rect.on("edit", function (e) {
                let max = rect.getBounds().getNorthEast();
                let min = rect.getBounds().getSouthWest();

                // Reverse lat long into co-ords
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

let xy = function (x, y) {
    if (L.Util.isArray(x)) {    // When doing xy([x, y]);
        return xy(x[0], x[1]);
    }

    // Modified specifically for this leaflet scale of 512
    return [-y, x];  // When doing xy(x, y);
};