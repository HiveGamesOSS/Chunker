import React, {PureComponent} from "react";
import {BlockSelector} from "./blockSelector";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimes} from "@fortawesome/free-solid-svg-icons";

export class BlockMapping extends PureComponent {
    render() {
        return (
            <div className="mappings-row">
                <div className="mappings-entry">
                    <BlockSelector
                        placeholder={"Select input block"}
                        identifier={this.props.value.old_identifier}
                        states={this.props.value.old_state_values}
                        java={this.props.inputJava}
                        disabled={this.props.value.old_identifier === undefined || this.props.value.old_identifier === "$custom_block"}
                        onChange={(identifier, states) => {
                            let clone = Object.assign({}, this.props.value);
                            clone.old_identifier = identifier;
                            clone.old_state_values = states;
                            this.props.onChange(clone);
                        }}
                        suggestions={this.props.inputBlockSuggestions}
                    />
                </div>
                <div className="mappings-entry">
                    <BlockSelector
                        placeholder={this.props.value.new_identifier === undefined ? "Matches input block" : "Select output block"}
                        identifier={this.props.value.new_identifier}
                        states={this.props.value.new_state_values}
                        java={this.props.outputJava}
                        disabled={this.props.value.new_identifier === undefined || this.props.value.new_identifier === "$custom_block"}
                        onChange={(identifier, states) => {
                            let clone = Object.assign({}, this.props.value);
                            clone.new_identifier = identifier;
                            clone.new_state_values = states;
                            this.props.onChange(clone);
                        }}
                        suggestions={this.props.outputBlockSuggestions}
                    />
                </div>
                <div className="mappings-delete">
                    <button
                        className="icon-button"
                        title="Delete entry"
                        disabled={!this.props.canDelete}
                        onClick={this.props.onDelete}
                    >
                        <FontAwesomeIcon icon={faTimes}/>
                    </button>
                </div>
            </div>
        )
    }
}