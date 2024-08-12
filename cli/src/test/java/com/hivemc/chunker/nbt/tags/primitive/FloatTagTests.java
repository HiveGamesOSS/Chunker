package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.tags.TagTestsBase;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Tests for the FloatTag.
 */
public class FloatTagTests extends TagTestsBase<FloatTag, Float> {
    @Override
    public BiConsumer<FloatTag, Float> getSetter() {
        return FloatTag::setValue;
    }

    @Override
    public Function<FloatTag, Float> getGetter() {
        return FloatTag::getValue;
    }

    @Override
    public Stream<FloatTag> getTestValues() {
        return Stream.of(Float.MAX_VALUE, Float.MIN_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0f, 1.23f, 1337f, -1337f).map(FloatTag::new);
    }
}
