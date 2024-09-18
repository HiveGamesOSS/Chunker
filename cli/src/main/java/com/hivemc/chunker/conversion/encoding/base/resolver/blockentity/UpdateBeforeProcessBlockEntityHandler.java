package com.hivemc.chunker.conversion.encoding.base.resolver.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * A class to indicate a block-entity type handler is updated before the column is submitted for processing.
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <T> the type of the block entity.
 */
public interface UpdateBeforeProcessBlockEntityHandler<R, T extends BlockEntity> {
    /**
     * Update a block entity before processing (when it's in the world).
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

    /**
     * Update a block entity before processing (when it's an item).
     *
     * @param resolvers        the resolvers which can be used by this type.
     * @param itemCompoundTag  the compound tag of the item which was used to make the block entity.
     * @param chunkerItemStack the output item stack.
     * @param blockEntity      the block entity.
     * @return the updated copy of the block entity.
     */
    T updateBeforeProcess(@NotNull R resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, T blockEntity);
}
