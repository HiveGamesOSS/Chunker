package com.hivemc.chunker.conversion.encoding.preview;

import java.io.IOException;

/**
 * Source of decoded ARGB pixel data for one region at LOD 0.
 */
public interface RegionRgbaSource {
    /**
     * @return an int[262144] ARGB array for the region (row-major, 512 width),
     *         or null if no chunks are present in that region.
     */
    int[] loadRegion(String world, int rx, int rz) throws IOException;
}
