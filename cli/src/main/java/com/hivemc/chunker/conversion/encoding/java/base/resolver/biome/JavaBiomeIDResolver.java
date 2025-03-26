package com.hivemc.chunker.conversion.encoding.java.base.resolver.biome;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Biome ID resolver, used for Java less than 1.18
 */
@SuppressWarnings("deprecation")
public class JavaBiomeIDResolver implements Resolver<Integer, ChunkerBiome> {
    private final InvertibleMap<ChunkerBiome.ChunkerVanillaBiome, Integer> mapping = InvertibleMap.enumKeys(ChunkerBiome.ChunkerVanillaBiome.class);

    /**
     * Create a new java ID biome resolver.
     *
     * @param javaVersion the game version being used, as certain biomes are only available after specific versions.
     */
    public JavaBiomeIDResolver(Version javaVersion) {
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OCEAN, 0);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.PLAINS, 1);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DESERT, 2);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_HILLS, 3); // Previously known as mountains below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FOREST, 4);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.TAIGA, 5);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SWAMP, 6);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.RIVER, 7);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.NETHER_WASTES, 8);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.THE_END, 9);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FROZEN_OCEAN, 10);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FROZEN_RIVER, 11);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_PLAINS, 12); // Previously known as snowy_tundra below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_MOUNTAINS, 13); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MUSHROOM_FIELDS, 14);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MUSHROOM_FIELD_SHORE, 15); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BEACH, 16);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DESERT_HILLS, 17); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WOODED_HILLS, 18); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.TAIGA_HILLS, 19); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MOUNTAIN_EDGE, 20); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.JUNGLE, 21);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.JUNGLE_HILLS, 22); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SPARSE_JUNGLE, 23); // Previously known as jungle_edge below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_OCEAN, 24);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.STONY_SHORE, 25); // Previously known as stone_shore below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_BEACH, 26);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BIRCH_FOREST, 27);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BIRCH_FOREST_HILLS, 28); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DARK_FOREST, 29);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_TAIGA, 30);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_TAIGA_HILLS, 31); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OLD_GROWTH_PINE_TAIGA, 32); // Previously known as giant_tree_taiga below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.GIANT_TREE_TAIGA_HILLS, 33); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_FOREST, 34); // Previously known as wooded_mountains below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SAVANNA, 35);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SAVANNA_PLATEAU, 36);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BADLANDS, 37);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WOODED_BADLANDS, 38); // Previously known as wooded_badlands_plateau below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.BADLANDS_PLATEAU, 39); // Discontinued 1.18

        if (javaVersion.isGreaterThanOrEqual(1, 13, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.SMALL_END_ISLANDS, 40);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.END_MIDLANDS, 41);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.END_HIGHLANDS, 42);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.END_BARRENS, 43);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.WARM_OCEAN, 44);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.LUKEWARM_OCEAN, 45);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.COLD_OCEAN, 46);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_WARM_OCEAN, 47); // Discontinued 1.18
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_LUKEWARM_OCEAN, 48);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_COLD_OCEAN, 49);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DEEP_FROZEN_OCEAN, 50);
        }

        if (javaVersion.isGreaterThanOrEqual(1, 9, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.THE_VOID, 127); // Note: Bedrock doesn't have the void
        }

        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SUNFLOWER_PLAINS, 129);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DESERT_LAKES, 130); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_GRAVELLY_HILLS, 131); // Previously known as gravelly_mountains below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.FLOWER_FOREST, 132);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.TAIGA_MOUNTAINS, 133); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SWAMP_HILLS, 134); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.ICE_SPIKES, 140);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MODIFIED_JUNGLE, 149); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MODIFIED_JUNGLE_EDGE, 151); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OLD_GROWTH_BIRCH_FOREST, 155); // Previously known as tall_birch_forest below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.TALL_BIRCH_HILLS, 156); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.DARK_FOREST_HILLS, 157); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_TAIGA_MOUNTAINS, 158); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.OLD_GROWTH_SPRUCE_TAIGA, 160); // Previously known as giant_spruce_taiga below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.GIANT_SPRUCE_TAIGA_HILLS, 161); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MODIFIED_GRAVELLY_MOUNTAINS, 162); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.WINDSWEPT_SAVANNA, 163); // Previously known as shattered_savanna below 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.SHATTERED_SAVANNA_PLATEAU, 164); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.ERODED_BADLANDS, 165);
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MODIFIED_WOODED_BADLANDS_PLATEAU, 166); // Discontinued 1.18
        mapping.put(ChunkerBiome.ChunkerVanillaBiome.MODIFIED_BADLANDS_PLATEAU, 167); // Discontinued 1.18

        if (javaVersion.isGreaterThanOrEqual(1, 14, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.BAMBOO_JUNGLE, 168);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.BAMBOO_JUNGLE_HILLS, 169); // Discontinued 1.18
        }

        // Nether biomes
        if (javaVersion.isGreaterThanOrEqual(1, 16, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.SOUL_SAND_VALLEY, 170);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.CRIMSON_FOREST, 171);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.WARPED_FOREST, 172);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.BASALT_DELTAS, 173);
        }

        // Caves
        if (javaVersion.isGreaterThanOrEqual(1, 17, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.DRIPSTONE_CAVES, 174);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.LUSH_CAVES, 175);
        }

        // Caves
        if (javaVersion.isGreaterThanOrEqual(1, 18, 0)) {
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.MEADOW, 177);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.GROVE, 178);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.SNOWY_SLOPES, 179);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.FROZEN_PEAKS, 180);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.JAGGED_PEAKS, 181);
            mapping.put(ChunkerBiome.ChunkerVanillaBiome.STONY_PEAKS, 182);
        }
    }

    @Override
    public Optional<Integer> from(ChunkerBiome input) {
        // Custom biomes aren't supported in this version since they are IDs
        ChunkerBiome.ChunkerVanillaBiome chunkerVanillaBiome = null;

        // Resolve it to a vanilla biome
        if (input instanceof ChunkerBiome.ChunkerVanillaBiome biome) {
            chunkerVanillaBiome = biome;
        } else if (input != null) {
            // Custom identifiers aren't supported, so use the fallback
            chunkerVanillaBiome = input.getFallback();
        }

        // First null check
        if (chunkerVanillaBiome == null) {
            return Optional.empty(); // Unable to resolve a vanilla biome
        }

        // Try to map it
        Integer mapped = mapping.forward().get(chunkerVanillaBiome);

        // It wasn't found, so first we should use chunkers built-in fallbacks
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
