package com.hivemc.chunker.conversion.handlers.pretransform;

import com.google.common.base.Preconditions;
import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Because of how certain parts of Minecraft work, some chunks may require data from other chunks, for example paintings
 * need to be relocated to the right chunk.
 * Because of this we must ensure that we pre-transform these chunks together then process them, this ensures entities
 * are correctly relocated. This also allows you to do logic that requires neighbouring columns.
 */
public class ColumnPreTransformConversionHandler implements ColumnConversionHandler {
    private final ColumnConversionHandler delegate;
    private final Map<RegionCoordPair, Map<ChunkCoordPair, ColumnData>> pending = new Object2ReferenceOpenHashMap<>();
    private final Set<RegionCoordPair> incompleteRegions = new ObjectOpenHashSet<>();
    private final Set<ChunkCoordPair> processedColumns = new ObjectOpenHashSet<>();
    private final List<ColumnData> cachedPendingSolving = new ArrayList<>();
    private final Stack<ColumnData> cachedSolvingStack = new Stack<>();
    private final Set<ColumnData> cachedChecking = new ObjectOpenHashSet<>();
    private final Set<ColumnData> cachedSolved = new ObjectOpenHashSet<>();

    /**
     * Create a new column pre transform conversion handler.
     *
     * @param delegate     the delegate to call after pre-transformation.
     * @param chunkerWorld the world being used for pre-transformation, used for tracking which regions have been
     *                     processed.
     */
    public ColumnPreTransformConversionHandler(ColumnConversionHandler delegate, ChunkerWorld chunkerWorld) {
        this.delegate = delegate;
        incompleteRegions.addAll(chunkerWorld.getRegions());
    }

    /**
     * Try and solve a column.
     *
     * @param input  the input column to solve.
     * @param output the output to write any solved columns.
     */
    protected void trySolve(ColumnData input, Set<ColumnData> output) {
        // Add the initial entry to our stack
        cachedSolvingStack.push(input);

        // While there are entries to solve loop through the solving stack
        while (!cachedSolvingStack.isEmpty()) {
            ColumnData current = cachedSolvingStack.pop();

            // Attempt to solve the current
            if (!current.getPendingCheckEdges().isEmpty()) {
                // If the pending checks aren't empty, this isn't solvable
                cachedChecking.clear();
                break;
            } else {
                cachedChecking.add(current);

                // Check the required columns (these are bidirectional)
                for (ColumnData value : current.getRequiredColumns().values()) {
                    if (value == null) continue;
                    // Add edges to stack if they're not solved
                    if (cachedChecking.add(value)) {
                        cachedSolvingStack.push(value);
                    }
                }
            }
        }

        // If there are checked entries, it was a success, add them to the output
        if (!cachedChecking.isEmpty()) {
            // Add all the checked entries
            output.addAll(cachedChecking);
            cachedChecking.clear();
        }

        // Reset the state for the next solve
        cachedSolvingStack.clear();
    }

    /**
     * Process any entries in the cachedPendingSolve array to see if they can be solved. If they can be solved they
     * are solved as a cluster.
     */
    protected void processPendingSolve() {
        if (!cachedPendingSolving.isEmpty()) {
            // Try to solve each pending entry
            for (ColumnData pendingSolve : cachedPendingSolving) {
                trySolve(pendingSolve, cachedSolved);
            }
            cachedPendingSolving.clear();

            // Now that we've solved the entries, we can transform them
            if (!cachedSolved.isEmpty()) {
                // Transform columns
                transformCluster(cachedSolved);
                for (ColumnData value : cachedSolved) {
                    RegionCoordPair regionCoordPair = value.getPosition().getRegion();
                    Map<ChunkCoordPair, ColumnData> region = pending.get(regionCoordPair);
                    region.remove(value.getPosition());

                    // If the region is now empty, we can call the parent flush
                    if (region.isEmpty() && !incompleteRegions.contains(regionCoordPair) && pending.remove(regionCoordPair) != null) {
                        delegate.flushRegion(regionCoordPair);
                    }
                }
                cachedSolved.clear();
            }
        }
    }

    /**
     * Check whether an edge can be solved.
     *
     * @param position   the current column position.
     * @param edge       the edge direction.
     * @param columnData the column data if present (can be null if the column is air).
     * @return true if the edge has been checked and should be removed from future checking.
     */
    protected boolean solveEdge(ChunkCoordPair position, Edge edge, @Nullable ColumnData columnData) {
        Edge opposite = edge.getOpposite();
        ChunkCoordPair relativePosition = edge.getRelative(position);
        RegionCoordPair relativePositionRegion = relativePosition.getRegion();
        Map<ChunkCoordPair, ColumnData> region = pending.get(relativePosition.getRegion());
        ColumnData relativeData = null;

        // If the region data was found
        if (region != null) {
            relativeData = region.get(relativePosition);
        }

        // If either of the lookups failed,
        if (relativeData == null) {
            // Check if the region was waiting to be processed (aka do not remove, waiting needed)
            return !incompleteRegions.contains(relativePositionRegion);
        } else {
            // If we have column data, prefill the edges to include ourselves (symmetrically)
            if (columnData != null && columnData.getRequiredColumns().containsKey(edge)) {
                // Fetch the column for ourselves
                relativeData.getRequiredColumns().put(opposite, columnData);
                columnData.getRequiredColumns().put(edge, relativeData);
            }

            // If it was waiting for this value then we should tell it we're here
            if (relativeData.getPendingCheckEdges().remove(opposite)) {
                // If we have column data, add ourselves to the relative if it contains our edge (edges stay as null until resolved)
                if (columnData != null && relativeData.getRequiredColumns().containsKey(opposite)) {
                    relativeData.getRequiredColumns().put(opposite, columnData);
                    columnData.getRequiredColumns().put(edge, relativeData);
                }

                // If the relative has no dependents and was just waiting to check we didn't depend, we can solve it
                if (relativeData.getRequiredColumns().isEmpty() && relativeData.getPendingCheckEdges().isEmpty()) {
                    region.remove(relativePosition);

                    // The chunk is ready for submitting (no transformation needed)
                    relativeData.submit(delegate);

                    // If the region is now empty, we can call the parent flush
                    if (region.isEmpty() && !incompleteRegions.contains(relativePositionRegion) && pending.remove(relativePositionRegion) != null) {
                        delegate.flushRegion(relativePositionRegion);
                    }
                } else if (relativeData.getPendingCheckEdges().isEmpty()) {
                    // There are no pending edge checks so this might be solvable
                    cachedPendingSolving.add(relativeData);
                }
            }
        }

        // Remove this edge as it's been checked
        return true;
    }

    /**
     * Check whether a column can be solved. If it can it is submitted immediately otherwise it is added to the pending.
     *
     * @param columnData the column.
     */
    protected void solveColumn(ColumnData columnData) {
        ChunkCoordPair position = columnData.getPosition();
        synchronized (this) {
            // Ensure duplicates don't happen
            Preconditions.checkArgument(processedColumns.add(position), "Duplicate chunk processed, unable to solve.");

            // First we'll resolve other edges that may have been waiting for this one
            columnData.getPendingCheckEdges().removeIf(edge -> solveEdge(position, edge, columnData));

            // If there are no required edges and nothing left pending checking, we can just be converted
            if (columnData.getRequiredColumns().isEmpty() && columnData.getPendingCheckEdges().isEmpty()) {
                columnData.submit(delegate);
            } else {
                // Push our column
                Map<ChunkCoordPair, ColumnData> region = pending.computeIfAbsent(position.getRegion(), (ignored) -> new Object2ReferenceOpenHashMap<>());
                region.put(position, columnData);

                // If all the edges were present we can check if it's possible to solve a cluster of connected chunks
                if (columnData.getPendingCheckEdges().isEmpty()) {
                    cachedPendingSolving.add(columnData);
                }
            }

            // Try solving any pending solving
            processPendingSolve();
        }
    }

    @Override
    public void convertColumn(ChunkerColumn column) {
        // What edges are required for this column to be pre-transformed
        Set<Edge> outgoingEdges = column.getRequiredPreTransformEdges();

        // We need to check every edge, because if we don't have dependencies & nothing depends on us, we can be converted now!
        EnumSet<Edge> pendingCheckEdges = EnumSet.allOf(Edge.class);

        // Create our map of requiredColumns, the values start as null and then get replaced with a reference
        Map<Edge, ColumnData> requiredColumns = new EnumMap<>(Edge.class);

        // Add the edges as null since they're not resolved yet
        if (outgoingEdges != null) {
            for (Edge edge : outgoingEdges) {
                requiredColumns.put(edge, null);
            }
        }

        // Create columnData
        ColumnData columnData = new ColumnData(
                column.getPosition(),
                column,
                requiredColumns,
                pendingCheckEdges
        );

        // Run our solving algorithm
        solveColumn(columnData);
    }

    @Override
    public void flushRegion(RegionCoordPair regionCoordPair) {
        // When a region is flushed it indicates that any remaining chunks are not present
        // We now should mark that as so
        synchronized (this) {
            // Mark this region as processed
            incompleteRegions.remove(regionCoordPair);

            // First mark any chunks inside the region (excluding the border) as done
            Map<ChunkCoordPair, ColumnData> region = pending.get(regionCoordPair);

            // If there is no pending region, flush the region (this means there were no columns depending on it)
            if (region == null) {
                delegate.flushRegion(regionCoordPair);
                return;
            }

            // Remove any solve-able columns
            region.entrySet().removeIf(entry -> {
                ColumnData columnData = entry.getValue();
                ChunkCoordPair chunkCoordPair = entry.getKey();

                // We just need to determine if all the required edges have been fulfilled
                columnData.getPendingCheckEdges().removeIf(edge -> regionCoordPair.isInside(edge.getRelative(chunkCoordPair)));
                columnData.getRequiredColumns().entrySet().removeIf(edge -> edge.getValue() == null && regionCoordPair.isInside(edge.getKey().getRelative(chunkCoordPair)));

                // Can be removed if there is no further requirement from this column
                if (columnData.getRequiredColumns().isEmpty() && columnData.getPendingCheckEdges().isEmpty()) {
                    // Add to cache for solving
                    cachedSolved.add(columnData);
                    return true;
                }

                // Could not be instantly solved
                return false;
            });

            // Now that we've solved the entries, we can transform them
            if (!cachedSolved.isEmpty()) {
                // Transform columns
                transformCluster(cachedSolved);
                cachedSolved.clear();
            }

            // Mark each corner as empty
            markAsEmpty(regionCoordPair.getChunk(0, 0), EnumSet.of(Edge.NEGATIVE_X, Edge.NEGATIVE_Z));
            markAsEmpty(regionCoordPair.getChunk(31, 0), EnumSet.of(Edge.POSITIVE_X, Edge.NEGATIVE_Z));
            markAsEmpty(regionCoordPair.getChunk(0, 31), EnumSet.of(Edge.NEGATIVE_X, Edge.POSITIVE_Z));
            markAsEmpty(regionCoordPair.getChunk(31, 31), EnumSet.of(Edge.POSITIVE_X, Edge.POSITIVE_Z));

            // Loop through the top and bottom edges, starting at 1 and 31 to avoid corners
            for (int x = 1; x < 31; x++) {
                // Top edge
                markAsEmpty(regionCoordPair.getChunk(x, 0), EnumSet.of(Edge.NEGATIVE_Z));

                // Bottom edge
                markAsEmpty(regionCoordPair.getChunk(x, 31), EnumSet.of(Edge.POSITIVE_Z));
            }

            // Loop through left and right edges, starting at 1 and 31 to avoid corners
            for (int z = 1; z < 31; z++) {
                // Left edge
                markAsEmpty(regionCoordPair.getChunk(0, z), EnumSet.of(Edge.NEGATIVE_X));

                // Right edge
                markAsEmpty(regionCoordPair.getChunk(31, z), EnumSet.of(Edge.POSITIVE_X));
            }

            // If the region is now empty, we can call the parent flush
            if (region.isEmpty() && pending.remove(regionCoordPair) != null) {
                delegate.flushRegion(regionCoordPair);
            }
        }
    }

    /**
     * Mark a chunk as empty and resolve any edges that can be solved.
     *
     * @param chunkCoordPair the chunk co-ordinate to mark as empty.
     * @param checkedEdges   the edges which need checking to see if they're solvable.
     */
    protected void markAsEmpty(ChunkCoordPair chunkCoordPair, EnumSet<Edge> checkedEdges) {
        // Ensure the chunk wasn't already processed
        if (processedColumns.contains(chunkCoordPair)) return;

        // First we'll resolve other edges that may have been waiting for this one
        checkedEdges.forEach(edge -> solveEdge(chunkCoordPair, edge, null));

        // Try solving any pending solving
        processPendingSolve();
    }

    /**
     * Call the pre-transformation handler for a column.
     *
     * @param column          the column being pre-transformed.
     * @param requiredColumns the required columns for the column which should be transformed to the actual columns if
     *                        present.
     */
    protected void handlePreTransform(ChunkerColumn column, Map<Edge, ColumnData> requiredColumns) {
        // Create a new map
        Map<Edge, ChunkerColumn> columns = new Object2ObjectOpenHashMap<>(requiredColumns.size());

        // Remove null values and map them back to column, null values are just unresolved edges
        requiredColumns.forEach(((edge, columnData) -> {
            if (columnData == null) return;
            columns.put(edge, columnData.getColumn());
        }));

        // Call pre-transform as the required edges are here
        column.preTransform(columns);
    }

    /**
     * Call the pre-transform handler on a solved cluster and then submit the columns to the delegate.
     *
     * @param cluster the cluster.
     */
    protected void transformCluster(Collection<ColumnData> cluster) {
        // Transform columns
        for (ColumnData pendingData : cluster) {
            // The chunk is now ready
            handlePreTransform(pendingData.getColumn(), pendingData.getRequiredColumns());
        }

        // Submit columns
        for (ColumnData pendingData : cluster) {
            pendingData.submit(delegate);
        }
    }

    @Override
    public void flushColumns() {
        Task.async("Submitting remaining columns", TaskWeight.NORMAL, () -> {
            // This flush should ideally yield no transforms, but if a region has chunks which were actually empty, and we didn't know, they'll flush here
            synchronized (this) {
                for (Map.Entry<RegionCoordPair, Map<ChunkCoordPair, ColumnData>> region : pending.entrySet()) {
                    // Transform columns
                    transformCluster(region.getValue().values());
                }

                // Clear
                pending.clear();
            }
        }).then("Calling delegate flushColumns", TaskWeight.NORMAL, delegate::flushColumns);
    }

    /**
     * Data class for tracking the status of a column.
     */
    protected static class ColumnData {
        private final ChunkCoordPair position;
        private final ChunkerColumn column;
        private final Map<Edge, ColumnData> requiredColumns;
        private final EnumSet<Edge> pendingCheckEdges;
        private boolean submitted;

        /**
         * Create a new column data.
         *
         * @param position          the position of the column.
         * @param column            the column reference.
         * @param requiredColumns   the columns required (with null column data where they're not loaded).
         * @param pendingCheckEdges the edges of neighbours which should be checked.
         */
        public ColumnData(ChunkCoordPair position, ChunkerColumn column, Map<Edge, ColumnData> requiredColumns, EnumSet<Edge> pendingCheckEdges) {
            this.position = position;
            this.column = column;
            this.requiredColumns = requiredColumns;
            this.pendingCheckEdges = pendingCheckEdges;
        }

        /**
         * Get the position which this column is at.
         *
         * @return the column position.
         */
        public ChunkCoordPair getPosition() {
            return position;
        }

        /**
         * Get the column held by this data.
         *
         * @return the column.
         */
        public ChunkerColumn getColumn() {
            return column;
        }

        /**
         * Get the neighbours which are required for this column.
         *
         * @return a map of the neighbours with null indicating the neighbour hasn't been loaded yet.
         */
        public Map<Edge, ColumnData> getRequiredColumns() {
            return requiredColumns;
        }

        /**
         * Get the edges that are pending checking to ensure they don't need this column.
         *
         * @return the set of edges.
         */
        public EnumSet<Edge> getPendingCheckEdges() {
            return pendingCheckEdges;
        }

        /**
         * Submit this column to the conversion handler.
         *
         * @param delegate the output to submit to.
         */
        public void submit(ColumnConversionHandler delegate) {
            Preconditions.checkArgument(!submitted, "Duplicate submission occurred for column!");

            // Mark as submitted
            submitted = true;
            delegate.convertColumn(column);
        }

        @Override
        public String toString() {
            return "ColumnData{" +
                    "position=" + position +
                    ",column=" + column +
                    ", requiredColumns=" + requiredColumns.keySet() +
                    ", pendingCheckEdges=" + pendingCheckEdges +
                    '}';
        }
    }
}
