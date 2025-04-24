package com.hivemc.chunker.conversion.intermediate.column.biome.layout;

import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.EmptyPalette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.ShortBasedPalette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.SingleValuePalette;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Biomes as a palette per chunk with 16x16x16 values per palette.
 */
public class ChunkerPaletteBasedBiomes implements ChunkerBiomes {
    private final List<Palette<ChunkerBiome>> chunks;

    /**
     * Create a new set of biomes based on a palette per chunk.
     *
     * @param chunks the biomes with a 16x16x16 palette per chunk.
     */
    public ChunkerPaletteBasedBiomes(List<Palette<ChunkerBiome>> chunks) {
        this.chunks = chunks;
    }

    @Override
    public ChunkerBiome[] asColumn(ChunkerBiome fallbackBiome) {
        ChunkerBiome[] output = new ChunkerBiome[256];

        // Use the first entry (chunk at 0)
        if (!chunks.isEmpty()) {
            Palette<ChunkerBiome> palette = chunks.get(0);
            for (int i = 0; i < output.length; i++) {
                // Grab the pos
                int x = i & 0xF;
                int z = (i >> 4) & 0xF;

                // Set the key to the looked up value
                output[i] = palette.get(x, 0, z, fallbackBiome);
            }
        } else {
            // Fill with the fallback if empty
            Arrays.fill(output, fallbackBiome);
        }

        return output;
    }

    @Override
    public ChunkerBiome[] as4X4(ChunkerBiome fallbackBiome) {
        ChunkerBiome[] output = new ChunkerBiome[1024];

        if (!chunks.isEmpty()) {
            // Loop through chunks 0-15
            for (int chunkY = 0; chunkY < 16; ++chunkY) {
                Palette<ChunkerBiome> palette = chunks.size() <= chunkY ? chunks.get(chunks.size() - 1) : chunks.get(chunkY);
                for (int localX = 0; localX < 4; ++localX) {
                    for (int localY = 0; localY < 4; ++localY) {
                        for (int localZ = 0; localZ < 4; ++localZ) {
                            // Use center point for each fetch
                            int x = (localX << 2) + 2;
                            int y = (localY << 2) + 2;
                            int z = (localZ << 2) + 2;

                            // Lookup the key
                            ChunkerBiome value = palette.get(x, y, z, fallbackBiome);
                            output[chunkY << 6 | localY << 4 | localZ << 2 | localX] = value;
                        }
                    }
                }
            }
        } else {
            // Fill with the fallback if empty
            Arrays.fill(output, fallbackBiome);
        }

        return output;
    }

    @Override
    public List<Palette<ChunkerBiome>> asPalette() {
        return chunks;
    }

    @Override
    public Palette<ChunkerBiome> as4X4Palette(int chunkY) {
        // If there are no chunks return an empty palette
        if (chunks.isEmpty()) {
            return EmptyPalette.instance(4);
        }

        // Use the top palette if chunkY is outside the range of chunks
        Palette<ChunkerBiome> oldPalette = chunkY < 0 || chunkY >= chunks.size() ? chunks.get(chunks.size() - 1) : chunks.get(chunkY);
        if (oldPalette.isEmpty()) {
            return EmptyPalette.instance(4);
        } else if (oldPalette.getKeyCount() == 1) {
            return new SingleValuePalette<>(4, oldPalette.getKey(0));
        } else {
            // Create palette
            ShortBasedPalette<ChunkerBiome> palette = new ShortBasedPalette<>(oldPalette.getKeyCount(), 4);

            // Look up each cluster and query the palette which exists
            for (int clusterX = 0; clusterX < 4; ++clusterX) {
                for (int clusterY = 0; clusterY < 4; ++clusterY) {
                    for (int clusterZ = 0; clusterZ < 4; ++clusterZ) {
                        // Lookup the key
                        ChunkerBiome biome = oldPalette.get((clusterX << 2) + 2, (clusterY << 2) + 2, (clusterZ << 2) + 2);

                        // Apply to the new palette
                        palette.set(clusterX, clusterY, clusterZ, biome);
                    }
                }
            }

            return palette;
        }
    }

    @Override
    public void remap(Function<ChunkerBiome, ChunkerBiome> mapping) {
        chunks.replaceAll(chunk -> chunk.map(mapping));
    }
}
