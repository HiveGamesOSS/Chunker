package com.hivemc.chunker.nbt.io;

import com.hivemc.chunker.nbt.util.ThrowableBiConsumer;
import com.hivemc.chunker.nbt.util.ThrowableFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * Base class for encoding tests for readers/writers.
 */
public abstract class ReaderWriterTestsBase {
    public static Stream<byte[]> byteInputs() {
        return Stream.of(new byte[]{}, new byte[]{0}, new byte[]{1}, new byte[]{Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE});
    }

    public static Stream<byte[]> getByteArrayExamples() {
        return Stream.of(
                new byte[]{},
                new byte[]{(byte) 0xC3, (byte) 0x28, (byte) 0x61, (byte) 0x62, (byte) 0x63}, // StorageKey example
                new byte[]{1, 2, 3},
                "Hello".getBytes(StandardCharsets.UTF_8)
        );
    }

    public abstract Function<DataOutput, Writer> getWriter();

    public abstract Function<DataInput, Reader> getReader();

    @ParameterizedTest
    @ValueSource(strings = {"", "Hello", "hello", " ", "hello world", "hello world!", "\n"})
    public void testStringEncode(String input) throws Exception {
        assertEncodeDecodeEqual(input, Writer::writeString, (reader) -> reader.readString(1024), getWriter(), getReader());
    }

    @Test
    public void testStringEncodeFail() throws Exception {
        byte[] output;

        // Writing
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(baos)) {
            Writer writer = getWriter().apply(dataOutputStream);

            // Write the value
            writer.writeString("Hello!");

            // Gather the bytes
            output = baos.toByteArray();
        }

        // Reading
        try (ByteArrayInputStream bais = new ByteArrayInputStream(output);
             DataInputStream dataOutputStream = new DataInputStream(bais)) {
            Reader reader = getReader().apply(dataOutputStream);

            // Read the value (should throw exception as it's longer than 5)
            assertThrowsExactly(IllegalArgumentException.class, () -> reader.readString(5));
        }
    }

    @ParameterizedTest
    @MethodSource("getByteArrayExamples")
    public void testShortPrefixedByteArrayEncode(byte[] input) throws Exception {
        assertEncodeDecodeEqual(input, Writer::writeShortPrefixedBytes, (reader) -> reader.readShortPrefixedBytes(1024), getWriter(), getReader(), Assertions::assertArrayEquals);
    }

    @Test
    public void testShortPrefixedByteArrayEncodeFail() throws Exception {
        byte[] output;

        // Writing
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(baos)) {
            Writer writer = getWriter().apply(dataOutputStream);

            // Write the value
            writer.writeShortPrefixedBytes(new byte[]{1, 2, 3, 4, 5, 6});

            // Gather the bytes
            output = baos.toByteArray();
        }

        // Reading
        try (ByteArrayInputStream bais = new ByteArrayInputStream(output);
             DataInputStream dataOutputStream = new DataInputStream(bais)) {
            Reader reader = getReader().apply(dataOutputStream);

            // Read the value (should throw exception as it's longer than 5)
            assertThrowsExactly(IllegalArgumentException.class, () -> reader.readShortPrefixedBytes(5));
        }
    }

    @ParameterizedTest
    @ValueSource(shorts = {Short.MIN_VALUE, Short.MAX_VALUE, (short) 0, (short) 1337, (short) -1337})
    public void testShortEncode(short input) throws Exception {
        assertEncodeDecodeEqual(input, Writer::writeShort, Reader::readShort, getWriter(), getReader());
    }

    @ParameterizedTest
    @MethodSource("byteInputs")
    public void testBytesEncode(byte[] input) throws Exception {
        assertEncodeDecodeEqual(input, getWriter(), getReader());
    }

    @ParameterizedTest
    @ValueSource(bytes = {Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) -1, (byte) 0, (byte) 1})
    public void testByteEncode(byte input) throws Exception {
        assertEncodeDecodeEqual(input, Writer::writeByte, Reader::readByte, getWriter(), getReader());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 255, 128})
    public void testUnsignedByteEncode(int input) throws Exception {
        assertEncodeDecodeEqual(input, Writer::writeByte, Reader::readUnsignedByte, getWriter(), getReader());
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1337, -1337})
    public void testIntegerEncode(int input) throws Exception {
        assertEncodeDecodeEqual(input, Writer::writeInt, Reader::readInt, getWriter(), getReader());
    }

    @ParameterizedTest
    @ValueSource(ints = {Short.MAX_VALUE, 0, 1337, Short.MAX_VALUE + 100})
    public void testInteger24Encode(int input) throws Exception {
        assertEncodeDecodeEqual(input, Writer::writeUnsignedInt24, Reader::readUnsignedInt24, getWriter(), getReader());
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MAX_VALUE, Long.MIN_VALUE, 0, 1337, -1337})
    public void testLongEncode(long input) throws Exception {
        assertEncodeDecodeEqual(input, Writer::writeLong, Reader::readLong, getWriter(), getReader());
    }

    @ParameterizedTest
    @ValueSource(floats = {Float.MAX_VALUE, Float.MIN_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0f, 1.23f, 1337f, -1337f})
    public void testFloatEncode(float input) throws Exception {
        assertEncodeDecodeEqual(input, Writer::writeFloat, Reader::readFloat, getWriter(), getReader());
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.MAX_VALUE, Double.MIN_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 1.23, 1337, -1337})
    public void testDoubleEncode(double input) throws Exception {
        assertEncodeDecodeEqual(input, Writer::writeDouble, Reader::readDouble, getWriter(), getReader());
    }

    protected <T> void assertEncodeDecodeEqual(byte[] input, Function<DataOutput, Writer> writerGenerator, Function<DataInput, Reader> readerGenerator) throws Exception {
        byte[] output;

        // Writing
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(baos)) {
            Writer writer = writerGenerator.apply(dataOutputStream);

            // Write the value
            writer.writeBytes(input);

            // Gather the bytes
            output = baos.toByteArray();
        }

        // Reading
        try (ByteArrayInputStream bais = new ByteArrayInputStream(output);
             DataInputStream dataOutputStream = new DataInputStream(bais)) {
            Reader reader = readerGenerator.apply(dataOutputStream);

            // Read the value
            byte[] decodedValue = new byte[input.length];
            reader.readBytes(decodedValue);

            // Check for equality
            assertArrayEquals(input, decodedValue);
        }
    }

    protected <T> void assertEncodeDecodeEqual(T input, ThrowableBiConsumer<Writer, T> writeFunction, ThrowableFunction<Reader, T> readFunction, Function<DataOutput, Writer> writerGenerator, Function<DataInput, Reader> readerGenerator) throws Exception {
        assertEncodeDecodeEqual(input, writeFunction, readFunction, writerGenerator, readerGenerator, Assertions::assertEquals);
    }

    protected <T> void assertEncodeDecodeEqual(T input, ThrowableBiConsumer<Writer, T> writeFunction, ThrowableFunction<Reader, T> readFunction, Function<DataOutput, Writer> writerGenerator, Function<DataInput, Reader> readerGenerator, BiConsumer<T, T> assertEquals) throws Exception {
        byte[] output;

        // Writing
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(baos)) {
            Writer writer = writerGenerator.apply(dataOutputStream);

            // Write the value
            writeFunction.accept(writer, input);

            // Gather the bytes
            output = baos.toByteArray();
        }

        // Reading
        try (ByteArrayInputStream bais = new ByteArrayInputStream(output);
             DataInputStream dataOutputStream = new DataInputStream(bais)) {
            Reader reader = readerGenerator.apply(dataOutputStream);

            // Read the value
            T decodedValue = readFunction.apply(reader);

            // Check for equality
            assertEquals.accept(input, decodedValue);
        }
    }
}
