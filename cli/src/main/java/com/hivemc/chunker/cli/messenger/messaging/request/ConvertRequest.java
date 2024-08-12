package com.hivemc.chunker.cli.messenger.messaging.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;
import com.hivemc.chunker.cli.messenger.messaging.DimensionPruningList;
import com.hivemc.chunker.cli.messenger.messaging.InvokesWorldConverterRequest;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * Request indicating to convert the world to another format.
 */
public class ConvertRequest extends BasicMessage implements InvokesWorldConverterRequest {
    private final UUID anonymousId;
    private final String inputPath;
    private final String outputPath;
    private final String outputType;
    @Nullable
    private final Map<Dimension, Dimension> inputToOutputDimension;
    @Nullable
    private final JsonObject mappings;
    @Nullable
    private final JsonObject nbtSettings;
    @Nullable
    private final JsonArray maps;
    private final boolean copyNbt;
    @Nullable
    private final DimensionPruningList pruningList;
    private final boolean skipMaps;
    private final boolean skipLootTables;
    private final boolean skipItemConversion;
    private final boolean skipBlockConnections;
    private final boolean enableCompact;
    private final boolean discardEmptyChunks;
    private final boolean preventYBiomeBlending;
    private final boolean customIdentifiers;

    /**
     * Create a new conversion request.
     *
     * @param anonymousId            the session ID for the user.
     * @param inputPath              the input path on the current machine for the world.
     * @param outputPath             the output path to write the world to.
     * @param outputType             the output type to use for the world in the format FORMAT_MAJOR_MINOR_PATCH.
     * @param inputToOutputDimension mapping of dimension type to output dimension type. If the dimension isn't present
     *                               in the map, it won't be processed.
     * @param mappings               the mappings (null if not present) which are parsed as a MappingsFile.
     * @param nbtSettings            NBT settings (if present) which are merged with the original settings.
     * @param maps                   a list of maps, null if it should use the original maps.
     * @param copyNbt                true if NBT should be copied from the input world to the output.
     * @param pruningList            a list of pruning rules to run for each dimension.
     * @param skipMaps               whether in-game maps should be skipped.
     * @param skipLootTables         whether loot tables inside containers should be skipped.
     * @param skipItemConversion     whether air should be used instead of converting items.
     * @param customIdentifiers      whether custom identifiers should be read/written.
     * @param skipBlockConnections   whether data based on other chunks should be skipped.
     * @param enableCompact          whether the world should be compacted after conversion (Bedrock).
     * @param discardEmptyChunks     whether empty chunks should not be written.
     * @param preventYBiomeBlending  whether biomes should be prevented from blending (Java).
     */
    public ConvertRequest(UUID anonymousId, String inputPath, String outputPath, String outputType, @Nullable Map<Dimension, Dimension> inputToOutputDimension, @Nullable JsonObject mappings, @Nullable JsonObject nbtSettings, @Nullable JsonArray maps, boolean copyNbt, @Nullable DimensionPruningList pruningList, boolean skipMaps, boolean skipLootTables, boolean skipItemConversion, boolean customIdentifiers, boolean skipBlockConnections, boolean enableCompact, boolean discardEmptyChunks, boolean preventYBiomeBlending) {
        this.anonymousId = anonymousId;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.outputType = outputType;
        this.inputToOutputDimension = inputToOutputDimension;
        this.mappings = mappings;
        this.nbtSettings = nbtSettings;
        this.maps = maps;
        this.copyNbt = copyNbt;
        this.pruningList = pruningList;
        this.skipMaps = skipMaps;
        this.skipLootTables = skipLootTables;
        this.skipItemConversion = skipItemConversion;
        this.customIdentifiers = customIdentifiers;
        this.skipBlockConnections = skipBlockConnections;
        this.enableCompact = enableCompact;
        this.discardEmptyChunks = discardEmptyChunks;
        this.preventYBiomeBlending = preventYBiomeBlending;
    }

    /**
     * Create a new conversion request.
     *
     * @param requestId              the request ID to use.
     * @param anonymousId            the session ID for the user.
     * @param inputPath              the input path on the current machine for the world.
     * @param outputPath             the output path to write the world to.
     * @param outputType             the output type to use for the world in the format FORMAT_MAJOR_MINOR_PATCH.
     * @param inputToOutputDimension mapping of dimension type to output dimension type. If the dimension isn't present
     *                               in the map, it won't be processed.
     * @param mappings               the mappings (null if not present) which are parsed as a MappingsFile.
     * @param nbtSettings            NBT settings (if present) which are merged with the original settings.
     * @param maps                   a list of maps, null if it should use the original maps.
     * @param copyNbt                true if NBT should be copied from the input world to the output.
     * @param pruningList            a list of pruning rules to run for each dimension.
     * @param skipMaps               whether in-game maps should be skipped.
     * @param skipLootTables         whether loot tables inside containers should be skipped.
     * @param skipItemConversion     whether air should be used instead of converting items.
     * @param customIdentifiers      whether custom identifiers should be read/written.
     * @param skipBlockConnections   whether data based on other chunks should be skipped.
     * @param enableCompact          whether the world should be compacted after conversion (Bedrock).
     * @param discardEmptyChunks     whether empty chunks should not be written.
     * @param preventYBiomeBlending  whether biomes should be prevented from blending (Java).
     */
    public ConvertRequest(UUID requestId, UUID anonymousId, String inputPath, String outputPath, String outputType, @Nullable Map<Dimension, Dimension> inputToOutputDimension, @Nullable JsonObject mappings, @Nullable JsonObject nbtSettings, @Nullable JsonArray maps, boolean copyNbt, @Nullable DimensionPruningList pruningList, boolean skipMaps, boolean skipLootTables, boolean skipItemConversion, boolean customIdentifiers, boolean skipBlockConnections, boolean enableCompact, boolean discardEmptyChunks, boolean preventYBiomeBlending) {
        super(requestId);
        this.anonymousId = anonymousId;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.outputType = outputType;
        this.inputToOutputDimension = inputToOutputDimension;
        this.mappings = mappings;
        this.nbtSettings = nbtSettings;
        this.maps = maps;
        this.copyNbt = copyNbt;
        this.pruningList = pruningList;
        this.skipMaps = skipMaps;
        this.skipLootTables = skipLootTables;
        this.skipItemConversion = skipItemConversion;
        this.customIdentifiers = customIdentifiers;
        this.skipBlockConnections = skipBlockConnections;
        this.enableCompact = enableCompact;
        this.discardEmptyChunks = discardEmptyChunks;
        this.preventYBiomeBlending = preventYBiomeBlending;
    }

    /**
     * The session ID used by the user.
     *
     * @return a random UUID used by the user.
     */
    public UUID getAnonymousId() {
        return anonymousId;
    }

    /**
     * The path to the input world.
     *
     * @return a path to the input world on the current system.
     */
    public String getInputPath() {
        return inputPath;
    }

    /**
     * The path which the output should be written.
     *
     * @return a path to where the output world should be written on the current system.
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * The output type to use for the world in the format:
     * FORMAT_MAJOR_MINOR_PATCH, e.g. JAVA_1_20_5, for Bedrock it can be prefixed with R, BEDROCK_R20 = 1.20
     *
     * @return the type as an encoded string.
     */
    public String getOutputType() {
        return outputType;
    }

    /**
     * A mapping of dimension type to output dimension type. If the dimension isn't present in the map, it won't be
     * processed.
     *
     * @return null if the input should map to the output otherwise a map of dimensions.
     */
    @Nullable
    public Map<Dimension, Dimension> getInputToOutputDimension() {
        return inputToOutputDimension;
    }

    /**
     * Get the mappings to use for blocks/items.
     *
     * @return the mappings (null if not present) which are parsed as a MappingsFile.
     */
    @Nullable
    public JsonObject getMappings() {
        return mappings;
    }

    /**
     * Get NBT settings which should be used for the level.dat
     *
     * @return NBT settings (if present) which are merged with the original settings.
     */
    @Nullable
    public JsonObject getNbtSettings() {
        return nbtSettings;
    }

    /**
     * A list of maps to use for in-game map conversion.
     *
     * @return a list of maps, null if it should use the original maps.
     */
    @Nullable
    public JsonArray getMaps() {
        return maps;
    }

    /**
     * Whether copying NBT should be enabled, this should only be enabled when the input and output is compatible.
     *
     * @return true if NBT should be copied between formats.
     */
    public boolean isCopyNbt() {
        return copyNbt;
    }

    /**
     * Get a list of pruning rules to run for each dimension.
     *
     * @return pruning rules otherwise null if none are present.
     */
    @Nullable
    public DimensionPruningList getPruningList() {
        return pruningList;
    }

    /**
     * Whether in-game maps should be skipped.
     *
     * @return true if the in-game maps should be skipped.
     */
    public boolean isSkipMaps() {
        return skipMaps;
    }

    /**
     * Whether loot tables should be skipped.
     *
     * @return true if container loot tables should be skipped.
     */
    public boolean isSkipLootTables() {
        return skipLootTables;
    }

    /**
     * Whether item conversion should be skipped.
     *
     * @return true if air should be used for item conversion.
     */
    public boolean isSkipItemConversion() {
        return skipItemConversion;
    }

    /**
     * Whether custom identifiers can be read / written.
     *
     * @return true if custom identifiers are supported.
     */
    public boolean isCustomIdentifiers() {
        return customIdentifiers;
    }

    /**
     * Whether block connections should be skipped, e.g. panes connecting to other blocks.
     * This is used when blocks need data from surrounding blocks.
     *
     * @return true if block connections should be skipped.
     */
    public boolean isSkipBlockConnections() {
        return skipBlockConnections;
    }

    /**
     * Whether compaction is enabled for LevelDB. Bedrock only.
     *
     * @return true if compaction should happen after conversion.
     */
    public boolean isEnableCompact() {
        return enableCompact;
    }

    /**
     * Whether empty chunks should be discarded instead of written.
     *
     * @return true if chunks should be removed instead of written.
     */
    public boolean isDiscardEmptyChunks() {
        return discardEmptyChunks;
    }

    /**
     * Whether biome blending should be prevented. Java only.
     *
     * @return true if biome blending should be prevented.
     */
    public boolean isPreventYBiomeBlending() {
        return preventYBiomeBlending;
    }
}
