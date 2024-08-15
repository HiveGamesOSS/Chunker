package com.hivemc.chunker.conversion.intermediate.column.biome.layout;

import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;

import java.util.List;
import java.util.function.Function;

/**
 * The biomes for a column, represented in various formats.
 */
public interface ChunkerBiomes {
    /**
     * Get the biomes as a top-down view [x][z] array in the size of [16][16].
     *
     * @param fallbackBiome the biome to use if no biome is currently present.
     * @return the biome array.
     */
    ChunkerBiome[] asColumn(ChunkerBiome fallbackBiome);

    /**
     * Get the biomes as clusters of 4x4 for the column.
     *
     * @param fallbackBiome the biome to use if no biome is currently present.
     * @return the array which is 1024 in length as it's a fixed size.
     */
    ChunkerBiome[] as4X4(ChunkerBiome fallbackBiome);

    /**
     * Get the biomes as a list of present chunks with a palette for each chunk.
     *
     * @return a list of the palettes.
     */
    List<Palette<ChunkerBiome>> asPalette();

    /**
     * Get the biomes as a palette of 4 x 4 block clusters.
     *
     * @param chunkYIndex the chunkY index to fetch (0 based).
     * @return a palette of the biomes.
     */
    Palette<ChunkerBiome> as4X4Palette(int chunkYIndex);

    /**
     * Remap the biomes using a specific mapping.
     *
     * @param mapping the function to use to map the biomes.
     */
    void remap(Function<ChunkerBiome, ChunkerBiome> mapping);
}
