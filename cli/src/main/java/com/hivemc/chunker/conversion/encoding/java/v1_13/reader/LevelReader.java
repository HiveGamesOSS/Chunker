package com.hivemc.chunker.conversion.encoding.java.v1_13.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaWorldReader;
import com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.JavaReaderPreTransformManager;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolversBuilder;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.JavaBlockEntityResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.JavaEntityResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.JavaPaintingMotiveResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaItemIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack.JavaItemStackResolver;
import com.hivemc.chunker.conversion.encoding.java.base.writer.pretransform.JavaWriterPreTransformManager;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerGeneratorType;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class LevelReader extends com.hivemc.chunker.conversion.encoding.java.v1_11.reader.LevelReader {
    public LevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        super(inputDirectory, inputVersion, converter);
    }

    @Override
    public @Nullable Object readCustomLevelSetting(@NotNull CompoundTag root, @NotNull String targetName, @NotNull Class<?> type) {
        if (type == ChunkerGeneratorType.class) {
            String generatorName = root.getString("generatorName", null);
            if (generatorName == null) {
                return ChunkerGeneratorType.NORMAL; // Default to normal
            }

            if (generatorName.equals("default")) {
                return ChunkerGeneratorType.CUSTOM; // Return custom as there is only generation parity in 1.16+
            }

            if (generatorName.equals("flat")) {
                Tag<?> generatorOptions = root.get("generatorOptions");
                if (generatorOptions == null) {
                    return ChunkerGeneratorType.FLAT; // Unknown generator type, use flat
                } else if (!(generatorOptions instanceof CompoundTag)) {
                    // Use legacy parsing if the generatorOptions are a string
                    return super.readCustomLevelSetting(root, targetName, type);
                }

                // Detect whether the layers look like a flat/void world
                CompoundTag generatorOptionsCompound = (CompoundTag) generatorOptions;
                return inferGeneratorTypeFromLayers(generatorOptionsCompound.get("layers"));
            }

            // Unknown
            return ChunkerGeneratorType.CUSTOM;
        }

        // Fallback to previous
        return super.readCustomLevelSetting(root, targetName, type);
    }

    protected ChunkerGeneratorType inferGeneratorTypeFromLayers(@Nullable Tag<?> layers) {
        if (layers == null) return ChunkerGeneratorType.FLAT;

        // Edge-case for empty layers writing the wrong tag type
        if (!(layers instanceof ListTag<?, ?> layersList)) {
            return ChunkerGeneratorType.VOID;
        }

        // Find whether this is a void preset
        boolean air = true;
        for (Tag<?> layerTag : layersList) {
            if (layerTag instanceof CompoundTag layerCompoundTag) {
                String blockIdentifier = layerCompoundTag.getString("block", "minecraft:air");

                // Check if it's air
                if (!blockIdentifier.equals("minecraft:air")) {
                    air = false;
                }
            }

        }
        if (air) {
            return ChunkerGeneratorType.VOID;
        }

        // Check for flat world
        if (layersList.size() == 3) {
            // Look for basic flat world
            if (((CompoundTag) layersList.get(0)).getString("block", null).equals("minecraft:bedrock")) {
                if (((CompoundTag) layersList.get(1)).getString("block", null).equals("minecraft:dirt")) {
                    if (((CompoundTag) layersList.get(2)).getString("block", null).equals("minecraft:grass_block")) {
                        return ChunkerGeneratorType.FLAT;
                    }
                }
            }
        }

        return ChunkerGeneratorType.CUSTOM;
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
                .entityResolverConstructor((resolvers) -> new JavaEntityResolver(version, resolvers))
                .blockEntityResolverConstructor((resolvers) -> new JavaBlockEntityResolver(version, resolvers))
                .itemStackResolverConstructor(JavaItemStackResolver::new)
                .paintingMotiveResolver(new JavaPaintingMotiveResolver(version))
                .itemIdentifierResolver(new JavaItemIdentifierResolver(converter, version, isReader()))
                .blockIdentifierResolver(new JavaBlockIdentifierResolver(converter, version, isReader(), converter.shouldAllowCustomIdentifiers()))
                .preTransformManager(isReader() ? new JavaReaderPreTransformManager(version) : new JavaWriterPreTransformManager(version));
    }
}
