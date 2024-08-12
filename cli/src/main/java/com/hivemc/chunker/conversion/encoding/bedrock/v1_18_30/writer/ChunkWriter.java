package com.hivemc.chunker.conversion.encoding.bedrock.v1_18_30.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;

public class ChunkWriter extends com.hivemc.chunker.conversion.encoding.bedrock.v1_18.writer.ChunkWriter {
    public ChunkWriter(Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension, ChunkerColumn chunkerColumn) {
        super(converter, resolvers, database, dimension, chunkerColumn);
    }
}
