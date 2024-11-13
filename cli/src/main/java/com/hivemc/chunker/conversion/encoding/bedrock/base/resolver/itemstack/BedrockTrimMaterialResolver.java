package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrimMaterial;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Trim Material resolver, used for Bedrock
 */
public class BedrockTrimMaterialResolver implements Resolver<String, ChunkerTrimMaterial> {
    private final InvertibleMap<ChunkerTrimMaterial, String> mapping = InvertibleMap.enumKeys(ChunkerTrimMaterial.class);

    /**
     * Create a new bedrock trim material resolver.
     *
     * @param version the game version being used, as certain trims are only available after specific versions.
     */
    public BedrockTrimMaterialResolver(Version version) {
        mapping.put(ChunkerTrimMaterial.AMETHYST, "amethyst");
        mapping.put(ChunkerTrimMaterial.COPPER, "copper");
        mapping.put(ChunkerTrimMaterial.DIAMOND, "diamond");
        mapping.put(ChunkerTrimMaterial.EMERALD, "emerald");
        mapping.put(ChunkerTrimMaterial.GOLD, "gold");
        mapping.put(ChunkerTrimMaterial.IRON, "iron");
        mapping.put(ChunkerTrimMaterial.LAPIS, "lapis");
        mapping.put(ChunkerTrimMaterial.NETHERITE, "netherite");
        mapping.put(ChunkerTrimMaterial.QUARTZ, "quartz");
        mapping.put(ChunkerTrimMaterial.REDSTONE, "redstone");

        // R21U5
        if (version.isGreaterThanOrEqual(1, 21, 50)) {
            mapping.put(ChunkerTrimMaterial.RESIN, "resin");
        }
    }

    @Override
    public Optional<String> from(ChunkerTrimMaterial input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerTrimMaterial> to(String input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
