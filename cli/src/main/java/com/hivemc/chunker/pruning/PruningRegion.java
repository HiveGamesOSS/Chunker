package com.hivemc.chunker.pruning;

/**
 * Represents a region which should be kept/deleted.
 */
public class PruningRegion {
    private int minChunkX = 0;
    private int minChunkZ = 0;
    private int maxChunkX = 0;
    private int maxChunkZ = 0;

    /**
     * Create an empty pruning region
     */
    public PruningRegion() {
    }

    /**
     * Create a pruning region.
     *
     * @param minChunkX the minimum chunkX of the region.
     * @param minChunkZ the minimum chunkZ of the region.
     * @param maxChunkX the maximum chunkX of the region.
     * @param maxChunkZ the maximum chunkZ of the region.
     */
    public PruningRegion(int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ) {
        this.minChunkX = minChunkX;
        this.minChunkZ = minChunkZ;
        this.maxChunkX = maxChunkX;
        this.maxChunkZ = maxChunkZ;
    }

    /**
     * The minimum chunk X for the region.
     *
     * @return the chunk co-ordinate.
     */
    public int getMinChunkX() {
        return minChunkX;
    }

    /**
     * The minimum chunk Z for the region.
     *
     * @return the chunk co-ordinate.
     */
    public int getMinChunkZ() {
        return minChunkZ;
    }

    /**
     * The maximum chunk X for the region.
     *
     * @return the chunk co-ordinate.
     */
    public int getMaxChunkX() {
        return maxChunkX;
    }

    /**
     * The maximum chunk Z for the region.
     *
     * @return the chunk co-ordinate.
     */
    public int getMaxChunkZ() {
        return maxChunkZ;
    }
}
