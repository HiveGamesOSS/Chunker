package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerEffectType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Effect ID resolver, used for Java
 */
public class JavaEffectIDResolver implements Resolver<Integer, ChunkerEffectType> {
    private final InvertibleMap<ChunkerEffectType, Integer> mapping = InvertibleMap.enumKeys(ChunkerEffectType.class);

    /**
     * Create a new java ID enchantment resolver.
     *
     * @param javaVersion the game version being used, as certain enchantments are only available after specific versions.
     */
    public JavaEffectIDResolver(Version javaVersion) {
        mapping.put(ChunkerEffectType.EMPTY, 0);
        mapping.put(ChunkerEffectType.EMPTY, -1);
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

        if (javaVersion.isGreaterThanOrEqual(1, 9, 0)) {
            mapping.put(ChunkerEffectType.LEVITATION, 25);
            mapping.put(ChunkerEffectType.LUCK, 26);
            mapping.put(ChunkerEffectType.UNLUCK, 27);
        }
        if (javaVersion.isGreaterThanOrEqual(1, 13, 0)) {
            mapping.put(ChunkerEffectType.CONDUIT_POWER, 29);
            mapping.put(ChunkerEffectType.SLOW_FALLING, 28);
            mapping.put(ChunkerEffectType.DOLPHINS_GRACE, 30);
        }
        if (javaVersion.isGreaterThanOrEqual(1, 14, 0)) {
            mapping.put(ChunkerEffectType.BAD_OMEN, 31);
            mapping.put(ChunkerEffectType.HERO_OF_THE_VILLAGE, 32);
        }

        if (javaVersion.isGreaterThanOrEqual(1, 19, 0)) {
            mapping.put(ChunkerEffectType.DARKNESS, 33);
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
