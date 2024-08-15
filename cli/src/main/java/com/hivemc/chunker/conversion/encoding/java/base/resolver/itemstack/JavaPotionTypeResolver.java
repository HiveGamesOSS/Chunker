package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Potion type resolver, used for Java
 */
public class JavaPotionTypeResolver implements Resolver<String, ChunkerPotionType> {
    private final InvertibleMap<ChunkerPotionType, String> mapping = InvertibleMap.enumKeys(ChunkerPotionType.class);

    /**
     * Create a new java potion resolver.
     *
     * @param javaVersion the game version being used, as certain potions are only available after specific versions.
     */
    public JavaPotionTypeResolver(Version javaVersion) {
        mapping.put(ChunkerPotionType.WATER, "minecraft:water");
        mapping.put(ChunkerPotionType.MUNDANE, "minecraft:mundane");
        // LONG_MUNDANE is only on Bedrock
        mapping.put(ChunkerPotionType.THICK, "minecraft:thick");
        mapping.put(ChunkerPotionType.AWKWARD, "minecraft:awkward");

        mapping.put(ChunkerPotionType.NIGHT_VISION, "minecraft:night_vision");
        mapping.put(ChunkerPotionType.LONG_NIGHT_VISION, "minecraft:long_night_vision");

        mapping.put(ChunkerPotionType.INVISIBILITY, "minecraft:invisibility");
        mapping.put(ChunkerPotionType.LONG_INVISIBILITY, "minecraft:long_invisibility");

        mapping.put(ChunkerPotionType.LEAPING, "minecraft:leaping");
        mapping.put(ChunkerPotionType.LONG_LEAPING, "minecraft:long_leaping");
        mapping.put(ChunkerPotionType.STRONG_LEAPING, "minecraft:strong_leaping");

        mapping.put(ChunkerPotionType.FIRE_RESISTANCE, "minecraft:fire_resistance");
        mapping.put(ChunkerPotionType.LONG_FIRE_RESISTANCE, "minecraft:long_fire_resistance");

        mapping.put(ChunkerPotionType.SWIFTNESS, "minecraft:swiftness");
        mapping.put(ChunkerPotionType.LONG_SWIFTNESS, "minecraft:long_swiftness");
        mapping.put(ChunkerPotionType.STRONG_SWIFTNESS, "minecraft:strong_swiftness");

        mapping.put(ChunkerPotionType.SLOWNESS, "minecraft:slowness");
        mapping.put(ChunkerPotionType.LONG_SLOWNESS, "minecraft:long_slowness");
        mapping.put(ChunkerPotionType.STRONG_SLOWNESS, "minecraft:strong_slowness");

        mapping.put(ChunkerPotionType.WATER_BREATHING, "minecraft:water_breathing");
        mapping.put(ChunkerPotionType.LONG_WATER_BREATHING, "minecraft:long_water_breathing");

        mapping.put(ChunkerPotionType.HEALING, "minecraft:healing");
        mapping.put(ChunkerPotionType.STRONG_HEALING, "minecraft:strong_healing");

        mapping.put(ChunkerPotionType.HARMING, "minecraft:harming");
        mapping.put(ChunkerPotionType.STRONG_HARMING, "minecraft:strong_harming");

        mapping.put(ChunkerPotionType.POISON, "minecraft:poison");
        mapping.put(ChunkerPotionType.LONG_POISON, "minecraft:long_poison");
        mapping.put(ChunkerPotionType.STRONG_POISON, "minecraft:strong_poison");

        mapping.put(ChunkerPotionType.REGENERATION, "minecraft:regeneration");
        mapping.put(ChunkerPotionType.LONG_REGENERATION, "minecraft:long_regeneration");
        mapping.put(ChunkerPotionType.STRONG_REGENERATION, "minecraft:strong_regeneration");

        mapping.put(ChunkerPotionType.STRENGTH, "minecraft:strength");
        mapping.put(ChunkerPotionType.LONG_STRENGTH, "minecraft:long_strength");
        mapping.put(ChunkerPotionType.STRONG_STRENGTH, "minecraft:strong_strength");

        mapping.put(ChunkerPotionType.WEAKNESS, "minecraft:weakness");
        mapping.put(ChunkerPotionType.LONG_WEAKNESS, "minecraft:long_weakness");

        mapping.put(ChunkerPotionType.LUCK, "minecraft:luck"); // Only on java
        // WITHER is only on Bedrock

        mapping.put(ChunkerPotionType.TURTLE_MASTER, "minecraft:turtle_master");
        mapping.put(ChunkerPotionType.LONG_TURTLE_MASTER, "minecraft:long_turtle_master");
        mapping.put(ChunkerPotionType.STRONG_TURTLE_MASTER, "minecraft:strong_turtle_master");

        mapping.put(ChunkerPotionType.SLOW_FALLING, "minecraft:slow_falling");
        mapping.put(ChunkerPotionType.LONG_SLOW_FALLING, "minecraft:long_slow_falling");

        if (javaVersion.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(ChunkerPotionType.WIND_CHARGED, "minecraft:wind_charged");
            mapping.put(ChunkerPotionType.WEAVING, "minecraft:weaving");
            mapping.put(ChunkerPotionType.OOZING, "minecraft:oozing");
            mapping.put(ChunkerPotionType.INFESTED, "minecraft:infested");
        }
    }

    @Override
    public Optional<String> from(ChunkerPotionType input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerPotionType> to(String input) {
        if (!input.contains(":")) {
            input = "minecraft:" + input;
        }
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
