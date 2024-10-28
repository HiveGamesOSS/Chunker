package com.hivemc.chunker.conversion.encoding.java.v1_11.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolversBuilder;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.JavaEntityTypeResolver;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaLevelWriter;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;

import java.io.File;

public class LevelWriter extends JavaLevelWriter {
    public LevelWriter(File outputFolder, Version version, Converter converter) {
        super(outputFolder, version, converter);
    }

    @Override
    public JavaWorldWriter createWorldWriter() {
        return new WorldWriter(outputFolder, converter, resolvers);
    }

    @Override
    public JavaResolversBuilder buildResolvers(Converter converter) {
        Version version = getVersion();

        // Use modern item / block resolvers
        return super.buildResolvers(converter)
                .entityTypeResolver(new JavaEntityTypeResolver(version, converter.shouldAllowCustomIdentifiers()));
    }
}
