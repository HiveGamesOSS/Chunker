package com.hivemc.chunker.conversion.intermediate.column.chunk;

/**
 * The co-ordinates of a chunk/column in the world (16x16).
 *
 * @param chunkX the x co-ordinate.
 * @param chunkZ the z co-ordinate.
 */
public record ChunkCoordPair(int chunkX, int chunkZ) {
    /**
     * Convert the position to a 10-bit index, allowing chunkX/Z to go from 0 to 31.
     * Useful for converting chunks into regions.
     *
     * @return a 10-bit index.
     */
    public int to10BitIndex() {
        return ((chunkX & 31) << 4) | (chunkZ & 31);
    }

    /**
     * Get the region X co-ordinate for the region that holds this chunk.
     *
     * @return the region x co-ordinate.
     */
    public int regionX() {
        return chunkX >> 5;
    }

    /**
     * Get the region Z co-ordinate for the region that holds this chunk.
     *
     * @return the region z co-ordinate.
     */
    public int regionZ() {
        return chunkZ >> 5;
    }

    /**
     * Returns true if this chunk is on the edge of a region.
     *
     * @return true if the chunk is on the edge of a region.
     */
    public boolean isRegionEdge() {
        int localX = (chunkX & 31);
        int localZ = (chunkZ & 31);
        return localX == 0 || localX == 31 || localZ == 0 || localZ == 31;
    }

    /**
     * Get the region this chunk co-ordinate pair is inside.
     *
     * @return a new region co-ord pair.
     */
    public RegionCoordPair getRegion() {
        return new RegionCoordPair(regionX(), regionZ());
    }

    /**
     * Check whether a position is inside this chunk.
     *
     * @param x the x position.
     * @param z the z position.
     * @return true if the position is inside the chunk.
     */
    public boolean isInside(int x, int z) {
        return (x >> 4 == chunkX) && (z >> 4 == chunkZ);
    }
}
