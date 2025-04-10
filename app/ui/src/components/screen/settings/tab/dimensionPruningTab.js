import React, {Component} from "react";
import {ProgressComponent} from "../../../progress";
import {SettingsInput} from "./world_settings/settingsInput";

export const DIMENSIONS = ["OVERWORLD", "NETHER", "THE_END"];

export class DimensionPruningTab extends Component {
    app = this.props.app;

    onChange = (input, output) => {
        this.app.setState((prevState) => {
            let dimensionMapping2 = Object.assign({}, prevState.dimensionMapping);

            if (output === "NONE") {
                delete dimensionMapping2[input];
            } else {
                dimensionMapping2[input] = output;
            }

            return {dimensionMapping: dimensionMapping2};
        });
    };

    validateSetting = (tab, setting) => {
        let tabIndex = DIMENSIONS.indexOf(tab);

        if (setting.type !== "Int32") return; // Don't validate any other settings

        // Ensure we don't have NaN set as a value
        if (this.app.state.pruningSettings[tabIndex]) {
            if (isNaN(this.app.state.pruningSettings[tabIndex].regions[setting.region][setting.name])) {
                // Reset NaN -> 0
                this.app.setState((prevState) => {
                    let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));
                    pruningSettingsClone[tabIndex].regions[setting.region][setting.name] = 0;

                    return {pruningSettings: pruningSettingsClone};
                }, () => this.validateSetting(tab, setting)); // Validate when done to ensure no reordering is needed
            } else {
                // Ensure that min & max is the right way around
                let minName = setting.name.replace("max", "min");
                let maxName = setting.name.replace("min", "max");

                // Get current values
                let min = this.app.state.pruningSettings[tabIndex].regions[setting.region][minName];
                let max = this.app.state.pruningSettings[tabIndex].regions[setting.region][maxName];

                // If min > max, then we should swap them
                if (min !== max && min > max) {
                    this.app.setState((prevState) => {
                        let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));

                        // Set the new min
                        pruningSettingsClone[tabIndex].regions[setting.region][minName] = max;

                        // Set the new max
                        pruningSettingsClone[tabIndex].regions[setting.region][maxName] = min;

                        return {pruningSettings: pruningSettingsClone};
                    });
                }
            }
        }
    }

    updateSetting = (tab, name, value, setting) => {
        let tabIndex = DIMENSIONS.indexOf(tab);
        if (name === "Dimension") {
            this.app.setState((prevState) => {
                let dimensionMappingClone = Object.assign({}, prevState.dimensionMapping);

                if (value === "NONE") {
                    delete dimensionMappingClone[tab];
                } else {
                    dimensionMappingClone[tab] = value;
                }

                return {dimensionMapping: dimensionMappingClone};
            });
        } else if (name === "Enabled") {
            this.app.setState((prevState) => {
                let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));

                if (value) {
                    pruningSettingsClone[tabIndex] = {
                        include: true,
                        regions: [{
                            minChunkX: -10,
                            minChunkZ: -10,
                            maxChunkX: 10,
                            maxChunkZ: 10
                        }]
                    };
                } else {
                    pruningSettingsClone[tabIndex] = null;
                }

                return {pruningSettings: pruningSettingsClone};
            });
        } else if (name === "addRegion") {
            this.app.setState((prevState) => {
                let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));
                pruningSettingsClone[tabIndex].regions = pruningSettingsClone[tabIndex].regions.concat([{
                    minChunkX: -10,
                    minChunkZ: -10,
                    maxChunkX: 10,
                    maxChunkZ: 10
                }]);

                return {pruningSettings: pruningSettingsClone};
            });
        } else if (name === "removeRegion") {
            this.app.setState((prevState) => {
                let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));
                pruningSettingsClone[tabIndex].regions.splice(setting.region, 1);

                return {pruningSettings: pruningSettingsClone};
            });
        } else {
            this.app.setState((prevState) => {
                let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));

                // Update settings
                pruningSettingsClone[tabIndex].regions[setting.region][name] = parseInt(value);

                // Return the new state
                return {pruningSettings: pruningSettingsClone};
            });
        }
    };

    setTab = (name, e) => {
        e.preventDefault();
        this.app.setState({dimensionSettingsTab: name});
    };

    toDimensionOption = (input, output) => {
        return {
            "name": "Dimension",
            "description": "The dimension to change " + input + " to.",
            "type": "Radio",
            "value": output || "NONE",
            "options": [
                {
                    "name": "None",
                    "color": "blue",
                    "value": "NONE"
                },
                {
                    "name": "Overworld",
                    "color": "green",
                    "value": "OVERWORLD"
                },
                {
                    "name": "Nether",
                    "color": "red",
                    "value": "NETHER"
                },
                {
                    "name": "The End",
                    "color": "yellow",
                    "value": "THE_END"
                }
            ]
        };
    };

    getOptions = (dimension, dimensionIndex) => {
        let enabled = !!(this.app.state.pruningSettings[dimensionIndex]
            && this.app.state.pruningSettings[dimensionIndex].regions
            && this.app.state.pruningSettings[dimensionIndex].regions.length > 0);
        let options = [
            {
                "display": "Prune chunks outside of a region",
                "name": "Enabled",
                "description": "This will make it so other chunks outside a certain region are discarded.",
                "type": "Boolean",
                "value": enabled
            }
        ];

        if (this.app.state.pruningSettings[dimensionIndex] && this.app.state.pruningSettings[dimensionIndex].regions) {
            this.app.state.pruningSettings[dimensionIndex].regions.forEach((region, index) => {
                options = options.concat([{
                    "display": "Region " + (index + 1),
                    "name": "removeRegion",
                    "description": "Remove this region",
                    "header": true,
                    "type": "Button",
                    "value": "X",
                    "region": index
                }]);
                // Add settings for dimension pruning
                options = options.concat([
                    {
                        "display": "Start Chunk X",
                        "name": "minChunkX",
                        "description": "The X co-ordinate of the chunk, you can get this by dividing X by 16",
                        "type": "Int32",
                        "value": region.minChunkX,
                        "region": index
                    },
                    {
                        "display": "Start Chunk Z",
                        "name": "minChunkZ",
                        "description": "The Z co-ordinate of the chunk, you can get this by dividing Z by 16",
                        "type": "Int32",
                        "value": region.minChunkZ,
                        "region": index
                    },
                    {
                        "display": "End Chunk X",
                        "name": "maxChunkX",
                        "description": "The X co-ordinate of the chunk, you can get this by dividing X by 16",
                        "type": "Int32",
                        "value": region.maxChunkX,
                        "region": index
                    },
                    {
                        "display": "End Chunk Z",
                        "name": "maxChunkZ",
                        "description": "The Z co-ordinate of the chunk, you can get this by dividing Z by 16",
                        "type": "Int32",
                        "value": region.maxChunkZ,
                        "region": index
                    }
                ]);
            });

            // Only show add region if it's enabled
            if (enabled) {
                options = options.concat([
                    {
                        "display": "Add Region",
                        "borderless": true,
                        "name": "addRegion",
                        "description": "Add another pruning region",
                        "value": "Add Region",
                        "type": "Button"
                    }
                ]);
            }
        }
        return options;
    };

    render() {
        let tab = this.app.state.dimensionSettingsTab;
        if (this.app.state.dimensionSettingsTab === undefined && this.app.settingsProgress.isComplete()) {
            tab = this.app.state.settings.dimensions[0];
        }

        let pruningSettings = this.getOptions(tab, DIMENSIONS.indexOf(tab));
        return (
            <div>
                {(this.app.settingsProgress.isComplete() &&
                    <React.Fragment>
                        <div className="topbar">
                            <h1>Dimensions/Pruning</h1>
                            <h2>You can change one dimension to another, you can also enter co-ordinates of chunks you
                                want to include in the conversion.</h2>
                            <ul className="tabs">
                                {this.app.state.settings.dimensions.map(name => (
                                    <li key={name}>
                                        <button className={tab === name ? "active" : ""}
                                                onClick={(e) => this.setTab(name, e)}>{name.replace("_", " ")}</button>
                                    </li>
                                ))}
                            </ul>
                        </div>
                        <div className="main_content settings dimensions" id={tab}>
                            <SettingsInput base={this.toDimensionOption(tab, this.app.state.dimensionMapping[tab])}
                                           name={"Output Dimension"}
                                           onChange={(name, value) => this.updateSetting(tab, name, value)}/>
                            {pruningSettings.map(setting => (
                                <SettingsInput key={setting.name + ":" + setting.region} base={setting}
                                               name={setting.display}
                                               onChange={(name, value) => this.updateSetting(tab, name, value, setting)}
                                               onBlur={() => this.validateSetting(tab, setting)}/>
                            ))}
                        </div>
                    </React.Fragment>
                )}
                {(!this.app.settingsProgress.isComplete() &&
                    <div className="center-table">
                        <div className="center-cell">
                            <div>
                                <ProgressComponent progress={this.app.settingsProgress}/>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        );
    }
}