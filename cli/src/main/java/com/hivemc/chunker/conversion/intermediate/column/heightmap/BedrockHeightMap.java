package com.hivemc.chunker.conversion.intermediate.column.heightmap;

/**
 * Bedrock specific height-map, based on highest block / slab.
 */
public class BedrockHeightMap implements HeightMap {
    private final short[][] heightMap;

    /**
     * Create a new Bedrock HeightMap.
     *
     * @param heightMap the array in the dimensions [16][16] with it being [x][z].
     */
    public BedrockHeightMap(short[][] heightMap) {
        this.heightMap = heightMap;
    }

    /**
     * Get the bedrock height map as an array of [x][z].
     *
     * @return the array of shorts in the dimensions [16][16].
     */
    public short[][] getHeightMap() {
        return heightMap;
    }
}
