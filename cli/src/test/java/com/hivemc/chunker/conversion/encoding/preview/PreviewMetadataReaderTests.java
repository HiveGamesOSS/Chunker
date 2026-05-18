package com.hivemc.chunker.conversion.encoding.preview;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the fast metadata-only scan for Java Edition (Anvil) worlds and Bedrock Edition worlds.
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

    @Test
    public void testReadsBedrockWorldKeysOnly(@TempDir Path tmp) throws Exception {
        File worldDir = tmp.resolve("bedrock_world").toFile();
        File dbDir = new File(worldDir, "db");
        assertTrue(dbDir.mkdirs());

        // Create a fresh LevelDB with the same options the converter uses.
        org.iq80.leveldb.Options options = new org.iq80.leveldb.Options();
        options.compressionType(org.iq80.leveldb.CompressionType.ZLIB_RAW);
        options.blockSize(160 * 1024);
        options.filterPolicy(new org.iq80.leveldb.table.BloomFilterPolicy(10));
        options.createIfMissing(true);

        org.iq80.leveldb.DBFactory factory = new org.iq80.leveldb.impl.Iq80DBFactory();
        try (org.iq80.leveldb.DB db = factory.open(dbDir, options)) {
            // Overworld chunk (10, 20), SubChunkPrefix tag (0x2F), subChunkY 0.
            // Key length = 10: x(4) z(4) subY(1) tag(1)
            db.put(bedrockKey(10, 20, 0, true, 0x2F), new byte[]{0x01});
            // Overworld chunk (-3, 7), Data2D tag (0x2D), no subchunk.
            // Key length = 9: x(4) z(4) tag(1)
            db.put(bedrockKeyNoSub(-3, 7, 0, 0x2D), new byte[]{0x02});
            // Nether chunk (0, 0) with Version tag (0x2C). Dimension 1, no subchunk.
            // Key length = 13: x(4) z(4) dim(4) tag(1)
            // 0x2C (44 = VERSION) is NOT in our "counts as present" set; this chunk should be IGNORED.
            db.put(bedrockKeyWithDim(0, 0, 1, 0x2C), new byte[]{0x03});
            // Nether chunk (0, 0) with Data3D tag (0x2B). Dimension 1, no subchunk.
            // Key length = 13: x(4) z(4) dim(4) tag(1)
            db.put(bedrockKeyWithDim(0, 0, 1, 0x2B), new byte[]{0x04});
            // End chunk (5, -5) with SubChunkPrefix. Dimension 2, with subChunkY 3.
            // Key length = 14: x(4) z(4) dim(4) subY(1) tag(1)
            db.put(bedrockKeyDimSub(5, -5, 2, 3, 0x2F), new byte[]{0x05});
            // Junk key that doesn't fit any chunk key length — should be ignored.
            db.put(new byte[]{0x01, 0x02, 0x03}, new byte[]{0x00});
        }

        File out = tmp.resolve("preview").toFile();
        assertTrue(out.mkdirs());

        new PreviewMetadataReader().readBedrockWorld(worldDir, out);

        PreviewMapBin loaded = PreviewMapBin.read(new File(out, "map.bin"));
        PreviewMapBin.WorldData ow = loaded.findByIdentifier("minecraft:overworld");
        PreviewMapBin.WorldData nether = loaded.findByIdentifier("minecraft:the_nether");
        PreviewMapBin.WorldData end = loaded.findByIdentifier("minecraft:the_end");

        assertNotNull(ow);
        assertNotNull(nether);
        assertNotNull(end);

        // Overworld: chunk (10, 20) and (-3, 7) should be marked.
        assertTrue(ow.regionHasAnyChunk(0, 0), "Overworld region (0,0) should contain chunk (10,20)");
        assertTrue(ow.regionHasAnyChunk(-1, 0), "Overworld region (-1,0) should contain chunk (-3,7)");

        // Nether: only (0,0) via Data3D — the Version tag-only chunk is ignored.
        assertTrue(nether.regionHasAnyChunk(0, 0), "Nether region (0,0) should contain chunk (0,0) via Data3D");

        // End: chunk (5, -5) → region (0, -1)
        assertTrue(end.regionHasAnyChunk(0, -1), "End region (0,-1) should contain chunk (5,-5)");
    }

    // Bedrock key builders (little-endian).
    private static byte[] bedrockKey(int x, int z, int subY, boolean withSub, int tag) {
        java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(withSub ? 10 : 9).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        buf.putInt(x).putInt(z);
        if (withSub) buf.put((byte) subY);
        buf.put((byte) tag);
        return buf.array();
    }
    private static byte[] bedrockKeyNoSub(int x, int z, int dim, int tag) {
        // Overworld: no dimension prefix in the key, length 9.
        java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(9).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        buf.putInt(x).putInt(z).put((byte) tag);
        return buf.array();
    }
    private static byte[] bedrockKeyWithDim(int x, int z, int dim, int tag) {
        java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(13).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        buf.putInt(x).putInt(z).putInt(dim).put((byte) tag);
        return buf.array();
    }
    private static byte[] bedrockKeyDimSub(int x, int z, int dim, int subY, int tag) {
        java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(14).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        buf.putInt(x).putInt(z).putInt(dim).put((byte) subY).put((byte) tag);
        return buf.array();
    }
}
