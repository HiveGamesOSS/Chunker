package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrimMaterial;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Trim Material resolver, used for Java
 */
public class JavaTrimMaterialResolver implements Resolver<String, ChunkerTrimMaterial> {
    private final InvertibleMap<ChunkerTrimMaterial, String> mapping = InvertibleMap.enumKeys(ChunkerTrimMaterial.class);

    /**
     * Create a new java trim material resolver.
     *
     * @param version the game version being used, as certain potions are only available after specific versions.
     */
    public JavaTrimMaterialResolver(Version version) {
        mapping.put(ChunkerTrimMaterial.AMETHYST, "minecraft:amethyst");
        mapping.put(ChunkerTrimMaterial.COPPER, "minecraft:copper");
        mapping.put(ChunkerTrimMaterial.DIAMOND, "minecraft:diamond");
        mapping.put(ChunkerTrimMaterial.EMERALD, "minecraft:emerald");
        mapping.put(ChunkerTrimMaterial.GOLD, "minecraft:gold");
        mapping.put(ChunkerTrimMaterial.IRON, "minecraft:iron");
        mapping.put(ChunkerTrimMaterial.LAPIS, "minecraft:lapis");
        mapping.put(ChunkerTrimMaterial.NETHERITE, "minecraft:netherite");
        mapping.put(ChunkerTrimMaterial.QUARTZ, "minecraft:quartz");
        mapping.put(ChunkerTrimMaterial.REDSTONE, "minecraft:redstone");

        // 1.21.4
        if (version.isGreaterThanOrEqual(1, 21, 4)) {
            mapping.put(ChunkerTrimMaterial.RESIN, "minecraft:resin");
        }
    }

    @Override
    public Optional<String> from(ChunkerTrimMaterial input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerTrimMaterial> to(String input) {
        if (!input.contains(":")) {
            input = "minecraft:" + input;
        }
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
