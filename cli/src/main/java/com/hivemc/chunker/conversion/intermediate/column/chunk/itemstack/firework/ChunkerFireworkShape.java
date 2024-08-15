package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.firework;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.Optional;

/**
 * The shape of the firework explosion.
 */
public enum ChunkerFireworkShape {
    SMALL_BALL("small_ball"),
    LARGE_BALL("large_ball"),
    STAR("star"),
    CREEPER("creeper"),
    BURST("burst");


    private static final ChunkerFireworkShape[] VALUES = values();
    private static final Map<String, ChunkerFireworkShape> BY_NAME = new Object2ObjectOpenHashMap<>();

    static {
        for (ChunkerFireworkShape value : VALUES) {
            BY_NAME.put(value.getName(), value);
        }
    }

    private final String name;

    /**
     * Create a new firework shape.
     *
     * @param name the identifier to use.
     */
    ChunkerFireworkShape(String name) {
        this.name = name;
    }

    /**
     * Get a shape by name.
     *
     * @param name the name.
     * @return the shape if found otherwise absent.
     */
    public static Optional<ChunkerFireworkShape> getByName(String name) {
        return Optional.ofNullable(BY_NAME.get(name));
    }

    /**
     * Get a shape by the ID.
     *
     * @param id the ID (ordinal).
     * @return the shape if found otherwise absent.
     */
    public static Optional<ChunkerFireworkShape> getByID(int id) {
        if (id < 0 || id >= VALUES.length) return Optional.empty();
        return Optional.of(VALUES[id]);
    }

    /**
     * Get the ID for this shape.
     *
     * @return the integer ID.
     */
    public int getID() {
        return ordinal();
    }

    /**
     * The name of the shape.
     *
     * @return the name in lowercase.
     */
    public String getName() {
        return name;
    }
}
