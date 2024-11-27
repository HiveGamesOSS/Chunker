package com.hivemc.chunker.nbt.tags.array;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Represents an Integer array NBT tag.
 */
public class IntArrayTag extends Tag<int[]> {
    public static final int MAX_ARRAY_LENGTH = 65536; // Unsigned Short MAX
    private int @Nullable [] value;

    /**
     * Create a IntArrayTag with an existing integer array.
     *
     * @param value the initial value for the tag.
     */
    public IntArrayTag(int @Nullable [] value) {
        super();
        this.value = value;
    }

    /**
     * Create a IntArrayTag with no value (null).
     */
    public IntArrayTag() {
        super();
    }

    @Override
    @NotNull
    public TagType<IntArrayTag, int[]> getType() {
        return TagType.INT_ARRAY;
    }

    @Override
    protected int valueHashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean valueEquals(int[] boxedValue) {
        return Arrays.equals(value, boxedValue) || value == null && boxedValue.length == 0 || boxedValue == null && value.length == 0;
    }

    @Override
    public boolean valueEquals(Tag<int[]> tag) {
        return valueEquals(((IntArrayTag) tag).getValue());
    }

    @Override
    public int[] getBoxedValue() {
        return getValue();
    }

    @Override
    public IntArrayTag clone() {
        return new IntArrayTag(value == null ? null : Arrays.copyOf(value, value.length));
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        if (value != null) {
            writer.writeInt(value.length);
            for (int entry : value) {
                writer.writeInt(entry);
            }
        } else {
            writer.writeInt(0);
        }
    }

    @Override
    public void decodeValue(Reader reader) throws IOException {
        int length = reader.readInt();
        if (length < 0 || length > MAX_ARRAY_LENGTH)
            throw new IllegalArgumentException("Could not read array with length " + length);

        // Create the array and read it
        value = new int[length];
        for (int i = 0; i < length; i++) {
            value[i] = reader.readInt();
        }
    }

    @Override
    public String toSNBT() {
        StringJoiner joiner = new StringJoiner(",", "[I;", "]");
        if (value != null) {
            for (int value : value) {
                joiner.add(String.valueOf(value));
            }
        }
        return joiner.toString();
    }

    /**
     * Get the value held by this tag.
     *
     * @return the value.
     */
    public int @Nullable [] getValue() {
        return value;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(int @Nullable [] value) {
        this.value = value;
    }

    /**
     * Get the number of entries in the array.
     *
     * @return the number of entries.
     */
    public int length() {
        return value == null ? 0 : value.length;
    }
}
