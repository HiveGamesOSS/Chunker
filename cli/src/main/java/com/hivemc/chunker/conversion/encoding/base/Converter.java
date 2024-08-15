package com.hivemc.chunker.conversion.encoding.base;

import com.google.common.base.CaseFormat;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.mapping.resolver.MappingsFileResolvers;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A converter is an interface for converter settings / feeding information back to the user.
 */
public interface Converter {
    /**
     * Whether compaction is enabled for LevelDB. Bedrock only.
     *
     * @return true if compaction should happen after conversion.
     */
    boolean shouldLevelDBCompaction();

    /**
     * Whether the in-game maps should be processed.
     *
     * @return true if it should be processed.
     */
    boolean shouldProcessMaps();

    /**
     * Whether items should be processed otherwise air is used.
     *
     * @return true if it should be processed.
     */
    boolean shouldProcessItems();

    /**
     * Whether entities should be processed.
     *
     * @return true if it should be processed.
     */
    boolean shouldProcessEntities();

    /**
     * Whether block entities should be processed.
     *
     * @return true if it should be processed.
     */
    boolean shouldProcessBlockEntities();

    /**
     * Whether the loot tables should be processed.
     *
     * @return true if it should be processed.
     */
    boolean shouldProcessLootTables();

    /**
     * Whether biomes should be processed.
     *
     * @return true if it should be processed.
     */
    boolean shouldProcessBiomes();

    /**
     * Whether the height-map should be processed.
     *
     * @return true if it should be processed.
     */
    boolean shouldProcessHeightMap();

    /**
     * Whether pre-transform should be run. Pre-Transform waits for required neighbouring columns before processing
     * them to infer missing information.
     *
     * @return true if pre-transform is enabled
     */
    boolean shouldProcessColumnPreTransform();

    /**
     * Whether lighting should be processed.
     *
     * @return true if it should be processed.
     */
    boolean shouldProcessLighting();

    /**
     * Whether a dimension should be processed.
     *
     * @param dimension the dimension.
     * @return true if the dimension should be processed.
     */
    boolean shouldProcessDimension(Dimension dimension);

    /**
     * Whether a region inside a world should be processed.
     *
     * @param dimension  the dimension.
     * @param regionPair the co-ordinates of the region.
     * @return true if the region should be processed.
     */
    boolean shouldProcessRegion(Dimension dimension, RegionCoordPair regionPair);

    /**
     * Whether a column inside a world should be processed.
     *
     * @param dimension  the dimension.
     * @param columnPair the co-ordinates of the column.
     * @return true if the column should be processed.
     */
    boolean shouldProcessColumn(Dimension dimension, ChunkCoordPair columnPair);

    /**
     * Whether NBT is allowed to be copied from the input to output (must be same format and version).
     *
     * @return true if copying is allowed.
     */
    boolean shouldAllowNBTCopying();

    /**
     * Whether custom identifiers should be processed.
     *
     * @return true if custom identifiers should be used.
     */
    boolean shouldAllowCustomIdentifiers();

    /**
     * Get the block / item user mappings.
     *
     * @return the mappings or null if none present.
     */
    @Nullable
    MappingsFileResolvers getBlockMappings();

    /**
     * Log a non-fatal exception.
     *
     * @param throwable the exception.
     */
    default void logNonFatalException(Throwable throwable) {
        throwable.printStackTrace();
    }

    /**
     * Log a missing mapping.
     *
     * @param type       the type of the missing mapping.
     * @param identifier the identifier as a string (can be a number, can be any format).
     */
    default void logMissingMapping(MissingMappingType type, String identifier) {
        System.err.println("Missing " + type.getName().replace('_', ' ') + " mapping for " + identifier);
    }

    /**
     * Whether empty chunks should be discarded instead of written.
     *
     * @return true if chunks should be removed instead of written.
     */
    boolean shouldDiscardEmptyChunks();

    /**
     * Whether biome blending should be prevented. Java only.
     *
     * @return true if biome blending should be prevented.
     */
    boolean shouldPreventYBiomeBlending();

    /**
     * Get the dimension mapping given an input.
     *
     * @param dimension the input dimension.
     * @return the new dimension or absent if the dimension should be removed.
     */
    Optional<Dimension> getNewDimension(Dimension dimension);

    /**
     * Get the main level data.
     *
     * @return the level or empty if the level hasn't been processed yet.
     */
    Optional<ChunkerLevel> level();

    /**
     * The type of missing mapping.
     */
    enum MissingMappingType {
        BLOCK,
        ITEM,
        POTION,
        EFFECT,
        BIOME,
        ENTITY,
        ENTITY_TYPE,
        BLOCK_ENTITY,
        HORN,
        PAINTING,
        ENCHANTMENT,
        TRIM_MATERIAL,
        TRIM_PATTERN;

        private final String name;

        /**
         * Create a new missing mapping type.
         */
        MissingMappingType() {
            name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name());
        }

        /**
         * Get the name of the missing mapping in lower underscore case.
         *
         * @return the name.
         */
        public String getName() {
            return name;
        }
    }
}
