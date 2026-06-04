import React, {PureComponent} from "react";
import {createFilter} from "react-select";
import CreatableSelect from "react-select/creatable";
import {optimizeSelect} from "../blocks/optimizedOption";

const STYLES = {
    container: (styles) => ({...styles, height: "100%"}), control: (styles) => ({...styles, height: "inherit"})
};

export class BiomeSelector extends PureComponent {
    getOptions = () => {
        return (this.props.suggestions ?? []).map(name => ({label: name, value: name}));
    };

    getValue = () => {
        const identifier = this.props.identifier;
        if (!identifier || identifier.length === 0) return null;
        return this.getOptions().find(o => o.value === identifier) ?? {label: identifier, value: identifier};
    };

    onChange = (val) => {
        this.props.onChange(val ? val.value : "");
    };

    isValidNewOption = (val) => {
        if (val.indexOf(":") === -1 || val.indexOf(":") === val.length - 1) return false; // Must be valid namespace
        if (val.startsWith("minecraft:")) return false; // Must not be minecraft namespace
        return true; // Seems fine
    };

    formatCreateLabel = (val) => {
        return `Use "${val}" as a custom biome`;
    };

    render() {
        return (<CreatableSelect
                filterOption={createFilter({ignoreAccents: false})}
                placeholder={this.props.placeholder ?? "Select a biome"}
                isClearable
                options={this.getOptions()}
                value={this.getValue()}
                onChange={this.onChange}
                isDisabled={this.props.disabled}
                noOptionsMessage={() => "No available options"}
                styles={STYLES}
                components={optimizeSelect.components}
                isValidNewOption={this.isValidNewOption}
                formatCreateLabel={this.formatCreateLabel}
            />);
    }
}
