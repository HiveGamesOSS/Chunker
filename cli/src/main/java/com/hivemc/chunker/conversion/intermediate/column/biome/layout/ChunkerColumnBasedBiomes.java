package com.hivemc.chunker.conversion.intermediate.column.biome.layout;

import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.ShortBasedPalette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.WriteablePalette;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 16x16 column based biomes.
 */
public class ChunkerColumnBasedBiomes implements ChunkerBiomes {
    private final ChunkerBiome[] columns;

    /**
     * Create a new column based biomes holder.
     *
     * @param columns the array which should be 256 in length.
     */
    public ChunkerColumnBasedBiomes(ChunkerBiome[] columns) {
        this.columns = columns;
    }

    @Override
    public ChunkerBiome[] asColumn(ChunkerBiome fallbackBiome) {
        return columns;
    }

    @Override
    public ChunkerBiome[] as4X4(ChunkerBiome fallbackBiome) {
        ChunkerBiome[] output = new ChunkerBiome[1024];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                int x = (j << 2) + 2;
                int z = (i << 2) + 2;
                int oldIndex = (z << 4 | x);
                output[i << 2 | j] = columns[oldIndex];
            }
        }

        // Copy to new y layers
        for (int i = 1; i < 64; ++i) {
            System.arraycopy(output, 0, output, i << 4, 16);
        }

        return output;
    }

    @Override
    public List<Palette<ChunkerBiome>> asPalette() {
        // Loop through each block of biome data
        // Convert the 256 values into XYZ
        WriteablePalette<ChunkerBiome> palette = new ShortBasedPalette<>(4, 16);
        for (int i = 0; i < columns.length; i++) {
            int x = i & 0xF;
            int z = (i >> 4) & 0xF;
            ChunkerBiome entry = columns[i];

            // Add to palette
            short paletteIndex = palette.getOrCreateKey(entry);

            // Set values
            for (int y = 0; y < 16; y++) {
                palette.setPaletteIndex(x, y, z, paletteIndex);
            }
        }

        // Return single chunk
        return new ArrayList<>(List.of(palette));
    }

    @Override
    public Palette<ChunkerBiome> as4X4Palette(int chunkY) {
        // Loop through each block of biome data
        // Convert the 256 values into XYZ chunks
        WriteablePalette<ChunkerBiome> palette = new ShortBasedPalette<>(4, 4);
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                int x = (j << 2) + 2;
                int z = (i << 2) + 2;
                int oldIndex = (z << 4 | x);

                // Add to palette
                ChunkerBiome entry = columns[oldIndex];
                short paletteIndex = palette.getOrCreateKey(entry);

                // Apply to 4 Y values
                for (int k = 0; k < 4; ++k) {
                    palette.setPaletteIndex(j, k, i, paletteIndex);
                }
            }
        }

        // Return single chunk
        return palette;
    }

    @Override
    public void remap(Function<ChunkerBiome, ChunkerBiome> mapping) {
        for (int i = 0; i < columns.length; i++) {
            columns[i] = mapping.apply(columns[i]);
        }
    }
}
