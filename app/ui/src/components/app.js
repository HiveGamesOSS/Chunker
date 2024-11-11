import React, {Component} from "react";
import {SelectWorldScreen} from "./screen/select/selectWorldScreen";
import api from "../api";
import {ProgressTracker} from "./progress";
import {Header} from "./page/header";
import {StepDisplay} from "./page/stepDisplay";
import {ErrorDisplay} from "./modal/errorDisplay";
import {Footer} from "./page/footer";
import {decode} from "base64-arraybuffer"
import {getVersionName} from "./screen/mode/modeOption";

export class App extends Component {
    errorModal = React.createRef();
    previewProgress = new ProgressTracker("Generating world preview", (newState) => this.setState({previewProgress: newState}), () => {
        this.cancelTask()
    });
    settingsProgress = new ProgressTracker("Grabbing world information", (newState) => this.setState({settingsProgress: newState}));
    screen = React.createRef();
    defaultConverterSettings = {
        customIdentifiers: true,
        blockConnections: true,
        itemConversion: true,
        lootTableConversion: true,
        mapConversion: true,
        enableCompact: true,
        discardEmptyChunks: false,
        preventYBiomeBlending: false
    };
    state = {
        previewProgress: this.previewProgress.state,
        settingsProgress: this.previewProgress.state,
        previewData: undefined,
        requestedPreview: false,
        showError: false,
        stage: 1,
        screen: SelectWorldScreen,
        sessionData: undefined,
        inputType: undefined,
        outputType: undefined,
        settings: undefined,
        worldSettingsTab: "World Settings",
        dimensionSettingsTab: undefined,
        editedSettings: {},
        pruningSettings: [null, null, null],
        mappings: {
            identifiers: []
        },
        convertResult: undefined,
        inputBlockSuggestions: [],
        outputBlockSuggestions: [],
        dimensionMapping: {
            "OVERWORLD": "OVERWORLD",
            "NETHER": "NETHER",
            "THE_END": "THE_END"
        },
        converterSettings: {}
    };

    getDimensionMappingsJSON = () => {
        if (Object.keys(this.state.dimensionMapping).length !== 3 ||
            Object.keys(this.state.dimensionMapping).filter(key => this.state.dimensionMapping[key] !== key).length > 0) {
            return JSON.stringify(this.state.dimensionMapping);
        } else {
            return "{}";
        }
    };

    getBlockMappingsJSON = () => {
        // Remove invalid mappings
        let clone = JSON.parse(JSON.stringify(this.state.mappings));
        // This checks we don't have empty identifiers, they should be null/undefined if valid (custom blocks)
        clone.identifiers = clone.identifiers.filter(a => (a.old_identifier?.length ?? 1) > 0 && (a.new_identifier?.length ?? 1) > 0);

        let mappings = JSON.stringify(clone);
        return mappings.length === 18 ? "{}" : mappings;
    };

    getPruningJSON = () => {
        let pruningSettings = this.state.pruningSettings;
        if (!pruningSettings || pruningSettings.filter(a => a !== null).length === 0) return "{}";
        return JSON.stringify({configs: pruningSettings});
    };

    getWorldName = () => {
        return this.state.editedSettings["LevelName"] || (this.state.settings !== undefined && this.state.settings.settings["World Settings"].filter(a => a.name === "LevelName")[0].value) || "World";
    };

    setScreen = (newScreen) => {
        this.setState({
            screen: newScreen
        });
    };

    updateSession = (sessionData) => {
        let dimensions = sessionData?.preloaded_settings?.dimension_mappings ?? {};
        if (Object.keys(dimensions).length === 0) {
            dimensions = {
                "OVERWORLD": "OVERWORLD",
                "NETHER": "NETHER",
                "THE_END": "THE_END"
            };
        }

        let mappings = sessionData?.preloaded_settings?.block_mappings ?? sessionData?.preloaded_settings?.mappings ?? {
            identifiers: []
        };
        if (!mappings.identifiers) {
            mappings.identifiers = [];
        }

        let pruning = sessionData?.preloaded_settings?.pruning?.configs ?? [];
        if (!pruning || pruning.filter(a => a !== null).length === 0) {
            pruning = [null, null, null];
        }

        this.setState({
            sessionData: sessionData,
            inputType: sessionData.version.input,
            mappings: mappings,
            editedSettings: sessionData?.preloaded_settings?.world_settings ?? {},
            converterSettings: sessionData?.preloaded_settings?.converter_settings ?? {},
            dimensionMapping: dimensions,
            pruningSettings: pruning
        });
    };

    savePreviewState = (data) => {
        this.setState({
            previewState: data
        });
    };

    showError = (title, body, errorId, stackTrace, canClose, ignoreIfErrorShowing = false) => {
        if (ignoreIfErrorShowing && this.state.showError && !this.state.canClose) return; // Ignore (used for websocket errors as they come after)
        this.setState({showError: true}, () => {
            this.errorModal.current.setState({
                title: title,
                body: body,
                errorId: errorId,
                stackTrace: stackTrace,
                canClose: canClose
            });
        });
    };

    generateSettings = () => {
        let self = this;
        api.send({type: "flow", method: "generate_settings"}, this.settingsProgress.pipe(function (message) {
            if (message.type === "error") {
                if (!message.cancelled) {
                    console.info("Failed to get settings: " + message.error);
                    self.showError("Failed to get world settings", message.error, message.errorId, message.stackTrace, false);
                }
            } else if (message.type === "response") {
                self.setState({
                    settings: message.output
                });

                if (self.state.requestPreview) {
                    // Start preview
                    self.generatePreview();
                }
            } else {
                console.info("Unknown response", message);
            }
        }));
    };

    generatePreview = () => {
        let self = this;
        api.send({type: "flow", method: "generate_preview"}, this.previewProgress.pipe(function (message) {
            if (message.type === "error") {
                if (!message.cancelled) {
                    console.info("Failed to preview: " + message.error);
                    self.showError("Failed to render preview", message.error, message.errorId, message.stackTrace, true); // Preview isn't required
                }
            } else if (message.type === "response") {
                // Doesn't need to do anything, as progress.js will mark as complete :)
                // Decode minX, minZ, maxX, maxZ
                let base64 = message.output;

                if (base64.length > 0) {
                    let buffer = decode(base64);
                    let worlds = [];
                    let dataView = new DataView(buffer);
                    let bufferIndex = 0;
                    let worldCount = dataView.getInt32(bufferIndex, true);
                    bufferIndex += 4;

                    for (let i = 0; i < worldCount; i++) {
                        let worldIndex = dataView.getUint8(bufferIndex);
                        bufferIndex += 1;

                        // World Index
                        worlds[worldIndex] = {};
                        worlds[worldIndex].minX = dataView.getInt32(bufferIndex, true);
                        bufferIndex += 4;
                        worlds[worldIndex].minZ = dataView.getInt32(bufferIndex, true);
                        bufferIndex += 4;
                        worlds[worldIndex].maxX = dataView.getInt32(bufferIndex, true);
                        bufferIndex += 4;
                        worlds[worldIndex].maxZ = dataView.getInt32(bufferIndex, true);
                        bufferIndex += 4;
                        let regionCount = dataView.getInt32(bufferIndex, true);
                        bufferIndex += 4;
                        bufferIndex += regionCount * (8 + 128); // Skip region bytes
                    }

                    self.setState({previewData: worlds});
                } else {
                    // Set to empty data (meaning preview is streamed)
                    self.setState({previewData: []});
                }
            } else {
                console.info("Unknown response", message);
            }
        }));
    };

    cancelTask = (callback) => {
        api.send({type: "flow", method: "cancel"}, function (message) {
            if (callback) {
                callback();
            }
        });
    };

    setStage = (stage) => this.setState({stage: stage});

    generateIssueLink = (error, stackTrace = undefined) => {
        let version = ((window.chunker && window.chunker.version) || "unknown") + "-" + ((window.chunker && window.chunker.gitVersion) || "unknown");
        let platform = (window.chunker && window.chunker.platform) || "";

        // Version info
        let inputVersion;
        if (this.state.inputType) {
            inputVersion = (this.state.inputType.id.startsWith("JAVA_") ? "Java " : "Bedrock ") + getVersionName(this.state.inputType.id);
        } else {
            inputVersion = "N/A";
        }
        let outputVersion;
        if (this.state.outputType) {
            outputVersion = (this.state.outputType.id.startsWith("JAVA_") ? "Java " : "Bedrock ") + getVersionName(this.state.outputType.id);
        } else {
            outputVersion = "N/A";
        }
        let description = (error ? encodeURIComponent("Error Displayed: `" + error + "`\n" + (stackTrace ? "Stack Trace: \n```\n" + stackTrace + "```\n" : "")) : "");

        return "https://github.com/HiveGamesOSS/Chunker/issues/new?assignees=&labels=Conversion+Bug&projects=&template=world_conversion_issue.yml" +
            "&version=" + version +
            "&platform=" + platform +
            "&input_version=" + inputVersion +
            "&output_version=" + outputVersion +
            "&description=" + description;
    };

    render() {
        let Screen = this.state.screen;
        return (
            <React.Fragment>
                <Header/>
                {this.state.showError &&
                    <ErrorDisplay ref={this.errorModal}
                                  app={this}
                                  close={() => this.setState({showError: false})}/>
                }
                <div className="wrapper">
                    <main id="content">
                        <StepDisplay stage={this.state.stage}/>
                        <Screen app={this} ref={this.screen}/>
                    </main>
                </div>
                <Footer app={this}/>
            </React.Fragment>
        );
    }
}

export default App;