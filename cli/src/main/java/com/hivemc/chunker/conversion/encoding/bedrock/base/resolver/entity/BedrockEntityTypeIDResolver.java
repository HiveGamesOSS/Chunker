package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Entity Type ID resolver, used for Bedrock
 */
public class BedrockEntityTypeIDResolver implements Resolver<Integer, ChunkerEntityType> {
    private final InvertibleMap<ChunkerVanillaEntityType, Integer> mapping = InvertibleMap.enumKeys(ChunkerVanillaEntityType.class);

    /**
     * Create a new bedrock ID entity type resolver.
     *
     * @param bedrockVersion the game version being used, as certain entities are only available after specific versions.
     */
    public BedrockEntityTypeIDResolver(Version bedrockVersion) {
        mapping.put(ChunkerVanillaEntityType.CHICKEN, 10);
        mapping.put(ChunkerVanillaEntityType.COW, 11);
        mapping.put(ChunkerVanillaEntityType.PIG, 12);
        mapping.put(ChunkerVanillaEntityType.SHEEP, 13);
        mapping.put(ChunkerVanillaEntityType.WOLF, 14);
        mapping.put(ChunkerVanillaEntityType.VILLAGER, 15); // v1
        mapping.put(ChunkerVanillaEntityType.MOOSHROOM, 16);
        mapping.put(ChunkerVanillaEntityType.SQUID, 17);
        mapping.put(ChunkerVanillaEntityType.RABBIT, 18);
        mapping.put(ChunkerVanillaEntityType.BAT, 19);
        mapping.put(ChunkerVanillaEntityType.IRON_GOLEM, 20);
        mapping.put(ChunkerVanillaEntityType.SNOW_GOLEM, 21);
        mapping.put(ChunkerVanillaEntityType.OCELOT, 22);
        mapping.put(ChunkerVanillaEntityType.HORSE, 23);
        mapping.put(ChunkerVanillaEntityType.DONKEY, 24);
        mapping.put(ChunkerVanillaEntityType.MULE, 25);
        mapping.put(ChunkerVanillaEntityType.SKELETON_HORSE, 26);
        mapping.put(ChunkerVanillaEntityType.ZOMBIE_HORSE, 27);
        mapping.put(ChunkerVanillaEntityType.POLAR_BEAR, 28);
        mapping.put(ChunkerVanillaEntityType.LLAMA, 29);
        mapping.put(ChunkerVanillaEntityType.PARROT, 30);
        mapping.put(ChunkerVanillaEntityType.DOLPHIN, 31);
        mapping.put(ChunkerVanillaEntityType.ZOMBIE, 32);
        mapping.put(ChunkerVanillaEntityType.CREEPER, 33);
        mapping.put(ChunkerVanillaEntityType.SKELETON, 34);
        mapping.put(ChunkerVanillaEntityType.SPIDER, 35);
        mapping.put(ChunkerVanillaEntityType.ZOMBIFIED_PIGLIN, 36);
        mapping.put(ChunkerVanillaEntityType.SLIME, 37);
        mapping.put(ChunkerVanillaEntityType.ENDERMAN, 38);
        mapping.put(ChunkerVanillaEntityType.SILVERFISH, 39);
        mapping.put(ChunkerVanillaEntityType.CAVE_SPIDER, 40);
        mapping.put(ChunkerVanillaEntityType.GHAST, 41);
        mapping.put(ChunkerVanillaEntityType.MAGMA_CUBE, 42);
        mapping.put(ChunkerVanillaEntityType.BLAZE, 43);
        mapping.put(ChunkerVanillaEntityType.ZOMBIE_VILLAGER, 44); // v1
        mapping.put(ChunkerVanillaEntityType.WITCH, 45);
        mapping.put(ChunkerVanillaEntityType.STRAY, 46);
        mapping.put(ChunkerVanillaEntityType.HUSK, 47);
        mapping.put(ChunkerVanillaEntityType.WITHER_SKELETON, 48);
        mapping.put(ChunkerVanillaEntityType.GUARDIAN, 49);
        mapping.put(ChunkerVanillaEntityType.ELDER_GUARDIAN, 50);
        // 51 is NPC (EDU)
        mapping.put(ChunkerVanillaEntityType.WITHER, 52);
        mapping.put(ChunkerVanillaEntityType.ENDER_DRAGON, 53);
        mapping.put(ChunkerVanillaEntityType.SHULKER, 54);
        mapping.put(ChunkerVanillaEntityType.ENDERMITE, 55);
        // 56 is Agent (EDU)
        mapping.put(ChunkerVanillaEntityType.VINDICATOR, 57);
        mapping.put(ChunkerVanillaEntityType.PHANTOM, 58);
        mapping.put(ChunkerVanillaEntityType.RAVAGER, 59);

        mapping.put(ChunkerVanillaEntityType.ARMOR_STAND, 61);
        // 62 is Tripod Camera (EDU)
        mapping.put(ChunkerVanillaEntityType.PLAYER, 63);
        mapping.put(ChunkerVanillaEntityType.ITEM, 64);
        mapping.put(ChunkerVanillaEntityType.TNT, 65);
        mapping.put(ChunkerVanillaEntityType.FALLING_BLOCK, 66);
        mapping.put(ChunkerVanillaEntityType.MOVING_BLOCK, 67);
        mapping.put(ChunkerVanillaEntityType.EXPERIENCE_BOTTLE, 68);
        mapping.put(ChunkerVanillaEntityType.EXPERIENCE_ORB, 69);
        mapping.put(ChunkerVanillaEntityType.EYE_OF_ENDER, 70);
        mapping.put(ChunkerVanillaEntityType.END_CRYSTAL, 71);
        mapping.put(ChunkerVanillaEntityType.FIREWORK_ROCKET, 72);
        mapping.put(ChunkerVanillaEntityType.TRIDENT, 73);
        mapping.put(ChunkerVanillaEntityType.TURTLE, 74);
        mapping.put(ChunkerVanillaEntityType.CAT, 75);
        mapping.put(ChunkerVanillaEntityType.SHULKER_BULLET, 76);
        mapping.put(ChunkerVanillaEntityType.FISHING_BOBBER, 77);
        // 78 is Chalkboard (EDU)
        mapping.put(ChunkerVanillaEntityType.DRAGON_FIREBALL, 79);
        mapping.put(ChunkerVanillaEntityType.ARROW, 80);
        mapping.put(ChunkerVanillaEntityType.SNOWBALL, 81);
        mapping.put(ChunkerVanillaEntityType.EGG, 82);
        mapping.put(ChunkerVanillaEntityType.PAINTING, 83);
        mapping.put(ChunkerVanillaEntityType.MINECART, 84);
        mapping.put(ChunkerVanillaEntityType.FIREBALL, 85);
        mapping.put(ChunkerVanillaEntityType.POTION, 86);
        mapping.put(ChunkerVanillaEntityType.ENDER_PEARL, 87);
        mapping.put(ChunkerVanillaEntityType.LEASH_KNOT, 88);
        mapping.put(ChunkerVanillaEntityType.WITHER_SKULL, 89);
        mapping.put(ChunkerVanillaEntityType.OAK_BOAT, 90);
        mapping.put(ChunkerVanillaEntityType.WITHER_SKULL_DANGEROUS, 91);

        mapping.put(ChunkerVanillaEntityType.LIGHTNING_BOLT, 93);
        mapping.put(ChunkerVanillaEntityType.SMALL_FIREBALL, 94);
        mapping.put(ChunkerVanillaEntityType.AREA_EFFECT_CLOUD, 95);
        mapping.put(ChunkerVanillaEntityType.HOPPER_MINECART, 96);
        mapping.put(ChunkerVanillaEntityType.TNT_MINECART, 97);
        mapping.put(ChunkerVanillaEntityType.CHEST_MINECART, 98);
        mapping.put(ChunkerVanillaEntityType.FURNACE_MINECART, 99);
        mapping.put(ChunkerVanillaEntityType.COMMAND_BLOCK_MINECART, 100);
        mapping.put(ChunkerVanillaEntityType.LINGERING_POTION, 101);
        mapping.put(ChunkerVanillaEntityType.LLAMA_SPIT, 102);
        mapping.put(ChunkerVanillaEntityType.EVOKER_FANGS, 103);
        mapping.put(ChunkerVanillaEntityType.EVOKER, 104);
        mapping.put(ChunkerVanillaEntityType.VEX, 105);
        // 106 is Ice Bomb (EDU)
        // 107 is Balloon (EDU)
        mapping.put(ChunkerVanillaEntityType.PUFFERFISH, 108);
        mapping.put(ChunkerVanillaEntityType.SALMON, 109);
        mapping.put(ChunkerVanillaEntityType.DROWNED, 110);
        mapping.put(ChunkerVanillaEntityType.TROPICAL_FISH, 111);
        mapping.put(ChunkerVanillaEntityType.COD, 112);
        mapping.put(ChunkerVanillaEntityType.PANDA, 113);
        mapping.put(ChunkerVanillaEntityType.PILLAGER, 114);
        mapping.put(ChunkerVanillaEntityType.VILLAGER, 115); // v2
        mapping.put(ChunkerVanillaEntityType.ZOMBIE_VILLAGER, 116); // v2
        mapping.put(ChunkerVanillaEntityType.SHIELD, 117);
        mapping.put(ChunkerVanillaEntityType.WANDERING_TRADER, 118);
        // 119 is Lectern (unused)

        if (bedrockVersion.isGreaterThanOrEqual(1, 13, 0)) {
            mapping.put(ChunkerVanillaEntityType.ELDER_GUARDIAN_GHOST, 120);
            mapping.put(ChunkerVanillaEntityType.FOX, 121);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 14, 0)) {
            mapping.put(ChunkerVanillaEntityType.BEE, 122);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 0)) {
            mapping.put(ChunkerVanillaEntityType.PIGLIN, 123);
            mapping.put(ChunkerVanillaEntityType.HOGLIN, 124);
            mapping.put(ChunkerVanillaEntityType.STRIDER, 125);
            mapping.put(ChunkerVanillaEntityType.ZOGLIN, 126);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 20)) {
            mapping.put(ChunkerVanillaEntityType.PIGLIN_BRUTE, 127);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 200)) {
            mapping.put(ChunkerVanillaEntityType.GOAT, 128);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 210)) {
            mapping.put(ChunkerVanillaEntityType.GLOW_SQUID, 129);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 17, 0)) {
            mapping.put(ChunkerVanillaEntityType.AXOLOTL, 130);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 18, 0)) {
            mapping.put(ChunkerVanillaEntityType.FROG, 132);
            mapping.put(ChunkerVanillaEntityType.TADPOLE, 133);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 18, 30)) {
            mapping.put(ChunkerVanillaEntityType.WARDEN, 131);
            mapping.put(ChunkerVanillaEntityType.ALLAY, 134);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 19, 0)) {
            mapping.put(ChunkerVanillaEntityType.OAK_CHEST_BOAT, 136);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 19, 10)) {
            mapping.put(ChunkerVanillaEntityType.TRADER_LLAMA, 137);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 19, 50)) {
            mapping.put(ChunkerVanillaEntityType.CAMEL, 138);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 19, 70)) {
            mapping.put(ChunkerVanillaEntityType.SNIFFER, 139);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 20, 60)) {
            mapping.put(ChunkerVanillaEntityType.BREEZE, 140);
            mapping.put(ChunkerVanillaEntityType.BREEZE_WIND_CHARGE_PROJECTILE, 141);
            mapping.put(ChunkerVanillaEntityType.ARMADILLO, 142);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 20, 70)) {
            mapping.put(ChunkerVanillaEntityType.WIND_CHARGE, 143);
            mapping.put(ChunkerVanillaEntityType.BOGGED, 144);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(ChunkerVanillaEntityType.OMINOUS_ITEM_SPAWNER, 145);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 50)) {
            mapping.put(ChunkerVanillaEntityType.CREAKING, 146);
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 80)) {
            mapping.put(ChunkerVanillaEntityType.HAPPY_GHAST, 147);
        }
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
