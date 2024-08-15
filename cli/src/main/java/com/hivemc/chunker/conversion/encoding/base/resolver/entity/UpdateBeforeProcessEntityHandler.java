package com.hivemc.chunker.conversion.encoding.base.resolver.entity;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * A class to indicate an entity type handler is updated before the column is submitted for processing.
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <T> the type of the entity.
 */
public interface UpdateBeforeProcessEntityHandler<R, T extends Entity> {
    /**
     * Update an entity before processing.
     *
     * @param resolvers the resolvers which can be used by this type.
     * @param column    the column which the block entity is inside.
     * @param entity    the entity being updated.
     * @return the updated copy of the entity.
     */
    T updateBeforeProcess(@NotNull R resolvers, ChunkerColumn column, T entity);
}
