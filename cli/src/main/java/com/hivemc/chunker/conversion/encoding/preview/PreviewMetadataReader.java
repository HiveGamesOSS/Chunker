package com.hivemc.chunker.conversion.encoding.preview;

import java.io.*;
import java.util.BitSet;
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
}
