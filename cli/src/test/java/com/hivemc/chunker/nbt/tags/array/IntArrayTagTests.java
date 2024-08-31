package com.hivemc.chunker.nbt.tags.array;

import com.hivemc.chunker.nbt.tags.TagTestsBase;
import com.hivemc.chunker.nbt.tags.TagWithName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the IntArrayTag.
 */
public class IntArrayTagTests extends TagTestsBase<IntArrayTag, int[]> {
    @Override
    public BiConsumer<IntArrayTag, int[]> getSetter() {
        return IntArrayTag::setValue;
    }

    @Override
    public Function<IntArrayTag, int[]> getGetter() {
        return IntArrayTag::getValue;
    }

    @Override
    public void assertValueEquals(int[] expected, int[] comparison) {
        assertArrayEquals(expected, comparison);
    }

    @Override
    public Stream<IntArrayTag> getTestValues() {
        return Stream.of(null, new int[]{}, new int[]{0}, new int[]{1}, new int[]{Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE}).map(IntArrayTag::new);
    }

    @Test
    protected void testLengthProtection() throws IOException {
        IntArrayTag tag = new IntArrayTag(new int[IntArrayTag.MAX_ARRAY_LENGTH + 1]);
        assertDecodeException(tag, IllegalArgumentException.class);
    }

    @Test
    protected void testLengthProtectionNamed() throws IOException {
        IntArrayTag tag = new IntArrayTag(new int[IntArrayTag.MAX_ARRAY_LENGTH + 1]);
        assertDecodeException(new TagWithName<>("hello", tag), IllegalArgumentException.class);
    }

    @Test
    protected void testLength() throws IOException {
        IntArrayTag tag = new IntArrayTag(new int[5]);
        assertEquals(tag.length(), 5);
    }

    @Test
    protected void testLengthEmpty() throws IOException {
        IntArrayTag tag = new IntArrayTag(new int[0]);
        assertEquals(tag.length(), 0);
    }

    @Test
    protected void testLengthNull() throws IOException {
        IntArrayTag tag = new IntArrayTag();
        assertEquals(tag.length(), 0);
    }
}
