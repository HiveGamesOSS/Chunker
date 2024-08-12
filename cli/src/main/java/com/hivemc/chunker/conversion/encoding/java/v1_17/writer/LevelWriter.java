package com.hivemc.chunker.conversion.encoding.java.v1_17.writer;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelSettings;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;

import java.io.File;
import java.io.IOException;

public class LevelWriter extends com.hivemc.chunker.conversion.encoding.java.v1_16.writer.LevelWriter {
    public LevelWriter(File outputFolder, Version version, Converter converter) {
        super(outputFolder, version, converter);
    }

    @Override
    public void writeCustomLevelSetting(ChunkerLevelSettings chunkerLevelSettings, CompoundTag output, String targetName, Object value) {
        // Check for caves and cliffs
        if (targetName.equals("CavesAndCliffs")) {
            // Note: This shouldn't be triggered when editing a world (is copy nbt indicates that)
            if ((boolean) value && !converter.shouldAllowNBTCopying()) {
                // Copy datapack
                File directory = new File(outputFolder, "datapacks");
                if (!directory.isDirectory()) {
                    directory.mkdirs();
                }

                File outputFile = new File(directory, "CavesAndCliffsHeightPack.zip");
                try {
                    Resources.asByteSource(Resources.getResource("java/CavesAndCliffsHeightPack.zip")).copyTo(Files.asByteSink(outputFile));
                } catch (IOException e) {
                    converter.logNonFatalException(e);
                }

                // Enable data packs
                CompoundTag dataPacks = output.getOrCreateCompound("DataPacks");
                if (!dataPacks.contains("Enabled")) {
                    dataPacks.put("Enabled", new ListTag<>(TagType.STRING));
                }

                // Add to enabled with vanilla
                ListTag<StringTag, String> enabled = dataPacks.getList("Enabled", StringTag.class);
                if (!enabled.contains("vanilla")) {
                    enabled.add(new StringTag("vanilla"));
                }

                // Add CaC height pack
                if (!enabled.contains("file/" + outputFile.getName())) {
                    enabled.add(new StringTag("file/" + outputFile.getName()));
                }
            }

            return;
        }

        super.writeCustomLevelSetting(chunkerLevelSettings, output, targetName, value);
    }

    @Override
    public JavaWorldWriter createWorldWriter() {
        return new WorldWriter(outputFolder, converter, resolvers);
    }
}
