import React, {Component} from "react";
import {BiomeMapping} from "./biomes/biomeMapping";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimes} from "@fortawesome/free-solid-svg-icons";
import {getFormatName, getVersionName} from "../../mode/modeOption";

export class BiomeMappingsTab extends Component {
    app = this.props.app;
    state = {pendingInput: "", pendingOutput: ""};

    update = (originalKey, entry) => {
        const entries = Object.entries(this.app.state.biomeMapping);
        const idx = entries.findIndex(([k]) => k === originalKey);
        if (idx === -1) return;
        entries[idx] = [entry.old_identifier ?? "", entry.new_identifier ?? ""];
        this.app.setState({biomeMapping: Object.fromEntries(entries)});
    };

    delete = (key) => {
        const clone = Object.assign({}, this.app.state.biomeMapping);
        delete clone[key];
        this.app.setState({biomeMapping: clone});
    };

    updatePending = (entry) => {
        const pendingInput = entry.old_identifier ?? "";
        const pendingOutput = entry.new_identifier ?? "";
        if (pendingInput.length > 0 && pendingOutput.length > 0) {
            this.app.setState({
                biomeMapping: Object.assign({}, this.app.state.biomeMapping, {[pendingInput]: pendingOutput})
            });
            this.setState({pendingInput: "", pendingOutput: ""});
        } else {
            this.setState({pendingInput: pendingInput, pendingOutput: pendingOutput});
        }
    };

    render() {
        let inputVersion = getVersionName(this.app.state.inputType.id);
        let inputFormat = getFormatName(this.app.state.inputType.id);
        let outputVersion = getVersionName(this.app.state.outputType.id);
        let outputFormat = getFormatName(this.app.state.outputType.id);
        const entries = Object.entries(this.app.state.biomeMapping);
        return (
            <div>
                <div className="topbar">
                    <h1>Biome Mapping</h1>
                    <h2><b>Biome mappings allow you to turn one biome into another.</b> Biomes which match the input
                        will be replaced with the output during conversion.
                    </h2>
                </div>
                <div className="main_content settings dimensions">
                    {(this.app.state.inputBiomeSuggestions.length === 0 || this.app.state.outputBiomeSuggestions.length === 0) &&
                        <div align="center">Biome Mapping is currently not available for the current world
                            input/output format.
                        </div>}
                    {this.app.state.inputBiomeSuggestions.length > 0 && this.app.state.outputBiomeSuggestions.length > 0 &&
                        <div>
                            <div className="mappings-row">
                                <div className="mappings-entry" align="center">
                                    <span>Input Biome ({inputFormat} {inputVersion})</span>
                                </div>
                                <div className="mappings-entry" align="center">
                                    <span>Output Biome ({outputFormat} {outputVersion})</span>
                                </div>
                                <div className="mappings-delete">
                                    <button className="icon-button" style={{visibility: "hidden"}}>
                                        <FontAwesomeIcon icon={faTimes}/>
                                    </button>
                                </div>
                            </div>
                            {entries.map(([key, value], k) => (
                                <BiomeMapping
                                    value={{old_identifier: key, new_identifier: value}}
                                    key={k}
                                    inputBiomeSuggestions={this.app.state.inputBiomeSuggestions}
                                    outputBiomeSuggestions={this.app.state.outputBiomeSuggestions}
                                    onChange={(newState) => this.update(key, newState)}
                                    canDelete={true}
                                    onDelete={() => this.delete(key)}
                                />
                            ))}
                            <BiomeMapping
                                value={{
                                    old_identifier: this.state.pendingInput,
                                    new_identifier: this.state.pendingOutput
                                }}
                                key="pending"
                                inputBiomeSuggestions={this.app.state.inputBiomeSuggestions}
                                outputBiomeSuggestions={this.app.state.outputBiomeSuggestions}
                                onChange={(newState) => this.updatePending(newState)}
                                canDelete={false}
                            />
                        </div>}
                </div>
            </div>
        );
    }
}
