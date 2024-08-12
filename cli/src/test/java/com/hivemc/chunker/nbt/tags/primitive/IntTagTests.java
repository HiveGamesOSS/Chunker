package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.tags.TagTestsBase;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Tests for the IntTag.
 */
public class IntTagTests extends TagTestsBase<IntTag, Integer> {
    @Override
    public BiConsumer<IntTag, Integer> getSetter() {
        return IntTag::setValue;
    }

    @Override
    public Function<IntTag, Integer> getGetter() {
        return IntTag::getValue;
    }

    @Override
    public Stream<IntTag> getTestValues() {
        return Stream.of(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1337, -1337).map(IntTag::new);
    }
}
