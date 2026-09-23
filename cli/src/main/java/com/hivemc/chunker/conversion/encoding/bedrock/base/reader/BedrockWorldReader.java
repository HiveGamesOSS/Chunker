package com.hivemc.chunker.conversion.encoding.bedrock.base.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.reader.WorldReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.handlers.WorldConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.scheduling.task.ProgressiveTask;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.iq80.leveldb.DB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A reader for Bedrock dimensions.
 */
public class BedrockWorldReader implements WorldReader {
    public static final int MAX_IN_FLIGHT_REGIONS = 4;
    public static final Comparator<RegionCoordPair> REGION_ORDER = Comparator
            .comparingInt(RegionCoordPair::regionX)
            .thenComparingInt(RegionCoordPair::regionZ);
    protected final BedrockResolvers resolvers;
    protected final Converter converter;
    protected final Map<RegionCoordPair, Set<ChunkCoordPair>> presentRegions;
    protected final Dimension dimension;
    protected final DB database;
    private final int maxInFlightRegions;

    /**
     * Create a new Bedrock world reader.
     *
     * @param resolvers      the resolvers to be used.
     * @param converter      the converter instance.
     * @param database       the LevelDB database.
     * @param presentRegions the regions present in the world.
     * @param dimension      the dimension being converted.
     */
    public BedrockWorldReader(BedrockResolvers resolvers, Converter converter, DB database, Map<RegionCoordPair, Set<ChunkCoordPair>> presentRegions, Dimension dimension) {
        this(resolvers, converter, database, presentRegions, dimension, MAX_IN_FLIGHT_REGIONS);
    }

    /**
     * Create a reader with an explicit in-flight region limit. Every region is still processed; this only limits how
     * many may enter the expensive conversion pipeline at once. Kept package-private for tests and benchmarks.
     */
    BedrockWorldReader(BedrockResolvers resolvers, Converter converter, DB database,
                       Map<RegionCoordPair, Set<ChunkCoordPair>> presentRegions, Dimension dimension,
                       int maxInFlightRegions) {
        if (maxInFlightRegions <= 0) throw new IllegalArgumentException("Maximum in-flight regions must be positive");

        this.database = database;
        this.resolvers = resolvers;
        this.converter = converter;
        this.presentRegions = presentRegions;
        this.dimension = dimension;
        this.maxInFlightRegions = maxInFlightRegions;
    }

    @Override
    public void readWorld(WorldConversionHandler worldConversionHandler) {
        // Use a copy for the present region hashset in the case of something modifying it
        ChunkerWorld chunkerWorld = new ChunkerWorld(
                dimension,
                new ObjectOpenHashSet<>(presentRegions.keySet())
        );

        // Submit world info (done before column reading)
        Task<ColumnConversionHandler> convertWorld = worldConversionHandler.convertWorld(chunkerWorld);

        // Handle the reading of the regions
        Task<Void> regionProcessing = convertWorld.thenConsume("Reading regions", TaskWeight.HIGHER, (columnConversionHandler) -> {
            if (columnConversionHandler == null) return; // This can be null if the columns aren't handled by the reader

            // Read the regions
            ProgressiveTask<Void> readingRegionFiles = Task.async("Reading regions", TaskWeight.HIGHER, () -> readRegions(presentRegions, columnConversionHandler));

            // Call the flush task after all the region files have been read
            readingRegionFiles.then("Flushing columns", TaskWeight.MEDIUM, columnConversionHandler::flushColumns);
        });

        // When the region processing is done flush the world
        regionProcessing.then("Flushing world", TaskWeight.MEDIUM, () -> worldConversionHandler.flushWorld(chunkerWorld));
    }

    /**
     * Read all the regions in the world.
     *
     * @param regions                 the regions to read.
     * @param columnConversionHandler the handler to submit the read columns to.
     */
    public void readRegions(Map<RegionCoordPair, Set<ChunkCoordPair>> regions, ColumnConversionHandler columnConversionHandler) {
        scheduleNextRegionBatch(orderRegions(regions.keySet()).iterator(), regions, columnConversionHandler);
    }

    /**
     * Schedule a small spatially local group of regions. The next group starts only after every column and writer task
     * in the current group completes, providing a hard upper bound on in-flight full-column data.
     */
    protected void scheduleNextRegionBatch(Iterator<RegionCoordPair> orderedRegions,
                                           Map<RegionCoordPair, Set<ChunkCoordPair>> regions,
                                           ColumnConversionHandler columnConversionHandler) {
        List<Task<Void>> batch = new ArrayList<>(maxInFlightRegions);
        while (orderedRegions.hasNext() && batch.size() < maxInFlightRegions) {
            RegionCoordPair region = orderedRegions.next();
            Set<ChunkCoordPair> columns = regions.remove(region);
            if (columns == null || !converter.shouldProcessRegion(dimension, region)) continue;

            batch.add(Task.async("Reading region", TaskWeight.NORMAL,
                            () -> readRegion(region, columns, columnConversionHandler))
                    .then("Region - Flushing", TaskWeight.MEDIUM,
                            () -> columnConversionHandler.flushRegion(region)));
        }

        if (orderedRegions.hasNext()) {
            // Reserve the same reading/flushing weight for every region still waiting to be scheduled.
            int remainingWeight = Math.toIntExact((long) regions.size()
                    * (TaskWeight.NORMAL.getWeight() + TaskWeight.MEDIUM.getWeight()));
            Task.join(batch).then("Scheduling next region batch", new TaskWeight(remainingWeight),
                    () -> scheduleNextRegionBatch(orderedRegions, regions, columnConversionHandler));
        }
    }

    /**
     * Order regions as a breadth-first walk over each connected component. This keeps the boundary between processed
     * and unprocessed regions compact, which limits the number of full columns retained for neighbour pre-transforms.
     */
    protected static List<RegionCoordPair> orderRegions(Set<RegionCoordPair> regions) {
        List<RegionCoordPair> seeds = new ArrayList<>(regions);
        seeds.sort(REGION_ORDER);

        Set<RegionCoordPair> remaining = new ObjectOpenHashSet<>(regions);
        List<RegionCoordPair> ordered = new ArrayList<>(regions.size());

        for (RegionCoordPair seed : seeds) {
            if (!remaining.remove(seed)) continue;
            int next = ordered.size();
            ordered.add(seed);

            while (next < ordered.size()) {
                RegionCoordPair current = ordered.get(next++);

                for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                    for (int offsetX = -1; offsetX <= 1; offsetX++) {
                        if (offsetX == 0 && offsetZ == 0) continue;

                        RegionCoordPair neighbor = new RegionCoordPair(current.regionX() + offsetX, current.regionZ() + offsetZ);
                        if (remaining.remove(neighbor)) ordered.add(neighbor);
                    }
                }
            }
        }

        return ordered;
    }

    /**
     * Read all the columns in a region.
     *
     * @param region                  the region to read with columns.
     * @param columnConversionHandler the handler to submit the columns to.
     */
    public void readRegion(RegionCoordPair region, Set<ChunkCoordPair> columns, ColumnConversionHandler columnConversionHandler) {
        for (ChunkCoordPair chunkCoordPair : columns) {
            if (!converter.shouldProcessColumn(dimension, chunkCoordPair)) continue;
            Task.async("Creating Column Reader", TaskWeight.LOW, () -> createColumnReader(chunkCoordPair))
                    .thenConsume("Reading Column", TaskWeight.HIGHER, (columnReader) -> columnReader.readColumn(columnConversionHandler));
        }
    }

    /**
     * Create the column reader used for reading a column.
     *
     * @param worldChunkCoords the column co-ordinates being read.
     * @return the new column reader.
     */
    public BedrockColumnReader createColumnReader(ChunkCoordPair worldChunkCoords) {
        return new BedrockColumnReader(resolvers, converter, database, dimension, worldChunkCoords);
    }
}
