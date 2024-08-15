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
 * Tests for the LongArrayTag.
 */
public class LongArrayTagTests extends TagTestsBase<LongArrayTag, long[]> {
    @Override
    public BiConsumer<LongArrayTag, long[]> getSetter() {
        return LongArrayTag::setValue;
    }

    @Override
    public Function<LongArrayTag, long[]> getGetter() {
        return LongArrayTag::getValue;
    }

    @Override
    public void assertValueEquals(long[] expected, long[] comparison) {
        assertArrayEquals(expected, comparison);
    }

    @Override
    public Stream<LongArrayTag> getTestValues() {
        return Stream.of(null, new long[]{}, new long[]{0}, new long[]{1}, new long[]{Long.MIN_VALUE, -1, 0, 1, Long.MAX_VALUE}).map(LongArrayTag::new);
    }

    @Test
    protected void testLengthProtection() throws IOException {
        LongArrayTag tag = new LongArrayTag(new long[LongArrayTag.MAX_ARRAY_LENGTH + 1]);
        assertDecodeException(tag, IllegalArgumentException.class);
    }

    @Test
    protected void testLengthProtectionNamed() throws IOException {
        LongArrayTag tag = new LongArrayTag(new long[LongArrayTag.MAX_ARRAY_LENGTH + 1]);
        assertDecodeException(new TagWithName<>("hello", tag), IllegalArgumentException.class);
    }
}
