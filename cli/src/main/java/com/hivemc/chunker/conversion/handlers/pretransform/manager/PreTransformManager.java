package com.hivemc.chunker.conversion.handlers.pretransform.manager;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.entity.EntityPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.*;

/**
 * PreTransformManager handles the processing of pre-transformations for a reader/writer. Pre-Transformations are column
 * transformation rules which are executed with neighbouring columns (if required). The most common use-case is gathering
 * data from connecting blocks which may be in other chunks (e.g. connecting fences if the data is missing).
 */
public abstract class PreTransformManager {
    protected final Map<ChunkerBlockType, BlockPreTransformHandler> blockTypeToHandler = new Object2ObjectOpenHashMap<>();
    protected final Map<Class<? extends Entity>, EntityPreTransformHandler<?>> entityTypeToHandler = new Object2ObjectOpenHashMap<>();

    /**
     * Create a new pre-transform manager.
     *
     * @param version the version being used for the manager.
     */
    public PreTransformManager(Version version) {
        // Call register handlers
        registerHandlers(version);
    }

    /**
     * Called on construction to register all the pre-transform handlers.
     *
     * @param version the version being used for this manager.
     */
    protected abstract void registerHandlers(Version version);

    /**
     * Register a block pre-transform handler.
     *
     * @param handler    the handler which should be called.
     * @param blockTypes the block types which the handler should be called for.
     */
    protected void registerHandler(BlockPreTransformHandler handler, Iterable<ChunkerBlockType> blockTypes) {
        for (ChunkerBlockType blockType : blockTypes) {
            blockTypeToHandler.put(blockType, handler);
        }
    }

    /**
     * Register a block pre-transform handler.
     *
     * @param handler    the handler which should be called.
     * @param blockType  the block type which the handler should be called for.
     * @param blockTypes the additional block types which the handler should be called for.
     */
    protected void registerHandler(BlockPreTransformHandler handler, ChunkerBlockType blockType, ChunkerBlockType... blockTypes) {
        blockTypeToHandler.put(blockType, handler);
        for (ChunkerBlockType blockType2 : blockTypes) {
            blockTypeToHandler.put(blockType2, handler);
        }
    }

    /**
     * Register a entity pre-transform handler.
     *
     * @param handler    the handler which should be called.
     * @param entityType the entity class which should be handled.
     * @param <T>        the type of the entity.
     */
    protected <T extends Entity> void registerHandler(EntityPreTransformHandler<T> handler, Class<T> entityType) {
        entityTypeToHandler.put(entityType, handler);
    }

    /**
     * Check whether a column contains a connectable block. By default, this checks for a connectable block type.
     *
     * @param column the column being checked.
     * @param chunk  the chunk being checked.
     * @return true if there is any block needing connection.
     */
    protected boolean containsConnectableBlock(ChunkerColumn column, ChunkerChunk chunk) {
        return chunk.getPalette().containsKey(identifier -> blockTypeToHandler.containsKey(identifier.getType()));
    }

    /**
     * Solve a column and queue any missing pre-transforms for later.
     *
     * @param column              the column.
     * @param preTransformAllowed whether queueing for later is allowed, if false, all the transformations happen with
     *                            limited data.
     */
    public void solve(ChunkerColumn column, boolean preTransformAllowed) {
        List<PendingPreTransform> pending = new ArrayList<>();
        Set<Edge> edges = EnumSet.noneOf(Edge.class);

        // Loop through and find any matching entities
        column.getEntities().removeIf(entity -> {
            for (Map.Entry<Class<? extends Entity>, EntityPreTransformHandler<?>> handlerEntry : entityTypeToHandler.entrySet()) {
                if (!handlerEntry.getKey().isInstance(entity)) continue; // Skip if the type doesn't match

                // Remove it now (if it was true) and don't process with other handlers
                if (solveEntity(column, preTransformAllowed, pending, edges, handlerEntry.getValue(), entity)) {
                    return true;
                }
            }

            // Don't remove for now
            return false;
        });

        // Loop through every chunk in the column
        for (ChunkerChunk chunk : column.getChunks().values()) {
            // Check if the palette contains a connectable value
            if (!containsConnectableBlock(column, chunk)) continue;

            // Loop through each block to process connections
            for (int localY = 0; localY < 16; localY++) {
                for (int localX = 0; localX < 16; localX++) {
                    for (int localZ = 0; localZ < 16; localZ++) {
                        // Fetch the identifier
                        ChunkerBlockIdentifier blockIdentifier = chunk.getPalette().get(localX, localY, localZ, ChunkerBlockIdentifier.AIR);

                        // Check if it's connectable by typeToHandler
                        BlockPreTransformHandler handler = blockTypeToHandler.get(blockIdentifier.getType());
                        if (handler == null) continue; // Skip if no handler

                        int x = column.getPosition().chunkX() << 4 | localX;
                        int y = chunk.getY() << 4 | localY;
                        int z = column.getPosition().chunkZ() << 4 | localZ;

                        // If pre-transform isn't allowed, we have to solve it now
                        boolean solveNow = !preTransformAllowed;
                        if (preTransformAllowed) {
                            // Calculate whether it can be solved now
                            Set<Edge> requiredEdges = handler.getRequiredEdges(column, x, y, z, blockIdentifier);

                            // If no edges are needed it can be solved now
                            solveNow = requiredEdges.isEmpty();

                            // Add all the required edges
                            if (!requiredEdges.isEmpty()) {
                                edges.addAll(requiredEdges);
                            }
                        }

                        // Solve it now if it's marked as so
                        if (solveNow) {
                            // Solve with empty edges
                            ChunkerBlockIdentifier newBlockIdentifier = handler.handle(column, Collections.emptyMap(), x, y, z, blockIdentifier);

                            // Update block if it changed
                            if (!newBlockIdentifier.equals(blockIdentifier)) {
                                column.setBlock(
                                        x,
                                        y,
                                        z,
                                        newBlockIdentifier
                                );
                            }
                        } else {
                            // Add it to the pending otherwise
                            pending.add(new PendingPreTransform.PendingBlockPreTransform(x, y, z, handler));
                        }
                    }
                }
            }

        }

        if (!pending.isEmpty()) {
            // Set up the handler which should be called when the edges are ready
            column.addPreTransformHandler(edges, (neighbours) -> solveEdges(column, neighbours, pending));
        }
    }

    /**
     * Called when an entity needs solving, this method queues the solve if it requires edges or solves it now.
     *
     * @param column              the column being pre-transformed.
     * @param preTransformAllowed whether pre-transform can be queued, if false the transformation happens immediately.
     * @param pending             the output list which the transformation should be added to if it's queued.
     * @param edges               the required edges for this entity to be solved.
     * @param handler             the handler to call for pre-transformation.
     * @param entity              the entity being pre-transformed.
     * @param <T>                 the type of the entity.
     * @return true if the entity should be removed.
     */
    protected <T extends Entity> boolean solveEntity(ChunkerColumn column, boolean preTransformAllowed, List<PendingPreTransform> pending, Set<Edge> edges, EntityPreTransformHandler<T> handler, Entity entity) {
        if (!handler.getHandledType().isInstance(entity)) return false;
        T typedEntity = handler.getHandledType().cast(entity);

        // If pre-transform isn't allowed, we have to solve it now
        boolean solveNow = !preTransformAllowed;
        if (preTransformAllowed) {
            // Calculate whether it can be solved now
            Set<Edge> requiredEdges = handler.getRequiredEdges(column, typedEntity);

            // If no edges are needed it can be solved now
            solveNow = requiredEdges.isEmpty();

            // Add all the required edges
            if (!requiredEdges.isEmpty()) {
                edges.addAll(requiredEdges);
            }
        }

        // Solve it now if it's marked as so
        if (solveNow) {
            // Solve with empty edges
            return handler.handle(column, Collections.emptyMap(), typedEntity);
        } else {
            // Add it to the pending otherwise
            pending.add(new PendingPreTransform.PendingEntityPreTransform<>(typedEntity, handler));

            // Don't remove
            return false;
        }
    }

    /**
     * Called when an entity is solved with neighbours.
     *
     * @param column              the column being pre-transformed.
     * @param neighbours          the neighbours which have been loaded (some edges may be missing if they are not found).
     * @param pendingPreTransform the pre-transformation to rule to run for the entity.
     * @param <T>                 the type of the entity.
     * @return true if the entity should be removed.
     */
    protected <T extends Entity> boolean solveEntity(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, PendingPreTransform.PendingEntityPreTransform<T> pendingPreTransform) {
        return pendingPreTransform.preTransformHandler().handle(column, neighbours, pendingPreTransform.entity());
    }

    /**
     * Called when the neighbouring columns have loaded and the pre-transformations need to be handled.
     *
     * @param column               the column being pre-transformed.
     * @param neighbours           the neighbours which have been loaded (some edges may be missing if they are not found).
     * @param pendingPreTransforms the list of transformations to run for the column.
     */
    protected void solveEdges(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, List<PendingPreTransform> pendingPreTransforms) {
        for (PendingPreTransform pendingPreTransform : pendingPreTransforms) {
            if (pendingPreTransform instanceof PendingPreTransform.PendingBlockPreTransform pendingBlockPreTransform) {
                // Fetch the block in the chunk
                ChunkerBlockIdentifier blockIdentifier = column.getBlock(
                        pendingBlockPreTransform.x(),
                        pendingBlockPreTransform.y(),
                        pendingBlockPreTransform.z()
                );

                // Run the pre-transform handler
                ChunkerBlockIdentifier newBlockIdentifier = pendingBlockPreTransform.preTransformHandler().handle(
                        column,
                        neighbours,
                        pendingBlockPreTransform.x(),
                        pendingBlockPreTransform.y(),
                        pendingBlockPreTransform.z(),
                        blockIdentifier
                );

                // Update block if it changed
                if (!newBlockIdentifier.equals(blockIdentifier)) {
                    column.setBlock(
                            pendingBlockPreTransform.x(),
                            pendingBlockPreTransform.y(),
                            pendingBlockPreTransform.z(),
                            newBlockIdentifier
                    );
                }
            } else if (pendingPreTransform instanceof PendingPreTransform.PendingEntityPreTransform<?> pendingEntityPreTransform) {
                // Skip if the entity is no longer part of the column
                if (!column.getEntities().contains(pendingEntityPreTransform.entity())) continue;

                // Call the handler and remove the entity if it returned true
                if (solveEntity(column, neighbours, pendingEntityPreTransform)) {
                    column.getEntities().remove(pendingEntityPreTransform.entity());
                }
            }
        }

        // Clear the pending transforms
        pendingPreTransforms.clear();
    }
}
