package com.hivemc.chunker.conversion.intermediate.column.entity.type;

/**
 * An enum representing every type of entity in the game.
 */
public enum ChunkerVanillaEntityType {
    ALLAY(true),
    AREA_EFFECT_CLOUD,
    ARMADILLO(true),
    ARMOR_STAND,
    ARROW,
    AXOLOTL(true),
    BAT(true),
    BEE(true),
    BLAZE(true),
    BLOCK_DISPLAY,
    BOAT,
    BOGGED(true),
    BREEZE(true),
    BREEZE_WIND_CHARGE_PROJECTILE,
    CAMEL(true),
    CAT(true),
    CAVE_SPIDER(true),
    CHEST_BOAT,
    CHEST_MINECART,
    CHICKEN(true),
    COD(true),
    COMMAND_BLOCK_MINECART,
    COW(true),
    CREEPER(true),
    DOLPHIN(true),
    DONKEY(true),
    DRAGON_FIREBALL,
    DROWNED(true),
    EGG,
    ELDER_GUARDIAN(true),
    ELDER_GUARDIAN_GHOST,
    END_CRYSTAL,
    ENDER_DRAGON(true),
    ENDER_PEARL,
    ENDERMAN(true),
    ENDERMITE(true),
    EVOKER(true),
    EVOKER_FANGS,
    EXPERIENCE_BOTTLE,
    EXPERIENCE_ORB,
    EYE_OF_ENDER,
    FALLING_BLOCK,
    FIREWORK_ROCKET,
    FOX(true),
    FROG(true),
    FURNACE_MINECART,
    GHAST(true),
    GIANT,
    GLOW_ITEM_FRAME,
    GLOW_SQUID(true),
    GOAT(true),
    GUARDIAN(true),
    HOGLIN(true),
    HOPPER_MINECART,
    HORSE(true),
    HUSK(true),
    ILLUSIONER,
    INTERACTION,
    IRON_GOLEM(true),
    ITEM,
    ITEM_DISPLAY,
    ITEM_FRAME,
    FIREBALL,
    LEASH_KNOT,
    LIGHTNING_BOLT,
    LINGERING_POTION,
    LLAMA(true),
    LLAMA_SPIT,
    MAGMA_CUBE(true),
    MARKER,
    MINECART,
    MOOSHROOM(true),
    MOVING_BLOCK,
    MULE(true),
    OCELOT(true),
    PAINTING,
    PANDA(true),
    PARROT(true),
    PHANTOM(true),
    PIG(true),
    PIGLIN(true),
    PIGLIN_BRUTE(true),
    PILLAGER(true),
    POLAR_BEAR(true),
    POTION,
    PUFFERFISH(true),
    RABBIT(true),
    RAVAGER(true),
    SALMON(true),
    SHEEP(true),
    SHIELD,
    SHULKER(true),
    SHULKER_BULLET,
    SILVERFISH(true),
    SKELETON(true),
    SKELETON_HORSE(true),
    SLIME(true),
    SMALL_FIREBALL,
    SNIFFER(true),
    SNOW_GOLEM(true),
    SNOWBALL,
    SPAWNER_MINECART,
    SPECTRAL_ARROW,
    SPIDER(true),
    SQUID(true),
    STRAY(true),
    STRIDER(true),
    TADPOLE(true),
    TEXT_DISPLAY,
    TNT,
    TNT_MINECART,
    TRADER_LLAMA(true),
    TRIDENT,
    TROPICAL_FISH(true),
    TURTLE(true),
    VEX(true),
    VILLAGER(true),
    VINDICATOR(true),
    WANDERING_TRADER(true),
    WARDEN(true),
    WIND_CHARGE,
    WITCH(true),
    WITHER(true),
    WITHER_SKELETON(true),
    WITHER_SKULL,
    WITHER_SKULL_DANGEROUS,
    WOLF(true),
    ZOGLIN(true),
    ZOMBIE(true),
    ZOMBIE_HORSE(true),
    ZOMBIE_VILLAGER(true),
    ZOMBIFIED_PIGLIN(true),
    PLAYER,
    FISHING_BOBBER;

    private final boolean vanillaSpawnEgg;

    /**
     * Create an entity type.
     *
     * @param vanillaSpawnEgg whether it has a spawn egg.
     */
    ChunkerVanillaEntityType(boolean vanillaSpawnEgg) {
        this.vanillaSpawnEgg = vanillaSpawnEgg;
    }

    /**
     * Create a new entity type with no spawn egg.
     */
    ChunkerVanillaEntityType() {
        this(false);
    }

    /**
     * Whether the entity has a spawn egg item.
     *
     * @return true if the entity does have a spawn egg item.
     */
    public boolean isVanillaSpawnEgg() {
        return vanillaSpawnEgg;
    }
}
