package com.hivemc.chunker.conversion.intermediate.column.biome.layout;

import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.ShortBasedPalette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Biomes represented as 4x4 clusters for a column, with a fixed size of 16 chunks height.
 */
public class ChunkerClusterBasedBiomes implements ChunkerBiomes {
    private final ChunkerBiome[] clusters;

    /**
     * Create a new set of biomes using a cluster array.
     *
     * @param clusters the clusters with 1024 in length.
     */
    public ChunkerClusterBasedBiomes(ChunkerBiome[] clusters) {
        this.clusters = clusters;
    }

    @Override
    public ChunkerBiome[] asColumn(ChunkerBiome fallbackBiome) {
        ChunkerBiome[] output = new ChunkerBiome[256];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                int x = j << 2;
                int z = i << 2;
                int newIndex = z << 4 | x;
                int oldIndex = i << 2 | j;

                ChunkerBiome biome = clusters[oldIndex];
                for (int k = 0; k < 4; k++) {
                    int offX = newIndex + (k << 4);
                    Arrays.fill(output, offX, offX + 4, biome);
                }
            }
        }

        return output;
    }

    @Override
    public ChunkerBiome[] as4X4(ChunkerBiome fallbackBiome) {
        return clusters;
    }

    @Override
    public List<Palette<ChunkerBiome>> asPalette() {
        // Loop through each block of biome data
        ArrayList<Palette<ChunkerBiome>> chunkPalettes = new ArrayList<>(16);

        // Loop through chunks 0-15
        for (int chunkY = 0; chunkY < 16; ++chunkY) {
            // Create palette
            ShortBasedPalette<ChunkerBiome> palette = new ShortBasedPalette<>(4, 16);

            // Convert each cluster to a per block palette form
            for (int clusterX = 0; clusterX < 4; ++clusterX) {
                for (int clusterY = 0; clusterY < 4; ++clusterY) {
                    for (int clusterZ = 0; clusterZ < 4; ++clusterZ) {
                        int x = clusterX << 2;
                        int y = clusterY << 2;
                        int z = clusterZ << 2;
                        int oldIndex = chunkY << 6 | clusterY << 4 | clusterZ << 2 | clusterX;

                        ChunkerBiome biome = oldIndex < clusters.length ? clusters[oldIndex] : null;

                        // If the biome is null, fallback to the default biome for the palette - 0
                        if (biome != null) {
                            // Add to palette
                            short paletteIndex = palette.getOrCreateKey(biome);

                            // Apply to the new palette
                            for (int localX = 0; localX < 4; localX++) {
                                for (int localY = 0; localY < 4; localY++) {
                                    for (int localZ = 0; localZ < 4; localZ++) {
                                        palette.setPaletteIndex(x + localX, y + localY, z + localZ, paletteIndex);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Add to chunk list
            chunkPalettes.add(palette);
        }

        // Return the palettes which were read
        return chunkPalettes;
    }

    @Override
    public Palette<ChunkerBiome> as4X4Palette(int chunkY) {
        ShortBasedPalette<ChunkerBiome> palette = new ShortBasedPalette<>(4, 4);

        // Use the last chunk for extending
        if (chunkY < 0) chunkY = 0;
        if (chunkY > 15) chunkY = 15;

        // Convert each cluster into a palette form
        for (int clusterX = 0; clusterX < 4; ++clusterX) {
            for (int clusterY = 0; clusterY < 4; ++clusterY) {
                for (int clusterZ = 0; clusterZ < 4; ++clusterZ) {
                    int oldIndex = chunkY << 6 | clusterY << 4 | clusterZ << 2 | clusterX;

                    ChunkerBiome biome = clusters[oldIndex];

                    // Apply to the new palette
                    palette.set(clusterX, clusterY, clusterZ, biome);
                }
            }
        }

        // Return single chunk
        return palette;
    }

    @Override
    public void remap(Function<ChunkerBiome, ChunkerBiome> mapping) {
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = mapping.apply(clusters[i]);
        }
    }
}
