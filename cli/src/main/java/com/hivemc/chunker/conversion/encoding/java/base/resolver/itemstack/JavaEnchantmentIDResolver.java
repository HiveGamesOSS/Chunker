package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.enchantment.ChunkerEnchantmentType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Enchantment ID resolver, used for Java
 */
public class JavaEnchantmentIDResolver implements Resolver<Integer, ChunkerEnchantmentType> {
    private final InvertibleMap<ChunkerEnchantmentType, Integer> mapping = InvertibleMap.enumKeys(ChunkerEnchantmentType.class);

    /**
     * Create a new java enchantment resolver.
     *
     * @param javaVersion the game version being used, as certain enchantments are only available after specific versions.
     */
    public JavaEnchantmentIDResolver(Version javaVersion) {
        mapping.put(ChunkerEnchantmentType.ALL_DAMAGE_PROTECTION, 0);
        mapping.put(ChunkerEnchantmentType.FIRE_PROTECTION, 1);
        mapping.put(ChunkerEnchantmentType.FALL_PROTECTION, 2);
        mapping.put(ChunkerEnchantmentType.BLAST_PROTECTION, 3);
        mapping.put(ChunkerEnchantmentType.PROJECTILE_PROTECTION, 4);
        mapping.put(ChunkerEnchantmentType.RESPIRATION, 5);
        mapping.put(ChunkerEnchantmentType.AQUA_AFFINITY, 6);
        mapping.put(ChunkerEnchantmentType.THORNS, 7);
        mapping.put(ChunkerEnchantmentType.DEPTH_STRIDER, 8);
        mapping.put(ChunkerEnchantmentType.SHARPNESS, 16);
        mapping.put(ChunkerEnchantmentType.SMITE, 17);
        mapping.put(ChunkerEnchantmentType.BANE_OF_ARTHROPODS, 18);
        mapping.put(ChunkerEnchantmentType.KNOCKBACK, 19);
        mapping.put(ChunkerEnchantmentType.FIRE_ASPECT, 20);
        mapping.put(ChunkerEnchantmentType.MOB_LOOTING, 21);
        mapping.put(ChunkerEnchantmentType.SWEEPING_EDGE, 22);
        mapping.put(ChunkerEnchantmentType.BLOCK_EFFICIENCY, 32);
        mapping.put(ChunkerEnchantmentType.SILK_TOUCH, 33);
        mapping.put(ChunkerEnchantmentType.UNBREAKING, 34);
        mapping.put(ChunkerEnchantmentType.BLOCK_FORTUNE, 35);
        mapping.put(ChunkerEnchantmentType.POWER_ARROWS, 48);
        mapping.put(ChunkerEnchantmentType.PUNCH_ARROWS, 49);
        mapping.put(ChunkerEnchantmentType.FLAMING_ARROWS, 50);
        mapping.put(ChunkerEnchantmentType.INFINITY_ARROWS, 51);
        mapping.put(ChunkerEnchantmentType.FISHING_LUCK, 61);
        mapping.put(ChunkerEnchantmentType.FISHING_SPEED, 62);

        if (javaVersion.isGreaterThanOrEqual(1, 9, 0)) {
            mapping.put(ChunkerEnchantmentType.FROST_WALKER, 9);
            mapping.put(ChunkerEnchantmentType.MENDING, 70);
        }

        if (javaVersion.isGreaterThanOrEqual(1, 11, 0)) {
            mapping.put(ChunkerEnchantmentType.BINDING_CURSE, 10);
            mapping.put(ChunkerEnchantmentType.VANISHING_CURSE, 71);
        }
    }

    @Override
    public Optional<Integer> from(ChunkerEnchantmentType input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerEnchantmentType> to(Integer input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
