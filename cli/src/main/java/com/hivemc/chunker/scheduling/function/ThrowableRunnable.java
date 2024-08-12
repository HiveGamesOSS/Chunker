package com.hivemc.chunker.scheduling.function;

/**
 * Similar to the built-in Runnable but allows throwing exceptions.
 */
@FunctionalInterface
public non-sealed interface ThrowableRunnable extends Invokable<Object, Void> {
    /**
     * The method which should be called when this is invoked.
     *
     * @throws Exception an exception if one occurs.
     */
    void run() throws Exception;

    @Override
    default Void invoke(Object ignored) throws Exception {
        run();
        return null; // No return type
    }
}

