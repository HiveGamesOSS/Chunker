package com.hivemc.chunker.nbt.io;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic Tests for ByteString.
 */
public class ByteStringTests {
    @Test
    public void testStringConstructor() {
        ByteString bs = new ByteString("hello");
        assertEquals("hello", bs.getString());
    }

    @Test
    public void testBytesConstructor() {
        byte[] bytes = "hello".getBytes(StandardCharsets.UTF_8);
        ByteString bs = new ByteString(bytes);
        assertArrayEquals(bytes, bs.getBytes());
    }

    @Test
    public void testGetString() {
        ByteString bs = new ByteString("world".getBytes(StandardCharsets.UTF_8));
        assertEquals("world", bs.getString());
    }

    @Test
    public void testGetBytes() {
        ByteString bs = new ByteString("world");
        assertArrayEquals("world".getBytes(StandardCharsets.UTF_8), bs.getBytes());
    }

    @Test
    public void testStringCache() {
        ByteString bs = new ByteString("test".getBytes(StandardCharsets.UTF_8));
        assertSame(bs.getString(), bs.getString());
    }

    @Test
    public void testByteCache() {
        ByteString bs = new ByteString("test");
        assertSame(bs.getBytes(), bs.getBytes());
    }

    @Test
    public void testEquals() {
        assertEquals(new ByteString("hello"), new ByteString("hello"));
    }

    @Test
    public void testEqualsStringAndBytesRepresentations() {
        String str = "hello";
        assertEquals(new ByteString(str), new ByteString(str.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(new ByteString("hello"), new ByteString("world"));
    }

    @Test
    public void testHashCode() {
        assertEquals(new ByteString("hello").hashCode(), new ByteString("hello").hashCode());
    }

    @Test
    public void testHashCode2() {
        String str = "hello";
        assertEquals(
                new ByteString(str).hashCode(),
                new ByteString(str.getBytes(StandardCharsets.UTF_8)).hashCode()
        );
    }

    @Test
    public void testToString() {
        assertEquals("hello", new ByteString("hello").toString());
    }
}
