package com.hivemc.chunker.conversion.encoding.bedrock.v1_17.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerGeneratorType;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;

public class ChunkWriter extends com.hivemc.chunker.conversion.encoding.bedrock.v1_16.writer.ChunkWriter {
    public ChunkWriter(Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension, ChunkerColumn chunkerColumn) {
        super(converter, resolvers, database, dimension, chunkerColumn);
    }

    @Override
    protected boolean isChunkHeightSupported(byte chunkY) {
        return super.isChunkHeightSupported(chunkY) || converter.level().map(level -> level.getSettings().CavesAndCliffs).orElse(true);
    }

    @Override
    protected void writeChunkBytes(ChunkerChunk chunk, byte[] bytes) {
        // Write the entry
        byte subChunkY = chunk.getY();

        // Only use CaC when the generator is normal as it is only support there
        if (converter.level().map(level -> level.getSettings().CavesAndCliffs).orElse(true)
                && dimension == Dimension.OVERWORLD
                && converter.level().map(level -> level.getSettings().GeneratorType).orElse(ChunkerGeneratorType.CUSTOM) == ChunkerGeneratorType.NORMAL) {
            subChunkY += 4; // Move up 4 for Caves & Cliffs in older versions
        }
        database.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), subChunkY, LevelDBChunkType.SUB_CHUNK_PREFIX), bytes);
    }
}
