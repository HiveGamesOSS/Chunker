package com.hivemc.chunker.conversion.encoding.preview;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the fast metadata-only scan for Java Edition (Anvil) worlds.
 */
public class PreviewMetadataReaderTests {
    private static void writeRegionWithChunks(File regionFile, int... chunkLocalIndices) throws IOException {
        // Minimum viable Anvil region: 8 KB header. We only set the location-table entries (first 4 KB).
        // A non-zero 4-byte entry (we write 0x00000201 -> offset=2 sectors, sectorCount=1) marks the chunk as present.
        byte[] header = new byte[8192];
        for (int idx : chunkLocalIndices) {
            int off = idx * 4;
            header[off] = 0x00;
            header[off + 1] = 0x00;
            header[off + 2] = 0x02;
            header[off + 3] = 0x01;
        }
        try (FileOutputStream fos = new FileOutputStream(regionFile)) {
            fos.write(header);
        }
    }

    @Test
    public void testReadsRegionHeadersAndProducesMapBin(@TempDir Path tmp) throws Exception {
        File world = tmp.resolve("world").toFile();
        File regionDir = new File(world, "region");
        assertTrue(regionDir.mkdirs());

        // Region (0,0) with chunks at local indices 0 and 17
        writeRegionWithChunks(new File(regionDir, "r.0.0.mca"), 0, 17);
        // Region (1,0) is empty (header all zeros)
        writeRegionWithChunks(new File(regionDir, "r.1.0.mca"));
        // Region (-1,2) with one chunk
        writeRegionWithChunks(new File(regionDir, "r.-1.2.mca"), 42);

        File out = tmp.resolve("preview").toFile();
        assertTrue(out.mkdirs());

        new PreviewMetadataReader().readJavaWorld(world, out);

        PreviewMapBin loaded = PreviewMapBin.read(new File(out, "map.bin"));
        PreviewMapBin.WorldData ow = loaded.findByIdentifier("minecraft:overworld");
        assertNotNull(ow);
        assertTrue(ow.regionHasAnyChunk(0, 0));
        assertFalse(ow.regionHasAnyChunk(1, 0));
        assertTrue(ow.regionHasAnyChunk(-1, 2));
    }

    @Test
    public void testSkipsCorruptRegionFile(@TempDir Path tmp) throws Exception {
        File world = tmp.resolve("world").toFile();
        File regionDir = new File(world, "region");
        assertTrue(regionDir.mkdirs());

        writeRegionWithChunks(new File(regionDir, "r.0.0.mca"), 0);
        // Truncated (only 10 bytes) — must be silently skipped, not abort the whole pass.
        try (FileOutputStream fos = new FileOutputStream(new File(regionDir, "r.1.0.mca"))) {
            fos.write(new byte[10]);
        }

        File out = tmp.resolve("preview").toFile();
        assertTrue(out.mkdirs());

        new PreviewMetadataReader().readJavaWorld(world, out);

        PreviewMapBin loaded = PreviewMapBin.read(new File(out, "map.bin"));
        PreviewMapBin.WorldData ow = loaded.findByIdentifier("minecraft:overworld");
        assertTrue(ow.regionHasAnyChunk(0, 0));
        assertFalse(ow.regionHasAnyChunk(1, 0));
    }
}
