package com.hivemc.chunker.conversion.encoding.bedrock.v1_18.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockChunkWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockWorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;

public class ColumnWriter extends com.hivemc.chunker.conversion.encoding.bedrock.v1_17_30.writer.ColumnWriter {
    public ColumnWriter(BedrockWorldWriter parent, Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension) {
        super(parent, converter, resolvers, database, dimension);
    }

    @Override
    protected boolean isWriteBlendingData() {
        return true; // Always write blending data
    }

    @Override
    public BedrockChunkWriter createChunkWriter(ChunkerColumn column) {
        return new ChunkWriter(converter, resolvers, database, dimension, column);
    }
}
