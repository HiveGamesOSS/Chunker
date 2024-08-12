package com.hivemc.chunker.conversion.encoding.bedrock.v1_13.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockColumnWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockWorldWriter;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;

import java.io.File;

public class WorldWriter extends BedrockWorldWriter {
    public WorldWriter(File outputFolder, Converter converter, BedrockResolvers resolvers, DB database) {
        super(outputFolder, converter, resolvers, database);
    }

    @Override
    public BedrockColumnWriter createColumnWriter(Dimension dimension) {
        return new ColumnWriter(this, converter, resolvers, database, dimension);
    }
}
