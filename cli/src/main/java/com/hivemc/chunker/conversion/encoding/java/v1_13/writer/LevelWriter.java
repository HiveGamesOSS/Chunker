package com.hivemc.chunker.conversion.encoding.java.v1_13.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.JavaReaderPreTransformManager;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolversBuilder;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.JavaBlockEntityResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.JavaEntityResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.JavaPaintingMotiveResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaItemIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack.JavaItemStackResolver;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import com.hivemc.chunker.conversion.encoding.java.base.writer.pretransform.JavaWriterPreTransformManager;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerGeneratorType;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelSettings;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;

import java.io.File;
import java.util.Map;

public class LevelWriter extends com.hivemc.chunker.conversion.encoding.java.v1_11.writer.LevelWriter {
    public LevelWriter(File outputFolder, Version version, Converter converter) {
        super(outputFolder, version, converter);
    }

    @Override
    public void writeCustomLevelSetting(ChunkerLevelSettings chunkerLevelSettings, CompoundTag output, String targetName, Object value) {
        if (value instanceof ChunkerGeneratorType generatorType) {
            // If the world doesn't support the custom type, use void instead
            if (!converter.shouldAllowNBTCopying() && generatorType == ChunkerGeneratorType.CUSTOM) {
                // Force void if no custom data
                generatorType = ChunkerGeneratorType.VOID;
            }

            // Based on the generatorType we write different data
            switch (generatorType) {
                case NORMAL:
                    output.put("generatorName", "default");
                    output.put("generatorVersion", 0);
                    return;
                case FLAT:
                    output.put("generatorName", "flat");
                    output.put("generatorVersion", 0);
                    return;
                case VOID:
                    output.put("generatorName", "flat");
                    output.put("generatorVersion", 0);

                    CompoundTag generatorOptions = new CompoundTag(2);
                    generatorOptions.put("biome", resolvers.writeBiome(ChunkerBiome.ChunkerVanillaBiome.PLAINS, Dimension.OVERWORLD));
                    ListTag<CompoundTag, Map<String, Tag<?>>> layers = new ListTag<>(TagType.COMPOUND, 1);

                    CompoundTag airLayer = new CompoundTag(2);
                    airLayer.put("block", "minecraft:air");
                    airLayer.put("height", (byte) 1);
                    layers.add(airLayer);

                    generatorOptions.put("layers", layers);
                    output.put("generatorOptions", generatorOptions);
                    return;
                case CUSTOM:
                    // Don't write anything
                    return;
            }
        }
        // Fallback to previous
        super.writeCustomLevelSetting(chunkerLevelSettings, output, targetName, value);
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
                .entityResolverConstructor((resolvers) -> new JavaEntityResolver(version, resolvers))
                .blockEntityResolverConstructor((resolvers) -> new JavaBlockEntityResolver(version, resolvers))
                .itemStackResolverConstructor(JavaItemStackResolver::new)
                .paintingMotiveResolver(new JavaPaintingMotiveResolver(version))
                .itemIdentifierResolver(new JavaItemIdentifierResolver(converter, version, isReader()))
                .blockIdentifierResolver(new JavaBlockIdentifierResolver(converter, version, isReader(), converter.shouldAllowCustomIdentifiers()))
                .preTransformManager(isReader() ? new JavaReaderPreTransformManager(version) : new JavaWriterPreTransformManager(version));
    }
}
