package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.tags.TagTestsBase;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Tests for the DoubleTag.
 */
public class DoubleTagTests extends TagTestsBase<DoubleTag, Double> {
    @Override
    public BiConsumer<DoubleTag, Double> getSetter() {
        return DoubleTag::setValue;
    }

    @Override
    public Function<DoubleTag, Double> getGetter() {
        return DoubleTag::getValue;
    }

    @Override
    public Stream<DoubleTag> getTestValues() {
        return DoubleStream.of(Double.MAX_VALUE, Double.MIN_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 1.23, 1337, -1337).mapToObj(DoubleTag::new);
    }
}
