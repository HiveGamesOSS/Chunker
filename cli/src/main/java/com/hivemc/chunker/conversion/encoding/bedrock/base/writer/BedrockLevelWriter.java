package com.hivemc.chunker.conversion.encoding.bedrock.base.writer;

import com.hivemc.chunker.conversion.WorldConverter;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.BedrockReaderWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockLevelReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.level.*;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.table.BloomFilterPolicy;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * A writer for Bedrock levels.
 */
public class BedrockLevelWriter implements LevelWriter, BedrockReaderWriter {
    /**
     * Custom World string used for void worlds.
     */
    public static final String VOID_WORD_STRING = "{ " +
            "\"biome_id\" : 1, " +
            "\"block_layers\" : [{" +
            "\"block_data\" : 0, " +
            "\"block_name\" : \"minecraft:air\", " +
            "\"count\" : 1 " +
            "}], " +
            "\"encoding_version\" : 4,  " +
            "\"structure_options\" : null" +
            "}";

    protected final File outputFolder;
    protected final Version version;
    protected final Converter converter;
    protected final BedrockResolvers resolvers;
    protected DB database;

    /**
     * Create a new level writer.
     *
     * @param outputFolder the output folder for the world.
     * @param version      the version being written.
     * @param converter    the converter instance.
     */
    public BedrockLevelWriter(File outputFolder, Version version, Converter converter) {
        this.outputFolder = outputFolder;
        this.version = version;
        this.converter = converter;
        resolvers = buildResolvers(converter).build();
    }

    /**
     * Open the LevelDB database for writing.
     *
     * @throws IOException if it failed to create a new database.
     */
    protected void openDatabase() throws IOException {
        File databaseDirectory = new File(outputFolder, "db");
        databaseDirectory.mkdirs();

        // Delete LOCK file (as it may have been left behind by a bad abort / corrupted DB)
        new File(databaseDirectory, "LOCK").delete();

        // LevelDB Options
        Options options = new Options();
        options.compressionType(CompressionType.ZLIB_RAW);
        options.blockSize(160 * 1024); // 160KB
        options.filterPolicy(new BloomFilterPolicy(10));
        options.writeBufferSize(400 * 1024 * 1024); // 400MB write buffer
        options.createIfMissing(true);

        // Create the factory and open the database
        DBFactory factory = new Iq80DBFactory();
        database = factory.open(databaseDirectory, options);

        if (converter.shouldAllowNBTCopying()) {
            remapExistingDB();
        }
    }

    /**
     * Remap any existing LevelDB fields to retain the information.
     *
     * @throws IOException if it failed to remap the database.
     */
    protected void remapExistingDB() throws IOException {
        List<byte[]> removals = new ArrayList<>();
        try (DBIterator iterator = database.iterator()) {
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                byte[] key = entry.getKey();
                int keyLength = key.length;

                // The keys we're looking for are (9, 10, 13, 14) depending on if they have sub chunk / dimension
                boolean containsSubChunk = keyLength == 14 || keyLength == 10;
                boolean containsDimension = keyLength == 14 || keyLength == 13;

                // If not 9 (both false) or any of the others then skip this entry
                if (keyLength != 9 && !containsSubChunk && !containsDimension) continue;

                // Skip local player
                if (Arrays.equals(key, LevelDBKey.LOCAL_PLAYER)) {
                    continue;
                }

                // Read key information
                // Mark dimension & chunk present
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(key);
                     DataInputStream dataInputStream = new DataInputStream(inputStream)) {
                    Reader reader = Reader.toBedrockReader(dataInputStream);

                    // Read co-ordinates
                    int x = reader.readInt();
                    int z = reader.readInt();

                    // Read dimension
                    Dimension dimension = Dimension.OVERWORLD;
                    if (containsDimension) {
                        int dimensionID = reader.readInt();
                        dimension = Dimension.fromBedrock((byte) dimensionID, null);

                        // If unknown report an issue
                        if (dimension == null) {
                            converter.logNonFatalException(new Exception("Unknown dimension key " + dimensionID));
                            removals.add(key);
                            continue;
                        }
                    }
                    byte subChunkY = 0;
                    // Read subChunk Y
                    if (containsSubChunk) {
                        subChunkY = reader.readByte();
                    }
                    byte type = reader.readByte();

                    // Check if it needs a dimension remap / pruning
                    Optional<Dimension> newDimension = converter.getNewDimension(dimension);
                    ChunkCoordPair chunkCoordPair = new ChunkCoordPair(x, z);
                    if (newDimension.isPresent() && converter.shouldProcessColumn(dimension, chunkCoordPair)) {
                        if (newDimension.get() != dimension) {
                            byte[] value = entry.getValue();

                            // Delete old key
                            removals.add(key);

                            // Write new key (with dimension changed)
                            if (containsSubChunk) {
                                database.put(LevelDBKey.key(newDimension.get(), chunkCoordPair, subChunkY, type), value);
                            } else {
                                database.put(LevelDBKey.key(newDimension.get(), chunkCoordPair, type), value);
                            }
                        }
                    } else {
                        // Remove as the dimension/column has been pruned
                        removals.add(key);
                    }
                }
            }
        }

        // Remove any keys marked for removal
        try (WriteBatch writeBatch = database.createWriteBatch()) {
            for (byte[] key : removals) {
                writeBatch.delete(key);
            }
            database.write(writeBatch);
            removals.clear();
        }
    }

    @Override
    public void free() throws Exception {
        if (database != null) {
            try {
                database.close();
            } finally {
                database = null;
            }
        }
    }

    @Override
    public void flushLevel() {
        // Compact database
        if (converter.shouldLevelDBCompaction()) {
            // Signal the converter to indicate compaction has started
            Task.signal(WorldConverter.SIGNAL_COMPACTION, true);

            // Call compaction
            database.compactRange(null, null);

            // Signal the converter to indicate compaction has ended
            Task.signal(WorldConverter.SIGNAL_COMPACTION, false);
        }
    }

    @Override
    public WorldWriter writeLevel(ChunkerLevel chunkerLevel) throws Exception {
        // Create the database
        openDatabase();

        // Save level data
        Task.asyncConsume("Writing Level Data", TaskWeight.NORMAL, this::writeLevelData, chunkerLevel);

        // Create a new world writer with the created worldData
        return createWorldWriter();
    }

    /**
     * Schedule writing all the level data.
     *
     * @param chunkerLevel the level being written.
     */
    protected void writeLevelData(ChunkerLevel chunkerLevel) {
        Task.asyncConsume("Writing Level Settings", TaskWeight.NORMAL, this::writeLevelSettings, chunkerLevel);
        Task.asyncConsume("Writing Local Player", TaskWeight.NORMAL, this::writeLocalPlayer, chunkerLevel);
        Task.asyncConsume("Writing Saved Maps", TaskWeight.NORMAL, this::writeMaps, chunkerLevel);
        Task.asyncConsume("Writing Portals", TaskWeight.NORMAL, this::writePortals, chunkerLevel);
    }

    /**
     * Schedule writing all the maps for the level.
     *
     * @param chunkerLevel the level to use.
     */
    protected void writeMaps(ChunkerLevel chunkerLevel) {
        if (chunkerLevel.getMaps().isEmpty()) {
            return;
        }

        // Write maps
        Task.asyncConsumeForEach("Writing Saved Map", TaskWeight.NORMAL, this::writeMap, chunkerLevel.getMaps());
    }

    /**
     * Turn a map into NBT.
     *
     * @param chunkerMap the map being encoded.
     * @return an NBT compound of the map.
     * @throws Exception if it failed to write the map.
     */
    protected CompoundTag prepareMap(ChunkerMap chunkerMap) throws Exception {
        // Use the original map NBT as a base if it's present
        CompoundTag mapData = chunkerMap.getOriginalNBT() != null ? chunkerMap.getOriginalNBT() : new CompoundTag();
        mapData.put("mapId", chunkerMap.getId());

        // Set the parentMapId to -1 if it's not present
        if (!mapData.contains("parentMapId")) {
            mapData.put("parentMapId", -1L);
        }

        // Add the decorations if they're not present
        if (!mapData.contains("decorations")) {
            mapData.put("decorations", new ListTag<>(TagType.COMPOUND));
        }

        // Scale requires 4 when it's not the parent map
        mapData.put("scale", mapData.getLong("parentMapId", -1L) == -1L ? (byte) 4 : chunkerMap.getScale());

        // Copy over the other settings
        mapData.put("dimension", chunkerMap.getDimension().getBedrockID());
        mapData.put("width", (short) chunkerMap.getWidth());
        mapData.put("height", (short) chunkerMap.getHeight());
        mapData.put("xCenter", chunkerMap.getXCenter());
        mapData.put("zCenter", chunkerMap.getZCenter());
        mapData.put("unlimitedTracking", chunkerMap.isUnlimitedTracking() ? (byte) 1 : (byte) 0);
        mapData.put("mapLocked", chunkerMap.isLocked() ? (byte) 1 : (byte) 0);
        if (chunkerMap.getBytes() != null) {
            mapData.put("colors", chunkerMap.getBytes());
        }
        return mapData;
    }

    /**
     * Write a map to the database.
     *
     * @param chunkerMap the map to write.
     * @throws Exception if it failed to write the map.
     */
    protected void writeMap(ChunkerMap chunkerMap) throws Exception {
        CompoundTag mapData = prepareMap(chunkerMap);

        // Write to DB
        byte[] value = Tag.writeBedrockNBT(mapData);
        database.put(("map_" + chunkerMap.getId()).getBytes(StandardCharsets.UTF_8), value);
    }

    /**
     * Write the portal data for the level.
     *
     * @param chunkerLevel the level to get the portal data from.
     * @throws Exception if it failed to write the portal data.
     */
    protected void writePortals(ChunkerLevel chunkerLevel) throws Exception {
        if (chunkerLevel.getPortals().isEmpty()) {
            return;
        }

        // Write portals
        CompoundTag entry = new CompoundTag();
        ListTag<CompoundTag, Map<String, Tag<?>>> portalRecords = new ListTag<>(TagType.COMPOUND);
        for (ChunkerPortal portal : chunkerLevel.getPortals()) {
            CompoundTag record = new CompoundTag();
            record.put("DimId", (int) portal.getDimension().getBedrockID());
            record.put("Span", portal.getWidth());
            record.put("TpX", portal.getX());
            record.put("TpY", portal.getY());
            record.put("TpZ", portal.getZ());
            record.put("Xa", portal.getXa());
            record.put("Za", portal.getZa());
            portalRecords.add(record);
        }
        entry.put("PortalRecords", portalRecords);

        // Wrap in a data tag
        CompoundTag data = new CompoundTag();
        data.put("data", entry);

        // Write to field
        byte[] value = Tag.writeBedrockNBT(data);
        database.put(LevelDBKey.PORTALS, value);
    }

    @Override
    public void writeCustomLevelSetting(ChunkerLevelSettings chunkerLevelSettings, CompoundTag output, String targetName, Object value) {
        // Check for next update
        if (targetName.equals("SummerDrop2025")) {
            // Not supported
            return;
        }

        // Check for next update
        if (targetName.equals("WinterDrop2024")) {
            // Not supported
            return;
        }

        // Check for next update
        if (targetName.equals("R21Support")) {
            // Not supported
            return;
        }

        // Check for next update
        if (targetName.equals("R20Support")) {
            // Not supported
            return;
        }

        // Check for caves and cliffs
        if (targetName.equals("CavesAndCliffs")) {
            // Not supported
            return;
        }

        // Write flat world version
        if (targetName.equals("FlatWorldVersion")) {
            output.put("WorldVersion", (int) value);
            return;
        }

        // Default implementation for seed
        if (targetName.equals("RandomSeed")) {
            // Ensure we retain the entire long when writing, as we strip it to an int on read.
            if (!output.contains("RandomSeed") || (int) output.getLong("RandomSeed") != (int) Long.parseLong((String) value)) {
                output.put("RandomSeed", Long.parseLong((String) value));
            }

            return;
        }

        if (value instanceof ChunkerGeneratorType type) {
            if (!converter.shouldAllowNBTCopying() && type == ChunkerGeneratorType.CUSTOM) // Force void if no custom data
                type = ChunkerGeneratorType.VOID;

            switch (type) {
                case NORMAL:
                    output.put("Generator", 1); // Normal map
                    return;
                case FLAT:
                    output.put("Generator", 2); // Flat map
                    return;
                case VOID:
                    output.put("Generator", 2); // Flat map
                    output.put("FlatWorldLayers", VOID_WORD_STRING);
                    return;
                case CUSTOM:
                    // Don't write anything
                    return;
            }
        }

        throw new IllegalArgumentException("Writing of " + targetName + " is not implemented.");
    }

    /**
     * Enable experiments for a level.dat.
     *
     * @param output      the root compound tag.
     * @param experiments the experiments which should be enabled.
     */
    protected void enableExperiments(CompoundTag output, String... experiments) {
        CompoundTag experimentsTag = output.getOrCreateCompound("experiments");
        for (String experiment : experiments) {
            experimentsTag.put(experiment, (byte) 1);
        }

        // Since experiments is present we should mark them as possibly previously used
        experimentsTag.put("experiments_ever_used", (byte) 1);
        experimentsTag.put("saved_with_toggled_experiments", (byte) 1);
    }

    /**
     * Write the level.dat.
     *
     * @param chunkerLevel the level to write settings from.
     * @throws Exception if it fails to write the level settings.
     */
    protected void writeLevelSettings(ChunkerLevel chunkerLevel) throws Exception {
        // Generate NBT from settings
        CompoundTag data = chunkerLevel.getOriginalLevelData() == null || !converter.shouldAllowNBTCopying() ? new CompoundTag() : chunkerLevel.getOriginalLevelData();
        chunkerLevel.getSettings().worldStartCount = 0xFFFFFFFFL - 1L; // Use 1 as start count (this is used to seed entity IDs)
        chunkerLevel.getSettings().toNBT(data, this, converter);


        if (!data.contains("Generator")) {
            data.put("Generator", 2); // Flat map
        }

        if (!data.contains("StorageVersion")) {
            data.put("StorageVersion", resolvers.dataVersion().getStorageVersion());
        }

        if (!data.contains("NetworkVersion")) {
            data.put("NetworkVersion", resolvers.dataVersion().getProtocolVersion());
        }

        // Fix SpawnY
        if (data.contains("SpawnY")) {
            int y = data.getInt("SpawnY");
            if (y == -1) {
                data.put("SpawnY", 32767);
            }
        }

        // Fix GameModes
        if (data.contains("GameType")) {
            int type = data.getInt("GameType");
            if (type == 3 || type == 4 || type == 6) {
                // Use spectator (or fall back to adventure) (disabled in 1.19U5 due to issues initializing)
                data.put("GameType", getVersion().isGreaterThanOrEqual(1, 18, 30) && getVersion().isLessThan(1, 19, 50) ? 6 : 2);
            }
        }

        // Add LastPlayed
        data.put("LastPlayed", Instant.now().getEpochSecond());

        // Mark minimum compatible version
        Version version = resolvers.dataVersion().getVersion();
        ListTag<IntTag, Integer> minimumVersion = new ListTag<>(TagType.INT);
        minimumVersion.add(new IntTag(version.getMajor()));
        minimumVersion.add(new IntTag(version.getMinor()));
        minimumVersion.add(new IntTag(version.getPatch()));
        minimumVersion.add(new IntTag(0));
        minimumVersion.add(new IntTag(0));

        if (!data.contains("MinimumCompatibleClientVersion")) {
            data.put("MinimumCompatibleClientVersion", minimumVersion);
        }

        // Mark last opened with version
        if (!data.contains("lastOpenedWithVersion")) {
            data.put("lastOpenedWithVersion", minimumVersion);
        }

        // Write the level.dat
        Tag.writeBedrockNBT(new File(outputFolder, "level.dat"), resolvers.dataVersion().getStorageVersion(), data);
    }

    /**
     * Write the local player data.
     *
     * @param output the level to get the local player from.
     * @throws Exception if it fails to write the local player.
     */
    protected void writeLocalPlayer(ChunkerLevel output) throws Exception {
        if (output.getPlayer() == null || converter.shouldAllowNBTCopying()) return; // Don't write local player
        ChunkerLevelPlayer player = output.getPlayer();

        // Create the NBT
        CompoundTag playerTag = new CompoundTag();

        // Write position data
        playerTag.put("Pos", ListTag.fromValues(TagType.FLOAT, List.of(
                (float) player.getPositionX(),
                (float) player.getPositionY() + BedrockLevelReader.PLAYER_HEIGHT,
                (float) player.getPositionZ()
        )));
        playerTag.put("Motion", ListTag.fromValues(TagType.FLOAT, List.of(
                (float) player.getMotionX(),
                (float) player.getMotionY(),
                (float) player.getMotionZ()
        )));
        playerTag.put("Rotation", ListTag.fromValues(TagType.FLOAT, List.of(
                player.getYaw(),
                player.getPitch()
        )));

        // Write main inventory
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, new ArrayList<>(player.getInventory().size()));
        for (Byte2ObjectMap.Entry<ChunkerItemStack> tag : player.getInventory().byte2ObjectEntrySet()) {
            // Don't include slots not in the main inventory
            if ((tag.getByteKey() & 0xFF) >= 100) continue;

            // Don't write air to inventories
            if (tag.getValue().getIdentifier().isAir()) continue;

            // Write the item with slot
            Optional<CompoundTag> item = resolvers.writeItem(tag.getValue());
            if (item.isEmpty()) continue;

            // Write the slot
            item.get().put("Slot", tag.getByteKey());

            // Add to items
            items.add(item.get());
        }
        playerTag.put("Inventory", items);

        // Write armor (reversed)
        ListTag<CompoundTag, Map<String, Tag<?>>> armor = new ListTag<>(TagType.COMPOUND, new ArrayList<>(4));
        for (int i = 3; i >= 0; i--) {
            ChunkerItemStack chunkerItemStack = player.getInventory().get((byte) (100 + i));

            // Use air if the item isn't present
            if (chunkerItemStack == null) {
                chunkerItemStack = new ChunkerItemStack(ChunkerBlockIdentifier.AIR);
            }

            // Write the item
            Optional<CompoundTag> item = resolvers.writeItem(chunkerItemStack);
            if (item.isEmpty()) continue;

            // Add to armor
            armor.add(item.get());
        }
        playerTag.put("Armor", armor);

        // Write offhand
        ListTag<CompoundTag, Map<String, Tag<?>>> offhand = new ListTag<>(TagType.COMPOUND, new ArrayList<>(1));
        for (int i = 0; i < 1; i++) {
            ChunkerItemStack chunkerItemStack = player.getInventory().get((byte) (150 + i));

            // Use air if the item isn't present
            if (chunkerItemStack == null) {
                chunkerItemStack = new ChunkerItemStack(ChunkerBlockIdentifier.AIR);
            }

            // Write the item
            Optional<CompoundTag> item = resolvers.writeItem(chunkerItemStack);
            if (item.isEmpty()) continue;

            // Add to armor
            offhand.add(item.get());
        }
        playerTag.put("Offhand", offhand);

        // Write dimension / gamemode
        playerTag.put("DimensionId", (int) player.getDimension().getBedrockID());

        // Handle specific game types
        if (player.getGameType() == 3 || player.getGameType() == 4 || player.getGameType() == 6) {
            // Use spectator (or fall back to adventure) (disabled in 1.19U5 due to issues initializing)
            playerTag.put("PlayerGameMode", getVersion().isGreaterThanOrEqual(1, 18, 30) && getVersion().isLessThan(1, 19, 50) ? 6 : 2);
        } else {
            playerTag.put("PlayerGameMode", player.getGameType());
        }

        // Add default movement attribute (Bedrock doesn't use the player default so you ends up using 0.7)
        CompoundTag movementAttribute = new CompoundTag();
        movementAttribute.put("Base", 0.1F);
        movementAttribute.put("Current", 0.1F);
        movementAttribute.put("DefaultMax", Float.MAX_VALUE);
        movementAttribute.put("DefaultMin", 0F);
        movementAttribute.put("Max", Float.MAX_VALUE);
        movementAttribute.put("Min", 0F);
        movementAttribute.put("Name", "minecraft:movement");
        playerTag.put("Attributes", new ListTag<>(TagType.COMPOUND, List.of(movementAttribute)));

        // Write to field
        byte[] value = Tag.writeBedrockNBT(playerTag);
        database.put(LevelDBKey.LOCAL_PLAYER, value);
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
     * @return a new world writer.
     */
    public BedrockWorldWriter createWorldWriter() {
        return new BedrockWorldWriter(outputFolder, converter, resolvers, database);
    }
}
