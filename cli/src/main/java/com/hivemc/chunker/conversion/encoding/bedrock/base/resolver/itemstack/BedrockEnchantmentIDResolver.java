package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.enchantment.ChunkerEnchantmentType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Enchantment ID resolver, used for Bedrock
 */
public class BedrockEnchantmentIDResolver implements Resolver<Integer, ChunkerEnchantmentType> {
    private final InvertibleMap<ChunkerEnchantmentType, Integer> mapping = InvertibleMap.enumKeys(ChunkerEnchantmentType.class);

    /**
     * Create a new bedrock ID enchantment resolver.
     *
     * @param bedrockVersion the game version being used, as certain enchantments are only available after specific versions.
     */
    public BedrockEnchantmentIDResolver(Version bedrockVersion) {
        mapping.put(ChunkerEnchantmentType.ALL_DAMAGE_PROTECTION, 0);
        mapping.put(ChunkerEnchantmentType.FIRE_PROTECTION, 1);
        mapping.put(ChunkerEnchantmentType.FALL_PROTECTION, 2);
        mapping.put(ChunkerEnchantmentType.BLAST_PROTECTION, 3);
        mapping.put(ChunkerEnchantmentType.PROJECTILE_PROTECTION, 4);
        mapping.put(ChunkerEnchantmentType.RESPIRATION, 6);
        mapping.put(ChunkerEnchantmentType.AQUA_AFFINITY, 8);
        mapping.put(ChunkerEnchantmentType.THORNS, 5);
        mapping.put(ChunkerEnchantmentType.DEPTH_STRIDER, 7);
        mapping.put(ChunkerEnchantmentType.SHARPNESS, 9);
        mapping.put(ChunkerEnchantmentType.SMITE, 10);
        mapping.put(ChunkerEnchantmentType.BANE_OF_ARTHROPODS, 11);
        mapping.put(ChunkerEnchantmentType.KNOCKBACK, 12);
        mapping.put(ChunkerEnchantmentType.FIRE_ASPECT, 13);
        mapping.put(ChunkerEnchantmentType.MOB_LOOTING, 14);
        // Note: SWEEPING_EDGE is not on Bedrock
        mapping.put(ChunkerEnchantmentType.BLOCK_EFFICIENCY, 15);
        mapping.put(ChunkerEnchantmentType.SILK_TOUCH, 16);
        mapping.put(ChunkerEnchantmentType.UNBREAKING, 17);
        mapping.put(ChunkerEnchantmentType.BLOCK_FORTUNE, 18);
        mapping.put(ChunkerEnchantmentType.POWER_ARROWS, 19);
        mapping.put(ChunkerEnchantmentType.PUNCH_ARROWS, 20);
        mapping.put(ChunkerEnchantmentType.FLAMING_ARROWS, 21);
        mapping.put(ChunkerEnchantmentType.INFINITY_ARROWS, 22);
        mapping.put(ChunkerEnchantmentType.FISHING_LUCK, 23);
        mapping.put(ChunkerEnchantmentType.FISHING_SPEED, 24);
        mapping.put(ChunkerEnchantmentType.FROST_WALKER, 25);
        mapping.put(ChunkerEnchantmentType.MENDING, 26);
        mapping.put(ChunkerEnchantmentType.BINDING_CURSE, 27);
        mapping.put(ChunkerEnchantmentType.VANISHING_CURSE, 28);

        if (bedrockVersion.isGreaterThanOrEqual(1, 4, 0)) {
            mapping.put(ChunkerEnchantmentType.IMPALING, 29);
            mapping.put(ChunkerEnchantmentType.RIPTIDE, 30);
            mapping.put(ChunkerEnchantmentType.LOYALTY, 31);
            mapping.put(ChunkerEnchantmentType.CHANNELING, 32);
        }

        if (bedrockVersion.isGreaterThanOrEqual(1, 8, 0)) {
            mapping.put(ChunkerEnchantmentType.MULTISHOT, 33);
            mapping.put(ChunkerEnchantmentType.PIERCING, 34);
            mapping.put(ChunkerEnchantmentType.QUICK_CHARGE, 35);
        }

        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 0)) {
            mapping.put(ChunkerEnchantmentType.SOUL_SPEED, 36);
        }

        if (bedrockVersion.isGreaterThanOrEqual(1, 19, 0)) {
            mapping.put(ChunkerEnchantmentType.SWIFT_SNEAK, 37);
        }

        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(ChunkerEnchantmentType.WIND_BURST, 38);
            mapping.put(ChunkerEnchantmentType.DENSITY, 39);
            mapping.put(ChunkerEnchantmentType.BREACH, 40);
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
