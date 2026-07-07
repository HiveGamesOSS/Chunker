package com.hivemc.chunker.conversion.encoding.preview;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the bounded LRU used by the preview tile service.
 */
public class PreviewTileCacheTests {
    private static int[] tile(int marker) {
        int[] a = new int[262144];
        a[0] = marker;
        return a;
    }

    @Test
    public void testPutAndGetSameInstance() {
        PreviewTileCache cache = new PreviewTileCache(4);
        PreviewTileKey k = new PreviewTileKey("minecraft:overworld", 0, 1, 1);
        int[] data = tile(7);
        cache.put(k, data);
        assertNotNull(cache.get(k));
        assertEquals(7, cache.get(k)[0]);
    }

    @Test
    public void testEvictsLeastRecentlyUsed() {
        PreviewTileCache cache = new PreviewTileCache(2);
        PreviewTileKey a = new PreviewTileKey("w", 0, 1, 1);
        PreviewTileKey b = new PreviewTileKey("w", 0, 2, 2);
        PreviewTileKey c = new PreviewTileKey("w", 0, 3, 3);
        cache.put(a, tile(1));
        cache.put(b, tile(2));
        // touch a so b becomes the LRU
        assertNotNull(cache.get(a));
        cache.put(c, tile(3));
        assertNotNull(cache.get(a));
        assertNull(cache.get(b));
        assertNotNull(cache.get(c));
    }

    @Test
    public void testCapacityFromHeapClampedToFloorCeiling() {
        // 64 MB at minimum, 512 MB at maximum, otherwise ~10% of max heap.
        // Each entry is 1 MB so capacity entries == capacity MB.
        assertEquals(64, PreviewTileCache.computeCapacityEntries(100L * 1024 * 1024));      // 10MB < 64MB floor
        assertEquals(64, PreviewTileCache.computeCapacityEntries(640L * 1024 * 1024));      // exactly 64MB at 10%, equal to floor
        assertEquals(200, PreviewTileCache.computeCapacityEntries(2000L * 1024 * 1024));    // 200MB
        assertEquals(512, PreviewTileCache.computeCapacityEntries(8000L * 1024 * 1024));    // capped at 512MB ceiling
    }
}
