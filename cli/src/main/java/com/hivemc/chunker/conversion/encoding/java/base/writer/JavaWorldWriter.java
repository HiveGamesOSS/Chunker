package com.hivemc.chunker.conversion.encoding.java.base.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.writer.ColumnWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaLevelReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A writer for Java worlds.
 */
public class JavaWorldWriter implements WorldWriter {
    public static final int OVERSIZED_THRESHOLD = 4096 * 256;
    protected final File outputFolder;
    protected final Converter converter;
    protected final JavaResolvers resolvers;
    protected final Map<File, RandomAccessFile> mcaFiles = new ConcurrentHashMap<>();

    /**
     * Create a new java writer.
     *
     * @param outputFolder the folder to write to.
     * @param converter    the converter instance.
     * @param resolvers    the resolvers to use.
     */
    public JavaWorldWriter(File outputFolder, Converter converter, JavaResolvers resolvers) {
        this.outputFolder = outputFolder;
        this.converter = converter;
        this.resolvers = resolvers;
    }

    @Override
    public ColumnWriter writeWorld(ChunkerWorld chunkerWorld) {
        // Make the output directory
        File directory = JavaLevelReader.getDimensionBaseDirectory(outputFolder, chunkerWorld.getDimension());
        directory.mkdirs();

        // Return a new column writer
        return createColumnWriter(chunkerWorld.getDimension());
    }

    @Override
    public void flushWorld(ChunkerWorld chunkerWorld) {
        // Not used, we flush preview data in the column writer
    }

    @Override
    public void flushWorlds() throws IOException {
        for (RandomAccessFile randomAccessFile : mcaFiles.values()) {
            try {
                randomAccessFile.close();
            } catch (Exception e) {
                // Log it but not much we can do
                converter.logNonFatalException(e);
            }
        }
    }

    /**
     * Write MCA chunk data to the disk.
     *
     * @param dimension      the dimension to write data for.
     * @param chunkCoordPair the chunk being written.
     * @param chunkData      the data for the chunk.
     * @throws Exception if it failed to write the data.
     */
    public void writeMCAData(Dimension dimension, ChunkCoordPair chunkCoordPair, CompoundTag chunkData) throws Exception {
        File directory = new File(JavaLevelReader.getDimensionBaseDirectory(outputFolder, dimension), "region");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File regionFile = new File(directory, "r." + chunkCoordPair.regionX() + "." + chunkCoordPair.regionZ() + ".mca");
        writeMCAData(regionFile, chunkCoordPair, chunkData);
    }

    /**
     * Write MCA POI data to the disk.
     *
     * @param dimension      the dimension to write data for.
     * @param chunkCoordPair the chunk being written.
     * @param chunkData      the data for the chunk.
     * @throws Exception if it failed to write the data.
     */
    public void writePOIData(Dimension dimension, ChunkCoordPair chunkCoordPair, CompoundTag chunkData) throws Exception {
        File directory = new File(JavaLevelReader.getDimensionBaseDirectory(outputFolder, dimension), "poi");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File regionFile = new File(directory, "r." + chunkCoordPair.regionX() + "." + chunkCoordPair.regionZ() + ".mca");
        writeMCAData(regionFile, chunkCoordPair, chunkData);
    }

    /**
     * Write MCA data to the disk.
     *
     * @param file           file to write data to.
     * @param chunkCoordPair the chunk being written.
     * @param chunkData      the data for the chunk.
     * @throws Exception if it failed to write the data.
     */
    protected void writeMCAData(File file, ChunkCoordPair chunkCoordPair, CompoundTag chunkData) throws Exception {
        // TODO: Close MCA files on crash to prevent holding resources

        // Convert the chunk to zlib bytes
        byte[] bytes = Tag.writeZLibJavaNBT(chunkData);
        long columnLookupOffset = ((chunkCoordPair.chunkX() & 31) + ((chunkCoordPair.chunkZ() & 31) << 5)) << 2;

        // Check whether an oversized file should be used
        boolean oversized = bytes.length >= OVERSIZED_THRESHOLD;

        // Write the oversized file data
        if (oversized) {
            File oversizedFile = new File(file.getParent(), "c." + chunkCoordPair.chunkX() + "." + chunkCoordPair.chunkZ() + ".mcc");
            Files.write(oversizedFile.toPath(), bytes);
        }

        // Sectors are 4KB blocks
        // sectorCount = bytes.length + length header + compression type
        int sectorCount = oversized ? 1 : (int) Math.ceil((bytes.length + 5) / 4096D);

        // Compute the file
        RandomAccessFile randomAccessFile = mcaFiles.computeIfAbsent(file, (target) -> {
            try {
                return new RandomAccessFile(target, "rw");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        // We'll need to use locks to ensure we're not writing two columns at once
        synchronized (randomAccessFile) {
            Writer writer = Writer.toJavaWriter(randomAccessFile);

            // Start by writing the data at the end (ensure we don't write in the header space)
            randomAccessFile.seek(Math.max(8192, randomAccessFile.length()));

            // Get the current position, so it can be used to know the sector
            long position = randomAccessFile.getFilePointer();
            int sectorPosition = (int) (position >> 12);

            // Write the data with length/type
            if (oversized) {
                writer.writeInt(1); // 1 for compression type
                writer.writeByte(0x80 | 2); // External file byte + ZLib compression = 2
            } else {
                writer.writeInt(bytes.length + 1); // size + 1 for compression type
                writer.writeByte(2); // ZLib compression = 2
                writer.writeBytes(bytes);
            }

            // Write any needed padding so file can be looked up in 4kb sectors
            randomAccessFile.setLength((long) (sectorPosition + sectorCount) << 12);

            // Write the location of our data + sector count to the right place
            randomAccessFile.seek(columnLookupOffset);
            writer.writeUnsignedInt24(sectorPosition);
            writer.writeByte(sectorCount);
        }
    }

    /**
     * Create a new column writer.
     *
     * @param dimension the dimension the column is inside.
     * @return the newly made column writer.
     */
    public JavaColumnWriter createColumnWriter(Dimension dimension) {
        return new JavaColumnWriter(this, converter, resolvers, dimension);
    }
}
