package com.hivemc.chunker.nbt.tags.collection;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.TagTestsBase;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.array.ByteArrayTag;
import com.hivemc.chunker.nbt.tags.array.IntArrayTag;
import com.hivemc.chunker.nbt.tags.array.LongArrayTag;
import com.hivemc.chunker.nbt.tags.primitive.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CompoundTag.
 */
public class CompoundTagTests extends TagTestsBase<CompoundTag, Map<String, Tag<?>>> {
    @Test
    public void testNullPut() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new IntTag(5));
        assertTrue(compoundTag.contains("Test"));
    }

    @Test
    public void testInitialCapacity() {
        CompoundTag compoundTag = new CompoundTag(5);
        assertTrue(compoundTag.getValue().isEmpty());
    }

    @Test
    public void testGet() {
        CompoundTag compoundTag = new CompoundTag();
        IntTag entry = new IntTag(5);
        compoundTag.put("Test", entry);
        assertEquals(compoundTag.get("Test"), entry);
    }

    @Test
    public void testGetOptionalSuccess() {
        CompoundTag compoundTag = new CompoundTag();
        IntTag entry = new IntTag(5);
        compoundTag.put("Test", entry);
        assertEquals(Optional.of(entry), compoundTag.getOptional("Test", IntTag.class));
    }

    @Test
    public void testGetOptionalEmpty() {
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(Optional.empty(), compoundTag.getOptional("Test", IntTag.class));
    }

    @Test
    public void testGetOptionalListSuccess() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> list = ListTag.fromValues(TagType.INT, List.of(5));
        compoundTag.put("Test", list);
        assertEquals(Optional.of(list), compoundTag.getOptionalList("Test", IntTag.class));
    }

    @Test
    public void testGetOptionalListEmpty() {
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(Optional.empty(), compoundTag.getOptionalList("Test", IntTag.class));
    }

    @Test
    public void testGetOptionalValueSuccess() {
        CompoundTag compoundTag = new CompoundTag();
        IntTag entry = new IntTag(5);
        compoundTag.put("Test", entry);
        assertEquals(Optional.of(5), compoundTag.getOptionalValue("Test", Integer.class));
    }

    @Test
    public void testGetOptionalValueEmpty() {
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(Optional.empty(), compoundTag.getOptionalValue("Test", Integer.class));
    }

    @Test
    public void testGetByte() {
        byte value = (byte) 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new ByteTag(value));
        assertEquals(value, compoundTag.getByte("Test"));
    }

    @Test
    public void testGetByteFallback() {
        byte value = (byte) 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new ByteTag(value));
        assertEquals(value, compoundTag.getByte("Test", (byte) 1));
    }

    @Test
    public void testGetByteFallbackUsed() {
        byte value = (byte) 5;
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(value, compoundTag.getByte("Test", value));
    }

    @Test
    public void testGetDouble() {
        double value = 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new DoubleTag(value));
        assertEquals(value, compoundTag.getDouble("Test"));
    }

    @Test
    public void testGetDoubleFallback() {
        double value = 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new DoubleTag(value));
        assertEquals(value, compoundTag.getDouble("Test", 1));
    }

    @Test
    public void testGetDoubleFallbackUsed() {
        double value = 5;
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(value, compoundTag.getDouble("Test", value));
    }

    @Test
    public void testGetFloat() {
        float value = (float) 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new FloatTag(value));
        assertEquals(value, compoundTag.getFloat("Test"));
    }

    @Test
    public void testGetFloatFallback() {
        float value = (float) 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new FloatTag(value));
        assertEquals(value, compoundTag.getFloat("Test", 1));
    }

    @Test
    public void testGetFloatFallbackUsed() {
        float value = (float) 5;
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(value, compoundTag.getFloat("Test", value));
    }

    @Test
    public void testGetInt() {
        int value = 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new IntTag(value));
        assertEquals(value, compoundTag.getInt("Test"));
    }

    @Test
    public void testGetIntFallback() {
        int value = 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new IntTag(value));
        assertEquals(value, compoundTag.getInt("Test", 1));
    }

    @Test
    public void testGetIntFallbackUsed() {
        int value = 5;
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(value, compoundTag.getInt("Test", value));
    }

    @Test
    public void testGetLong() {
        long value = 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new LongTag(value));
        assertEquals(value, compoundTag.getLong("Test"));
    }

    @Test
    public void testGetLongFallback() {
        long value = 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new LongTag(value));
        assertEquals(value, compoundTag.getLong("Test", 1));
    }

    @Test
    public void testGetLongFallbackUsed() {
        long value = 5;
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(value, compoundTag.getLong("Test", value));
    }

    @Test
    public void testGetShort() {
        short value = (short) 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new ShortTag(value));
        assertEquals(value, compoundTag.getShort("Test"));
    }

    @Test
    public void testGetShortFallback() {
        short value = (short) 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new ShortTag(value));
        assertEquals(value, compoundTag.getShort("Test", (short) 1));
    }


    @Test
    public void testGetShortFallbackUsed() {
        short value = (short) 5;
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(value, compoundTag.getShort("Test", value));
    }

    @Test
    public void testGetString() {
        String value = "Hi";
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new StringTag(value));
        assertEquals(value, compoundTag.getString("Test"));
    }

    @Test
    public void testGetStringFallback() {
        String value = "Hi";
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new StringTag(value));
        assertEquals(value, compoundTag.getString("Test", "Bye"));
    }

    @Test
    public void testGetStringFallbackUsed() {
        String value = "Hi";
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(value, compoundTag.getString("Test", value));
    }

    @Test
    public void testGetByteArray() {
        byte[] value = new byte[]{1};
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new ByteArrayTag(value));
        assertArrayEquals(value, compoundTag.getByteArray("Test"));
    }

    @Test
    public void testGetByteArrayFallback() {
        byte[] value = new byte[]{1};
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new ByteArrayTag(value));
        assertArrayEquals(value, compoundTag.getByteArray("Test", new byte[]{2}));
    }

    @Test
    public void testGetByteArrayFallbackUsed() {
        byte[] value = new byte[]{1};
        CompoundTag compoundTag = new CompoundTag();
        assertArrayEquals(value, compoundTag.getByteArray("Test", value));
    }

    @Test
    public void testGetLongArray() {
        long[] value = new long[]{2};
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new LongArrayTag(value));
        assertArrayEquals(value, compoundTag.getLongArray("Test"));
    }

    @Test
    public void testGetLongArrayFallback() {
        long[] value = new long[]{2};
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new LongArrayTag(value));
        assertArrayEquals(value, compoundTag.getLongArray("Test", new long[]{1}));
    }

    @Test
    public void testGetLongArrayFallbackUsed() {
        long[] value = new long[]{2};
        CompoundTag compoundTag = new CompoundTag();
        assertArrayEquals(value, compoundTag.getLongArray("Test", value));
    }

    @Test
    public void testGetIntArray() {
        int[] value = new int[]{3};
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new IntArrayTag(value));
        assertArrayEquals(value, compoundTag.getIntArray("Test"));
    }

    @Test
    public void testGetIntArrayFallback() {
        int[] value = new int[]{3};
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new IntArrayTag(value));
        assertArrayEquals(value, compoundTag.getIntArray("Test", new int[]{1}));
    }

    @Test
    public void testGetIntArrayFallbackUsed() {
        int[] value = new int[]{3};
        CompoundTag compoundTag = new CompoundTag();
        assertArrayEquals(value, compoundTag.getIntArray("Test", value));
    }

    @Test
    public void testGetCompound() {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag entry = new CompoundTag();
        compoundTag.put("Test", entry);
        assertEquals(entry, compoundTag.getCompound("Test"));
    }

    @Test
    public void testGetCompoundDefault() {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag entry = new CompoundTag();
        compoundTag.put("Test", entry);
        assertEquals(entry, compoundTag.getCompound("Test", null));
    }

    @Test
    public void testGetCompoundDefaultMissing() {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag entry = new CompoundTag();
        compoundTag.put("Test", entry);
        assertNull(compoundTag.getCompound("Test2", null));
    }

    @Test
    public void testGetOrCreateCompoundGet() {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag entry = new CompoundTag();
        compoundTag.put("Test", entry);
        assertTrue(compoundTag.contains("Test"));
        assertEquals(entry, compoundTag.getOrCreateCompound("Test"));
    }

    @Test
    public void testGetOrCreateCompoundCreate() {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag entry = new CompoundTag();
        assertEquals(entry, compoundTag.getOrCreateCompound("Test"));
        assertTrue(compoundTag.contains("Test"));
    }

    @Test
    public void testGetList() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> entry = new ListTag<>(TagType.INT, List.of(new IntTag(1)));
        compoundTag.put("Test", entry);
        assertEquals(entry, compoundTag.getList("Test", IntTag.class));
    }

    @Test
    public void testGetListDefaultLongArray() {
        // Some versions of Java edition use a LongArray instead of an empty list tag, in this case it should return null
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new LongArrayTag(new long[0]));
        assertNull(compoundTag.getList("Test", IntTag.class, null));
    }

    @Test
    public void testGetListDefaultLongArrayEmpty() {
        // Some versions of Java edition use a LongArray instead of an empty list tag, in this case it should return null
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new LongArrayTag());
        assertNull(compoundTag.getList("Test", IntTag.class, null));
    }

    @Test
    public void testGetListDefaultLongArrayNotEmpty() {
        // Some versions of Java edition use a LongArray instead of an empty list tag, in this case it should have an exception
        // As it's not empty
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new LongArrayTag(new long[]{1}));
        assertThrowsExactly(IllegalArgumentException.class, () -> compoundTag.getList("Test", IntTag.class, null));
    }

    @Test
    public void testGetListMismatch() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> entry = new ListTag<>(TagType.INT, List.of(new IntTag(1)));
        compoundTag.put("Test", entry);
        assertThrowsExactly(IllegalArgumentException.class, () -> compoundTag.getList("Test", DoubleTag.class));
    }

    @Test
    public void testGetListDefault() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> entry = new ListTag<>(TagType.INT, List.of(new IntTag(1)));
        compoundTag.put("Test", entry);
        assertEquals(entry, compoundTag.getList("Test", IntTag.class, null));
    }

    @Test
    public void testGetListDefaultMismatch() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> entry = new ListTag<>(TagType.INT, List.of(new IntTag(1)));
        compoundTag.put("Test", entry);
        assertThrowsExactly(IllegalArgumentException.class, () -> compoundTag.getList("Test", DoubleTag.class, null));
    }

    @Test
    public void testGetListDefaultNotFoundMismatch() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> entry = new ListTag<>(TagType.INT, List.of(new IntTag(1)));
        compoundTag.put("Test", entry);
        assertNull(compoundTag.getList("Test2", DoubleTag.class, null));
    }


    @Test
    public void testGetListValues() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> entry = new ListTag<>(TagType.INT, List.of(new IntTag(1)));
        compoundTag.put("Test", entry);
        assertEquals(List.of(1), compoundTag.getListValues("Test", IntTag.class));
    }

    @Test
    public void testGetListValuesMismatch() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> entry = new ListTag<>(TagType.INT, List.of(new IntTag(1)));
        compoundTag.put("Test", entry);
        assertThrowsExactly(IllegalArgumentException.class, () -> compoundTag.getListValues("Test", DoubleTag.class));
    }

    @Test
    public void testGetListValuesDefault() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> entry = new ListTag<>(TagType.INT, List.of(new IntTag(1)));
        compoundTag.put("Test", entry);
        assertEquals(List.of(1), compoundTag.getListValues("Test", IntTag.class, null));
    }

    @Test
    public void testGetListValuesDefaultMismatch() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> entry = new ListTag<>(TagType.INT, List.of(new IntTag(1)));
        compoundTag.put("Test", entry);
        assertThrowsExactly(IllegalArgumentException.class, () -> compoundTag.getListValues("Test", DoubleTag.class, null));
    }

    @Test
    public void testGetListValuesDefaultNotFoundMismatch() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag<IntTag, Integer> entry = new ListTag<>(TagType.INT, List.of(new IntTag(1)));
        compoundTag.put("Test", entry);
        assertNull(compoundTag.getListValues("Test2", DoubleTag.class, null));
    }

    @Test
    public void testGetListValuesDefaultLongArray() {
        // Some versions of Java edition use a LongArray instead of an empty list tag, in this case it should return null
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new LongArrayTag(new long[0]));
        assertNull(compoundTag.getListValues("Test", IntTag.class, null));
    }

    @Test
    public void testGetListValuesDefaultLongArrayEmpty() {
        // Some versions of Java edition use a LongArray instead of an empty list tag, in this case it should return null
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new LongArrayTag());
        assertNull(compoundTag.getListValues("Test", IntTag.class, null));
    }

    @Test
    public void testGetListValuesDefaultLongArrayNotEmpty() {
        // Some versions of Java edition use a LongArray instead of an empty list tag, in this case it should have an exception
        // As it's not empty
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", new LongArrayTag(new long[]{1}));
        assertThrowsExactly(IllegalArgumentException.class, () -> compoundTag.getListValues("Test", IntTag.class, null));
    }

    @Test
    public void testRemove() {
        CompoundTag compoundTag = new CompoundTag();
        IntTag entry = new IntTag(5);
        compoundTag.put("Test", new IntTag(5));
        assertTrue(compoundTag.contains("Test"));
        Tag<?> removed = compoundTag.remove("Test");
        assertFalse(compoundTag.contains("Test"));
        assertEquals(entry, removed);
    }

    @Test
    public void testPutByte() {
        byte value = (byte) 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", value);
        assertEquals(value, compoundTag.getByte("Test"));
    }

    @Test
    public void testPutDouble() {
        double value = 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", value);
        assertEquals(value, compoundTag.getDouble("Test"));
    }

    @Test
    public void testPutFloat() {
        float value = (float) 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", value);
        assertEquals(value, compoundTag.getFloat("Test"));
    }

    @Test
    public void testPutInt() {
        int value = 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", value);
        assertEquals(value, compoundTag.getInt("Test"));
    }

    @Test
    public void testPutLong() {
        long value = 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", value);
        assertEquals(value, compoundTag.getLong("Test"));
    }

    @Test
    public void testPutShort() {
        short value = (short) 5;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", value);
        assertEquals(value, compoundTag.getShort("Test"));
    }

    @Test
    public void testPutString() {
        String value = "Hello!!";
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", value);
        assertEquals(value, compoundTag.getString("Test"));
    }

    @Test
    public void testPutByteArray() {
        byte[] value = new byte[]{6};
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", value);
        assertEquals(value, compoundTag.getByteArray("Test"));
    }

    @Test
    public void testPutIntArray() {
        int[] value = new int[]{3};
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", value);
        assertEquals(value, compoundTag.getIntArray("Test"));
    }

    @Test
    public void testPutLongArray() {
        long[] value = new long[]{1};
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Test", value);
        assertEquals(value, compoundTag.getLongArray("Test"));
    }

    @Test
    public void testSizeEmpty() {
        CompoundTag compoundTag = new CompoundTag(Collections.emptyMap());
        assertEquals(0, compoundTag.size());
    }

    @Test
    public void testSizeNull() {
        CompoundTag compoundTag = new CompoundTag();
        assertEquals(0, compoundTag.size());
    }

    @Test
    public void testSize() {
        CompoundTag compoundTag = new CompoundTag(Map.of("test", new IntTag(5), "hello", new StringTag("world!")));
        assertEquals(2, compoundTag.size());
    }

    @Test
    protected void testLengthProtection() throws IOException {
        Map<String, Tag<?>> values = new Object2ObjectOpenHashMap<>();
        for (int i = 0; i < CompoundTag.MAX_COMPOUND_LENGTH + 1; i++) {
            values.put("test_" + i, new IntTag(i));
        }

        CompoundTag tag = new CompoundTag(values);
        assertDecodeException(tag, IllegalArgumentException.class);
    }

    @Test
    protected void testLengthProtectionNamed() throws IOException {
        Map<String, Tag<?>> values = new Object2ObjectOpenHashMap<>();
        for (int i = 0; i < CompoundTag.MAX_COMPOUND_LENGTH + 1; i++) {
            values.put("test_" + i, new IntTag(i));
        }

        CompoundTag tag = new CompoundTag(values);
        assertDecodeException(new TagWithName<>("hello", tag), IllegalArgumentException.class);
    }

    @Test
    public void testSNBTEscape() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("\"", new CompoundTag());
        assertEquals("{\"\\\"\":{}}", compoundTag.toSNBT());
    }

    @Override
    public BiConsumer<CompoundTag, Map<String, Tag<?>>> getSetter() {
        return null;
    }

    @Override
    public Function<CompoundTag, Map<String, Tag<?>>> getGetter() {
        return CompoundTag::getValue;
    }

    @Override
    public Stream<CompoundTag> getTestValues() {
        return Stream.of(
                null,
                new LinkedHashMap<String, Tag<?>>(Map.of("Hello", new StringTag("Hi!"))),
                new HashMap<String, Tag<?>>(Map.of("Hello", new StringTag("Hi!"))),
                new Object2ObjectOpenHashMap<String, Tag<?>>(Map.of("Hello", new StringTag("Hi!"))),
                new LinkedHashMap<String, Tag<?>>(Map.of("Hello", new IntTag(5))),
                new HashMap<String, Tag<?>>(Map.of("Hello", new IntTag(5))),
                new HashMap<String, Tag<?>>(Map.of("Hello", new IntTag(5))),
                new LinkedHashMap<String, Tag<?>>(Map.of("Hello", new StringTag("Hi!"), "1", new FloatTag(1f))),
                new HashMap<String, Tag<?>>(Map.of("Hello", new StringTag("Hi!"), "1", new FloatTag(1f))),
                new Object2ObjectOpenHashMap<String, Tag<?>>(Map.of("Hello", new StringTag("Hi!"), "1", new FloatTag(1f))),
                new LinkedHashMap<String, Tag<?>>(),
                new HashMap<String, Tag<?>>(),
                new Object2ObjectOpenHashMap<String, Tag<?>>()
        ).map(CompoundTag::new);
    }
}
