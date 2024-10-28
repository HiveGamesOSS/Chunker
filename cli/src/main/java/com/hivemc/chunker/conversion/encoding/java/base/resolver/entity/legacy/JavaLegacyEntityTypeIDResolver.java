package com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Entity Type ID resolver, used for Java
 */
public class JavaLegacyEntityTypeIDResolver implements Resolver<Integer, ChunkerEntityType> {
    private final InvertibleMap<ChunkerVanillaEntityType, Integer> mapping = InvertibleMap.enumKeys(ChunkerVanillaEntityType.class);

    /**
     * Create a new java ID entity type resolver.
     *
     * @param javaVersion the game version being used, as certain entities are only available after specific versions.
     */
    public JavaLegacyEntityTypeIDResolver(Version javaVersion) {
        mapping.put(ChunkerVanillaEntityType.ITEM, 1);
        mapping.put(ChunkerVanillaEntityType.EXPERIENCE_ORB, 2);
        mapping.put(ChunkerVanillaEntityType.EGG, 7);
        mapping.put(ChunkerVanillaEntityType.LEASH_KNOT, 8);
        mapping.put(ChunkerVanillaEntityType.PAINTING, 9);
        mapping.put(ChunkerVanillaEntityType.ARROW, 10);
        mapping.put(ChunkerVanillaEntityType.SNOWBALL, 11);
        mapping.put(ChunkerVanillaEntityType.FIREBALL, 12);
        mapping.put(ChunkerVanillaEntityType.SMALL_FIREBALL, 13);
        mapping.put(ChunkerVanillaEntityType.ENDER_PEARL, 14);
        mapping.put(ChunkerVanillaEntityType.EYE_OF_ENDER, 15);
        mapping.put(ChunkerVanillaEntityType.POTION, 16);
        mapping.put(ChunkerVanillaEntityType.EXPERIENCE_BOTTLE, 17);
        mapping.put(ChunkerVanillaEntityType.ITEM_FRAME, 18);
        mapping.put(ChunkerVanillaEntityType.WITHER_SKULL, 19);
        mapping.put(ChunkerVanillaEntityType.TNT, 20);
        mapping.put(ChunkerVanillaEntityType.FALLING_BLOCK, 21);
        mapping.put(ChunkerVanillaEntityType.FIREWORK_ROCKET, 22);
        mapping.put(ChunkerVanillaEntityType.SPECTRAL_ARROW, 24);
        mapping.put(ChunkerVanillaEntityType.SHULKER_BULLET, 25);
        mapping.put(ChunkerVanillaEntityType.DRAGON_FIREBALL, 26);
        mapping.put(ChunkerVanillaEntityType.ARMOR_STAND, 30);
        mapping.put(ChunkerVanillaEntityType.OAK_BOAT, 41);
        mapping.put(ChunkerVanillaEntityType.MINECART, 42);
        mapping.put(ChunkerVanillaEntityType.CHEST_MINECART, 43);
        mapping.put(ChunkerVanillaEntityType.FURNACE_MINECART, 44);
        mapping.put(ChunkerVanillaEntityType.TNT_MINECART, 45);
        mapping.put(ChunkerVanillaEntityType.HOPPER_MINECART, 46);
        mapping.put(ChunkerVanillaEntityType.SPAWNER_MINECART, 47);
        mapping.put(ChunkerVanillaEntityType.COMMAND_BLOCK_MINECART, 40);
        mapping.put(ChunkerVanillaEntityType.CREEPER, 50);
        mapping.put(ChunkerVanillaEntityType.SKELETON, 51);
        mapping.put(ChunkerVanillaEntityType.SPIDER, 52);
        mapping.put(ChunkerVanillaEntityType.GIANT, 53);
        mapping.put(ChunkerVanillaEntityType.ZOMBIE, 54);
        mapping.put(ChunkerVanillaEntityType.SLIME, 55);
        mapping.put(ChunkerVanillaEntityType.GHAST, 56);
        mapping.put(ChunkerVanillaEntityType.ZOMBIFIED_PIGLIN, 57);
        mapping.put(ChunkerVanillaEntityType.ENDERMAN, 58);
        mapping.put(ChunkerVanillaEntityType.CAVE_SPIDER, 59);
        mapping.put(ChunkerVanillaEntityType.SILVERFISH, 60);
        mapping.put(ChunkerVanillaEntityType.BLAZE, 61);
        mapping.put(ChunkerVanillaEntityType.MAGMA_CUBE, 62);
        mapping.put(ChunkerVanillaEntityType.ENDER_DRAGON, 63);
        mapping.put(ChunkerVanillaEntityType.WITHER, 64);
        mapping.put(ChunkerVanillaEntityType.BAT, 65);
        mapping.put(ChunkerVanillaEntityType.WITCH, 66);
        mapping.put(ChunkerVanillaEntityType.ENDERMITE, 67);
        mapping.put(ChunkerVanillaEntityType.GUARDIAN, 68);
        mapping.put(ChunkerVanillaEntityType.SHULKER, 69);
        mapping.put(ChunkerVanillaEntityType.PIG, 90);
        mapping.put(ChunkerVanillaEntityType.SHEEP, 91);
        mapping.put(ChunkerVanillaEntityType.COW, 92);
        mapping.put(ChunkerVanillaEntityType.CHICKEN, 93);
        mapping.put(ChunkerVanillaEntityType.SQUID, 94);
        mapping.put(ChunkerVanillaEntityType.WOLF, 95);
        mapping.put(ChunkerVanillaEntityType.MOOSHROOM, 96);
        mapping.put(ChunkerVanillaEntityType.SNOW_GOLEM, 97);
        mapping.put(ChunkerVanillaEntityType.OCELOT, 98);
        mapping.put(ChunkerVanillaEntityType.IRON_GOLEM, 99);
        mapping.put(ChunkerVanillaEntityType.HORSE, 100);
        mapping.put(ChunkerVanillaEntityType.RABBIT, 101);
        mapping.put(ChunkerVanillaEntityType.VILLAGER, 120);
        mapping.put(ChunkerVanillaEntityType.END_CRYSTAL, 200);
    }

    @Override
    public Optional<Integer> from(ChunkerEntityType input) {
        if (input instanceof ChunkerVanillaEntityType chunkerVanillaEntityType) {
            return Optional.ofNullable(mapping.forward().get(chunkerVanillaEntityType));
        } else {
            // No possible mapping
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChunkerEntityType> to(Integer input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
