package com.hivemc.chunker.conversion.encoding.preview;

import com.hivemc.chunker.nbt.io.Writer;

import java.io.*;
import java.util.*;

/**
 * Reads and writes the map.bin metadata blob in the format produced by the legacy
 * PreviewWorldWriter, and answers empty-tile queries used by the lazy tile service
 * to skip generation of tiles that cannot contain any block data.
 */
public final class PreviewMapBin {
    private final List<WorldData> worlds;

    private PreviewMapBin(List<WorldData> worlds) {
        this.worlds = worlds;
    }

    public WorldData findByIdentifier(String identifier) {
        for (WorldData w : worlds) {
            if (w.identifier.equals(identifier)) return w;
        }
        return null;
    }

    /**
     * A tile is empty when every region it covers at LOD 0 has zero present chunks.
     * For lod == 0 each tile maps to one region. For lod &lt; 0 a tile covers 2^|lod| x 2^|lod| regions.
     * Positive lod is undefined here (the native pyramid bottoms out at LOD 0); always returns
     * false in that case so the caller does not skip a tile the client may legitimately request.
     */
    public boolean isTileEmpty(String world, int lod, int tx, int tz) {
        WorldData w = findByIdentifier(world);
        if (w == null) return true;
        // Guard against positive lod: Java bit-shift uses the low 5 bits of the right operand,
        // so `1 << -lod` wraps to a huge value and the loop below would never terminate.
        if (lod > 0) return false;
        int scale = 1 << (-lod);
        int minRx = tx * scale;
        int minRz = tz * scale;
        int maxRx = minRx + scale; // exclusive
        int maxRz = minRz + scale;
        for (int rx = minRx; rx < maxRx; rx++) {
            for (int rz = minRz; rz < maxRz; rz++) {
                if (w.regionHasAnyChunk(rx, rz)) return false;
            }
        }
        return true;
    }

    public static PreviewMapBin read(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             DataInputStream dis = new DataInputStream(bis)) {
            int worldCount = Integer.reverseBytes(dis.readInt());
            List<WorldData> worlds = new ArrayList<>(worldCount);
            for (int i = 0; i < worldCount; i++) {
                int bedrockId = Integer.reverseBytes(dis.readInt());
                int minX = Integer.reverseBytes(dis.readInt());
                int minZ = Integer.reverseBytes(dis.readInt());
                int maxX = Integer.reverseBytes(dis.readInt());
                int maxZ = Integer.reverseBytes(dis.readInt());
                int regionCount = Integer.reverseBytes(dis.readInt());
                Map<Long, BitSet> regionToChunks = new HashMap<>(regionCount * 2);
                for (int r = 0; r < regionCount; r++) {
                    int rx = Integer.reverseBytes(dis.readInt());
                    int rz = Integer.reverseBytes(dis.readInt());
                    byte[] bitset = dis.readNBytes(128);
                    regionToChunks.put(packRegion(rx, rz), BitSet.valueOf(bitset));
                }
                worlds.add(new WorldData(bedrockId, identifierForBedrockId(bedrockId), minX, minZ, maxX, maxZ, regionToChunks));
            }
            return new PreviewMapBin(worlds);
        }
    }

    private static String identifierForBedrockId(int id) {
        return switch (id) {
            case 0 -> "minecraft:overworld";
            case 1 -> "minecraft:the_nether";
            case 2 -> "minecraft:the_end";
            default -> "custom:" + id;
        };
    }

    private static long packRegion(int rx, int rz) {
        return ((long) rx << 32) | (rz & 0xFFFFFFFFL);
    }

    /**
     * Builder mirrors the layout of the legacy PreviewWorldWriter for byte-for-byte compatibility.
     */
    public static final class Builder {
        private final List<WorldData> worlds = new ArrayList<>();

        public Builder addWorld(int bedrockId, String identifier, int minX, int minZ, int maxX, int maxZ) {
            worlds.add(new WorldData(bedrockId, identifier, minX, minZ, maxX, maxZ, new HashMap<>()));
            return this;
        }

        public Builder addRegion(int worldIndex, int rx, int rz, BitSet present) {
            worlds.get(worldIndex).regionToChunks.put(packRegion(rx, rz), present);
            return this;
        }

        public int size() {
            return worlds.size();
        }

        public Builder setBoundsForWorld(int worldIndex, int minX, int minZ, int maxX, int maxZ) {
            WorldData old = worlds.get(worldIndex);
            WorldData updated = new WorldData(old.bedrockId, old.identifier, minX, minZ, maxX, maxZ, old.regionToChunks);
            worlds.set(worldIndex, updated);
            return this;
        }

        public void writeTo(File file) throws IOException {
            try (FileOutputStream fos = new FileOutputStream(file);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 DataOutputStream dos = new DataOutputStream(bos)) {
                Writer writer = Writer.toLittleEndianWriter(dos);
                writer.writeInt(worlds.size());
                for (WorldData w : worlds) {
                    writer.writeInt(w.bedrockId);
                    writer.writeInt(w.minX);
                    writer.writeInt(w.minZ);
                    writer.writeInt(w.maxX);
                    writer.writeInt(w.maxZ);
                    writer.writeInt(w.regionToChunks.size());
                    for (Map.Entry<Long, BitSet> e : w.regionToChunks.entrySet()) {
                        long packed = e.getKey();
                        int rx = (int) (packed >> 32);
                        int rz = (int) packed;
                        writer.writeInt(rx);
                        writer.writeInt(rz);
                        byte[] bits = e.getValue().toByteArray();
                        writer.writeBytes(bits);
                        for (int i = bits.length; i < 128; i++) writer.writeByte(0);
                    }
                }
            }
        }
    }

    public static final class WorldData {
        public final int bedrockId;
        public final String identifier;
        private final int minX, minZ, maxX, maxZ;
        private final Map<Long, BitSet> regionToChunks;

        WorldData(int bedrockId, String identifier, int minX, int minZ, int maxX, int maxZ, Map<Long, BitSet> regionToChunks) {
            this.bedrockId = bedrockId;
            this.identifier = identifier;
            this.minX = minX;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxZ = maxZ;
            this.regionToChunks = regionToChunks;
        }

        public int minX() { return minX; }
        public int minZ() { return minZ; }
        public int maxX() { return maxX; }
        public int maxZ() { return maxZ; }

        public boolean regionHasAnyChunk(int rx, int rz) {
            BitSet b = regionToChunks.get(packRegion(rx, rz));
            return b != null && !b.isEmpty();
        }
    }
}
