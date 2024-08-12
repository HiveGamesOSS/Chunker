package com.hivemc.chunker.conversion.encoding.base.resolver.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import org.jetbrains.annotations.NotNull;

/**
 * A class to indicate a block-entity type handler is updated before block entities are written.
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <T> the type of the block entity.
 */
public interface UpdateBeforeWriteBlockEntityHandler<R, T extends BlockEntity> {
    /**
     * Update a block entity before writing.
     *
     * @param resolvers   the resolvers which can be used by this type.
     * @param column      the column which the block entity is inside.
     * @param x           the global X co-ordinate.
     * @param y           the global Y co-ordinate.
     * @param z           the global Z co-ordinate.
     * @param blockEntity the block entity being updated.
     * @return the updated copy of the block entity.
     */
    T updateBeforeWrite(@NotNull R resolvers, ChunkerColumn column, int x, int y, int z, T blockEntity);

    /**
     * Get an additional type that is handled by this block entity. This is registered to the type handlers so that
     * format specific block entities are still handled as every block entity needs a final type handler.
     *
     * @return a block entity type.
     */
    Class<? extends T> getAdditionalHandledClass();
}
