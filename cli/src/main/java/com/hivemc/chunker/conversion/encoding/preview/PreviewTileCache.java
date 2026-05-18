package com.hivemc.chunker.conversion.encoding.preview;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bounded LRU cache of decoded preview tile ARGB arrays (one entry per tile, 1 MB each).
 * Thread-safe via synchronized methods; the access pattern is moderate and not on the hot path
 * for the renderer.
 */
public final class PreviewTileCache {
    public static final int BYTES_PER_ENTRY = 262144 * 4; // int[262144] of ARGB
    private static final long MIN_CAPACITY_BYTES = 64L * 1024 * 1024;
    private static final long MAX_CAPACITY_BYTES = 512L * 1024 * 1024;

    private final int capacityEntries;
    private final LinkedHashMap<PreviewTileKey, int[]> map;

    public PreviewTileCache(int capacityEntries) {
        this.capacityEntries = capacityEntries;
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<PreviewTileKey, int[]> eldest) {
                return size() > PreviewTileCache.this.capacityEntries;
            }
        };
    }

    /**
     * Compute the number of cache entries given the JVM max heap in bytes.
     * Clamped to [64MB / 1MB-per-entry, 512MB / 1MB-per-entry].
     */
    public static int computeCapacityEntries(long maxHeapBytes) {
        long tenPercent = maxHeapBytes / 10;
        long clampedBytes = Math.max(MIN_CAPACITY_BYTES, Math.min(MAX_CAPACITY_BYTES, tenPercent));
        return (int) (clampedBytes / BYTES_PER_ENTRY);
    }

    public synchronized int[] get(PreviewTileKey key) {
        return map.get(key);
    }

    public synchronized void put(PreviewTileKey key, int[] data) {
        map.put(key, data);
    }

    public synchronized int size() {
        return map.size();
    }
}
