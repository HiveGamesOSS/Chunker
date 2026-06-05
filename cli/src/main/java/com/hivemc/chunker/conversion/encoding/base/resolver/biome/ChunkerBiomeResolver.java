package com.hivemc.chunker.conversion.encoding.base.resolver.biome;

import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Collections;
import java.util.Set;

/**
 * Base implementation for a Biome resolver which is backed by a InvertibleMap.
 *
 * @param <T> the format-specific type which this resolver converts biomes between (usually String / Integer).
 */
public abstract class ChunkerBiomeResolver<T> implements Resolver<T, ChunkerBiome> {
    protected final InvertibleMap<ChunkerBiome.ChunkerVanillaBiome, T> mapping = InvertibleMap.enumKeys(ChunkerBiome.ChunkerVanillaBiome.class);

    /**
     * Get the set of vanilla biomes supported by this resolver.
     *
     * @return an unmodifiable set of the biomes supported.
     */
    public Set<ChunkerBiome.ChunkerVanillaBiome> getSupportedBiomes() {
        return Collections.unmodifiableSet(mapping.forward().keySet());
    }
}
