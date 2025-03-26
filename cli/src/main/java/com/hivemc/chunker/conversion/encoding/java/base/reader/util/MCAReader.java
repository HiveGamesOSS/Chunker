package com.hivemc.chunker.conversion.encoding.java.base.reader.util;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

/**
 * Utility for reading Java .mca files.
 */
public class MCAReader implements AutoCloseable {
    private final Converter converter;
    private final File folder;
    private final RandomAccessFile randomAccessFile;
    private final Reader reader;

    /**
     * Create a new MCAReader (must be closed after).
     *
     * @param converter the converter instance.
     * @param file      the mca file to read.
     * @throws FileNotFoundException if the file wasn't present.
     */
    public MCAReader(Converter converter, File file) throws FileNotFoundException {
        this.converter = converter;
        folder = file.getParentFile();
        randomAccessFile = new RandomAccessFile(file, "r");
        reader = Reader.toJavaReader(randomAccessFile);
    }

    /**
     * Read the offset table used for indexing the columns from the .mca file.
     *
     * @return the table of indexes for the columns (1024 columns in an MCA file).
     * @throws IOException if it failed to read the offset table.
     */
    public int[] readOffsetTable() throws IOException {
        int[] offsets = new int[1024];

        // Read into temporary buffer
        byte[] temp = new byte[4096];
        reader.readBytes(temp);

        // Read the header which contains the chunk offsets
        for (int i = 0; i < 1024; i++) {
            int tempIndex = i << 2;
            int offset = ((temp[tempIndex] & 0xFF) << 16) |
                    ((temp[tempIndex + 1] & 0xFF) << 8) |
                    (temp[tempIndex + 2] & 0xFF);
            // Skip sector count byte (unused, we can validate without it)

            // Only record the offset if it's more than 0, the first 4096 is the header, so it's invalid to be there
            if (offset > 0) {
                offsets[i] = offset;
            }
        }

        return offsets;
    }

    /**
     * Read a column at an offset.
     *
     * @param columnCoordPair the global position being read at the offset (used for oversized chunks).
     * @param offset          the offset to read at.
     * @return the task which is reading and decompressing the chunk.
     * @throws IOException if it failed to read the .mca file.
     */
    public Task<CompoundTag> readColumn(ChunkCoordPair columnCoordPair, int offset) throws IOException {
        // Seek to the location of the column (4096 sized chunks)
        randomAccessFile.seek(offset * 4096L);

        int chunkLength = reader.readInt() - 1; // Minus 1 as it includes compression type

        // Check compression type
        byte rawType = reader.readByte();
        byte compressionType = (byte) (rawType & ~0x80);

        byte[] compressedColumn;
        // Check for oversized chunks (external files)
        if ((rawType & 0x80) != 0) {
            // Read the external file
            File file = new File(folder, "c." + columnCoordPair.chunkX() + "." + columnCoordPair.chunkZ() + ".mcc");
            compressedColumn = Files.readAllBytes(file.toPath());
        } else {
            // Read bytes for later processing
            compressedColumn = new byte[chunkLength];
            reader.readBytes(compressedColumn);
        }

        // Make a task to do the decompression, this is allowed async as we don't need to do any more sync operations
        return Task.async(
                "Decompressing column data",
                TaskWeight.HIGH,
                () -> decompressColumn(columnCoordPair, compressionType, compressedColumn)
        );
    }

    /**
     * Decompress a column.
     *
     * @param chunkCoordPair   the co-ordinates of the chunk being decompressed (used for exceptions).
     * @param compressionType  the compression type.
     * @param compressedColumn the column to be decompressed.
     * @return the decompressed compound tag.
     * @throws IOException if it failed to decompress / read.
     */
    protected CompoundTag decompressColumn(ChunkCoordPair chunkCoordPair, byte compressionType, byte[] compressedColumn) throws IOException {
        try {
            // LZ4 was added in 1.20.5, but there is no harm supporting it here
            return switch (compressionType) {
                case 0 -> null; // Empty
                case 1 -> Tag.readGZipJavaNBT(compressedColumn); // GZip
                case 2 -> Tag.readZLibJavaNBT(compressedColumn); // Deflate - Default
                case 3 -> Tag.readUncompressedJavaNBT(compressedColumn);// Uncompressed
                case 4 -> Tag.readLZ4JavaNBT(compressedColumn); // LZ4
                default -> {
                    converter.logNonFatalException(new Exception("Unsupported Chunk Compression Type " + compressionType));
                    yield null;
                }
            };
        } catch (Exception e) {
            throw new IOException("Failed to decompress column %s inside %s".formatted(chunkCoordPair, chunkCoordPair.getRegion()), e);
        }
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }
}
