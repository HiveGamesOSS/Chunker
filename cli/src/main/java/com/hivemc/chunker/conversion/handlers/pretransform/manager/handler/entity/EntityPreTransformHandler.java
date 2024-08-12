package com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.entity;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;

import java.util.Map;
import java.util.Set;

/**
 * A pre-transformation handler that is applied to a specific entity type.
 *
 * @param <T> the entity type handled by this handler.
 */
public interface EntityPreTransformHandler<T extends Entity> {
    /**
     * The type which this pre-transform handler is applied to.
     *
     * @return the type which the entity must be an instanceof.
     */
    Class<T> getHandledType();

    /**
     * Get the edges which are required to run the pre-transform handler.
     *
     * @param column the column being pre-transformed.
     * @param entity the entity being pre-transformed.
     * @return a set of the edges, if it is empty the handler is run immediately otherwise it is queued for when the
     * neighbours are loaded.
     */
    Set<Edge> getRequiredEdges(ChunkerColumn column, T entity);

    /**
     * Handle the pre-transformation for the entity.
     *
     * @param column     the column being pre-transformed.
     * @param neighbours the neighbouring chunks, note: not all required columns may be present if they do not exist or
     *                   if this was called immediately and preTransform isn't enabled.
     * @param entity     the entity being pre-transformed.
     * @return whether the entity should be removed, true if so.
     */
    boolean handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, T entity);
}
