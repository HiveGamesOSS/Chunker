package com.hivemc.chunker.conversion.encoding.java.base.resolver.biome;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerCustomBiome;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Biome name resolver, used for Java 1.18+
 */
public class JavaNamedBiomeResolver implements Resolver<String, ChunkerBiome> {
    private final InvertibleMap<ChunkerBiome.ChunkerVanillaBiome, String> mapping = InvertibleMap.enumKeys(ChunkerBiome.ChunkerVanillaBiome.class);
    private final boolean customIdentifierSupported;

    /**
     * Create a new java identifier biome resolver.
     *
     * @param javaVersion               the game version being used, as certain biomes are only available after
     *                                  specific versions.
     * @param customIdentifierSupported whether custom identifiers should be passed through as
     *                                  ChunkerCustomBiome.
     */
    public JavaNamedBiomeResolver(Version javaVersion, boolean customIdentifierSupported) {
        this.customIdentifierSupported = customIdentifierSupported;

        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OCEAN, "minecraft:ocean");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.PLAINS, "minecraft:plains");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DESERT, "minecraft:desert");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FOREST, "minecraft:forest");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.TAIGA, "minecraft:taiga");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SWAMP, "minecraft:swamp");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.RIVER, "minecraft:river");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.NETHER_WASTES, "minecraft:nether_wastes");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.THE_END, "minecraft:the_end");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FROZEN_OCEAN, "minecraft:frozen_ocean");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FROZEN_RIVER, "minecraft:frozen_river");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_PLAINS, "minecraft:snowy_plains");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MUSHROOM_FIELDS, "minecraft:mushroom_fields");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BEACH, "minecraft:beach");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_HILLS, "minecraft:windswept_hills");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.JUNGLE, "minecraft:jungle");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SPARSE_JUNGLE, "minecraft:sparse_jungle");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_OCEAN, "minecraft:deep_ocean");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.STONY_SHORE, "minecraft:stony_shore");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_BEACH, "minecraft:snowy_beach");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BIRCH_FOREST, "minecraft:birch_forest");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DARK_FOREST, "minecraft:dark_forest");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_TAIGA, "minecraft:snowy_taiga");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OLD_GROWTH_PINE_TAIGA, "minecraft:old_growth_pine_taiga");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_FOREST, "minecraft:windswept_forest");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SAVANNA, "minecraft:savanna");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SAVANNA_PLATEAU, "minecraft:savanna_plateau");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BADLANDS, "minecraft:badlands");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WOODED_BADLANDS, "minecraft:wooded_badlands");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SMALL_END_ISLANDS, "minecraft:small_end_islands");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.END_MIDLANDS, "minecraft:end_midlands");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.END_HIGHLANDS, "minecraft:end_highlands");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.END_BARRENS, "minecraft:end_barrens");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WARM_OCEAN, "minecraft:warm_ocean");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.LUKEWARM_OCEAN, "minecraft:lukewarm_ocean");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.COLD_OCEAN, "minecraft:cold_ocean");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_LUKEWARM_OCEAN, "minecraft:deep_lukewarm_ocean");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_COLD_OCEAN, "minecraft:deep_cold_ocean");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_FROZEN_OCEAN, "minecraft:deep_frozen_ocean");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.THE_VOID, "minecraft:the_void");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SUNFLOWER_PLAINS, "minecraft:sunflower_plains");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_GRAVELLY_HILLS, "minecraft:windswept_gravelly_hills");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FLOWER_FOREST, "minecraft:flower_forest");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.ICE_SPIKES, "minecraft:ice_spikes");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OLD_GROWTH_BIRCH_FOREST, "minecraft:old_growth_birch_forest");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OLD_GROWTH_SPRUCE_TAIGA, "minecraft:old_growth_spruce_taiga");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_SAVANNA, "minecraft:windswept_savanna");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.ERODED_BADLANDS, "minecraft:eroded_badlands");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BAMBOO_JUNGLE, "minecraft:bamboo_jungle");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SOUL_SAND_VALLEY, "minecraft:soul_sand_valley");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.CRIMSON_FOREST, "minecraft:crimson_forest");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WARPED_FOREST, "minecraft:warped_forest");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BASALT_DELTAS, "minecraft:basalt_deltas");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DRIPSTONE_CAVES, "minecraft:dripstone_caves");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.LUSH_CAVES, "minecraft:lush_caves");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MEADOW, "minecraft:meadow");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.GROVE, "minecraft:grove");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_SLOPES, "minecraft:snowy_slopes");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FROZEN_PEAKS, "minecraft:frozen_peaks");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.JAGGED_PEAKS, "minecraft:jagged_peaks");
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.STONY_PEAKS, "minecraft:stony_peaks");

        if (javaVersion.isGreaterThanOrEqual(1, 19, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_DARK, "minecraft:deep_dark");
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.MANGROVE_SWAMP, "minecraft:mangrove_swamp");
        }

        if (javaVersion.isGreaterThanOrEqual(1, 20, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.CHERRY_GROVE, "minecraft:cherry_grove");
        }

        if (javaVersion.isGreaterThanOrEqual(1, 21, 2)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.PALE_GARDEN, "minecraft:pale_garden");
        }
    }

    @Override
    public Optional<String> from(ChunkerBiome input) {
        if (input instanceof ChunkerBiome.ChunkerVanillaBiome chunkerVanillaBiome) {
            return from(chunkerVanillaBiome);
        } else if (input instanceof ChunkerCustomBiome customIdentifierBiome) {
            if (customIdentifierSupported) {
                return Optional.ofNullable(customIdentifierBiome.getIdentifier());
            } else {
                // If they're not supported check for a fallback
                ChunkerBiome.ChunkerVanillaBiome fallback = input.getFallback();
                if (fallback != null) {
                    return from(fallback);
                } else {
                    // No possible mapping
                    return Optional.empty();
                }
            }
        } else {
            return Optional.empty(); // Not possible to find a mapping
        }
    }

    protected Optional<String> from(ChunkerBiome.ChunkerVanillaBiome ChunkerVanillaBiome) {
        // Try to map it
        String mapped = mapping.forward().get(ChunkerVanillaBiome);

        // It wasn't found, so first we should use chunkers built-in fallbacks
        if (mapped == null) {
            ChunkerBiome.ChunkerVanillaBiome fallback = ChunkerVanillaBiome.getFallback();

            // Use the fallback if it's present
            if (fallback != null) {
                mapped = mapping.forward().get(fallback);
            }
        }

        // Return the optional of the value, if it's null it was because there's no mapping
        return Optional.ofNullable(mapped);
    }

    @Override
    public Optional<ChunkerBiome> to(String input) {
        // Ensure namespace is present
        if (!input.contains(":")) {
            input = "minecraft:" + input;
        } else if (!input.startsWith("minecraft:") && customIdentifierSupported) {
            // Custom biome if it's supported
            return Optional.of(new ChunkerCustomBiome(input));
        }

        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
