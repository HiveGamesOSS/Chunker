import React, {Component} from "react";

export class CLIExportTab extends Component {
    app = this.props.app;

    generateCommand = () => {
        let outputType = this.app.state.outputType.id;
        let worldSettings = JSON.stringify(JSON.stringify(this.app.state.editedSettings));
        let pruningSettings = JSON.stringify(this.app.getPruningJSON());
        let dimensionMappings = JSON.stringify(this.app.getDimensionMappingsJSON());
        let blockMappings = JSON.stringify(this.app.getBlockMappingsJSON());
        let converterSettings = JSON.stringify(JSON.stringify(this.app.state.converterSettings));

        return "java -jar chunker-cli.jar " +
            " --inputDirectory input" +
            " --outputDirectory output" +
            " --outputFormat " + outputType +
            (converterSettings.length === 4 ? "" : " --converterSettings " + converterSettings) +
            (worldSettings.length === 4 ? "" : " --worldSettings " + worldSettings) +
            (dimensionMappings.length === 4 ? "" : " --dimensionMappings " + dimensionMappings) +
            (blockMappings.length === 4 ? "" : " --blockMappings " + blockMappings) +
            (blockMappings.length === 4 ? "" : " --pruning " + pruningSettings) +
            (outputType === this.app.state.inputType.id ? " --keepOriginalNBT" : "");
    };

    render() {
        return (
            <div>
                <React.Fragment>
                    <div className="topbar">
                        <h1>Export Settings for CLI</h1>
                        <h2>This screen allows you to view your current settings as a CLI command.</h2>
                    </div>
                    <div className="main_content settings dimensions api">
                        <div>
                            <h4 style={{marginBlockEnd: "5px", marginBlockStart: "1px"}}>Example Usage</h4>
                        </div>
                        <div className="white_box">
                            <label className="legend" htmlFor="name">
                                <span className="tooltip">You can use this command with the CLI to replicate your current settings. You will need to make sure the paths are correct.</span>
                                Command
                            </label>
                            <input type="text" value={this.generateCommand()} readOnly={true}/>
                        </div>
                        <div>
                            <hr/>
                            <h4 style={{marginBlockEnd: "5px"}}>CLI Parameters</h4>
                        </div>
                        <div className="white_box">
                            <label className="legend" htmlFor="name">
                                <span className="tooltip">This is the output format for the world.</span>--outputFormat
                            </label>
                            <input type="text"
                                   value={this.app.state.outputType.id}
                                   readOnly={true}/>
                        </div>
                        <div className="white_box">
                            <label className="legend" htmlFor="name">
                                    <span
                                        className="tooltip">This is the settings from the Converter Settings tab.</span>--converterSettings
                            </label>
                            <input type="text" value={JSON.stringify(this.app.state.converterSettings)}
                                   readOnly={true}/>
                        </div>
                        <div className="white_box">
                            <label className="legend" htmlFor="name">
                                <span className="tooltip">This is the NBT settings which are written from the World Settings tab.</span>--worldSettings
                            </label>
                            <input type="text" value={JSON.stringify(this.app.state.editedSettings)}
                                   readOnly={true}/>
                        </div>
                        <div className="white_box">
                            <label className="legend" htmlFor="name">
                                <span className="tooltip">These are the mappings on how it should map dimensions from the Dimensions tab.</span>--dimensionMappings
                            </label>
                            <input type="text" value={this.app.getDimensionMappingsJSON()} readOnly={true}/>
                        </div>
                        <div className="white_box">
                            <label className="legend" htmlFor="name">
                                <span className="tooltip">These are the mappings on how it should prune dimensions from the Dimensions tab.</span>--pruning
                            </label>
                            <input type="text" value={this.app.getPruningJSON()} readOnly={true}/>
                        </div>
                        <div className="white_box">
                            <label className="legend" htmlFor="name">
                                <span className="tooltip">These are the block mappings generated from the Block Mapping tab.</span>--blockMappings
                            </label>
                            <input type="text" value={this.app.getBlockMappingsJSON()} readOnly={true}/>
                        </div>
                    </div>
                </React.Fragment>
            </div>
        );
    }
}