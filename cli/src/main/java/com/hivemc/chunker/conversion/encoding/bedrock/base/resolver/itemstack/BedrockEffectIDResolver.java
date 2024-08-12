package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerEffectType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Effect ID resolver, used for Bedrock
 */
public class BedrockEffectIDResolver implements Resolver<Integer, ChunkerEffectType> {
    private final InvertibleMap<ChunkerEffectType, Integer> mapping = InvertibleMap.enumKeys(ChunkerEffectType.class);

    /**
     * Create a new bedrock ID effect resolver.
     *
     * @param bedrockVersion the game version being used, as certain effects are only available after specific versions.
     */
    public BedrockEffectIDResolver(Version bedrockVersion) {
        mapping.put(ChunkerEffectType.EMPTY, 0);
        mapping.put(ChunkerEffectType.SPEED, 1);
        mapping.put(ChunkerEffectType.SLOWNESS, 2);
        mapping.put(ChunkerEffectType.HASTE, 3);
        mapping.put(ChunkerEffectType.MINING_FATIGUE, 4);
        mapping.put(ChunkerEffectType.STRENGTH, 5);
        mapping.put(ChunkerEffectType.INSTANT_HEALTH, 6);
        mapping.put(ChunkerEffectType.INSTANT_DAMAGE, 7);
        mapping.put(ChunkerEffectType.JUMP_BOOST, 8);
        mapping.put(ChunkerEffectType.NAUSEA, 9);
        mapping.put(ChunkerEffectType.REGENERATION, 10);
        mapping.put(ChunkerEffectType.RESISTANCE, 11);
        mapping.put(ChunkerEffectType.FIRE_RESISTANCE, 12);
        mapping.put(ChunkerEffectType.WATER_BREATHING, 13);
        mapping.put(ChunkerEffectType.INVISIBILITY, 14);
        mapping.put(ChunkerEffectType.BLINDNESS, 15);
        mapping.put(ChunkerEffectType.NIGHT_VISION, 16);
        mapping.put(ChunkerEffectType.HUNGER, 17);
        mapping.put(ChunkerEffectType.WEAKNESS, 18);
        mapping.put(ChunkerEffectType.POISON, 19);
        mapping.put(ChunkerEffectType.WITHER, 20);
        mapping.put(ChunkerEffectType.HEALTH_BOOST, 21);
        mapping.put(ChunkerEffectType.ABSORPTION, 22);
        mapping.put(ChunkerEffectType.SATURATION, 23);
        mapping.put(ChunkerEffectType.LEVITATION, 24);
        mapping.put(ChunkerEffectType.FATAL_POISON, 25);
        mapping.put(ChunkerEffectType.CONDUIT_POWER, 26);
        mapping.put(ChunkerEffectType.SLOW_FALLING, 27);
        mapping.put(ChunkerEffectType.BAD_OMEN, 28);
        mapping.put(ChunkerEffectType.HERO_OF_THE_VILLAGE, 29);
        if (bedrockVersion.isGreaterThanOrEqual(1, 18, 0)) {
            mapping.put(ChunkerEffectType.DARKNESS, 30);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(ChunkerEffectType.TRIAL_OMEN, 31);
            mapping.put(ChunkerEffectType.WIND_CHARGED, 32);
            mapping.put(ChunkerEffectType.WEAVING, 33);
            mapping.put(ChunkerEffectType.OOZING, 34);
            mapping.put(ChunkerEffectType.INFESTED, 35);
            mapping.put(ChunkerEffectType.RAID_OMEN, 36);
        }
    }

    @Override
    public Optional<Integer> from(ChunkerEffectType input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerEffectType> to(Integer input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
