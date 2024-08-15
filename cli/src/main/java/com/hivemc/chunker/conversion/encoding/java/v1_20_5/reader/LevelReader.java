package com.hivemc.chunker.conversion.encoding.java.v1_20_5.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaWorldReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolversBuilder;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack.JavaComponentItemStackResolver;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

import java.io.File;

public class LevelReader extends com.hivemc.chunker.conversion.encoding.java.v1_20_3.reader.LevelReader {
    public LevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        super(inputDirectory, inputVersion, converter);
    }

    @Override
    public JavaWorldReader createWorldReader(File dimensionFolder, Dimension dimension) {
        return new WorldReader(converter, resolvers, dimensionFolder, dimension);
    }

    @Override
    public JavaResolversBuilder buildResolvers(Converter converter) {
        return super.buildResolvers(converter)
                .itemStackResolverConstructor(JavaComponentItemStackResolver::new);
    }
}
