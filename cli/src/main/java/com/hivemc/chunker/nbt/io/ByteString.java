package com.hivemc.chunker.nbt.io;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Represents a String which is held either as UTF-8 bytes or a Java String. It allows conversion between the two and
 * caches the result.
 * Note: Comparison/Hashing uses the byte array value to compare.
 */
public class ByteString {
    private byte[] bytes;
    private String string;

    /**
     * Create a ByteString from a String value.
     *
     * @param string the string value.
     */
    public ByteString(@NotNull String string) {
        this.string = string;
    }

    /**
     * Create a ByteString from a UTF-8 byte array.
     *
     * @param bytes the UTF-8 bytes.
     */
    public ByteString(byte @NotNull [] bytes) {
        this.bytes = bytes;
    }

    /**
     * Get the String representation, decoding from bytes if not yet cached.
     *
     * @return the String value.
     */
    @NotNull
    public String getString() {
        if (string == null) string = new String(bytes, StandardCharsets.UTF_8);
        return string;
    }

    /**
     * Get the UTF-8 byte array, encoding from the String if not yet cached.
     *
     * @return the UTF-8 bytes.
     */
    public byte @NotNull [] getBytes() {
        if (bytes == null) bytes = string.getBytes(StandardCharsets.UTF_8);
        return bytes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ByteString other)) return false;
        return Arrays.equals(getBytes(), other.getBytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getBytes());
    }

    @Override
    public String toString() {
        return getString();
    }
}
