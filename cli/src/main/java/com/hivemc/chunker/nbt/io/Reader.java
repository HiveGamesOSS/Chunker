package com.hivemc.chunker.nbt.io;

import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Represents a way to read various types from an input.
 */
public interface Reader {
    /**
     * Create a big endian based reader from a DataInput.
     *
     * @param dataInput the data input to use as the source.
     * @return a reader which wraps the DataInput.
     */
    static Reader toBigEndianReader(DataInput dataInput) {
        return new DataInputReaderBE(dataInput);
    }

    /**
     * Create a Java edition based reader from a DataInput.
     *
     * @param dataInput the data input to use as the source.
     * @return a reader which wraps the DataInput.
     */
    static Reader toJavaReader(DataInput dataInput) {
        return toBigEndianReader(dataInput); // Java uses Big Endian
    }

    /**
     * Create a little endian based reader from a DataInput.
     *
     * @param dataInput the data input to use as the source.
     * @return a reader which wraps the DataInput.
     */
    static Reader toLittleEndianReader(DataInput dataInput) {
        return new DataInputReaderLE(dataInput);
    }

    /**
     * Create a Bedrock edition based reader from a DataInput.
     *
     * @param dataInput the data input to use as the source.
     * @return a reader which wraps the DataInput.
     */
    static Reader toBedrockReader(DataInput dataInput) {
        return toLittleEndianReader(dataInput); // Bedrock uses Little Endian
    }

    /**
     * Read a short-length based byte[] from the buffer.
     *
     * @param maxLength the maximum accepting length for the byte array.
     * @return the byte[] array value which was read.
     * @throws IOException an exception if it failed to read from the underlying buffer.
     */
    default byte[] readShortPrefixedBytes(int maxLength) throws IOException {
        int length = readShort();
        if (length < 0 || length > maxLength)
            throw new IllegalArgumentException("Could not read String with length " + length);

        // Allocate the array to decode into
        byte[] bytes = new byte[length];
        readBytes(bytes);

        // Return the bytes
        return bytes;
    }

    /**
     * Read a short-length based String from the buffer.
     *
     * @param maxLength the maximum accepting length for the String.
     * @return the String value which was read.
     * @throws IOException an exception if it failed to read from the underlying buffer.
     */
    @NotNull
    default String readString(int maxLength) throws IOException {
        // Read the string from bytes
        return new String(readShortPrefixedBytes(maxLength), StandardCharsets.UTF_8);
    }

    /**
     * Read a short from the buffer.
     *
     * @return the newly read short.
     * @throws IOException if the reading fails or there is a decoding error.
     */
    short readShort() throws IOException;

    /**
     * Read a byte array from the buffer.
     *
     * @param array the output array to write to.
     * @throws IOException if the reading fails or there is a decoding error.
     */
    void readBytes(byte @NotNull [] array) throws IOException;

    /**
     * Read a byte from the buffer.
     *
     * @return the newly read byte.
     * @throws IOException if the reading fails or there is a decoding error.
     */
    byte readByte() throws IOException;

    /**
     * Read an unsigned byte from the buffer.
     *
     * @return the newly read unsigned byte.
     * @throws IOException if the reading fails or there is a decoding error.
     */
    int readUnsignedByte() throws IOException;

    /**
     * Read an integer from the buffer.
     *
     * @return the newly read integer.
     * @throws IOException if the reading fails or there is a decoding error.
     */
    int readInt() throws IOException;

    /**
     * Read an unsigned 3-byte integer from the buffer.
     *
     * @return the newly read integer.
     * @throws IOException if the reading fails or there is a decoding error.
     */
    int readUnsignedInt24() throws IOException;

    /**
     * Read a long from the buffer.
     *
     * @return the newly read long.
     * @throws IOException if the reading fails or there is a decoding error.
     */
    long readLong() throws IOException;

    /**
     * Read a float from the buffer.
     *
     * @return the newly read float.
     * @throws IOException if the reading fails or there is a decoding error.
     */
    float readFloat() throws IOException;

    /**
     * Read a double from the buffer.
     *
     * @return the newly read double.
     * @throws IOException if the reading fails or there is a decoding error.
     */
    double readDouble() throws IOException;
}
