package com.hivemc.chunker.conversion.encoding.bedrock.base.reader;

import com.hivemc.chunker.conversion.bedrock.resolver.MockConverter;
import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.scheduling.task.Environment;
import com.hivemc.chunker.scheduling.task.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BedrockWorldReaderTests {
    @Test
    public void testRegionOrderWalksConnectedRegionsBeforeDistantComponents() {
        Set<RegionCoordPair> regions = new HashSet<>();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                regions.add(new RegionCoordPair(x, z));
            }
        }
        RegionCoordPair distant = new RegionCoordPair(100, 100);
        regions.add(distant);

        List<RegionCoordPair> ordered = BedrockWorldReader.orderRegions(regions);

        assertEquals(regions.size(), ordered.size());
        assertEquals(regions, new HashSet<>(ordered));
        assertEquals(new RegionCoordPair(-2, -2), ordered.get(0));
        assertEquals(distant, ordered.get(ordered.size() - 1));

        Set<RegionCoordPair> visited = new HashSet<>();
        visited.add(ordered.get(0));
        for (RegionCoordPair current : ordered.subList(1, ordered.size() - 1)) {
            assertTrue(hasNeighbor(current, visited), () -> current + " is disconnected from the preceding walk");
            visited.add(current);
        }
    }

    @Test
    public void testRegionBatchLimitDelaysTheNextBatch() throws Exception {
        Map<RegionCoordPair, Set<ChunkCoordPair>> regions = new LinkedHashMap<>();
        for (int regionX = 0; regionX < 5; regionX++) {
            RegionCoordPair region = new RegionCoordPair(regionX, 0);
            regions.put(region, Set.of(region.getChunk(0, 0)));
        }

        int batchSize = 2;
        BlockingWorldReader reader = new BlockingWorldReader(regions, batchSize);
        RecordingHandler handler = new RecordingHandler();
        AtomicReference<Throwable> taskFailure = new AtomicReference<>();
        Environment environment = Task.environment("Region batch test", 5, taskFailure::set, null);

        try {
            reader.readRegions(regions, handler);

            assertTrue(reader.firstBatchStarted.await(10, TimeUnit.SECONDS), "The first region batch did not start");
            assertEquals(batchSize, reader.startedRegions.size());
            assertEquals(batchSize, reader.maximumActiveRegions.get());
        } finally {
            reader.releaseFirstBatch.countDown();
            environment.close();
        }

        environment.future().join();

        assertNull(taskFailure.get());
        assertEquals(5, reader.startedRegions.size());
        assertTrue(reader.maximumActiveRegions.get() <= batchSize);
        assertEquals(5, handler.flushedRegions.size());
    }

    @Test
    public void testProgressIncludesRegionsWaitingForLaterBatches() throws Exception {
        Map<RegionCoordPair, Set<ChunkCoordPair>> regions = new LinkedHashMap<>();
        for (int x = 0; x < 6; x++) {
            RegionCoordPair region = new RegionCoordPair(x, 0);
            regions.put(region, Set.of(region.getChunk(0, 0)));
        }
        CountDownLatch secondBatchStarted = new CountDownLatch(2);
        CountDownLatch release = new CountDownLatch(1);
        BedrockWorldReader reader = new BedrockWorldReader(null, new MockConverter(null), null,
                regions, Dimension.OVERWORLD, 2) {
            @Override
            public void readRegion(RegionCoordPair region, Set<ChunkCoordPair> columns, ColumnConversionHandler handler) {
                if (region.regionX() == 2 || region.regionX() == 3) {
                    secondBatchStarted.countDown();
                    try {
                        if (!release.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Timed out");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(e);
                    }
                }
            }
        };
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Environment environment = Task.environment("Batch progress", 5, failure::set, null);
        try {
            reader.readRegions(regions, new RecordingHandler());
            assertTrue(secondBatchStarted.await(10, TimeUnit.SECONDS));
            // Two of six regions are complete; later batches must not disappear from progress.
            assertTrue(environment.getProgress() > 0.30 && environment.getProgress() < 0.40,
                    environment::getDetailedProgress);
        } finally {
            release.countDown();
            environment.close();
        }
        environment.future().join();
        assertNull(failure.get());
        assertEquals(1D, environment.getProgress());
    }

    private static boolean hasNeighbor(RegionCoordPair current, Set<RegionCoordPair> visited) {
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                if (offsetX == 0 && offsetZ == 0) continue;
                if (visited.contains(new RegionCoordPair(current.regionX() + offsetX, current.regionZ() + offsetZ))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final class BlockingWorldReader extends BedrockWorldReader {
        private final AtomicInteger activeRegions = new AtomicInteger();
        private final AtomicInteger maximumActiveRegions = new AtomicInteger();
        private final List<RegionCoordPair> startedRegions = Collections.synchronizedList(new ArrayList<>());
        private final CountDownLatch firstBatchStarted;
        private final CountDownLatch releaseFirstBatch = new CountDownLatch(1);

        private BlockingWorldReader(Map<RegionCoordPair, Set<ChunkCoordPair>> presentRegions, int batchSize) {
            super(null, new MockConverter(null), null, presentRegions, Dimension.OVERWORLD, batchSize);
            firstBatchStarted = new CountDownLatch(batchSize);
        }

        @Override
        public void readRegion(RegionCoordPair region, Set<ChunkCoordPair> columns, ColumnConversionHandler handler) {
            int active = activeRegions.incrementAndGet();
            maximumActiveRegions.accumulateAndGet(active, Math::max);
            startedRegions.add(region);

            try {
                if (firstBatchStarted.getCount() > 0) {
                    firstBatchStarted.countDown();
                    if (!releaseFirstBatch.await(10, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Timed out waiting to release the first batch");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Region batch test was interrupted", e);
            } finally {
                activeRegions.decrementAndGet();
            }
        }
    }

    private static final class RecordingHandler implements ColumnConversionHandler {
        private final List<RegionCoordPair> flushedRegions = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void convertColumn(ChunkerColumn column) {
        }

        @Override
        public void flushRegion(RegionCoordPair regionCoordPair) {
            flushedRegions.add(regionCoordPair);
        }

        @Override
        public void flushColumns() {
        }
    }
}
