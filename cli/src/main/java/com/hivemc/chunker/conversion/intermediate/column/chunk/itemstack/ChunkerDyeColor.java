package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.Optional;

/**
 * The color that something can be colored.
 */
public enum ChunkerDyeColor {
    WHITE("white"),
    ORANGE("orange"),
    MAGENTA("magenta"),
    LIGHT_BLUE("light_blue"),
    YELLOW("yellow"),
    LIME("lime"),
    PINK("pink"),
    GRAY("gray"),
    LIGHT_GRAY("light_gray"),
    CYAN("cyan"),
    PURPLE("purple"),
    BLUE("blue"),
    BROWN("brown"),
    GREEN("green"),
    RED("red"),
    BLACK("black");

    private static final ChunkerDyeColor[] VALUES = values();
    private static final Map<String, ChunkerDyeColor> BY_NAME = new Object2ObjectOpenHashMap<>();

    static {
        for (ChunkerDyeColor value : VALUES) {
            BY_NAME.put(value.getName(), value);
        }
    }

    private final String name;

    /**
     * Create a new dye color.
     *
     * @param name the identifier used for the color.
     */
    ChunkerDyeColor(String name) {
        this.name = name;
    }

    /**
     * Get the color by reversed ID (used for bedrock / legacy java).
     *
     * @param id the reversed ID.
     * @return the dye if it was found otherwise empty.
     */
    public static Optional<ChunkerDyeColor> getColorByReversedID(int id) {
        return getColorByID(15 - id); // Invert ID for bedrock
    }

    /**
     * Get the color by ID.
     *
     * @param id the ID.
     * @return the dye if it was found otherwise empty.
     */
    public static Optional<ChunkerDyeColor> getColorByID(int id) {
        if (id < 0 || id >= VALUES.length) return Optional.empty();
        return Optional.of(VALUES[id]);
    }

    /**
     * Get the color by name.
     *
     * @param name the name.
     * @return the dye if it was found otherwise empty.
     */
    public static Optional<ChunkerDyeColor> getColorByName(String name) {
        return Optional.ofNullable(BY_NAME.get(name));
    }

    /**
     * Get the ID of the dye color.
     *
     * @return the ID.
     */
    public int getID() {
        return ordinal();
    }

    /**
     * Get the reversed ID (used for bedrock / legacy java).
     *
     * @return the reversed ID.
     */
    public int getReversedID() {
        return 15 - ordinal(); // Inverted on bedrock/legacy java sometimes
    }

    /**
     * Get the name of the dye as an identifier.
     *
     * @return the name as an identifier.
     */
    public String getName() {
        return name;
    }
}
