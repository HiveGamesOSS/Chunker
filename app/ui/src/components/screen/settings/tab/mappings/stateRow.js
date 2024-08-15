import React, {Component} from "react";
import "font-awesome/css/font-awesome.min.css";
import {faTrash} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import CreatableSelect from "react-select/creatable";

let numbers = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15].map(a => ({label: a, value: a}));

export class StateRow extends Component {
    delete = () => {
        this.props.onChange(this.props.myKey, undefined);
    };

    onChangeFieldName = (value) => {
        let clone = Object.assign({}, this.props.name);
        clone.name = value;
        this.props.onChange(this.props.myKey, clone);
    };

    onChangeFieldValue = (value) => {
        if (!this.props.useStates) {
            value = parseInt(value);
        }
        let clone = Object.assign({}, this.props.value);
        clone.value = value;
        this.props.onChange(this.props.myKey, clone);
    };

    filterNames = (name) => {
        return this.props.value.name === name || !this.props.values.some(a => a.name === name);
    };

    render() {
        return (
            <tr className={this.props.className}>
                <td>
                    <CreatableSelect value={{
                        "label": this.props.value.name,
                        "value": this.props.value.name
                    }} onChange={(a) => this.onChangeFieldName(a.value)} isDisabled={!this.props.useStates}
                                     isSearchable={true}
                                     options={this.props.suggestions && this.props.suggestions.states ? Object.keys(this.props.suggestions.states).filter(this.filterNames).map(k => ({
                                         label: k,
                                         value: k
                                     })) : []}/>
                </td>
                <td>
                    <CreatableSelect value={{
                        "label": this.props.value.value,
                        "value": this.props.value.value
                    }} onChange={(a) => this.onChangeFieldValue(a.value)} isSearchable={true}
                                     options={this.props.suggestions && this.props.suggestions.states && this.props.value.name ? this.props.suggestions.states[this.props.value.name].map(k => ({
                                         label: k,
                                         value: k
                                     })) : (this.props.useStates ? [] : numbers)}/>
                </td>
                <td align="center">
                    <span>
                        <button variant="link" title="Delete" onClick={this.delete}>
                            <FontAwesomeIcon icon={faTrash}/>
                        </button>
                    </span>
                </td>
            </tr>
        );
    }
}