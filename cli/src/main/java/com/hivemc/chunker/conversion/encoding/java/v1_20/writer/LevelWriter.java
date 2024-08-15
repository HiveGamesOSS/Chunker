package com.hivemc.chunker.conversion.encoding.java.v1_20.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelSettings;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.io.File;

public class LevelWriter extends com.hivemc.chunker.conversion.encoding.java.v1_19_3.writer.LevelWriter {
    public LevelWriter(File outputFolder, Version version, Converter converter) {
        super(outputFolder, version, converter);
    }

    @Override
    public void writeCustomLevelSetting(ChunkerLevelSettings chunkerLevelSettings, CompoundTag output, String targetName, Object value) {
        if (targetName.equals("R20Support")) return; // Built-in
        super.writeCustomLevelSetting(chunkerLevelSettings, output, targetName, value);
    }

    @Override
    protected void writeExtraLevelSettings(CompoundTag data) throws Exception {
        // Call super
        super.writeExtraLevelSettings(data);

        // Add empty DragonFight tag, this satisfies the client when it's missing
        data.getOrCreateCompound("DragonFight");
    }

    @Override
    public JavaWorldWriter createWorldWriter() {
        return new WorldWriter(outputFolder, converter, resolvers);
    }
}
