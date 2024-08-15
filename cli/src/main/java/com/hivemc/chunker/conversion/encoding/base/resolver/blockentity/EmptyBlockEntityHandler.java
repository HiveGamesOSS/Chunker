package com.hivemc.chunker.conversion.encoding.base.resolver.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A block entity handler which has no read / write logic but still has a named block entity with a class.
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <T> the type of the block entity.
 */
public class EmptyBlockEntityHandler<R, T extends BlockEntity> extends BlockEntityHandler<R, CompoundTag, T> {
    /**
     * Create a new block entity type handler.
     *
     * @param serializedName   the serialized name to use for this block entity type.
     * @param blockEntityClass the class which is encoded by this handler.
     * @param constructor      the constructor for the block entity.
     */
    public EmptyBlockEntityHandler(String serializedName, Class<T> blockEntityClass, Supplier<T> constructor) {
        super(serializedName, blockEntityClass, constructor);
    }

    @Override
    public void read(@NotNull R resolvers, @NotNull CompoundTag input, @NotNull T blockEntity) {
        // Does nothing
    }

    @Override
    public void write(@NotNull R resolvers, @NotNull CompoundTag output, @NotNull T blockEntity) {
        // Does nothing
    }
}
