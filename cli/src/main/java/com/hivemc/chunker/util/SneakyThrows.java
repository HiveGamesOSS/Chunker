package com.hivemc.chunker.util;

/**
 * Util class to allow exceptions to be rethrown
 */
public class SneakyThrows {
    /**
     * Throw an exception without having to declare the exception in the method.
     *
     * @param exception the exception to be thrown.
     * @param <T>       the type of the exception.
     * @throws T the exception given as a parameter.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void throwException(Throwable exception) throws T {
        throw (T) exception;
    }
}
