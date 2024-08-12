package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.tags.TagTestsBase;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Tests for the LongTag.
 */
public class LongTagTests extends TagTestsBase<LongTag, Long> {
    @Override
    public BiConsumer<LongTag, Long> getSetter() {
        return LongTag::setValue;
    }

    @Override
    public Function<LongTag, Long> getGetter() {
        return LongTag::getValue;
    }

    @Override
    public Stream<LongTag> getTestValues() {
        return LongStream.of(Long.MAX_VALUE, Long.MIN_VALUE, 0, 1337, -1337).mapToObj(LongTag::new);
    }
}
