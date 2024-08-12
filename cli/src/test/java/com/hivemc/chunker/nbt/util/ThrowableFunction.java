package com.hivemc.chunker.nbt.util;

/**
 * Function which can throw exceptions.
 *
 * @param <T> the input type.
 * @param <U> the return type.
 */
@FunctionalInterface
public interface ThrowableFunction<T, U> {
    U apply(T t) throws Exception;
}
