package com.hivemc.chunker.conversion.encoding.java.v1_20_5.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolversBuilder;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack.JavaComponentItemStackResolver;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;

import java.io.File;

public class LevelWriter extends com.hivemc.chunker.conversion.encoding.java.v1_20_3.writer.LevelWriter {
    public LevelWriter(File outputFolder, Version version, Converter converter) {
        super(outputFolder, version, converter);
    }

    @Override
    public JavaWorldWriter createWorldWriter() {
        return new WorldWriter(outputFolder, converter, resolvers);
    }

    @Override
    public JavaResolversBuilder buildResolvers(Converter converter) {
        return super.buildResolvers(converter)
                .itemStackResolverConstructor(JavaComponentItemStackResolver::new);
    }
}
