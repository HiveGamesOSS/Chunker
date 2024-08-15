package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.tags.TagTestsBase;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Tests for the ShortTag.
 */
public class ShortTagTests extends TagTestsBase<ShortTag, Short> {
    @Override
    public BiConsumer<ShortTag, Short> getSetter() {
        return ShortTag::setValue;
    }

    @Override
    public Function<ShortTag, Short> getGetter() {
        return ShortTag::getValue;
    }

    @Override
    public Stream<ShortTag> getTestValues() {
        return Stream.of(Short.MIN_VALUE, Short.MAX_VALUE, (short) 0, (short) 1337, (short) -1337).map(ShortTag::new);
    }
}
