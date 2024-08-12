package com.hivemc.chunker.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * Tests to ensure that SneakyThrows allows throwing of unchecked exceptions.
 */
public class SneakyThrowTests {
    @Test
    public void testSneakyThrows() {
        assertThrowsExactly(Exception.class, () -> SneakyThrows.throwException(new Exception("Test!")));
    }
}
