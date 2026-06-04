import React from "react";
import {BaseScreen} from "../baseScreen";
import {SettingsScreen} from "../settings/settingsScreen";
import {ModeOption} from "./modeOption";
import {ProcessingScreen} from "../processing/processingScreen";
import api from "../../../api";

const COLLAPSED_COUNT = 3;

export class ModeScreen extends BaseScreen {
    state = {
        selected: undefined,
        javaExpanded: false,
        bedrockExpanded: false
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

            api.send({
                type: "flow",
                method: "get_biomes",
                outputType: data.id
            }, function (message) {
                if (message.type === "response") {
                    self.app.setState({
                        inputBiomeSuggestions: message.output.input,
                        outputBiomeSuggestions: message.output.output
                    });
                }
            });

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

    toggleExpanded = (edition) => {
        if (edition === "JAVA") {
            this.setState({javaExpanded: !this.state.javaExpanded});
        } else {
            this.setState({bedrockExpanded: !this.state.bedrockExpanded});
        }
    };

    // Pick the writers to display when collapsed: the COLLAPSED_COUNT most recent,
    // but if the source version belongs to this edition and isn't already among
    // them, swap it in for the oldest of the top picks. Order stays newest-first.
    pickCollapsed = (writers, sourceId) => {
        if (writers.length <= COLLAPSED_COUNT) {
            return writers;
        }
        let top = writers.slice(0, COLLAPSED_COUNT);
        let sourceIndex = writers.findIndex(w => w.id === sourceId);
        if (sourceIndex === -1 || sourceIndex < COLLAPSED_COUNT) {
            return top;
        }
        let combined = top.slice(0, COLLAPSED_COUNT - 1).concat([writers[sourceIndex]]);
        return combined.sort((a, b) => writers.indexOf(a) - writers.indexOf(b));
    };

    renderSection(label, edition, writers, expanded, sourceId) {
        if (writers.length === 0) {
            return null;
        }
        let collapsed = this.pickCollapsed(writers, sourceId);
        // Force the section expanded if the current selection would otherwise be hidden,
        // so the user never loses sight of what they picked.
        let selectionHidden = this.state.selected !== undefined
            && writers.some(w => w.id === this.state.selected)
            && !collapsed.some(w => w.id === this.state.selected);
        let isExpanded = expanded || selectionHidden;
        let visible = isExpanded ? writers : collapsed;
        let canToggle = writers.length > COLLAPSED_COUNT && !selectionHidden;
        return (
            <div className="edition_section">
                <h3 className="edition_heading">{label}</h3>
                <div className="edition_grid">
                    {visible.map(key => (
                        <ModeOption
                            key={key.id} selected={this.state.selected} update={this.updateSelected}
                            type={key.id} value={key} source={key.id === sourceId}/>
                    ))}
                </div>
                {canToggle && (
                    <div className="edition_toggle">
                        <button
                            type="button" className="button blue small"
                            onClick={() => this.toggleExpanded(edition)}>
                            {isExpanded ? "Show Less" : "Show All (" + writers.length + ")"}
                        </button>
                    </div>
                )}
            </div>
        );
    }

    render() {
        let writers = this.app.state.sessionData.version.writers.slice(0).reverse();
        let sourceId = this.app.state.inputType ? this.app.state.inputType.id : undefined;
        let javaWriters = writers.filter(w => w.id.startsWith("JAVA_"));
        let bedrockWriters = writers.filter(w => w.id.startsWith("BEDROCK_"));
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
                    {this.renderSection("Bedrock Edition", "BEDROCK", bedrockWriters, this.state.bedrockExpanded, sourceId)}
                    {this.renderSection("Java Edition", "JAVA", javaWriters, this.state.javaExpanded, sourceId)}
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
