import React, {Component} from "react";
import {SettingsInput} from "./world_settings/settingsInput";
import {saveAs} from "file-saver";
import JSZip from "jszip";

export class ConverterSettingsTab extends Component {
    app = this.props.app;

    updateSetting = (name, value) => {
        let newSettings = Object.assign({}, this.app.state.converterSettings);

        // If it's the default value delete it
        if (value === this.app.defaultConverterSettings[name]) {
            delete newSettings[name];
        } else {
            newSettings[name] = value;
        }

        this.app.setState({converterSettings: newSettings});
    };

    getOptions = () => {
        let normal = [
            {
                "display": "Passthrough Custom Identifiers",
                "name": "customIdentifiers",
                "description": "Whether custom identifiers outside the minecraft namespace are allowed to be read / written. This should be disabled if you want to prevent modded blocks from being converted.",
                "type": "Boolean"
            },
            {
                "display": "Calculate Block Connections Using Neighbours",
                "name": "blockConnections",
                "description": "When reading legacy/bedrock worlds, block-connections are used to connect chests/fences/panes, this can hold neighbouring chunks from being converted but greatly increases conversion accuracy as blocks on the edge can have data fetched from surrounding chunks.",
                "type": "Boolean"
            },
            {
                "display": "Convert Items",
                "name": "itemConversion",
                "description": "Whether converting items should be enabled.",
                "type": "Boolean"
            },
            {
                "display": "Convert Structure Loot Tables",
                "name": "lootTableConversion",
                "description": "Whether the converter should attempt to match structure loot table files.",
                "type": "Boolean"
            },
            {
                "display": "In-Game Map Conversion",
                "name": "mapConversion",
                "description": "Whether converting in-game maps should be enabled.",
                "type": "Boolean"
            },
            {
                "display": "Discard Empty Chunks",
                "name": "discardEmptyChunks",
                "description": "Whether empty chunk data should not be written.",
                "type": "Boolean"
            },
            {
                "display": "Prevent Y Biome Blending",
                "name": "preventYBiomeBlending",
                "description": "Whether an empty chunk should be required at the top of each column to prevent vertical biome blending (Java only).",
                "type": "Boolean"
            }
        ];

        // If bedrock output add bedrock states
        if (this.app.state.outputType.id.startsWith("BEDROCK")) {
            normal = normal.concat({
                "display": "Enable LevelDB Compact",
                "name": "enableCompact",
                "description": "Compacts the database at the end, may cause longer conversion times.",
                "type": "Boolean"
            });
        }

        // Generate values (Falling back to defaults when not present)
        let app = this.app;
        normal.forEach(obj => {
            let value = app.state.converterSettings[obj.name];
            obj.value = value !== undefined ? value : app.defaultConverterSettings[obj.name];
        });
        return normal;
    };

    downloadEmbedded = () => {
        // Generate a ZIP
        let zip = new JSZip();
        zip.file("world_settings.chunker.json", JSON.stringify(this.app.state.editedSettings));
        zip.file("converter_settings.chunker.json", JSON.stringify(this.app.state.converterSettings));
        zip.file("block_mappings.chunker.json", this.app.getBlockMappingsJSON());
        zip.file("pruning.chunker.json", this.app.getPruningJSON());
        zip.file("dimension_mappings.chunker.json", this.app.getDimensionMappingsJSON());
        zip.file("README.txt", "Please copy the .json files in this folder to the same directory as your level.dat, Chunker will automatically preload these when you select your world.");

        zip.generateAsync({type: "blob"}).then(function (blob) {
            saveAs(blob, "ExportedChunkerSettings.zip");
        }, function (err) {
            // Just print for now
            console.error(err);
        });
    };

    render() {
        let settings = this.getOptions();
        return (
            <div>
                <React.Fragment>
                    <div className="topbar">
                        <h1>Converter Settings</h1>
                        <h2>This tab allows you to disable/enable some of the features on the world converter, doing so
                            may lead to faster conversions but lower quality.</h2>
                    </div>
                    <div className="main_content settings dimensions">
                        {settings.map(setting => (
                            <SettingsInput key={setting.name} base={setting} name={setting.display}
                                           onChange={(name, value) => this.updateSetting(name, value)}/>
                        ))}
                        <div className="white_box">
                            <label className="legend" htmlFor="name">
                                <span className="tooltip">Placing these files in the same directory as your level.dat will load them when the world is loaded with Chunker.</span>Export
                                Settings for Preloading
                            </label>
                            <div className="fields">
                                <button
                                    className="button blue small"
                                    onClick={(e) => this.downloadEmbedded()}>Save .zip
                                </button>
                            </div>
                        </div>
                        <div className="white_box">
                            <label className="legend" htmlFor="name">
                                <span className="tooltip">Allows you to get your current settings as CLI usage.</span>Export
                                Settings for CLI Usage
                            </label>
                            <div className="fields">
                                <button
                                    className="button magenta small"
                                    onClick={(e) => this.app.screen.current.switchTab("cli", e)}>Export
                                </button>
                            </div>
                        </div>
                    </div>
                </React.Fragment>
            </div>
        );
    }
}