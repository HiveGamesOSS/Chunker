package com.hivemc.chunker.conversion.encoding.preview;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Round-trip tests for the map.bin reader/writer and empty-tile lookups.
 */
public class PreviewMapBinTests {
    @Test
    public void testRoundTrip(@TempDir Path tmp) throws Exception {
        PreviewMapBin.Builder b = new PreviewMapBin.Builder();
        BitSet present = new BitSet(1024);
        present.set(0);
        present.set(17);
        b.addWorld(0, "minecraft:overworld", -5, -3, 2, 4)
                .addRegion(0, 0, 0, present);

        File out = tmp.resolve("map.bin").toFile();
        b.writeTo(out);

        PreviewMapBin loaded = PreviewMapBin.read(out);
        PreviewMapBin.WorldData ow = loaded.findByIdentifier("minecraft:overworld");
        assertEquals(-5, ow.minX());
        assertEquals(2, ow.maxX());
        assertEquals(-3, ow.minZ());
        assertEquals(4, ow.maxZ());
        assertTrue(ow.regionHasAnyChunk(0, 0));
        assertFalse(ow.regionHasAnyChunk(1, 0));
    }

    @Test
    public void testTileEmptinessAtLodZero(@TempDir Path tmp) throws Exception {
        // Region (0,0) has at least one chunk, region (1,0) does not exist.
        PreviewMapBin.Builder b = new PreviewMapBin.Builder();
        BitSet present = new BitSet(1024);
        present.set(5);
        b.addWorld(0, "minecraft:overworld", 0, 0, 0, 0).addRegion(0, 0, 0, present);

        File out = tmp.resolve("map.bin").toFile();
        b.writeTo(out);

        PreviewMapBin loaded = PreviewMapBin.read(out);
        assertFalse(loaded.isTileEmpty("minecraft:overworld", 0, 0, 0));
        assertTrue(loaded.isTileEmpty("minecraft:overworld", 0, 1, 0));
    }

    @Test
    public void testTileEmptinessAtNegativeLodAggregatesChildren(@TempDir Path tmp) throws Exception {
        // At LOD -1 a single tile covers a 2x2 block of LOD-0 regions.
        // Region (0,0) present, (1,0), (0,1), (1,1) absent -> LOD -1 tile (0,0) is NOT empty.
        // Regions (2..3, 2..3) all absent -> LOD -1 tile (1,1) IS empty.
        PreviewMapBin.Builder b = new PreviewMapBin.Builder();
        BitSet present = new BitSet(1024);
        present.set(0);
        b.addWorld(0, "minecraft:overworld", 0, 0, 3, 3).addRegion(0, 0, 0, present);

        File out = tmp.resolve("map.bin").toFile();
        b.writeTo(out);

        PreviewMapBin loaded = PreviewMapBin.read(out);
        assertFalse(loaded.isTileEmpty("minecraft:overworld", -1, 0, 0));
        assertTrue(loaded.isTileEmpty("minecraft:overworld", -1, 1, 1));
    }
}
