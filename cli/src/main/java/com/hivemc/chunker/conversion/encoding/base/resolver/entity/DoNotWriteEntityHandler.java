package com.hivemc.chunker.conversion.encoding.base.resolver.entity;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;

/**
 * An interface to indicate the EntityHandler should remove the entity type before block entities are written.
 * This is useful for entities that are only present in the Chunker format and are not written into files.
 *
 * @param <T> the type of the entity.
 */
public interface DoNotWriteEntityHandler<T extends Entity> {
    /**
     * Check whether the entity should be removed.
     *
     * @param column the column containing the entity.
     * @param entity the entity.
     * @return true if the entity should be removed.
     */
    default boolean shouldRemoveBeforeWrite(ChunkerColumn column, T entity) {
        return true;
    }
}
