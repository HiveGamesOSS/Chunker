package com.hivemc.chunker.conversion.encoding.preview;

import com.hivemc.chunker.conversion.encoding.base.writer.ColumnWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.io.Writer;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PreviewWorldWriter writes binary data on present regions in the various dimensions.
 */
public class PreviewWorldWriter implements WorldWriter {
    private final List<WorldData> worldDataList = Collections.synchronizedList(new ArrayList<>(4));
    private final File outputFolder;

    /**
     * Create a new preview world writer.
     *
     * @param outputFolder the output folder which the preview / binary data should be written to.
     */
    public PreviewWorldWriter(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    @Override
    public ColumnWriter writeWorld(ChunkerWorld chunkerWorld) {
        // Create the world entry
        WorldData worldData = new WorldData();
        worldData.dimension = chunkerWorld.getDimension();

        // Add it to the list
        worldDataList.add(worldData);

        // Return a new column writer
        return new PreviewColumnWriter(outputFolder, worldData);
    }

    @Override
    public void flushWorlds() throws IOException {
        // Save a binary file with worlds, min/max and present chunks
        File outputFile = new File(outputFolder, "map.bin");
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
            Writer writer = Writer.toLittleEndianWriter(writerStream);

            // Encode bytes
            writer.writeInt(worldDataList.size());
            for (WorldData worldData : worldDataList) {
                writer.writeByte(worldData.dimension.ordinal());
                writer.writeInt(worldData.minX);
                writer.writeInt(worldData.minZ);
                writer.writeInt(worldData.maxX);
                writer.writeInt(worldData.maxZ);

                // Write each region
                writer.writeInt(worldData.regionToPresentChunks.size());
                for (Map.Entry<RegionCoordPair, Set<ChunkCoordPair>> entry : worldData.regionToPresentChunks.entrySet()) {
                    // Write region position
                    writer.writeInt(entry.getKey().regionX());
                    writer.writeInt(entry.getKey().regionZ());

                    // Write a bitset of present chunks
                    BitSet presentChunks = new BitSet(1024);
                    for (ChunkCoordPair pos : entry.getValue()) {
                        presentChunks.set(pos.to10BitIndex(), true);
                    }

                    // Write the byte array
                    byte[] bitSet = presentChunks.toByteArray();
                    writer.writeBytes(bitSet);

                    // Pad to 128 bytes
                    for (int i = bitSet.length; i < 128; i++) {
                        writer.writeByte(0);
                    }
                }
            }
        }
    }

    /**
     * World data to record the region the world covers.
     */
    public static class WorldData {
        public final Map<RegionCoordPair, Set<ChunkCoordPair>> regionToPresentChunks = new ConcurrentHashMap<>();
        public int minX = Integer.MAX_VALUE;
        public int minZ = Integer.MAX_VALUE;
        public int maxX = Integer.MIN_VALUE;
        public int maxZ = Integer.MIN_VALUE;
        public Dimension dimension;
    }
}
