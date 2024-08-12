package com.hivemc.chunker.nbt;

import com.hivemc.chunker.nbt.tags.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure the behaviour of TagType is as expected.
 */
class TagTypeTests {
    @ParameterizedTest
    @MethodSource("com.hivemc.chunker.nbt.TagType#values")
    void testTagTypeConstructor(TagType<?, ?> type) {
        // The END tag shouldn't have a constructor
        if (type == TagType.END) {
            assertNull(type.getConstructor());
        } else {
            // Test the constructor
            Supplier<? extends Tag<?>> constructor = type.getConstructor();
            assertNotNull(constructor);

            Tag<?> constructed = constructor.get();
            assertEquals(type.getTagClass(), constructed.getClass());
        }
    }

    @ParameterizedTest
    @MethodSource("com.hivemc.chunker.nbt.TagType#values")
    void testTagTypeClass(TagType<?, ?> type) {
        // The END tag shouldn't have a constructor
        if (type == TagType.END) {
            assertNull(type.getTagClass());
        } else {
            // Test the constructor
            Supplier<? extends Tag<?>> constructor = type.getConstructor();
            assertNotNull(constructor);

            Tag<?> constructed = constructor.get();
            assertEquals(type, constructed.getType());
        }
    }

    @ParameterizedTest
    @MethodSource("com.hivemc.chunker.nbt.TagType#values")
    void testTagTypeIdLookup(TagType<?, ?> type) {
        int id = type.getId();
        assertEquals(type, TagType.getById(id));
    }

    @Test
    void testTagTypeIdLookupNotFound() {
        assertThrowsExactly(IllegalArgumentException.class, () -> TagType.getById(-1));
    }

    @Test
    void testTagTypeIdLookupNotFound2() {
        assertThrowsExactly(IllegalArgumentException.class, () -> TagType.getById(TagType.values().length));
    }
}
