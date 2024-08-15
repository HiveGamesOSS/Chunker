package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.tags.TagTestsBase;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Tests for the ByteTag.
 */
public class ByteTagTests extends TagTestsBase<ByteTag, Byte> {
    @Override
    public BiConsumer<ByteTag, Byte> getSetter() {
        return ByteTag::setValue;
    }

    @Override
    public Function<ByteTag, Byte> getGetter() {
        return ByteTag::getValue;
    }

    @Override
    public Stream<ByteTag> getTestValues() {
        return Stream.of(Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) -1, (byte) 0, (byte) 1).map(ByteTag::new);
    }
}
