package com.hivemc.chunker.nbt.io;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Represents a way to write various types to an output.
 */
public interface Writer {
    /**
     * Create a big endian based writer from a DataOutput.
     *
     * @param dataOutput the data output to use as the destination.
     * @return a writer which wraps the DataOutput.
     */
    static Writer toBigEndianWriter(DataOutput dataOutput) {
        return new DataOutputWriterBE(dataOutput);
    }

    /**
     * Create a Java edition based writer from a DataOutput.
     *
     * @param dataOutput the data output to use as the destination.
     * @return a writer which wraps the DataOutput.
     */
    static Writer toJavaWriter(DataOutput dataOutput) {
        return toBigEndianWriter(dataOutput); // Java uses Big Endian
    }

    /**
     * Create a little endian based writer from a DataOutput.
     *
     * @param dataOutput the data output to use as the destination.
     * @return a writer which wraps the DataOutput.
     */
    static Writer toLittleEndianWriter(DataOutput dataOutput) {
        return new DataOutputWriterLE(dataOutput);
    }

    /**
     * Create a Bedrock edition based writer from a DataOutput.
     *
     * @param dataOutput the data output to use as the destination.
     * @return a writer which wraps the DataOutput.
     */
    static Writer toBedrockWriter(DataOutput dataOutput) {
        return toLittleEndianWriter(dataOutput); // Bedrock uses Little Endian
    }

    /**
     * Write a short-length based byte array to the buffer.
     *
     * @param value the value to write to the buffer
     * @throws IOException an exception if it failed to write to the underlying buffer.
     */
    default void writeShortPrefixedBytes(byte[] value) throws IOException {
        writeShort((short) value.length);
        writeBytes(value);
    }

    /**
     * Write a short-length based String to the buffer.
     *
     * @param value the value to write to the buffer
     * @throws IOException an exception if it failed to write to the underlying buffer.
     */
    default void writeString(@NotNull String value) throws IOException {
        // Write the string as bytes
        writeShortPrefixedBytes(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Write a short to the buffer.
     *
     * @param value the value to write to the buffer.
     * @throws IOException if the writing fails or there is an encoding error.
     */
    void writeShort(short value) throws IOException;

    /**
     * Write a byte array to the buffer.
     *
     * @param value the value to write to the buffer.
     * @throws IOException if the writing fails or there is an encoding error.
     */
    void writeBytes(byte[] value) throws IOException;

    /**
     * Write a byte to the buffer.
     *
     * @param value the value to write to the buffer.
     * @throws IOException if the writing fails or there is an encoding error.
     */
    void writeByte(byte value) throws IOException;

    /**
     * Write an int as a byte to the buffer.
     *
     * @param value the value to write to the buffer.
     * @throws IOException if the writing fails or there is an encoding error.
     */
    void writeByte(int value) throws IOException;

    /**
     * Write an int to the buffer.
     *
     * @param value the value to write to the buffer.
     * @throws IOException if the writing fails or there is an encoding error.
     */
    void writeInt(int value) throws IOException;

    /**
     * Write an unsigned 3-byte int to the buffer.
     *
     * @param value the value to write to the buffer.
     * @throws IOException if the writing fails or there is an encoding error.
     */
    void writeUnsignedInt24(int value) throws IOException;

    /**
     * Write a long to the buffer.
     *
     * @param value the value to write to the buffer.
     * @throws IOException if the writing fails or there is an encoding error.
     */
    void writeLong(long value) throws IOException;

    /**
     * Write a float to the buffer.
     *
     * @param value the value to write to the buffer.
     * @throws IOException if the writing fails or there is an encoding error.
     */
    void writeFloat(float value) throws IOException;

    /**
     * Write a double to the buffer.
     *
     * @param value the value to write to the buffer.
     * @throws IOException if the writing fails or there is an encoding error.
     */
    void writeDouble(double value) throws IOException;
}
