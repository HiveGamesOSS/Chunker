package com.hivemc.chunker.nbt;

import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.array.ByteArrayTag;
import com.hivemc.chunker.nbt.tags.array.IntArrayTag;
import com.hivemc.chunker.nbt.tags.array.LongArrayTag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Class holding all the different types of NBT tags with a mapping to class and constructor for serialization.
 */
public class TagType<T extends Tag<U>, U> {
    // Storage of all the types
    private static final List<TagType<?, ?>> VALUES = new ArrayList<>(13);

    // Valid tags which are supported
    public static final TagType<Tag<Void>, Void> END = new TagType<>(null, null, null);
    public static final TagType<ByteTag, Byte> BYTE = new TagType<>(ByteTag.class, ByteTag::new, ByteTag::new);
    public static final TagType<ShortTag, Short> SHORT = new TagType<>(ShortTag.class, ShortTag::new, ShortTag::new);
    public static final TagType<IntTag, Integer> INT = new TagType<>(IntTag.class, IntTag::new, IntTag::new);
    public static final TagType<LongTag, Long> LONG = new TagType<>(LongTag.class, LongTag::new, LongTag::new);
    public static final TagType<FloatTag, Float> FLOAT = new TagType<>(FloatTag.class, FloatTag::new, FloatTag::new);
    public static final TagType<DoubleTag, Double> DOUBLE = new TagType<>(DoubleTag.class, DoubleTag::new, DoubleTag::new);
    public static final TagType<ByteArrayTag, byte[]> BYTE_ARRAY = new TagType<>(ByteArrayTag.class, ByteArrayTag::new, ByteArrayTag::new);
    public static final TagType<StringTag, String> STRING = new TagType<>(StringTag.class, StringTag::new, StringTag::new);
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final TagType<ListTag<Tag<Object>, Object>, List<Tag<Object>>> LIST = new TagType<>((Class<ListTag<Tag<Object>, Object>>) (Class) ListTag.class, ListTag::new, ListTag::new);
    public static final TagType<CompoundTag, Map<String, Tag<?>>> COMPOUND = new TagType<>(CompoundTag.class, CompoundTag::new, CompoundTag::new);
    public static final TagType<IntArrayTag, int[]> INT_ARRAY = new TagType<>(IntArrayTag.class, IntArrayTag::new, IntArrayTag::new);
    public static final TagType<LongArrayTag, long[]> LONG_ARRAY = new TagType<>(LongArrayTag.class, LongArrayTag::new, LongArrayTag::new);

    // Each tag type stores useful class info for construction
    private final Class<T> tagClass;
    private final Supplier<T> constructor;
    private final Function<U, T> valueConstructor;
    private final int ordinal;

    private TagType(Class<T> tagClass, Supplier<T> constructor, Function<U, T> valueConstructor) {
        this.tagClass = tagClass;
        this.constructor = constructor;
        this.valueConstructor = valueConstructor;

        // Add to values
        ordinal = VALUES.size();
        VALUES.add(this);
    }

    /**
     * Get the class which corresponds to this tag.
     *
     * @return the class or null if there is no mapping.
     */
    @Nullable
    public Class<T> getTagClass() {
        return tagClass;
    }

    /**
     * Get the constructor which can be constructed to decode this tag.
     *
     * @return the constructor or null if there is no mapping.
     */
    @Nullable
    public Supplier<T> getConstructor() {
        return constructor;
    }

    /**
     * Get the constructor which turns a raw type into the tag.
     *
     * @return the constructor or null if there is no mapping.
     */
    public Function<U, T> getValueConstructor() {
        return valueConstructor;
    }

    /**
     * Get the ID for this tag type.
     *
     * @return the id based on the ordinal in the enum.
     */
    public int getId() {
        return ordinal;
    }

    /**
     * Get a tag from an integer ID.
     *
     * @param id the ID of the tag to look up.
     * @return the tag if it was found otherwise an IllegalArgumentException.
     */
    public static TagType<?, ?> getById(int id) {
        if (id < 0 || id >= VALUES.size()) throw new IllegalArgumentException("Unknown tag type " + id);
        return VALUES.get(id);
    }

    /**
     * Get a copy of all the valid tag types.
     *
     * @return a new array of the values.
     */
    public static TagType<?, ?>[] values() {
        return VALUES.toArray(TagType[]::new);
    }
}
