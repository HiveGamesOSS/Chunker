import React, {Component} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPencilAlt, faTrash} from "@fortawesome/free-solid-svg-icons";
import {MappingPart} from "./mappingPart";
import {StatesTable} from "./statesTable";
import CreatableSelect from "react-select/creatable/dist/react-select.esm";

export class MappingRow extends Component {
    state = {
        editing: false
    };

    updateField = (fieldName, value) => {
        let newValue = Object.assign({}, this.props.mapping);
        newValue[fieldName] = value;

        this.props.update(newValue);
    };

    edit = () => this.setState({editing: true});

    render() {
        return (
            <tr>
                <td>
                    <MappingPart useStates={this.props.oldUseStates} identifier={this.props.mapping.old_identifier}
                                 values={this.props.mapping.old_state_values}/>
                </td>
                <td>
                    <MappingPart useStates={this.props.newUseStates} identifier={this.props.mapping.new_identifier}
                                 values={this.props.mapping.new_state_values}/>
                </td>
                <td align="center">
                    <span>
                        <button onClick={this.edit} variant="link" title="Edit" className="no-padding">
                            <FontAwesomeIcon icon={faPencilAlt}/>
                        </button>
                        <button variant="link" onClick={this.props.delete} title="Delete" className="no-padding">
                            <FontAwesomeIcon icon={faTrash}/>
                        </button>
                    </span>
                </td>
                <div size="xl" show={this.state.editing} onHide={() => this.setState({editing: false})}>
                    <div closeButton>
                        <h2>
                            Mapping Entry Editor
                        </h2>
                    </div>
                    <div>
                        <h5>Old Entry</h5>
                        <div>
                            <label column="true" sm="3">Old Identifier: </label>
                            <div sm="9">
                                <CreatableSelect value={{
                                    "label": this.props.mapping.old_identifier,
                                    "value": this.props.mapping.old_identifier
                                }} onChange={(a) => this.updateField("old_identifier", a.value)} isSearchable={true}
                                                 options={this.props.oldSuggestions.map(entry => ({
                                                     label: entry.name,
                                                     value: entry.name
                                                 }))}/>
                            </div>
                        </div>
                        <StatesTable
                            suggestions={this.props.oldSuggestions.filter(a => a.name === this.props.mapping.old_identifier)[0] || []}
                            useStates={this.props.oldUseStates} values={this.props.mapping.old_state_values}
                            updateStates={(v) => this.updateField("old_state_values", v)}/>

                        <br/>
                        <h5>New Entry</h5>
                        <div>
                            <label column="true" sm="3">New Identifier: </label>
                            <div sm="9">
                                <CreatableSelect value={{
                                    "label": this.props.mapping.new_identifier,
                                    "value": this.props.mapping.new_identifier
                                }} onChange={(a) => this.updateField("new_identifier", a.value)} isSearchable={true}
                                                 options={this.props.newSuggestions.map(entry => ({
                                                     label: entry.name,
                                                     value: entry.name
                                                 }))}/>
                            </div>
                        </div>
                        <StatesTable
                            suggestions={this.props.newSuggestions.filter(a => a.name === this.props.mapping.new_identifier)[0] || []}
                            useStates={this.props.newUseStates} values={this.props.mapping.new_state_values}
                            updateStates={(v) => this.updateField("new_state_values", v)}/>
                    </div>

                    <div>
                        <button onClick={() => this.setState({editing: false})}>Close</button>
                    </div>
                </div>
            </tr>
        );
    }
}