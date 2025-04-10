package com.hivemc.chunker.conversion.encoding.java.v1_21_6.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaColumnWriter;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

import java.io.File;

public class WorldWriter extends com.hivemc.chunker.conversion.encoding.java.v1_21_4.writer.WorldWriter {
    public WorldWriter(File outputFolder, Converter converter, JavaResolvers resolvers) {
        super(outputFolder, converter, resolvers);
    }

    @Override
    public JavaColumnWriter createColumnWriter(Dimension dimension) {
        return new ColumnWriter(this, converter, resolvers, dimension);
    }
}
