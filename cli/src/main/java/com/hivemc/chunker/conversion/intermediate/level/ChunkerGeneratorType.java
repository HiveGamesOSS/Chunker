package com.hivemc.chunker.conversion.intermediate.level;

/**
 * The generator to use for the world.
 */
public enum ChunkerGeneratorType {
    /**
     * The normal world generator.
     */
    NORMAL,
    /**
     * Grass flat map generator.
     */
    FLAT,
    /**
     * A generator that has nothing.
     */
    VOID,
    /**
     * A generator which cannot be converted from input -> output unless the format matches (which NBT is used).
     */
    CUSTOM
}
