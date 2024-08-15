import React, {PureComponent} from "react";
import chroma from "chroma-js";
import SeedRandom from "seedrandom";
import {createFilter} from "react-select";
import CreatableSelect from "react-select/creatable"
import {optimizeSelect} from "./optimizedOption";

const DEFAULT_SCALE = chroma.scale("Spectral")


export class BlockSelector extends PureComponent {

    getNiceColor = (a) => {
        let count = 0;
        let color;
        do {
            color = DEFAULT_SCALE(this.rng(a + count));
            count++;
        } while (color.get("lab.l") > 80);

        return color.hex();
    }

    rng = (a) => {
        return new SeedRandom(a).quick();
    }

    getValues = () => {
        if (this.props.identifier && this.props.identifier.length > 0) {
            return this.deserializeValue(this.props.identifier, this.props.states)
        } else {
            return [];
        }
    }

    getOptions = (values) => {
        let block = values.filter(a => a.block);
        if (block.length === 0) {
            // Show blocks if none selected
            return [{
                label: "Blocks",
                options: this.props.suggestions.map(a => this.getBlockOption(a.name))
            }];
        } else {
            // Show data options for the block
            const suggestion = this.props.suggestions.find(a => a.name === block[0].value);
            const states = suggestion ? suggestion.states || [] : [];

            // Filter options
            return Object.keys(states)
                .filter(a => !values.some(b => b.state_name === a))
                .map((a) => ({
                    label: this.formatOption(a).toUpperCase(),
                    options: states[a].map(b => this.getStateOption(a, b))
                }));
        }
    };

    deserializeValue = (identifier, states) => {
        if (states && Object.keys(states).length > 0) {
            let options = identifier.length > 0 ? [this.getBlockOption(identifier)] : [];

            // Parse each option
            Object.keys(states).forEach(a => options.push(this.getStateOption(a, states[a])));

            // Return list
            return options;
        } else {
            // Only return block
            return identifier.length > 0 ? [this.getBlockOption(identifier)] : []
        }
    };

    getBlockOption = (blockName) => {
        return {label: blockName, value: blockName, block: true};
    };

    getStateOption = (stateName, stateValue) => {
        return {
            label: this.formatOption(stateValue),
            value: stateName + "=" + stateValue,
            block: false,
            state_name: stateName,
            state_value: stateValue,
            color: this.getNiceColor(stateName)
        };
    };

    formatOption = (stateValue) => {
        // Check for bit
        if (typeof stateValue === "string" && stateValue.endsWith("_bit")) {
            stateValue = stateValue.substring(0, stateValue.length - 4);
        }

        // Otherwise parse
        if (stateValue === true) {
            return "True";
        } else if (stateValue === false) {
            return "False";
        } else if (Number.isInteger(stateValue)) {
            return stateValue;
        } else {
            // Snake to english
            let parts = stateValue.toLowerCase().split("_");
            for (let i = 0; i < parts.length; i++) {
                parts[i] = parts[i][0].toUpperCase() + parts[i].slice(1);
            }

            return parts.join(" ");
        }
    }

    serializeValues = (values) => {
        if (!values || values.length === 0 || !values[0].block) {
            return ["", []]; // Note: block check ensures we don't transfer invalid states to other blocks, can be removed in the future potentially
        } else {
            let data = values.slice(1).reduce((obj, item) => {
                obj[item.state_name] = item.state_value;
                return obj;
            }, {});
            return [values[0].value, data];
        }
    };

    getStyles = () => {
        return {
            container: (styles) => ({
                ...styles,
                height: "100%"
            }),
            control: (styles) => ({
                ...styles,
                height: "inherit"
            }),
            option: (styles) => ({
                ...styles,
                backgroundColor: "white",
            }),
            multiValue: (styles, {data}) => {
                const color = chroma(data.color ?? "gray");
                return {
                    ...styles,
                    backgroundColor: color.alpha(0.3).css(),
                };
            },
            multiValueLabel: (styles, {data}) => ({
                ...styles,
                color: data.color,
            }),
            multiValueRemove: (styles, {data}) => {
                const color = chroma(data.color ?? "gray");
                return {
                    ...styles,
                    color: data.color,
                    ":hover": {
                        backgroundColor: color.alpha(0.6).css(),
                        color: color.get("lab.l") < 70 ? "black" : "white",
                    }
                }
            }
        }
    };

    formatLabel = (option, {context}) => (
        context === "menu" || !option.state_name ? <span>{option.label}</span> :
            <span>{option.state_name}={this.stringify(option.state_value)}</span>
    );

    stringify = (option) => {
        if (option === true) return "true";
        if (option === false) return "false";
        return option;
    }

    onChange = (val) => {
        if (val != null) {
            // Apply flat mapping (ensures extra values are added)
            val = val.flatMap(x => {
                if (x.extra) {
                    return [x].concat(x.extra);
                } else {
                    return [x];
                }
            });

            // Ensure block is always first
            val.sort((a, b) => b.block - a.block);
        }

        // Serialize for saving
        let serialized = this.serializeValues(val);
        this.props.onChange(serialized[0], serialized[1]);
    }

    isValidNewOption = (val, values) => {
        let block = values.filter(a => a.block);

        if (block.length === 0) {
            // Should be identifier or identifier with states
            if (this.isImport(val)) return true; // Allow importing
            if (val.indexOf(":") === -1 || val.indexOf(":") === val.length - 1) return false; // Must be valid namespace
            if (val.startsWith("minecraft:")) return false; // Must not be minecraft namespace
            if (val.indexOf("[") !== -1 && val.indexOf("]") === -1) return false; // Ensure states are balanced
            if (val.indexOf("[") === -1 && val.indexOf("]") !== -1) return false; // Ensure states are balanced
            if (val.indexOf("[") !== -1 && val.indexOf("=") === -1) return false; // Ensure states are present

            return true;
        } else {
            // Should be a single state
            return val.match(/([A-Za-z0-9_-]+)=([A-Za-z0-9_-]+)/);
        }
    };

    isImport = (val) => {
        // Imports are fully qualified identifiers, e.g. minecraft:stone[type=granite] etc
        return val.startsWith("minecraft:") && val.indexOf("[") !== -1 && val.indexOf("]") !== -1
    }

    formatCreateLabel = (val, values) => {
        let block = values.filter(a => a.block);

        // Handle full identifiers
        if (block.length === 0) {
            if (this.isImport(val)) {
                return `Import "${val}"`;
            } else {
                return `Use "${val}" as a custom identifier`;
            }
        } else {
            // Handle single state
            if (block[0].value.startsWith("minecraft:")) {
                return `Import "${val}"`;
            } else {
                return `Use "${val}" as a custom state`;
            }
        }
    }

    getNewOptionData = (val, label, values, options) => {
        let block = values.filter(a => a.block);

        // Handle full identifiers
        if (block.length === 0) {
            if (val.indexOf("[") !== -1 && val.indexOf("]") !== -1) {
                let block = val.split("[")[0];
                let extraStates = this.getCustomStateOptions(val.split("[")[1].split("]")[0], block);
                return {...this.getBlockOption(block), label: label, extra: extraStates};
            } else {
                return {...this.getBlockOption(val), label: label};
            }
        } else {
            let option = this.getCustomStateOption(val, block[0].value);
            if (option !== null) {
                return {...option, label: label};
            } else {
                return null;
            }
        }
    };

    getCustomStateOption = (val, block) => {
        let parts = val.replace(" ", "").split("=");
        let left = parts[0];
        let right = parts[1];

        // Parse if java or the data value
        if (!this.props.java || left === "data") {
            // Automatically parse into numbers / boolean etc
            if (right === "true") {
                right = true;
            } else if (right === "false") {
                right = false;
            } else if (!isNaN(Number.parseInt(right))) {
                right = Number.parseInt(right);
            }
        }

        // If this block is in the minecraft: namespace, we should attempt to validate the property
        if (block.startsWith("minecraft:")) {
            const suggestion = this.props.suggestions.find(a => a.name === block);
            const states = suggestion ? suggestion.states || [] : [];

            // Check the value exists
            if (states[left] === undefined || !states[left].some(v => v === right)) {
                return null; // Not a valid state, don't allow it
            }
        }

        return this.getStateOption(left, right);
    };

    getCustomStateOptions = (val, block) => {
        // Handle edge case
        if (val.length === 0) return [];
        if (val.indexOf("=") === -1) return [];

        // Otherwise process
        if (val.indexOf(",") !== -1) {
            return val.split(",").map((x) => this.getCustomStateOption(x, block)).filter(x => x !== null && x !== undefined);
        } else {
            let value = this.getCustomStateOption(val, block);
            return value === null || value === undefined ? [] : [value];
        }
    };

    render() {
        let values = this.getValues();
        let options = this.getOptions(values);

        return (<CreatableSelect
            filterOption={createFilter({ignoreAccents: false})}
            placeholder={this.props.placeholder ?? "Select a block"}
            closeMenuOnSelect={options.length === 1 && values.length >= 1}
            isClearable
            isMulti
            options={options}
            onChange={this.onChange}
            isDisabled={this.props.disabled}
            value={values}
            noOptionsMessage={() => "No available options"}
            formatOptionLabel={this.formatLabel}
            styles={this.getStyles()}
            components={optimizeSelect.components}
            isValidNewOption={(val) => this.isValidNewOption(val, values)}
            formatCreateLabel={(val) => this.formatCreateLabel(val, values)}
            getNewOptionData={(val, label) => this.getNewOptionData(val, label, values, options)}
        />);
    }
}