package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.tags.TagTestsBase;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        return Stream.concat(
                Stream.of("", "Hello", "hello", " ", "hello world", "hello world!", "😎", "\n").map(StringTag::new),
                Stream.of(new byte[]{0, 0, 0, 1, 0, 0, 0, 39}).map(StringTag::new) // StorageKey example in Bedrock
        );
    }

    @Test
    public void testGetByteArrayValue() {
        byte[] storageKey = new byte[]{0, 0, 0, 1, 0, 0, 0, 39};
        StringTag tag = new StringTag(storageKey);
        assertEquals(storageKey, tag.getByteArrayValue());
    }

    @Test
    public void testSetByteArrayValue() {
        byte[] storageKey = new byte[]{0, 0, 0, 1, 0, 0, 0, 39};
        StringTag tag = new StringTag();
        tag.setValue(storageKey);
        assertEquals(storageKey, tag.getByteArrayValue());
    }

    @Test
    public void testSetNullString() {
        StringTag tag = new StringTag("Hi!");
        tag.setValue((String) null);
        assertNull(tag.getValue());
    }

    @Test
    public void testGetNullString() {
        StringTag tag = new StringTag();
        assertNull(tag.getValue());
    }
}
