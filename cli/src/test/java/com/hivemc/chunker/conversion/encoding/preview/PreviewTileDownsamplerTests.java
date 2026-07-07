package com.hivemc.chunker.conversion.encoding.preview;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for alpha-aware 2x downsampling used to build higher LOD preview tiles.
 */
public class PreviewTileDownsamplerTests {
    private static final int W = 512;

    private static int[] solid(int argb) {
        int[] a = new int[W * W];
        for (int i = 0; i < a.length; i++) a[i] = argb;
        return a;
    }

    @Test
    public void testAllTransparentInputsProduceTransparentOutput() {
        int[] zero = new int[W * W];
        int[] out = PreviewTileDownsampler.aggregate(zero, zero, zero, zero);
        for (int v : out) assertEquals(0, v);
    }

    @Test
    public void testSolidColorRoundTrips() {
        int red = 0xFFFF0000;
        int[] r = solid(red);
        int[] out = PreviewTileDownsampler.aggregate(r, r, r, r);
        for (int v : out) assertEquals(red, v);
    }

    @Test
    public void testTopLeftGoesToUpperLeftQuadrant() {
        int red = 0xFFFF0000;
        int blue = 0xFF0000FF;
        int[] tl = solid(red);
        int[] tr = solid(blue);
        int[] bl = solid(blue);
        int[] br = solid(blue);
        int[] out = PreviewTileDownsampler.aggregate(tl, tr, bl, br);
        // Upper-left quadrant: red. Upper-right: blue. Lower-left: blue. Lower-right: blue.
        assertEquals(red, out[(10 << 9) | 10]);
        assertEquals(blue, out[(10 << 9) | 300]);
        assertEquals(blue, out[(300 << 9) | 10]);
        assertEquals(blue, out[(300 << 9) | 300]);
    }

    @Test
    public void testAlphaZeroInputsDoNotPolluteAverage() {
        // 3 transparent, 1 red 0xFFFF0000 inside a 2x2 block of the top-left source.
        int[] tl = new int[W * W];
        tl[(0 << 9) | 0] = 0xFFFF0000; // one opaque red pixel
        int[] zero = new int[W * W];
        int[] out = PreviewTileDownsampler.aggregate(tl, zero, zero, zero);
        // The output pixel covering (0,0)..(1,1) of tl lives at output (0,0).
        assertEquals(0xFFFF0000, out[0]);
    }

    @Test
    public void testMixedColorAverage() {
        // 2x2 block: red, blue, transparent, transparent. Expected: average of red and blue, opaque.
        int[] tl = new int[W * W];
        tl[(0 << 9) | 0] = 0xFFFF0000;
        tl[(0 << 9) | 1] = 0xFF0000FF;
        int[] zero = new int[W * W];
        int[] out = PreviewTileDownsampler.aggregate(tl, zero, zero, zero);
        // Average of R=(255+0)/2=127, G=0, B=(0+255)/2=127, A=255
        assertEquals(0xFF7F007F, out[0]);
    }
}
