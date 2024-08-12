package com.hivemc.chunker.scheduling.function;

/**
 * Similar to the built-in Function but allows throwing exceptions.
 *
 * @param <T> type which is taken as a parameter.
 * @param <R> type which is returned by the function.
 */
@FunctionalInterface
public non-sealed interface ThrowableFunction<T, R> extends Invokable<T, R> {
    /**
     * The method which should be called when this is invoked.
     *
     * @param t the input type parameter.
     * @return the returned output type.
     * @throws Exception an exception if one occurs.
     */
    R apply(T t) throws Exception;

    @Override
    default R invoke(T t) throws Exception {
        return apply(t);
    }
}
