package com.hivemc.chunker.conversion.encoding.java.v1_16.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaWorldReader;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerGeneratorType;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class LevelReader extends com.hivemc.chunker.conversion.encoding.java.v1_15.reader.LevelReader {
    public LevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        super(inputDirectory, inputVersion, converter);
    }

    @Override
    public JavaWorldReader createWorldReader(File dimensionFolder, Dimension dimension) {
        return new WorldReader(converter, resolvers, dimensionFolder, dimension);
    }

    @Override
    public @Nullable Object readCustomLevelSetting(@NotNull CompoundTag root, @NotNull String targetName, @NotNull Class<?> type) {
        // Default implementation for seed
        if (targetName.equals("RandomSeed")) {
            // Try old property
            if (root.contains("RandomSeed")) return String.valueOf(root.getLong("RandomSeed"));

            // Try new property
            CompoundTag worldGenSettings = root.getCompound("WorldGenSettings");
            if (worldGenSettings == null) return null;

            // If seed exists we'll use that
            return worldGenSettings.getOptionalValue("seed", Long.class).map(String::valueOf).orElse(null);
        }

        if (type == ChunkerGeneratorType.class) {
            // If string format, use legacy
            if (root.contains("generatorOptions") && root.get("generatorOptions") instanceof StringTag) {
                return super.readCustomLevelSetting(root, targetName, type);
            }

            // Try new property
            CompoundTag worldGenSettings = root.getCompound("WorldGenSettings");
            if (worldGenSettings == null) return ChunkerGeneratorType.NORMAL;

            // Grab the tag and check it has dimensions
            CompoundTag dimensions = worldGenSettings.getCompound("dimensions");
            if (dimensions == null) return ChunkerGeneratorType.NORMAL;

            // If there's no overworld we assume it's custom
            CompoundTag overWorld = dimensions.getCompound("minecraft:overworld");
            if (overWorld == null) return ChunkerGeneratorType.CUSTOM;

            // If there's no generator we assume default
            CompoundTag generator = overWorld.getCompound("generator");
            if (generator == null) return ChunkerGeneratorType.NORMAL;

            // Grab the generator type
            String generatorName = generator.getString("type", null);

            // If there's no type, probably still default or if it's noise
            if (generatorName == null || generatorName.equals("minecraft:noise")) {
                return ChunkerGeneratorType.NORMAL;
            }

            // If the generator is flat, it could be void or flat, we'll need to check the layers
            if (generatorName.equals("minecraft:flat")) {
                CompoundTag generatorOptions = generator.getCompound("settings");
                if (generatorOptions == null) return ChunkerGeneratorType.FLAT;

                // Grab the layers for the flat generator
                Tag<?> layers = generatorOptions.get("layers");
                return inferGeneratorTypeFromLayers(layers);
            }

            // Unknown
            return ChunkerGeneratorType.CUSTOM;
        }

        return super.readCustomLevelSetting(root, targetName, type);
    }
}
