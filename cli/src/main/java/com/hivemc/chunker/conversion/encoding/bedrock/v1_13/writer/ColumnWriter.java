package com.hivemc.chunker.conversion.encoding.bedrock.v1_13.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockChunkWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockColumnWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockWorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;

public class ColumnWriter extends BedrockColumnWriter {
    public ColumnWriter(BedrockWorldWriter parent, Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension) {
        super(parent, converter, resolvers, database, dimension);
    }

    @Override
    public BedrockChunkWriter createChunkWriter(ChunkerColumn column) {
        return new ChunkWriter(converter, resolvers, database, dimension, column);
    }
}
