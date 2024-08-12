package com.hivemc.chunker.conversion.encoding.java.v1_20_3.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaWorldReader;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class LevelReader extends com.hivemc.chunker.conversion.encoding.java.v1_20.reader.LevelReader {
    public LevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        super(inputDirectory, inputVersion, converter);
    }

    @Override
    public @Nullable Object readCustomLevelSetting(@NotNull CompoundTag root, @NotNull String targetName, @NotNull Class<?> type) {
        // Check for experimental
        if (targetName.equals("R21Support")) {
            CompoundTag dataPacks = root.getCompound("DataPacks");
            if (dataPacks == null) return false;

            return dataPacks.contains("Enabled") &&
                    dataPacks.getList("Enabled", StringTag.class).contains("update_1_21");
        }
        return super.readCustomLevelSetting(root, targetName, type);
    }

    @Override
    public JavaWorldReader createWorldReader(File dimensionFolder, Dimension dimension) {
        return new WorldReader(converter, resolvers, dimensionFolder, dimension);
    }
}
