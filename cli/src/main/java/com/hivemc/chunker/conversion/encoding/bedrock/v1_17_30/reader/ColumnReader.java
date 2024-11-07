package com.hivemc.chunker.conversion.encoding.bedrock.v1_17_30.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockChunkReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ColumnReader extends com.hivemc.chunker.conversion.encoding.bedrock.v1_17.reader.ColumnReader {
    public ColumnReader(BedrockResolvers resolvers, Converter converter, DB database, Dimension dimension, ChunkCoordPair columnCoords) {
        super(resolvers, converter, database, dimension, columnCoords);
    }

    @Override
    protected void readBiomeHeightMap(ChunkerColumn column) {
        try {
            // Read Data3D first (1.18+)
            byte[] value = database.get(LevelDBKey.key(dimension, column.getPosition(), LevelDBChunkType.DATA_3D));
            if (value != null) {
                ByteBuffer buffer = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
                if (converter.shouldProcessHeightMap()) {
                    column.setHeightMap(readHeightMap(buffer));
                } else {
                    // Skip the bytes
                    buffer.position(buffer.position() + 256);
                }
                if (converter.shouldProcessBiomes()) {
                    column.setBiomes(readBiomesExtended(buffer));
                }
                return; // We're done
            }

            // Fallback to legacy
            super.readBiomeHeightMap(column);
        } catch (Exception e) {
            converter.logNonFatalException(e);
        }
    }

    @Override
    public BedrockChunkReader createChunkReader(ChunkerChunk chunk) {
        return new ChunkReader(resolvers, converter, dimension, chunk);
    }
}
