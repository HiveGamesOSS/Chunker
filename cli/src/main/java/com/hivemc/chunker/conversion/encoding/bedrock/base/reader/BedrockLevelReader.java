package com.hivemc.chunker.conversion.encoding.bedrock.base.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.reader.LevelReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.BedrockReaderWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.handlers.LevelConversionHandler;
import com.hivemc.chunker.conversion.handlers.WorldConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.level.*;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.FloatTag;
import com.hivemc.chunker.scheduling.task.FutureTask;
import com.hivemc.chunker.scheduling.task.ProgressiveTask;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.table.BloomFilterPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A reader for Bedrock levels.
 */
public class BedrockLevelReader implements LevelReader, BedrockReaderWriter {
    public static final float PLAYER_HEIGHT = 1.62001f; // Height + 0.00001 offset (based on save data)
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
    protected final File inputDirectory;
    protected final Version inputVersion;
    protected final Converter converter;
    protected final BedrockResolvers resolvers;
    protected DB database;

    /**
     * Create a new level reader.
     *
     * @param inputDirectory the input world directory.
     * @param inputVersion   the version being read.
     * @param converter      the converter instance.
     */
    public BedrockLevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        this.inputDirectory = inputDirectory;
        this.inputVersion = inputVersion;
        this.converter = converter;
        resolvers = buildResolvers(converter).build();
    }

    /**
     * Open the LevelDB database.
     *
     * @throws IOException if it fails to open the database.
     */
    protected void openDatabase() throws IOException {
        File databaseDirectory = new File(inputDirectory, "db");

        // Delete LOCK file (as it may have been left behind by a bad abort / corrupted DB)
        new File(databaseDirectory, "LOCK").delete();

        // LevelDB Options
        Options options = new Options();
        options.compressionType(CompressionType.ZLIB_RAW);
        options.blockSize(160 * 1024); // 160KB
        options.filterPolicy(new BloomFilterPolicy(10));
        options.createIfMissing(true);

        // Create the factory and open the database
        DBFactory factory = new Iq80DBFactory();
        database = factory.open(databaseDirectory, options);
    }

    @Override
    public void free() throws Exception {
        // Called on exception / done
        if (database != null) {
            try {
                database.close();
            } finally {
                database = null;
            }
        }
    }

    @Override
    public void readLevel(LevelConversionHandler levelConversionHandler) throws IOException {
        // Open database
        openDatabase();

        // Collect level data
        FutureTask<WorldConversionHandler> levelDataCollection = Task.asyncUnwrap("Collecting Level Data", TaskWeight.MEDIUM, this::collectLevelData, levelConversionHandler);

        Task<EnumMap<Dimension, Map<RegionCoordPair, Set<ChunkCoordPair>>>> usedRegions = Task.async("Collecting Used Regions", TaskWeight.MEDIUM, () -> {
            // Create a lookup for each dimension and region present
            EnumMap<Dimension, Map<RegionCoordPair, Set<ChunkCoordPair>>> dimensionLookup = new EnumMap<>(Dimension.class);
            // Scan the database for valid chunks
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
                                continue;
                            }
                        }

                        // Read subChunk Y
                        if (containsSubChunk) {
                            reader.readByte();
                        }

                        // Read type
                        byte type = reader.readByte();

                        // Ensure the chunk either has: biome/height data, chunk data, block entity/entity data
                        if (type != LevelDBChunkType.DATA_2D.getId() && type != LevelDBChunkType.DATA_3D.getId()
                                && type != LevelDBChunkType.SUB_CHUNK_PREFIX.getId()
                                && type != LevelDBChunkType.ENTITY.getId() && type != LevelDBChunkType.BLOCK_ENTITY.getId()) {
                            continue;
                        }

                        // Create the pairs used for adding to the lookup
                        ChunkCoordPair chunkCoordPair = new ChunkCoordPair(x, z);
                        RegionCoordPair regionCoordPair = chunkCoordPair.getRegion();

                        // Add to lookup
                        Map<RegionCoordPair, Set<ChunkCoordPair>> regionLookup = dimensionLookup.computeIfAbsent(dimension, (ignored) -> new Object2ObjectOpenHashMap<>());
                        Set<ChunkCoordPair> columns = regionLookup.computeIfAbsent(regionCoordPair, (ignored) -> new ObjectOpenHashSet<>());
                        columns.add(chunkCoordPair);
                    }
                }
            }

            return dimensionLookup;
        });

        // When we've collected the level data and present chunks, go through each world and call the reading process
        ProgressiveTask<Void> worldReading = levelDataCollection.thenConsume("Reading Worlds", TaskWeight.HIGHEST, (worldConversionHandler) -> {
            if (worldConversionHandler == null) return; // This can be null if the worlds aren't handled by the reader

            // Read the worlds
            usedRegions.thenConsume("Reading Worlds", TaskWeight.HIGHEST, (dimensionLookup) -> {
                List<Task<Void>> worlds = new ArrayList<>(dimensionLookup.size());
                for (Map.Entry<Dimension, Map<RegionCoordPair, Set<ChunkCoordPair>>> entry : dimensionLookup.entrySet()) {
                    // Create a world reader if it should be processed
                    if (converter.shouldProcessDimension(entry.getKey())) {
                        BedrockWorldReader reader = createWorldReader(entry.getValue(), entry.getKey());

                        // Read the world
                        worlds.add(Task.asyncConsume("Reading World", TaskWeight.HIGHER, reader::readWorld, worldConversionHandler));
                    }
                }

                Task.join(worlds).then("Flushing Worlds", TaskWeight.MEDIUM, worldConversionHandler::flushWorlds);
            });
        });

        // Flush the level after all the worlds have been read
        worldReading.then("Flushing Level", TaskWeight.MEDIUM, levelConversionHandler::flushLevel);
    }

    /**
     * Collect all the data required for ChunkerLevel and submit it.
     *
     * @param levelConversionHandler the handler for submitting the level data to.
     * @return the task which submits the ChunkerLevel data.
     */
    protected Task<WorldConversionHandler> collectLevelData(LevelConversionHandler levelConversionHandler) {
        ChunkerLevel output = new ChunkerLevel();
        ProgressiveTask<Void> parseLevelSettings = Task.asyncConsume("Parsing Level Settings", TaskWeight.NORMAL, this::parseLevelSettings, output);
        ProgressiveTask<Void> parseLocalPlayer = Task.asyncConsume("Parsing Local Player", TaskWeight.NORMAL, this::parseLocalPlayer, output);
        ProgressiveTask<Void> parseMaps = Task.asyncConsume("Parsing Saved Maps", TaskWeight.NORMAL, this::parseMaps, output);
        ProgressiveTask<Void> parsePortals = Task.asyncConsume("Parsing Portals", TaskWeight.NORMAL, this::parsePortals, output);

        // When those tasks are done we can convert the level
        return Task.join(parseLevelSettings, parseLocalPlayer, parseMaps, parsePortals)
                .thenUnwrap("Converting Level", TaskWeight.LOW, levelConversionHandler::convertLevel, output);
    }

    /**
     * Parse the level.dat of the world.
     *
     * @param output the output to set the settings and original data.
     * @throws Exception if it failed to parse the level.dat
     */
    protected void parseLevelSettings(ChunkerLevel output) throws Exception {
        CompoundTag level = Tag.readBedrockNBT(new File(inputDirectory, "level.dat"));
        output.setSettings(ChunkerLevelSettings.fromNBT(Objects.requireNonNull(level), this, converter));
        output.setOriginalLevelData(level);
    }

    @Override
    public @Nullable Object readCustomLevelSetting(@NotNull CompoundTag root, @NotNull String targetName, @NotNull Class<?> type) {
        // Check for AutumnDrop2025 support
        if (targetName.equals("AutumnDrop2025")) {
            return false;
        }

        // Check for SummerDrop2025 support
        if (targetName.equals("SummerDrop2025")) {
            return false;
        }

        // Check for WinterDrop2024 support
        if (targetName.equals("WinterDrop2024")) {
            return false;
        }

        // Check for R21 support
        if (targetName.equals("R21Support")) {
            return false;
        }

        // Check for R20 support
        if (targetName.equals("R20Support")) {
            return false;
        }

        // Check for caves and cliffs
        if (targetName.equals("CavesAndCliffs")) {
            return false;
        }

        // Read flat world version
        if (targetName.equals("FlatWorldVersion")) {
            return root.getInt("WorldVersion", 0);
        }

        // Default implementation for seed
        if (targetName.equals("RandomSeed")) {
            // After StorageVersion 8, seed became based on a long
            if (root.getInt("StorageVersion", 0) > 8) {
                return root.contains(targetName) ? String.valueOf(root.getLong(targetName)) : null;
            } else {
                // Integer is used as this is what is displayed in Bedrock settings on older versions
                return root.contains(targetName) ? String.valueOf((int) root.getLong(targetName)) : null;
            }
        }

        if (type == ChunkerGeneratorType.class) {
            int generator = root.getInt("Generator", 1); // Default to normal

            return switch (generator) {
                case 0 -> ChunkerGeneratorType.CUSTOM; // Legacy
                case 1 -> ChunkerGeneratorType.NORMAL; // Normal generation
                case 2 -> {
                    // Flat generator
                    String generatorOptions = root.getString("generatorOptions", null);
                    if (generatorOptions != null && !generatorOptions.isEmpty()) {
                        if (VOID_WORD_STRING.equals(root.getString("FlatWorldLayers", null))) {
                            // VOID
                            yield ChunkerGeneratorType.VOID;
                        }

                        yield ChunkerGeneratorType.CUSTOM;
                    }

                    yield ChunkerGeneratorType.FLAT;
                }
                default -> ChunkerGeneratorType.CUSTOM; // Unknown
            };
        }

        // Throw an error, so we know to handle something if we missed it
        throw new IllegalArgumentException("Type " + targetName + " isn't implemented for setting parsing");
    }

    /**
     * Parse the local player data.
     *
     * @param output the output to set the player data.
     */
    protected void parseLocalPlayer(ChunkerLevel output) {
        byte[] bytes = database.get(LevelDBKey.LOCAL_PLAYER);
        if (bytes == null) return; // No local player
        try {
            // Parse the NBT
            CompoundTag player = Objects.requireNonNull(Tag.readBedrockNBT(bytes));

            // Read the position data
            List<Float> positions = player.getListValues("Pos", FloatTag.class, List.of(0F, 0F, 0F));
            List<Float> motion = player.getListValues("Motion", FloatTag.class, List.of(0F, 0F, 0F));
            List<Float> rotation = player.getListValues("Rotation", FloatTag.class, List.of(0F, 0F));

            // Parse the inventory
            Byte2ObjectAVLTreeMap<ChunkerItemStack> inventory = new Byte2ObjectAVLTreeMap<>();

            // Parse main inventory
            ListTag<CompoundTag, Map<String, Tag<?>>> items = player.getList("Inventory", CompoundTag.class, null);
            if (items != null) {
                byte index = 0;
                for (CompoundTag itemTag : items) {
                    byte slot = itemTag.getByte("Slot", index);

                    // Read item
                    ChunkerItemStack item = resolvers.readItem(itemTag);
                    inventory.put(slot, item);

                    // Increment index
                    index++;
                }
            }

            // Parse armor (reversed)
            ListTag<CompoundTag, Map<String, Tag<?>>> armor = player.getList("Armor", CompoundTag.class, null);
            if (armor != null) {
                byte index = 0;
                for (CompoundTag itemTag : armor) {
                    // To line up with Java an offset of 100 is used
                    byte slot = (byte) (100 + (3 - itemTag.getByte("Slot", index)));

                    // Read item
                    ChunkerItemStack item = resolvers.readItem(itemTag);
                    inventory.put(slot, item);

                    // Increment index
                    index++;
                }
            }

            // Parse off-hand
            ListTag<CompoundTag, Map<String, Tag<?>>> offhand = player.getList("Offhand", CompoundTag.class, null);
            if (offhand != null) {
                byte index = 0;
                for (CompoundTag itemTag : offhand) {
                    // To line up with Java an offset of 150 is used
                    byte slot = (byte) (150 + itemTag.getByte("Slot", index));

                    // Read item
                    ChunkerItemStack item = resolvers.readItem(itemTag);
                    inventory.put(slot, item);

                    // Increment index
                    index++;
                }
            }
            output.setPlayer(new ChunkerLevelPlayer(
                    Dimension.fromBedrockNBT(player.get("DimensionId"), Dimension.OVERWORLD),
                    positions.get(0),
                    positions.get(1) - PLAYER_HEIGHT, // Offset in Bedrock
                    positions.get(2),
                    motion.get(0),
                    motion.get(1),
                    motion.get(2),
                    rotation.get(0),
                    rotation.get(1),
                    inventory,
                    player.getInt("PlayerGameMode", 0)
            ));
        } catch (Exception e) {
            converter.logNonFatalException(e);
        }
    }

    @Override
    public Version getVersion() {
        return inputVersion;
    }

    /**
     * Parse all the in-game maps inside the level.
     *
     * @param output the output to add the maps to.
     */
    protected void parseMaps(ChunkerLevel output) {
        List<ChunkerMap> maps = new ArrayList<>();
        if (converter.shouldProcessMaps()) {
            List<Task<ChunkerMap>> tasks = new ArrayList<>();

            // Iterate through the database for maps
            try (DBIterator iterator = database.iterator()) {
                iterator.seek(LevelDBKey.MAP_PREFIX); // Skip to the key

                while (iterator.hasNext()) {
                    Map.Entry<byte[], byte[]> entry = iterator.next();

                    // Ensure it starts with the map key
                    if (!LevelDBKey.startsWith(entry.getKey(), LevelDBKey.MAP_PREFIX)) continue;

                    // Attempt to parse the mapID from the key
                    try {
                        String suffix = LevelDBKey.extractSuffix(entry.getKey(), LevelDBKey.MAP_PREFIX);
                        long mapID = Long.parseLong(suffix);

                        // Create the read task
                        tasks.add(Task.async("Parsing map", TaskWeight.NORMAL, () -> parseMap(mapID, entry.getValue())));
                    } catch (Exception e) {
                        converter.logNonFatalException(e);
                    }
                }
            }

            // Write maps back to our array when they're done
            if (!tasks.isEmpty()) {
                Task.join(tasks).thenConsume("Parsing map sync", TaskWeight.LOW, (mapArray) -> {
                    for (ChunkerMap map : mapArray) {
                        if (map == null) continue;
                        maps.add(map);
                    }

                    // Sort the list
                    maps.sort(ChunkerMap.BY_ID_COMPARATOR);
                });
            }
        }
        output.setMaps(maps);
    }

    /**
     * Parse a map from the ID and NBT encoded data.
     *
     * @param id   the ID of a map.
     * @param data the NBT as a byte array.
     * @return a parsed map otherwise null if it failed to parse.
     */
    @Nullable
    protected ChunkerMap parseMap(long id, byte[] data) {
        try {
            // Read the data
            CompoundTag mapCompound = Objects.requireNonNull(Tag.readBedrockNBT(data));

            // Create a nice ChunkerMap with all the properties we need (and defaults)
            return new ChunkerMap(
                    id,
                    id,
                    mapCompound.getShort("height", (short) 128),
                    mapCompound.getShort("width", (short) 128),
                    mapCompound.getByte("scale", (byte) 0),
                    Dimension.fromBedrockNBT(mapCompound.get("dimension"), Dimension.OVERWORLD),
                    mapCompound.getInt("xCenter", 0),
                    mapCompound.getInt("zCenter", 0),
                    mapCompound.getByte("unlimitedTracking", (byte) 0) != 0,
                    mapCompound.getByte("mapLocked", (byte) 0) != 0,
                    mapCompound.getByteArray("colors", null),
                    resolvers.converter().shouldAllowNBTCopying() ? mapCompound : null
            );

        } catch (Exception e) {
            converter.logNonFatalException(e);
            return null;
        }
    }

    /**
     * Parse all the portal data in the world.
     *
     * @param output the output to add the portal data to.
     */
    protected void parsePortals(ChunkerLevel output) {
        output.setPortals(new ArrayList<>());
        try {
            byte[] value = database.get(LevelDBKey.PORTALS);
            if (value == null) return;

            // Read the data
            CompoundTag wrappedData = Objects.requireNonNull(Tag.readBedrockNBT(value));
            CompoundTag data = wrappedData.getCompound("data");
            if (data == null) return;

            // Grab the portals
            ListTag<CompoundTag, Map<String, Tag<?>>> portalRecords = data.getList("PortalRecords", CompoundTag.class, null);
            if (portalRecords == null) return;
            for (CompoundTag portalRecord : portalRecords) {
                output.getPortals().add(new ChunkerPortal(
                        Dimension.fromBedrock((byte) portalRecord.getInt("DimId", 0), Dimension.OVERWORLD),
                        portalRecord.getInt("TpX", 0),
                        portalRecord.getInt("TpY", 0),
                        portalRecord.getInt("TpZ", 0),
                        portalRecord.getByte("Span", (byte) 0),
                        portalRecord.getByte("Xa", (byte) 0),
                        portalRecord.getByte("Za", (byte) 0)
                ));
            }
        } catch (Exception e) {
            converter.logNonFatalException(e);
        }
    }

    /**
     * Create a new reader for worlds.
     *
     * @param presentRegions the regions present in the dimension.
     * @param dimension      the dimension type.
     * @return the newly created world reader.
     */
    public BedrockWorldReader createWorldReader(Map<RegionCoordPair, Set<ChunkCoordPair>> presentRegions, Dimension dimension) {
        return new BedrockWorldReader(resolvers, converter, database, presentRegions, dimension);
    }
}
