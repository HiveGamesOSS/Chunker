package com.hivemc.chunker.conversion.encoding.java.base.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.encoding.java.JavaDataVersion;
import com.hivemc.chunker.conversion.encoding.java.base.JavaReaderWriter;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaLevelReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerGeneratorType;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelPlayer;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelSettings;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.Long2ShortOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A writer for Java levels.
 */
public class JavaLevelWriter implements LevelWriter, JavaReaderWriter {
    protected final Long2ShortMap oldToNewMapIDs = new Long2ShortOpenHashMap();
    protected final File outputFolder;
    protected final Version version;
    protected final Converter converter;
    protected final JavaResolvers resolvers;
    protected final File dataFolder;

    /**
     * Create a new java level writer.
     *
     * @param outputFolder the folder to write the world to.
     * @param version      the version being written.
     * @param converter    the converter instance.
     */
    public JavaLevelWriter(File outputFolder, Version version, Converter converter) {
        this.outputFolder = outputFolder;
        this.version = version;
        this.converter = converter;
        resolvers = buildResolvers(converter).build();
        dataFolder = new File(outputFolder, "data");
    }

    @Override
    public WorldWriter writeLevel(ChunkerLevel chunkerLevel) {
        // Ensure directory has been made
        outputFolder.mkdirs();

        // Normalise map IDs (they need to be numerical for Java)
        short id = 0;
        for (ChunkerMap map : chunkerLevel.getMaps()) {
            // Set new ID
            oldToNewMapIDs.put(map.getId(), id);
            map.setId(id);

            // Increment ID
            id++;
        }

        // Save level data
        Task.asyncConsume("Writing Level Data", TaskWeight.NORMAL, this::writeLevelData, chunkerLevel);

        // Create a new world writer with the created worldData
        return createWorldWriter();
    }

    /**
     * Schedule all the level data writing tasks.
     *
     * @param chunkerLevel the level to write.
     */
    protected void writeLevelData(ChunkerLevel chunkerLevel) {
        Task.asyncConsume("Writing Level Settings", TaskWeight.NORMAL, this::writeLevelSettings, chunkerLevel);
        Task.asyncConsume("Writing Saved Maps", TaskWeight.NORMAL, this::writeMaps, chunkerLevel);
    }

    /**
     * Write all the maps in the level to disk.
     *
     * @param chunkerLevel the level containing the maps.
     * @throws Exception if it failed to write the maps.
     */
    protected void writeMaps(ChunkerLevel chunkerLevel) throws Exception {
        if (chunkerLevel.getMaps().isEmpty()) {
            return;
        }

        // Make data directory
        if (!dataFolder.isDirectory()) {
            dataFolder.mkdirs();
        }

        // Update idcounts.dat
        CompoundTag root = new CompoundTag();
        root.put("map", (short) chunkerLevel.getMaps().stream().mapToLong(ChunkerMap::getId).max().orElse(0));

        Tag.writeUncompressedJavaNBT(new File(dataFolder, "idcounts.dat"), root);

        // Write maps
        Task.asyncConsumeForEach("Writing Saved Map", TaskWeight.NORMAL, this::writeMap, chunkerLevel.getMaps());
    }

    /**
     * Turn a map into NBT.
     *
     * @param chunkerMap the map to turn into NBT.
     * @return the NBT representation of the map.
     * @throws Exception if it failed to turn the map to NBT.
     */
    protected CompoundTag prepareMap(ChunkerMap chunkerMap) throws Exception {
        // Use the original map NBT as a base if it's present
        CompoundTag mapData = chunkerMap.getOriginalNBT() != null ? chunkerMap.getOriginalNBT() : new CompoundTag();

        // Copy over the other settings
        mapData.put("dimension", chunkerMap.getDimension().getJavaID());
        mapData.put("width", (short) chunkerMap.getWidth());
        mapData.put("height", (short) chunkerMap.getHeight());
        mapData.put("xCenter", chunkerMap.getXCenter());
        mapData.put("zCenter", chunkerMap.getZCenter());
        mapData.put("trackingPosition", chunkerMap.isUnlimitedTracking() ? (byte) 1 : (byte) 0);
        mapData.put("unlimitedTracking", chunkerMap.isUnlimitedTracking() ? (byte) 1 : (byte) 0);
        mapData.put("locked", chunkerMap.isLocked() ? (byte) 1 : (byte) 0);
        mapData.put("scale", chunkerMap.getScale());
        mapData.put("banners", new ListTag<>());
        if (chunkerMap.getBytes() != null) {
            mapData.put("colors", resolvers.writeMapColors(chunkerMap.getBytes()));
        }
        return mapData;
    }

    /**
     * Write a map to a data file.
     *
     * @param chunkerMap the map to write.
     * @throws Exception if it failed to write the map.
     */
    protected void writeMap(ChunkerMap chunkerMap) throws Exception {
        CompoundTag mapData = prepareMap(chunkerMap);

        // Wrap the mapData in a compound tag
        CompoundTag root = new CompoundTag();
        root.put("data", mapData);
        root.put("DataVersion", resolvers.dataVersion().getDataVersion());

        // Write to disk
        Tag.writeGZipJavaNBT(new File(dataFolder, "map_" + chunkerMap.getId() + ".dat"), root);
    }

    @Override
    public void writeCustomLevelSetting(ChunkerLevelSettings chunkerLevelSettings, CompoundTag output, String targetName, Object value) {
        // Check for next update
        if (targetName.equals("AutumnDrop2025")) {
            // Not supported
            return;
        }

        if (targetName.equals("SummerDrop2025")) {
            // Not supported
            return;
        }

        if (targetName.equals("WinterDrop2024")) {
            // Not needed in less than 1.21.2
            return;
        }

        if (targetName.equals("R21Support")) {
            // Not needed in less than 1.20.3
            return;
        }

        if (targetName.equals("R20Support")) {
            // Not needed in less than 1.19.3
            return;
        }

        if (targetName.equals("CavesAndCliffs")) {
            // Not needed in less than 1.17
            return;
        }

        if (targetName.equals("FlatWorldVersion")) {
            // Not written on Java
            return;
        }

        // Default implementation for seed
        if (targetName.equals("RandomSeed")) {
            output.put("RandomSeed", Long.parseLong((String) value));
            return;
        }

        if (value instanceof ChunkerGeneratorType generatorType) {
            // Force void if no custom data
            if (!converter.shouldAllowNBTCopying() && generatorType == ChunkerGeneratorType.CUSTOM) {
                generatorType = ChunkerGeneratorType.VOID;
            }

            switch (generatorType) {
                case NORMAL:
                    output.put("generatorName", "default");
                    output.put("generatorVersion", 0);
                    return;
                case FLAT:
                    output.put("generatorName", "flat");
                    output.put("generatorVersion", 0);
                    return;
                case VOID:
                    output.put("generatorName", "flat");
                    output.put("generatorVersion", 0);
                    output.put("generatorOptions", "3;minecraft:air;127;decoration");
                    return;
                case CUSTOM:
                    // Don't write anything
                    return;
            }
        }

        throw new IllegalArgumentException("Writing of " + targetName + " is not implemented.");
    }

    /**
     * Enable an experiment for the level.dat.
     *
     * @param output      the level.dat root.
     * @param experiments the experiments to enable.
     */
    protected void enableExperiments(CompoundTag output, String... experiments) {
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

        // Add experiments
        for (String experiment : experiments) {
            if (!enabled.contains(experiment)) {
                enabled.add(new StringTag(experiment));
            }
        }

        // Add to enabled_features
        if (!output.contains("enabled_features")) {
            output.put("enabled_features", new ListTag<>(TagType.STRING));
        }
        ListTag<StringTag, String> enabledFeatures = output.getList("enabled_features", StringTag.class);
        if (!enabledFeatures.contains("vanilla")) {
            enabledFeatures.add(new StringTag("vanilla"));
        }
        // Add experiments as features
        for (String experiment : experiments) {
            if (!enabledFeatures.contains(experiment)) {
                enabledFeatures.add(new StringTag(experiment));
            }
        }
    }

    /**
     * Write the level.dat for the level.
     *
     * @param chunkerLevel the input level.
     * @throws Exception if it failed to write the level.dat.
     */
    protected void writeLevelSettings(ChunkerLevel chunkerLevel) throws Exception {
        // Generate NBT from settings
        CompoundTag data = chunkerLevel.getOriginalLevelData() == null || !converter.shouldAllowNBTCopying() ? new CompoundTag() : chunkerLevel.getOriginalLevelData();
        chunkerLevel.getSettings().toNBT(data, this, converter);

        // Write player data
        try {
            writePlayer(chunkerLevel, data, chunkerLevel.getPlayer());
        } catch (Exception e) {
            converter.logNonFatalException(e);
        }

        // Write extra settings
        writeExtraLevelSettings(data);

        // Write the level.dat
        CompoundTag root = new CompoundTag();
        root.put("Data", data);
        Tag.writeGZipJavaNBT(new File(outputFolder, "level.dat"), root);
    }

    /**
     * Write the local player to the level.dat.
     *
     * @param chunkerLevel the chunker level.
     * @param level        the output level.dat root.
     * @param player       the player being written (can be null).
     * @throws Exception if it failed to write the player.
     */
    protected void writePlayer(ChunkerLevel chunkerLevel, CompoundTag level, @Nullable ChunkerLevelPlayer player) throws Exception {
        if (player == null) return; // Don't write the player if it doesn't exist
        CompoundTag playerTag = level.getOrCreateCompound("Player");

        // Add dataVersion
        playerTag.put("DataVersion", resolvers.dataVersion().getDataVersion());

        // Write position data
        playerTag.put("Pos", ListTag.fromValues(TagType.DOUBLE, List.of(
                player.getPositionX(),
                player.getPositionY(),
                player.getPositionZ()
        )));
        playerTag.put("Motion", ListTag.fromValues(TagType.DOUBLE, List.of(
                player.getMotionX(),
                player.getMotionY(),
                player.getMotionZ()
        )));
        playerTag.put("Rotation", ListTag.fromValues(TagType.FLOAT, List.of(
                player.getYaw(),
                player.getPitch()
        )));

        // Write the dimension ID
        playerTag.put("Dimension", player.getDimension().getJavaID());

        // Write the game type
        int gameType = player.getGameType();
        if (gameType == 5) {
            // Default should go to the level game type
            gameType = chunkerLevel.getSettings().GameType;
        }
        if (gameType > 3) {
            // Fall back to spectator
            gameType = 3;
        }
        playerTag.put("playerGameType", gameType);

        boolean splitEquipment = resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 21, 5);

        // Write the inventory
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, new ArrayList<>(player.getInventory().size()));
        for (Byte2ObjectMap.Entry<ChunkerItemStack> tag : player.getInventory().byte2ObjectEntrySet()) {
            // Don't write air to inventories
            if (tag.getValue().getIdentifier().isAir()) continue;

            // Don't write equipment if it's split into the equipment tag
            if (splitEquipment && JavaLevelReader.SLOT_TO_EQUIPMENT.containsKey(tag.getByteKey())) continue;

            // Write the item with slot
            Optional<CompoundTag> item = resolvers.writeItem(tag.getValue());
            if (item.isEmpty()) continue;

            // Write the slot
            item.get().put("Slot", tag.getByteKey());

            // Add to items
            items.add(item.get());
        }
        playerTag.put("Inventory", items);

        // Write the equipment (1.21.5+)
        if (splitEquipment) {
            CompoundTag equipment = new CompoundTag();
            for (Map.Entry<Byte, String> slot : JavaLevelReader.SLOT_TO_EQUIPMENT.entrySet()) {
                ChunkerItemStack tag = player.getInventory().get(slot.getKey().byteValue());

                // Don't write air to equipment
                if (tag == null || tag.getIdentifier().isAir()) continue;

                // Write the item
                Optional<CompoundTag> item = resolvers.writeItem(tag);
                if (item.isEmpty()) continue;

                // Add to equipment
                equipment.put(slot.getValue(), item.get());
            }
            playerTag.put("equipment", equipment);
        }
    }

    /**
     * Write any extra NBT data to the level.dat.
     *
     * @param data the root compound to output to.
     * @throws Exception if it failed to write extra data.
     */
    protected void writeExtraLevelSettings(CompoundTag data) throws Exception {
        // Version Info
        if (!data.contains("Version")) {
            // Ensure dataVersion is present
            if (resolvers.dataVersion() == null) throw new Exception("Unable to find a suitable data version.");

            CompoundTag version = new CompoundTag();
            version.put("Id", resolvers.dataVersion().getDataVersion());
            version.put("Name", resolvers.dataVersion().getVersion().toString());
            version.put("Snapshot", (byte) 0);
            data.put("Version", version);
        }

        if (!data.contains("version")) {
            data.put("version", JavaDataVersion.LAST_ANVIL_FILE_VERSION);
        }

        if (!data.contains("DataVersion")) {
            // Ensure dataVersion is present
            if (resolvers.dataVersion() == null) throw new Exception("Unable to find a suitable data version.");

            data.put("DataVersion", resolvers.dataVersion().getDataVersion());
        }

        // Force void world
        if (!data.contains("generatorName")) {
            data.put("generatorName", "flat");
        }

        if (!data.contains("generatorVersion")) {
            data.put("generatorVersion", 0);
        }

        if (!data.contains("initialized")) {
            data.put("initialized", (byte) 1);
        }

        // Set the last played
        data.put("LastPlayed", Instant.now().toEpochMilli());

        // Fix SpawnY
        if (data.contains("SpawnY")) {
            int y = data.getInt("SpawnY");
            if (y == 32767) {
                data.put("SpawnY", -1);
            }
        }


        // Fix GameModes
        if (data.contains("GameType")) {
            int type = data.getInt("GameType");
            if (type > 3) {
                // Fall back to spectator
                data.put("GameType", 3);
            }
        }
    }

    @Override
    public void flushLevel() {
        // Not used, flushing of data / handling is done in the world writer / column writer
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public @Nullable PreTransformManager getPreTransformManager() {
        return resolvers.preTransformManager();
    }

    /**
     * Create a new world writer.
     *
     * @return an instance of the world writer.
     */
    public JavaWorldWriter createWorldWriter() {
        return new JavaWorldWriter(outputFolder, converter, resolvers);
    }
}
