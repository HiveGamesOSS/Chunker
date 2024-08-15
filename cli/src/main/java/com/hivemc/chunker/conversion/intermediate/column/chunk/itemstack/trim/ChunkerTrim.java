package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim;

import java.util.Objects;

/**
 * Trim decoration for armor.
 */
public class ChunkerTrim {
    private final ChunkerTrimMaterial material;
    private final ChunkerTrimPattern pattern;

    /**
     * Create a new trim.
     *
     * @param material the material for the trim.
     * @param pattern  the pattern of the material.
     */
    public ChunkerTrim(ChunkerTrimMaterial material, ChunkerTrimPattern pattern) {
        this.material = material;
        this.pattern = pattern;
    }

    /**
     * Get the material used for the trim.
     *
     * @return the material.
     */
    public ChunkerTrimMaterial getMaterial() {
        return material;
    }

    /**
     * Get the pattern used for the trim.
     *
     * @return the pattern.
     */
    public ChunkerTrimPattern getPattern() {
        return pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkerTrim that)) return false;
        return getMaterial() == that.getMaterial() && getPattern() == that.getPattern();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaterial(), getPattern());
    }

    @Override
    public String toString() {
        return "ChunkerTrim{" +
                "material=" + getMaterial() +
                ", pattern=" + getPattern() +
                '}';
    }
}
