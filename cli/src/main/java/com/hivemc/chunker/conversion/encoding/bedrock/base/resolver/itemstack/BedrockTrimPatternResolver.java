package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrimPattern;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Trim Pattern resolver, used for Bedrock
 */
public class BedrockTrimPatternResolver implements Resolver<String, ChunkerTrimPattern> {
    private final InvertibleMap<ChunkerTrimPattern, String> mapping = InvertibleMap.enumKeys(ChunkerTrimPattern.class);

    /**
     * Create a new bedrock trim pattern resolver.
     *
     * @param version the game version being used, as certain trims are only available after specific versions.
     */
    public BedrockTrimPatternResolver(Version version) {
        // R19U8
        if (version.isGreaterThanOrEqual(1, 19, 80)) {
            mapping.put(ChunkerTrimPattern.COAST, "coast");
            mapping.put(ChunkerTrimPattern.DUNE, "dune");
            mapping.put(ChunkerTrimPattern.EYE, "eye");
            mapping.put(ChunkerTrimPattern.RIB, "rib");
            mapping.put(ChunkerTrimPattern.SENTRY, "sentry");
            mapping.put(ChunkerTrimPattern.SNOUT, "snout");
            mapping.put(ChunkerTrimPattern.SPIRE, "spire");
            mapping.put(ChunkerTrimPattern.TIDE, "tide");
            mapping.put(ChunkerTrimPattern.VEX, "vex");
            mapping.put(ChunkerTrimPattern.WARD, "ward");
            mapping.put(ChunkerTrimPattern.WILD, "wild");
            mapping.put(ChunkerTrimPattern.HOST, "host");
            mapping.put(ChunkerTrimPattern.RAISER, "raiser");
            mapping.put(ChunkerTrimPattern.SHAPER, "shaper");
            mapping.put(ChunkerTrimPattern.SILENCE, "silence");
            mapping.put(ChunkerTrimPattern.WAYFINDER, "wayfinder");
        }

        // R20U8
        if (version.isGreaterThanOrEqual(1, 20, 80)) {
            mapping.put(ChunkerTrimPattern.BOLT, "bolt");
            mapping.put(ChunkerTrimPattern.FLOW, "flow");
        }
    }

    @Override
    public Optional<String> from(ChunkerTrimPattern input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerTrimPattern> to(String input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
