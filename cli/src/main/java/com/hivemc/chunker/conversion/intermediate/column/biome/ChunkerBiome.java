package com.hivemc.chunker.conversion.intermediate.column.biome;

import com.hivemc.chunker.util.InvertibleMap;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
        OCEAN("minecraft:ocean"),
        PLAINS("minecraft:plains"),
        DESERT("minecraft:desert"),
        WINDSWEPT_HILLS("minecraft:windswept_hills", "minecraft:extreme_hills"),
        FOREST("minecraft:forest"),
        TAIGA("minecraft:taiga"),
        SWAMP("minecraft:swamp", "minecraft:swampland"),
        RIVER("minecraft:river"),
        NETHER_WASTES("minecraft:nether_wastes", "minecraft:hell"),
        THE_END("minecraft:the_end"),
        FROZEN_OCEAN("minecraft:frozen_ocean"),
        FROZEN_RIVER("minecraft:frozen_river"),
        SNOWY_PLAINS("minecraft:snowy_plains", "minecraft:ice_plains"),
        MUSHROOM_FIELDS("minecraft:mushroom_fields", "minecraft:mushroom_island"),
        BEACH("minecraft:beach"),
        JUNGLE("minecraft:jungle"),
        SPARSE_JUNGLE("minecraft:sparse_jungle", "minecraft:jungle_edge"),
        DEEP_OCEAN("minecraft:deep_ocean"),
        STONY_SHORE("minecraft:stony_shore", "minecraft:stone_beach"),
        SNOWY_BEACH("minecraft:snowy_beach", "minecraft:cold_beach"),
        BIRCH_FOREST("minecraft:birch_forest"),
        DARK_FOREST("minecraft:dark_forest", "minecraft:roofed_forest"),
        SNOWY_TAIGA("minecraft:snowy_taiga", "minecraft:cold_taiga"),
        OLD_GROWTH_PINE_TAIGA("minecraft:old_growth_pine_taiga", "minecraft:mega_taiga"),
        WINDSWEPT_FOREST("minecraft:windswept_forest", "minecraft:extreme_hills_plus_trees"),
        SAVANNA("minecraft:savanna"),
        SAVANNA_PLATEAU("minecraft:savanna_plateau"),
        BADLANDS("minecraft:badlands", "minecraft:mesa"),
        WOODED_BADLANDS("minecraft:wooded_badlands", "minecraft:mesa_plateau_stone"),
        SMALL_END_ISLANDS("minecraft:small_end_islands"),
        END_MIDLANDS("minecraft:end_midlands"),
        END_HIGHLANDS("minecraft:end_highlands"),
        END_BARRENS("minecraft:end_barrens"),
        WARM_OCEAN("minecraft:warm_ocean"),
        LUKEWARM_OCEAN("minecraft:lukewarm_ocean"),
        COLD_OCEAN("minecraft:cold_ocean"),
        DEEP_LUKEWARM_OCEAN("minecraft:deep_lukewarm_ocean"),
        DEEP_COLD_OCEAN("minecraft:deep_cold_ocean"),
        DEEP_FROZEN_OCEAN("minecraft:deep_frozen_ocean"),
        THE_VOID("minecraft:the_void"),
        SUNFLOWER_PLAINS("minecraft:sunflower_plains"),
        FLOWER_FOREST("minecraft:flower_forest"),
        ICE_SPIKES("minecraft:ice_spikes", "minecraft:ice_plains_spikes"),
        OLD_GROWTH_BIRCH_FOREST("minecraft:old_growth_birch_forest", "minecraft:birch_forest_mutated"),
        OLD_GROWTH_SPRUCE_TAIGA("minecraft:old_growth_spruce_taiga", "minecraft:redwood_taiga_mutated"),
        WINDSWEPT_GRAVELLY_HILLS("minecraft:windswept_gravelly_hills", "minecraft:extreme_hills_mutated"),
        WINDSWEPT_SAVANNA("minecraft:windswept_savanna", "minecraft:savanna_mutated"),
        ERODED_BADLANDS("minecraft:eroded_badlands", "minecraft:mesa_bryce"),
        BAMBOO_JUNGLE("minecraft:bamboo_jungle"),
        SOUL_SAND_VALLEY("minecraft:soul_sand_valley", "minecraft:soulsand_valley"),
        CRIMSON_FOREST("minecraft:crimson_forest"),
        WARPED_FOREST("minecraft:warped_forest"),
        BASALT_DELTAS("minecraft:basalt_deltas"),
        DRIPSTONE_CAVES("minecraft:dripstone_caves"),
        LUSH_CAVES("minecraft:lush_caves"),
        MEADOW("minecraft:meadow"),
        GROVE("minecraft:grove"),
        SNOWY_SLOPES("minecraft:snowy_slopes"),
        FROZEN_PEAKS("minecraft:frozen_peaks"),
        JAGGED_PEAKS("minecraft:jagged_peaks"),
        STONY_PEAKS("minecraft:stony_peaks"),
        DEEP_DARK("minecraft:deep_dark"),
        MANGROVE_SWAMP("minecraft:mangrove_swamp"),
        CHERRY_GROVE("minecraft:cherry_grove"),
        PALE_GARDEN("minecraft:pale_garden"),
        SULFUR_CAVES("minecraft:sulfur_caves"),

        // No longer used in newer versions
        @Deprecated
        SNOWY_MOUNTAINS(SNOWY_PLAINS, "minecraft:snowy_mountains", "minecraft:ice_mountains"),
        @Deprecated
        MUSHROOM_FIELD_SHORE(MUSHROOM_FIELDS, "minecraft:mushroom_field_shore", "minecraft:mushroom_island_shore"),
        @Deprecated
        DESERT_HILLS(DESERT, "minecraft:desert_hills", "minecraft:desert_hills"),
        @Deprecated
        WOODED_HILLS(FOREST, "minecraft:wooded_hills", "minecraft:forest_hills"),
        @Deprecated
        TAIGA_HILLS(TAIGA, "minecraft:taiga_hills", "minecraft:taiga_hills"),
        @Deprecated
        MOUNTAIN_EDGE(WINDSWEPT_HILLS, "minecraft:mountain_edge", "minecraft:extreme_hills_edge"),
        @Deprecated
        JUNGLE_HILLS(JUNGLE, "minecraft:jungle_hills", "minecraft:jungle_hills"),
        @Deprecated
        BIRCH_FOREST_HILLS(BIRCH_FOREST, "minecraft:birch_forest_hills", "minecraft:birch_forest_hills"),
        @Deprecated
        SNOWY_TAIGA_HILLS(SNOWY_TAIGA, "minecraft:snowy_taiga_hills", "minecraft:cold_taiga_hills"),
        @Deprecated
        GIANT_TREE_TAIGA_HILLS(OLD_GROWTH_PINE_TAIGA, "minecraft:giant_tree_taiga_hills", "minecraft:mega_taiga_hills"),
        @Deprecated
        BADLANDS_PLATEAU(BADLANDS, "minecraft:badlands_plateau", "minecraft:mesa_plateau"),
        @Deprecated
        DEEP_WARM_OCEAN(WARM_OCEAN, "minecraft:deep_warm_ocean", "minecraft:deep_warm_ocean"),
        @Deprecated
        DESERT_LAKES(DESERT, "minecraft:desert_lakes", "minecraft:desert_mutated"),
        @Deprecated
        TAIGA_MOUNTAINS(TAIGA, "minecraft:taiga_mountains", "minecraft:taiga_mutated"),
        @Deprecated
        SWAMP_HILLS(SWAMP, "minecraft:swamp_hills", "minecraft:swampland_mutated"),
        @Deprecated
        MODIFIED_JUNGLE(JUNGLE, "minecraft:modified_jungle", "minecraft:jungle_mutated"),
        @Deprecated
        MODIFIED_JUNGLE_EDGE(SPARSE_JUNGLE, "minecraft:modified_jungle_edge", "minecraft:jungle_edge_mutated"),
        @Deprecated
        TALL_BIRCH_HILLS(OLD_GROWTH_BIRCH_FOREST, "minecraft:tall_birch_hills", "minecraft:birch_forest_hills_mutated"),
        @Deprecated
        DARK_FOREST_HILLS(DARK_FOREST, "minecraft:dark_forest_hills", "minecraft:roofed_forest_mutated"),
        @Deprecated
        SNOWY_TAIGA_MOUNTAINS(SNOWY_TAIGA, "minecraft:snowy_taiga_mountains", "minecraft:cold_taiga_mutated"),
        @Deprecated
        GIANT_SPRUCE_TAIGA_HILLS(OLD_GROWTH_SPRUCE_TAIGA, "minecraft:giant_spruce_taiga_hills", "minecraft:redwood_taiga_hills_mutated"),
        @Deprecated
        MODIFIED_GRAVELLY_MOUNTAINS(WINDSWEPT_GRAVELLY_HILLS, "minecraft:modified_gravelly_mountains", "minecraft:extreme_hills_plus_trees_mutated"),
        @Deprecated
        SHATTERED_SAVANNA_PLATEAU(WINDSWEPT_SAVANNA, "minecraft:shattered_savanna_plateau", "minecraft:savanna_plateau_mutated"),
        @Deprecated
        MODIFIED_WOODED_BADLANDS_PLATEAU(WOODED_BADLANDS, "minecraft:modified_wooded_badlands_plateau", "minecraft:mesa_plateau_stone_mutated"),
        @Deprecated
        MODIFIED_BADLANDS_PLATEAU(BADLANDS, "minecraft:modified_badlands_plateau", "minecraft:mesa_plateau_mutated"),
        @Deprecated
        BAMBOO_JUNGLE_HILLS(BAMBOO_JUNGLE, "minecraft:bamboo_jungle_hills", "minecraft:bamboo_jungle_hills"),
        @Deprecated
        LEGACY_FROZEN_OCEAN(FROZEN_OCEAN, null, "minecraft:legacy_frozen_ocean"),
        ;

        // Mapping of identifiers <-> ChunkerVanillaBiome (prioritizing the Java name but with aliases for the Bedrock name)
        private static final InvertibleMap<String, ChunkerVanillaBiome> mapping = InvertibleMap.create();

        static {
            for (ChunkerVanillaBiome biome : values()) {
                if (biome.javaIdentifier != null) {
                    mapping.put(biome.javaIdentifier, biome);
                }
                if (biome.bedrockIdentifier != null && !biome.bedrockIdentifier.equals(biome.javaIdentifier)) {
                    mapping.put(biome.bedrockIdentifier, biome);
                }
            }
        }

        /**
         * Find vanilla biome by name
         *
         * @param value The string name of the biome
         * @return Biome if found otherwise empty optional
         */
        public static Optional<ChunkerVanillaBiome> find(String value) {
            return Optional.ofNullable(mapping.forward().get(value));
        }

        /**
         * Get the Java-style string identifier for the vanilla biome.
         *
         * @return the Java identifier, or empty if this biome has no Java identifier.
         */
        public Optional<String> getJavaIdentifier() {
            return Optional.ofNullable(javaIdentifier);
        }

        /**
         * Get the Bedrock-style string identifier for the vanilla biome.
         *
         * @return the Bedrock identifier, or empty if this biome has no Bedrock identifier.
         */
        public Optional<String> getBedrockIdentifier() {
            return Optional.ofNullable(bedrockIdentifier);
        }

        private final ChunkerVanillaBiome fallback;
        private final String javaIdentifier;
        private final String bedrockIdentifier;

        /**
         * Create a Vanilla biome where the Java and Bedrock identifiers are the same.
         *
         * @param identifier the identifier used by both Java and Bedrock.
         */
        ChunkerVanillaBiome(String identifier) {
            this(null, identifier, identifier);
        }

        /**
         * Create a Vanilla biome with separate Java and Bedrock identifiers.
         *
         * @param javaIdentifier    the Java identifier, or null if this biome has no Java identifier.
         * @param bedrockIdentifier the Bedrock identifier, or null if this biome has no Bedrock identifier.
         */
        ChunkerVanillaBiome(@Nullable String javaIdentifier, @Nullable String bedrockIdentifier) {
            this(null, javaIdentifier, bedrockIdentifier);
        }

        /**
         * Create a Vanilla biome with a fallback and separate Java and Bedrock identifiers.
         *
         * @param fallback          the fallback to use if this biome isn't supported, this should be used when a biome
         *                          has been split or merged.
         * @param javaIdentifier    the Java identifier, or null if this biome has no Java identifier.
         * @param bedrockIdentifier the Bedrock identifier, or null if this biome has no Bedrock identifier.
         */
        ChunkerVanillaBiome(ChunkerVanillaBiome fallback, @Nullable String javaIdentifier, @Nullable String bedrockIdentifier) {
            this.fallback = fallback;
            this.javaIdentifier = javaIdentifier;
            this.bedrockIdentifier = bedrockIdentifier;
        }

        @Override
        public ChunkerVanillaBiome getFallback() {
            return fallback;
        }
    }
}
