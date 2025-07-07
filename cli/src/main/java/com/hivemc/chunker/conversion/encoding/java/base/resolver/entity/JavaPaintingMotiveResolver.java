package com.hivemc.chunker.conversion.encoding.java.base.resolver.entity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.entity.PaintingEntity;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Painting Motive resolver, used for Java
 */
public class JavaPaintingMotiveResolver implements Resolver<String, PaintingEntity.Motive> {
    private final InvertibleMap<PaintingEntity.Motive, String> mapping = InvertibleMap.enumKeys(PaintingEntity.Motive.class);

    /**
     * Create a new java painting motive resolver.
     *
     * @param javaVersion the game version being used, as certain paintings are only available after specific versions.
     */
    public JavaPaintingMotiveResolver(Version javaVersion) {
        mapping.put(PaintingEntity.Motive.KEBAB, "minecraft:kebab");
        mapping.put(PaintingEntity.Motive.AZTEC, "minecraft:aztec");
        mapping.put(PaintingEntity.Motive.ALBAN, "minecraft:alban");
        mapping.put(PaintingEntity.Motive.AZTEC2, "minecraft:aztec2");
        mapping.put(PaintingEntity.Motive.BOMB, "minecraft:bomb");
        mapping.put(PaintingEntity.Motive.PLANT, "minecraft:plant");
        mapping.put(PaintingEntity.Motive.WASTELAND, "minecraft:wasteland");
        mapping.put(PaintingEntity.Motive.WANDERER, "minecraft:wanderer");
        mapping.put(PaintingEntity.Motive.GRAHAM, "minecraft:graham");
        mapping.put(PaintingEntity.Motive.SEA, "minecraft:sea");
        mapping.put(PaintingEntity.Motive.CREEBET, "minecraft:creebet");
        mapping.put(PaintingEntity.Motive.SUNSET, "minecraft:sunset");
        mapping.put(PaintingEntity.Motive.COURBET, "minecraft:courbet");
        mapping.put(PaintingEntity.Motive.POOL, "minecraft:pool");
        mapping.put(PaintingEntity.Motive.MATCH, "minecraft:match");
        mapping.put(PaintingEntity.Motive.BUST, "minecraft:bust");
        mapping.put(PaintingEntity.Motive.STAGE, "minecraft:stage");
        mapping.put(PaintingEntity.Motive.VOID, "minecraft:void");
        mapping.put(PaintingEntity.Motive.SKULL_AND_ROSES, "minecraft:skull_and_roses");
        mapping.put(PaintingEntity.Motive.WITHER, "minecraft:wither");
        mapping.put(PaintingEntity.Motive.FIGHTERS, "minecraft:fighters");
        mapping.put(PaintingEntity.Motive.SKELETON, "minecraft:skeleton");
        mapping.put(PaintingEntity.Motive.DONKEY_KONG, "minecraft:donkey_kong");
        mapping.put(PaintingEntity.Motive.POINTER, "minecraft:pointer");
        mapping.put(PaintingEntity.Motive.PIGSCENE, "minecraft:pigscene");
        mapping.put(PaintingEntity.Motive.BURNING_SKULL, "minecraft:burning_skull");

        if (javaVersion.isGreaterThanOrEqual(1, 19, 0)) {
            mapping.put(PaintingEntity.Motive.EARTH, "minecraft:earth");
            mapping.put(PaintingEntity.Motive.FIRE, "minecraft:fire");
            mapping.put(PaintingEntity.Motive.WATER, "minecraft:water");
            mapping.put(PaintingEntity.Motive.WIND, "minecraft:wind");
        }

        if (javaVersion.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(PaintingEntity.Motive.BACKYARD, "minecraft:backyard");
            mapping.put(PaintingEntity.Motive.BOUQUET, "minecraft:bouquet");
            mapping.put(PaintingEntity.Motive.CAVEBIRD, "minecraft:cavebird");
            mapping.put(PaintingEntity.Motive.CHANGING, "minecraft:changing");
            mapping.put(PaintingEntity.Motive.COTAN, "minecraft:cotan");
            mapping.put(PaintingEntity.Motive.ENDBOSS, "minecraft:endboss");
            mapping.put(PaintingEntity.Motive.FERN, "minecraft:fern");
            mapping.put(PaintingEntity.Motive.FINDING, "minecraft:finding");
            mapping.put(PaintingEntity.Motive.LOWMIST, "minecraft:lowmist");
            mapping.put(PaintingEntity.Motive.ORB, "minecraft:orb");
            mapping.put(PaintingEntity.Motive.OWLEMONS, "minecraft:owlemons");
            mapping.put(PaintingEntity.Motive.PASSAGE, "minecraft:passage");
            mapping.put(PaintingEntity.Motive.POND, "minecraft:pond");
            mapping.put(PaintingEntity.Motive.SUNFLOWERS, "minecraft:sunflowers");
            mapping.put(PaintingEntity.Motive.TIDES, "minecraft:tides");
            mapping.put(PaintingEntity.Motive.UNPACKED, "minecraft:unpacked");
            mapping.put(PaintingEntity.Motive.MEDITATIVE, "minecraft:meditative");
            mapping.put(PaintingEntity.Motive.PRAIRIE_RIDE, "minecraft:prairie_ride");
            mapping.put(PaintingEntity.Motive.HUMBLE, "minecraft:humble");
            mapping.put(PaintingEntity.Motive.BAROQUE, "minecraft:baroque");
        }
        if (javaVersion.isGreaterThanOrEqual(1, 21, 7)) {
            mapping.put(PaintingEntity.Motive.DENNIS, "minecraft:dennis");
        }
    }

    @Override
    public Optional<String> from(PaintingEntity.Motive input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<PaintingEntity.Motive> to(String input) {
        if (!input.contains(":")) {
            input = "minecraft:" + input;
        }
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
