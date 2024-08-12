package com.hivemc.chunker.conversion.encoding.bedrock.v1_17_30.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockCompoundTag;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.io.Writer;
import org.iq80.leveldb.DB;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ChunkWriter extends com.hivemc.chunker.conversion.encoding.bedrock.v1_17.writer.ChunkWriter {
    public ChunkWriter(Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension, ChunkerColumn chunkerColumn) {
        super(converter, resolvers, database, dimension, chunkerColumn);
    }

    @Override
    protected void writeBlockPalette(ChunkerChunk chunk) throws Exception {
        byte[] bytes;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            Writer writer = Writer.toBedrockWriter(dataOutputStream);

            writer.writeByte((byte) 9); // Version 9 palette with extra index

            Palette<BedrockBlockCompoundTag> blockPalette = chunk.getPalette().map(resolvers::writeBlock);

            // Calculate waterlogged states
            boolean containsWaterlogged = blockPalette.containsValue(BedrockBlockCompoundTag::waterlogged);

            // Write size
            int layers = containsWaterlogged ? 2 : 1;
            writer.writeByte((byte) layers); // Size

            // Write index
            writer.writeByte(chunk.getY());

            // Write each layer
            writeLayers(writer, layers, blockPalette);

            // Collect the byte array for leveldb
            bytes = byteArrayOutputStream.toByteArray();
        }

        // Write the entry
        writeChunkBytes(chunk, bytes);
    }

    @Override
    protected void writeChunkBytes(ChunkerChunk chunk, byte[] bytes) {
        byte subChunkY = chunk.getY();
        database.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), subChunkY, LevelDBChunkType.SUB_CHUNK_PREFIX), bytes);
    }
}
