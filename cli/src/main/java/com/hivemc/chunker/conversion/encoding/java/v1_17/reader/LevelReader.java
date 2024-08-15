package com.hivemc.chunker.conversion.encoding.java.v1_17.reader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaWorldReader;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LevelReader extends com.hivemc.chunker.conversion.encoding.java.v1_16.reader.LevelReader {
    private static final Gson GSON = new Gson();

    public LevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        super(inputDirectory, inputVersion, converter);
    }

    @Override
    public @Nullable Object readCustomLevelSetting(@NotNull CompoundTag root, @NotNull String targetName, @NotNull Class<?> type) {
        // Check for caves and cliffs
        if (targetName.equals("CavesAndCliffs")) {
            return isCavesAndCliffs();
        }
        if (targetName.equals("FlatWorldVersion")) {
            return isCavesAndCliffs() ? 1 : 0;
        }
        return super.readCustomLevelSetting(root, targetName, type);
    }

    private boolean isCavesAndCliffs() {
        // CavesAndCliffs for Java is awkward because it is in a directory in older versions in the datapack, so we'll look for the right height changes
        File datapacks = new File(inputDirectory, "datapacks");
        if (!datapacks.isDirectory()) return false; // No packs, no caves and cliffs

        // Loop through each zip file
        for (File file : Objects.requireNonNull(datapacks.listFiles())) {
            if (!file.getName().endsWith(".zip")) continue;

            // Try to read the dimension definition file
            try {
                try (ZipFile zip = new ZipFile(file)) {
                    ZipEntry entry = zip.getEntry("data/minecraft/dimension_type/overworld.json");
                    if (entry == null) continue;
                    try (InputStream inputStream = zip.getInputStream(entry);
                         InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                        JsonObject jsonObject = GSON.fromJson(inputStreamReader, JsonObject.class);
                        // Just validate min_y
                        // Note: If this world contains extended height otherwise, it'll convert what it can without C&C
                        if (jsonObject.has("min_y") && jsonObject.get("min_y").getAsInt() == -64) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                return false; // Ignore the file if it can't be parsed
            }
        }

        // No CavesAndCliffs found
        return false;
    }

    @Override
    public JavaWorldReader createWorldReader(File dimensionFolder, Dimension dimension) {
        return new WorldReader(converter, resolvers, dimensionFolder, dimension);
    }
}
