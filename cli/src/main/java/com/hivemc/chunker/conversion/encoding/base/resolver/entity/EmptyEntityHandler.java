package com.hivemc.chunker.conversion.encoding.base.resolver.entity;

import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * An entity handler which has no read / write logic but still has a named entity with a class.
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <T> the type of the entity.
 */
public class EmptyEntityHandler<R, T extends Entity> extends EntityHandler<R, CompoundTag, T> {
    /**
     * Create a new block entity type handler.
     *
     * @param entityType  the type of the entity.
     * @param entityClass the class which is encoded by this handler.
     * @param constructor the constructor for the entity.
     */
    public EmptyEntityHandler(ChunkerEntityType entityType, Class<T> entityClass, Supplier<T> constructor) {
        super(entityType, entityClass, constructor);
    }

    @Override
    public void read(@NotNull R resolvers, @NotNull CompoundTag input, @NotNull T entity) {
        // Does nothing
    }

    @Override
    public void write(@NotNull R resolvers, @NotNull CompoundTag output, @NotNull T entity) {
        // Does nothing
    }
}
