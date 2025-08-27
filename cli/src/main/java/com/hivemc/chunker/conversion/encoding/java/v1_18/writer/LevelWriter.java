package com.hivemc.chunker.conversion.encoding.java.v1_18.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelSettings;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.io.File;

public class LevelWriter extends com.hivemc.chunker.conversion.encoding.java.v1_17.writer.LevelWriter {
    public LevelWriter(File outputFolder, Version version, Converter converter) {
        super(outputFolder, version, converter);
    }

    @Override
    protected CompoundTag createDimensionBiomeSource(String dimension, long seed) {
        CompoundTag biomes = new CompoundTag(2);
        if (dimension.equals("minecraft:overworld")) {
            biomes.put("preset", "minecraft:overworld");
            biomes.put("type", "minecraft:multi_noise");
        }

        if (dimension.equals("minecraft:the_end")) {
            biomes.put("type", "minecraft:the_end");
            biomes.put("seed", seed);
        }

        if (dimension.equals("minecraft:the_nether")) {
            biomes.put("preset", "minecraft:nether");
            biomes.put("type", "minecraft:multi_noise");
        }
        return biomes;
    }

    @Override
    public void writeCustomLevelSetting(ChunkerLevelSettings chunkerLevelSettings, CompoundTag output, String targetName, Object value) {
        if (targetName.equals("CavesAndCliffs")) return; // 1.18 has this built in
        super.writeCustomLevelSetting(chunkerLevelSettings, output, targetName, value);
    }

    @Override
    public JavaWorldWriter createWorldWriter() {
        return new WorldWriter(outputFolder, converter, resolvers);
    }
}
