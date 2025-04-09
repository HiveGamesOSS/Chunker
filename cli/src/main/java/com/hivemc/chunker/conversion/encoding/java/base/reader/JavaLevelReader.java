package com.hivemc.chunker.conversion.encoding.java.base.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.reader.LevelReader;
import com.hivemc.chunker.conversion.encoding.java.base.JavaReaderWriter;
import com.hivemc.chunker.conversion.encoding.java.base.reader.util.MCAReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.handlers.LevelConversionHandler;
import com.hivemc.chunker.conversion.handlers.WorldConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.level.*;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.DoubleTag;
import com.hivemc.chunker.nbt.tags.primitive.FloatTag;
import com.hivemc.chunker.scheduling.task.FutureTask;
import com.hivemc.chunker.scheduling.task.ProgressiveTask;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import com.hivemc.chunker.util.BlockPosition;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * A reader for Java levels.
 */
public class JavaLevelReader implements LevelReader, JavaReaderWriter {
    /**
     * Map of slot ID to equipment name (used for 1.21.5 Java).
     */
    public static final Map<Byte, String> SLOT_TO_EQUIPMENT = Map.of(
            (byte) -106, "offhand",
            (byte) 100, "feet",
            (byte) 101, "legs",
            (byte) 102, "chest",
            (byte) 103, "head"
    );
    protected final File inputDirectory;
    protected final Version inputVersion;
    protected final Converter converter;
    protected final JavaResolvers resolvers;

    /**
     * Create a new java level reader.
     *
     * @param inputDirectory the input directory of the world.
     * @param inputVersion   the input version being read.
     * @param converter      the converter instance.
     */
    public JavaLevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        this.inputDirectory = inputDirectory;
        this.inputVersion = inputVersion;
        this.converter = converter;
        resolvers = buildResolvers(converter).build();
    }

    /**
     * Get the base directory used for a dimension.
     *
     * @param directory the root world folder.
     * @param dimension the dimension.
     * @return the folder which the dimension data resides in.
     */
    public static File getDimensionBaseDirectory(File directory, Dimension dimension) {
        return switch (dimension) {
            case OVERWORLD -> directory;
            case NETHER -> new File(directory, "DIM-1");
            case THE_END -> new File(directory, "DIM1");
        };
    }

    @Override
    public void readLevel(LevelConversionHandler levelConversionHandler) {
        // Collect level data
        FutureTask<WorldConversionHandler> levelDataCollection = Task.asyncUnwrap("Collecting Level Data", TaskWeight.MEDIUM, this::collectLevelData, levelConversionHandler);

        // When we've collected the level data, go through each world and call the reading process
        ProgressiveTask<Void> worldReading = levelDataCollection.thenConsume("Reading Worlds", TaskWeight.HIGHEST, (worldConversionHandler) -> {
            if (worldConversionHandler == null) return; // This can be null if the worlds aren't handled by the reader

            // Read worlds
            List<Task<Void>> worlds = new ArrayList<>(3);
            for (Dimension dimension : Dimension.values()) {
                File dimensionBaseDirectory = getDimensionBaseDirectory(inputDirectory, dimension);

                // Create a world reader if the dimension is present
                if (dimensionBaseDirectory.exists() && converter.shouldProcessDimension(dimension)) {
                    JavaWorldReader reader = createWorldReader(dimensionBaseDirectory, dimension);

                    // Read the world
                    worlds.add(Task.asyncConsume("Reading World", TaskWeight.HIGHER, reader::readWorld, worldConversionHandler));
                }
            }

            Task.join(worlds).then("Flushing Worlds", TaskWeight.MEDIUM, worldConversionHandler::flushWorlds);
        });

        // Flush the level after all the worlds have been read
        worldReading.then("Flushing Level", TaskWeight.MEDIUM, levelConversionHandler::flushLevel);
    }

    /**
     * Schedule and collect all the level data.
     *
     * @param levelConversionHandler the conversion handler to submit the ChunkerLevel data to.
     * @return the task of the submitted level data.
     */
    protected Task<WorldConversionHandler> collectLevelData(LevelConversionHandler levelConversionHandler) {
        ChunkerLevel output = new ChunkerLevel();
        ProgressiveTask<Void> parseLevelSettings = Task.asyncConsume("Parsing Level Settings", TaskWeight.NORMAL, this::parseLevelSettings, output);
        ProgressiveTask<Void> parseMaps = Task.asyncConsume("Parsing Saved Maps", TaskWeight.NORMAL, this::parseMaps, output);
        ProgressiveTask<Void> parsePOI = Task.asyncConsume("Parsing POIs", TaskWeight.NORMAL, this::parsePOI, output);

        // When those tasks are done we can convert the level
        return Task.join(parseLevelSettings, parseMaps, parsePOI).thenUnwrap("Converting Level", TaskWeight.LOW, levelConversionHandler::convertLevel, output);
    }

    /**
     * Parse the points-of-interest data for the world.
     *
     * @param output the output level to write POI data to (currently only portals).
     * @throws Exception if it failed to parse the poi data.
     */
    protected void parsePOI(ChunkerLevel output) throws Exception {
        output.setPortals(Collections.synchronizedList(new ArrayList<>()));

        // Loop through dimensions and parse the POI
        for (Dimension dimension : Dimension.values()) {
            File poiBaseDirectory = new File(getDimensionBaseDirectory(inputDirectory, dimension), "poi");

            // Don't parse if it doesn't exist / it shouldn't be processed
            if (!poiBaseDirectory.exists() || !converter.shouldProcessDimension(dimension)) continue;

            // Grab all the .mca files
            File[] mcaFiles = poiBaseDirectory.listFiles((parent, fileName) -> fileName.endsWith(".mca"));
            if (mcaFiles == null) continue;

            // Loop through and collect dimension POIs
            List<CompoundTag> collectedPOIs = Collections.synchronizedList(new ArrayList<>());
            FutureTask<Void> task = Task.asyncConsumeForEach("Reading POI region", TaskWeight.NORMAL, (file) -> parsePOI(dimension, file, collectedPOIs), mcaFiles);

            // Calculate the portals and add them to the portals
            task.then("Processing Dimension POIs", TaskWeight.NORMAL, () -> {
                output.getPortals().addAll(collectPortals(dimension, collectedPOIs));
            });
        }
    }

    /**
     * Collect all the portals from POI data.
     *
     * @param dimension     the dimension being read.
     * @param collectedPOIs all the collected points of interests as NBT tags.
     * @return a list of portals based on the POI data.
     */
    protected List<ChunkerPortal> collectPortals(Dimension dimension, List<CompoundTag> collectedPOIs) {
        List<ChunkerPortal> portals = new ArrayList<>();

        // Extract positions of all the portals
        List<List<BlockPosition>> portalPositions = new ArrayList<>();
        for (CompoundTag poi : collectedPOIs) {
            String type = poi.getString("type", null);
            if (!"minecraft:nether_portal".equals(type)) continue; // Not a portal

            int[] pos = poi.getIntArray("pos", null);
            if (pos == null || pos.length != 3) continue; // Not valid

            // Find any adjacent blocks and put it in that list
            BlockPosition entry = new BlockPosition(pos[0], pos[1], pos[2]);
            List<BlockPosition> list = findAdjacentPortalBlock(portalPositions, entry);

            // Create the list if one wasn't found
            if (list == null) {
                list = new ArrayList<>(1);
                portalPositions.add(list);
            }

            // Add the entry
            list.add(entry);
        }

        // Now use the portal groups to count portals
        for (List<BlockPosition> positions : portalPositions) {
            try {
                if (positions.size() < 2) continue; // Don't count portals that don't have more than 1 entry

                // Sort the positions
                positions.sort(BlockPosition.BY_Y_X_Z);

                // Grab the bottom positions
                BlockPosition bottomLeft = positions.get(0);
                BlockPosition nextToBottomLeft = positions.get(1);

                // Count how many on the same Y
                byte width = (byte) positions.stream().filter(position -> position.y() == bottomLeft.y()).count();

                // Create a portal
                ChunkerPortal portal = new ChunkerPortal(
                        dimension,
                        bottomLeft.x(),
                        bottomLeft.y(),
                        bottomLeft.z(),
                        width, // Count how many on the same Y
                        (byte) Math.abs(bottomLeft.x() - nextToBottomLeft.x()),
                        (byte) Math.abs(bottomLeft.z() - nextToBottomLeft.z())
                );
                portals.add(portal);
            } catch (Exception e) {
                converter.logNonFatalException(e);
            }
        }

        return portals;
    }

    /**
     * Find the adjacent portal blocks from a POI list.
     *
     * @param portalPositions the list of all the currently known portals.
     * @param matcher         the block to find an adjacent portal block for.
     * @return null if there are no adjacent portals, otherwise the list of portals that has adjacent blocks.
     */
    @Nullable
    protected List<BlockPosition> findAdjacentPortalBlock(List<List<BlockPosition>> portalPositions, BlockPosition matcher) {
        // Technical note: This will allow diagonals on two axis, but given portals need surrounding with obsidian, this is considered fine
        for (List<BlockPosition> positions : portalPositions) {
            for (BlockPosition entry : positions) {
                int difference = Math.abs(entry.x() - matcher.x()) + Math.abs(entry.y() - matcher.y()) + Math.abs(entry.z() - matcher.z());
                if (difference <= 2) {
                    return positions; // This list is close enough
                }
            }
        }

        return null; // Can't find any adjacent
    }

    /**
     * Collect all the POIs from inside a region file.
     *
     * @param dimension     the dimension the POI is being read from.
     * @param file          the region file to parse POI from.
     * @param collectedPOIs the output list to add POIs to.
     * @throws Exception if it failed to parse the POIs.
     */
    protected void parsePOI(Dimension dimension, File file, List<CompoundTag> collectedPOIs) throws Exception {
        String[] parts = file.getName().split("\\.");
        if (parts.length != 4 || file.length() < 4096)
            return; // Skip if it doesn't have the right parts or isn't the right size

        RegionCoordPair regionCoordPair;
        try {
            // Parse region co-ordinates
            int regionX = Integer.parseInt(parts[1]);
            int regionZ = Integer.parseInt(parts[2]);
            regionCoordPair = new RegionCoordPair(regionX, regionZ);
        } catch (NumberFormatException e) {
            // Ignore if the region co-ordinates didn't parse properly
            return;
        }

        // Read the MCA file
        try (MCAReader mcaReader = new MCAReader(converter, file)) {
            int[] offsets = mcaReader.readOffsetTable();

            // Read each chunk at each offset
            for (int i = 0; i < offsets.length; i++) {
                int offset = offsets[i];
                if (offset <= 0) continue; // Skip offsets which are invalid or empty

                ChunkCoordPair localCoords = new ChunkCoordPair(
                        i & 31,
                        i >> 5
                );

                // Get the columns co-ordinates
                ChunkCoordPair columnsCoords = regionCoordPair.getChunk(localCoords.chunkX(), localCoords.chunkZ());

                // Ignore if this column shouldn't be processed
                if (!converter.shouldProcessColumn(dimension, columnsCoords)) continue;

                mcaReader.readColumn(columnsCoords, offset).thenConsume("Parsing POI Region", TaskWeight.NORMAL, (compound) -> collectColumnPOIs(compound, collectedPOIs));
            }
        }
    }

    /**
     * Collect all the POIs from inside a column.
     *
     * @param column        the root of the POI data for the column.
     * @param collectedPOIs the output list to add POIs to.
     * @throws Exception if it failed to parse the POIs.
     */
    protected void collectColumnPOIs(@Nullable CompoundTag column, List<CompoundTag> collectedPOIs) throws Exception {
        if (column == null) return;

        // Parse each section in the POI
        CompoundTag sections = column.getCompound("Sections");
        if (sections == null) return;
        for (Map.Entry<String, Tag<?>> section : sections) {
            if (!(section.getValue() instanceof CompoundTag sectionTag)) continue;

            // Check if the entry is valid
            byte valid = sectionTag.getByte("Valid", (byte) 0);
            if (valid != (byte) 1) continue; // Invalid entry

            ListTag<CompoundTag, Map<String, Tag<?>>> records = sectionTag.getList("Records", CompoundTag.class, null);
            if (records == null) continue; // No entries

            // Add the collected POIs
            collectedPOIs.addAll(records.getValue());
        }
    }

    /**
     * Parse the settings from the level.dat.
     *
     * @param output the level where the settings should be written to.
     * @throws Exception if it failed to parse the level.dat.
     */
    protected void parseLevelSettings(ChunkerLevel output) throws Exception {
        CompoundTag level = Tag.readGZipJavaNBT(new File(inputDirectory, "level.dat"));

        // Parse settings
        output.setSettings(ChunkerLevelSettings.fromNBT(Objects.requireNonNull(level), this, converter));
        output.setOriginalLevelData(level);

        // Parse local player
        try {
            output.setPlayer(parsePlayer(level));
        } catch (Exception e) {
            converter.logNonFatalException(e);
        }
    }

    /**
     * Parse the local player from the level.dat.
     *
     * @param level the level.dat compound.
     * @return the parsed player or null if one wasn't found
     * @throws Exception if it failed to parse the player.
     */
    @Nullable
    protected ChunkerLevelPlayer parsePlayer(CompoundTag level) throws Exception {
        CompoundTag player = level.getCompound("Player");
        if (player == null) return null; // No local player

        List<Double> positions = player.getListValues("Pos", DoubleTag.class, List.of(0D, 0D, 0D));
        List<Double> motion = player.getListValues("Motion", DoubleTag.class, List.of(0D, 0D, 0D));
        List<Float> rotation = player.getListValues("Rotation", FloatTag.class, List.of(0F, 0F));

        // Parse the inventory
        Byte2ObjectAVLTreeMap<ChunkerItemStack> inventory = new Byte2ObjectAVLTreeMap<>();
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

        // Parse the equipment if there is an equipment tag
        CompoundTag equipment = player.getCompound("equipment");
        if (equipment != null) {
            for (Map.Entry<Byte, String> slot : SLOT_TO_EQUIPMENT.entrySet()) {
                CompoundTag itemTag = equipment.getCompound(slot.getValue());
                if (itemTag == null) continue;

                // Read item
                ChunkerItemStack item = resolvers.readItem(itemTag);
                inventory.put(slot.getKey().byteValue(), item);
            }
        }

        // Create the local player
        return new ChunkerLevelPlayer(
                Dimension.fromJavaNBT(player.get("Dimension"), Dimension.OVERWORLD),
                positions.get(0),
                positions.get(1),
                positions.get(2),
                motion.get(0),
                motion.get(1),
                motion.get(2),
                rotation.get(0),
                rotation.get(1),
                inventory,
                player.getInt("playerGameType", 0)
        );
    }

    @Override
    public @Nullable Object readCustomLevelSetting(@NotNull CompoundTag root, @NotNull String targetName, @NotNull Class<?> type) {
        // Check for SummerDrop2025 support
        if (targetName.equals("SummerDrop2025")) {
            return false;
        }

        if (targetName.equals("WinterDrop2024")) {
            // Not in less than 1.21.2
            return false;
        }

        if (targetName.equals("R21Support")) {
            // Not in less than 1.20.3
            return false;
        }

        if (targetName.equals("R20Support")) {
            // Not in less than 1.19.3
            return false;
        }

        if (targetName.equals("CavesAndCliffs")) {
            // Not in less than 1.17
            return false;
        }

        // Old format since less than 1.17
        if (targetName.equals("FlatWorldVersion")) {
            return 0;
        }

        // Default implementation for seed
        if (targetName.equals("RandomSeed")) {
            return root.contains(targetName) ? String.valueOf(root.getLong(targetName)) : null;
        }

        // Handling for the generator type
        if (type == ChunkerGeneratorType.class) {
            if (!root.contains("generatorName")) return ChunkerGeneratorType.NORMAL;

            String generatorName = root.getString("generatorName");

            if (generatorName.equals("default")) {
                return ChunkerGeneratorType.CUSTOM;
            }

            // If it's flat, it may be one of the presets, but we'll do a basic check
            if (generatorName.equals("flat")) {
                // If it has generator options, it's probably some sort of custom flat map
                if (root.contains("generatorOptions") && !root.getString("generatorOptions").isEmpty()) {
                    // Simple check for the VOID preset
                    if (root.getString("generatorOptions").startsWith("3;minecraft:air;127;")) {
                        // VOID
                        return ChunkerGeneratorType.VOID;
                    }

                    return ChunkerGeneratorType.CUSTOM;
                }

                return ChunkerGeneratorType.FLAT;
            }

            // Unknown
            return ChunkerGeneratorType.CUSTOM;
        }

        // Throw an error, so we know to handle something if we missed it
        throw new IllegalArgumentException("Type " + targetName + " isn't implemented for setting parsing");
    }

    @Override
    public Version getVersion() {
        return inputVersion;
    }

    /**
     * Parse all the maps for the level (in-game items).
     *
     * @param output the output level to add the maps to.
     */
    protected void parseMaps(ChunkerLevel output) {
        List<ChunkerMap> maps = new ArrayList<>();
        if (converter.shouldProcessMaps()) {
            File dataDirectory = new File(inputDirectory, "data");
            if (dataDirectory.isDirectory()) {
                File[] mapFiles = dataDirectory.listFiles((dir, name) -> name.startsWith("map_") && name.endsWith(".dat"));
                if (mapFiles != null) {
                    FutureTask<ChunkerMap[]> parsingTask = Task.asyncForEach("Parsing map", TaskWeight.NORMAL, this::parseMap, ChunkerMap[]::new, mapFiles);
                    parsingTask.thenConsume("Parsing map sync", TaskWeight.LOW, (mapArray) -> {
                        for (ChunkerMap map : mapArray) {
                            if (map == null) continue;
                            maps.add(map);
                        }

                        // Sort the list
                        maps.sort(ChunkerMap.BY_ID_COMPARATOR);
                    });
                }
            }
        }
        output.setMaps(maps);
    }

    /**
     * Parse a map from a .dat file.
     *
     * @param mapFile the map file name.
     * @return the parsed map or null if it failed to parse.
     */
    @Nullable
    protected ChunkerMap parseMap(File mapFile) {
        try {
            // Read the file
            CompoundTag mapCompound = Tag.readPossibleGZipJavaNBT(mapFile);
            if (mapCompound == null) return null; // Failed to parse

            // Remove the map_ prefix and the .dat suffix
            String idString = mapFile.getName().substring(4, mapFile.getName().length() - 4);
            int id = Integer.parseInt(idString);

            // Create a nice ChunkerMap with all the properties we need (and defaults)
            ChunkerMap map = new ChunkerMap(
                    id,
                    id,
                    mapCompound.getShort("height", (short) 128),
                    mapCompound.getShort("width", (short) 128),
                    mapCompound.getByte("scale", (byte) 0),
                    Dimension.fromJavaNBT(mapCompound.get("dimension"), Dimension.OVERWORLD),
                    mapCompound.getInt("xCenter", 0),
                    mapCompound.getInt("zCenter", 0),
                    mapCompound.getByte("unlimitedTracking", (byte) 0) != 0,
                    mapCompound.getByte("locked", (byte) 0) != 0,
                    null,
                    resolvers.converter().shouldAllowNBTCopying() ? mapCompound : null
            );

            // Read the bytes for the map
            if (mapCompound.contains("colors")) {
                map.setBytes(resolvers.readMapColors(mapCompound.getByteArray("colors")));
            }
            return map;

        } catch (Exception e) {
            converter.logNonFatalException(new Exception("Could not read map " + mapFile.getName(), e));
            return null;
        }
    }

    /**
     * Create a new world reader.
     *
     * @param dimensionFolder the dimension folder for the world.
     * @param dimension       the dimension being read.
     * @return a newly created reader.
     */
    public JavaWorldReader createWorldReader(File dimensionFolder, Dimension dimension) {
        return new JavaWorldReader(converter, resolvers, dimensionFolder, dimension);
    }
}
