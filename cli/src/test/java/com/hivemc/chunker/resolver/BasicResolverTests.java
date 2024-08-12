package com.hivemc.chunker.resolver;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Basic tests to ensure implementing a resolver works properly with the default methods.
 */
public class BasicResolverTests {
    @Test
    public void testBasicTo() {
        IntToStringResolver resolver = new IntToStringResolver();
        assertEquals("5", resolver.to(5).orElse(null));
    }

    @Test
    public void testBasicFrom() {
        IntToStringResolver resolver = new IntToStringResolver();
        assertEquals(5, resolver.from("5").orElse(null));
    }

    @Test
    public void testBasicFromAbsent() {
        IntToStringResolver resolver = new IntToStringResolver();
        assertNull(resolver.from("X").orElse(null));
    }

    @Test
    public void testBasicInverseTo() {
        Resolver<String, Integer> resolver = new IntToStringResolver().inverse();
        assertEquals(5, resolver.to("5").orElse(null));
    }

    @Test
    public void testBasicInverseFrom() {
        Resolver<String, Integer> resolver = new IntToStringResolver().inverse();
        assertEquals("5", resolver.from(5).orElse(null));
    }

    @Test
    public void testBasicInverseFromAbsent() {
        Resolver<String, Integer> resolver = new IntToStringResolver().inverse();
        assertNull(resolver.to("X").orElse(null));
    }

    @Test
    public void testBasicChainedTo() {
        IntToStringResolver original = new IntToStringResolver();
        Resolver<Integer, Integer> resolver = new IntToStringResolver().then(original.inverse());
        assertEquals(5, resolver.to(5).orElse(null));
    }

    @Test
    public void testBasicChainedFrom() {
        IntToStringResolver original = new IntToStringResolver();
        Resolver<Integer, Integer> resolver = new IntToStringResolver().then(original.inverse());
        assertEquals(5, resolver.from(5).orElse(null));
    }

    @Test
    public void testBasicChainedFromAbsent() {
        Resolver<String, Integer> original = new IntToStringResolver().inverse();
        Resolver<String, String> resolver = original.then(original.inverse());
        assertNull(resolver.from("X").orElse(null));
    }

    static class IntToStringResolver implements Resolver<Integer, String> {
        @Override
        public Optional<String> to(Integer input) {
            return Optional.of(input.toString());
        }

        @Override
        public Optional<Integer> from(String input) {
            try {
                return Optional.of(Integer.parseInt(input));
            } catch (NumberFormatException exception) {
                return Optional.empty(); // Not possible
            }
        }
    }
}
