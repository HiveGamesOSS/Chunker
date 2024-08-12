package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion;

/**
 * The type of bottle used for a potion.
 */
public enum ChunkerPotionBottleType {
    UNKNOWN,
    REGULAR,
    SPLASH,
    LINGERING;

    private static final ChunkerPotionBottleType[] VALUES = values();

    /**
     * Get the bottle type from ID.
     *
     * @param id the id.
     * @return the bottle type otherwise UNKNOWN is not found.
     */
    public static ChunkerPotionBottleType fromID(int id) {
        id = id + 1;
        if (id >= VALUES.length || id < 0) return UNKNOWN;
        return VALUES[id];
    }

    /**
     * Get the ID for this bottle type.
     *
     * @return integer ID.
     */
    public int getID() {
        return ordinal() - 1;
    }
}
