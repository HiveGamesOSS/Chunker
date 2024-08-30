package com.hivemc.chunker.conversion;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.gson.JsonObject;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.reader.LevelReader;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.handlers.LevelConversionHandler;
import com.hivemc.chunker.conversion.handlers.WorldConversionHandler;
import com.hivemc.chunker.conversion.handlers.pipeline.Pipeline;
import com.hivemc.chunker.conversion.handlers.pretransform.ColumnPreTransformConversionHandler;
import com.hivemc.chunker.conversion.handlers.pretransform.ColumnPreTransformWriterConversionHandler;
import com.hivemc.chunker.conversion.handlers.writer.LevelWriterConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelSettings;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.mapping.resolver.MappingsFileResolvers;
import com.hivemc.chunker.pruning.PruningConfig;
import com.hivemc.chunker.pruning.PruningRegion;
import com.hivemc.chunker.scheduling.task.Environment;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import com.hivemc.chunker.scheduling.task.TrackedTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A converter which allows a session to be created with settings and taking a reader and writer to use for conversion.
 */
public class WorldConverter implements Converter {
    /**
     * Signal used to indicate compaction has started, this is used to indicate the progress bar should change.
     */
    public static final String SIGNAL_COMPACTION = "signal_compaction";

    private final UUID sessionID;
    // State
    @Nullable
    protected Consumer<Boolean> compactionSignalConsumer;
    @Nullable
    protected ChunkerLevel level;
    @Nullable
    protected LevelReader reader = null;
    @Nullable
    protected LevelWriter writer = null;
    @Nullable
    protected Environment environment = null;
    protected Multimap<Converter.MissingMappingType, String> missingIdentifiers = Multimaps.synchronizedSetMultimap(
            MultimapBuilder.enumKeys(Converter.MissingMappingType.class)
                    .hashSetValues()
                    .build()
    );
    // Settings
    @Nullable
    private Map<Dimension, PruningConfig> pruningConfigs;
    @Nullable
    private Map<Dimension, Dimension> dimensionMapping;
    @Nullable
    private JsonObject changedSettings;
    @Nullable
    private List<ChunkerMap> maps;
    @Nullable
    private MappingsFileResolvers blockMappings;
    private boolean levelDBCompaction = false;
    private boolean processMaps = true;
    private boolean processItems = true;
    private boolean processEntities = true;
    private boolean processBlockEntities = true;
    private boolean processLootTables = true;
    private boolean processBiomes = true;
    private boolean processHeightMap = true;
    private boolean processLighting = true;
    private boolean processColumnPreTransform = true;
    private boolean allowNBTCopying = false;
    private boolean discardEmptyChunks = false;
    private boolean preventYBiomeBlending = false;
    private boolean customIdentifiers = true;
    private boolean exceptions = false;
    private boolean cancelled = false;

    /**
     * Create a new WorldConverter with a sessionID.
     *
     * @param sessionID the sessionID to identify the conversion.
     */
    public WorldConverter(UUID sessionID) {
        this.sessionID = sessionID;
    }

    /**
     * Set the handler for if compaction is signalled by the converter as started / stopped.
     *
     * @param compactionSignalConsumer a consumer which is called when compaction starts.
     */
    public void setCompactionSignal(@Nullable Consumer<Boolean> compactionSignalConsumer) {
        this.compactionSignalConsumer = compactionSignalConsumer;
    }

    /**
     * Set the pruning configuration used for discarding columns from the input worlds.
     *
     * @param pruningConfigs the configurations or null if none present.
     */
    public void setPruningConfigs(@Nullable Map<Dimension, PruningConfig> pruningConfigs) {
        this.pruningConfigs = pruningConfigs;
    }


    /**
     * Set which dimension should map to which output dimension, if a dimension is not in the map it is discarded.
     *
     * @param dimensionMapping the mappings or null if it should keep the same input as output.
     */
    public void setDimensionMapping(@Nullable Map<Dimension, Dimension> dimensionMapping) {
        this.dimensionMapping = dimensionMapping;
    }

    /**
     * Set whether LevelDB should compact after writing.
     *
     * @param levelDBCompaction true if it should compact (Java only).
     */
    public void setLevelDBCompaction(boolean levelDBCompaction) {
        this.levelDBCompaction = levelDBCompaction;
    }

    /**
     * Set whether in-game maps should be converted.
     *
     * @param processMaps true if they should be converted.
     */
    public void setProcessMaps(boolean processMaps) {
        this.processMaps = processMaps;
    }

    /**
     * Set whether biome blending should be avoided (Java only).
     *
     * @param preventYBiomeBlending true if it should be avoided.
     */
    public void setPreventYBiomeBlending(boolean preventYBiomeBlending) {
        this.preventYBiomeBlending = preventYBiomeBlending;
    }

    /**
     * Set whether items should be converted otherwise air will be used.
     *
     * @param processItems true if they should be converted.
     */
    public void setProcessItems(boolean processItems) {
        this.processItems = processItems;
    }

    /**
     * Set whether entities should be converted.
     *
     * @param processEntities true if they should be converted.
     */
    public void setProcessEntities(boolean processEntities) {
        this.processEntities = processEntities;
    }

    /**
     * Set whether block-entities should be converted.
     *
     * @param processBlockEntities true if they should be converted.
     */
    public void setProcessBlockEntities(boolean processBlockEntities) {
        this.processBlockEntities = processBlockEntities;
    }

    /**
     * Set whether container loot tables should be converted.
     *
     * @param processLootTables true if they should be converted.
     */
    public void setProcessLootTables(boolean processLootTables) {
        this.processLootTables = processLootTables;
    }

    /**
     * Set whether biomes should be converted.
     *
     * @param processBiomes true if they should be converted.
     */
    public void setProcessBiomes(boolean processBiomes) {
        this.processBiomes = processBiomes;
    }

    /**
     * Set whether the height-map should be converted.
     *
     * @param processHeightMap true if it should be converted.
     */
    public void setProcessHeightMap(boolean processHeightMap) {
        this.processHeightMap = processHeightMap;
    }

    /**
     * Set whether lighting should be converted.
     *
     * @param processLighting true if it should be converted.
     */
    public void setProcessLighting(boolean processLighting) {
        this.processLighting = processLighting;
    }

    /**
     * Set whether this conversion allows copying of NBT from input to output.
     *
     * @param allowNBTCopying true if the output supports the same NBT as the input.
     */
    public void setAllowNBTCopying(boolean allowNBTCopying) {
        this.allowNBTCopying = allowNBTCopying;
    }

    /**
     * Set whether empty chunks should be removed.
     *
     * @param discardEmptyChunks true if they should be removed.
     */
    public void setDiscardEmptyChunks(boolean discardEmptyChunks) {
        this.discardEmptyChunks = discardEmptyChunks;
    }

    /**
     * Set whether pre-transform should be used (block-connections and other neighbour chunk fetching).
     *
     * @param processColumnPreTransform true if it should be enabled.
     */
    public void setProcessColumnPreTransform(boolean processColumnPreTransform) {
        this.processColumnPreTransform = processColumnPreTransform;
    }

    /**
     * Set whether custom identifiers should be allowed.
     *
     * @param customIdentifiers true if custom identifiers should be converted.
     */
    public void setCustomIdentifiers(boolean customIdentifiers) {
        this.customIdentifiers = customIdentifiers;
    }

    @Override
    public boolean shouldLevelDBCompaction() {
        return levelDBCompaction;
    }

    @Override
    public boolean shouldProcessMaps() {
        return processMaps;
    }

    @Override
    public boolean shouldProcessItems() {
        return processItems;
    }

    @Override
    public boolean shouldProcessEntities() {
        return processEntities;
    }

    @Override
    public boolean shouldProcessBlockEntities() {
        return processBlockEntities;
    }

    @Override
    public boolean shouldProcessLootTables() {
        return processLootTables;
    }

    @Override
    public boolean shouldProcessBiomes() {
        return processBiomes;
    }

    @Override
    public boolean shouldProcessHeightMap() {
        return processHeightMap;
    }

    @Override
    public boolean shouldProcessColumnPreTransform() {
        return processColumnPreTransform;
    }

    @Override
    public boolean shouldProcessLighting() {
        return processLighting;
    }

    @Override
    public boolean shouldPreventYBiomeBlending() {
        return preventYBiomeBlending;
    }

    @Override
    public boolean shouldProcessDimension(Dimension dimension) {
        return dimensionMapping == null || dimensionMapping.containsKey(dimension);
    }

    /**
     * Whether this converter was cancelled.
     *
     * @return true if it was cancelled.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Whether any exceptions occurred and were logged during conversion.
     *
     * @return true if any exceptions occurred.
     */
    public boolean isExceptions() {
        return exceptions;
    }

    /**
     * Get a map of all the missing identifiers found during conversion.
     *
     * @return the backing map of missing identifiers sorted by the type.
     */
    public Multimap<MissingMappingType, String> getMissingIdentifiers() {
        return missingIdentifiers;
    }

    @Override
    public boolean shouldProcessRegion(Dimension dimension, RegionCoordPair regionPair) {
        if (pruningConfigs == null || pruningConfigs.isEmpty()) return true;

        PruningConfig pruningConfig = pruningConfigs.get(dimension);

        // Ensure the config / regions are present
        if (pruningConfig == null || pruningConfig.getRegions() == null || pruningConfig.getRegions().isEmpty())
            return true;

        ChunkCoordPair minRegionChunk = regionPair.getChunk(0, 0);
        ChunkCoordPair maxRegionChunk = regionPair.getChunk(31, 31);

        // Find regions which overlap
        for (PruningRegion region : pruningConfig.getRegions()) {
            if (maxRegionChunk.chunkX() >= region.getMinChunkX() &&
                    minRegionChunk.chunkX() <= region.getMaxChunkX() &&
                    maxRegionChunk.chunkZ() >= region.getMinChunkZ() &&
                    minRegionChunk.chunkZ() <= region.getMaxChunkZ()) {
                return pruningConfig.isInclude(); // Overlap
            }
        }

        return !pruningConfig.isInclude(); // No overlap
    }

    @Override
    public boolean shouldProcessColumn(Dimension dimension, ChunkCoordPair columnPair) {
        if (pruningConfigs == null || pruningConfigs.isEmpty()) return true;

        PruningConfig pruningConfig = pruningConfigs.get(dimension);

        // Ensure the config / regions are present
        if (pruningConfig == null || pruningConfig.getRegions() == null || pruningConfig.getRegions().isEmpty())
            return true;

        // Find any matching area
        for (PruningRegion region : pruningConfig.getRegions()) {
            if (columnPair.chunkX() >= region.getMinChunkX() && columnPair.chunkX() <= region.getMaxChunkX() &&
                    columnPair.chunkZ() >= region.getMinChunkZ() && columnPair.chunkZ() <= region.getMaxChunkZ()) {
                return pruningConfig.isInclude();
            }
        }
        return !pruningConfig.isInclude();
    }

    @Override
    public boolean shouldAllowNBTCopying() {
        return allowNBTCopying;
    }

    @Override
    public boolean shouldAllowCustomIdentifiers() {
        return customIdentifiers;
    }

    @Override
    @Nullable
    public MappingsFileResolvers getBlockMappings() {
        return blockMappings;
    }

    /**
     * Set the block mappings to use.
     *
     * @param blockMappings the mappings to use or null if not present.
     */
    public void setBlockMappings(@Nullable MappingsFileResolvers blockMappings) {
        this.blockMappings = blockMappings;
    }

    @Override
    public boolean shouldDiscardEmptyChunks() {
        return discardEmptyChunks;
    }

    @Override
    public Optional<Dimension> getNewDimension(Dimension dimension) {
        return dimensionMapping == null ? Optional.of(dimension) : Optional.ofNullable(dimensionMapping.get(dimension));
    }

    @Override
    public void logNonFatalException(Throwable throwable) {
        exceptions = true;

        // Log to console
        Converter.super.logNonFatalException(throwable);
    }

    /**
     * Log a fatal exception and cancel conversion.
     *
     * @param throwable the throwable being logged.
     */
    public void logFatalException(Throwable throwable) {
        // Cancel
        cancel(throwable);

        // Call the non-fatal logger
        logNonFatalException(throwable);
    }

    /**
     * Handle a signal sent by the tasks.
     *
     * @param signalName  the name of the signal.
     * @param signalValue the value that is being signalled.
     */
    public void handleSignal(String signalName, Object signalValue) {
        // If it's the compaction signal forward it to the handler
        if (signalName.equals(SIGNAL_COMPACTION)) {
            if (compactionSignalConsumer == null) return;
            compactionSignalConsumer.accept((Boolean) signalValue);
        }
    }

    @Override
    public void logMissingMapping(MissingMappingType type, String identifier) {
        // Add to map
        if (missingIdentifiers.put(type, identifier)) {
            // Log if it's new
            Converter.super.logMissingMapping(type, identifier);
        }
    }

    @Override
    public Optional<ChunkerLevel> level() {
        return Optional.ofNullable(level);
    }

    /**
     * The settings which should be modified for the level.dat.
     *
     * @return the settings as a key-value object, otherwise null if no changes.
     */
    @Nullable
    public JsonObject getChangedSettings() {
        return changedSettings;
    }

    /**
     * Set the settings which should be applied to the level.dat.
     *
     * @param changedSettings the changed settings.
     */
    public void setChangedSettings(@Nullable JsonObject changedSettings) {
        this.changedSettings = changedSettings;
    }

    /**
     * Set the maps used for the world.
     *
     * @param maps the list of maps for the world.
     */
    public void setMaps(@Nullable List<ChunkerMap> maps) {
        this.maps = maps;
    }

    /**
     * Create and start the conversion task.
     *
     * @param reader the reader to use for conversion.
     * @param writer the writer to use for conversion.
     * @return a task which completes when the conversion completes.
     */
    public TrackedTask<Void> convert(@NotNull LevelReader reader, @NotNull LevelWriter writer) {
        // Create the environment
        this.reader = reader;
        this.writer = writer;
        cancelled = false;
        exceptions = false;
        missingIdentifiers.clear();
        environment = Task.environment("World Conversion", 8, this::logFatalException, this::handleSignal);

        try {
            // Create the handler that calls the writer
            LevelWriterConversionHandler writerHandler = new LevelWriterConversionHandler(writer);

            // Create the conversion pipeline (it always uses the writer as a base)
            Pipeline pipeline = new Pipeline(writerHandler);

            // Add handler for the level, this handles saving the level to the world converter instance
            level = null;
            pipeline.levelHandlers((delegate) -> new LevelHandler(this, delegate));

            // Add handler for the world, handles the remapping of dimensions
            pipeline.worldHandlers((delegate, level) -> new WorldHandler(this, delegate));

            // Pre-transforming is allowing columns to be processed together
            // If it's enabled, we need to hold the chunks using the handler
            // The Reader is responsible for solving which edges are needed
            // But we need to call the writer PreTransformManager ourselves as it's before writing.
            if (shouldProcessColumnPreTransform()) {
                // Add the pre-transform writer conversion handler, this ensures columns know which edges are needed
                // Add pre-transform to the pipeline (this is required to handle processes that need adjacent chunks)
                pipeline.columnHandlers(
                        (delegate, world) -> new ColumnPreTransformWriterConversionHandler(
                                writer::getPreTransformManager,
                                delegate,
                                true
                        ),
                        ColumnPreTransformConversionHandler::new
                );
            } else {
                // Add the writer handler, this ensures that the writer is still called just without connected chunks
                pipeline.columnHandlers(
                        (delegate, world) -> new ColumnPreTransformWriterConversionHandler(
                                writer::getPreTransformManager,
                                delegate,
                                false
                        )
                );
            }

            // Get the composed handler to use for conversion
            LevelConversionHandler handler = pipeline.build();

            // Level reading
            Task.asyncConsume("Reading Level", TaskWeight.NORMAL, reader::readLevel, handler);

            // Return the environment to allow for progress tracking
            return environment;
        } finally {
            environment.close(); // Close indicates that we're done scheduling the base tasks

            // Ensure free is called for the reader & writer (always)
            environment.setFreeCallback(() -> {
                // Free reader
                try {
                    reader.free();
                } catch (Throwable e) {
                    try {
                        logNonFatalException(e);
                    } catch (Throwable e2) {
                        // We tried, this is likely an OOM
                    }
                }

                // Free writer
                try {
                    writer.free();
                } catch (Throwable e) {
                    try {
                        logNonFatalException(e);
                    } catch (Throwable e2) {
                        // We tried, this is likely an OOM
                    }
                }
            });
        }

    }

    /**
     * Cancel the conversion task.
     *
     * @param fatalException an exception to use as the reason for the future.
     */
    public void cancel(@Nullable Throwable fatalException) {
        cancelled = true;

        // Cancel the environment with the exception
        if (environment != null) {
            environment.cancel(fatalException);
        }
    }

    /**
     * WorldConversionHandler which applies dimension remapping.
     */
    static class WorldHandler implements WorldConversionHandler {
        private final WorldConverter worldConverter;
        private final WorldConversionHandler delegate;

        public WorldHandler(WorldConverter worldConverter, WorldConversionHandler delegate) {
            this.worldConverter = worldConverter;
            this.delegate = delegate;
        }

        @Override
        public Task<ColumnConversionHandler> convertWorld(ChunkerWorld world) {
            // Apply dimension remapping
            Optional<Dimension> newDimension = worldConverter.getNewDimension(world.getDimension());
            if (newDimension.isPresent()) {
                world.setDimension(newDimension.get());
                return delegate.convertWorld(world);
            }

            // Dimension no longer exists
            return Task.asyncUnwrap("Empty Dimension", TaskWeight.LOW, () -> null);
        }

        @Override
        public void flushWorld(ChunkerWorld world) {
            delegate.flushWorld(world);
        }

        @Override
        public void flushWorlds() {
            delegate.flushWorlds();
        }
    }

    /**
     * LevelHandler that integrates with WorldConverter:
     * - Replacing the maps with specified ones.
     * - Moving maps and portals to the correct dimension.
     * - Applies any changed settings to the level.dat.
     * - Saves the level to the WorldConverter for context.
     */
    static class LevelHandler implements LevelConversionHandler {
        private final WorldConverter worldConverter;
        private final LevelConversionHandler delegate;

        public LevelHandler(WorldConverter worldConverter, LevelConversionHandler delegate) {
            this.worldConverter = worldConverter;
            this.delegate = delegate;
        }

        @Override
        public Task<WorldConversionHandler> convertLevel(ChunkerLevel level) {
            // Apply the maps to the level
            if (worldConverter.maps != null) {
                level.setMaps(worldConverter.maps);
            }

            // Apply dimension remapping to maps
            level.getMaps().removeIf(map -> {
                Optional<Dimension> newDimension = worldConverter.getNewDimension(map.getDimension());
                if (newDimension.isPresent()) {
                    map.setDimension(newDimension.get());
                    return false;
                } else {
                    return true; // Dimension no longer exists
                }
            });

            // Apply dimension remapping to portals
            level.getPortals().removeIf(portal -> {
                Optional<Dimension> newDimension = worldConverter.getNewDimension(portal.getDimension());
                if (newDimension.isPresent()) {
                    portal.setDimension(newDimension.get());
                    return false;
                } else {
                    return true; // Dimension no longer exists
                }
            });

            // Turn the current settings to JSON
            JsonObject baseSettings = level.getSettings().toJSON();

            // Apply any changed settings
            if (worldConverter.getChangedSettings() != null) {
                baseSettings.asMap().putAll(worldConverter.getChangedSettings().asMap());
            }

            // Save settings (turning them from json to object)
            level.setSettings(ChunkerLevelSettings.fromJSON(baseSettings));

            // Save level to converter
            worldConverter.level = level;

            // Call delegate so it's converted
            return delegate.convertLevel(level);
        }

        @Override
        public void flushLevel() {
            delegate.flushLevel();
        }
    }
}
