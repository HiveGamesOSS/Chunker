package com.hivemc.chunker.conversion.encoding.preview;

/**
 * Stateless 2x downsampling for ARGB preview tiles, alpha-aware so that empty chunks
 * (alpha == 0) do not contaminate the colour of their neighbours when building higher LOD tiles.
 */
public final class PreviewTileDownsampler {
    public static final int TILE_SIZE = 512;
    private static final int TILE_PIXELS = TILE_SIZE * TILE_SIZE;
    private static final int HALF = TILE_SIZE / 2;

    private PreviewTileDownsampler() {
    }

    /**
     * Combine four source tiles laid out as a 2x2 grid into one tile.
     * Each source quadrant is downsampled 2x and copied to the matching quadrant of the output.
     *
     * @param topLeft     source for the upper-left output quadrant
     * @param topRight    source for the upper-right output quadrant
     * @param bottomLeft  source for the lower-left output quadrant
     * @param bottomRight source for the lower-right output quadrant
     * @return a new int[TILE_PIXELS] ARGB tile
     */
    public static int[] aggregate(int[] topLeft, int[] topRight, int[] bottomLeft, int[] bottomRight) {
        int[] out = new int[TILE_PIXELS];
        downsampleInto(topLeft, out, 0, 0);
        downsampleInto(topRight, out, HALF, 0);
        downsampleInto(bottomLeft, out, 0, HALF);
        downsampleInto(bottomRight, out, HALF, HALF);
        return out;
    }

    private static void downsampleInto(int[] src, int[] dst, int destX, int destY) {
        for (int sy = 0; sy < TILE_SIZE; sy += 2) {
            int oy = destY + (sy >> 1);
            int srcRow0 = sy << 9; // sy * TILE_SIZE
            int srcRow1 = (sy + 1) << 9;
            int dstRow = oy << 9;
            for (int sx = 0; sx < TILE_SIZE; sx += 2) {
                int p00 = src[srcRow0 | sx];
                int p01 = src[srcRow0 | (sx + 1)];
                int p10 = src[srcRow1 | sx];
                int p11 = src[srcRow1 | (sx + 1)];

                int r = 0, g = 0, b = 0, count = 0;
                if ((p00 >>> 24) != 0) { r += (p00 >>> 16) & 0xFF; g += (p00 >>> 8) & 0xFF; b += p00 & 0xFF; count++; }
                if ((p01 >>> 24) != 0) { r += (p01 >>> 16) & 0xFF; g += (p01 >>> 8) & 0xFF; b += p01 & 0xFF; count++; }
                if ((p10 >>> 24) != 0) { r += (p10 >>> 16) & 0xFF; g += (p10 >>> 8) & 0xFF; b += p10 & 0xFF; count++; }
                if ((p11 >>> 24) != 0) { r += (p11 >>> 16) & 0xFF; g += (p11 >>> 8) & 0xFF; b += p11 & 0xFF; count++; }

                int outArgb;
                if (count == 0) {
                    outArgb = 0;
                } else {
                    outArgb = 0xFF000000 | ((r / count) << 16) | ((g / count) << 8) | (b / count);
                }
                dst[dstRow | (destX + (sx >> 1))] = outArgb;
            }
        }
    }
}
