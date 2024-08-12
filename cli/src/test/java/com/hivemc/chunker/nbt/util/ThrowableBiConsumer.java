package com.hivemc.chunker.nbt.util;

/**
 * BiConsumer which can throw exceptions.
 *
 * @param <T> the first input type.
 * @param <U> the second input type.
 */
@FunctionalInterface
public interface ThrowableBiConsumer<T, U> {
    void accept(T t, U u) throws Exception;
}
