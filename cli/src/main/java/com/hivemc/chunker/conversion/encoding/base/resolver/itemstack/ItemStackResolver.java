package com.hivemc.chunker.conversion.encoding.base.resolver.itemstack;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.resolver.property.PropertyBasedResolver;

/**
 * Resolver used for converting between the format specific data-type and an item stack.
 *
 * @param <R> the resolver type to be used.
 * @param <S> the format specific data-type.
 */
public abstract class ItemStackResolver<R, S> extends PropertyBasedResolver<R, S, ChunkerItemStack> {
    /**
     * Create a new property resolver, this registers all the property handlers.
     *
     * @param resolvers the resolvers to use.
     */
    public ItemStackResolver(R resolvers) {
        super(resolvers);
    }
}
