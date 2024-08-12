package com.hivemc.chunker.scheduling.function;

/**
 * Similar to the built-in Consumer but allows throwing exceptions.
 *
 * @param <T> type which is taken as a parameter.
 */
@FunctionalInterface
public non-sealed interface ThrowableConsumer<T> extends Invokable<T, Void> {
    /**
     * The method which should be called when this is invoked.
     *
     * @param t the input type parameter.
     * @throws Exception an exception if one occurs.
     */
    void accept(T t) throws Exception;

    @Override
    default Void invoke(T t) throws Exception {
        accept(t);
        return null; // Return null as there's no return type
    }
}
