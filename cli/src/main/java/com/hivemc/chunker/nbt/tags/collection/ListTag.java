package com.hivemc.chunker.nbt.tags.collection;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * Represents a list of a specific tag.
 *
 * @param <T> The type of tag in the list.
 * @param <V> The boxed value type held by the tags in the list.
 */
public class ListTag<T extends Tag<V>, V> extends Tag<List<T>> implements Iterable<T> {
    public static final int MAX_LIST_LENGTH = 16 * 16 * 4096; // Limit set to an entry per every block in a chunk
    @Nullable
    private TagType<T, V> listType;
    @Nullable
    private List<T> value;

    /**
     * Create a ListTag with an existing typed tag List.
     *
     * @param type  the tag type which is used for the list.
     * @param value the initial value for the tag.
     */
    public ListTag(@Nullable TagType<T, V> type, @Nullable List<T> value) {
        super();
        listType = type;
        this.value = value;
    }

    /**
     * Create a ListTag with atyped tag List.
     *
     * @param type the tag type which is used for the list.
     */
    public ListTag(@Nullable TagType<T, V> type) {
        this(type, null);
    }

    /**
     * Create a ListTag with an existing typed tag List.
     *
     * @param value the initial value for the tag.
     */
    @SuppressWarnings("unchecked")
    public ListTag(List<T> value) {
        this(value.isEmpty() ? null : (TagType<T, V>) value.get(0).getType(), value);
    }

    /**
     * Create a ListTag no value (null) and no listType (null).
     */
    public ListTag() {
        super();
    }

    /**
     * Create a ListTag with an existing typed value List.
     *
     * @param type   the tag type which is used for the list.
     * @param values the initial values to be transformed into tags.
     */
    public static <T extends Tag<V>, V> ListTag<T, V> fromValues(TagType<T, V> type, List<V> values) {
        // Turn the values into tags
        List<T> newList = new ArrayList<>(values.size());
        Function<V, T> tagConstructor = type.getValueConstructor();
        for (V value : values) {
            newList.add(tagConstructor.apply(value));
        }
        return new ListTag<>(type, newList);
    }

    @Override
    @NotNull
    @SuppressWarnings({"unchecked", "rawtypes", "RedundantCast"})
    public TagType<? extends Tag<List<T>>, ? super List<T>> getType() {
        return (TagType<? extends Tag<List<T>>, ? super List<T>>) (TagType) TagType.LIST;
    }

    @Override
    protected int valueHashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean valueEquals(List<T> boxedValue) {
        return Objects.equals(value, boxedValue) || value == null && boxedValue.isEmpty() || boxedValue == null && value.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean valueEquals(Tag<List<T>> tag) {
        return valueEquals(((ListTag<T, V>) tag).getValue());
    }

    @Override
    public List<T> getBoxedValue() {
        return getValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListTag<T, V> clone() {
        if (value != null) {
            List<T> copy = new ObjectArrayList<>(value.size());
            for (T entry : value) {
                copy.add((T) entry.clone());
            }
            return new ListTag<>(getListType(), copy);
        } else {
            return new ListTag<>(getListType(), null);
        }
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        writer.writeByte((listType == null ? TagType.END : listType).getId());
        writer.writeInt(value == null ? 0 : value.size());

        if (listType != null && value != null && listType != TagType.END) {
            for (T tag : value) {
                tag.encodeValue(writer);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void decodeValue(Reader reader) throws IOException {
        // Read tag id
        int tagId = reader.readUnsignedByte();
        listType = (TagType<T, V>) TagType.getById(tagId);

        // Validate length
        int length = reader.readInt();
        if (length < 0 || length > MAX_LIST_LENGTH)
            throw new IllegalArgumentException("Could not read list with length " + length);

        // Start reading
        if (listType != TagType.END && length > 0) {
            // Allocate array
            value = new ObjectArrayList<>(length);

            for (int i = 0; i < length; i++) {
                T tag = Objects.requireNonNull(Objects.requireNonNull(listType).getConstructor()).get();
                tag.decodeValue(reader);
                value.add(tag);
            }
        } else {
            // Always use END tag to indicate empty lists
            // Older versions sometimes use BYTE
            listType = (TagType<T, V>) TagType.END;
        }
    }

    @Override
    public String toSNBT() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        if (value != null) {
            for (T tag : value) {
                joiner.add(tag.toSNBT());
            }
        }
        return joiner.toString();
    }

    /**
     * Get the type which is used for this list.
     *
     * @return The TagType used for this list, may be null if the list isn't initialized.
     */
    @Nullable
    public TagType<T, V> getListType() {
        return listType;
    }

    /**
     * Get the value held by this tag, it may not be modifiable if the true value backing the list is null.
     *
     * @return the value.
     */
    public List<T> getValue() {
        return value == null ? Collections.emptyList() : value;
    }

    /**
     * Set the list held by this tag.
     *
     * @param listType the type of values which are held by the list.
     * @param value    the list containing values corresponding to the listType.
     */
    public void setValue(@Nullable TagType<T, V> listType, @Nullable List<T> value) {
        this.listType = listType;
        this.value = value;
    }

    /**
     * Check if the list contains a value.
     *
     * @param value the value which should be checked for.
     * @return true if the value was in the list.
     */
    public boolean contains(V value) {
        if (this.value == null || this.value.isEmpty()) return false; // Not possible
        for (T tag : this.value) {
            if (tag.valueEquals(value)) return true;
        }
        return false;
    }

    /**
     * Get the number of entries in the list.
     *
     * @return the number of entries.
     */
    public int size() {
        return value == null ? 0 : value.size();
    }

    /**
     * Add a value to the list tag, when the internal list isn't initialized it will use the type of this value.
     *
     * @param value the new value to add
     * @return whether the add was successful based on the backing list.
     */
    @SuppressWarnings("unchecked")
    public boolean add(T value) {
        if (this.value == null) {
            this.value = new ObjectArrayList<>(1);
            listType = (TagType<T, V>) value.getType();
        }
        return this.value.add(value);
    }

    /**
     * Set a specific index if present to a value.
     *
     * @param index the index to get.
     * @return the value in the list.
     * @throws IndexOutOfBoundsException if the index is not present in the list.
     */
    public T get(int index) {
        if (value == null) {
            throw new IndexOutOfBoundsException();
        }
        return value.get(index);
    }

    /**
     * Set a specific index if present to a value.
     *
     * @param index the index to set.
     * @param value the value to set.
     * @return the previous value before the item is replaced.
     * @throws IndexOutOfBoundsException if the index is not present in the list.
     */
    public T set(int index, T value) {
        if (this.value == null) {
            throw new IndexOutOfBoundsException();
        }
        return this.value.set(index, value);
    }

    /**
     * Convert the current tag list into a boxed value list.
     *
     * @return a new list of the boxed values, may be an unmodifiable empty list if the underlying value is empty or
     * null.
     */
    public List<V> toList() {
        if (value == null || value.isEmpty()) return Collections.emptyList();

        // Map each tag to the boxed value
        List<V> output = new ArrayList<>(value.size());
        for (T tag : value) {
            output.add(tag.getBoxedValue());
        }
        return output;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return value == null ? Collections.emptyIterator() : value.iterator();
    }
}
