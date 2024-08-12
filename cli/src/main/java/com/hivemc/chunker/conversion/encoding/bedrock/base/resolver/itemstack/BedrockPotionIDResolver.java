package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Potion ID resolver, used for Bedrock
 */
public class BedrockPotionIDResolver implements Resolver<Integer, ChunkerPotionType> {
    private final InvertibleMap<ChunkerPotionType, Integer> mapping = InvertibleMap.enumKeys(ChunkerPotionType.class);

    /**
     * Create a new bedrock ID potion resolver.
     *
     * @param bedrockVersion the game version being used, as certain potions are only available after specific versions.
     */
    public BedrockPotionIDResolver(Version bedrockVersion) {
        mapping.put(ChunkerPotionType.EMPTY, -1);
        mapping.put(ChunkerPotionType.WATER, 0);
        mapping.put(ChunkerPotionType.MUNDANE, 1);
        mapping.put(ChunkerPotionType.LONG_MUNDANE, 2); // Only on bedrock
        mapping.put(ChunkerPotionType.THICK, 3);
        mapping.put(ChunkerPotionType.AWKWARD, 4);

        mapping.put(ChunkerPotionType.NIGHT_VISION, 5);
        mapping.put(ChunkerPotionType.LONG_NIGHT_VISION, 6);

        mapping.put(ChunkerPotionType.INVISIBILITY, 7);
        mapping.put(ChunkerPotionType.LONG_INVISIBILITY, 8);

        mapping.put(ChunkerPotionType.LEAPING, 9);
        mapping.put(ChunkerPotionType.LONG_LEAPING, 10);
        mapping.put(ChunkerPotionType.STRONG_LEAPING, 11);

        mapping.put(ChunkerPotionType.FIRE_RESISTANCE, 12);
        mapping.put(ChunkerPotionType.LONG_FIRE_RESISTANCE, 13);

        mapping.put(ChunkerPotionType.SWIFTNESS, 14);
        mapping.put(ChunkerPotionType.LONG_SWIFTNESS, 15);
        mapping.put(ChunkerPotionType.STRONG_SWIFTNESS, 16);

        mapping.put(ChunkerPotionType.SLOWNESS, 17);
        mapping.put(ChunkerPotionType.LONG_SLOWNESS, 18);

        mapping.put(ChunkerPotionType.WATER_BREATHING, 19);
        mapping.put(ChunkerPotionType.LONG_WATER_BREATHING, 20);

        mapping.put(ChunkerPotionType.HEALING, 21);
        mapping.put(ChunkerPotionType.STRONG_HEALING, 22);

        mapping.put(ChunkerPotionType.HARMING, 23);
        mapping.put(ChunkerPotionType.STRONG_HARMING, 24);

        mapping.put(ChunkerPotionType.POISON, 25);
        mapping.put(ChunkerPotionType.LONG_POISON, 26);
        mapping.put(ChunkerPotionType.STRONG_POISON, 27);

        mapping.put(ChunkerPotionType.REGENERATION, 28);
        mapping.put(ChunkerPotionType.LONG_REGENERATION, 29);
        mapping.put(ChunkerPotionType.STRONG_REGENERATION, 30);

        mapping.put(ChunkerPotionType.STRENGTH, 31);
        mapping.put(ChunkerPotionType.LONG_STRENGTH, 32);
        mapping.put(ChunkerPotionType.STRONG_STRENGTH, 33);

        mapping.put(ChunkerPotionType.WEAKNESS, 34);
        mapping.put(ChunkerPotionType.LONG_WEAKNESS, 35);

        // LUCK is only on Java
        mapping.put(ChunkerPotionType.WITHER, 36); // Only on bedrock

        mapping.put(ChunkerPotionType.TURTLE_MASTER, 37);
        mapping.put(ChunkerPotionType.LONG_TURTLE_MASTER, 38);
        mapping.put(ChunkerPotionType.STRONG_TURTLE_MASTER, 39);

        mapping.put(ChunkerPotionType.SLOW_FALLING, 40);
        mapping.put(ChunkerPotionType.LONG_SLOW_FALLING, 41);

        // Parity was added with strong slowness in 1.16
        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 0)) {
            mapping.put(ChunkerPotionType.STRONG_SLOWNESS, 42);
        }

        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(ChunkerPotionType.WIND_CHARGED, 43);
            mapping.put(ChunkerPotionType.WEAVING, 44);
            mapping.put(ChunkerPotionType.OOZING, 45);
            mapping.put(ChunkerPotionType.INFESTED, 46);
        }
    }

    @Override
    public Optional<Integer> from(ChunkerPotionType input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerPotionType> to(Integer input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
