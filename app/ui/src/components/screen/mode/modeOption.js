import React, {Component} from "react";

export function getVersionName(inputVersionName) {
    // Filter the version name and extract the actual version for display
    let type = inputVersionName.split("_")[0];
    let version = inputVersionName.replace(type + "_", "").replace(type, "");
    version = version.replace(/^R(.*)$/g, "1.$1").replace(/_/g, ".");

    // Ensure it's in the form 1.0.0
    if (version.length > 0) {
        let size = version.split(".").length;
        if (size > 0) {
            while (size < 3) {
                size++;
                version += ".0";
            }
        }
    }
    return version;
}

export class ModeOption extends Component {
    render() {
        let version = this.props.value.version ? this.props.value.version : getVersionName(this.props.type);
        let java = this.props.type.startsWith("JAVA_");
        let bedrock = this.props.type.startsWith("BEDROCK_");
        let beta = (bedrock && version === "1.21.90") || (java && version === "1.21.6"); // Beta label
        let label;
        if (java) {
            label = "Java Edition";
        } else if (bedrock) {
            label = "Bedrock Edition";
        } else {
            label = this.props.type.split("_")[0];
        }
        return (
            <div>
                <input type="radio" value={this.props.type} checked={this.props.selected === this.props.type}
                       id={this.props.type} name="export" onChange={() => this.props.update(this.props.type)}/>
                <label className={"gray_box"} htmlFor={this.props.type}>
                    {label}
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