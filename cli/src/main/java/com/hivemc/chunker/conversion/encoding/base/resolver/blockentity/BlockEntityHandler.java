package com.hivemc.chunker.conversion.encoding.base.resolver.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.resolver.hierarchy.TypeHandler;

import java.util.function.Supplier;

/**
 * A BlockEntityTypeHandler handles encoding data from a datatype to a Chunker BlockEntity.
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <D> the datatype being used to store entity data.
 * @param <T> the block entity type which this handler can write/read properties to/from.
 */
public abstract class BlockEntityHandler<R, D, T extends BlockEntity> implements TypeHandler<R, String, D, T> {
    private final String serializedName;
    private final Class<T> blockEntityClass;
    private final Supplier<T> blockEntityConstructor;

    /**
     * Create a new block entity type handler.
     *
     * @param serializedName         the serialized name to use for this block entity type.
     * @param blockEntityClass       the class which is encoded by this handler.
     * @param blockEntityConstructor the constructor for the class if this class is needed to construct it.
     */
    public BlockEntityHandler(String serializedName, Class<T> blockEntityClass, Supplier<T> blockEntityConstructor) {
        this.serializedName = serializedName;
        this.blockEntityClass = blockEntityClass;
        this.blockEntityConstructor = blockEntityConstructor;
    }

    @Override
    public String getKey() {
        return serializedName;
    }

    @Override
    public T construct() {
        return blockEntityConstructor.get();
    }

    @Override
    public Class<T> getHandledClass() {
        return blockEntityClass;
    }
}
