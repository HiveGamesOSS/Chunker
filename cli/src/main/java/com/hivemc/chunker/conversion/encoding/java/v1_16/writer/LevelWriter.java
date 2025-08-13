package com.hivemc.chunker.conversion.encoding.java.v1_16.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerGeneratorType;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelPlayer;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelSettings;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

public class LevelWriter extends com.hivemc.chunker.conversion.encoding.java.v1_15.writer.LevelWriter {
    public LevelWriter(File outputFolder, Version version, Converter converter) {
        super(outputFolder, version, converter);
    }

    @Override
    public JavaWorldWriter createWorldWriter() {
        return new WorldWriter(outputFolder, converter, resolvers);
    }

    @Override
    protected CompoundTag prepareMap(ChunkerMap chunkerMap) throws Exception {
        CompoundTag map = super.prepareMap(chunkerMap);

        // Use dimension name
        map.put("dimension", chunkerMap.getDimension().getIdentifier());
        return map;
    }

    @Override
    protected void writePlayer(ChunkerLevel chunkerLevel, CompoundTag level, @Nullable ChunkerLevelPlayer player) throws Exception {
        super.writePlayer(chunkerLevel, level, player);

        // Use dimension name
        if (player != null) {
            CompoundTag playerTag = level.getOrCreateCompound("Player");
            playerTag.put("Dimension", player.getDimension().getIdentifier());
        }
    }

    @Override
    public void writeCustomLevelSetting(ChunkerLevelSettings chunkerLevelSettings, CompoundTag output, String targetName, Object value) {
        // Default implementation for seed
        if (targetName.equals("RandomSeed")) {
            return; // This is ignored as we write it in the ChunkerGeneratorType (as it's easier)
        }

        if (value instanceof ChunkerGeneratorType generatorType) {
            if (!converter.shouldAllowNBTCopying() && generatorType == ChunkerGeneratorType.CUSTOM) // Force void if no custom data
                generatorType = ChunkerGeneratorType.VOID;

            switch (generatorType) {
                case NORMAL:
                case FLAT:
                case VOID:
                    long seed = Long.parseLong(chunkerLevelSettings.RandomSeed);
                    CompoundTag dimensions = new CompoundTag(3);
                    dimensions.put("minecraft:overworld", createDimension("minecraft:overworld", generatorType, seed));

                    // Use normal for nether as the client complains otherwise
                    dimensions.put("minecraft:the_end", createDimension("minecraft:the_end", ChunkerGeneratorType.NORMAL, seed));
                    dimensions.put("minecraft:the_nether", createDimension("minecraft:the_nether", ChunkerGeneratorType.NORMAL, seed));

                    // Settings
                    CompoundTag worldGenSettings = new CompoundTag(4);
                    worldGenSettings.put("bonus_chest", (byte) (chunkerLevelSettings.bonusChestEnabled ? 1 : 0));
                    worldGenSettings.put("generate_features", (byte) (chunkerLevelSettings.MapFeatures ? 1 : 0));
                    worldGenSettings.put("seed", seed);
                    worldGenSettings.put("dimensions", dimensions);

                    output.put("WorldGenSettings", worldGenSettings);
                    return;
                case CUSTOM:
                    // Don't write anything
                    return;
            }
        }

        super.writeCustomLevelSetting(chunkerLevelSettings, output, targetName, value);
    }

    protected CompoundTag createDimension(String dimension, ChunkerGeneratorType type, long seed) {
        CompoundTag tag = new CompoundTag(2);
        CompoundTag generator = new CompoundTag(4);

        // Fill in the generator settings
        generator.put("type", type == ChunkerGeneratorType.NORMAL ? "minecraft:noise" : "minecraft:flat");
        if (type == ChunkerGeneratorType.NORMAL) {
            generator.put("settings", dimension.replace("the_", ""));
            generator.put("seed", seed);
            generator.put("biome_source", createDimensionBiomeSource(dimension, seed));
        } else {
            CompoundTag settings = new CompoundTag(3);

            // Add structures tag
            CompoundTag structures = new CompoundTag(1);
            structures.put("structures", new CompoundTag());
            settings.put("structures", structures);

            settings.put("biome", resolvers.writeBiome(ChunkerBiome.ChunkerVanillaBiome.PLAINS, Dimension.OVERWORLD));
            ListTag<CompoundTag, Map<String, Tag<?>>> layers = new ListTag<>(TagType.COMPOUND, 3);

            // Layers vary depending on generator
            if (type == ChunkerGeneratorType.FLAT) {
                // Bedrock
                CompoundTag bedrockLayer = new CompoundTag(2);
                bedrockLayer.put("block", "minecraft:bedrock");
                bedrockLayer.put("height", 1);
                layers.add(bedrockLayer);

                // Dirt
                CompoundTag dirtLayer = new CompoundTag(2);
                dirtLayer.put("block", "minecraft:dirt");
                dirtLayer.put("height", 2);
                layers.add(dirtLayer);

                // Grass
                CompoundTag grassLayer = new CompoundTag(2);
                grassLayer.put("block", "minecraft:grass_block");
                grassLayer.put("height", 1);
                layers.add(grassLayer);
            } else if (type == ChunkerGeneratorType.VOID) {
                CompoundTag airLayer = new CompoundTag(2);
                airLayer.put("block", "minecraft:air");
                airLayer.put("height", 1);
                layers.add(airLayer);
            }

            // Add layers
            settings.put("layers", layers);

            // Add settings
            generator.put("settings", settings);
        }

        // Add the tags
        tag.put("generator", generator);
        tag.put("type", dimension);

        return tag;
    }

    protected CompoundTag createDimensionBiomeSource(String dimension, long seed) {
        CompoundTag biomes = new CompoundTag(3);
        biomes.put("seed", seed);

        if (dimension.equals("minecraft:overworld")) {
            biomes.put("type", "minecraft:vanilla_layered");
            biomes.put("large_biomes", (byte) 0);
        }

        if (dimension.equals("minecraft:the_end")) {
            biomes.put("type", "minecraft:the_end");
        }

        if (dimension.equals("minecraft:the_nether")) {
            biomes.put("preset", "minecraft:nether");
            biomes.put("type", "minecraft:multi_noise");
        }
        return biomes;
    }
}
