package com.hivemc.chunker.conversion.encoding.bedrock.v1_21_100.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockWorldReader;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class LevelReader extends com.hivemc.chunker.conversion.encoding.bedrock.v1_21_90.reader.LevelReader {
    public LevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        super(inputDirectory, inputVersion, converter);
    }

    @Override
    public @Nullable Object readCustomLevelSetting(@NotNull CompoundTag root, @NotNull String targetName, @NotNull Class<?> type) {
        // Check for AutumnDrop2025
        if (targetName.equals("AutumnDrop2025")) {
            // Check experiment
            CompoundTag experiments = root.getCompound("experiments");
            if (experiments == null) return false;

            // If the byte is set next update is enabled
            return experiments.getByte("y_2025_drop_3", (byte) 0) == (byte) 1;
        }
        return super.readCustomLevelSetting(root, targetName, type);
    }

    @Override
    public BedrockWorldReader createWorldReader(Map<RegionCoordPair, Set<ChunkCoordPair>> presentRegions, Dimension dimension) {
        return new WorldReader(resolvers, converter, database, presentRegions, dimension);
    }
}
