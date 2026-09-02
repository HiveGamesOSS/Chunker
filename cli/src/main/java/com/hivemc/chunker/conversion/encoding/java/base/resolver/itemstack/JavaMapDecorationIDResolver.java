package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.map.ChunkerExplorerMap;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Map Decoration ID resolver, used for Java. Decorations used the index of the type before 1.20.5 gave them names.
 */
public class JavaMapDecorationIDResolver implements Resolver<Integer, ChunkerExplorerMap> {
    private final InvertibleMap<ChunkerExplorerMap, Integer> mapping = InvertibleMap.enumKeys(ChunkerExplorerMap.class);

    /**
     * Create a new java map decoration ID resolver.
     *
     * @param version the game version being used, as certain decorations are only available after specific versions.
     */
    public JavaMapDecorationIDResolver(Version version) {
        // 1.11 added the explorer maps
        if (version.isGreaterThanOrEqual(1, 11, 0)) {
            mapping.put(ChunkerExplorerMap.WOODLAND_MANSION, 8);
            mapping.put(ChunkerExplorerMap.OCEAN_MONUMENT, 9);
        }

        // 1.13 added the buried treasure map, after the banner decorations
        if (version.isGreaterThanOrEqual(1, 13, 0)) {
            mapping.put(ChunkerExplorerMap.BURIED_TREASURE, 26);
        }

        // 1.14 added the village, jungle temple and swamp hut explorer maps
        if (version.isGreaterThanOrEqual(1, 14, 0)) {
            mapping.put(ChunkerExplorerMap.DESERT_VILLAGE, 27);
            mapping.put(ChunkerExplorerMap.PLAINS_VILLAGE, 28);
            mapping.put(ChunkerExplorerMap.SAVANNA_VILLAGE, 29);
            mapping.put(ChunkerExplorerMap.SNOWY_VILLAGE, 30);
            mapping.put(ChunkerExplorerMap.TAIGA_VILLAGE, 31);
            mapping.put(ChunkerExplorerMap.JUNGLE_PYRAMID, 32);
            mapping.put(ChunkerExplorerMap.SWAMP_HUT, 33);
        }
    }

    @Override
    public Optional<Integer> from(ChunkerExplorerMap input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerExplorerMap> to(Integer input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
