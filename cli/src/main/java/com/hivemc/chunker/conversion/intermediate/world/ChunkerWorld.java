package com.hivemc.chunker.conversion.intermediate.world;

import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;

import java.util.Set;

/**
 * A world with a dimension being converted.
 */
public class ChunkerWorld {
    private Dimension dimension;
    private Set<RegionCoordPair> regions;

    /**
     * Create a new ChunkerWorld.
     *
     * @param dimension the dimension type of the world.
     * @param regions   the co-ordinates of present regions inside the world.
     */
    public ChunkerWorld(Dimension dimension, Set<RegionCoordPair> regions) {
        this.dimension = dimension;
        this.regions = regions;
    }

    /**
     * Get the dimension used for this world.
     *
     * @return the dimension.
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * Set the dimension for this world.
     *
     * @param dimension the new dimension.
     */
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    /**
     * Get the regions that this world contains as co-ordinates.
     *
     * @return a set of co-ordinates of all present regions.
     */
    public Set<RegionCoordPair> getRegions() {
        return regions;
    }

    /**
     * Set the regions which this world contains.
     *
     * @param regions the regions the world contains.
     */
    public void setRegions(Set<RegionCoordPair> regions) {
        this.regions = regions;
    }

    @Override
    public String toString() {
        return "ChunkerWorld{" +
                "dimension=" + dimension +
                ", regions=" + regions +
                '}';
    }
}
