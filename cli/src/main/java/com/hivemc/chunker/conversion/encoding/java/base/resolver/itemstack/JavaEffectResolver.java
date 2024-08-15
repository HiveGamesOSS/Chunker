package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerEffectType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Effect resolver, used for Java
 */
public class JavaEffectResolver implements Resolver<String, ChunkerEffectType> {
    private final InvertibleMap<ChunkerEffectType, String> mapping = InvertibleMap.enumKeys(ChunkerEffectType.class);

    /**
     * Create a new java ID enchantment resolver.
     *
     * @param javaVersion the game version being used, as certain enchantments are only available after specific versions.
     */
    public JavaEffectResolver(Version javaVersion) {
        mapping.put(ChunkerEffectType.EMPTY, "minecraft:empty"); // This isn't actually a valid name, however Java discards invalid names
        mapping.put(ChunkerEffectType.SPEED, "minecraft:speed");
        mapping.put(ChunkerEffectType.SLOWNESS, "minecraft:slowness");
        mapping.put(ChunkerEffectType.HASTE, "minecraft:haste");
        mapping.put(ChunkerEffectType.MINING_FATIGUE, "minecraft:mining_fatigue");
        mapping.put(ChunkerEffectType.STRENGTH, "minecraft:strength");
        mapping.put(ChunkerEffectType.INSTANT_HEALTH, "minecraft:instant_health");
        mapping.put(ChunkerEffectType.INSTANT_DAMAGE, "minecraft:instant_damage");
        mapping.put(ChunkerEffectType.JUMP_BOOST, "minecraft:jump_boost");
        mapping.put(ChunkerEffectType.NAUSEA, "minecraft:nausea");
        mapping.put(ChunkerEffectType.REGENERATION, "minecraft:regeneration");
        mapping.put(ChunkerEffectType.RESISTANCE, "minecraft:resistance");
        mapping.put(ChunkerEffectType.FIRE_RESISTANCE, "minecraft:fire_resistance");
        mapping.put(ChunkerEffectType.WATER_BREATHING, "minecraft:water_breathing");
        mapping.put(ChunkerEffectType.INVISIBILITY, "minecraft:invisibility");
        mapping.put(ChunkerEffectType.BLINDNESS, "minecraft:blindness");
        mapping.put(ChunkerEffectType.NIGHT_VISION, "minecraft:night_vision");
        mapping.put(ChunkerEffectType.HUNGER, "minecraft:hunger");
        mapping.put(ChunkerEffectType.WEAKNESS, "minecraft:weakness");
        mapping.put(ChunkerEffectType.POISON, "minecraft:poison");
        mapping.put(ChunkerEffectType.WITHER, "minecraft:wither");
        mapping.put(ChunkerEffectType.HEALTH_BOOST, "minecraft:health_boost");
        mapping.put(ChunkerEffectType.ABSORPTION, "minecraft:absorption");
        mapping.put(ChunkerEffectType.SATURATION, "minecraft:saturation");

        if (javaVersion.isGreaterThanOrEqual(1, 9, 0)) {
            mapping.put(ChunkerEffectType.LEVITATION, "minecraft:levitation");
            mapping.put(ChunkerEffectType.LUCK, "minecraft:luck");
            mapping.put(ChunkerEffectType.UNLUCK, "minecraft:unluck");
        }
        if (javaVersion.isGreaterThanOrEqual(1, 13, 0)) {
            mapping.put(ChunkerEffectType.CONDUIT_POWER, "minecraft:conduit_power");
            mapping.put(ChunkerEffectType.SLOW_FALLING, "minecraft:slow_falling");
            mapping.put(ChunkerEffectType.DOLPHINS_GRACE, "minecraft:dolphins_grace");
        }
        if (javaVersion.isGreaterThanOrEqual(1, 14, 0)) {
            mapping.put(ChunkerEffectType.BAD_OMEN, "minecraft:bad_omen");
            mapping.put(ChunkerEffectType.HERO_OF_THE_VILLAGE, "minecraft:hero_of_the_village");
        }
        if (javaVersion.isGreaterThanOrEqual(1, 19, 0)) {
            mapping.put(ChunkerEffectType.DARKNESS, "minecraft:darkness");
        }
        if (javaVersion.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(ChunkerEffectType.TRIAL_OMEN, "minecraft:trial_omen");
            mapping.put(ChunkerEffectType.WIND_CHARGED, "minecraft:wind_charged");
            mapping.put(ChunkerEffectType.WEAVING, "minecraft:weaving");
            mapping.put(ChunkerEffectType.OOZING, "minecraft:oozing");
            mapping.put(ChunkerEffectType.INFESTED, "minecraft:infested");
            mapping.put(ChunkerEffectType.RAID_OMEN, "minecraft:raid_omen");
        }
    }

    @Override
    public Optional<String> from(ChunkerEffectType input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerEffectType> to(String input) {
        if (!input.contains(":")) {
            input = "minecraft:" + input;
        }
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
