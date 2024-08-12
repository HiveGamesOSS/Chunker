package com.hivemc.chunker.conversion.encoding.base.resolver.entity;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * A class to indicate an entity type handler is updated before entities are written.
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <T> the type of the entity.
 */
public interface UpdateBeforeWriteEntityHandler<R, T extends Entity> {
    /**
     * Update an entity before writing.
     *
     * @param resolvers the resolvers which can be used by this type.
     * @param column    the column which the block entity is inside.
     * @param entity    the entity being updated.
     * @return the updated copy of the entity.
     */
    T updateBeforeWrite(@NotNull R resolvers, ChunkerColumn column, T entity);

    /**
     * Get an additional type that is handled by this entity. This is registered to the type handlers so that format
     * specific entities are still handled as every entity needs a final type handler.
     *
     * @return an entity type.
     */
    Class<? extends T> getAdditionalHandledClass();
}
