package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.tags.TagTestsBase;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Tests for the StringTag.
 */
public class StringTagTests extends TagTestsBase<StringTag, String> {
    @Override
    public BiConsumer<StringTag, String> getSetter() {
        return StringTag::setValue;
    }

    @Override
    public Function<StringTag, String> getGetter() {
        return StringTag::getValue;
    }

    @Override
    public Stream<StringTag> getTestValues() {
        return Stream.of("", "Hello", "hello", " ", "hello world", "hello world!", "😎", "\n").map(StringTag::new);
    }
}
