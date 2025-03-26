package com.hivemc.chunker.conversion.encoding.java.base.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.reader.WorldReader;
import com.hivemc.chunker.conversion.encoding.java.base.reader.util.MCAReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.handlers.WorldConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.scheduling.task.ProgressiveTask;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A reader for Java worlds.
 */
public class JavaWorldReader implements WorldReader {
    protected final Converter converter;
    protected final JavaResolvers resolvers;
    protected final File dimensionFolder;
    protected final Dimension dimension;

    /**
     * Create a new world reader.
     *
     * @param converter       the converter instance.
     * @param resolvers       the resolvers to use.
     * @param dimensionFolder the dimension folder to read data from.
     * @param dimension       the dimension being read.
     */
    public JavaWorldReader(Converter converter, JavaResolvers resolvers, File dimensionFolder, Dimension dimension) {
        this.converter = converter;
        this.resolvers = resolvers;
        this.dimensionFolder = dimensionFolder;
        this.dimension = dimension;
    }

    @Override
    public void readWorld(WorldConversionHandler worldConversionHandler) {
        Set<RegionCoordPair> regions = new ObjectOpenHashSet<>();
        ChunkerWorld chunkerWorld = new ChunkerWorld(
                dimension,
                regions
        );

        // Get each region file and loop through
        File[] folders = getMCAFolders();
        Set<String> knownRegionFiles = new ObjectOpenHashSet<>();

        for (File folder : folders) {
            File[] regionFiles = folder.listFiles((parent, fileName) -> fileName.endsWith(".mca"));
            if (regionFiles != null) {
                for (File regionFile : regionFiles) {
                    String[] parts = regionFile.getName().split("\\.");
                    if (parts.length != 4 || regionFile.length() < 4096) continue;

                    // Add to the known files
                    knownRegionFiles.add(regionFile.getAbsolutePath());

                    // Parse and add the region
                    try {
                        int x = Integer.parseInt(parts[1]);
                        int z = Integer.parseInt(parts[2]);

                        // Add to the list of regions
                        regions.add(new RegionCoordPair(x, z));
                    } catch (NumberFormatException e) {
                        // Ignore the region file
                    }
                }
            }
        }

        // Copy the regions for reading of the world
        HashSet<RegionCoordPair> regionsCopy = new HashSet<>(regions);

        // Submit world info (done before column reading)
        Task<ColumnConversionHandler> convertWorld = worldConversionHandler.convertWorld(chunkerWorld);

        // Handle the reading of the regions
        Task<Void> regionProcessing = convertWorld.thenConsume("Reading regions", TaskWeight.HIGHER, (columnConversionHandler) -> {
            if (columnConversionHandler == null) return; // This can be null if the columns aren't handled by the reader

            // Read the regions
            ProgressiveTask<Void> readingRegionFiles = Task.async("Reading region files", TaskWeight.HIGHER, () -> readRegionFiles(regionsCopy, knownRegionFiles, columnConversionHandler));

            // Call the flush task after all the region files have been read
            readingRegionFiles.then("Flushing columns", TaskWeight.MEDIUM, columnConversionHandler::flushColumns);
        });

        // When the region processing is done flush the world
        regionProcessing.then("Flushing world", TaskWeight.MEDIUM, () -> worldConversionHandler.flushWorld(chunkerWorld));
    }

    /**
     * Read all the region files and submit the columns.
     *
     * @param regions                 the region co-ordinates to read.
     * @param knownRegionFiles        a set of files which can be valid .mca files (exist and are bigger than 4096 bytes)
     * @param columnConversionHandler the handler to submit the columns to.
     */
    protected void readRegionFiles(Set<RegionCoordPair> regions, Set<String> knownRegionFiles, ColumnConversionHandler columnConversionHandler) {
        // Process regions
        for (RegionCoordPair region : regions) {
            if (converter.shouldProcessRegion(dimension, region)) {
                // Multiple region files can be handled by later versions, so it's abstracted here
                File[] regionFiles = getRegionFiles(region, knownRegionFiles);

                // Read the region file then perform GC, this ensures in systems where the Java process is not bound
                // we do not consume too much memory and keep it fair to other processes
                Task.async("Reading region file", TaskWeight.NORMAL, () -> readRegion(regionFiles, region, columnConversionHandler))
                        .then("Region - Flushing", TaskWeight.MEDIUM, () -> columnConversionHandler.flushRegion(region))
                        .then("Region - System::GC", TaskWeight.NONE, System::gc);
            }
        }
    }

    /**
     * Get all the folders which contain MCA files for this dimension.
     *
     * @return an array of all the folders (to be combined).
     */
    protected File[] getMCAFolders() {
        // Base version only uses region folder
        return new File[]{new File(dimensionFolder, "region")};
    }

    /**
     * Get a list of all the valid MCA files for a region.
     *
     * @param region     the region co-ordinates.
     * @param knownFiles a hashset of absolute paths to known files within the MCAFolders.
     * @return an array of all the matching .mca files, null entries are used when the file isn't present.
     */
    protected @Nullable File[] getRegionFiles(RegionCoordPair region, Set<String> knownFiles) {
        File[] folders = getMCAFolders();
        File[] files = new File[folders.length];

        // Create a path for each MCA folder
        File[] mcaFolders = getMCAFolders();
        for (int i = 0; i < mcaFolders.length; i++) {
            File folder = mcaFolders[i];
            File temp = new File(folder, "r." + region.regionX() + "." + region.regionZ() + ".mca");

            // Only add the file if it's known to exist
            if (knownFiles.contains(temp.getAbsolutePath())) {
                files[i] = temp;
            }
        }

        // Return the files
        return files;
    }

    /**
     * Read a region file.
     *
     * @param regionFiles             the region files which should be read for the region (e.g. entities in later
     *                                versions).
     * @param region                  the region co-ordinates.
     * @param columnConversionHandler the conversion handler to submit the read columns to.
     */
    @SuppressWarnings("resource")
    protected void readRegion(@Nullable File[] regionFiles, RegionCoordPair region, ColumnConversionHandler columnConversionHandler) {
        int regionFilesCount = regionFiles.length;
        MCAReader[] mcaReaders = new MCAReader[regionFilesCount];
        try {
            // Open all our required files
            boolean foundValidFile = false;
            for (int i = 0; i < regionFilesCount; i++) {
                File file = regionFiles[i];

                // Skip if the file doesn't exist / is invalid
                if (file == null) continue;

                // Otherwise open a random access file
                try {
                    mcaReaders[i] = new MCAReader(converter, file);
                    foundValidFile = true;
                } catch (FileNotFoundException e) {
                    // Ignored, it'll be null if this happens
                }
            }

            // Don't continue if there's no files to read
            if (!foundValidFile) {
                converter.logNonFatalException(new Exception("Misnamed region file for " + dimension + ", " + region));
                return;
            }

            // Stage 1. Collect all known offsets
            Int2ObjectMap<int[]> positionsToOffsets = new Int2ObjectOpenHashMap<>();
            for (int regionFileIndex = 0; regionFileIndex < regionFilesCount; regionFileIndex++) {
                MCAReader mcaReader = mcaReaders[regionFileIndex];
                if (mcaReader == null) continue;

                try {
                    // Read the header which contains the chunk offsets
                    int[] offsets = mcaReader.readOffsetTable();
                    for (int i = 0; i < offsets.length; i++) {
                        int offset = offsets[i];

                        // Only record the offset if it's more than 0, the first 4096 is the header, so it's invalid to be there
                        if (offset > 0) {
                            int[] mcaOffsets = positionsToOffsets.computeIfAbsent(i, (ignored) -> new int[regionFilesCount]);
                            mcaOffsets[regionFileIndex] = offset;
                        }
                    }
                } catch (Exception e) {
                    converter.logNonFatalException(e);
                }
            }

            // Stage 2. Iterate found regions, lookup each one and combine the results
            for (Int2ObjectMap.Entry<int[]> columnOffsets : positionsToOffsets.int2ObjectEntrySet()) {
                ChunkCoordPair localCoords = new ChunkCoordPair(
                        columnOffsets.getIntKey() & 31,
                        columnOffsets.getIntKey() >> 5
                );
                ChunkCoordPair columnsCoords = region.getChunk(localCoords.chunkX(), localCoords.chunkZ());

                // Ignore if this column shouldn't be processed
                if (!converter.shouldProcessColumn(dimension, columnsCoords)) continue;

                // Iterate through each region file
                List<Task<CompoundTag>> decompressingTasks = new ArrayList<>(regionFilesCount);
                List<Integer> decompressingTasksIndexes = new ArrayList<>(regionFilesCount);

                for (int regionFileIndex = 0; regionFileIndex < regionFilesCount; regionFileIndex++) {
                    MCAReader mcaReader = mcaReaders[regionFileIndex];
                    if (mcaReader == null) continue;

                    // Get the column offset specific for this region file
                    int offset = columnOffsets.getValue()[regionFileIndex];
                    if (offset <= 0) continue; // Skip if it's 0 or less as that indicates it's not used in this file

                    try {
                        // Schedule the decompression of this column
                        decompressingTasks.add(mcaReader.readColumn(columnsCoords, offset));

                        // Add to the indexes, so we know this file was found
                        decompressingTasksIndexes.add(regionFileIndex);
                    } catch (Exception e) {
                        converter.logNonFatalException(e);
                    }
                }
                // Wait for decompression tasks to complete (note: null tasks will be in place of regions that weren't found)
                Task.join(decompressingTasks)
                        .then("Combining input column NBT", TaskWeight.LOW, (results) -> {
                            // Some indexes may have been skipped if data wasn't found, so we need to make a predictable order for combining tags
                            CompoundTag[] compoundTags = new CompoundTag[regionFilesCount];
                            for (int i = 0; i < decompressingTasksIndexes.size(); i++) {
                                compoundTags[decompressingTasksIndexes.get(i)] = results.get(i);
                            }
                            return combineColumnCompounds(compoundTags);
                        })
                        .then("Creating Column Reader", TaskWeight.LOW, (column) -> createColumnReader(columnsCoords, column))
                        .thenConsume("Reading Column", TaskWeight.HIGHER, (columnReader) -> columnReader.readColumn(columnConversionHandler));

            }
        } finally {
            // Ensure all the region files get closed
            for (MCAReader mcaReader : mcaReaders) {
                if (mcaReader == null) continue;
                try {
                    mcaReader.close();
                } catch (IOException e) {
                    // Failed to close the file
                    converter.logNonFatalException(e);
                }
            }
        }
    }

    /**
     * Combine compoundTags fetched from multiple files.
     *
     * @param compoundTags the compound tags in the same order as the folders array with null when a tag is missing.
     * @return the combined compound tag.
     */
    protected CompoundTag combineColumnCompounds(CompoundTag[] compoundTags) {
        // By default, no combining is supported in older versions
        if (compoundTags.length > 1)
            throw new IllegalArgumentException("Combining compounds is unsupported at this version");
        return compoundTags[0];
    }

    /**
     * Create a new column reader.
     *
     * @param worldChunkCoords the co-ordinates of the column.
     * @param columnNBT        the NBT compound which was read for the column.
     * @return a newly created column reader.
     */
    public JavaColumnReader createColumnReader(ChunkCoordPair worldChunkCoords, CompoundTag columnNBT) {
        return new JavaColumnReader(converter, resolvers, dimension, worldChunkCoords, columnNBT);
    }
}
