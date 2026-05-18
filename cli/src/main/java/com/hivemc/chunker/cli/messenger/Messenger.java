package com.hivemc.chunker.cli.messenger;

import com.google.common.util.concurrent.ExecutionError;
import com.google.gson.*;
import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;
import com.hivemc.chunker.cli.messenger.messaging.BasicMessageTypeAdapter;
import com.hivemc.chunker.cli.messenger.messaging.request.*;
import com.hivemc.chunker.cli.messenger.messaging.response.ErrorResponse;
import com.hivemc.chunker.cli.messenger.messaging.response.OutputResponse;
import com.hivemc.chunker.cli.messenger.messaging.response.ProgressResponse;
import com.hivemc.chunker.cli.messenger.messaging.response.ProgressStateResponse;
import com.hivemc.chunker.cli.messenger.messaging.response.TileErrorResponse;
import com.hivemc.chunker.cli.messenger.messaging.response.TileReadyResponse;
import com.hivemc.chunker.conversion.WorldConverter;
import com.hivemc.chunker.conversion.encoding.EncodingType;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.reader.LevelReader;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.conversion.encoding.preview.ChunkerWorldRegionRgbaSource;
import com.hivemc.chunker.conversion.encoding.preview.PreviewMapBin;
import com.hivemc.chunker.conversion.encoding.preview.PreviewMetadataReader;
import com.hivemc.chunker.conversion.encoding.preview.PreviewTileCache;
import com.hivemc.chunker.conversion.encoding.preview.PreviewTileService;
import com.hivemc.chunker.conversion.encoding.preview.RegionRgbaSource;
import com.hivemc.chunker.conversion.encoding.settings.SettingsLevelWriter;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerCustomBiome;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.conversion.intermediate.world.DimensionRegistry;
import com.hivemc.chunker.mapping.DimensionMapping;
import com.hivemc.chunker.mapping.DimensionMappingList;
import com.hivemc.chunker.mapping.MappingsFile;
import com.hivemc.chunker.mapping.resolver.MappingsFileResolvers;
import com.hivemc.chunker.pruning.PruningConfig;
import com.hivemc.chunker.scheduling.LoggedException;
import com.hivemc.chunker.scheduling.TaskMonitorThread;
import com.hivemc.chunker.scheduling.task.TrackedTask;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Messenger handles JSON communication between ChunkerWeb and itself through System.out.
 */
public class Messenger {
    private static final Map<UUID, Map<UUID, WorldConverter>> SESSION_ID_TO_WORLD_CONVERTERS = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, PreviewTileService> SESSION_ID_TO_PREVIEW_SERVICE = new ConcurrentHashMap<>();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(BasicMessage.class, new BasicMessageTypeAdapter())
            .create();

    /**
     * The main entry point for the messenger.
     *
     * @param args the arguments (unused).
     */
    public static void main(String[] args) {
        try {
            // Core message loop
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                BasicMessage message = GSON.fromJson(line, BasicMessage.class);
                switch (message.getType()) {
                    case DETECT_VERSION -> {
                        DetectVersionRequest detectVersionRequest = (DetectVersionRequest) message;
                        WorldConverter emptyWorldConverter = new WorldConverter(detectVersionRequest.getAnonymousId());

                        Optional<? extends LevelReader> reader = EncodingType.findReader(new File(detectVersionRequest.getInputPath()), emptyWorldConverter);
                        if (reader.isPresent()) {
                            LevelReader levelReader = reader.get();
                            JsonObject response = new JsonObject();
                            response.add("input", toEncodedObject(levelReader.getEncodingType(), levelReader.getVersion()));
                            response.add("writers", getWriters());

                            // Add warnings
                            String warnings = levelReader.getWarnings();
                            response.add("warnings", warnings == null ? null : new JsonPrimitive(warnings));

                            write(new OutputResponse(
                                    message.getRequestId(),
                                    response
                            ));
                        } else {
                            write(new ErrorResponse(
                                    message.getRequestId(),
                                    false,
                                    "World format is not supported.",
                                    "",
                                    null,
                                    null
                            ));
                        }
                    }
                    case SETTINGS -> {
                        SettingsRequest settingsRequest = (SettingsRequest) message;
                        WorldConverter worldConverter = createWorldConverter(settingsRequest.getAnonymousId(), settingsRequest.getRequestId());
                        boolean started = startConversionRequest(
                                settingsRequest.getAnonymousId(),
                                settingsRequest.getRequestId(),
                                worldConverter,
                                new File(settingsRequest.getInputPath()),
                                new SettingsLevelWriter(new File(settingsRequest.getOutputPath()))
                        );

                        // Turn off certain features for settings
                        worldConverter.setProcessLighting(false);
                        worldConverter.setProcessHeightMap(false);
                        worldConverter.setProcessBlockEntities(false);
                        worldConverter.setProcessColumnPreTransform(false);
                        worldConverter.setProcessEntities(false);
                        worldConverter.setProcessBiomes(false);
                        worldConverter.setProcessItems(false);

                        // Write an error if it failed to start
                        if (!started) {
                            removeWorldConverter(settingsRequest.getAnonymousId(), settingsRequest.getRequestId());
                            write(new ErrorResponse(
                                    settingsRequest.getRequestId(),
                                    false,
                                    "Failed to start settings task.",
                                    null,
                                    null,
                                    null
                            ));
                        }
                    }
                    case PREVIEW -> {
                        PreviewRequest previewRequest = (PreviewRequest) message;
                        UUID anonymousId = previewRequest.getAnonymousId();
                        UUID requestId = previewRequest.getRequestId();
                        File inputDir = new File(previewRequest.getInputPath());
                        File outputDir = new File(previewRequest.getOutputPath());

                        // Immediately signal that we have started — moves the renderer off the queue screen.
                        write(new ProgressResponse(requestId, 0.0));

                        // Run the metadata pass + tile service init off the messenger thread so the main loop stays responsive.
                        CompletableFuture.runAsync(() -> {
                            try {
                                // 1) Run the metadata pass. Choose Bedrock vs Java by what's in the world directory.
                                PreviewMetadataReader metadataReader = new PreviewMetadataReader();
                                if (new File(inputDir, "db/CURRENT").isFile()) {
                                    metadataReader.readBedrockWorld(inputDir, outputDir);
                                } else {
                                    metadataReader.readJavaWorld(inputDir, outputDir);
                                }

                                // Coarse progress signal halfway through.
                                write(new ProgressResponse(requestId, 0.5));

                                // 2) Spin up the long-lived tile service for this session.
                                PreviewMapBin mapBin = PreviewMapBin.read(new File(outputDir, "map.bin"));
                                RegionRgbaSource source = new ChunkerWorldRegionRgbaSource(inputDir);
                                int cacheEntries = PreviewTileCache.computeCapacityEntries(Runtime.getRuntime().maxMemory());
                                int workers = Math.min(4, Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
                                PreviewTileService service = new PreviewTileService(outputDir, mapBin, source, new PreviewTileCache(cacheEntries), workers);
                                service.setEventListener(new PreviewTileService.EventListener() {
                                    @Override public void onTileReady(TileReadyResponse r) { write(r); }
                                    @Override public void onTileError(TileErrorResponse r) { write(r); }
                                });

                                // Stop and replace any pre-existing service for this session.
                                PreviewTileService previous = SESSION_ID_TO_PREVIEW_SERVICE.put(anonymousId, service);
                                if (previous != null) previous.shutdown();

                                // 3) Tell the client we're done with the metadata pass.
                                write(new OutputResponse(requestId, null));
                            } catch (Throwable t) {
                                // Use Throwable: catches any unexpected runtime issue including OOMs, so we always
                                // surface an error to the client instead of leaving it stuck on the progress screen.
                                write(new ErrorResponse(
                                        requestId,
                                        false,
                                        "Failed to start preview.",
                                        null,
                                        t.getMessage(),
                                        printStackTrace(t)
                                ));
                            }
                        });
                    }
                    case REQUEST_PREVIEW_TILES -> {
                        RequestPreviewTilesRequest req = (RequestPreviewTilesRequest) message;
                        PreviewTileService svc = SESSION_ID_TO_PREVIEW_SERVICE.get(req.getAnonymousId());
                        if (svc == null) {
                            write(new ErrorResponse(req.getRequestId(), false,
                                    "No active preview session.", null, null, null));
                        } else {
                            svc.enqueueRange(req.getWorld(), req.getLod(),
                                    req.getMinTx(), req.getMinTz(), req.getMaxTx(), req.getMaxTz());
                            write(new OutputResponse(req.getRequestId(), null));
                        }
                    }
                    case CANCEL_PREVIEW_TILES -> {
                        CancelPreviewTilesRequest req = (CancelPreviewTilesRequest) message;
                        PreviewTileService svc = SESSION_ID_TO_PREVIEW_SERVICE.get(req.getAnonymousId());
                        if (svc != null) {
                            svc.cancel(req.getWorld(), req.getLod());
                        }
                        write(new OutputResponse(req.getRequestId(), null));
                    }
                    case CONVERT -> {
                        ConvertRequest convertRequest = (ConvertRequest) message;
                        WorldConverter worldConverter = createWorldConverter(convertRequest.getAnonymousId(), convertRequest.getRequestId());
                        if (convertRequest.getMappings() != null && !convertRequest.getMappings().isEmpty()) {
                            try {
                                worldConverter.setBlockMappings(new MappingsFileResolvers(MappingsFile.load(convertRequest.getMappings())));
                            } catch (Exception e) {
                                removeWorldConverter(convertRequest.getAnonymousId(), convertRequest.getRequestId());
                                write(new ErrorResponse(
                                        convertRequest.getRequestId(),
                                        false,
                                        "Failed to parse block mappings.",
                                        null,
                                        e.getMessage(),
                                        printStackTrace(e)
                                ));
                                return;
                            }
                        }
                        worldConverter.setChangedSettings(convertRequest.getNbtSettings());

                        // Apply dimension registry
                        DimensionRegistry registry = worldConverter.getDimensionRegistry();
                        JsonObject customDimensions = convertRequest.getCustomDimensions();
                        if (customDimensions != null && !customDimensions.isEmpty()) {
                            DimensionMappingList dimensionMapping = GSON.fromJson(customDimensions, DimensionMappingList.class);
                            if (dimensionMapping.getMappings() != null) {
                                List<DimensionMapping> mappings = dimensionMapping.getMappings();
                                for (int i = 0, id = 1000; i < mappings.size(); id++, i++) {
                                    DimensionMapping mapping = mappings.get(i);
                                    registry.register(mapping.identifier(), mapping.toDimension(id));
                                }
                            }
                        }

                        // Convert identifiers to Dimension types
                        Map<String, String> rawDimensionMapping = convertRequest.getInputToOutputDimension();
                        if (rawDimensionMapping != null) {
                            Map<Dimension, Dimension> dimensionMapping = new Object2ObjectOpenHashMap<>(rawDimensionMapping.size());
                            for (String key : rawDimensionMapping.keySet()) {
                                Dimension src = registry.getByIdentifier(key);
                                Dimension dst = registry.getByIdentifier(rawDimensionMapping.get(key));
                                if (src != null && dst != null) {
                                    dimensionMapping.put(src, dst);
                                }
                            }
                            worldConverter.setDimensionMapping(dimensionMapping);
                        }

                        // Apply biome mappings
                        Map<String, String> rawBiomeMapping = convertRequest.getBiomeMappings();
                        if (rawBiomeMapping != null && !rawBiomeMapping.isEmpty()) {
                            Map<ChunkerBiome, ChunkerBiome> biomeMapping = new Object2ObjectOpenHashMap<>(rawBiomeMapping.size());
                            for (Map.Entry<String, String> entry : rawBiomeMapping.entrySet()) {
                                Optional<? extends ChunkerBiome> src = entry.getKey().startsWith("minecraft:")
                                        ? ChunkerBiome.ChunkerVanillaBiome.find(entry.getKey())
                                        : Optional.of(new ChunkerCustomBiome(entry.getKey()));
                                Optional<? extends ChunkerBiome> dst = entry.getValue().startsWith("minecraft:")
                                        ? ChunkerBiome.ChunkerVanillaBiome.find(entry.getValue())
                                        : Optional.of(new ChunkerCustomBiome(entry.getValue()));
                                if (src.isPresent() && dst.isPresent()) {
                                    biomeMapping.put(src.get(), dst.get());
                                } else {
                                    removeWorldConverter(convertRequest.getAnonymousId(), convertRequest.getRequestId());
                                    String error;
                                    if (dst.isPresent()) {
                                        error = "Unknown input biome for mapping \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"";
                                    } else if (src.isPresent()) {
                                        error = "Unknown output biome for mapping \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"";
                                    } else {
                                        error = "Unknown input and output biome for mapping \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"";
                                    }
                                    write(new ErrorResponse(
                                            convertRequest.getRequestId(),
                                            false,
                                            error,
                                            null,
                                            null,
                                            null
                                    ));
                                    return;
                                }
                            }
                            worldConverter.setBiomeMapping(biomeMapping);
                        }

                        // Turn the identifier based pruning map into a dimension map
                        if (convertRequest.getPruningList() != null && convertRequest.getPruningList().getConfigs() != null && !convertRequest.getPruningList().getConfigs().isEmpty()) {
                            Map<String, PruningConfig> pruning = convertRequest.getPruningList().getConfigs();

                            Map<Dimension, PruningConfig> pruningConfigs = new Object2ObjectOpenHashMap<>(pruning.size());
                            for (String key : pruning.keySet()) {
                                pruningConfigs.put(registry.getByIdentifier(key), pruning.get(key));
                            }

                            worldConverter.setPruningConfigs(pruningConfigs);
                        }

                        // Load the in-game map data
                        if (convertRequest.getMaps() != null) {
                            // Parse maps
                            List<ChunkerMap> chunkerMaps = new ArrayList<>(convertRequest.getMaps().size());
                            for (JsonElement element : convertRequest.getMaps()) {
                                JsonObject jsonObject = element.getAsJsonObject();
                                ChunkerMap map = GSON.fromJson(jsonObject, ChunkerMap.class);
                                if (jsonObject.has("file")) {
                                    try {
                                        map.loadImage(new File(jsonObject.get("file").getAsString()));
                                    } catch (IOException e) {
                                        removeWorldConverter(convertRequest.getAnonymousId(), convertRequest.getRequestId());
                                        write(new ErrorResponse(
                                                convertRequest.getRequestId(),
                                                false,
                                                "Failed to parse map " + map.getId() + ".",
                                                null,
                                                e.getMessage(),
                                                printStackTrace(e)
                                        ));
                                        return;
                                    }
                                }
                                chunkerMaps.add(map);
                            }

                            worldConverter.setMaps(chunkerMaps);
                        }

                        worldConverter.setAllowNBTCopying(convertRequest.isCopyNbt());
                        worldConverter.setProcessMaps(!convertRequest.isSkipMaps());
                        worldConverter.setProcessLootTables(!convertRequest.isSkipLootTables());
                        worldConverter.setProcessItems(!convertRequest.isSkipItemConversion());
                        worldConverter.setProcessColumnPreTransform(!convertRequest.isSkipBlockConnections());
                        worldConverter.setLevelDBCompaction(convertRequest.isEnableCompact());
                        worldConverter.setDiscardEmptyChunks(convertRequest.isDiscardEmptyChunks());
                        worldConverter.setPreventYBiomeBlending(convertRequest.isPreventYBiomeBlending());
                        worldConverter.setCustomIdentifiers(convertRequest.isCustomIdentifiers());

                        // Add the handler for the compaction signal to let the UI know
                        worldConverter.setCompactionSignal((started) -> {
                            if (started) {
                                write(new ProgressStateResponse(convertRequest.getRequestId(), "Compacting output", true));
                            } else {
                                write(new ProgressStateResponse(convertRequest.getRequestId(), null, false));
                            }
                        });

                        // Find the writer to use
                        Optional<? extends LevelWriter> writer = findWriter(convertRequest.getOutputType(), worldConverter, new File(convertRequest.getOutputPath()));
                        if (writer.isEmpty()) {
                            removeWorldConverter(convertRequest.getAnonymousId(), convertRequest.getRequestId());
                            write(new ErrorResponse(
                                    convertRequest.getRequestId(),
                                    false,
                                    "Failed to find output type.",
                                    null,
                                    null,
                                    null
                            ));
                        } else {
                            boolean started = startConversionRequest(
                                    convertRequest.getAnonymousId(),
                                    convertRequest.getRequestId(),
                                    worldConverter,
                                    new File(convertRequest.getInputPath()),
                                    writer.get()
                            );

                            // Write an error if it failed to start
                            if (!started) {
                                removeWorldConverter(convertRequest.getAnonymousId(), convertRequest.getRequestId());
                                write(new ErrorResponse(
                                        convertRequest.getRequestId(),
                                        false,
                                        "Failed to start conversion.",
                                        null,
                                        null,
                                        null
                                ));
                            }
                        }
                    }
                    case KILL -> {
                        KillRequest killRequest = (KillRequest) message;

                        // Shutdown any preview tile service for this session.
                        PreviewTileService previewService = SESSION_ID_TO_PREVIEW_SERVICE.remove(killRequest.getAnonymousId());
                        if (previewService != null) previewService.shutdown();

                        // Loop through all the converters under this anonymous ID and cancel them
                        Map<UUID, WorldConverter> converters = SESSION_ID_TO_WORLD_CONVERTERS.get(killRequest.getAnonymousId());
                        if (converters != null) {
                            List<CompletableFuture<Void>> futures = new ArrayList<>(converters.size());
                            for (WorldConverter worldConverter : converters.values()) {
                                futures.add(worldConverter.cancel(null));
                            }

                            // Ensure all futures have completed
                            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((ex, result) -> {
                                write(new OutputResponse(message.getRequestId(), new JsonPrimitive(true)));
                            });
                        } else {
                            // Mark as successful
                            write(new OutputResponse(message.getRequestId(), new JsonPrimitive(true)));
                        }
                    }
                    default -> // Not supported
                            write(new ErrorResponse(
                                    message.getRequestId(),
                                    false,
                                    "Message type does not exist, outdated software?",
                                    null,
                                    null,
                                    null
                            ));
                }
            }
        } catch (OutOfMemoryError e) {
            try {
                e.printStackTrace();
            } catch (OutOfMemoryError e2) {
                // We tried printing it
            }

            // Use error code 12 for OOM
            System.exit(12);
        }
    }

    /**
     * Remove a world converter from the internal storage.
     *
     * @param sessionID the session ID of the user.
     * @param taskID    the specific task ID to remove.
     */
    public static void removeWorldConverter(UUID sessionID, UUID taskID) {
        Map<UUID, WorldConverter> taskToConverter = SESSION_ID_TO_WORLD_CONVERTERS.get(sessionID);
        if (taskToConverter != null) {
            taskToConverter.remove(taskID);
        }
    }

    /**
     * Create a world converter and add it to the internal storage.
     *
     * @param sessionID the session ID of the user.
     * @param taskID    the specific task ID to use.
     * @return an instance of the newly created WorldConverter.
     */
    public static WorldConverter createWorldConverter(UUID sessionID, UUID taskID) {
        WorldConverter worldConverter = new WorldConverter(sessionID);
        Map<UUID, WorldConverter> taskToConverter = SESSION_ID_TO_WORLD_CONVERTERS.computeIfAbsent(sessionID, (ignored) -> new Object2ObjectOpenHashMap<>());
        taskToConverter.put(taskID, worldConverter);
        return worldConverter;
    }

    /**
     * Find and construct a writer from an input ID.
     *
     * @param id              the ID of the writer in the format FORMAT_MAJOR_MINOR_PATCH
     * @param worldConverter  the world converter instance to use for writer construction.
     * @param outputDirectory the output directory which files should be written to.
     * @return an optional which present if the writer was found and successfully constructed.
     */
    public static Optional<? extends LevelWriter> findWriter(String id, WorldConverter worldConverter, File outputDirectory) {
        try {
            int separatorIndex = id.indexOf("_");
            String type = separatorIndex == -1 ? id : id.substring(0, separatorIndex);
            Version version = null;

            // Parse version if it's present
            if (separatorIndex != -1) {
                // Parse the version
                String versionString = id.substring(separatorIndex + 1);

                // Bedrock versions are in the format R20 -> 1_20
                if (versionString.startsWith("R")) {
                    versionString = "1_" + versionString.substring(1);
                }

                versionString = versionString.replace('_', '.'); // Replace underscores with periods
                version = Version.fromString(versionString);
            }

            // Parse the encoding type
            Optional<EncodingType> encodingType = findEncodingType(type);
            if (encodingType.isEmpty()) return Optional.empty(); // Unable to find encoding type

            // Attempt to create a writer
            return encodingType.get().createWriter(outputDirectory, version, worldConverter);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty(); // Unable to make writer
        }
    }

    /**
     * Find the encoding type from a string.
     *
     * @param type the input (case-insensitive) matching the EncodingType names.
     * @return present if found with the encoding type.
     */
    public static Optional<EncodingType> findEncodingType(String type) {
        for (EncodingType encodingType : EncodingType.getWriteableTypes()) {
            if (encodingType.getName().equalsIgnoreCase(type)) return Optional.of(encodingType);
        }
        return Optional.empty();
    }

    /**
     * Schedule a conversion request and set up a monitor thread to feedback the progress / result.
     *
     * @param sessionID      the user sessionID.
     * @param taskID         the current taskID for this request.
     * @param worldConverter the world converter being used.
     * @param inputPath      the input path to the input world.
     * @param writer         the writer to be used for the request.
     * @return true if the task was started successfully.
     */
    public static boolean startConversionRequest(UUID sessionID, UUID taskID, WorldConverter worldConverter, File inputPath, LevelWriter writer) {
        // Detect the input reader
        Optional<? extends LevelReader> reader = EncodingType.findReader(inputPath, worldConverter);
        if (reader.isEmpty()) return false; // Shouldn't happen, but we should handle the case that it did

        TrackedTask<Void> environment = worldConverter.convert(reader.get(), writer);

        // Start progress monitor thread
        TaskMonitorThread taskMonitorThread = new TaskMonitorThread(environment,
                (progress) -> write(new ProgressResponse(taskID, progress)),
                (exception) -> {
                    if (exception.isPresent()) {
                        Throwable throwable = exception.get();

                        // Unwrap completion exception
                        if (throwable instanceof CompletionException) {
                            throwable = throwable.getCause();
                        }

                        // Unwrap execution error
                        if (throwable instanceof ExecutionError) {
                            throwable = throwable.getCause();
                        }

                        // Check that it isn't a cancellation
                        if (throwable instanceof CancellationException) {
                            write(new ErrorResponse(
                                    taskID,
                                    true,
                                    "The process was cancelled",
                                    null,
                                    null,
                                    null
                            ));
                        } else {
                            // Report if it wasn't logged
                            if (!(throwable instanceof LoggedException)) {
                                throwable.printStackTrace();
                            }

                            // Always exit if it's an OOM as the memory may not be recoverable
                            if (throwable instanceof OutOfMemoryError) {
                                System.exit(12);
                            } else {
                                // Tell the user there was an error
                                write(new ErrorResponse(
                                        taskID,
                                        false,
                                        getFriendlyErrorMessage(exception.get()),
                                        sessionID.toString(),
                                        exception.get().getMessage(),
                                        printStackTrace(exception.get())
                                ));
                            }
                        }
                    } else if (worldConverter.isCancelled()) {
                        write(new ErrorResponse(
                                taskID,
                                true,
                                "The process was cancelled",
                                null,
                                null,
                                null
                        ));
                    } else {
                        JsonObject output = new JsonObject();

                        // Add session ID if there were exceptions
                        if (worldConverter.isExceptions()) {
                            output.addProperty("anonymousId", sessionID.toString());
                        }

                        // Generate the array of missing identifiers
                        JsonArray missingIdentifiers = new JsonArray(worldConverter.getMissingIdentifiers().size());
                        for (Map.Entry<Converter.MissingMappingType, String> entry : worldConverter.getMissingIdentifiers().entries()) {
                            JsonObject missingIdentifier = new JsonObject();
                            missingIdentifier.addProperty("identifier", entry.getKey().getName() + ": " + entry.getValue());
                            missingIdentifier.addProperty("type", entry.getKey().toString());
                            missingIdentifier.addProperty("value", entry.getValue());
                            missingIdentifiers.add(missingIdentifier);
                        }
                        output.add("missingIdentifiers", missingIdentifiers);

                        // Write the response
                        write(new OutputResponse(
                                taskID,
                                output
                        ));
                    }

                    // Request GC
                    System.gc();
                }
        );

        // Start the monitor thread
        taskMonitorThread.start();

        // Started
        return true;
    }

    /**
     * Get a list of user-facing writers encoded JsonObjects which can be used for writing.
     * Note: This excludes any internal formats like PREVIEW / SETTINGS as they shouldn't be selectable.
     *
     * @return a JsonArray of JsonObjects.
     */
    public static JsonArray getWriters() {
        JsonArray writers = new JsonArray();
        for (EncodingType encodingType : EncodingType.getWriteableTypes()) {
            if (encodingType.isInternal()) continue; // Don't list internal
            Collection<Version> versions = encodingType.getSupportedVersions();

            // If the type doesn't have any versions, write just the type
            if (versions == null || versions.isEmpty()) {
                writers.add(toEncodedObject(encodingType, null));
            } else {
                // Write each version
                for (Version version : versions) {
                    writers.add(toEncodedObject(encodingType, version));
                }
            }
        }
        return writers;
    }

    /**
     * Transform an encoding type and version to a JSON representation.
     *
     * @param encodingType the format being encoded.
     * @param version      the version being encoded.
     * @return encoded as a JsonObject in the format {"id": "JAVA_1_20_5", "version":"1.20.5", "type":"JAVA"}
     */
    public static JsonObject toEncodedObject(EncodingType encodingType, @Nullable Version version) {
        JsonObject encoding = new JsonObject();
        if (version != null) {
            encoding.addProperty("id", toEncodedString(encodingType, version));
            encoding.addProperty("version", version.toString());
        } else {
            encoding.addProperty("id", encodingType.getName().toUpperCase(Locale.ROOT));
        }
        encoding.addProperty("type", encodingType.getName().toUpperCase(Locale.ROOT));
        return encoding;
    }

    /**
     * Transform an encoding type and version to a string.
     *
     * @param encodingType the format being encoded.
     * @param version      the version being encoded.
     * @return encoded in the format FORMAT_MAJOR_MINOR_PATCH, except Bedrock which is encoded as BEDROCK_RMINOR_PATCH.
     */
    public static String toEncodedString(EncodingType encodingType, Version version) {
        String encodedVersion = version.toString().replaceAll("\\.0$", "").replace('.', '_');

        // Bedrock versions are in the format R20_60
        if (encodingType == EncodingType.BEDROCK) {
            encodedVersion = encodedVersion.replaceAll("^1_", "R");
        }

        // Return the encoded version
        return encodingType.getName().toUpperCase(Locale.ROOT) + "_" + encodedVersion;
    }

    /**
     * Serialize a message to the output.
     *
     * @param message the message to serialize.
     */
    public static void write(BasicMessage message) {
        System.out.println(GSON.toJson(message));
    }

    /**
     * Convert a throwable into a string using printStackTrace.
     *
     * @param throwable the throwable to output.
     * @return the stack trace as a string or null if it failed to print.
     */
    @Nullable
    public static String printStackTrace(Throwable throwable) {
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Get a friendly error message to show to the user as the main message when conversion fails.
     *
     * @param throwable the throwable which caused the error.
     * @return a user-friendly error message, if one doesn't exist it will just state "A fatal error occurred during
     * conversion."
     */
    public static String getFriendlyErrorMessage(Throwable throwable) {
        // Unwrap CompletionException
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }

        // Handle the case that LevelDB fails to read a marketplace world
        if (throwable instanceof IllegalStateException &&
                throwable.getMessage().equals("CURRENT file does not end with newline")) {
            return "The world is either encrypted or corrupted. Chunker is unable to read " +
                    "marketplace worlds as they are encrypted.";
        }

        // Return the default
        return "A fatal error occurred during conversion.";
    }
}