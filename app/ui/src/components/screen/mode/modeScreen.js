import React from "react";
import {BaseScreen} from "../baseScreen";
import {SettingsScreen} from "../settings/settingsScreen";
import {ModeOption} from "./modeOption";
import {ProcessingScreen} from "../processing/processingScreen";

export class ModeScreen extends BaseScreen {
    state = {
        selected: undefined
    };

    getStage = () => {
        return 2;
    };

    convertWorld = (advanced) => {
        // Get data
        let data = this.app.state.sessionData.version.writers.filter(key => key.id === this.state.selected)[0];

        // Start preview if already done
        if (!this.app.state.requestPreview && advanced && this.app.settingsProgress.isComplete()) {
            this.app.generatePreview();
        }

        // Update output
        this.app.setState({outputType: data, requestPreview: advanced});

        let self = this;
        if (advanced) {
            // Fetch inputType mappings
            fetch("static://blocks/" + this.app.state.inputType.id)
                .then(res => res.json())
                .then(data =>
                    self.app.setState({inputBlockSuggestions: data})
                );

            // Fetch outputType mappings
            fetch("static://blocks/" + data.id)
                .then(res => res.json())
                .then(data =>
                    self.app.setState({outputBlockSuggestions: data})
                );

            // Next screen
            this.app.setScreen(SettingsScreen);
        } else {
            // Next screen
            this.app.setScreen(ProcessingScreen);
        }
    };
    updateSelected = (newSelection) => {
        this.setState({selected: newSelection});
    };

    render() {
        let writers = this.app.state.sessionData.version.writers.slice(0).reverse();
        return (
            <div className="maincol">
                <div className="topbar">
                    <h1>Export As</h1>
                    <h2>What Minecraft version would you like to export this world to?</h2>
                </div>
                {this.app.state.sessionData.version.warnings &&
                    <div className="main_content warning">
                        <span>Warning: {this.app.state.sessionData.version.warnings}</span>
                    </div>}
                <div className="main_content export">
                    {writers.map(key => (
                        <ModeOption
                            key={key.id} selected={this.state.selected} update={this.updateSelected} type={key.id}
                            value={key} source={key.id === this.app.state.inputType.id}/>
                    ))}
                </div>
                <div className="bottombar">
                    <button
                        type="submit" className="button magenta" disabled={this.state.selected === undefined}
                        onClick={() => this.convertWorld(true)}>Advanced Mode
                    </button>
                    <button
                        type="submit" className="button green" disabled={this.state.selected === undefined}
                        onClick={() => this.convertWorld(false)}>Convert
                    </button>
                </div>
            </div>
        );
    }
}