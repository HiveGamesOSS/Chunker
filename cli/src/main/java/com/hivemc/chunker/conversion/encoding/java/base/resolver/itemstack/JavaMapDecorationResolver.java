package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.map.ChunkerExplorerMap;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Map Decoration resolver, used for Java. This is the decoration which points at the structure a filled map was made
 * for, which is how the explorer maps are told apart before 26.3 gave each of them their own item.
 */
public class JavaMapDecorationResolver implements Resolver<String, ChunkerExplorerMap> {
    private final InvertibleMap<ChunkerExplorerMap, String> mapping = InvertibleMap.enumKeys(ChunkerExplorerMap.class);

    /**
     * Create a new java map decoration resolver.
     *
     * @param version the game version being used, as certain decorations are only available after specific versions.
     */
    public JavaMapDecorationResolver(Version version) {
        // 1.20.5 moved the decorations to a component with named types
        if (version.isGreaterThanOrEqual(1, 20, 5)) {
            mapping.put(ChunkerExplorerMap.BURIED_TREASURE, "minecraft:red_x");
            mapping.put(ChunkerExplorerMap.DESERT_VILLAGE, "minecraft:village_desert");
            mapping.put(ChunkerExplorerMap.JUNGLE_PYRAMID, "minecraft:jungle_temple");
            mapping.put(ChunkerExplorerMap.OCEAN_MONUMENT, "minecraft:monument");
            mapping.put(ChunkerExplorerMap.PLAINS_VILLAGE, "minecraft:village_plains");
            mapping.put(ChunkerExplorerMap.SAVANNA_VILLAGE, "minecraft:village_savanna");
            mapping.put(ChunkerExplorerMap.SNOWY_VILLAGE, "minecraft:village_snowy");
            mapping.put(ChunkerExplorerMap.SWAMP_HUT, "minecraft:swamp_hut");
            mapping.put(ChunkerExplorerMap.TAIGA_VILLAGE, "minecraft:village_taiga");
            mapping.put(ChunkerExplorerMap.WOODLAND_MANSION, "minecraft:mansion");
        }

        // 1.21
        if (version.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(ChunkerExplorerMap.BURIED_TRIAL_CHAMBERS, "minecraft:trial_chambers");
        }

        // 26.3
        if (version.isGreaterThanOrEqual(26, 3, 0)) {
            mapping.put(ChunkerExplorerMap.ABANDONED_CAMP, "minecraft:abandoned_camp");
            mapping.put(ChunkerExplorerMap.BURIED_ANCIENT_CITY, "minecraft:ancient_city");
            mapping.put(ChunkerExplorerMap.BURIED_MINESHAFT, "minecraft:mineshaft");
            mapping.put(ChunkerExplorerMap.DESERT_PYRAMID, "minecraft:desert_pyramid");
            mapping.put(ChunkerExplorerMap.WARM_OCEAN_RUINS, "minecraft:ocean_ruin_warm");
        }
    }

    @Override
    public Optional<String> from(ChunkerExplorerMap input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerExplorerMap> to(String input) {
        if (!input.contains(":")) {
            input = "minecraft:" + input;
        }
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
