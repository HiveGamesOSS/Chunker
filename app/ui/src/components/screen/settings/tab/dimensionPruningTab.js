import React, {Component} from "react";
import {ProgressComponent} from "../../../progress";
import {SettingsInput} from "./world_settings/settingsInput";

export function getDimensionDisplayName(identifier) {
    switch (identifier) {
        case "minecraft:overworld":
            return "Overworld";
        case "minecraft:the_nether":
            return "The Nether";
        case "minecraft:the_end":
            return "The End";
        default:
            return identifier;
    }
}

export const VANILLA_DIMENSIONS = ["minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"];

export function isVanillaDimension(identifier) {
    return VANILLA_DIMENSIONS.includes(identifier);
}

// Identifier pattern which excludes vanilla dimensions
const IDENTIFIER_PATTERN = "(?!(?:" + VANILLA_DIMENSIONS.join("|") + ")$)[\\w\\-]+:[\\w\\-]+";
const VALID_IDENTIFIER = new RegExp("^(?:" + IDENTIFIER_PATTERN + ")$");

export function isValidDimensionIdentifier(identifier) {
    return VALID_IDENTIFIER.test(identifier);
}

const DIMENSION_DEFAULTS = {identifier: "", biomeHeight: 24, fallbackBiome: "minecraft:plains"};
const DEFAULT_IDENTIFIER_PREFIX = "my:world_";

const CUSTOM_DIMENSION_FIELDS = [
    {
        "display": "Identifier", "name": "identifier", "type": "String", "identifierOnly": true,
        "description": "Namespaced identifier in the format namespace:id (can't be a vanilla dimension).",
        "pattern": IDENTIFIER_PATTERN,
        "coerce": (value) => value.trim().toLowerCase()
    },
    {
        "display": "Biome Height", "name": "biomeHeight", "type": "Int32", "min": 1, "max": 256,
        "description": "Number of biome sections (1-256)",
        "coerce": (value) => parseInt(value)
    },
    {
        "display": "Fallback Biome", "name": "fallbackBiome", "type": "Biome",
        "description": "The biome used as a fallback for padding / when a biome can't be converted.",
        "placeholder": "Select a fallback biome",
        "allowCustom": false,
        "coerce": (value) => value || DIMENSION_DEFAULTS.fallbackBiome
    }
];

function getDimensionColor(identifier) {
    switch (identifier) {
        case "minecraft:overworld":
            return "green";
        case "minecraft:the_nether":
            return "red";
        case "minecraft:the_end":
            return "yellow";
        default:
            return "magenta";
    }
}

export class DimensionPruningTab extends Component {
    app = this.props.app;

    validateSetting = (tab, setting) => {
        if (setting.type !== "Int32") return; // Don't validate any other settings

        // Ensure we don't have NaN set as a value
        if (this.app.state.pruningSettings[tab]) {
            if (isNaN(this.app.state.pruningSettings[tab].regions[setting.region][setting.name])) {
                // Reset NaN -> 0
                this.app.setState((prevState) => {
                    let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));
                    pruningSettingsClone[tab].regions[setting.region][setting.name] = 0;

                    return {pruningSettings: pruningSettingsClone};
                }, () => this.validateSetting(tab, setting)); // Validate when done to ensure no reordering is needed
            } else {
                // Ensure that min & max is the right way around
                let minName = setting.name.replace("max", "min");
                let maxName = setting.name.replace("min", "max");

                // Get current values
                let min = this.app.state.pruningSettings[tab].regions[setting.region][minName];
                let max = this.app.state.pruningSettings[tab].regions[setting.region][maxName];

                // If min > max, then we should swap them
                if (min !== max && min > max) {
                    this.app.setState((prevState) => {
                        let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));

                        // Set the new min
                        pruningSettingsClone[tab].regions[setting.region][minName] = max;

                        // Set the new max
                        pruningSettingsClone[tab].regions[setting.region][maxName] = min;

                        return {pruningSettings: pruningSettingsClone};
                    });
                }
            }
        }
    }

    updateSetting = (tab, name, value, setting) => {
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
        } else if (name === "Pruning") {
            this.app.setState((prevState) => {
                let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));

                if (value !== "OFF") {
                    pruningSettingsClone[tab] = {
                        regions: [{
                            minChunkX: -10,
                            minChunkZ: -10,
                            maxChunkX: 10,
                            maxChunkZ: 10
                        }],
                        ...pruningSettingsClone[tab],
                        include: value === "INCLUDE"
                    };
                } else {
                    pruningSettingsClone[tab] = null;
                }

                return {pruningSettings: pruningSettingsClone};
            });
        } else if (name === "addRegion") {
            this.app.setState((prevState) => {
                let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));
                pruningSettingsClone[tab].regions = pruningSettingsClone[tab].regions.concat([{
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
                pruningSettingsClone[tab].regions.splice(setting.region, 1);

                return {pruningSettings: pruningSettingsClone};
            });
        } else {
            this.app.setState((prevState) => {
                let pruningSettingsClone = JSON.parse(JSON.stringify(prevState.pruningSettings));

                // Update settings
                if (name === "name") {
                    // Check if name is equal to the default
                    if (value !== ("Region " + (setting.region + 1))) {
                        pruningSettingsClone[tab].regions[setting.region][name] = value;
                    } else {
                        // Delete the name
                        delete pruningSettingsClone[tab].regions[setting.region][name];
                    }
                } else {
                    // Parse as int
                    pruningSettingsClone[tab].regions[setting.region][name] = parseInt(value);
                }


                // Return the new state
                return {pruningSettings: pruningSettingsClone};
            });
        }
    };

    setTab = (name, e) => {
        e.preventDefault();
        this.app.setState({dimensionSettingsTab: name});
    };

    isPreExistingDimension = (identifier) => !isVanillaDimension(identifier)
        && (this.app.state.settings?.dimensions ?? []).includes(identifier);

    addCustomDimension = (e) => {
        e.preventDefault();
        this.app.setState((prevState) => {
            let dimensions = prevState.customDimensions?.dimensions ?? [];

            // Find a default name to use
            let taken = new Set((this.app.state.settings?.dimensions ?? [])
                .concat(dimensions.map(dimension => dimension.identifier)));
            let suffix = 1;
            while (taken.has(DEFAULT_IDENTIFIER_PREFIX + suffix)) suffix++;

            // Return the new entry
            return {
                customDimensions: {
                    dimensions: dimensions.concat([Object.assign({}, DIMENSION_DEFAULTS, {
                        identifier: DEFAULT_IDENTIFIER_PREFIX + suffix
                    })])
                },
                dimensionSettingsTab: "#" + dimensions.length
            };
        });
    };

    deleteCustomDimension = (index) => {
        this.app.setState((prevState) => {
            let dimensions = prevState.customDimensions?.dimensions ?? [];

            return {
                customDimensions: {dimensions: dimensions.filter((dim, i) => i !== index)},
                dimensionSettingsTab: undefined
            };
        });
    };

    updateCustomDimension = (index, target, field, value) => {
        this.app.setState((prevState) => {
            // Create the entry
            let dimensions = (prevState.customDimensions?.dimensions ?? []).slice();
            let previous = dimensions[index] ?? target;
            let entry = Object.assign({}, previous, {[field.name]: field.coerce ? field.coerce(value) : value});

            // -1 indicates a new entry
            dimensions[index === -1 ? dimensions.length : index] = entry;

            let state = {customDimensions: {dimensions: dimensions}};

            // Ensure we rename previous identifiers
            if (previous.identifier && previous.identifier !== entry.identifier) {
                state.dimensionMapping = Object.fromEntries(Object.entries(prevState.dimensionMapping)
                    .map(([input, output]) => [input, output === previous.identifier ? entry.identifier : output]));
            }
            return state;
        });
    };

    validateCustomDimension = (index, entry, field) => {
        if (index === -1 || field.type !== "Int32") return; // No need to validate non numbers

        // Ensure the field is within max and min
        this.updateCustomDimension(index, entry, field, Math.min(field.max, Math.max(field.min, entry[field.name] || field.min)));
    };

    toCustomDimensionOptions = (entry) => {
        return CUSTOM_DIMENSION_FIELDS.filter(field => !field.identifierOnly || !this.isPreExistingDimension(entry.identifier))
            .map(field => Object.assign({}, field, {
                "value": entry[field.name],
                "suggestions": field.type === "Biome" ? this.app.state.outputBiomeSuggestions : undefined
            }));
    }

    toDimensionOption = (input) => {
        let identifiers = [...this.app.getKnownDimensions()];
        return {
            "name": "Dimension",
            "description": "The dimension to change " + getDimensionDisplayName(input) + " to.",
            "type": "Radio",
            "value": this.app.getDimensionMappings()[input] ?? "NONE",
            "options": [
                {
                    "name": "None",
                    "color": "blue",
                    "value": "NONE"
                },
                ...identifiers.map(dim => ({
                    "name": getDimensionDisplayName(dim), "color": getDimensionColor(dim),
                    "raw": !isVanillaDimension(dim), "value": dim
                }))
            ]
        };
    };

    getOptions = (dimension) => {
        let enabled = !!(this.app.state.pruningSettings[dimension]
            && this.app.state.pruningSettings[dimension].regions
            && this.app.state.pruningSettings[dimension].regions.length > 0);
        let pruningSetting = enabled ? (this.app.state.pruningSettings[dimension].include ? "INCLUDE" : "EXCLUDE") : "OFF";
        let options = [
            {
                "name": "Pruning",
                "display": "Chunk Pruning",
                "description": "Whether chunk pruning should include or exclude regions, off indicates no pruning.",
                "type": "Radio",
                "value": pruningSetting,
                "options": [
                    {
                        "name": "Off",
                        "color": "blue",
                        "value": "OFF"
                    },
                    {
                        "name": "Include",
                        "color": "green",
                        "value": "INCLUDE"
                    },
                    {
                        "name": "Exclude",
                        "color": "red",
                        "value": "EXCLUDE"
                    }
                ]
            }
        ];

        if (this.app.state.pruningSettings[dimension] && this.app.state.pruningSettings[dimension].regions) {
            this.app.state.pruningSettings[dimension].regions.forEach((region, index) => {
                options = options.concat([{
                    "display": ("Region " + (index + 1)),
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
                        "display": "Region Name",
                        "name": "name",
                        "description": "The internal name used for this region, useful for if you're exporting your Chunker settings.",
                        "type": "String",
                        "value": region.name ?? ("Region " + (index + 1)),
                        "region": index
                    },
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
        let customDimensions = this.app.state.customDimensions?.dimensions ?? [];
        let tab = this.app.state.dimensionSettingsTab;

        // The tab is either an index of the custom dimension or the identifier in the world
        let isCustomTab = tab != null && tab.startsWith("#");

        // Check whether the dimension exists
        if (isCustomTab) {
            let declared = customDimensions[parseInt(tab.substring(1))];
            if (declared != null && this.isPreExistingDimension(declared.identifier)) {
                tab = declared.identifier;
                isCustomTab = false;
            }
        }

        if (tab === undefined && this.app.settingsProgress.isComplete()) {
            tab = this.app.state.settings.dimensions[0];
        }

        let customIndex = isCustomTab ? parseInt(tab.substring(1)) : customDimensions.findIndex(dim => dim.identifier === tab);

        let customEntry;
        if (isCustomTab) {
            customEntry = customDimensions[customIndex];
        } else if (tab != null && !isVanillaDimension(tab)) {
            customEntry = customDimensions[customIndex] ?? Object.assign({}, DIMENSION_DEFAULTS, {identifier: tab});
        }

        // Dimensions which are new can't have pruning settings (since there is no existing data)
        let mappingKey = isCustomTab ? null : tab;
        let pruningSettings = mappingKey ? this.getOptions(mappingKey) : [];

        let tabs = (this.app.state.settings?.dimensions ?? []).map(identifier => ({
            id: identifier, label: getDimensionDisplayName(identifier), raw: !isVanillaDimension(identifier)
        })).concat(customDimensions.flatMap((dim, index) => this.isPreExistingDimension(dim.identifier) ? [] : [{
            id: "#" + index, label: dim.identifier || "Custom #" + (index + 1), raw: !!dim.identifier
        }]));
        return (
            <div>
                {(this.app.settingsProgress.isComplete() &&
                    <React.Fragment>
                        <div className="topbar">
                            <h1>Dimensions/Pruning</h1>
                            <h2>You can change one dimension to another, you can also enter co-ordinates of chunks you
                                want to include in the conversion.</h2>
                            {(customDimensions.length > 0 || tabs.some(({raw}) => raw)) &&
                                <h2><strong>Note: </strong> Chunker does not provide datapacks to load custom
                                    dimensions, please ensure they are present before loading the world.</h2>}
                            <ul className="tabs dimension_tabs">
                                {tabs.map(({id, label, raw}) => (
                                    <li key={id}>
                                        <button
                                            className={[tab === id && "active", raw && "identifier"].filter(Boolean).join(" ")}
                                            title={raw ? label : undefined}
                                            onClick={(e) => this.setTab(id, e)}>{label}</button>
                                    </li>
                                ))}
                                <li>
                                    <button title="Add a custom dimension"
                                            onClick={this.addCustomDimension}>+
                                    </button>
                                </li>
                            </ul>
                        </div>
                        <div className="main_content settings dimensions" id={tab}>
                            {customEntry != null && this.toCustomDimensionOptions(customEntry).map(setting => (
                                <SettingsInput key={setting.name} base={setting} name={setting.display}
                                               onChange={(name, value) => this.updateCustomDimension(customIndex, customEntry, setting, value)}
                                               onBlur={() => this.validateCustomDimension(customIndex, customEntry, setting)}/>
                            ))}
                            {mappingKey &&
                                <SettingsInput base={this.toDimensionOption(mappingKey)}
                                               name={"Output Dimension"}
                                               onChange={(name, value) => this.updateSetting(mappingKey, name, value)}/>}
                            {pruningSettings.map(setting => (
                                <SettingsInput key={setting.name + ":" + setting.region} base={setting}
                                               name={setting.display}
                                               onChange={(name, value) => this.updateSetting(mappingKey, name, value, setting)}
                                               onBlur={() => this.validateSetting(mappingKey, setting)}/>
                            ))}
                            {customEntry != null && !this.isPreExistingDimension(customEntry.identifier) &&
                                <SettingsInput base={{
                                    "display": "Delete Custom Dimension",
                                    "borderless": true,
                                    "name": "delete",
                                    "description": "Remove this custom dimension",
                                    "value": "Delete",
                                    "type": "Button"
                                }} name={"Delete Custom Dimension"}
                                               onChange={() => this.deleteCustomDimension(customIndex)}/>}
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
