import React from "react";
import {BaseScreen} from "../baseScreen";
import {SettingsScreen} from "../settings/settingsScreen";
import {ModeOption, getFormatName} from "./modeOption";
import {ProcessingScreen} from "../processing/processingScreen";
import api from "../../../api";

const COLLAPSED_COUNT = 3;

// Editions shown first, in this order, with a friendly heading. Any other format
// is grouped automatically under its own heading so Chunker stays open ended.
const EDITIONS = [
    {id: "BEDROCK", label: "Bedrock Edition"},
    {id: "JAVA", label: "Java Edition"}
];

export class ModeScreen extends BaseScreen {
    state = {
        selected: undefined,
        expanded: {}
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
        this.setState({
            expanded: {...this.state.expanded, [edition]: !this.state.expanded[edition]}
        });
    };

    // Group writers by their format (the text before the first underscore) so every
    // format gets a section, not just the built-in editions. Known editions come
    // first in a fixed order; anything else follows in the order it appears.
    groupWriters = (writers) => {
        let buckets = new Map();
        for (let writer of writers) {
            let edition = writer.id.split("_")[0];
            if (!buckets.has(edition)) {
                buckets.set(edition, []);
            }
            buckets.get(edition).push(writer);
        }

        let groups = [];
        for (let edition of EDITIONS) {
            if (buckets.has(edition.id)) {
                groups.push({id: edition.id, label: edition.label, writers: buckets.get(edition.id)});
                buckets.delete(edition.id);
            }
        }
        for (let [edition, group] of buckets) {
            groups.push({id: edition, label: getFormatName(edition), writers: group});
        }
        return groups;
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

    renderSection(group, sourceId) {
        let {id, label, writers} = group;
        let collapsed = this.pickCollapsed(writers, sourceId);
        // Force the section expanded if the current selection would otherwise be hidden,
        // so the user never loses sight of what they picked.
        let selectionHidden = this.state.selected !== undefined
            && writers.some(w => w.id === this.state.selected)
            && !collapsed.some(w => w.id === this.state.selected);
        let isExpanded = this.state.expanded[id] || selectionHidden;
        let visible = isExpanded ? writers : collapsed;
        let canToggle = writers.length > COLLAPSED_COUNT && !selectionHidden;
        return (
            <div className="edition_section" key={id}>
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
                            onClick={() => this.toggleExpanded(id)}>
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
        let groups = this.groupWriters(writers);
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
                    {groups.map(group => this.renderSection(group, sourceId))}
                </div>
                <div className="bottombar">
                    <button onClick={() => window.location.reload()} type="submit" className="button red">Restart
                    </button>
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
