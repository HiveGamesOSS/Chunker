package com.hivemc.chunker.conversion.encoding.bedrock.base.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockCompoundTag;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.encoding.bedrock.util.PaletteUtil;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.LiquidLevel;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.ShortBasedPalette;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.scheduling.function.ThrowableConsumer;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import org.iq80.leveldb.DB;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A writer for Bedrock chunks.
 */
public class BedrockChunkWriter {
    protected final BedrockResolvers resolvers;
    protected final DB database;
    protected final Converter converter;
    protected final Dimension dimension;
    protected final ChunkerColumn chunkerColumn;

    /**
     * Create a new Bedrock Chunk Writer.
     *
     * @param converter     the converter instance.
     * @param resolvers     the resolvers being used.
     * @param database      the LevelDB database.
     * @param dimension     the dimension of the chunk.
     * @param chunkerColumn the column being written.
     */
    public BedrockChunkWriter(Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension, ChunkerColumn chunkerColumn) {
        this.resolvers = resolvers;
        this.database = database;
        this.converter = converter;
        this.dimension = dimension;
        this.chunkerColumn = chunkerColumn;
    }

    /**
     * Write the chunk to LevelDB.
     *
     * @param chunkerChunk the chunk being written.
     */
    public void writeChunk(@Nullable ChunkerChunk chunkerChunk) {
        // Don't write if it's empty and discard is enabled
        if (chunkerChunk == null || converter.shouldDiscardEmptyChunks() && chunkerChunk.isEmpty()) return;
        if (!isChunkHeightSupported(chunkerChunk.getY())) return;

        // Create the tasks
        createChunkTasks(chunkerChunk);
    }

    /**
     * Create the tasks used for writing the chunk data.
     *
     * @param chunkerChunk the chunk being written.
     * @return a list of tasks that represent the writing of the chunk.
     */
    protected List<Task<Void>> createChunkTasks(ChunkerChunk chunkerChunk) {
        List<Task<Void>> tasks = new ArrayList<>();
        tasks.add(Task.asyncConsume("Writing Block Palette", TaskWeight.HIGH, this::writeBlockPalette, chunkerChunk));
        return tasks;
    }

    /**
     * Check whether a chunk height is supported.
     *
     * @param chunkY the index Y of the chunk.
     * @return true if the chunk height is supported.
     */
    protected boolean isChunkHeightSupported(byte chunkY) {
        return chunkY >= 0 && chunkY < 16; // 0-15 is supported for every version (higher needs a newer version)
    }

    /**
     * Write the block palette data to LevelDB.
     *
     * @param chunk the chunk being written.
     * @throws Exception if it failed to write the palette.
     */
    protected void writeBlockPalette(ChunkerChunk chunk) throws Exception {
        byte[] bytes;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            Writer writer = Writer.toBedrockWriter(dataOutputStream);

            writer.writeByte((byte) 8); // Version 8 chunks, palette

            Palette<BedrockBlockCompoundTag> blockPalette = chunk.getPalette().map(resolvers::writeBlock);

            // Calculate waterlogged states
            boolean containsWaterlogged = blockPalette.containsValue(BedrockBlockCompoundTag::waterlogged);

            // Write size
            int layers = containsWaterlogged ? 2 : 1;
            writer.writeByte((byte) layers); // Size

            // Write each layer
            writeLayers(writer, layers, blockPalette);

            // Collect the byte array for leveldb
            bytes = byteArrayOutputStream.toByteArray();
        }

        writeChunkBytes(chunk, bytes);
    }

    /**
     * Write the block palette for the chunk.
     *
     * @param chunk the chunk input.
     * @param bytes the bytes for the block palette.
     */
    protected void writeChunkBytes(ChunkerChunk chunk, byte[] bytes) {
        // Write the entry
        byte subChunkY = chunk.getY();
        database.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), subChunkY, LevelDBChunkType.SUB_CHUNK_PREFIX), bytes);
    }

    /**
     * Write the block layer data to a writer.
     *
     * @param writer       the output writer.
     * @param layers       the number of layers (1 if a liquid layer should be generated).
     * @param blockPalette the block palette to use.
     * @throws Exception if it failed to write the palette.
     */
    protected void writeLayers(Writer writer, int layers, Palette<BedrockBlockCompoundTag> blockPalette) throws Exception {
        ThrowableConsumer<BedrockBlockCompoundTag> writeKey = (entry) -> Tag.encodeNamed(writer, "", entry.compoundTag());
        // Loop through each layer
        for (int layer = 0; layer < layers; layer++) {
            if (layer == 0) {
                PaletteUtil.writeChunkPalette(writer, blockPalette, false, false, writeKey);
            } else {
                // Generate liquid layer
                Palette<BedrockBlockCompoundTag> liquidPalette = generateLiquidPalette(blockPalette);
                PaletteUtil.writeChunkPalette(writer, liquidPalette, false, false, writeKey);
            }
        }
    }

    /**
     * Generate a liquid palette from the block palette, looking for waterlogged states.
     *
     * @param blockPalette the block palette input.
     * @return a generated liquid palette.
     */
    protected Palette<BedrockBlockCompoundTag> generateLiquidPalette(Palette<BedrockBlockCompoundTag> blockPalette) {
        short[][][] values = new short[16][16][16];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    BedrockBlockCompoundTag block = blockPalette.get(x, y, z);
                    values[x][y][z] = (short) (block == null || !block.waterlogged() ? 0 : 1);
                }
            }
        }
        List<BedrockBlockCompoundTag> keys = List.of(
                resolvers.writeBlock(ChunkerBlockIdentifier.AIR),
                resolvers.writeBlock(new ChunkerBlockIdentifier(ChunkerVanillaBlockType.WATER, Map.of(
                        VanillaBlockStates.LIQUID_LEVEL, LiquidLevel._0,
                        VanillaBlockStates.FLOWING, Bool.FALSE
                )))
        );
        return new ShortBasedPalette<>(keys, values);
    }
}
