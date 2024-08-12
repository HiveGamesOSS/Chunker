package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.enchantment.ChunkerEnchantmentType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Enchantment resolver, used for Java
 */
public class JavaEnchantmentResolver implements Resolver<String, ChunkerEnchantmentType> {
    private final InvertibleMap<ChunkerEnchantmentType, String> mapping = InvertibleMap.enumKeys(ChunkerEnchantmentType.class);

    /**
     * Create a new java enchantment resolver.
     *
     * @param javaVersion the game version being used, as certain enchantments are only available after specific versions.
     */
    public JavaEnchantmentResolver(Version javaVersion) {
        mapping.put(ChunkerEnchantmentType.ALL_DAMAGE_PROTECTION, "minecraft:protection");
        mapping.put(ChunkerEnchantmentType.FIRE_PROTECTION, "minecraft:fire_protection");
        mapping.put(ChunkerEnchantmentType.FALL_PROTECTION, "minecraft:feather_falling");
        mapping.put(ChunkerEnchantmentType.BLAST_PROTECTION, "minecraft:blast_protection");
        mapping.put(ChunkerEnchantmentType.PROJECTILE_PROTECTION, "minecraft:projectile_protection");
        mapping.put(ChunkerEnchantmentType.RESPIRATION, "minecraft:respiration");
        mapping.put(ChunkerEnchantmentType.AQUA_AFFINITY, "minecraft:aqua_affinity");
        mapping.put(ChunkerEnchantmentType.THORNS, "minecraft:thorns");
        mapping.put(ChunkerEnchantmentType.DEPTH_STRIDER, "minecraft:depth_strider");
        mapping.put(ChunkerEnchantmentType.SHARPNESS, "minecraft:sharpness");
        mapping.put(ChunkerEnchantmentType.SMITE, "minecraft:smite");
        mapping.put(ChunkerEnchantmentType.BANE_OF_ARTHROPODS, "minecraft:bane_of_arthropods");
        mapping.put(ChunkerEnchantmentType.KNOCKBACK, "minecraft:knockback");
        mapping.put(ChunkerEnchantmentType.FIRE_ASPECT, "minecraft:fire_aspect");
        mapping.put(ChunkerEnchantmentType.MOB_LOOTING, "minecraft:looting");
        mapping.put(ChunkerEnchantmentType.SWEEPING_EDGE, "minecraft:sweeping");
        mapping.put(ChunkerEnchantmentType.BLOCK_EFFICIENCY, "minecraft:efficiency");
        mapping.put(ChunkerEnchantmentType.SILK_TOUCH, "minecraft:silk_touch");
        mapping.put(ChunkerEnchantmentType.UNBREAKING, "minecraft:unbreaking");
        mapping.put(ChunkerEnchantmentType.BLOCK_FORTUNE, "minecraft:fortune");
        mapping.put(ChunkerEnchantmentType.POWER_ARROWS, "minecraft:power");
        mapping.put(ChunkerEnchantmentType.PUNCH_ARROWS, "minecraft:punch");
        mapping.put(ChunkerEnchantmentType.FLAMING_ARROWS, "minecraft:flame");
        mapping.put(ChunkerEnchantmentType.INFINITY_ARROWS, "minecraft:infinity");
        mapping.put(ChunkerEnchantmentType.FISHING_LUCK, "minecraft:luck_of_the_sea");
        mapping.put(ChunkerEnchantmentType.FISHING_SPEED, "minecraft:lure");

        if (javaVersion.isGreaterThanOrEqual(1, 9, 0)) {
            mapping.put(ChunkerEnchantmentType.FROST_WALKER, "minecraft:frost_walker");
            mapping.put(ChunkerEnchantmentType.MENDING, "minecraft:mending");
        }

        if (javaVersion.isGreaterThanOrEqual(1, 11, 0)) {
            mapping.put(ChunkerEnchantmentType.BINDING_CURSE, "minecraft:binding_curse");
            mapping.put(ChunkerEnchantmentType.VANISHING_CURSE, "minecraft:vanishing_curse");
        }

        if (javaVersion.isGreaterThanOrEqual(1, 13, 0)) {
            mapping.put(ChunkerEnchantmentType.IMPALING, "minecraft:impaling");
            mapping.put(ChunkerEnchantmentType.RIPTIDE, "minecraft:riptide");
            mapping.put(ChunkerEnchantmentType.LOYALTY, "minecraft:loyalty");
            mapping.put(ChunkerEnchantmentType.CHANNELING, "minecraft:channeling");
        }

        if (javaVersion.isGreaterThanOrEqual(1, 14, 0)) {
            mapping.put(ChunkerEnchantmentType.MULTISHOT, "minecraft:multishot");
            mapping.put(ChunkerEnchantmentType.PIERCING, "minecraft:piercing");
            mapping.put(ChunkerEnchantmentType.QUICK_CHARGE, "minecraft:quick_charge");
        }

        if (javaVersion.isGreaterThanOrEqual(1, 16, 0)) {
            mapping.put(ChunkerEnchantmentType.SOUL_SPEED, "minecraft:soul_speed");
        }

        if (javaVersion.isGreaterThanOrEqual(1, 19, 0)) {
            mapping.put(ChunkerEnchantmentType.SWIFT_SNEAK, "minecraft:swift_sneak");
        }

        if (javaVersion.isGreaterThanOrEqual(1, 20, 5)) {
            // Renamed
            mapping.put(ChunkerEnchantmentType.SWEEPING_EDGE, "minecraft:sweeping_edge");
        }

        if (javaVersion.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(ChunkerEnchantmentType.WIND_BURST, "minecraft:wind_burst");
            mapping.put(ChunkerEnchantmentType.DENSITY, "minecraft:density");
            mapping.put(ChunkerEnchantmentType.BREACH, "minecraft:breach");
        }
    }

    @Override
    public Optional<String> from(ChunkerEnchantmentType input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerEnchantmentType> to(String input) {
        if (!input.contains(":")) {
            input = "minecraft:" + input;
        }
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
