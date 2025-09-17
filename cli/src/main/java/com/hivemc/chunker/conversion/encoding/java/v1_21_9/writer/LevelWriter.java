package com.hivemc.chunker.conversion.encoding.java.v1_21_9.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelSettings;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.primitive.DoubleTag;

import java.io.File;

public class LevelWriter extends com.hivemc.chunker.conversion.encoding.java.v1_21_6.writer.LevelWriter {
    public LevelWriter(File outputFolder, Version version, Converter converter) {
        super(outputFolder, version, converter);
    }

    @Override
    public void writeCustomLevelSetting(ChunkerLevelSettings chunkerLevelSettings, CompoundTag output, String targetName, Object value) {
        if (targetName.equals("AutumnDrop2025")) return; // Supported for this release
        super.writeCustomLevelSetting(chunkerLevelSettings, output, targetName, value);
    }

    @Override
    protected void writeExtraLevelSettings(CompoundTag data) throws Exception {
        // Write the previous extra settings
        super.writeExtraLevelSettings(data);

        // Transform all the Spawn settings to the new names
        CompoundTag spawnTag = data.getOrCreateCompound("spawn");
        if (data.contains("SpawnX") && data.contains("SpawnY") && data.contains("SpawnZ")) {
            int spawnX = data.getInt("SpawnX");
            int spawnY = data.getInt("SpawnY");
            int spawnZ = data.getInt("SpawnZ");
            data.remove("SpawnX");
            data.remove("SpawnY");
            data.remove("SpawnZ");
            spawnTag.put("pos", new int[]{spawnX, spawnY, spawnZ});
        } else if (!spawnTag.contains("pos")) {
            spawnTag.put("pos", new int[]{0, 0, 0});
        }
        if (data.contains("SpawnPitch")) {
            spawnTag.put("pitch", data.remove("SpawnPitch"));
        } else if (!spawnTag.contains("pitch")) {
            spawnTag.put("pitch", 0f);
        }
        if (data.contains("SpawnYaw")) {
            spawnTag.put("yaw", data.remove("SpawnYaw"));
        } else if (!spawnTag.contains("yaw")) {
            spawnTag.put("yaw", 0f);
        }
        if (data.contains("SpawnDimension")) {
            spawnTag.put("dimension", data.remove("SpawnDimension"));
        } else if (!spawnTag.contains("dimension")) {
            spawnTag.put("dimension", "minecraft:overworld");
        }

        // Transform all the world border settings to the new names
        CompoundTag worldBorderTag = data.getOrCreateCompound("world_border");
        if (data.contains("BorderCenterX")) {
            worldBorderTag.put("center_x", data.remove("BorderCenterX"));
        } else if (!worldBorderTag.contains("center_x")) {
            worldBorderTag.put("center_x", 0.0);
        }
        if (data.contains("BorderCenterZ")) {
            worldBorderTag.put("center_z", data.remove("BorderCenterZ"));
        } else if (!worldBorderTag.contains("center_z")) {
            worldBorderTag.put("center_z", 0.0);
        }
        if (data.contains("BorderSize")) {
            worldBorderTag.put("size", data.remove("BorderSize"));
        } else if (!worldBorderTag.contains("size")) {
            worldBorderTag.put("size", 5.999997E7);
        }
        if (data.contains("BorderSizeLerpTime")) {
            worldBorderTag.put("lerp_time", data.remove("BorderSizeLerpTime"));
        } else if (!worldBorderTag.contains("lerp_time")) {
            worldBorderTag.put("lerp_time", 0L);
        }
        if (data.contains("BorderSizeLerpTarget")) {
            worldBorderTag.put("lerp_target", data.remove("BorderSizeLerpTarget"));
        } else if (!worldBorderTag.contains("lerp_target")) {
            worldBorderTag.put("lerp_target", 0.0);
        }
        if (data.contains("BorderSafeZone")) {
            worldBorderTag.put("safe_zone", data.remove("BorderSafeZone"));
        } else if (!worldBorderTag.contains("safe_zone")) {
            worldBorderTag.put("safe_zone", 5.0);
        }
        if (data.contains("BorderDamagePerBlock")) {
            worldBorderTag.put("damage_per_block", data.remove("BorderDamagePerBlock"));
        } else if (!worldBorderTag.contains("damage_per_block")) {
            worldBorderTag.put("damage_per_block", 0.2);
        }
        if (data.contains("BorderWarningBlocks")) {
            DoubleTag tag = (DoubleTag) data.remove("BorderWarningBlocks");
            worldBorderTag.put("warning_blocks", (int) tag.getValue());
        } else if (!worldBorderTag.contains("warning_blocks")) {
            worldBorderTag.put("warning_blocks", 5);
        }
        if (data.contains("BorderWarningTime")) {
            DoubleTag tag = (DoubleTag) data.remove("BorderWarningTime");
            worldBorderTag.put("warning_time", (int) tag.getValue());
        } else if (!worldBorderTag.contains("warning_time")) {
            worldBorderTag.put("warning_time", 15);
        }
    }

    @Override
    public JavaWorldWriter createWorldWriter() {
        return new WorldWriter(outputFolder, converter, resolvers);
    }
}
