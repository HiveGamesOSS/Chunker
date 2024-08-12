import React, {Component} from "react";

export class MappingPart extends Component {
    render() {
        return (
            <div>
                <h6>{this.props.identifier}</h6>
                <ul>
                    {this.props.values.map((value, key) => (
                        <li key={key}>{value.name}={value.value}</li>
                    ))}
                </ul>
            </div>
        );
    }
}