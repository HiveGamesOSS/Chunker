package com.hivemc.chunker.conversion.encoding.base;

import com.hivemc.chunker.conversion.encoding.EncodingType;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;

import java.util.Set;

/**
 * An interface that represents either a LevelReader or a LevelWriter.
 */
public interface LevelReaderWriter {
    /**
     * Check if this class is a reader.
     *
     * @return true if this class is a reader.
     */
    boolean isReader();

    /**
     * Get the encoding type that this level reader/writer uses.
     *
     * @return the encoding type.
     */
    EncodingType getEncodingType();

    /**
     * Get the version handled by this reader / writer.
     *
     * @return the version of the game.
     */
    Version getVersion();

    /**
     * Get the vanilla biomes that this format supports.
     *
     * @return the set of supported biomes, or an empty set if the format supports no biomes.
     */
    Set<ChunkerBiome.ChunkerVanillaBiome> getSupportedBiomes();

    /**
     * Called when the conversion is complete, whether it error'd or not.
     */
    default void free() throws Exception {
        // By default, does nothing, useful for closing resources.
    }
}
