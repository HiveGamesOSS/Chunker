package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerCustomEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Entity Type resolver, used for Bedrock
 */
public class BedrockEntityTypeResolver implements Resolver<String, ChunkerEntityType> {
    private final InvertibleMap<ChunkerVanillaEntityType, String> mapping = InvertibleMap.enumKeys(ChunkerVanillaEntityType.class);
    private final boolean customIdentifierSupported;

    /**
     * Create a new bedrock entity type resolver.
     *
     * @param bedrockVersion            the game version being used, as certain entities are only available after specific versions.
     * @param customIdentifierSupported whether custom identifiers should be passed through as
     *                                  ChunkerCustomEntityType.
     */
    public BedrockEntityTypeResolver(Version bedrockVersion, boolean customIdentifierSupported) {
        this.customIdentifierSupported = customIdentifierSupported;

        mapping.put(ChunkerVanillaEntityType.CHICKEN, "minecraft:chicken");
        mapping.put(ChunkerVanillaEntityType.COW, "minecraft:cow");
        mapping.put(ChunkerVanillaEntityType.PIG, "minecraft:pig");
        mapping.put(ChunkerVanillaEntityType.SHEEP, "minecraft:sheep");
        mapping.put(ChunkerVanillaEntityType.WOLF, "minecraft:wolf");
        mapping.put(ChunkerVanillaEntityType.VILLAGER, "minecraft:villager"); // v1
        mapping.put(ChunkerVanillaEntityType.MOOSHROOM, "minecraft:mooshroom");
        mapping.put(ChunkerVanillaEntityType.SQUID, "minecraft:squid");
        mapping.put(ChunkerVanillaEntityType.RABBIT, "minecraft:rabbit");
        mapping.put(ChunkerVanillaEntityType.BAT, "minecraft:bat");
        mapping.put(ChunkerVanillaEntityType.IRON_GOLEM, "minecraft:iron_golem");
        mapping.put(ChunkerVanillaEntityType.SNOW_GOLEM, "minecraft:snow_golem");
        mapping.put(ChunkerVanillaEntityType.OCELOT, "minecraft:ocelot");
        mapping.put(ChunkerVanillaEntityType.HORSE, "minecraft:horse");
        mapping.put(ChunkerVanillaEntityType.DONKEY, "minecraft:donkey");
        mapping.put(ChunkerVanillaEntityType.MULE, "minecraft:mule");
        mapping.put(ChunkerVanillaEntityType.SKELETON_HORSE, "minecraft:skeleton_horse");
        mapping.put(ChunkerVanillaEntityType.ZOMBIE_HORSE, "minecraft:zombie_horse");
        mapping.put(ChunkerVanillaEntityType.POLAR_BEAR, "minecraft:polar_bear");
        mapping.put(ChunkerVanillaEntityType.LLAMA, "minecraft:llama");
        mapping.put(ChunkerVanillaEntityType.PARROT, "minecraft:parrot");
        mapping.put(ChunkerVanillaEntityType.DOLPHIN, "minecraft:dolphin");
        mapping.put(ChunkerVanillaEntityType.ZOMBIE, "minecraft:zombie");
        mapping.put(ChunkerVanillaEntityType.CREEPER, "minecraft:creeper");
        mapping.put(ChunkerVanillaEntityType.SKELETON, "minecraft:skeleton");
        mapping.put(ChunkerVanillaEntityType.SPIDER, "minecraft:spider");
        mapping.put(ChunkerVanillaEntityType.ZOMBIFIED_PIGLIN, "minecraft:zombie_pigman");
        mapping.put(ChunkerVanillaEntityType.SLIME, "minecraft:slime");
        mapping.put(ChunkerVanillaEntityType.ENDERMAN, "minecraft:enderman");
        mapping.put(ChunkerVanillaEntityType.SILVERFISH, "minecraft:silverfish");
        mapping.put(ChunkerVanillaEntityType.CAVE_SPIDER, "minecraft:cave_spider");
        mapping.put(ChunkerVanillaEntityType.GHAST, "minecraft:ghast");
        mapping.put(ChunkerVanillaEntityType.MAGMA_CUBE, "minecraft:magma_cube");
        mapping.put(ChunkerVanillaEntityType.BLAZE, "minecraft:blaze");
        mapping.put(ChunkerVanillaEntityType.ZOMBIE_VILLAGER, "minecraft:zombie_villager"); // v1
        mapping.put(ChunkerVanillaEntityType.WITCH, "minecraft:witch");
        mapping.put(ChunkerVanillaEntityType.STRAY, "minecraft:stray");
        mapping.put(ChunkerVanillaEntityType.HUSK, "minecraft:husk");
        mapping.put(ChunkerVanillaEntityType.WITHER_SKELETON, "minecraft:wither_skeleton");
        mapping.put(ChunkerVanillaEntityType.GUARDIAN, "minecraft:guardian");
        mapping.put(ChunkerVanillaEntityType.ELDER_GUARDIAN, "minecraft:elder_guardian");
        // 51 is NPC (EDU)
        mapping.put(ChunkerVanillaEntityType.WITHER, "minecraft:wither");
        mapping.put(ChunkerVanillaEntityType.ENDER_DRAGON, "minecraft:ender_dragon");
        mapping.put(ChunkerVanillaEntityType.SHULKER, "minecraft:shulker");
        mapping.put(ChunkerVanillaEntityType.ENDERMITE, "minecraft:endermite");
        // 56 is Agent (EDU)
        mapping.put(ChunkerVanillaEntityType.VINDICATOR, "minecraft:vindicator");
        mapping.put(ChunkerVanillaEntityType.PHANTOM, "minecraft:phantom");
        mapping.put(ChunkerVanillaEntityType.RAVAGER, "minecraft:ravager");

        mapping.put(ChunkerVanillaEntityType.ARMOR_STAND, "minecraft:armor_stand");
        // 62 is Tripod Camera (EDU)
        mapping.put(ChunkerVanillaEntityType.PLAYER, "minecraft:player");
        mapping.put(ChunkerVanillaEntityType.ITEM, "minecraft:item");
        mapping.put(ChunkerVanillaEntityType.TNT, "minecraft:tnt");
        mapping.put(ChunkerVanillaEntityType.FALLING_BLOCK, "minecraft:falling_block");
        mapping.put(ChunkerVanillaEntityType.MOVING_BLOCK, "minecraft:moving_block");
        mapping.put(ChunkerVanillaEntityType.EXPERIENCE_BOTTLE, "minecraft:xp_bottle");
        mapping.put(ChunkerVanillaEntityType.EXPERIENCE_ORB, "minecraft:xp_orb");
        mapping.put(ChunkerVanillaEntityType.EYE_OF_ENDER, "minecraft:eye_of_ender_signal");
        mapping.put(ChunkerVanillaEntityType.END_CRYSTAL, "minecraft:ender_crystal");
        mapping.put(ChunkerVanillaEntityType.FIREWORK_ROCKET, "minecraft:fireworks_rocket");
        mapping.put(ChunkerVanillaEntityType.TRIDENT, "minecraft:thrown_trident");
        mapping.put(ChunkerVanillaEntityType.TURTLE, "minecraft:turtle");
        mapping.put(ChunkerVanillaEntityType.CAT, "minecraft:cat");
        mapping.put(ChunkerVanillaEntityType.SHULKER_BULLET, "minecraft:shulker_bullet");
        mapping.put(ChunkerVanillaEntityType.FISHING_BOBBER, "minecraft:fishing_hook");
        // 78 is Chalkboard (EDU)
        mapping.put(ChunkerVanillaEntityType.DRAGON_FIREBALL, "minecraft:dragon_fireball");
        mapping.put(ChunkerVanillaEntityType.ARROW, "minecraft:arrow");
        mapping.put(ChunkerVanillaEntityType.SNOWBALL, "minecraft:snowball");
        mapping.put(ChunkerVanillaEntityType.EGG, "minecraft:egg");
        mapping.put(ChunkerVanillaEntityType.PAINTING, "minecraft:painting");
        mapping.put(ChunkerVanillaEntityType.MINECART, "minecraft:minecart");
        mapping.put(ChunkerVanillaEntityType.FIREBALL, "minecraft:fireball");
        mapping.put(ChunkerVanillaEntityType.POTION, "minecraft:splash_potion");
        mapping.put(ChunkerVanillaEntityType.ENDER_PEARL, "minecraft:ender_pearl");
        mapping.put(ChunkerVanillaEntityType.LEASH_KNOT, "minecraft:leash_knot");
        mapping.put(ChunkerVanillaEntityType.WITHER_SKULL, "minecraft:wither_skull");
        mapping.put(ChunkerVanillaEntityType.OAK_BOAT, "minecraft:boat");
        mapping.put(ChunkerVanillaEntityType.WITHER_SKULL_DANGEROUS, "minecraft:wither_skull_dangerous");

        mapping.put(ChunkerVanillaEntityType.LIGHTNING_BOLT, "minecraft:lightning_bolt");
        mapping.put(ChunkerVanillaEntityType.SMALL_FIREBALL, "minecraft:small_fireball");
        mapping.put(ChunkerVanillaEntityType.AREA_EFFECT_CLOUD, "minecraft:area_effect_cloud");
        mapping.put(ChunkerVanillaEntityType.HOPPER_MINECART, "minecraft:hopper_minecart");
        mapping.put(ChunkerVanillaEntityType.TNT_MINECART, "minecraft:tnt_minecart");
        mapping.put(ChunkerVanillaEntityType.CHEST_MINECART, "minecraft:chest_minecart");
        mapping.put(ChunkerVanillaEntityType.FURNACE_MINECART, "minecraft:furnace_minecart");
        mapping.put(ChunkerVanillaEntityType.COMMAND_BLOCK_MINECART, "minecraft:command_block_minecart");
        mapping.put(ChunkerVanillaEntityType.LINGERING_POTION, "minecraft:lingering_potion");
        mapping.put(ChunkerVanillaEntityType.LLAMA_SPIT, "minecraft:llama_spit");
        mapping.put(ChunkerVanillaEntityType.EVOKER_FANGS, "minecraft:evocation_fang");
        mapping.put(ChunkerVanillaEntityType.EVOKER, "minecraft:evocation_illager");
        mapping.put(ChunkerVanillaEntityType.VEX, "minecraft:vex");
        // 106 is Ice Bomb (EDU)
        // 107 is Balloon (EDU)
        mapping.put(ChunkerVanillaEntityType.PUFFERFISH, "minecraft:pufferfish");
        mapping.put(ChunkerVanillaEntityType.SALMON, "minecraft:salmon");
        mapping.put(ChunkerVanillaEntityType.DROWNED, "minecraft:drowned");
        mapping.put(ChunkerVanillaEntityType.TROPICAL_FISH, "minecraft:tropicalfish");
        mapping.put(ChunkerVanillaEntityType.COD, "minecraft:cod");
        mapping.put(ChunkerVanillaEntityType.PANDA, "minecraft:panda");
        mapping.put(ChunkerVanillaEntityType.PILLAGER, "minecraft:pillager");
        mapping.put(ChunkerVanillaEntityType.VILLAGER, "minecraft:villager_v2"); // v2
        mapping.put(ChunkerVanillaEntityType.ZOMBIE_VILLAGER, "minecraft:zombie_villager_v2"); // v2
        mapping.put(ChunkerVanillaEntityType.SHIELD, "minecraft:shield");
        mapping.put(ChunkerVanillaEntityType.WANDERING_TRADER, "minecraft:wandering_trader");
        // 119 is Lectern (unused)

        if (bedrockVersion.isGreaterThanOrEqual(1, 13, 0)) {
            mapping.put(ChunkerVanillaEntityType.ELDER_GUARDIAN_GHOST, "minecraft:elder_guardian_ghost");
            mapping.put(ChunkerVanillaEntityType.FOX, "minecraft:fox");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 14, 0)) {
            mapping.put(ChunkerVanillaEntityType.BEE, "minecraft:bee");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 0)) {
            mapping.put(ChunkerVanillaEntityType.PIGLIN, "minecraft:piglin");
            mapping.put(ChunkerVanillaEntityType.HOGLIN, "minecraft:hoglin");
            mapping.put(ChunkerVanillaEntityType.STRIDER, "minecraft:strider");
            mapping.put(ChunkerVanillaEntityType.ZOGLIN, "minecraft:zoglin");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 20)) {
            mapping.put(ChunkerVanillaEntityType.PIGLIN_BRUTE, "minecraft:piglin_brute");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 200)) {
            mapping.put(ChunkerVanillaEntityType.GOAT, "minecraft:goat");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 16, 210)) {
            mapping.put(ChunkerVanillaEntityType.GLOW_SQUID, "minecraft:glow_squid");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 17, 0)) {
            mapping.put(ChunkerVanillaEntityType.AXOLOTL, "minecraft:axolotl");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 18, 0)) {
            mapping.put(ChunkerVanillaEntityType.FROG, "minecraft:frog");
            mapping.put(ChunkerVanillaEntityType.TADPOLE, "minecraft:tadpole");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 18, 30)) {
            mapping.put(ChunkerVanillaEntityType.WARDEN, "minecraft:warden");
            mapping.put(ChunkerVanillaEntityType.ALLAY, "minecraft:allay");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 19, 0)) {
            mapping.put(ChunkerVanillaEntityType.OAK_CHEST_BOAT, "minecraft:chest_boat");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 19, 10)) {
            mapping.put(ChunkerVanillaEntityType.TRADER_LLAMA, "minecraft:trader_llama");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 19, 50)) {
            mapping.put(ChunkerVanillaEntityType.CAMEL, "minecraft:camel");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 19, 70)) {
            mapping.put(ChunkerVanillaEntityType.SNIFFER, "minecraft:sniffer");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 20, 60)) {
            mapping.put(ChunkerVanillaEntityType.ARMADILLO, "minecraft:armadillo");
            mapping.put(ChunkerVanillaEntityType.BREEZE, "minecraft:breeze");
            mapping.put(ChunkerVanillaEntityType.BREEZE_WIND_CHARGE_PROJECTILE, "minecraft:breeze_wind_charge_projectile");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 20, 70)) {
            mapping.put(ChunkerVanillaEntityType.WIND_CHARGE, "minecraft:wind_charge_projectile");
            mapping.put(ChunkerVanillaEntityType.BOGGED, "minecraft:bogged");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(ChunkerVanillaEntityType.OMINOUS_ITEM_SPAWNER, "minecraft:ominous_item_spawner");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 50)) {
            mapping.put(ChunkerVanillaEntityType.CREAKING, "minecraft:creaking");
        }
        if (bedrockVersion.isGreaterThanOrEqual(1, 21, 80)) {
            mapping.put(ChunkerVanillaEntityType.HAPPY_GHAST, "minecraft:happy_ghast");
        }
    }

    @Override
    public Optional<String> from(ChunkerEntityType input) {
        if (input instanceof ChunkerCustomEntityType chunkerCustomEntityType) {
            if (customIdentifierSupported) {
                return Optional.ofNullable(chunkerCustomEntityType.getIdentifier());
            } else {
                // No possible mapping
                return Optional.empty();
            }
        } else if (input instanceof ChunkerVanillaEntityType chunkerVanillaEntityType) {
            return Optional.ofNullable(mapping.forward().get(chunkerVanillaEntityType));
        } else {
            // No possible mapping
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChunkerEntityType> to(String input) {
        // Ensure namespace is present
        if (!input.contains(":")) {
            input = "minecraft:" + input;
        } else if (!input.startsWith("minecraft:") && customIdentifierSupported) {
            // Custom entity type if it's supported
            return Optional.of(new ChunkerCustomEntityType(input));
        }
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
