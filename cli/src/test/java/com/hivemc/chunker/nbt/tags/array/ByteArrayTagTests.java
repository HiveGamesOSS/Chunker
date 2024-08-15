package com.hivemc.chunker.nbt.tags.array;

import com.hivemc.chunker.nbt.tags.TagTestsBase;
import com.hivemc.chunker.nbt.tags.TagWithName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Tests for the ByteArrayTag.
 */
public class ByteArrayTagTests extends TagTestsBase<ByteArrayTag, byte[]> {
    @Override
    public BiConsumer<ByteArrayTag, byte[]> getSetter() {
        return ByteArrayTag::setValue;
    }

    @Override
    public Function<ByteArrayTag, byte[]> getGetter() {
        return ByteArrayTag::getValue;
    }

    @Override
    public void assertValueEquals(byte[] expected, byte[] comparison) {
        assertArrayEquals(expected, comparison);
    }

    @Override
    public Stream<ByteArrayTag> getTestValues() {
        return Stream.of(null, new byte[]{}, new byte[]{0}, new byte[]{1}, new byte[]{Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE}).map(ByteArrayTag::new);
    }

    @Test
    protected void testLengthProtection() throws IOException {
        ByteArrayTag tag = new ByteArrayTag(new byte[ByteArrayTag.MAX_ARRAY_LENGTH + 1]);
        assertDecodeException(tag, IllegalArgumentException.class);
    }

    @Test
    protected void testLengthProtectionNamed() throws IOException {
        ByteArrayTag tag = new ByteArrayTag(new byte[ByteArrayTag.MAX_ARRAY_LENGTH + 1]);
        assertDecodeException(new TagWithName<>("hello", tag), IllegalArgumentException.class);
    }
}
