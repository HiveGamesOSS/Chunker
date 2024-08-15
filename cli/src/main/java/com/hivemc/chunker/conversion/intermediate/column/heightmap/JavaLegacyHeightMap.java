package com.hivemc.chunker.conversion.intermediate.column.heightmap;

/**
 * Java Legacy specific HeightMap.
 */
public class JavaLegacyHeightMap implements HeightMap {
    private final short[][] heightMap;

    /**
     * Create a new Java Legacy HeightMap.
     *
     * @param heightMap the array in the dimensions [16][16] with it being [x][z].
     */
    public JavaLegacyHeightMap(short[][] heightMap) {
        this.heightMap = heightMap;
    }

    /**
     * Get the java legacy height map as an array of [x][z].
     *
     * @return the array of shorts in the dimensions [16][16].
     */
    public short[][] getHeightMap() {
        return heightMap;
    }
}
