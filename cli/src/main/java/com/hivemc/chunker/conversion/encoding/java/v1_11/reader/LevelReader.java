package com.hivemc.chunker.conversion.encoding.java.v1_11.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaLevelReader;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaWorldReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolversBuilder;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.JavaEntityTypeResolver;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

import java.io.File;

public class LevelReader extends JavaLevelReader {
    public LevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        super(inputDirectory, inputVersion, converter);
    }

    @Override
    public JavaWorldReader createWorldReader(File dimensionFolder, Dimension dimension) {
        return new WorldReader(converter, resolvers, dimensionFolder, dimension);
    }

    @Override
    public JavaResolversBuilder buildResolvers(Converter converter) {
        Version version = getVersion();

        // Use modern item / block resolvers
        return super.buildResolvers(converter)
                .entityTypeResolver(new JavaEntityTypeResolver(version, converter.shouldAllowCustomIdentifiers()));
    }
}
