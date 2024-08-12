package com.hivemc.chunker.scheduling.function;

import org.jetbrains.annotations.Nullable;

/**
 * A generic class which can be used to represent any function that takes an input and produces an output.
 * This is identical to ThrowableFunction but acts as a parent to any lambdas used allowing a common invoke method.
 *
 * @param <T> type which is taken as a parameter.
 * @param <R> type which is returned by the function.
 */
public sealed interface Invokable<T, R> permits ThrowableConsumer, ThrowableFunction, ThrowableRunnable, ThrowableSupplier {
    /**
     * Invoke the function.
     *
     * @param t the input parameter.
     * @return the returned value, null if no value was returned.
     * @throws Exception an exception if one was thrown during the function.
     */
    @Nullable
    R invoke(T t) throws Exception;
}

