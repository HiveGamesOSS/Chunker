package com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Legacy entity type resolver, used for Java
 */
public class JavaLegacyEntityTypeResolver implements Resolver<String, ChunkerEntityType> {
    private final InvertibleMap<ChunkerVanillaEntityType, String> mapping = InvertibleMap.enumKeys(ChunkerVanillaEntityType.class);

    /**
     * Create a new java entity type resolver.
     *
     * @param version the game version being used, as certain entities are only available after specific versions.
     */
    public JavaLegacyEntityTypeResolver(Version version) {
        mapping.put(ChunkerVanillaEntityType.AREA_EFFECT_CLOUD, "AreaEffectCloud");
        mapping.put(ChunkerVanillaEntityType.ARMOR_STAND, "ArmorStand");
        mapping.put(ChunkerVanillaEntityType.ARROW, "Arrow");
        mapping.put(ChunkerVanillaEntityType.BAT, "Bat");
        mapping.put(ChunkerVanillaEntityType.BLAZE, "Blaze");
        mapping.put(ChunkerVanillaEntityType.OAK_BOAT, "Boat");
        mapping.put(ChunkerVanillaEntityType.CAVE_SPIDER, "CaveSpider");
        mapping.put(ChunkerVanillaEntityType.CHICKEN, "Chicken");
        mapping.put(ChunkerVanillaEntityType.COW, "Cow");
        mapping.put(ChunkerVanillaEntityType.CREEPER, "Creeper");
        mapping.put(ChunkerVanillaEntityType.DONKEY, "Donkey");
        mapping.put(ChunkerVanillaEntityType.DRAGON_FIREBALL, "DragonFireball");
        mapping.put(ChunkerVanillaEntityType.ELDER_GUARDIAN, "ElderGuardian");
        mapping.put(ChunkerVanillaEntityType.END_CRYSTAL, "EnderCrystal");
        mapping.put(ChunkerVanillaEntityType.ENDER_DRAGON, "EnderDragon");
        mapping.put(ChunkerVanillaEntityType.ENDERMAN, "Enderman");
        mapping.put(ChunkerVanillaEntityType.ENDERMITE, "Endermite");
        mapping.put(ChunkerVanillaEntityType.EYE_OF_ENDER, "EyeOfEnderSignal");
        mapping.put(ChunkerVanillaEntityType.FALLING_BLOCK, "FallingSand");
        mapping.put(ChunkerVanillaEntityType.FIREBALL, "Fireball");
        mapping.put(ChunkerVanillaEntityType.FIREWORK_ROCKET, "FireworksRocketEntity");
        mapping.put(ChunkerVanillaEntityType.GHAST, "Ghast");
        mapping.put(ChunkerVanillaEntityType.GIANT, "Giant");
        mapping.put(ChunkerVanillaEntityType.GUARDIAN, "Guardian");
        mapping.put(ChunkerVanillaEntityType.HORSE, "EntityHorse");
        mapping.put(ChunkerVanillaEntityType.HUSK, "Husk");
        mapping.put(ChunkerVanillaEntityType.ITEM, "Item");
        mapping.put(ChunkerVanillaEntityType.ITEM_FRAME, "ItemFrame");
        mapping.put(ChunkerVanillaEntityType.MAGMA_CUBE, "LavaSlime");
        mapping.put(ChunkerVanillaEntityType.LEASH_KNOT, "LeashKnot");
        mapping.put(ChunkerVanillaEntityType.MINECART, "Minecart");
        mapping.put(ChunkerVanillaEntityType.CHEST_MINECART, "MinecartChest");
        mapping.put(ChunkerVanillaEntityType.COMMAND_BLOCK_MINECART, "MinecartCommandBlock");
        mapping.put(ChunkerVanillaEntityType.FURNACE_MINECART, "MinecartFurnace");
        mapping.put(ChunkerVanillaEntityType.HOPPER_MINECART, "MinecartHopper");
        mapping.put(ChunkerVanillaEntityType.SPAWNER_MINECART, "MinecartSpawner");
        mapping.put(ChunkerVanillaEntityType.TNT_MINECART, "MinecartTNT");
        mapping.put(ChunkerVanillaEntityType.MULE, "Mule");
        mapping.put(ChunkerVanillaEntityType.MOOSHROOM, "MushroomCow");
        mapping.put(ChunkerVanillaEntityType.OCELOT, "Ozelot");
        mapping.put(ChunkerVanillaEntityType.PAINTING, "Painting");
        mapping.put(ChunkerVanillaEntityType.PIG, "Pig");
        mapping.put(ChunkerVanillaEntityType.ZOMBIFIED_PIGLIN, "PigZombie");
        mapping.put(ChunkerVanillaEntityType.POLAR_BEAR, "PolarBear");
        mapping.put(ChunkerVanillaEntityType.TNT, "PrimedTnt");
        mapping.put(ChunkerVanillaEntityType.RABBIT, "Rabbit");
        mapping.put(ChunkerVanillaEntityType.SHEEP, "Sheep");
        mapping.put(ChunkerVanillaEntityType.SILVERFISH, "Silverfish");
        mapping.put(ChunkerVanillaEntityType.SKELETON, "Skeleton");
        mapping.put(ChunkerVanillaEntityType.SKELETON_HORSE, "SkeletonHorse");
        mapping.put(ChunkerVanillaEntityType.SLIME, "Slime");
        mapping.put(ChunkerVanillaEntityType.SMALL_FIREBALL, "SmallFireball");
        mapping.put(ChunkerVanillaEntityType.SNOW_GOLEM, "SnowMan");
        mapping.put(ChunkerVanillaEntityType.SNOWBALL, "Snowball");
        mapping.put(ChunkerVanillaEntityType.SPECTRAL_ARROW, "SpectralArrow");
        mapping.put(ChunkerVanillaEntityType.SPIDER, "Spider");
        mapping.put(ChunkerVanillaEntityType.SQUID, "Squid");
        mapping.put(ChunkerVanillaEntityType.STRAY, "Stray");
        mapping.put(ChunkerVanillaEntityType.EGG, "ThrownEgg");
        mapping.put(ChunkerVanillaEntityType.ENDER_PEARL, "ThrownEnderpearl");
        mapping.put(ChunkerVanillaEntityType.EXPERIENCE_BOTTLE, "ThrownExpBottle");
        mapping.put(ChunkerVanillaEntityType.POTION, "ThrownPotion");
        mapping.put(ChunkerVanillaEntityType.VILLAGER, "Villager");
        mapping.put(ChunkerVanillaEntityType.IRON_GOLEM, "VillagerGolem");
        mapping.put(ChunkerVanillaEntityType.WITCH, "Witch");
        mapping.put(ChunkerVanillaEntityType.WITHER, "WitherBoss");
        mapping.put(ChunkerVanillaEntityType.WITHER_SKELETON, "WitherSkeleton");
        mapping.put(ChunkerVanillaEntityType.WITHER_SKULL, "WitherSkull");
        mapping.put(ChunkerVanillaEntityType.WOLF, "Wolf");
        mapping.put(ChunkerVanillaEntityType.EXPERIENCE_ORB, "XPOrb");
        mapping.put(ChunkerVanillaEntityType.ZOMBIE, "Zombie");
        mapping.put(ChunkerVanillaEntityType.ZOMBIE_HORSE, "ZombieHorse");
        mapping.put(ChunkerVanillaEntityType.ZOMBIE_VILLAGER, "ZombieVillager");

        if (version.isGreaterThanOrEqual(1, 9, 0)) {
            mapping.put(ChunkerVanillaEntityType.MINECART, "MinecartRideable");
            mapping.put(ChunkerVanillaEntityType.SHULKER, "Shulker");
            mapping.put(ChunkerVanillaEntityType.SHULKER_BULLET, "ShulkerBullet");
        }
    }

    @Override
    public Optional<String> from(ChunkerEntityType input) {
        if (input instanceof ChunkerVanillaEntityType chunkerVanillaEntityType) {
            return Optional.ofNullable(mapping.forward().get(chunkerVanillaEntityType));
        } else {
            // No possible mapping
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChunkerEntityType> to(String input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
