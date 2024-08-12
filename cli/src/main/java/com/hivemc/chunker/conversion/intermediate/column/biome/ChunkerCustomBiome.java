package com.hivemc.chunker.conversion.intermediate.column.biome;

/**
 * A custom biome that isn't included in Vanilla.
 */
public class ChunkerCustomBiome implements ChunkerBiome {
    private final String identifier;

    /**
     * Create a new custom biome with a namespaced identifier.
     *
     * @param identifier the namespaced identifier.
     */
    public ChunkerCustomBiome(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get a namespaced identifier for this biome.
     *
     * @return the namespaced identifier in the format namespace:identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public ChunkerVanillaBiome getFallback() {
        return null;
    }
}
