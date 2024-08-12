package com.hivemc.chunker.nbt.tags.collection;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.TagTestsBase;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ListTag.
 */
public class ListTagTests extends TagTestsBase<ListTag<Tag<?>, ?>, List<Tag<?>>> {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ListTag newInferred(List<Tag<?>> list) {
        return new ListTag(infer(list), list);
    }

    @SuppressWarnings("rawtypes")
    public static TagType infer(Collection<Tag<?>> collection) {
        if (collection == null || collection.isEmpty()) return TagType.END; // Empty list
        Iterator<Tag<?>> iter = collection.iterator();
        return iter.next().getType(); // Use the first entry
    }

    @Test
    public void testContains() {
        ListTag<IntTag, Integer> list = new ListTag<>(TagType.INT, List.of(new IntTag(1), new IntTag(2)));
        assertTrue(list.contains(1));
    }

    @Test
    public void testContainsFalse() {
        ListTag<IntTag, Integer> list = new ListTag<>(TagType.INT, List.of(new IntTag(1), new IntTag(2)));
        assertFalse(list.contains(3));
    }

    @Test
    public void testSizeEmpty() {
        ListTag<Tag<Void>, Void> list = new ListTag<>(TagType.END, List.of());
        assertEquals(0, list.size());
    }

    @Test
    public void testSizeNull() {
        ListTag<Tag<Object>, Object> list = new ListTag<>();
        assertEquals(0, list.size());
    }

    @Test
    public void testSize() {
        ListTag<IntTag, Integer> list = new ListTag<>(TagType.INT, List.of(new IntTag(1), new IntTag(2)));
        assertEquals(2, list.size());
    }

    @Test
    public void testGetNull() {
        ListTag<Tag<Object>, Object> list = new ListTag<>();
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    public void testGetEmpty() {
        ListTag<IntTag, Integer> list = new ListTag<>(TagType.INT, Collections.emptyList());
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    public void testGet() {
        ListTag<IntTag, Integer> list = new ListTag<>(TagType.INT, List.of(new IntTag(1), new IntTag(2)));
        assertEquals(new IntTag(1), list.get(0));
        assertEquals(new IntTag(2), list.get(1));
    }

    @Test
    public void testSetNull() {
        ListTag<IntTag, Integer> list = new ListTag<>();
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> list.set(0, new IntTag(0)));
    }

    @Test
    public void testSetEmpty() {
        ListTag<IntTag, Integer> list = new ListTag<>(TagType.INT, new ArrayList<>());
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> list.set(0, new IntTag(0)));
    }

    @Test
    public void testSet() {
        ListTag<IntTag, Integer> list = new ListTag<>(TagType.INT, new ArrayList<>(List.of(new IntTag(1), new IntTag(2))));
        assertEquals(new IntTag(1), list.set(0, new IntTag(2)));
        assertEquals(new IntTag(2), list.get(0));
    }

    @Test
    public void testAddNull() {
        ListTag<IntTag, Integer> list = new ListTag<>();
        assertTrue(list.add(new IntTag(5)));
        assertEquals(new IntTag(5), list.get(0));
    }

    @Test
    public void testAddEmpty() {
        ListTag<IntTag, Integer> list = new ListTag<>();
        assertTrue(list.add(new IntTag(5)));
        assertEquals(new IntTag(5), list.get(0));
    }

    @Test
    public void testAdd() {
        ListTag<IntTag, Integer> list = new ListTag<>(TagType.INT, new ArrayList<>(List.of(new IntTag(1), new IntTag(2))));
        assertTrue(list.add(new IntTag(5)));
        assertEquals(new IntTag(5), list.get(2));
    }

    @Test
    public void testIteratorNull() {
        ListTag<Tag<Object>, Object> list = new ListTag<>();
        int iterations = 0;
        for (Tag<Object> entry : list) {
            assertEquals(list.get(iterations++), entry);
        }
        assertEquals(0, iterations);
    }

    @Test
    public void testIterator() {
        ListTag<IntTag, Integer> list = new ListTag<>(TagType.INT, List.of(new IntTag(1), new IntTag(2)));
        int iterations = 0;
        for (IntTag entry : list) {
            assertEquals(list.get(iterations++), entry);
        }
        assertEquals(2, iterations);
    }

    @Test
    protected void testLengthProtection() throws IOException {
        ArrayList<IntTag> values = new ArrayList<>();
        for (int i = 0; i < ListTag.MAX_LIST_LENGTH + 1; i++) {
            values.add(new IntTag(i));
        }

        ListTag<IntTag, Integer> tag = new ListTag<>(TagType.INT, values);
        assertDecodeException(tag, IllegalArgumentException.class);
    }

    @Test
    protected void testLengthProtectionNamed() throws IOException {
        ArrayList<IntTag> values = new ArrayList<>();
        for (int i = 0; i < ListTag.MAX_LIST_LENGTH + 1; i++) {
            values.add(new IntTag(i));
        }

        ListTag<IntTag, Integer> tag = new ListTag<>(TagType.INT, values);
        assertDecodeException(new TagWithName<>("hello", tag), IllegalArgumentException.class);
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("getTestValues")
    public <T extends Tag<V>, V> void testFromValues(ListTag<T, V> input) {
        // The casting here is used because technically empty lists should still work
        TagType<T, V> listType = input.getListType() == null ? (TagType<T, V>) TagType.END : input.getListType();
        assertEquals(input, ListTag.fromValues(listType, input.toList()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public BiConsumer<ListTag<Tag<?>, ?>, List<Tag<?>>> getSetter() {
        return (list, newValue) -> list.setValue(infer(newValue), newValue);
    }

    @Override
    public Function<ListTag<Tag<?>, ?>, List<Tag<?>>> getGetter() {
        return ListTag::getValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Stream<ListTag<Tag<?>, ?>> getTestValues() {
        return Stream.<List<Tag<?>>>of(
                null,
                new ArrayList<>(List.of(new StringTag("Hi!"), new StringTag("Hello!"))),
                List.of(new StringTag("Hi!"), new StringTag("Hello!")),
                new ArrayList<>(List.of(new IntTag(1), new IntTag(2), new IntTag(3))),
                List.of(new IntTag(1), new IntTag(2), new IntTag(3)),
                new ArrayList<>(List.of(new IntTag(1), new IntTag(2))),
                List.of(new IntTag(1), new IntTag(2)),
                new ArrayList<>(List.of(new IntTag(1))),
                List.of(new IntTag(1)),
                new ArrayList<>(List.of()),
                List.of()
        ).map(ListTagTests::newInferred);
    }
}
