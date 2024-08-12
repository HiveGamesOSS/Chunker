import React, {Component} from "react";
import {BlockMapping} from "./blocks/blockMapping";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimes} from "@fortawesome/free-solid-svg-icons";
import {getVersionName} from "../../mode/modeOption";


export class PaletteMappingsTab extends Component {
    app = this.props.app;

    delete = (id) => {
        let clone = JSON.parse(JSON.stringify(this.app.state.mappings));
        clone.identifiers = clone.identifiers.slice(0, id).concat(clone.identifiers.slice(id + 1, clone.identifiers.length));
        this.app.setState({mappings: clone});
    };

    update = (id, newState) => {
        let clone = JSON.parse(JSON.stringify(this.app.state.mappings));
        clone.identifiers[id] = newState;
        this.app.setState({mappings: clone});
    };

    toJSON = () => {
        return JSON.stringify({
            "value": {
                "mappings": this.app.state.mappings,
            }, "type": "mappings"
        });
    };

    fromJSON = (json) => {
        try {
            let parsed = JSON.parse(json);
            if (parsed.type !== undefined && parsed.type === "mappings") {
                if (Array.isArray(parsed.value.mappings)) {
                    this.app.setState({mappings: parsed.value.mappings});
                }
            }
        } catch (e) {
            // Ignored, file invalid
            console.info("File failed to be read", e, json);
        }
    };

    render() {
        let inputVersion = getVersionName(this.app.state.inputType.id);
        let inputJava = this.app.state.inputType.id.startsWith("JAVA_");
        let outputVersion = getVersionName(this.app.state.outputType.id);
        let outputJava = this.app.state.outputType.id.startsWith("JAVA_");
        return (
            <div>
                <div className="topbar">
                    <h1>Block Mapping</h1>
                    <h2><b>Block mappings allow you to turn one block into another.</b> Blocks which match the input
                        will be turned into the output, states which are not set will also be converted where possible.
                    </h2>
                </div>
                <div className="main_content settings dimensions">
                    {(this.app.state.inputBlockSuggestions.length === 0 || this.app.state.outputBlockSuggestions.length === 0) &&
                        <p>
                            <div
                                align="center">Block Mapping is currently not available for the current world
                                input/output format.
                            </div>
                        </p>}
                    {this.app.state.inputBlockSuggestions.length > 0 && this.app.state.outputBlockSuggestions.length > 0 &&
                        <div>
                            <div className="mappings-row">
                                <div className="mappings-entry" align="center">
                                    <span>Input Block ({inputJava ? "Java" : "Bedrock"} {inputVersion})</span>
                                </div>
                                <div className="mappings-entry" align="center">
                                    <span>Output Block ({outputJava ? "Java" : "Bedrock"} {outputVersion})</span>
                                </div>
                                <div className="mappings-delete">
                                    <button className="icon-button" style={{visibility: "hidden"}}>
                                        <FontAwesomeIcon icon={faTimes}/>
                                    </button>
                                </div>
                            </div>
                            {this.app.state.mappings.identifiers.concat([{
                                "old_identifier": "",
                                "new_identifier": "",
                                "old_state_values": [],
                                "new_state_values": []
                            }]).map((a, k) => (
                                <BlockMapping
                                    value={a}
                                    key={k}
                                    inputJava={inputJava}
                                    inputBlockSuggestions={this.app.state.inputBlockSuggestions}
                                    outputJava={outputJava}
                                    outputBlockSuggestions={this.app.state.outputBlockSuggestions}
                                    onChange={(newState) => this.update(k, newState)}
                                    canDelete={k < this.app.state.mappings.identifiers.length}
                                    onDelete={() => this.delete(k)}
                                />
                            ))}
                        </div>}
                </div>
            </div>
        );
    }
}