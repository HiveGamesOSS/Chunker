import React, {Component} from "react";
import {SelectWorldScreen} from "./screen/select/selectWorldScreen";
import api from "../api";
import {ProgressTracker} from "./progress";
import {Header} from "./page/header";
import {StepDisplay} from "./page/stepDisplay";
import {ErrorDisplay} from "./modal/errorDisplay";
import {Footer} from "./page/footer";
import {getVersionName} from "./screen/mode/modeOption";
import {parseMapBin, worldBoundsForAutoFit, isTileEmpty} from "./screen/settings/tab/preview/mapBin";
import {computeMinZoom} from "./screen/settings/tab/preview/autoFit";
import {ClientTileCache} from "./screen/settings/tab/preview/clientTileCache";

export class App extends Component {
    errorModal = React.createRef();
    previewProgress = new ProgressTracker("Generating world preview", (newState) => this.setState({previewProgress: newState}), () => {
        this.cancelTask()
    });
    // Tracks the second preview phase — pre-loading default-LOD tiles of the world bounds
    // so the Map mounts with content already on disk/cached. queuePosition is pinned to -1
    // so ProgressComponent renders the percentage bar from the start, not the "queue" UI.
    previewTileProgress = (() => {
        const t = new ProgressTracker("Loading map tiles", (newState) => this.setState({previewTileProgress: newState}));
        t.state.queuePosition = -1;
        return t;
    })();
    // Shared between the App-level preloader and the Map's ChunkerPreviewLayer. Tiles fetched
    // before Map mounts stay resident in this cache, so createTile finds them on first paint.
    clientTileCache = new ClientTileCache();
    _preloadUnsub = null;
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
        previewTileProgress: this.previewTileProgress.state,
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
        pruningSettings: {},
        mappings: {
            identifiers: []
        },
        convertResult: undefined,
        inputBlockSuggestions: [],
        outputBlockSuggestions: [],
        dimensionMapping: {
            "minecraft:overworld": "minecraft:overworld",
            "minecraft:the_nether": "minecraft:the_nether",
            "minecraft:the_end": "minecraft:the_end"
        },
        biomeMapping: {},
        customDimensions: {},
        converterSettings: {}
    };

    getDimensionMappingsJSON = () => {
        const mapping = this.state.dimensionMapping;
        const keys = Object.keys(mapping);
        const defaultCount = this.state.settings?.dimensions?.length ?? 3;
        if (keys.length !== defaultCount || keys.some(key => mapping[key] !== key)) {
            return JSON.stringify(mapping);
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

    getCustomDimensionsJSON = () => {
        const json = this.state.customDimensions;
        const dimensions = json?.dimensions;
        if (!dimensions || dimensions.length === 0) return "{}";
        return JSON.stringify(json);
    };

    getBiomeMappingsJSON = () => {
        const mappings = this.state.biomeMapping;
        if (Object.keys(mappings).length === 0) return "{}";
        return JSON.stringify(mappings);
    };

    getPruningJSON = () => {
        let pruningSettings = this.state.pruningSettings;
        if (!pruningSettings || Object.values(pruningSettings).filter(a => a !== null).length === 0) return "{}";
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
                "minecraft:overworld": "minecraft:overworld",
                "minecraft:the_nether": "minecraft:the_nether",
                "minecraft:the_end": "minecraft:the_end"
            };
        }

        let mappings = sessionData?.preloaded_settings?.block_mappings ?? sessionData?.preloaded_settings?.mappings ?? {
            identifiers: []
        };
        if (!mappings.identifiers) {
            mappings.identifiers = [];
        }

        let pruning = sessionData?.preloaded_settings?.pruning?.configs ?? {};
        if (!pruning || Object.values(pruning).filter(a => a !== null).length === 0) {
            pruning = {};
        }

        let biomeMapping = sessionData?.preloaded_settings?.biome_mappings ?? {};
        let customDimensions = sessionData?.preloaded_settings?.custom_dimensions ?? {};

        this.setState({
            sessionData: sessionData,
            inputType: sessionData.version.input,
            mappings: mappings,
            editedSettings: sessionData?.preloaded_settings?.world_settings ?? {},
            converterSettings: sessionData?.preloaded_settings?.converter_settings ?? {},
            dimensionMapping: dimensions,
            pruningSettings: pruning,
            biomeMapping: biomeMapping,
            customDimensions: customDimensions
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
        // Reset all three preview signals so a second generatePreview (different world /
        // re-trigger) re-runs the full cycle cleanly. Order matters under legacy React's
        // non-batched setState: kick previewProgress to 0 first so PreviewComponent flips to
        // the generation branch before previewData is cleared — otherwise we'd flash
        // "Loading map tiles" or an empty render in between.
        if (this._preloadUnsub) {
            this._preloadUnsub();
            this._preloadUnsub = null;
        }
        this.clientTileCache.evictAllExcept([]);
        this.previewProgress.setProgress(0);
        this.previewTileProgress.setProgress(0);
        this.setState({previewData: undefined});

        api.send({type: "flow", method: "generate_preview"}, this.previewProgress.pipe(function (message) {
            if (message.type === "error") {
                if (!message.cancelled) {
                    console.info("Failed to preview: " + message.error);
                    self.showError("Failed to render preview", message.error, message.errorId, message.stackTrace, true); // Preview isn't required
                }
                // Unblock the tile-loading branch so PreviewComponent doesn't hang there
                // (previewData stays undefined so the Map branch won't fire either).
                self.previewTileProgress.setProgress(100);
            } else if (message.type === "response") {
                let sessionId = self.state.sessionData && self.state.sessionData.session;
                if (!sessionId) {
                    console.info("Preview response received but no session id available");
                    self.setState({previewData: undefined});
                    self.previewTileProgress.setProgress(100);
                    return;
                }
                fetch(`session://${sessionId}/preview/map.bin`)
                    .then((r) => {
                        if (!r.ok) throw new Error("HTTP " + r.status);
                        return r.arrayBuffer();
                    })
                    .then((buffer) => {
                        if (buffer.byteLength > 0) {
                            const parsed = parseMapBin(buffer);
                            self.setState({previewData: parsed});
                            self.preloadDefaultWorldTiles(parsed, sessionId);
                        } else {
                            self.setState({previewData: undefined});
                            self.previewTileProgress.setProgress(100);
                        }
                    })
                    .catch((err) => {
                        console.info("Failed to fetch map.bin", err);
                        self.showError("Failed to load preview metadata", err.message || String(err), undefined, undefined, true);
                        self.setState({previewData: undefined});
                        self.previewTileProgress.setProgress(100);
                    });
            } else {
                console.info("Unknown response", message);
            }
        }));
    };

    /**
     * Pre-loads the default world's tiles before the Map mounts so it paints from a warm
     * cache. We enqueue tiles across multiple LODs — starting at the auto-fit LOD and
     * stepping toward LOD 0 — so the progress bar has enough granularity to look smooth.
     * The Java side's {@code PreviewTileCache} reuses the lower-LOD tiles when aggregating
     * higher-LOD ones, so adding intermediate LODs costs essentially no extra region work;
     * it only exposes more {@code tile_ready} events for us to count.
     */
    preloadDefaultWorldTiles = (parsedMapBin, sessionId) => {
        const dims = this.state.settings && this.state.settings.dimensions;
        if (!dims || dims.length === 0) {
            this.previewTileProgress.setProgress(100);
            return;
        }
        const layer = (this.state.previewState && this.state.previewState.layer) || dims[0];
        const bounds = worldBoundsForAutoFit(parsedMapBin, layer);
        if (!bounds) {
            this.previewTileProgress.setProgress(100);
            return;
        }
        const autoFitLod = computeMinZoom(bounds);

        // Soft cap on tracked tiles. At auto-fit LOD alone the world is 1–4 tiles, which
        // makes the bar jump in 25–100% steps. We grow the tracked set by stepping toward
        // LOD 0 (4x more tiles each step) until the next LOD would blow past this budget,
        // which lands us around 80–120 tracked events regardless of world size — fine
        // granularity without unbounded enqueue for huge worlds.
        const MAX_TRACKED = 120;
        const expected = new Set();       // keys: "lod,tx,tz"
        const lodRequests = [];
        for (let l = autoFitLod; l <= 0; l++) {
            const s = 1 << (-l);
            const chunksPerTile = 32 * s;   // 1 native tile = 512 px = 32 chunks at LOD 0
            const minTx = Math.floor(bounds.minX / chunksPerTile);
            const maxTx = Math.floor(bounds.maxX / chunksPerTile);
            const minTz = Math.floor(bounds.minZ / chunksPerTile);
            const maxTz = Math.floor(bounds.maxZ / chunksPerTile);
            const candidates = [];
            for (let tx = minTx; tx <= maxTx; tx++) {
                for (let tz = minTz; tz <= maxTz; tz++) {
                    if (!isTileEmpty(parsedMapBin, layer, l, tx, tz)) {
                        candidates.push(`${l},${tx},${tz}`);
                    }
                }
            }
            if (candidates.length === 0) continue;
            // Stop expanding to deeper LODs if adding them would push past the budget — but
            // only once we already have a coarser LOD enqueued. For the very first LOD we
            // accept its tiles unconditionally, otherwise a huge world would pre-load nothing.
            if (lodRequests.length > 0 && expected.size + candidates.length > MAX_TRACKED) break;
            for (const k of candidates) expected.add(k);
            lodRequests.push({lod: l, minTx, maxTx, minTz, maxTz});
        }

        if (expected.size === 0) {
            this.previewTileProgress.setProgress(100);
            return;
        }

        const total = expected.size;
        let received = 0;
        this.previewTileProgress.setProgress(0);

        // Lerp the displayed progress toward the real one. With ~85 tracked tiles the bar
        // moves in ~1% steps anyway, but tile_ready events arrive in bursts (a slow region
        // load releases a flurry of cheap aggregated tiles right after), so the raw bar
        // stutters. The lerp + the existing 300 ms CSS ease on .progress_fill gives a smooth
        // continuous animation between bursts.
        let displayedProgress = 0;
        let targetProgress = 0;
        const lerpTimer = setInterval(() => {
            const diff = targetProgress - displayedProgress;
            if (Math.abs(diff) < 0.1) return;
            displayedProgress += diff * 0.15;
            this.previewTileProgress.setProgress(displayedProgress);
        }, 100);

        const finish = () => {
            clearInterval(lerpTimer);
            if (this._preloadUnsub) {
                this._preloadUnsub();
                this._preloadUnsub = null;
            }
            this.previewTileProgress.setProgress(100);
        };
        const advance = () => {
            received++;
            if (received >= total) {
                finish();
            } else {
                targetProgress = (received / total) * 100;
            }
        };
        const onTile = (event) => {
            if (event.world !== layer) return;
            const key = `${event.lod},${event.tx},${event.tz}`;
            if (!expected.has(key)) return;
            expected.delete(key);
            // Populate the shared cache for every preloaded LOD so ChunkerPreviewLayer.createTile
            // finds a hit when the user zooms within the preloaded range (not just at autoFit).
            const cacheKey = this.clientTileCache.keyFor(layer, event.lod, event.tx, event.tz);
            const resolvedUrl = event.path && event.path.startsWith("session://")
                ? event.path
                : `session://${sessionId}/preview/${event.path}`;
            this.clientTileCache.put(cacheKey, {blobUrl: resolvedUrl, sizeBytes: 512 * 512 * 4});
            advance();
        };
        const onTileError = (event) => {
            if (event.world !== layer) return;
            const key = `${event.lod},${event.tx},${event.tz}`;
            if (!expected.has(key)) return;
            expected.delete(key);
            advance();
        };
        const unsubReady = api.addListener("tile_ready", onTile);
        const unsubError = api.addListener("tile_error", onTileError);
        this._preloadUnsub = () => {
            clearInterval(lerpTimer);
            if (unsubReady) unsubReady();
            if (unsubError) unsubError();
        };

        // One rect request per LOD. Java's PreviewTileService dedupes via inFlight, so
        // overlapping work between LODs (e.g. LOD -3 internally aggregating cached LOD -2
        // children) won't cause duplicated region loads.
        for (const r of lodRequests) {
            api.send({type: "flow", method: "request_preview_tiles",
                world: layer, lod: r.lod, minTx: r.minTx, minTz: r.minTz, maxTx: r.maxTx, maxTz: r.maxTz}, () => {});
        }
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