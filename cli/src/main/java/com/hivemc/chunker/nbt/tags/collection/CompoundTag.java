package com.hivemc.chunker.nbt.tags.collection;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.array.ByteArrayTag;
import com.hivemc.chunker.nbt.tags.array.IntArrayTag;
import com.hivemc.chunker.nbt.tags.array.LongArrayTag;
import com.hivemc.chunker.nbt.tags.primitive.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

/**
 * Represents a dictionary of tags which are stored in a key-value storage, using a String as the key.
 */
public class CompoundTag extends Tag<Map<String, Tag<?>>> implements Iterable<Map.Entry<String, Tag<?>>> {
    public static final int MAX_COMPOUND_LENGTH = 32768;
    public static final int MAX_NAME_LENGTH = 256;
    @Nullable
    protected Map<String, Tag<?>> value;

    /**
     * Create a CompoundTag with an existing Map.
     *
     * @param value the initial value for the tag.
     */
    public CompoundTag(@Nullable Map<String, Tag<?>> value) {
        super();
        this.value = value;
    }

    /**
     * Create a CompoundTag with no value (null).
     */
    public CompoundTag() {
        this(null);
    }

    @Override
    @NotNull
    public TagType<CompoundTag, Map<String, Tag<?>>> getType() {
        return TagType.COMPOUND;
    }

    @Override
    public boolean valueEquals(Map<String, Tag<?>> boxedValue) {
        return Objects.equals(value, boxedValue) || value == null && boxedValue.isEmpty() || boxedValue == null && value.isEmpty();
    }

    @Override
    public boolean valueEquals(Tag<Map<String, Tag<?>>> tag) {
        return valueEquals(((CompoundTag) tag).getValue());
    }

    @Override
    public Map<String, Tag<?>> getBoxedValue() {
        return getValue();
    }

    @Override
    public CompoundTag clone() {
        if (value != null) {
            Map<String, Tag<?>> copy = new Object2ObjectLinkedOpenHashMap<>(value.size());
            for (Map.Entry<String, Tag<?>> namedPair : value.entrySet()) {
                copy.put(namedPair.getKey(), namedPair.getValue().clone());
            }
            return new CompoundTag(copy);
        } else {
            return new CompoundTag();
        }
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        if (value != null) {
            for (Map.Entry<String, Tag<?>> namedPair : value.entrySet()) {
                Tag.encodeNamed(writer, namedPair.getKey(), namedPair.getValue());
            }
        }

        // Write end tag
        Tag.encode(writer, null);
    }

    @Override
    public void decodeValue(Reader reader) throws IOException {
        Tag<?> lastTag;
        value = new Object2ObjectLinkedOpenHashMap<>(8);

        do {
            lastTag = Tag.decodeTagClass(reader);
            if (lastTag != null) {
                // Read the name
                String name = reader.readString(MAX_NAME_LENGTH);

                // Decode value
                lastTag.decodeValue(reader);

                // Add to our storage
                value.put(name, lastTag);

                // Validate if the storage is now too big
                if (value.size() > MAX_COMPOUND_LENGTH) {
                    throw new IllegalArgumentException("Could not read array with length above " + value.size() + " for " + name);
                }
            }
        } while (lastTag != null);
    }

    @Override
    public String toSNBT() {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        if (value != null) {
            for (Map.Entry<String, Tag<?>> namedPair : value.entrySet()) {
                String escaped = StringTag.SNBT_ESCAPER.escape(namedPair.getKey());

                // Add quotes if it was escaped
                if (!escaped.equals(namedPair.getKey())) {
                    escaped = String.format("\"%s\"", escaped);
                }
                joiner.add(escaped + ":" + namedPair.getValue().toSNBT());
            }
        }
        return joiner.toString();
    }

    /**
     * Add a Tag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name the name of the tag.
     * @param tag  the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, @NotNull Tag<?> tag) {
        // Ensure the value isn't null and allocate the map
        if (value == null) {
            value = new Object2ObjectLinkedOpenHashMap<>(1);
        }

        return value.put(name, tag);
    }

    /**
     * Add a ByteTag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name  the name of the tag.
     * @param value the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, byte value) {
        return put(name, new ByteTag(value));
    }

    /**
     * Add a DoubleTag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name  the name of the tag.
     * @param value the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, double value) {
        return put(name, new DoubleTag(value));
    }

    /**
     * Add a FloatTag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name  the name of the tag.
     * @param value the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, float value) {
        return put(name, new FloatTag(value));
    }

    /**
     * Add a IntTag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name  the name of the tag.
     * @param value the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, int value) {
        return put(name, new IntTag(value));
    }

    /**
     * Add a LongTag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name  the name of the tag.
     * @param value the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, long value) {
        return put(name, new LongTag(value));
    }

    /**
     * Add a ShortTag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name  the name of the tag.
     * @param value the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, short value) {
        return put(name, new ShortTag(value));
    }

    /**
     * Add a StringTag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name  the name of the tag.
     * @param value the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, @NotNull String value) {
        return put(name, new StringTag(value));
    }

    /**
     * Add a ByteArrayTag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name  the name of the tag.
     * @param value the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, byte @NotNull [] value) {
        return put(name, new ByteArrayTag(value));
    }

    /**
     * Add a IntArrayTag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name  the name of the tag.
     * @param value the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, int @NotNull [] value) {
        return put(name, new IntArrayTag(value));
    }

    /**
     * Add a LongArrayTag to the key-value storage.
     * Note: This will initialize the backing map if it was null.
     *
     * @param name  the name of the tag.
     * @param value the value for the tag.
     * @return the previous tag if one was associated or null if there was no previous.
     */
    public Tag<?> put(@NotNull String name, long @NotNull [] value) {
        return put(name, new LongArrayTag(value));
    }

    /**
     * Check whether the CompoundTag contains a specific name.
     *
     * @param name the name to search for.
     * @return true if the name was found in the map, or false otherwise (case-sensitive).
     */
    public boolean contains(String name) {
        if (value == null) return false; // No map present
        return value.containsKey(name);
    }

    /**
     * Remove an entry from the key-value based storage if it exists.
     *
     * @param name the name to search for.
     * @return the name was found in the map, or null otherwise (case-sensitive).
     */
    public Tag<?> remove(String name) {
        if (value == null) return null; // No map present
        return value.remove(name);
    }

    /**
     * Get an entry from the key-value based storage.
     *
     * @param name the name to search for.
     * @param <T>  the type to be used for the tag.
     * @return the tag if it was found otherwise null.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Tag<?>> T get(String name) {
        return (T) get(name, Tag.class);
    }

    /**
     * Get an entry from the key-value based storage.
     *
     * @param name  the name to search for.
     * @param clazz the class to expect for the tag, if it is not this class a ClassCastException will be thrown.
     * @param <T>   the type to be used for the tag.
     * @return the tag if it was found otherwise null.
     */
    @Nullable
    public <T extends Tag<?>> T get(String name, Class<T> clazz) {
        if (value == null) return null; // No map present
        Tag<?> tag = value.get(name);

        // Return the tag
        return clazz.cast(tag);
    }

    /**
     * Get an entry from the key-value based storage as an optional of the tag.
     *
     * @param name     the name to search for.
     * @param classTag the class to expect for the tag, if it is not this class a ClassCastException will be thrown.
     * @param <T>      the tag type to be used for the optional.
     * @return the optional with the value if present (not tag).
     */
    public <T extends Tag<?>> Optional<T> getOptional(String name, Class<T> classTag) {
        Tag<T> tag = get(name);
        if (tag == null) return Optional.empty();

        // Return the tag
        return Optional.of(classTag.cast(tag));
    }

    /**
     * Get an entry from the key-value based storage as an optional of the tag value.
     *
     * @param name       the name to search for.
     * @param classValue the class to expect for the tag value, if it is not this class a ClassCastException will be thrown.
     * @param <V>        the type to be used for the optional.
     * @return the optional with the value if present (not tag).
     */
    public <V> Optional<V> getOptionalValue(String name, Class<V> classValue) {
        Tag<V> tag = get(name);
        if (tag == null) return Optional.empty();

        // Return the tag
        return Optional.of(classValue.cast(tag.getBoxedValue()));
    }

    /**
     * Get a ByteTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public byte getByte(String name) {
        ByteTag tag = get(name, ByteTag.class);
        if (tag == null) throw new IllegalArgumentException("Could not access " + name);

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a ByteTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the name wasn't found.
     * @return the tag if it was found otherwise it will return the default value.
     */
    public byte getByte(String name, byte defaultValue) {
        ByteTag tag = get(name, ByteTag.class);
        if (tag == null) return defaultValue;

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a DoubleTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public double getDouble(String name) {
        DoubleTag tag = get(name, DoubleTag.class);
        if (tag == null) throw new IllegalArgumentException("Could not access " + name);

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a DoubleTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the name wasn't found.
     * @return the tag if it was found otherwise it will return the default value.
     */
    public double getDouble(String name, double defaultValue) {
        DoubleTag tag = get(name, DoubleTag.class);
        if (tag == null) return defaultValue;

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a FloatTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public float getFloat(String name) {
        FloatTag tag = get(name, FloatTag.class);
        if (tag == null) throw new IllegalArgumentException("Could not access " + name);

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a FloatTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the name wasn't found.
     * @return the tag if it was found otherwise it will return the default value.
     */
    public float getFloat(String name, float defaultValue) {
        FloatTag tag = get(name, FloatTag.class);
        if (tag == null) return defaultValue;

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a IntTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public int getInt(String name) {
        IntTag tag = get(name, IntTag.class);
        if (tag == null) throw new IllegalArgumentException("Could not access " + name);

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a IntTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the name wasn't found.
     * @return the tag if it was found otherwise it will return the default value.
     */
    public int getInt(String name, int defaultValue) {
        IntTag tag = get(name, IntTag.class);
        if (tag == null) return defaultValue;

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a LongTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public long getLong(String name) {
        LongTag tag = get(name, LongTag.class);
        if (tag == null) throw new IllegalArgumentException("Could not access " + name);

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a LongTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the name wasn't found.
     * @return the tag if it was found otherwise it will return the default value.
     */
    public long getLong(String name, long defaultValue) {
        LongTag tag = get(name, LongTag.class);
        if (tag == null) return defaultValue;

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a ShortTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public short getShort(String name) {
        ShortTag tag = get(name, ShortTag.class);
        if (tag == null) throw new IllegalArgumentException("Could not access " + name);

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a ShortTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the name wasn't found.
     * @return the tag if it was found otherwise it will return the default value.
     */
    public short getShort(String name, short defaultValue) {
        ShortTag tag = get(name, ShortTag.class);
        if (tag == null) return defaultValue;

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a StringTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public String getString(String name) {
        StringTag tag = get(name, StringTag.class);
        if (tag == null) throw new IllegalArgumentException("Could not access " + name);

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a StringTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the name wasn't found.
     * @return the tag if it was found otherwise it will return the default value.
     */
    public String getString(String name, String defaultValue) {
        StringTag tag = get(name, StringTag.class);
        if (tag == null) return defaultValue;

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a ByteArrayTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public byte[] getByteArray(String name) {
        ByteArrayTag tag = get(name, ByteArrayTag.class);
        if (tag == null) throw new IllegalArgumentException("Could not access " + name);

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a ByteArrayTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the name wasn't found.
     * @return the tag if it was found otherwise it will return the default value.
     */
    public byte[] getByteArray(String name, byte[] defaultValue) {
        ByteArrayTag tag = get(name, ByteArrayTag.class);
        if (tag == null) return defaultValue;

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a IntArrayTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public int[] getIntArray(String name) {
        IntArrayTag tag = get(name, IntArrayTag.class);
        if (tag == null) throw new IllegalArgumentException("Could not access " + name);

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a IntArrayTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the name wasn't found.
     * @return the tag if it was found otherwise it will return the default value.
     */
    public int[] getIntArray(String name, int[] defaultValue) {
        IntArrayTag tag = get(name, IntArrayTag.class);
        if (tag == null) return defaultValue;

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a LongArrayTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public long[] getLongArray(String name) {
        LongArrayTag tag = get(name, LongArrayTag.class);
        if (tag == null) throw new IllegalArgumentException("Could not access " + name);

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a LongArrayTag value from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the name wasn't found.
     * @return the tag if it was found otherwise it will return the default value.
     */
    public long[] getLongArray(String name, long[] defaultValue) {
        LongArrayTag tag = get(name, LongArrayTag.class);
        if (tag == null) return defaultValue;

        // Return the value
        return tag.getValue();
    }

    /**
     * Get a ListTag from the key-value based storage, if it is not the right type it will throw a ClassCastException and if the
     * listTagType doesn't match it will throw an IllegalArgumentException.
     *
     * @param name        the name to search for.
     * @param listTagType the type of the list values to expect.
     * @param <T>         the type of each tag in the list.
     * @param <V>         the boxed value type of each tag in the list.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    @SuppressWarnings("unchecked")
    public <T extends Tag<V>, V> ListTag<T, V> getList(String name, Class<T> listTagType) {
        ListTag<T, V> listTag = get(name, ListTag.class);
        if (listTag == null) throw new IllegalArgumentException("Could not access " + name);
        if (listTag.getListType() != TagType.END && listTag.getListType() != null && !Objects.equals(listTag.getListType().getTagClass(), listTagType)) {
            throw new IllegalArgumentException("Unexpected list type for " + name + " expected " + listTagType + " got " + listTag.getListType().getTagClass());
        }
        return listTag;
    }

    /**
     * Get a ListTag from the key-value based storage, if it is not the right type it will throw a ClassCastException and if the
     * listTagType doesn't match it will throw an IllegalArgumentException.
     * Note: LongArrayTag is used as an empty list in some version of Java edition, this method will return null instead
     * of throwing an exception for this case.
     *
     * @param name         the name to search for.
     * @param listTagType  the type of the list values to expect.
     * @param defaultValue the value to return if the name wasn't found.
     * @param <T>          the type of each tag in the list.
     * @param <V>          the boxed value type of each tag in the list.
     * @return the tag if it was found otherwise defaultValue, if the type is wrong it will throw an IllegalArgumentException.
     */
    @SuppressWarnings("unchecked")
    public <T extends Tag<V>, V> ListTag<T, V> getList(String name, Class<T> listTagType, ListTag<T, V> defaultValue) {
        Tag<?> tag = get(name, Tag.class);

        // Check if the tag is a list
        if (!(tag instanceof ListTag)) {
            // Check if the tag isn't present or if it's a long tag which we'll also count as not present.
            if (tag == null || tag instanceof LongArrayTag longArrayTag && longArrayTag.length() == 0){
                return defaultValue;
            }

            // Invalid list tag type
            throw new IllegalArgumentException("Unexpected tag type " + tag + ", expected ListTag.");
        }

        // Check the type of the list tag
        ListTag<T, V> listTag = (ListTag<T, V>) tag;
        if (listTag.getListType() != TagType.END && listTag.getListType() != null && !Objects.equals(listTag.getListType().getTagClass(), listTagType)) {
            throw new IllegalArgumentException("Unexpected list type for " + name + " expected " + listTagType + " got " + listTag.getListType().getTagClass());
        }
        return listTag;
    }

    /**
     * Get ListTag values from the key-value based storage, if it is not the right type it will throw a
     * ClassCastException and if the listTagType doesn't match it will throw an IllegalArgumentException.
     *
     * @param name        the name to search for.
     * @param listTagType the type of the list values to expect.
     * @param <T>         the type of each tag in the list.
     * @param <V>         the boxed value type of each tag in the list.
     * @return the tag if it was found otherwise it will throw an IllegalArgumentException.
     */
    public <T extends Tag<V>, V> List<V> getListValues(String name, Class<T> listTagType) {
        return getList(name, listTagType).toList();
    }

    /**
     * Get ListTag values from the key-value based storage, if it is not the right type it will throw a
     * ClassCastException and if the listTagType doesn't match it will throw an IllegalArgumentException.
     *
     * @param name         the name to search for.
     * @param listTagType  the type of the list values to expect.
     * @param defaultValue the value to return if the name wasn't found.
     * @param <T>          the type of each tag in the list.
     * @param <V>          the boxed value type of each tag in the list.
     * @return the tag if it was found otherwise defaultValue, if the type is wrong it will throw an IllegalArgumentException.
     */
    public <T extends Tag<V>, V> List<V> getListValues(String name, Class<T> listTagType, List<V> defaultValue) {
        ListTag<T, V> listTag = getList(name, listTagType, null);
        if (listTag == null) return defaultValue;
        return listTag.toList();
    }

    /**
     * Get a CompoundTag from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name the name to search for.
     * @return the tag if it was found otherwise null.
     */
    @Nullable
    public CompoundTag getCompound(String name) {
        return get(name, CompoundTag.class);
    }

    /**
     * Get a CompoundTag from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     *
     * @param name         the name to search for.
     * @param defaultValue the value to return if the value wasn't found.
     * @return the tag if it was found otherwise defaultValue.
     */
    public CompoundTag getCompound(String name, CompoundTag defaultValue) {
        CompoundTag value = getCompound(name);
        if (value == null) return defaultValue;
        return value;
    }

    /**
     * Get a CompoundTag from the key-value based storage, if it is not the right type it will throw a ClassCastException.
     * If the tag is not present, a new compound tag will be created.
     *
     * @param name the name to search for.
     * @return the fetched or created tag.
     */
    public CompoundTag getOrCreateCompound(String name) {
        CompoundTag tag = get(name, CompoundTag.class);

        // Create if absent
        if (tag == null) {
            tag = new CompoundTag();
            put(name, tag);
        }
        return tag;
    }

    /**
     * Get the value held by this tag it may not be modifiable if the true value backing the map is null.
     *
     * @return the value.
     */
    public Map<String, Tag<?>> getValue() {
        return value == null ? Collections.emptyMap() : value;
    }

    /**
     * Get the number of elements inside the CompoundTag.
     *
     * @return the number of elements inside the tag, 0 if none.
     */
    public int size() {
        return value == null ? 0 : value.size();
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<String, Tag<?>>> iterator() {
        return value == null ? Collections.emptyIterator() : value.entrySet().iterator();
    }
}
