package com.hivemc.chunker.conversion.encoding.bedrock.v1_20_60.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockWorldWriter;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelSettings;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.io.File;

public class LevelWriter extends com.hivemc.chunker.conversion.encoding.bedrock.v1_20_50.writer.LevelWriter {
    public LevelWriter(File outputFolder, Version version, Converter converter) {
        super(outputFolder, version, converter);
    }

    @Override
    public void writeCustomLevelSetting(ChunkerLevelSettings chunkerLevelSettings, CompoundTag output, String targetName, Object value) {
        // Check for R21
        if (targetName.equals("R21Support")) {
            if ((boolean) value && !converter.shouldAllowNBTCopying()) {
                // Extra armadillo experiment needed for 1.20.60
                enableExperiments(output, "updateAnnouncedLive2023", "armadillo");
            }
            // Handled
            return;
        }
        super.writeCustomLevelSetting(chunkerLevelSettings, output, targetName, value);
    }

    @Override
    public BedrockWorldWriter createWorldWriter() {
        return new WorldWriter(outputFolder, converter, resolvers, database);
    }
}
