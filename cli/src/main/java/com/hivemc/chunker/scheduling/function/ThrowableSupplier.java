package com.hivemc.chunker.scheduling.function;

/**
 * Similar to the built-in Supplier but allows throwing exceptions.
 *
 * @param <T> type which is returned by the function.
 */
@FunctionalInterface
public non-sealed interface ThrowableSupplier<T> extends Invokable<Object, T> {
    /**
     * The method which should be called when this is invoked.
     *
     * @return the returned output type.
     * @throws Exception an exception if one occurs.
     */
    T get() throws Exception;

    @Override
    default T invoke(Object ignored) throws Exception {
        return get();
    }
}

