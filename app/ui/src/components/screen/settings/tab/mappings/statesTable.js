import React, {Component} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import {StateRow} from "./stateRow";

export class StatesTable extends Component {
    deleteState = (id) => {
        this.props.updateStates(this.props.values.slice(0, id).concat(this.props.values.slice(id + 1, this.props.values.length)));
    };

    addState = (value) => {
        this.props.updateStates(this.props.values.concat(value));
    };

    updateState = (id, newValue) => {
        let states = this.props.values.slice(0);
        states[id] = newValue;
        this.props.updateStates(states);
    };

    onChange = (id, newValue) => {
        if (id === undefined && newValue === undefined) {
            if (this.props.useStates) {
                this.addState({
                    name: "",
                    value: ""
                });
            } else {
                this.addState({
                    name: "data",
                    value: 0
                });
            }
        } else if (newValue === undefined) {
            this.deleteState(id);
        } else {
            this.updateState(id, newValue);
        }
    };

    render() {
        return (
            <table className="no-top-line-table">
                <colgroup>
                    <col width="40%"/>
                    <col width="40%"/>
                    <col width="20%"/>
                </colgroup>
                <thead>
                <tr align="center">
                    {this.props.useStates && (<th>State Name</th>)}
                    {!this.props.useStates && (<th>Name</th>)}
                    {this.props.useStates && (<th>State Value</th>)}
                    {!this.props.useStates && (<th>Value</th>)}
                    <th>
                        <button disabled={!this.props.useStates && this.props.values.length > 0} className="no-padding"
                                variant="link" title="Add modify" onClick={() => {
                            this.onChange(undefined, undefined);
                            return false;
                        }}><FontAwesomeIcon icon={faPlus}/></button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {this.props.values.map((value, index) => (
                    <StateRow suggestions={this.props.suggestions} useStates={this.props.useStates}
                              values={this.props.values} key={index} myKey={index} value={value}
                              onChange={this.onChange}/>
                ))}
                </tbody>
            </table>
        );
    }
}