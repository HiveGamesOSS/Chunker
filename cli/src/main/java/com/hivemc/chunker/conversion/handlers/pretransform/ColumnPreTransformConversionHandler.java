package com.hivemc.chunker.conversion.handlers.pretransform;

import com.google.common.base.Preconditions;
import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Because of how certain parts of Minecraft work, some chunks may require data from other chunks, for example paintings
 * need to be relocated to the right chunk.
 * Because of this we must ensure that we pre-transform these chunks together then process them, this ensures entities
 * are correctly relocated. This also allows you to do logic that requires neighbouring columns.
 *
 * <p>Columns are transformed independently once all four neighbouring positions are known. A transformed column is
 * held only until every adjacent column that can mutate it has also transformed. This local lifecycle prevents a chain
 * of connectable blocks from retaining an entire world's columns as one dependency cluster.</p>
 */
public class ColumnPreTransformConversionHandler implements ColumnConversionHandler {
    private final ColumnConversionHandler delegate;
    private final Map<RegionCoordPair, Map<ChunkCoordPair, ColumnData>> pending = new Object2ReferenceOpenHashMap<>();
    private final Set<RegionCoordPair> incompleteRegions = new ObjectOpenHashSet<>();
    private final Set<RegionCoordPair> flushedRegions = new ObjectOpenHashSet<>();
    private final LongSet processedColumns = new LongOpenHashSet();
    private final ArrayDeque<ColumnData> readyToTransform = new ArrayDeque<>();

    /**
     * Create a new column pre-transform conversion handler.
     *
     * @param delegate     the delegate to call after pre-transformation.
     * @param chunkerWorld the world being used for tracking regions which have not completed reading.
     */
    public ColumnPreTransformConversionHandler(ColumnConversionHandler delegate, ChunkerWorld chunkerWorld) {
        this.delegate = delegate;
        incompleteRegions.addAll(chunkerWorld.getRegions());
    }

    @Override
    public void convertColumn(ChunkerColumn column) {
        synchronized (this) {
            ChunkCoordPair position = column.getPosition();
            Preconditions.checkArgument(processedColumns.add(position.toLong()), "Duplicate chunk processed, unable to solve.");

            ColumnData columnData = new ColumnData(column);
            pending.computeIfAbsent(position.getRegion(), ignored -> new Object2ReferenceOpenHashMap<>())
                    .put(position, columnData);

            for (Edge edge : Edge.ALL_EDGES) {
                ChunkCoordPair relativePosition = edge.getRelative(position);
                ColumnData neighbour = getPending(relativePosition);
                if (neighbour != null) {
                    link(columnData, edge, neighbour);
                } else if (!incompleteRegions.contains(relativePosition.getRegion())) {
                    columnData.resolve(edge);
                }
            }

            enqueueIfReady(columnData);
            processReady();
        }
    }

    private ColumnData getPending(ChunkCoordPair position) {
        Map<ChunkCoordPair, ColumnData> region = pending.get(position.getRegion());
        return region == null ? null : region.get(position);
    }

    private void link(ColumnData column, Edge edge, ColumnData neighbour) {
        column.link(edge, neighbour);
        neighbour.link(edge.getOpposite(), column);
        enqueueIfReady(neighbour);
    }

    private void enqueueIfReady(ColumnData columnData) {
        if (!columnData.transformed && columnData.pendingCheckEdges.isEmpty() && !columnData.queued) {
            columnData.queued = true;
            readyToTransform.addLast(columnData);
        }
    }

    private void processReady() {
        while (!readyToTransform.isEmpty()) {
            ColumnData current = readyToTransform.removeFirst();
            current.queued = false;
            if (current.transformed || !current.pendingCheckEdges.isEmpty()) continue;

            current.transform();

            List<ColumnData> neighbours = new ArrayList<>(current.neighbours.values());
            trySubmit(current);
            for (ColumnData neighbour : neighbours) {
                trySubmit(neighbour);
            }
        }
    }

    /**
     * Submit a transformed column once no adjacent, untransformed column can still mutate it.
     */
    private void trySubmit(ColumnData columnData) {
        if (!columnData.transformed || columnData.submitted) return;

        for (Map.Entry<Edge, ColumnData> entry : columnData.neighbours.entrySet()) {
            ColumnData neighbour = entry.getValue();
            if (neighbour.requiredEdges.contains(entry.getKey().getOpposite()) && !neighbour.transformed) {
                return;
            }
        }

        columnData.submitted = true;
        delegate.convertColumn(columnData.column);

        RegionCoordPair regionPosition = columnData.position.getRegion();
        Map<ChunkCoordPair, ColumnData> region = pending.get(regionPosition);
        if (region != null) {
            region.remove(columnData.position);
            if (region.isEmpty()) {
                pending.remove(regionPosition);
                flushRegionIfComplete(regionPosition);
            }
        }

        // Remaining neighbours no longer need the full submitted column. Any neighbour which depended on it has
        // already transformed, while a neighbour it depended on cannot mutate it.
        for (Map.Entry<Edge, ColumnData> entry : new ArrayList<>(columnData.neighbours.entrySet())) {
            entry.getValue().neighbours.remove(entry.getKey().getOpposite());
        }
        columnData.neighbours.clear();
    }

    @Override
    public void flushRegion(RegionCoordPair regionPosition) {
        synchronized (this) {
            incompleteRegions.remove(regionPosition);

            // Resolve positions missing from the region itself.
            Map<ChunkCoordPair, ColumnData> region = pending.get(regionPosition);
            if (region != null) {
                for (ColumnData columnData : new ArrayList<>(region.values())) {
                    columnData.pendingCheckEdges.removeIf(edge -> edge.getRelative(columnData.position).getRegion().equals(regionPosition));
                    enqueueIfReady(columnData);
                }
            }

            // Resolve columns in adjacent regions which were waiting to learn that a boundary position is absent.
            for (Edge boundary : Edge.ALL_EDGES) {
                RegionCoordPair adjacentRegion = new RegionCoordPair(
                        regionPosition.regionX() + boundary.getX(),
                        regionPosition.regionZ() + boundary.getZ()
                );
                Map<ChunkCoordPair, ColumnData> adjacent = pending.get(adjacentRegion);
                if (adjacent == null) continue;

                for (ColumnData columnData : new ArrayList<>(adjacent.values())) {
                    columnData.pendingCheckEdges.removeIf(edge -> edge.getRelative(columnData.position).getRegion().equals(regionPosition));
                    enqueueIfReady(columnData);
                }
            }

            processReady();
            flushRegionIfComplete(regionPosition);
        }
    }

    private void flushRegionIfComplete(RegionCoordPair regionPosition) {
        if (!incompleteRegions.contains(regionPosition)
                && !pending.containsKey(regionPosition)
                && flushedRegions.add(regionPosition)) {
            delegate.flushRegion(regionPosition);
        }
    }

    @Override
    public void flushColumns() {
        Task.async("Submitting remaining columns", TaskWeight.NORMAL, () -> {
            synchronized (this) {
                incompleteRegions.clear();
                for (Map<ChunkCoordPair, ColumnData> region : pending.values()) {
                    for (ColumnData columnData : region.values()) {
                        columnData.pendingCheckEdges.clear();
                        enqueueIfReady(columnData);
                    }
                }
                processReady();

                Preconditions.checkState(pending.isEmpty(), "Unable to resolve all pending columns before world flush.");
            }
        }).then("Calling delegate flushColumns", TaskWeight.NORMAL, delegate::flushColumns);
    }

    private static final class ColumnData {
        private final ChunkCoordPair position;
        private final ChunkerColumn column;
        private final EnumSet<Edge> requiredEdges;
        private final EnumSet<Edge> pendingCheckEdges = EnumSet.allOf(Edge.class);
        private final Map<Edge, ColumnData> neighbours = new EnumMap<>(Edge.class);
        private boolean queued;
        private boolean transformed;
        private boolean submitted;

        private ColumnData(ChunkerColumn column) {
            this.position = column.getPosition();
            this.column = column;
            requiredEdges = column.getRequiredPreTransformEdges().isEmpty()
                    ? EnumSet.noneOf(Edge.class)
                    : EnumSet.copyOf(column.getRequiredPreTransformEdges());
        }

        private void link(Edge edge, ColumnData neighbour) {
            neighbours.put(edge, neighbour);
            resolve(edge);
        }

        private void resolve(Edge edge) {
            pendingCheckEdges.remove(edge);
        }

        private void transform() {
            Map<Edge, ChunkerColumn> requiredNeighbours = new EnumMap<>(Edge.class);
            for (Edge edge : requiredEdges) {
                ColumnData neighbour = neighbours.get(edge);
                if (neighbour != null) requiredNeighbours.put(edge, neighbour.column);
            }
            column.preTransform(requiredNeighbours);
            transformed = true;
        }
    }
}
