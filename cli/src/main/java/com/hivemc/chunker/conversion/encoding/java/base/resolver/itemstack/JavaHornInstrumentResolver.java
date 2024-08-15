package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.horn.ChunkerHornInstrument;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Horn instrument resolver, used for Java
 */
public class JavaHornInstrumentResolver implements Resolver<String, ChunkerHornInstrument> {
    private final InvertibleMap<ChunkerHornInstrument, String> mapping = InvertibleMap.enumKeys(ChunkerHornInstrument.class);

    /**
     * Create a new java horn instrument resolver.
     *
     * @param javaVersion the game version being used.
     */
    public JavaHornInstrumentResolver(Version javaVersion) {
        mapping.put(ChunkerHornInstrument.PONDER_GOAT_HORN, "minecraft:ponder_goat_horn");
        mapping.put(ChunkerHornInstrument.SING_GOAT_HORN, "minecraft:sing_goat_horn");
        mapping.put(ChunkerHornInstrument.SEEK_GOAT_HORN, "minecraft:seek_goat_horn");
        mapping.put(ChunkerHornInstrument.FEEL_GOAT_HORN, "minecraft:feel_goat_horn");
        mapping.put(ChunkerHornInstrument.ADMIRE_GOAT_HORN, "minecraft:admire_goat_horn");
        mapping.put(ChunkerHornInstrument.CALL_GOAT_HORN, "minecraft:call_goat_horn");
        mapping.put(ChunkerHornInstrument.YEARN_GOAT_HORN, "minecraft:yearn_goat_horn");
        mapping.put(ChunkerHornInstrument.DREAM_GOAT_HORN, "minecraft:dream_goat_horn");
    }

    @Override
    public Optional<String> from(ChunkerHornInstrument input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerHornInstrument> to(String input) {
        if (!input.contains(":")) {
            input = "minecraft:" + input;
        }
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
