import React, {Component} from "react";

export function getVersionName(inputVersionName) {
    // Filter the version name and extract the actual version for display
    let version = inputVersionName.replace("JAVA_", "").replace("BEDROCK_", "");
    version = version.replace(/^R(.*)$/g, "1.$1").replace(/_/g, ".");

    // Ensure it's in the form 1.0.0
    let size = version.split(".").length;
    while (size < 3) {
        size++;
        version += ".0";
    }
    return version;
}

export class ModeOption extends Component {
    render() {
        let version = this.props.value.version ? this.props.value.version : getVersionName(this.props.type);
        let java = this.props.type.startsWith("JAVA_");
        let beta = (!java && version === "1.21.40") || (java && version === "1.21.1"); // Beta label
        return (
            <div>
                <input type="radio" value={this.props.type} checked={this.props.selected === this.props.type}
                       id={this.props.type} name="export" onChange={() => this.props.update(this.props.type)}/>
                <label className={"gray_box"} htmlFor={this.props.type}>
                    {java ? "Java Edition" : "Bedrock Edition"}
                    <span className="version">{version}</span>
                    <span className="labels">
                        {this.props.source && <span>Source Version</span>}
                        {beta && <span className="beta">Beta</span>}
                    </span>
                </label>
            </div>
        );
    }
}