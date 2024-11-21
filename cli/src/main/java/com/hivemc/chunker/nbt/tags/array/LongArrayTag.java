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
 * Represents a Long array NBT tag.
 */
public class LongArrayTag extends Tag<long[]> {
    public static final int MAX_ARRAY_LENGTH = 65536; // Unsigned Short MAX
    private long @Nullable [] value;

    /**
     * Create a LongArrayTag with an existing long array.
     *
     * @param value the initial value for the tag.
     */
    public LongArrayTag(long @Nullable [] value) {
        super();
        this.value = value;
    }

    /**
     * Create a LongArrayTag with no value (null).
     */
    public LongArrayTag() {
        super();
    }

    @Override
    @NotNull
    public TagType<LongArrayTag, long[]> getType() {
        return TagType.LONG_ARRAY;
    }

    @Override
    protected int valueHashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean valueEquals(long[] boxedValue) {
        return Arrays.equals(value, boxedValue) || value == null && boxedValue.length == 0 || boxedValue == null && value.length == 0;
    }

    @Override
    public boolean valueEquals(Tag<long[]> tag) {
        return valueEquals(((LongArrayTag) tag).getValue());
    }

    @Override
    public long[] getBoxedValue() {
        return getValue();
    }

    @Override
    public LongArrayTag clone() {
        return new LongArrayTag(value == null ? null : Arrays.copyOf(value, value.length));
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        if (value != null) {
            writer.writeInt(value.length);
            for (long entry : value) {
                writer.writeLong(entry);
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
        value = new long[length];
        for (int i = 0; i < length; i++) {
            value[i] = reader.readLong();
        }
    }

    @Override
    public String toSNBT() {
        StringJoiner joiner = new StringJoiner(",", "[L;", "]");
        if (value != null) {
            for (long value : value) {
                joiner.add(value + "l");
            }
        }
        return joiner.toString();
    }

    /**
     * Get the value held by this tag.
     *
     * @return the value.
     */
    public long @Nullable [] getValue() {
        return value;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(long @Nullable [] value) {
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
