package com.hivemc.chunker.nbt.io;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for reading/writing Java data.
 */
public class JavaReaderWriterTests extends ReaderWriterTestsBase {
    @Override
    public Function<DataOutput, Writer> getWriter() {
        return Writer::toJavaWriter;
    }

    @Override
    public Function<DataInput, Reader> getReader() {
        return Reader::toJavaReader;
    }

    @ParameterizedTest
    @ValueSource(strings = {"😎", "Hello World 🎉"})
    public void testStringEncodeUtf8(String input) throws Exception {
        // Test to ensure symmetrical encoding for characters which are impacted by Java's writeUTF vs byte-array
        assertEncodeDecodeEqual(input, Writer::writeString, reader -> reader.readString(1024), getWriter(), getReader());
    }

    @ParameterizedTest
    @ValueSource(strings = {"😎", "Hello World 🎉"})
    public void testByteStringEncodeModifiedUtf8(String input) throws Exception {
        // Test to ensure symmetrical encoding for characters which are impacted by Java's writeUTF vs byte-array
        // This one uses ByteString (what NBT uses)
        assertEncodeDecodeEqual(input,
                (writer, s) -> writer.writeShortPrefixedByteString(new ByteString(s)),
                reader -> reader.readShortPrefixedByteString(1024).getString(),
                getWriter(), getReader());
    }

    @ParameterizedTest
    @ValueSource(strings = {"😎", "Hello World 🎉"})
    public void testStringEncodingDifference(String input) throws Exception {
        // Test to ensure that these would be an issue if we didn't use the correct writer
        byte[] javaBytes = encodeString(input, Writer::toJavaWriter);
        byte[] bigEndianBytes = encodeString(input, Writer::toBigEndianWriter);
        assertFalse(Arrays.equals(javaBytes, bigEndianBytes), "Test values do not show a difference in UTF-8 encoding");
    }

    private byte[] encodeString(String value, Function<DataOutput, Writer> writerFactory) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {
            writerFactory.apply(dos).writeString(value);
            return baos.toByteArray();
        }
    }
}
