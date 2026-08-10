package com.hivemc.chunker.mapping;

import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

import java.util.Locale;

/**
 * Custom Dimension definitions for a world.
 */
public record DimensionMapping(
        String identifier,
        int biomeHeight,
        String fallbackBiome
) {
    /**
     * Creates a registerable dimension based on the mapping
     * @return The dimension
     * @throws IllegalArgumentException if the fallback biome isn't a vanilla biome.
     */
    public Dimension toDimension(int bedrockID) {
        return new Dimension(null, bedrockID, identifier, resolveFallbackBiome(), biomeHeight);
    }

    private ChunkerBiome.ChunkerVanillaBiome resolveFallbackBiome() {
        if (fallbackBiome == null) return ChunkerBiome.ChunkerVanillaBiome.PLAINS;

        // Turn the input to lowercase to support older uppercase non minecraft: prefixed values
        String name = fallbackBiome.toLowerCase(Locale.ROOT);
        return ChunkerBiome.ChunkerVanillaBiome.find(name.contains(":") ? name : "minecraft:" + name)
                .orElseThrow(() -> new IllegalArgumentException("Unknown fallback biome \"" + fallbackBiome
                        + "\" for dimension \"" + identifier + "\""));
    }
}
