import React from "react";
import {BaseScreen} from "../baseScreen";
import {ProcessingScreen} from "../processing/processingScreen";
import {ModeScreen} from "../mode/modeScreen";
import "./settingsScreen.css"
import {PreviewTab} from "./tab/previewTab";
import {WorldSettingsTab} from "./tab/worldSettingsTab";
import {DimensionPruningTab} from "./tab/dimensionPruningTab";
import {PaletteMappingsTab} from "./tab/paletteMappingsTab";
import {ConverterSettingsTab} from "./tab/converterSettingsTab";
import {CLIExportTab} from "./tab/cliTab";

export class SettingsScreen extends BaseScreen {
    state = {
        tab: "preview"
    };

    nextScreen = () => this.app.setScreen(ProcessingScreen);
    previousScreen = () => this.app.setScreen(ModeScreen);

    getStage = () => {
        return 2;
    };

    switchTab = (name, e) => {
        this.setState({tab: name});
        e.preventDefault();
    };

    render() {
        return (
            <React.Fragment>
                <div className="sidebar">
                    <ul>
                        <li>
                            <button className={this.state.tab === "preview" ? "active" : ""}
                                    onClick={(e) => this.switchTab("preview", e)}>World Preview
                            </button>
                        </li>
                        <li>
                            <button className={this.state.tab === "settings" ? "active" : ""}
                                    onClick={(e) => this.switchTab("settings", e)}>World Settings
                            </button>
                        </li>
                        <li>
                            <button className={this.state.tab === "dimensions" ? "active" : ""}
                                    onClick={(e) => this.switchTab("dimensions", e)}>Dimensions/Pruning
                            </button>
                        </li>
                        <li>
                            <button className={this.state.tab === "mappings" ? "active" : ""}
                                    onClick={(e) => this.switchTab("mappings", e)}>Block Mapping
                            </button>
                        </li>
                        <li>
                            <button
                                className={this.state.tab === "converter" || this.state.tab === "api" ? "active" : ""}
                                onClick={(e) => this.switchTab("converter", e)}>Converter Settings
                            </button>
                        </li>
                    </ul>
                </div>
                <div className="maincol">
                    {this.state.tab === "preview" &&
                        <PreviewTab app={this.app}/>
                    }
                    {this.state.tab === "settings" &&
                        <WorldSettingsTab app={this.app}/>
                    }
                    {this.state.tab === "dimensions" &&
                        <DimensionPruningTab app={this.app}/>
                    }
                    {this.state.tab === "converter" &&
                        <ConverterSettingsTab app={this.app}/>
                    }
                    {this.state.tab === "cli" &&
                        <CLIExportTab app={this.app}/>
                    }
                    {this.state.tab === "mappings" &&
                        <PaletteMappingsTab app={this.app}/>
                    }

                    <div className="bottombar">
                        <button className="button red" onClick={this.previousScreen}>Switch Mode</button>
                        <button className="button green" onClick={this.nextScreen}>Convert</button>
                    </div>
                </div>
            </React.Fragment>
        );
    }
}