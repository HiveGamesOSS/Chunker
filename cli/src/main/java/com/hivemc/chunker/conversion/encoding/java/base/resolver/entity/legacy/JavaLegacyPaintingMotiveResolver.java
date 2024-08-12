package com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.entity.PaintingEntity;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Painting Motive resolver, used for legacy Java
 */
public class JavaLegacyPaintingMotiveResolver implements Resolver<String, PaintingEntity.Motive> {
    private final InvertibleMap<PaintingEntity.Motive, String> mapping = InvertibleMap.enumKeys(PaintingEntity.Motive.class);

    /**
     * Create a new java painting motive resolver.
     *
     * @param version the game version being used, as certain paintings are only available after specific versions.
     */
    public JavaLegacyPaintingMotiveResolver(Version version) {
        mapping.put(PaintingEntity.Motive.KEBAB, "Kebab");
        mapping.put(PaintingEntity.Motive.AZTEC, "Aztec");
        mapping.put(PaintingEntity.Motive.ALBAN, "Alban");
        mapping.put(PaintingEntity.Motive.AZTEC2, "Aztec2");
        mapping.put(PaintingEntity.Motive.BOMB, "Bomb");
        mapping.put(PaintingEntity.Motive.PLANT, "Plant");
        mapping.put(PaintingEntity.Motive.WASTELAND, "Wasteland");
        mapping.put(PaintingEntity.Motive.WANDERER, "Wanderer");
        mapping.put(PaintingEntity.Motive.GRAHAM, "Graham");
        mapping.put(PaintingEntity.Motive.SEA, "Sea");
        mapping.put(PaintingEntity.Motive.CREEBET, "Creebet");
        mapping.put(PaintingEntity.Motive.SUNSET, "Sunset");
        mapping.put(PaintingEntity.Motive.COURBET, "Courbet");
        mapping.put(PaintingEntity.Motive.POOL, "Pool");
        mapping.put(PaintingEntity.Motive.MATCH, "Match");
        mapping.put(PaintingEntity.Motive.BUST, "Bust");
        mapping.put(PaintingEntity.Motive.STAGE, "Stage");
        mapping.put(PaintingEntity.Motive.VOID, "Void");
        mapping.put(PaintingEntity.Motive.SKULL_AND_ROSES, "SkullAndRoses");
        mapping.put(PaintingEntity.Motive.WITHER, "Wither");
        mapping.put(PaintingEntity.Motive.FIGHTERS, "Fighters");
        mapping.put(PaintingEntity.Motive.SKELETON, "Skeleton");
        mapping.put(PaintingEntity.Motive.DONKEY_KONG, "DonkeyKong");
        mapping.put(PaintingEntity.Motive.POINTER, "Pointer");
        mapping.put(PaintingEntity.Motive.PIGSCENE, "Pigscene");
        mapping.put(PaintingEntity.Motive.BURNING_SKULL, "BurningSkull");
    }

    @Override
    public Optional<String> from(PaintingEntity.Motive input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<PaintingEntity.Motive> to(String input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
