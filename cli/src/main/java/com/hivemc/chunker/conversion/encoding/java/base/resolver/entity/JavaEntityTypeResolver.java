package com.hivemc.chunker.conversion.encoding.java.base.resolver.entity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerCustomEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Entity Type resolver, used for Java
 */
public class JavaEntityTypeResolver implements Resolver<String, ChunkerEntityType> {
    private final InvertibleMap<ChunkerVanillaEntityType, String> mapping = InvertibleMap.enumKeys(ChunkerVanillaEntityType.class);
    private final boolean customIdentifierSupported;

    /**
     * Create a new java entity type resolver.
     *
     * @param version                   the game version being used, as certain entities are only available after specific versions.
     * @param customIdentifierSupported whether custom identifiers should be passed through as
     *                                  ChunkerCustomEntityType.
     */
    public JavaEntityTypeResolver(Version version, boolean customIdentifierSupported) {
        this.customIdentifierSupported = customIdentifierSupported;

        mapping.put(ChunkerVanillaEntityType.AREA_EFFECT_CLOUD, "minecraft:area_effect_cloud");
        mapping.put(ChunkerVanillaEntityType.ARMOR_STAND, "minecraft:armor_stand");
        mapping.put(ChunkerVanillaEntityType.ARROW, "minecraft:arrow");
        mapping.put(ChunkerVanillaEntityType.BAT, "minecraft:bat");
        mapping.put(ChunkerVanillaEntityType.BLAZE, "minecraft:blaze");
        mapping.put(ChunkerVanillaEntityType.OAK_BOAT, "minecraft:boat");
        mapping.put(ChunkerVanillaEntityType.CAVE_SPIDER, "minecraft:cave_spider");
        mapping.put(ChunkerVanillaEntityType.OAK_CHEST_BOAT, "minecraft:chest_boat");
        mapping.put(ChunkerVanillaEntityType.CHEST_MINECART, "minecraft:chest_minecart");
        mapping.put(ChunkerVanillaEntityType.CHICKEN, "minecraft:chicken");
        mapping.put(ChunkerVanillaEntityType.COMMAND_BLOCK_MINECART, "minecraft:commandblock_minecart");
        mapping.put(ChunkerVanillaEntityType.COW, "minecraft:cow");
        mapping.put(ChunkerVanillaEntityType.CREEPER, "minecraft:creeper");
        mapping.put(ChunkerVanillaEntityType.DONKEY, "minecraft:donkey");
        mapping.put(ChunkerVanillaEntityType.DRAGON_FIREBALL, "minecraft:dragon_fireball");
        mapping.put(ChunkerVanillaEntityType.EGG, "minecraft:egg");
        mapping.put(ChunkerVanillaEntityType.ELDER_GUARDIAN, "minecraft:elder_guardian");
        mapping.put(ChunkerVanillaEntityType.END_CRYSTAL, "minecraft:ender_crystal");
        mapping.put(ChunkerVanillaEntityType.ENDER_DRAGON, "minecraft:ender_dragon");
        mapping.put(ChunkerVanillaEntityType.ENDER_PEARL, "minecraft:ender_pearl");
        mapping.put(ChunkerVanillaEntityType.ENDERMAN, "minecraft:enderman");
        mapping.put(ChunkerVanillaEntityType.ENDERMITE, "minecraft:endermite");
        mapping.put(ChunkerVanillaEntityType.EXPERIENCE_BOTTLE, "minecraft:xp_bottle");
        mapping.put(ChunkerVanillaEntityType.EXPERIENCE_ORB, "minecraft:xp_orb");
        mapping.put(ChunkerVanillaEntityType.EYE_OF_ENDER, "minecraft:eye_of_ender_signal");
        mapping.put(ChunkerVanillaEntityType.FALLING_BLOCK, "minecraft:falling_block");
        mapping.put(ChunkerVanillaEntityType.FIREWORK_ROCKET, "minecraft:fireworks_rocket");
        mapping.put(ChunkerVanillaEntityType.FURNACE_MINECART, "minecraft:furnace_minecart");
        mapping.put(ChunkerVanillaEntityType.GHAST, "minecraft:ghast");
        mapping.put(ChunkerVanillaEntityType.GIANT, "minecraft:giant");
        mapping.put(ChunkerVanillaEntityType.GUARDIAN, "minecraft:guardian");
        mapping.put(ChunkerVanillaEntityType.HOPPER_MINECART, "minecraft:hopper_minecart");
        mapping.put(ChunkerVanillaEntityType.HORSE, "minecraft:horse");
        mapping.put(ChunkerVanillaEntityType.IRON_GOLEM, "minecraft:villager_golem");
        mapping.put(ChunkerVanillaEntityType.ITEM, "minecraft:item");
        mapping.put(ChunkerVanillaEntityType.ITEM_FRAME, "minecraft:item_frame");
        mapping.put(ChunkerVanillaEntityType.FIREBALL, "minecraft:fireball");
        mapping.put(ChunkerVanillaEntityType.LEASH_KNOT, "minecraft:leash_knot");
        mapping.put(ChunkerVanillaEntityType.LIGHTNING_BOLT, "minecraft:lightning_bolt");
        mapping.put(ChunkerVanillaEntityType.MAGMA_CUBE, "minecraft:magma_cube");
        mapping.put(ChunkerVanillaEntityType.MARKER, "minecraft:marker");
        mapping.put(ChunkerVanillaEntityType.MINECART, "minecraft:minecart");
        mapping.put(ChunkerVanillaEntityType.MOOSHROOM, "minecraft:mooshroom");
        mapping.put(ChunkerVanillaEntityType.MULE, "minecraft:mule");
        mapping.put(ChunkerVanillaEntityType.OCELOT, "minecraft:ocelot");
        mapping.put(ChunkerVanillaEntityType.PAINTING, "minecraft:painting");
        mapping.put(ChunkerVanillaEntityType.PIG, "minecraft:pig");
        mapping.put(ChunkerVanillaEntityType.POTION, "minecraft:potion");
        mapping.put(ChunkerVanillaEntityType.RABBIT, "minecraft:rabbit");
        mapping.put(ChunkerVanillaEntityType.SHEEP, "minecraft:sheep");
        mapping.put(ChunkerVanillaEntityType.SILVERFISH, "minecraft:silverfish");
        mapping.put(ChunkerVanillaEntityType.SKELETON, "minecraft:skeleton");
        mapping.put(ChunkerVanillaEntityType.SKELETON_HORSE, "minecraft:skeleton_horse");
        mapping.put(ChunkerVanillaEntityType.SLIME, "minecraft:slime");
        mapping.put(ChunkerVanillaEntityType.SMALL_FIREBALL, "minecraft:small_fireball");
        mapping.put(ChunkerVanillaEntityType.SNOW_GOLEM, "minecraft:snowman");
        mapping.put(ChunkerVanillaEntityType.SNOWBALL, "minecraft:snowball");
        mapping.put(ChunkerVanillaEntityType.SPAWNER_MINECART, "minecraft:spawner_minecart");
        mapping.put(ChunkerVanillaEntityType.SPECTRAL_ARROW, "minecraft:spectral_arrow");
        mapping.put(ChunkerVanillaEntityType.SPIDER, "minecraft:spider");
        mapping.put(ChunkerVanillaEntityType.SQUID, "minecraft:squid");
        mapping.put(ChunkerVanillaEntityType.TNT, "minecraft:tnt");
        mapping.put(ChunkerVanillaEntityType.TNT_MINECART, "minecraft:tnt_minecart");
        mapping.put(ChunkerVanillaEntityType.TRIDENT, "minecraft:trident");
        mapping.put(ChunkerVanillaEntityType.VILLAGER, "minecraft:villager");
        mapping.put(ChunkerVanillaEntityType.WITCH, "minecraft:witch");
        mapping.put(ChunkerVanillaEntityType.WITHER, "minecraft:wither");
        mapping.put(ChunkerVanillaEntityType.WITHER_SKELETON, "minecraft:wither_skeleton");
        mapping.put(ChunkerVanillaEntityType.WITHER_SKULL, "minecraft:wither_skull");
        mapping.put(ChunkerVanillaEntityType.WOLF, "minecraft:wolf");
        mapping.put(ChunkerVanillaEntityType.ZOMBIE, "minecraft:zombie");
        mapping.put(ChunkerVanillaEntityType.ZOMBIE_HORSE, "minecraft:zombie_horse");
        mapping.put(ChunkerVanillaEntityType.ZOMBIFIED_PIGLIN, "minecraft:zombie_pigman");
        mapping.put(ChunkerVanillaEntityType.ZOMBIE_VILLAGER, "minecraft:zombie_villager");
        mapping.put(ChunkerVanillaEntityType.PLAYER, "minecraft:player");
        mapping.put(ChunkerVanillaEntityType.FISHING_BOBBER, "minecraft:fishing_bobber");

        if (version.isGreaterThanOrEqual(1, 9, 0)) {
            mapping.put(ChunkerVanillaEntityType.SHULKER, "minecraft:shulker");
            mapping.put(ChunkerVanillaEntityType.SHULKER_BULLET, "minecraft:shulker_bullet");
        }
        if (version.isGreaterThanOrEqual(1, 10, 0)) {
            mapping.put(ChunkerVanillaEntityType.HUSK, "minecraft:husk");
            mapping.put(ChunkerVanillaEntityType.POLAR_BEAR, "minecraft:polar_bear");
            mapping.put(ChunkerVanillaEntityType.STRAY, "minecraft:stray");
        }
        if (version.isGreaterThanOrEqual(1, 11, 0)) {
            mapping.put(ChunkerVanillaEntityType.LLAMA, "minecraft:llama");
            mapping.put(ChunkerVanillaEntityType.LLAMA_SPIT, "minecraft:llama_spit");
            mapping.put(ChunkerVanillaEntityType.VINDICATOR, "minecraft:vindication_illager");
            mapping.put(ChunkerVanillaEntityType.EVOKER, "minecraft:evocation_illager");
            mapping.put(ChunkerVanillaEntityType.EVOKER_FANGS, "minecraft:evocation_fangs");
            mapping.put(ChunkerVanillaEntityType.VEX, "minecraft:vex");
        }
        if (version.isGreaterThanOrEqual(1, 12, 0)) {
            mapping.put(ChunkerVanillaEntityType.PARROT, "minecraft:parrot");
            mapping.put(ChunkerVanillaEntityType.ILLUSIONER, "minecraft:illusion_illager");
        }
        if (version.isGreaterThanOrEqual(1, 13, 0)) {
            // Renamed entities
            mapping.put(ChunkerVanillaEntityType.COMMAND_BLOCK_MINECART, "minecraft:command_block_minecart");
            mapping.put(ChunkerVanillaEntityType.END_CRYSTAL, "minecraft:end_crystal");
            mapping.put(ChunkerVanillaEntityType.SNOW_GOLEM, "minecraft:snow_golem");
            mapping.put(ChunkerVanillaEntityType.EVOKER, "minecraft:evoker");
            mapping.put(ChunkerVanillaEntityType.EVOKER_FANGS, "minecraft:evoker_fangs");
            mapping.put(ChunkerVanillaEntityType.ILLUSIONER, "minecraft:illusioner");
            mapping.put(ChunkerVanillaEntityType.VINDICATOR, "minecraft:vindicator");
            mapping.put(ChunkerVanillaEntityType.IRON_GOLEM, "minecraft:iron_golem");
            mapping.put(ChunkerVanillaEntityType.EXPERIENCE_ORB, "minecraft:experience_orb");
            mapping.put(ChunkerVanillaEntityType.EXPERIENCE_BOTTLE, "minecraft:experience_bottle");
            mapping.put(ChunkerVanillaEntityType.EYE_OF_ENDER, "minecraft:eye_of_ender");
            mapping.put(ChunkerVanillaEntityType.FIREWORK_ROCKET, "minecraft:firework_rocket");

            // New entities
            mapping.put(ChunkerVanillaEntityType.PHANTOM, "minecraft:phantom");
            mapping.put(ChunkerVanillaEntityType.TURTLE, "minecraft:turtle");
            mapping.put(ChunkerVanillaEntityType.COD, "minecraft:cod");
            mapping.put(ChunkerVanillaEntityType.SALMON, "minecraft:salmon");
            mapping.put(ChunkerVanillaEntityType.PUFFERFISH, "minecraft:pufferfish");
            mapping.put(ChunkerVanillaEntityType.TROPICAL_FISH, "minecraft:tropical_fish");
            mapping.put(ChunkerVanillaEntityType.DROWNED, "minecraft:drowned");
            mapping.put(ChunkerVanillaEntityType.DOLPHIN, "minecraft:dolphin");
        }
        if (version.isGreaterThanOrEqual(1, 14, 0)) {
            mapping.put(ChunkerVanillaEntityType.PANDA, "minecraft:panda");
            mapping.put(ChunkerVanillaEntityType.PILLAGER, "minecraft:pillager");
            mapping.put(ChunkerVanillaEntityType.RAVAGER, "minecraft:ravager");
            mapping.put(ChunkerVanillaEntityType.CAT, "minecraft:cat");
            mapping.put(ChunkerVanillaEntityType.TRADER_LLAMA, "minecraft:trader_llama");
            mapping.put(ChunkerVanillaEntityType.WANDERING_TRADER, "minecraft:wandering_trader");
            mapping.put(ChunkerVanillaEntityType.FOX, "minecraft:fox");
        }
        if (version.isGreaterThanOrEqual(1, 15, 0)) {
            mapping.put(ChunkerVanillaEntityType.BEE, "minecraft:bee");
        }
        if (version.isGreaterThanOrEqual(1, 16, 0)) {
            mapping.put(ChunkerVanillaEntityType.PIGLIN, "minecraft:piglin");
            mapping.put(ChunkerVanillaEntityType.HOGLIN, "minecraft:hoglin");
            mapping.put(ChunkerVanillaEntityType.ZOMBIFIED_PIGLIN, "minecraft:zombified_piglin");
            mapping.put(ChunkerVanillaEntityType.STRIDER, "minecraft:strider");
            mapping.put(ChunkerVanillaEntityType.ZOGLIN, "minecraft:zoglin");
        }
        if (version.isGreaterThanOrEqual(1, 16, 2)) {
            mapping.put(ChunkerVanillaEntityType.PIGLIN_BRUTE, "minecraft:piglin_brute");
        }
        if (version.isGreaterThanOrEqual(1, 17, 0)) {
            mapping.put(ChunkerVanillaEntityType.AXOLOTL, "minecraft:axolotl");
            mapping.put(ChunkerVanillaEntityType.GLOW_SQUID, "minecraft:glow_squid");
            mapping.put(ChunkerVanillaEntityType.GLOW_ITEM_FRAME, "minecraft:glow_item_frame");
            mapping.put(ChunkerVanillaEntityType.GOAT, "minecraft:goat");
        }
        if (version.isGreaterThanOrEqual(1, 19, 0)) {
            mapping.put(ChunkerVanillaEntityType.WARDEN, "minecraft:warden");
            mapping.put(ChunkerVanillaEntityType.TADPOLE, "minecraft:tadpole");
            mapping.put(ChunkerVanillaEntityType.FROG, "minecraft:frog");
            mapping.put(ChunkerVanillaEntityType.ALLAY, "minecraft:allay");
        }
        if (version.isGreaterThanOrEqual(1, 19, 3)) {
            mapping.put(ChunkerVanillaEntityType.CAMEL, "minecraft:camel");
        }
        if (version.isGreaterThanOrEqual(1, 19, 4)) {
            mapping.put(ChunkerVanillaEntityType.INTERACTION, "minecraft:interaction");
            mapping.put(ChunkerVanillaEntityType.ITEM_DISPLAY, "minecraft:item_display");
            mapping.put(ChunkerVanillaEntityType.TEXT_DISPLAY, "minecraft:text_display");
            mapping.put(ChunkerVanillaEntityType.BLOCK_DISPLAY, "minecraft:block_display");
            mapping.put(ChunkerVanillaEntityType.SNIFFER, "minecraft:sniffer");
        }
        if (version.isGreaterThanOrEqual(1, 20, 3)) {
            mapping.put(ChunkerVanillaEntityType.BREEZE, "minecraft:breeze");
            mapping.put(ChunkerVanillaEntityType.BREEZE_WIND_CHARGE_PROJECTILE, "minecraft:breeze_wind_charge");
        }
        if (version.isGreaterThanOrEqual(1, 20, 5)) {
            mapping.put(ChunkerVanillaEntityType.WIND_CHARGE, "minecraft:wind_charge");
            mapping.put(ChunkerVanillaEntityType.ARMADILLO, "minecraft:armadillo");
            mapping.put(ChunkerVanillaEntityType.BOGGED, "minecraft:bogged");
        }
        if (version.isGreaterThanOrEqual(1, 20, 5)) {
            mapping.put(ChunkerVanillaEntityType.WIND_CHARGE, "minecraft:wind_charge");
            mapping.put(ChunkerVanillaEntityType.ARMADILLO, "minecraft:armadillo");
            mapping.put(ChunkerVanillaEntityType.BOGGED, "minecraft:bogged");
            mapping.put(ChunkerVanillaEntityType.OMINOUS_ITEM_SPAWNER, "minecraft:ominous_item_spawner");
        }
        if (version.isGreaterThanOrEqual(1, 21, 2)) {
            mapping.put(ChunkerVanillaEntityType.CREAKING, "minecraft:creaking");

            // Boats were flattened into separate entities
            mapping.put(ChunkerVanillaEntityType.OAK_BOAT, "minecraft:oak_boat");
            mapping.put(ChunkerVanillaEntityType.SPRUCE_BOAT, "minecraft:spruce_boat");
            mapping.put(ChunkerVanillaEntityType.BIRCH_BOAT, "minecraft:birch_boat");
            mapping.put(ChunkerVanillaEntityType.JUNGLE_BOAT, "minecraft:jungle_boat");
            mapping.put(ChunkerVanillaEntityType.ACACIA_BOAT, "minecraft:acacia_boat");
            mapping.put(ChunkerVanillaEntityType.CHERRY_BOAT, "minecraft:cherry_boat");
            mapping.put(ChunkerVanillaEntityType.DARK_OAK_BOAT, "minecraft:dark_oak_boat");
            mapping.put(ChunkerVanillaEntityType.PALE_OAK_BOAT, "minecraft:pale_oak_boat");
            mapping.put(ChunkerVanillaEntityType.MANGROVE_BOAT, "minecraft:mangrove_boat");
            mapping.put(ChunkerVanillaEntityType.BAMBOO_RAFT, "minecraft:bamboo_raft");
            mapping.put(ChunkerVanillaEntityType.OAK_CHEST_BOAT, "minecraft:oak_chest_boat");
            mapping.put(ChunkerVanillaEntityType.SPRUCE_CHEST_BOAT, "minecraft:spruce_chest_boat");
            mapping.put(ChunkerVanillaEntityType.BIRCH_CHEST_BOAT, "minecraft:birch_chest_boat");
            mapping.put(ChunkerVanillaEntityType.JUNGLE_CHEST_BOAT, "minecraft:jungle_chest_boat");
            mapping.put(ChunkerVanillaEntityType.ACACIA_CHEST_BOAT, "minecraft:acacia_chest_boat");
            mapping.put(ChunkerVanillaEntityType.CHERRY_CHEST_BOAT, "minecraft:cherry_chest_boat");
            mapping.put(ChunkerVanillaEntityType.DARK_OAK_CHEST_BOAT, "minecraft:dark_oak_chest_boat");
            mapping.put(ChunkerVanillaEntityType.PALE_OAK_CHEST_BOAT, "minecraft:pale_oak_chest_boat");
            mapping.put(ChunkerVanillaEntityType.MANGROVE_CHEST_BOAT, "minecraft:mangrove_chest_boat");
            mapping.put(ChunkerVanillaEntityType.BAMBOO_CHEST_RAFT, "minecraft:bamboo_chest_raft");
        }
        if (version.isGreaterThanOrEqual(1, 21, 5)) {
            // Potion was flattened
            mapping.put(ChunkerVanillaEntityType.LINGERING_POTION, "minecraft:lingering_potion");
            mapping.put(ChunkerVanillaEntityType.POTION, "minecraft:splash_potion");
        }
        if (version.isGreaterThanOrEqual(1, 21, 6)) {
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
