package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrimPattern;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Trim Pattern resolver, used for Java
 */
public class JavaTrimPatternResolver implements Resolver<String, ChunkerTrimPattern> {
    private final InvertibleMap<ChunkerTrimPattern, String> mapping = InvertibleMap.enumKeys(ChunkerTrimPattern.class);

    /**
     * Create a new java trim pattern resolver.
     *
     * @param version the game version being used, as certain potions are only available after specific versions.
     */
    public JavaTrimPatternResolver(Version version) {
        // 1.19.4
        if (version.isGreaterThanOrEqual(1, 19, 4)) {
            mapping.put(ChunkerTrimPattern.COAST, "minecraft:coast");
            mapping.put(ChunkerTrimPattern.DUNE, "minecraft:dune");
            mapping.put(ChunkerTrimPattern.EYE, "minecraft:eye");
            mapping.put(ChunkerTrimPattern.RIB, "minecraft:rib");
            mapping.put(ChunkerTrimPattern.SENTRY, "minecraft:sentry");
            mapping.put(ChunkerTrimPattern.SNOUT, "minecraft:snout");
            mapping.put(ChunkerTrimPattern.SPIRE, "minecraft:spire");
            mapping.put(ChunkerTrimPattern.TIDE, "minecraft:tide");
            mapping.put(ChunkerTrimPattern.VEX, "minecraft:vex");
            mapping.put(ChunkerTrimPattern.WARD, "minecraft:ward");
            mapping.put(ChunkerTrimPattern.WILD, "minecraft:wild");
        }

        // 1.20
        if (version.isGreaterThanOrEqual(1, 20, 0)) {
            mapping.put(ChunkerTrimPattern.HOST, "minecraft:host");
            mapping.put(ChunkerTrimPattern.RAISER, "minecraft:raiser");
            mapping.put(ChunkerTrimPattern.SHAPER, "minecraft:shaper");
            mapping.put(ChunkerTrimPattern.SILENCE, "minecraft:silence");
            mapping.put(ChunkerTrimPattern.WAYFINDER, "minecraft:wayfinder");
        }

        // 1.20.5
        if (version.isGreaterThanOrEqual(1, 20, 5)) {
            mapping.put(ChunkerTrimPattern.BOLT, "minecraft:bolt");
            mapping.put(ChunkerTrimPattern.FLOW, "minecraft:flow");
        }
    }

    @Override
    public Optional<String> from(ChunkerTrimPattern input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerTrimPattern> to(String input) {
        if (!input.contains(":")) {
            input = "minecraft:" + input;
        }
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
