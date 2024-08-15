import React, {Component} from "react";
import {ProgressComponent} from "../../../progress";
import {SettingsInput} from "./world_settings/settingsInput";
import mergeSettings from "./world_settings/settings";

export class WorldSettingsTab extends Component {
    app = this.props.app;

    updateSetting = (name, value) => {
        let clone = Object.assign({}, this.app.state.editedSettings);
        clone[name] = value;

        this.app.setState({editedSettings: clone});
    };

    setTab = (name, e) => {
        this.app.setState({worldSettingsTab: name});
        e.preventDefault();
    };

    render() {
        let categories = [];
        let settings = [];
        let java = this.app.state.outputType.id.startsWith("JAVA");
        if (this.app.settingsProgress.isComplete() && this.app.state.settings.settings !== undefined) {
            let allSettings = this.app.state.settings.settings;
            categories = Object.keys(allSettings);
            allSettings[this.app.state.worldSettingsTab].forEach((original) => {
                // Create clone to merge
                let item = Object.assign({}, original);
                if (original.order === undefined) {
                    item.order = Number.MAX_SAFE_INTEGER;
                }

                // Apply merging (descriptions etc)
                if (mergeSettings[this.app.state.worldSettingsTab] !== undefined && mergeSettings[this.app.state.worldSettingsTab][original.name] !== undefined) {
                    item = Object.assign(item, mergeSettings[this.app.state.worldSettingsTab][original.name]);

                    // Automatically calculate weight based on config position
                    if (original.order === undefined) {
                        item.order = Object.keys(mergeSettings[this.app.state.worldSettingsTab]).indexOf(original.name);
                    }
                }

                // Add to tab
                if ((java && item.java) || (!java && item.bedrock)) {
                    settings.push(item);
                }

                let oldValue = item.value;
                // Update value from editedSettings
                if (this.app.state.editedSettings[item.name] !== undefined) {
                    item.value = this.app.state.editedSettings[item.name];
                }

                // Special case as GeneratorType varies on input/output
                if (item.name === "GeneratorType") {
                    item.type = "Radio";

                    // If the world isn't being edited or it wasn't detected as custom
                    if (this.app.state.outputType.id !== this.app.state.inputType.id || oldValue !== "CUSTOM") {
                        if (item.value === "CUSTOM") {
                            item.value = "VOID";
                        }
                        item.options = [
                            {name: "NORMAL", color: "blue", value: "NORMAL"},
                            {name: "FLAT", color: "green", value: "FLAT"},
                            {name: "VOID", color: "red", value: "VOID"},
                        ];
                    } else {
                        item.options = [
                            {name: "NORMAL", color: "blue", value: "NORMAL"},
                            {name: "FLAT", color: "green", value: "FLAT"},
                            {name: "CUSTOM", color: "yellow", value: "CUSTOM"},
                            {name: "VOID", color: "red", value: "VOID"},
                        ];
                    }
                }
            });

            // Sort settings by order
            settings.sort((a, b) => a.order - b.order);
        }
        return (
            <React.Fragment>
                {(!this.app.settingsProgress.isComplete() &&
                    <div className="main_content">
                        <ProgressComponent progress={this.app.settingsProgress}/>
                    </div>
                )}
                {(this.app.settingsProgress.isComplete() &&
                    <React.Fragment>
                        <div className="topbar">
                            <h1>World Settings</h1>
                            <h2>Your in-game world settings. Hover over field names for a detailed description.</h2>
                            <ul className="tabs">
                                {categories.map(category => (
                                    <li key={category}>
                                        <button className={this.app.state.worldSettingsTab === category ? "active" : ""}
                                                onClick={(e) => this.setTab(category, e)}>{category}</button>
                                    </li>
                                ))}
                            </ul>
                        </div>
                        {settings.length === 0 &&
                            <div className="main_content settings">
                                <h2>There are no settings in this category</h2>
                            </div>
                        }
                        {settings.length > 0 &&
                            <div className="main_content settings">
                                {settings.map((value, key) => (
                                    <SettingsInput key={this.app.state.worldSettingsTab + ":" + key} base={value}
                                                   name={value.display || value.name} onChange={this.updateSetting}/>
                                ))}
                            </div>
                        }
                    </React.Fragment>
                )}
            </React.Fragment>
        );
    }
}