package com.hivemc.chunker.conversion.encoding.preview;

import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.table.BloomFilterPolicy;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads only the chunk-location headers of a Minecraft world's region files to determine
 * which chunks exist, without decompressing any chunk data. Output matches the byte layout
 * produced by the legacy PreviewWorldWriter.
 */
public final class PreviewMetadataReader {
    private static final Pattern REGION_FILE = Pattern.compile("^r\\.(-?\\d+)\\.(-?\\d+)\\.mca$");

    public void readJavaWorld(File worldDir, File outputFolder) throws IOException {
        if (!outputFolder.exists() && !outputFolder.mkdirs()) {
            throw new IOException("Could not create preview output folder: " + outputFolder);
        }

        PreviewMapBin.Builder builder = new PreviewMapBin.Builder();

        // Overworld: <world>/region
        scanDimensionInto(builder, 0, "minecraft:overworld", new File(worldDir, "region"));

        // Nether: <world>/DIM-1/region
        File nether = new File(worldDir, "DIM-1/region");
        if (nether.isDirectory()) {
            scanDimensionInto(builder, 1, "minecraft:the_nether", nether);
        }

        // End: <world>/DIM1/region
        File end = new File(worldDir, "DIM1/region");
        if (end.isDirectory()) {
            scanDimensionInto(builder, 2, "minecraft:the_end", end);
        }

        builder.writeTo(new File(outputFolder, "map.bin"));
    }

    private void scanDimensionInto(PreviewMapBin.Builder builder, int bedrockId, String identifier, File regionDir) {
        int worldIndex = builder.size();
        builder.addWorld(bedrockId, identifier, 0, 0, 0, 0);
        if (!regionDir.isDirectory()) return;

        File[] files = regionDir.listFiles();
        if (files == null) return;

        int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (File f : files) {
            Matcher m = REGION_FILE.matcher(f.getName());
            if (!m.matches()) continue;
            int rx, rz;
            try {
                rx = Integer.parseInt(m.group(1));
                rz = Integer.parseInt(m.group(2));
            } catch (NumberFormatException ignored) {
                continue;
            }
            BitSet present = readRegionPresence(f);
            if (present == null) continue; // corrupt or unreadable; skip
            if (present.isEmpty()) continue;
            builder.addRegion(worldIndex, rx, rz, present);

            // Update bounds in chunk coordinates from the present chunks of this region.
            for (int bit = present.nextSetBit(0); bit >= 0; bit = present.nextSetBit(bit + 1)) {
                int localX = bit & 31;
                int localZ = (bit >> 5) & 31;
                int cx = (rx << 5) | localX;
                int cz = (rz << 5) | localZ;
                if (cx < minX) minX = cx;
                if (cz < minZ) minZ = cz;
                if (cx > maxX) maxX = cx;
                if (cz > maxZ) maxZ = cz;
            }
        }

        if (minX != Integer.MAX_VALUE) {
            builder.setBoundsForWorld(worldIndex, minX, minZ, maxX, maxZ);
        }
    }

    private BitSet readRegionPresence(File regionFile) {
        try (FileInputStream fis = new FileInputStream(regionFile);
             DataInputStream dis = new DataInputStream(new BufferedInputStream(fis, 4096))) {
            byte[] header = dis.readNBytes(4096);
            if (header.length < 4096) return null;
            BitSet present = new BitSet(1024);
            for (int i = 0; i < 1024; i++) {
                int off = i * 4;
                int entry = ((header[off] & 0xFF) << 24)
                        | ((header[off + 1] & 0xFF) << 16)
                        | ((header[off + 2] & 0xFF) << 8)
                        | (header[off + 3] & 0xFF);
                if (entry != 0) present.set(i);
            }
            return present;
        } catch (IOException e) {
            return null;
        }
    }

    public void readBedrockWorld(File worldDir, File outputFolder) throws IOException {
        if (!outputFolder.exists() && !outputFolder.mkdirs()) {
            throw new IOException("Could not create preview output folder: " + outputFolder);
        }

        File dbDir = new File(worldDir, "db");
        new File(dbDir, "LOCK").delete();

        Options options = new Options();
        options.compressionType(CompressionType.ZLIB_RAW);
        options.blockSize(160 * 1024);
        options.filterPolicy(new BloomFilterPolicy(10));
        options.createIfMissing(true);

        // Per-dimension state: bounds and region presence.
        // dimensionID -> (minX, minZ, maxX, maxZ)
        Map<Integer, int[]> dimensionBounds = new HashMap<>();
        // dimensionID -> (regionKey -> BitSet)
        Map<Integer, Map<Long, BitSet>> dimensionRegions = new HashMap<>();

        // LevelDBChunkType byte values that count as a chunk being present:
        // DATA_3D=43, DATA_2D=45, SUB_CHUNK_PREFIX=47, BLOCK_ENTITY=49, ENTITY=50
        final byte TYPE_DATA_3D = 43;
        final byte TYPE_DATA_2D = 45;
        final byte TYPE_SUB_CHUNK_PREFIX = 47;
        final byte TYPE_BLOCK_ENTITY = 49;
        final byte TYPE_ENTITY = 50;

        try (DB db = new Iq80DBFactory().open(dbDir, options);
             DBIterator iterator = db.iterator()) {
            while (iterator.hasNext()) {
                byte[] key = iterator.next().getKey();
                int keyLength = key.length;

                boolean containsSubChunk = keyLength == 14 || keyLength == 10;
                boolean containsDimension = keyLength == 14 || keyLength == 13;

                if (keyLength != 9 && !containsSubChunk && !containsDimension) continue;

                ByteBuffer buffer = ByteBuffer.wrap(key).order(ByteOrder.LITTLE_ENDIAN);
                int x = buffer.getInt();
                int z = buffer.getInt();
                int dimensionID = containsDimension ? buffer.getInt() : 0;
                if (containsSubChunk) buffer.get();
                byte type = buffer.get();

                if (type != TYPE_DATA_3D && type != TYPE_DATA_2D && type != TYPE_SUB_CHUNK_PREFIX
                        && type != TYPE_ENTITY && type != TYPE_BLOCK_ENTITY) {
                    continue;
                }

                // Compute region coordinates and bit index within the region.
                int rx = x >> 5;
                int rz = z >> 5;
                int bit = ((z & 31) << 5) | (x & 31);

                // Record the chunk in the region presence map.
                Map<Long, BitSet> regions = dimensionRegions.computeIfAbsent(dimensionID, id -> new HashMap<>());
                long regionKey = ((long) rx << 32) | (rz & 0xFFFFFFFFL);
                BitSet regionBits = regions.computeIfAbsent(regionKey, k -> new BitSet(1024));
                regionBits.set(bit);

                // Update bounds.
                int[] bounds = dimensionBounds.computeIfAbsent(dimensionID,
                        id -> new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE});
                if (x < bounds[0]) bounds[0] = x;
                if (z < bounds[1]) bounds[1] = z;
                if (x > bounds[2]) bounds[2] = x;
                if (z > bounds[3]) bounds[3] = z;
            }
        }

        PreviewMapBin.Builder builder = new PreviewMapBin.Builder();

        // Always include the three vanilla dimensions.
        int[] vanillaDims = {0, 1, 2};
        String[] vanillaIdentifiers = {"minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"};
        for (int i = 0; i < vanillaDims.length; i++) {
            int dimId = vanillaDims[i];
            int worldIndex = builder.size();
            builder.addWorld(dimId, vanillaIdentifiers[i], 0, 0, 0, 0);
            populateDimension(builder, worldIndex, dimId, dimensionBounds, dimensionRegions);
        }

        // Custom dimensions (id > 2) — only add if they have chunks.
        for (Map.Entry<Integer, Map<Long, BitSet>> entry : dimensionRegions.entrySet()) {
            int dimId = entry.getKey();
            if (dimId <= 2) continue;
            int worldIndex = builder.size();
            builder.addWorld(dimId, "custom:" + dimId, 0, 0, 0, 0);
            populateDimension(builder, worldIndex, dimId, dimensionBounds, dimensionRegions);
        }

        builder.writeTo(new File(outputFolder, "map.bin"));
    }

    private void populateDimension(PreviewMapBin.Builder builder, int worldIndex, int dimId,
                                   Map<Integer, int[]> dimensionBounds,
                                   Map<Integer, Map<Long, BitSet>> dimensionRegions) {
        Map<Long, BitSet> regions = dimensionRegions.get(dimId);
        if (regions == null) return;

        for (Map.Entry<Long, BitSet> entry : regions.entrySet()) {
            long packed = entry.getKey();
            int rx = (int) (packed >> 32);
            int rz = (int) packed;
            BitSet bits = entry.getValue();
            if (!bits.isEmpty()) {
                builder.addRegion(worldIndex, rx, rz, bits);
            }
        }

        int[] bounds = dimensionBounds.get(dimId);
        if (bounds != null && bounds[0] != Integer.MAX_VALUE) {
            builder.setBoundsForWorld(worldIndex, bounds[0], bounds[1], bounds[2], bounds[3]);
        }
    }
}
