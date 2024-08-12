package com.hivemc.chunker.conversion.encoding.base.resolver.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import org.jetbrains.annotations.NotNull;

/**
 * A class to indicate a block-entity type handler is updated before the column is submitted for processing.
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <T> the type of the block entity.
 */
public interface UpdateBeforeProcessBlockEntityHandler<R, T extends BlockEntity> {
    /**
     * Update a block entity before processing.
     *
     * @param resolvers   the resolvers which can be used by this type.
     * @param column      the column which the block entity is inside.
     * @param x           the global X co-ordinate.
     * @param y           the global Y co-ordinate.
     * @param z           the global Z co-ordinate.
     * @param blockEntity the block entity being updated.
     * @return the updated copy of the block entity.
     */
    T updateBeforeProcess(@NotNull R resolvers, ChunkerColumn column, int x, int y, int z, T blockEntity);
}
