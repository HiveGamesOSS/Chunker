package com.hivemc.chunker.conversion.encoding.base.resolver.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;

/**
 * An interface to indicate the BlockEntityHandler should remove the block entity type before the chunk is submitted.
 * This is useful for block entities that are only present in the input format and are not Chunker entities.
 *
 * @param <T> the type of the block entity.
 */
public interface DoNotProcessBlockEntityHandler<T extends BlockEntity> {
    /**
     * Check whether the block entity should be removed.
     *
     * @param column      the column the block entity is inside.
     * @param x           the global position X.
     * @param y           the global position Y.
     * @param z           the global position Z.
     * @param blockEntity the block entity.
     * @return true if the block entity should be removed.
     */
    default boolean shouldRemoveBeforeProcess(ChunkerColumn column, int x, int y, int z, T blockEntity) {
        return true;
    }
}
