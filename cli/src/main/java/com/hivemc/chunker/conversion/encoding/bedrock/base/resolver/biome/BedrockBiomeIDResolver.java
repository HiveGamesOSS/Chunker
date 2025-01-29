package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.biome;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Biome ID resolver, used for Bedrock
 */
@SuppressWarnings("deprecation")
public class BedrockBiomeIDResolver implements Resolver<Integer, ChunkerBiome> {
    private final InvertibleMap<ChunkerBiome.ChunkerVanillaBiome, Integer> mapping = InvertibleMap.enumKeys(ChunkerBiome.ChunkerVanillaBiome.class);

    /**
     * Create a new bedrock ID biome resolver.
     *
     * @param bedrockVersion the game version being used, as certain biomes are only available after specific versions.
     */
    public BedrockBiomeIDResolver(Version bedrockVersion) {
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OCEAN, 0);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.PLAINS, 1);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DESERT, 2);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_HILLS, 3); // extreme_hills
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FOREST, 4);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.TAIGA, 5);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SWAMP, 6); // swampland
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.RIVER, 7);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.NETHER_WASTES, 8); // hell
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.THE_END, 9);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.LEGACY_FROZEN_OCEAN, 10);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FROZEN_RIVER, 11);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_PLAINS, 12); // ice_plains
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_MOUNTAINS, 13); // ice_mountains
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MUSHROOM_FIELDS, 14); // mushroom_island
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MUSHROOM_FIELD_SHORE, 15); // mushroom_island_shore
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BEACH, 16);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DESERT_HILLS, 17);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WOODED_HILLS, 18); // forest_hills
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.TAIGA_HILLS, 19); // taiga_hills
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MOUNTAIN_EDGE, 20); // extreme_hills_edge
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.JUNGLE, 21);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.JUNGLE_HILLS, 22);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SPARSE_JUNGLE, 23); // jungle_edge
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_OCEAN, 24);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.STONY_SHORE, 25); // stone_beach
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_BEACH, 26); // cold_beach
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BIRCH_FOREST, 27); // birch_forest
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BIRCH_FOREST_HILLS, 28); // birch_forest_hills
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DARK_FOREST, 29); // roofed_forest
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_TAIGA, 30); // cold_taiga
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_TAIGA_HILLS, 31); // cold_taiga_hills
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OLD_GROWTH_PINE_TAIGA, 32); // mega_taiga
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.GIANT_TREE_TAIGA_HILLS, 33); // mega_taiga_hills
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_FOREST, 34); // extreme_hills_plus_trees
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SAVANNA, 35);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SAVANNA_PLATEAU, 36);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BADLANDS, 37); // mesa
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WOODED_BADLANDS, 38); // mesa_plateau_stone
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BADLANDS_PLATEAU, 39); // mesa_plateau

        if (bedrockVersion.isGreaterThanOrEqual(1, 4, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.WARM_OCEAN, 40);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_WARM_OCEAN, 41);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.LUKEWARM_OCEAN, 42);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_LUKEWARM_OCEAN, 43);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.COLD_OCEAN, 44);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_COLD_OCEAN, 45);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.FROZEN_OCEAN, 46);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_FROZEN_OCEAN, 47);
        }

        if (bedrockVersion.isGreaterThanOrEqual(1, 9, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.BAMBOO_JUNGLE, 48);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.BAMBOO_JUNGLE_HILLS, 49);
        }

        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SUNFLOWER_PLAINS, 129);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DESERT_LAKES, 130); // desert_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_GRAVELLY_HILLS, 131); // extreme_hills_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FLOWER_FOREST, 132);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.TAIGA_MOUNTAINS, 133); // taiga_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SWAMP_HILLS, 134); // swampland_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.ICE_SPIKES, 140); // ice_plains_spikes
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MODIFIED_JUNGLE, 149); // jungle_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MODIFIED_JUNGLE_EDGE, 151); // jungle_edge_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OLD_GROWTH_BIRCH_FOREST, 155); // birch_forest_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.TALL_BIRCH_HILLS, 156); // birch_forest_hills_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DARK_FOREST_HILLS, 157); // roofed_forest_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_TAIGA_MOUNTAINS, 158); // cold_taiga_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OLD_GROWTH_SPRUCE_TAIGA, 160); // redwood_taiga_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.GIANT_SPRUCE_TAIGA_HILLS, 161); // redwood_taiga_hills_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MODIFIED_GRAVELLY_MOUNTAINS, 162); // extreme_hills_plus_trees_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_SAVANNA, 163); // savanna_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SHATTERED_SAVANNA_PLATEAU, 164); // savanna_plateau_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.ERODED_BADLANDS, 165); // mesa_bryce
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MODIFIED_WOODED_BADLANDS_PLATEAU, 166); // mesa_plateau_stone_mutated
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MODIFIED_BADLANDS_PLATEAU, 167); // mesa_plateau_mutated

        // Nether biomes
        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.SOUL_SAND_VALLEY, 178);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.CRIMSON_FOREST, 179);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.WARPED_FOREST, 180);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.BASALT_DELTAS, 181);
        }

        if (bedrockVersion.isGreaterThanOrEqual(1, 18, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.JAGGED_PEAKS, 182);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.FROZEN_PEAKS, 183);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_SLOPES, 184);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.GROVE, 185);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.MEADOW, 186);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.LUSH_CAVES, 187);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DRIPSTONE_CAVES, 188);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.STONY_PEAKS, 189);
        }

        if (bedrockVersion.isGreaterThanOrEqual(1, 19, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_DARK, 190);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.MANGROVE_SWAMP, 191);
        }

        if (bedrockVersion.isGreaterThanOrEqual(1, 20, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.CHERRY_GROVE, 192);
        }

        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 50)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.PALE_GARDEN, 193);
        }
    }

    @Override
    public Optional<Integer> from(ChunkerBiome input) {
        // Custom biomes aren't supported in this version since they are IDs
        ChunkerBiome.ChunkerVanillaBiome chunkerVanillaBiome;

        // Resolve it to a vanilla biome
        if (input instanceof ChunkerBiome.ChunkerVanillaBiome biome) {
            chunkerVanillaBiome = biome;
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

        // Try to map it
        Integer mapped = mapping.forward().get(chunkerVanillaBiome);

        // It wasn't found, so first we should use the built-in fallbacks
        if (mapped == null) {
            ChunkerBiome.ChunkerVanillaBiome fallback = chunkerVanillaBiome.getFallback();

            // Use the fallback if it's present
            if (fallback != null) {
                mapped = mapping.forward().get(fallback);
            }
        }

        // Return the optional of the value, if it's null it was because there's no mapping
        return Optional.ofNullable(mapped);
    }

    @Override
    public Optional<ChunkerBiome> to(Integer input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
