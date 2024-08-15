package com.hivemc.chunker.nbt.tags;

import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * General tests for testing various tag types with NBT encoding / decoding / functionality.
 *
 * @param <T> the tag type.
 * @param <V> the value held by the tag.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TagTestsBase<T extends Tag<V>, V> {
    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testEncodeDecode(T input) throws IOException {
        assertEncodeDecodeEqual(input);
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testNamedEncodeDecodeEmpty(T input) throws IOException {
        assertEncodeDecodeEqual(new TagWithName<>("", input));
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testNamedEncodeDecodeBasic(T input) throws IOException {
        assertEncodeDecodeEqual(new TagWithName<>("hello", input));
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testValueConstructor(T input) {
        Function<? super V, ? extends Tag<V>> valueConstructor = input.getType().getValueConstructor();
        Tag<V> referenceClone = valueConstructor.apply(input.getBoxedValue());
        assertEquals(input, referenceClone);
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testClone(T input) {
        assertEquals(input, input.clone());
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testBoxedValue(T input) {
        assertValueEquals(input.getBoxedValue(), input.clone().getBoxedValue());
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testValueEqualsBoxed(T input) {
        assertTrue(input.valueEquals(input.clone().getBoxedValue()));
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testValueEqualsTag(T input) {
        assertTrue(input.valueEquals(input.clone()));
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testValueHashCode(T input) {
        assertEquals(input.valueHashCode(), input.clone().valueHashCode());
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testHashCode(T input) {
        assertEquals(input.hashCode(), input.clone().hashCode());
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testTagType(T input) {
        assertEquals(input.getType(), input.clone().getType());
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testSetValue(T input) {
        if (getSetter() == null) return; // Some tags like CompoundTag don't have a setter

        // Create a new tag
        T newTag = (T) Objects.requireNonNull(input.getType().getConstructor()).get();
        getSetter().accept(newTag, getGetter().apply(input));

        // Test equality
        assertEquals(input, newTag);
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testGetValueAndBoxed(T input) {
        // Test equality
        assertValueEquals(input.getBoxedValue(), getGetter().apply(input));
    }

    /**
     * The method called to assert value equals on this tag (should be changed for array types).
     *
     * @param expected   The expected value.
     * @param comparison The value being compared against the expected.
     */
    public void assertValueEquals(V expected, V comparison) {
        assertEquals(expected, comparison);
    }

    /**
     * Returns the function representing the Tag::setValue method.
     *
     * @return a method reference to the setValue method.
     */
    public abstract BiConsumer<T, V> getSetter();

    /**
     * Returns the function representing the Tag::getValue method.
     *
     * @return a method reference to the getValue method.
     */
    public abstract Function<T, V> getGetter();

    /**
     * Get a set of tags to be used for testing various methods.
     *
     * @return a stream of test values for the tag type.
     */
    public abstract Stream<T> getTestValues();

    protected void assertEncodeDecodeEqual(Tag<?> tag) throws IOException {
        // Ensure the tag can be read and written using any of the pairs
        assertEncodeDecodeEqual(tag, Writer::toBedrockWriter, Reader::toBedrockReader);
        assertEncodeDecodeEqual(tag, Writer::toJavaWriter, Reader::toJavaReader);
        assertEncodeDecodeEqual(tag, Writer::toBigEndianWriter, Reader::toBigEndianReader);
        assertEncodeDecodeEqual(tag, Writer::toLittleEndianWriter, Reader::toLittleEndianReader);
    }

    protected void assertEncodeDecodeEqual(TagWithName<?> tag) throws IOException {
        // Ensure the tag can be read and written using any of the pairs
        assertEncodeDecodeEqual(tag, Writer::toBedrockWriter, Reader::toBedrockReader);
        assertEncodeDecodeEqual(tag, Writer::toJavaWriter, Reader::toJavaReader);
        assertEncodeDecodeEqual(tag, Writer::toBigEndianWriter, Reader::toBigEndianReader);
        assertEncodeDecodeEqual(tag, Writer::toLittleEndianWriter, Reader::toLittleEndianReader);
    }

    protected void assertDecodeException(Tag<?> tag, Class<? extends Throwable> exception) throws IOException {
        // Ensure the tag can be read and written using any of the pairs
        assertDecodeException(tag, exception, Writer::toBedrockWriter, Reader::toBedrockReader);
        assertDecodeException(tag, exception, Writer::toJavaWriter, Reader::toJavaReader);
        assertDecodeException(tag, exception, Writer::toBigEndianWriter, Reader::toBigEndianReader);
        assertDecodeException(tag, exception, Writer::toLittleEndianWriter, Reader::toLittleEndianReader);
    }

    protected void assertDecodeException(TagWithName<?> tag, Class<? extends Throwable> exception) throws IOException {
        // Ensure the tag can be read and written using any of the pairs
        assertDecodeException(tag, exception, Writer::toBedrockWriter, Reader::toBedrockReader);
        assertDecodeException(tag, exception, Writer::toJavaWriter, Reader::toJavaReader);
        assertDecodeException(tag, exception, Writer::toBigEndianWriter, Reader::toBigEndianReader);
        assertDecodeException(tag, exception, Writer::toLittleEndianWriter, Reader::toLittleEndianReader);
    }

    public void assertDecodeException(Tag<?> tag, Class<? extends Throwable> exception, Function<DataOutput, Writer> writerGenerator, Function<DataInput, Reader> readerGenerator) throws IOException {
        byte[] output;

        // Writing
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(baos)) {
            Writer writer = writerGenerator.apply(dataOutputStream);

            // Write the tag
            Tag.encode(writer, tag);

            // Gather the bytes
            output = baos.toByteArray();
        }

        // Reading
        try (ByteArrayInputStream bais = new ByteArrayInputStream(output);
             DataInputStream dataOutputStream = new DataInputStream(bais)) {
            Reader reader = readerGenerator.apply(dataOutputStream);

            // Check for throwable
            assertThrows(exception, () -> Tag.decode(reader));
        }
    }

    public void assertDecodeException(TagWithName<?> tag, Class<? extends Throwable> exception, Function<DataOutput, Writer> writerGenerator, Function<DataInput, Reader> readerGenerator) throws IOException {
        byte[] output;

        // Writing
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(baos)) {
            Writer writer = writerGenerator.apply(dataOutputStream);

            // Write the tag
            Tag.encodeNamed(writer, tag.name(), tag.tag());

            // Gather the bytes
            output = baos.toByteArray();
        }

        // Reading
        try (ByteArrayInputStream bais = new ByteArrayInputStream(output);
             DataInputStream dataOutputStream = new DataInputStream(bais)) {
            Reader reader = readerGenerator.apply(dataOutputStream);

            // Check for throwable
            assertThrows(exception, () -> Tag.decodeNamed(reader));
        }
    }

    protected void assertEncodeDecodeEqual(TagWithName<?> tag, Function<DataOutput, Writer> writerGenerator, Function<DataInput, Reader> readerGenerator) throws IOException {
        byte[] output;

        // Writing
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(baos)) {
            Writer writer = writerGenerator.apply(dataOutputStream);

            // Write the tag
            Tag.encodeNamed(writer, tag.name(), tag.tag());

            // Gather the bytes
            output = baos.toByteArray();
        }

        // Reading
        try (ByteArrayInputStream bais = new ByteArrayInputStream(output);
             DataInputStream dataOutputStream = new DataInputStream(bais)) {
            Reader reader = readerGenerator.apply(dataOutputStream);

            // Read the tag
            TagWithName<?> decodedTag = Tag.decodeNamed(reader);

            // Check for equality
            assertEquals(tag, decodedTag);
        }
    }

    protected void assertEncodeDecodeEqual(Tag<?> tag, Function<DataOutput, Writer> writerGenerator, Function<DataInput, Reader> readerGenerator) throws IOException {
        byte[] output;

        // Writing
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(baos)) {
            Writer writer = writerGenerator.apply(dataOutputStream);

            // Write the tag
            Tag.encode(writer, tag);

            // Gather the bytes
            output = baos.toByteArray();
        }

        // Reading
        try (ByteArrayInputStream bais = new ByteArrayInputStream(output);
             DataInputStream dataOutputStream = new DataInputStream(bais)) {
            Reader reader = readerGenerator.apply(dataOutputStream);

            // Read the tag
            Tag<?> decodedTag = Tag.decode(reader);

            // Check for equality
            assertEquals(tag, decodedTag);
        }
    }
}
