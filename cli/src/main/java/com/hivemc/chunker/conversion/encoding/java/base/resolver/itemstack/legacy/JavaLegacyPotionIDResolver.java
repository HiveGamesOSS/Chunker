package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack.legacy;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Potion ID resolver, used for Java.
 */
public class JavaLegacyPotionIDResolver implements Resolver<Integer, ChunkerPotionType> {
    private final InvertibleMap<ChunkerPotionType, Integer> mapping = InvertibleMap.enumKeys(ChunkerPotionType.class);

    /**
     * Create a new java ID potion resolver.
     *
     * @param javaVersion the game version being used, as certain potions are only available after specific versions.
     */
    public JavaLegacyPotionIDResolver(Version javaVersion) {
        mapping.put(ChunkerPotionType.WATER, 0x00);
        mapping.put(ChunkerPotionType.REGENERATION, 0x01);
        mapping.put(ChunkerPotionType.SWIFTNESS, 0x02);
        mapping.put(ChunkerPotionType.FIRE_RESISTANCE, 0x03);
        mapping.put(ChunkerPotionType.POISON, 0x04);
        mapping.put(ChunkerPotionType.HEALING, 0x05);
        mapping.put(ChunkerPotionType.NIGHT_VISION, 0x06);
        mapping.put(ChunkerPotionType.WEAKNESS, 0x08);
        mapping.put(ChunkerPotionType.STRENGTH, 0x09);
        mapping.put(ChunkerPotionType.SLOWNESS, 0x0A);
        mapping.put(ChunkerPotionType.LEAPING, 0x0B);
        mapping.put(ChunkerPotionType.HARMING, 0x0C);
        mapping.put(ChunkerPotionType.WATER_BREATHING, 0x0D);
        mapping.put(ChunkerPotionType.INVISIBILITY, 0x0E);
        mapping.put(ChunkerPotionType.AWKWARD, 0x10);
        mapping.put(ChunkerPotionType.REGENERATION, 0x11);
        mapping.put(ChunkerPotionType.SWIFTNESS, 0x12);
        mapping.put(ChunkerPotionType.FIRE_RESISTANCE, 0x13);
        mapping.put(ChunkerPotionType.POISON, 0x14);
        mapping.put(ChunkerPotionType.HEALING, 0x15);
        mapping.put(ChunkerPotionType.NIGHT_VISION, 0x16);
        mapping.put(ChunkerPotionType.WEAKNESS, 0x18);
        mapping.put(ChunkerPotionType.STRENGTH, 0x19);
        mapping.put(ChunkerPotionType.SLOWNESS, 0x1A);
        mapping.put(ChunkerPotionType.LEAPING, 0x1B);
        mapping.put(ChunkerPotionType.HARMING, 0x1C);
        mapping.put(ChunkerPotionType.WATER_BREATHING, 0x1D);
        mapping.put(ChunkerPotionType.INVISIBILITY, 0x1E);
        mapping.put(ChunkerPotionType.THICK, 0x20);
        mapping.put(ChunkerPotionType.STRONG_REGENERATION, 0x21);
        mapping.put(ChunkerPotionType.STRONG_SWIFTNESS, 0x22);
        mapping.put(ChunkerPotionType.FIRE_RESISTANCE, 0x23);
        mapping.put(ChunkerPotionType.STRONG_POISON, 0x24);
        mapping.put(ChunkerPotionType.STRONG_HEALING, 0x25);
        mapping.put(ChunkerPotionType.NIGHT_VISION, 0x26);
        mapping.put(ChunkerPotionType.WEAKNESS, 0x28);
        mapping.put(ChunkerPotionType.STRONG_STRENGTH, 0x29);
        mapping.put(ChunkerPotionType.SLOWNESS, 0x2A);
        mapping.put(ChunkerPotionType.STRONG_LEAPING, 0x2B);
        mapping.put(ChunkerPotionType.STRONG_HARMING, 0x2C);
        mapping.put(ChunkerPotionType.WATER_BREATHING, 0x2D);
        mapping.put(ChunkerPotionType.INVISIBILITY, 0x2E);
        mapping.put(ChunkerPotionType.STRONG_REGENERATION, 0x31);
        mapping.put(ChunkerPotionType.STRONG_SWIFTNESS, 0x32);
        mapping.put(ChunkerPotionType.FIRE_RESISTANCE, 0x33);
        mapping.put(ChunkerPotionType.STRONG_POISON, 0x34);
        mapping.put(ChunkerPotionType.STRONG_HEALING, 0x35);
        mapping.put(ChunkerPotionType.NIGHT_VISION, 0x36);
        mapping.put(ChunkerPotionType.WEAKNESS, 0x38);
        mapping.put(ChunkerPotionType.STRONG_STRENGTH, 0x39);
        mapping.put(ChunkerPotionType.SLOWNESS, 0x3A);
        mapping.put(ChunkerPotionType.STRONG_LEAPING, 0x3B);
        mapping.put(ChunkerPotionType.STRONG_HARMING, 0x3C);
        mapping.put(ChunkerPotionType.WATER_BREATHING, 0x3D);
        mapping.put(ChunkerPotionType.INVISIBILITY, 0x3E);
        mapping.put(ChunkerPotionType.MUNDANE, 0x40);
        mapping.put(ChunkerPotionType.LONG_REGENERATION, 0x41);
        mapping.put(ChunkerPotionType.LONG_SWIFTNESS, 0x42);
        mapping.put(ChunkerPotionType.LONG_FIRE_RESISTANCE, 0x43);
        mapping.put(ChunkerPotionType.LONG_POISON, 0x44);
        mapping.put(ChunkerPotionType.HEALING, 0x45);
        mapping.put(ChunkerPotionType.LONG_NIGHT_VISION, 0x46);
        mapping.put(ChunkerPotionType.LONG_WEAKNESS, 0x48);
        mapping.put(ChunkerPotionType.LONG_STRENGTH, 0x49);
        mapping.put(ChunkerPotionType.LONG_SLOWNESS, 0x4A);
        mapping.put(ChunkerPotionType.LONG_LEAPING, 0x4B);
        mapping.put(ChunkerPotionType.HARMING, 0x4C);
        mapping.put(ChunkerPotionType.LONG_WATER_BREATHING, 0x4D);
        mapping.put(ChunkerPotionType.LONG_INVISIBILITY, 0x4E);
        mapping.put(ChunkerPotionType.AWKWARD, 0x50);
        mapping.put(ChunkerPotionType.LONG_REGENERATION, 0x51);
        mapping.put(ChunkerPotionType.LONG_SWIFTNESS, 0x52);
        mapping.put(ChunkerPotionType.LONG_FIRE_RESISTANCE, 0x53);
        mapping.put(ChunkerPotionType.LONG_POISON, 0x54);
        mapping.put(ChunkerPotionType.HEALING, 0x55);
        mapping.put(ChunkerPotionType.LONG_NIGHT_VISION, 0x56);
        mapping.put(ChunkerPotionType.LONG_WEAKNESS, 0x58);
        mapping.put(ChunkerPotionType.LONG_STRENGTH, 0x59);
        mapping.put(ChunkerPotionType.LONG_SLOWNESS, 0x5A);
        mapping.put(ChunkerPotionType.LONG_LEAPING, 0x5B);
        mapping.put(ChunkerPotionType.HARMING, 0x5C);
        mapping.put(ChunkerPotionType.LONG_WATER_BREATHING, 0x5D);
        mapping.put(ChunkerPotionType.LONG_INVISIBILITY, 0x5E);
        mapping.put(ChunkerPotionType.THICK, 0x60);
        mapping.put(ChunkerPotionType.REGENERATION, 0x61);
        mapping.put(ChunkerPotionType.SWIFTNESS, 0x62);
        mapping.put(ChunkerPotionType.LONG_FIRE_RESISTANCE, 0x63);
        mapping.put(ChunkerPotionType.POISON, 0x64);
        mapping.put(ChunkerPotionType.STRONG_HEALING, 0x65);
        mapping.put(ChunkerPotionType.LONG_NIGHT_VISION, 0x66);
        mapping.put(ChunkerPotionType.LONG_WEAKNESS, 0x68);
        mapping.put(ChunkerPotionType.STRENGTH, 0x69);
        mapping.put(ChunkerPotionType.LONG_SLOWNESS, 0x6A);
        mapping.put(ChunkerPotionType.LEAPING, 0x6B);
        mapping.put(ChunkerPotionType.STRONG_HARMING, 0x6C);
        mapping.put(ChunkerPotionType.LONG_WATER_BREATHING, 0x6D);
        mapping.put(ChunkerPotionType.LONG_INVISIBILITY, 0x6E);
        mapping.put(ChunkerPotionType.REGENERATION, 0x71);
        mapping.put(ChunkerPotionType.SWIFTNESS, 0x72);
        mapping.put(ChunkerPotionType.LONG_FIRE_RESISTANCE, 0x73);
        mapping.put(ChunkerPotionType.POISON, 0x74);
        mapping.put(ChunkerPotionType.STRONG_HEALING, 0x75);
        mapping.put(ChunkerPotionType.LONG_NIGHT_VISION, 0x76);
        mapping.put(ChunkerPotionType.LONG_WEAKNESS, 0x78);
        mapping.put(ChunkerPotionType.STRENGTH, 0x79);
        mapping.put(ChunkerPotionType.LONG_SLOWNESS, 0x7A);
        mapping.put(ChunkerPotionType.LEAPING, 0x7B);
        mapping.put(ChunkerPotionType.STRONG_HARMING, 0x7C);
        mapping.put(ChunkerPotionType.LONG_WATER_BREATHING, 0x7D);
        mapping.put(ChunkerPotionType.LONG_INVISIBILITY, 0x7E);
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
