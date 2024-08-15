package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion;

/**
 * The different types of potion.
 */
public enum ChunkerPotionType {
    EMPTY("empty"),
    WATER("water"),
    MUNDANE("mundane"),
    LONG_MUNDANE("long_mundane"), // Bedrock only
    THICK("thick"),
    AWKWARD("awkward"),
    NIGHT_VISION("night_vision"),
    LONG_NIGHT_VISION("long_night_vision"),
    INVISIBILITY("invisibility"),
    LONG_INVISIBILITY("long_invisibility"),
    LEAPING("leaping"),
    LONG_LEAPING("long_leaping"),
    STRONG_LEAPING("strong_leaping"),
    FIRE_RESISTANCE("fire_resistance"),
    LONG_FIRE_RESISTANCE("long_fire_resistance"),
    SWIFTNESS("swiftness"),
    LONG_SWIFTNESS("long_swiftness"),
    STRONG_SWIFTNESS("strong_swiftness"),
    SLOWNESS("slowness"),
    LONG_SLOWNESS("long_slowness"),
    STRONG_SLOWNESS("strong_slowness"),
    TURTLE_MASTER("turtle_master"),
    LONG_TURTLE_MASTER("long_turtle_master"),
    STRONG_TURTLE_MASTER("strong_turtle_master"),
    WATER_BREATHING("water_breathing"),
    LONG_WATER_BREATHING("long_water_breathing"),
    HEALING("healing"),
    STRONG_HEALING("strong_healing"),
    HARMING("harming"),
    STRONG_HARMING("strong_harming"),
    POISON("poison"),
    LONG_POISON("long_poison"),
    STRONG_POISON("strong_poison"),
    REGENERATION("regeneration"),
    LONG_REGENERATION("long_regeneration"),
    STRONG_REGENERATION("strong_regeneration"),
    STRENGTH("strength"),
    LONG_STRENGTH("long_strength"),
    STRONG_STRENGTH("strong_strength"),
    WEAKNESS("weakness"),
    LONG_WEAKNESS("long_weakness"),
    LUCK("luck"), // Only on Java
    WITHER("wither"), // Only on Bedrock
    SLOW_FALLING("slow_falling"),
    LONG_SLOW_FALLING("long_slow_falling"),
    WIND_CHARGED("wind_charged"),
    WEAVING("weaving"),
    OOZING("oozing"),
    INFESTED("infested");

    private final String identifier;

    /**
     * Create a new potion type.
     *
     * @param identifier the identifier for the potion.
     */
    ChunkerPotionType(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get the identifier for this type.
     *
     * @return the identifier.
     */
    public String getIdentifier() {
        return identifier;
    }
}
