package com.hivemc.chunker.conversion.intermediate.column.chunk;

/**
 * A pair of co-ordinates of a region (32 x 32 chunks)
 *
 * @param regionX the X co-ordinate of the region.
 * @param regionZ the Z co-ordinate of the region.
 */
public record RegionCoordPair(int regionX, int regionZ) {
    /**
     * Get a chunk co-ordinate pair inside this region.
     *
     * @param localChunkX the local co-ordinate inside the region.
     * @param localChunkZ the local co-ordinate inside the region.
     * @return a new chunk co-ord pair.
     */
    public ChunkCoordPair getChunk(int localChunkX, int localChunkZ) {
        return new ChunkCoordPair(
                regionX << 5 | localChunkX,
                regionZ << 5 | localChunkZ
        );
    }

    /**
     * Check whether a chunk is inside the region.
     *
     * @param chunkCoordPair the chunk co-ordinate pair.
     * @return true if it is inside the region.
     */
    public boolean isInside(ChunkCoordPair chunkCoordPair) {
        return chunkCoordPair.regionX() == regionX && chunkCoordPair.regionZ() == regionZ;
    }
}
