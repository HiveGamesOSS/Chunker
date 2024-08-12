package com.hivemc.chunker.conversion.intermediate.column.biome;

import org.jetbrains.annotations.Nullable;

/**
 * A type which represents a Minecraft biome.
 */
public interface ChunkerBiome {
    /**
     * A fallback if the biome is not supported on the output format.
     *
     * @return null if there is no fallback, a fallback is used when biomes are discontinued and have a suitable
     * replacement.
     */
    @Nullable
    ChunkerVanillaBiome getFallback();

    /**
     * List of vanilla biome IDs. Deprecated values indicate lossy conversion in future versions.
     */
    enum ChunkerVanillaBiome implements ChunkerBiome {
        OCEAN,
        PLAINS,
        DESERT,
        WINDSWEPT_HILLS,
        FOREST,
        TAIGA,
        SWAMP,
        RIVER,
        NETHER_WASTES,
        THE_END,
        FROZEN_OCEAN,
        FROZEN_RIVER,
        SNOWY_PLAINS,
        MUSHROOM_FIELDS,
        BEACH,
        JUNGLE,
        SPARSE_JUNGLE,
        DEEP_OCEAN,
        STONY_SHORE,
        SNOWY_BEACH,
        BIRCH_FOREST,
        DARK_FOREST,
        SNOWY_TAIGA,
        OLD_GROWTH_PINE_TAIGA,
        WINDSWEPT_FOREST,
        SAVANNA,
        SAVANNA_PLATEAU,
        BADLANDS,
        WOODED_BADLANDS,
        SMALL_END_ISLANDS,
        END_MIDLANDS,
        END_HIGHLANDS,
        END_BARRENS,
        WARM_OCEAN,
        LUKEWARM_OCEAN,
        COLD_OCEAN,
        DEEP_LUKEWARM_OCEAN,
        DEEP_COLD_OCEAN,
        DEEP_FROZEN_OCEAN,
        THE_VOID,
        SUNFLOWER_PLAINS,
        FLOWER_FOREST,
        ICE_SPIKES,
        OLD_GROWTH_BIRCH_FOREST,
        OLD_GROWTH_SPRUCE_TAIGA,
        WINDSWEPT_GRAVELLY_HILLS,
        WINDSWEPT_SAVANNA,
        ERODED_BADLANDS,
        BAMBOO_JUNGLE,
        SOUL_SAND_VALLEY,
        CRIMSON_FOREST,
        WARPED_FOREST,
        BASALT_DELTAS,
        DRIPSTONE_CAVES,
        LUSH_CAVES,
        MEADOW,
        GROVE,
        SNOWY_SLOPES,
        FROZEN_PEAKS,
        JAGGED_PEAKS,
        STONY_PEAKS,
        DEEP_DARK,
        MANGROVE_SWAMP,
        CHERRY_GROVE,

        // No longer used in newer versions
        @Deprecated
        SNOWY_MOUNTAINS(SNOWY_PLAINS),
        @Deprecated
        MUSHROOM_FIELD_SHORE(MUSHROOM_FIELDS),
        @Deprecated
        DESERT_HILLS(DESERT),
        @Deprecated
        WOODED_HILLS(FOREST),
        @Deprecated
        TAIGA_HILLS(TAIGA),
        @Deprecated
        MOUNTAIN_EDGE(WINDSWEPT_HILLS),
        @Deprecated
        JUNGLE_HILLS(JUNGLE),
        @Deprecated
        BIRCH_FOREST_HILLS(BIRCH_FOREST),
        @Deprecated
        SNOWY_TAIGA_HILLS(SNOWY_TAIGA),
        @Deprecated
        GIANT_TREE_TAIGA_HILLS(OLD_GROWTH_PINE_TAIGA),
        @Deprecated
        BADLANDS_PLATEAU(BADLANDS),
        @Deprecated
        DEEP_WARM_OCEAN(WARM_OCEAN),
        @Deprecated
        DESERT_LAKES(DESERT),
        @Deprecated
        TAIGA_MOUNTAINS(TAIGA),
        @Deprecated
        SWAMP_HILLS(SWAMP),
        @Deprecated
        MODIFIED_JUNGLE(JUNGLE),
        @Deprecated
        MODIFIED_JUNGLE_EDGE(SPARSE_JUNGLE),
        @Deprecated
        TALL_BIRCH_HILLS(OLD_GROWTH_BIRCH_FOREST),
        @Deprecated
        DARK_FOREST_HILLS(DARK_FOREST),
        @Deprecated
        SNOWY_TAIGA_MOUNTAINS(SNOWY_TAIGA),
        @Deprecated
        GIANT_SPRUCE_TAIGA_HILLS(OLD_GROWTH_SPRUCE_TAIGA),
        @Deprecated
        MODIFIED_GRAVELLY_MOUNTAINS(WINDSWEPT_GRAVELLY_HILLS),
        @Deprecated
        SHATTERED_SAVANNA_PLATEAU(WINDSWEPT_SAVANNA),
        @Deprecated
        MODIFIED_WOODED_BADLANDS_PLATEAU(WOODED_BADLANDS),
        @Deprecated
        MODIFIED_BADLANDS_PLATEAU(BADLANDS),
        @Deprecated
        BAMBOO_JUNGLE_HILLS(BAMBOO_JUNGLE),
        @Deprecated
        LEGACY_FROZEN_OCEAN(FROZEN_OCEAN),
        ;

        private final ChunkerVanillaBiome fallback;

        /**
         * Create a Vanilla biome.
         *
         * @param fallback the fallback to use if this biome isn't supported, this should be used when a biome has been
         *                 split or merged.
         */
        ChunkerVanillaBiome(ChunkerVanillaBiome fallback) {
            this.fallback = fallback;
        }

        /**
         * Create a Vanilla biome with no fallback.
         */
        ChunkerVanillaBiome() {
            this(null);
        }

        @Override
        public ChunkerVanillaBiome getFallback() {
            return fallback;
        }
    }
}
