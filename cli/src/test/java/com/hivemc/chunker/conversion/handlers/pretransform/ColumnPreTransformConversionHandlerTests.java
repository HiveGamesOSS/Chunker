package com.hivemc.chunker.conversion.handlers.pretransform;

import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.scheduling.task.Environment;
import com.hivemc.chunker.scheduling.task.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ColumnPreTransformConversionHandlerTests {
    @Test
    public void testTargetIsNotSubmittedBeforeNeighbourMutation() {
        RegionCoordPair region = new RegionCoordPair(0, 0);
        RecordingHandler delegate = new RecordingHandler();
        ColumnPreTransformConversionHandler handler = createHandler(delegate, Set.of(region));

        ChunkerColumn source = new ChunkerColumn(new ChunkCoordPair(0, 0));
        ChunkerColumn target = new ChunkerColumn(new ChunkCoordPair(1, 0));
        source.addPreTransformHandler(EnumSet.of(Edge.POSITIVE_X), neighbours -> {
            assertEquals(target, neighbours.get(Edge.POSITIVE_X));
            target.setLightPopulated(true);
        });

        // Load the target first to prove it waits for a neighbour which can still mutate it.
        handler.convertColumn(target);
        handler.convertColumn(source);
        handler.flushRegion(region);

        assertEquals(2, delegate.converted.size());
        assertTrue(delegate.lightPopulatedAtSubmission.get(target.getPosition()));
        assertEquals(List.of(region), delegate.flushedRegions);
    }

    @Test
    public void testTargetWaitsForMultipleMutatingNeighbours() {
        RegionCoordPair region = new RegionCoordPair(0, 0);
        AtomicInteger mutations = new AtomicInteger();
        ChunkerColumn target = new ChunkerColumn(new ChunkCoordPair(10, 10));
        RecordingHandler delegate = new RecordingHandler() {
            @Override
            public synchronized void convertColumn(ChunkerColumn column) {
                if (column == target) assertEquals(2, mutations.get());
                super.convertColumn(column);
            }
        };
        ColumnPreTransformConversionHandler handler = createHandler(delegate, Set.of(region));

        ChunkerColumn west = new ChunkerColumn(new ChunkCoordPair(9, 10));
        ChunkerColumn east = new ChunkerColumn(new ChunkCoordPair(11, 10));
        west.addPreTransformHandler(EnumSet.of(Edge.POSITIVE_X), neighbours -> {
            assertSame(target, neighbours.get(Edge.POSITIVE_X));
            mutations.incrementAndGet();
        });
        east.addPreTransformHandler(EnumSet.of(Edge.NEGATIVE_X), neighbours -> {
            assertSame(target, neighbours.get(Edge.NEGATIVE_X));
            mutations.incrementAndGet();
        });

        handler.convertColumn(target);
        handler.convertColumn(west);
        handler.convertColumn(east);
        handler.flushRegion(region);

        assertEquals(2, mutations.get());
        assertSubmittedOnce(delegate, target, west, east);
    }

    @Test
    public void testCrossRegionNeighboursWorkInEitherFlushOrder() {
        assertCrossRegionNeighbours(false);
        assertCrossRegionNeighbours(true);
    }

    @Test
    public void testMissingCrossRegionNeighbourResolvesAsAbsent() {
        RegionCoordPair leftRegion = new RegionCoordPair(0, 0);
        RegionCoordPair rightRegion = new RegionCoordPair(1, 0);
        RecordingHandler delegate = new RecordingHandler();
        ColumnPreTransformConversionHandler handler = createHandler(delegate, Set.of(leftRegion, rightRegion));
        AtomicInteger transforms = new AtomicInteger();

        ChunkerColumn boundary = new ChunkerColumn(new ChunkCoordPair(31, 0));
        boundary.addPreTransformHandler(EnumSet.of(Edge.POSITIVE_X), neighbours -> {
            assertFalse(neighbours.containsKey(Edge.POSITIVE_X));
            transforms.incrementAndGet();
        });

        handler.convertColumn(boundary);
        handler.flushRegion(leftRegion);
        assertEquals(0, delegate.converted.size());

        // Finishing the adjacent region proves that its boundary position does not exist.
        handler.flushRegion(rightRegion);

        assertEquals(1, transforms.get());
        assertSubmittedOnce(delegate, boundary);
        assertEquals(Set.of(leftRegion, rightRegion), new HashSet<>(delegate.flushedRegions));
    }

    @Test
    public void testTwoWayDependencyTransformsAndSubmitsOnce() {
        RegionCoordPair region = new RegionCoordPair(0, 0);
        RecordingHandler delegate = new RecordingHandler();
        ColumnPreTransformConversionHandler handler = createHandler(delegate, Set.of(region));
        AtomicInteger leftTransforms = new AtomicInteger();
        AtomicInteger rightTransforms = new AtomicInteger();

        ChunkerColumn left = new ChunkerColumn(new ChunkCoordPair(10, 10));
        ChunkerColumn right = new ChunkerColumn(new ChunkCoordPair(11, 10));
        left.addPreTransformHandler(EnumSet.of(Edge.POSITIVE_X), neighbours -> {
            assertSame(right, neighbours.get(Edge.POSITIVE_X));
            leftTransforms.incrementAndGet();
        });
        right.addPreTransformHandler(EnumSet.of(Edge.NEGATIVE_X), neighbours -> {
            assertSame(left, neighbours.get(Edge.NEGATIVE_X));
            rightTransforms.incrementAndGet();
        });

        handler.convertColumn(left);
        handler.convertColumn(right);
        handler.flushRegion(region);

        assertEquals(1, leftTransforms.get());
        assertEquals(1, rightTransforms.get());
        assertSubmittedOnce(delegate, left, right);
    }

    @Test
    public void testColumnArrivalOrderDoesNotChangeResolvedNeighbours() {
        List<List<Integer>> orders = List.of(
                List.of(0, 1, 2),
                List.of(0, 2, 1),
                List.of(1, 0, 2),
                List.of(1, 2, 0),
                List.of(2, 0, 1),
                List.of(2, 1, 0)
        );

        for (List<Integer> order : orders) {
            RegionCoordPair region = new RegionCoordPair(0, 0);
            RecordingHandler delegate = new RecordingHandler();
            ColumnPreTransformConversionHandler handler = createHandler(delegate, Set.of(region));
            ChunkerColumn west = new ChunkerColumn(new ChunkCoordPair(9, 10));
            ChunkerColumn center = new ChunkerColumn(new ChunkCoordPair(10, 10));
            ChunkerColumn east = new ChunkerColumn(new ChunkCoordPair(11, 10));
            west.setLightPopulated(true);
            center.addPreTransformHandler(EnumSet.of(Edge.NEGATIVE_X, Edge.POSITIVE_X), neighbours -> {
                assertSame(west, neighbours.get(Edge.NEGATIVE_X));
                assertSame(east, neighbours.get(Edge.POSITIVE_X));
                center.setLightPopulated(neighbours.get(Edge.NEGATIVE_X).isLightPopulated()
                        && !neighbours.get(Edge.POSITIVE_X).isLightPopulated());
            });

            List<ChunkerColumn> columns = List.of(west, center, east);
            for (int index : order) handler.convertColumn(columns.get(index));
            handler.flushRegion(region);

            assertTrue(center.isLightPopulated(), () -> "Unexpected result for arrival order " + order);
            assertSubmittedOnce(delegate, west, center, east);
        }
    }

    @Test
    public void testLongChainSubmitsColumnsBeforeTheWholeWorldFinishes() {
        int regionCount = 8;
        int columnsPerRegion = 32;
        Set<RegionCoordPair> regions = new HashSet<>();
        for (int regionX = 0; regionX < regionCount; regionX++) {
            regions.add(new RegionCoordPair(regionX, 0));
        }

        RecordingHandler delegate = new RecordingHandler();
        ColumnPreTransformConversionHandler handler = createHandler(delegate, regions);
        List<ChunkerColumn> allColumns = new ArrayList<>(regionCount * columnsPerRegion);

        for (int regionX = 0; regionX < regionCount; regionX++) {
            int submittedBeforeRegion = delegate.converted.size();
            RegionCoordPair region = new RegionCoordPair(regionX, 0);

            for (int localX = 0; localX < columnsPerRegion; localX++) {
                ChunkerColumn column = new ChunkerColumn(new ChunkCoordPair(regionX * columnsPerRegion + localX, 0));
                column.addPreTransformHandler(EnumSet.of(Edge.POSITIVE_X), ignored -> {
                });
                allColumns.add(column);
                handler.convertColumn(column);
            }

            handler.flushRegion(region);
            assertTrue(delegate.converted.size() > submittedBeforeRegion,
                    () -> "Region " + region + " retained every column instead of making progress");
            if (regionX < regionCount - 1) {
                assertTrue(delegate.converted.size() < allColumns.size(),
                        "The unresolved boundary column should remain until the next region arrives");
            }
        }

        assertEquals(regionCount * columnsPerRegion, delegate.converted.size());
        assertEquals(regionCount * columnsPerRegion, delegate.submissionCounts.size());
        assertTrue(delegate.submissionCounts.values().stream().allMatch(count -> count == 1));
        assertEquals(regions, new HashSet<>(delegate.flushedRegions));
    }

    @Test
    public void testRepeatedRegionFlushIsIdempotent() {
        RegionCoordPair region = new RegionCoordPair(0, 0);
        RecordingHandler delegate = new RecordingHandler();
        ColumnPreTransformConversionHandler handler = createHandler(delegate, Set.of(region));
        ChunkerColumn column = new ChunkerColumn(new ChunkCoordPair(0, 0));

        handler.convertColumn(column);
        handler.flushRegion(region);
        handler.flushRegion(region);

        assertSubmittedOnce(delegate, column);
        assertEquals(List.of(region), delegate.flushedRegions);
    }

    @Test
    public void testFinalColumnFlushResolvesEverything() {
        RegionCoordPair region = new RegionCoordPair(0, 0);
        RecordingHandler delegate = new RecordingHandler();
        ColumnPreTransformConversionHandler handler = createHandler(delegate, Set.of(region));
        ChunkerColumn column = new ChunkerColumn(new ChunkCoordPair(0, 0));
        column.addPreTransformHandler(EnumSet.of(Edge.POSITIVE_X), neighbours ->
                assertFalse(neighbours.containsKey(Edge.POSITIVE_X)));
        handler.convertColumn(column);

        AtomicReference<Throwable> taskFailure = new AtomicReference<>();
        Environment environment = Task.environment("Pre-transform final flush test", 2, taskFailure::set, null);
        try {
            handler.flushColumns();
        } finally {
            environment.close();
        }
        environment.future().join();

        assertNull(taskFailure.get());
        assertSubmittedOnce(delegate, column);
        assertEquals(1, delegate.columnFlushes.get());
    }

    @Test
    public void testDuplicateColumnIsRejected() {
        RegionCoordPair region = new RegionCoordPair(0, 0);
        ColumnPreTransformConversionHandler handler = createHandler(new RecordingHandler(), Set.of(region));
        handler.convertColumn(new ChunkerColumn(new ChunkCoordPair(0, 0)));

        assertThrows(IllegalArgumentException.class,
                () -> handler.convertColumn(new ChunkerColumn(new ChunkCoordPair(0, 0))));
    }

    private static void assertCrossRegionNeighbours(boolean flushRightFirst) {
        RegionCoordPair leftRegion = new RegionCoordPair(0, 0);
        RegionCoordPair rightRegion = new RegionCoordPair(1, 0);
        RecordingHandler delegate = new RecordingHandler();
        ColumnPreTransformConversionHandler handler = createHandler(delegate, Set.of(leftRegion, rightRegion));
        AtomicReference<ChunkerColumn> resolvedNeighbour = new AtomicReference<>();

        ChunkerColumn left = new ChunkerColumn(new ChunkCoordPair(31, 0));
        ChunkerColumn right = new ChunkerColumn(new ChunkCoordPair(32, 0));
        left.addPreTransformHandler(EnumSet.of(Edge.POSITIVE_X),
                neighbours -> resolvedNeighbour.set(neighbours.get(Edge.POSITIVE_X)));

        handler.convertColumn(left);
        handler.convertColumn(right);
        handler.flushRegion(flushRightFirst ? rightRegion : leftRegion);
        if (delegate.submissionCounts.containsKey(left.getPosition())) {
            assertSame(right, resolvedNeighbour.get(), "The dependent column was submitted before resolving its neighbour");
        }
        handler.flushRegion(flushRightFirst ? leftRegion : rightRegion);

        assertSame(right, resolvedNeighbour.get());
        assertSubmittedOnce(delegate, left, right);
        assertEquals(Set.of(leftRegion, rightRegion), new HashSet<>(delegate.flushedRegions));
        assertEquals(2, delegate.flushedRegions.size());
    }

    private static ColumnPreTransformConversionHandler createHandler(RecordingHandler delegate, Set<RegionCoordPair> regions) {
        return new ColumnPreTransformConversionHandler(delegate, new ChunkerWorld(Dimension.OVERWORLD, regions));
    }

    private static void assertSubmittedOnce(RecordingHandler delegate, ChunkerColumn... columns) {
        for (ChunkerColumn column : columns) {
            assertEquals(1, delegate.submissionCounts.getOrDefault(column.getPosition(), 0),
                    () -> "Unexpected submission count for " + column.getPosition());
        }
    }

    private static class RecordingHandler implements ColumnConversionHandler {
        private final List<ChunkerColumn> converted = Collections.synchronizedList(new ArrayList<>());
        private final Map<ChunkCoordPair, Boolean> lightPopulatedAtSubmission = new ConcurrentHashMap<>();
        private final Map<ChunkCoordPair, Integer> submissionCounts = new ConcurrentHashMap<>();
        private final List<RegionCoordPair> flushedRegions = Collections.synchronizedList(new ArrayList<>());
        private final AtomicInteger columnFlushes = new AtomicInteger();

        @Override
        public synchronized void convertColumn(ChunkerColumn column) {
            converted.add(column);
            lightPopulatedAtSubmission.put(column.getPosition(), column.isLightPopulated());
            submissionCounts.merge(column.getPosition(), 1, Integer::sum);
        }

        @Override
        public void flushRegion(RegionCoordPair regionCoordPair) {
            flushedRegions.add(regionCoordPair);
        }

        @Override
        public void flushColumns() {
            columnFlushes.incrementAndGet();
        }
    }
}
