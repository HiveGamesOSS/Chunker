package com.hivemc.chunker.conversion.encoding.base.resolver.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * An interface to indicate the BlockEntityHandler uses custom NBT to be written.
 * This is useful for block entities like banners which use components.
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <T> the type of the block entity.
 */
public interface CustomItemNBTBlockEntityHandler<R, T extends BlockEntity> {
    /**
     * Generate properties from Item NBT.
     *
     * @param resolvers resolvers used for translating data.
     * @param itemStack the item stack being handled.
     * @param output    the output, either newly constructed or otherwise created by the read method.
     * @param input     the input NBT at the root of the item.
     * @return true if this method was able to add information to the output, if it's false and there was no block
     * entity data read, the output is discarded.
     */
    boolean generateFromItemNBT(@NotNull R resolvers, @NotNull ChunkerItemStack itemStack, @NotNull T output, @NotNull CompoundTag input);

    /**
     * Write properties to Item NBT.
     *
     * @param resolvers the resolvers used for translating data.
     * @param itemStack the item stack being written to.
     * @param input     the input block entity being written.
     * @param output    the output NBT tag at the root.
     * @return true if the block entity data should also be written (calling the write method on this handler).
     */
    boolean writeToItemNBT(@NotNull R resolvers, @NotNull ChunkerItemStack itemStack, @NotNull T input, @NotNull CompoundTag output);
}
