package com.hivemc.chunker.conversion.encoding.base.resolver.entity;

import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.resolver.hierarchy.TypeHandler;

import java.util.function.Supplier;

/**
 * A BlockEntityTypeHandler handles encoding data from a datatype to a Chunker BlockEntity.
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <D> the datatype being used to store entity data.
 * @param <T> the block entity type which this handler can write/read properties to/from.
 */
public abstract class EntityHandler<R, D, T extends Entity> implements TypeHandler<R, ChunkerEntityType, D, T> {
    private final ChunkerEntityType entityType;
    private final Class<T> entityClass;
    private final Supplier<T> entityConstructor;

    /**
     * Create a new entity type handler.
     *
     * @param entityType        the entity type used for this entity.
     * @param entityClass       the class which is encoded by this handler.
     * @param entityConstructor the constructor for the class if this class is needed to construct it.
     */
    public EntityHandler(ChunkerEntityType entityType, Class<T> entityClass, Supplier<T> entityConstructor) {
        this.entityType = entityType;
        this.entityClass = entityClass;
        this.entityConstructor = entityConstructor;
    }

    @Override
    public ChunkerEntityType getKey() {
        return entityType;
    }

    @Override
    public T construct() {
        return entityConstructor.get();
    }

    @Override
    public Class<T> getHandledClass() {
        return entityClass;
    }
}
