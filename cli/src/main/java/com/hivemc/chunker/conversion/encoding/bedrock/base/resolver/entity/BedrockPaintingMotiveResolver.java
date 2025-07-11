package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.entity.PaintingEntity;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Painting Motive resolver, used for Bedrock
 */
public class BedrockPaintingMotiveResolver implements Resolver<String, PaintingEntity.Motive> {
    private final InvertibleMap<PaintingEntity.Motive, String> mapping = InvertibleMap.enumKeys(PaintingEntity.Motive.class);

    /**
     * Create a new bedrock painting motive resolver.
     *
     * @param bedrockVersion the game version being used, as certain paintings are only available after specific versions.
     */
    public BedrockPaintingMotiveResolver(Version bedrockVersion) {
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
        mapping.put(PaintingEntity.Motive.EARTH, "Earth");
        mapping.put(PaintingEntity.Motive.FIRE, "Fire");
        mapping.put(PaintingEntity.Motive.WATER, "Water");
        mapping.put(PaintingEntity.Motive.WIND, "Wind");
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

        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(PaintingEntity.Motive.BACKYARD, "backyard");
            mapping.put(PaintingEntity.Motive.BOUQUET, "bouquet");
            mapping.put(PaintingEntity.Motive.CAVEBIRD, "cavebird");
            mapping.put(PaintingEntity.Motive.CHANGING, "changing");
            mapping.put(PaintingEntity.Motive.COTAN, "cotan");
            mapping.put(PaintingEntity.Motive.ENDBOSS, "endboss");
            mapping.put(PaintingEntity.Motive.FERN, "fern");
            mapping.put(PaintingEntity.Motive.FINDING, "finding");
            mapping.put(PaintingEntity.Motive.LOWMIST, "lowmist");
            mapping.put(PaintingEntity.Motive.ORB, "orb");
            mapping.put(PaintingEntity.Motive.OWLEMONS, "owlemons");
            mapping.put(PaintingEntity.Motive.PASSAGE, "passage");
            mapping.put(PaintingEntity.Motive.POND, "pond");
            mapping.put(PaintingEntity.Motive.SUNFLOWERS, "sunflowers");
            mapping.put(PaintingEntity.Motive.TIDES, "tides");
            mapping.put(PaintingEntity.Motive.UNPACKED, "unpacked");
            mapping.put(PaintingEntity.Motive.MEDITATIVE, "meditative");
            mapping.put(PaintingEntity.Motive.PRAIRIE_RIDE, "prairie_ride");
            mapping.put(PaintingEntity.Motive.HUMBLE, "humble");
            mapping.put(PaintingEntity.Motive.BAROQUE, "baroque");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 93)) {
            mapping.put(PaintingEntity.Motive.DENNIS, "dennis");
        }
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
