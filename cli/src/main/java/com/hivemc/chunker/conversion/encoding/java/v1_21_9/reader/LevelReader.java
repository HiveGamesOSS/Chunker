package com.hivemc.chunker.conversion.encoding.java.v1_21_9.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaWorldReader;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class LevelReader extends com.hivemc.chunker.conversion.encoding.java.v1_21_6.reader.LevelReader {
    public LevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        super(inputDirectory, inputVersion, converter);
    }

    @Override
    public @Nullable Object readCustomLevelSetting(@NotNull CompoundTag root, @NotNull String targetName, @NotNull Class<?> type) {
        if (targetName.equals("AutumnDrop2025")) return true; // Supported for this release
        return super.readCustomLevelSetting(root, targetName, type);
    }

    @Override
    protected CompoundTag prepareNBTForLevelSettings(CompoundTag level) throws Exception {
        // Call super
        level = super.prepareNBTForLevelSettings(level);

        // Make a copy (this ensures that we don't overwrite the original
        level = level.clone();

        // Extract the new spawn tag
        if (level.contains("spawn")) {
            // Transform all the Spawn settings to the old names so we can parse it
            CompoundTag spawnTag = (CompoundTag) level.remove("spawn");
            if (spawnTag.contains("pos")) {
                int[] position = spawnTag.getIntArray("pos");
                level.put("SpawnX", position[0]);
                level.put("SpawnY", position[1]);
                level.put("SpawnZ", position[2]);
            }
            if (spawnTag.contains("pitch")) {
                level.put("SpawnPitch", spawnTag.getFloat("pitch"));
            }
            if (spawnTag.contains("yaw")) {
                level.put("SpawnYaw", spawnTag.getFloat("yaw"));
            }
            if (spawnTag.contains("dimension")) {
                level.put("SpawnDimension", spawnTag.getString("dimension"));
            }
        }

        // Extract the new world_border tag (legacy)
        if (level.contains("world_border")) {
            // Transform all the world border settings so we can parse it
            CompoundTag worldBorderTag = (CompoundTag) level.remove("world_border");
            if (worldBorderTag.contains("center_x")) {
                level.put("BorderCenterX", worldBorderTag.getDouble("center_x"));
            }
            if (worldBorderTag.contains("center_z")) {
                level.put("BorderCenterZ", worldBorderTag.getDouble("center_z"));
            }
            if (worldBorderTag.contains("size")) {
                level.put("BorderSize", worldBorderTag.getDouble("size"));
            }
            if (worldBorderTag.contains("lerp_time")) {
                level.put("BorderSizeLerpTime", worldBorderTag.getLong("lerp_time"));
            }
            if (worldBorderTag.contains("lerp_target")) {
                level.put("BorderSizeLerpTarget", worldBorderTag.getDouble("lerp_target"));
            }
            if (worldBorderTag.contains("safe_zone")) {
                level.put("BorderSafeZone", worldBorderTag.getDouble("safe_zone"));
            }
            if (worldBorderTag.contains("damage_per_block")) {
                level.put("BorderDamagePerBlock", worldBorderTag.getDouble("damage_per_block"));
            }
            if (worldBorderTag.contains("warning_blocks")) {
                level.put("BorderWarningBlocks", (double) worldBorderTag.getInt("warning_blocks"));
            }
            if (worldBorderTag.contains("warning_time")) {
                level.put("BorderWarningTime", (double) worldBorderTag.getInt("warning_time"));
            }
        }
        return level;
    }

    @Override
    public JavaWorldReader createWorldReader(File dimensionFolder, Dimension dimension) {
        return new WorldReader(converter, resolvers, dimensionFolder, dimension);
    }
}
