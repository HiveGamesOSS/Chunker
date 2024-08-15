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
 * Represents a Byte array NBT tag.
 */
public class ByteArrayTag extends Tag<byte[]> {
    public static final int MAX_ARRAY_LENGTH = 65536; // Unsigned Short MAX
    private byte @Nullable [] value;

    /**
     * Create a ByteArrayTag with an existing byte array.
     *
     * @param value the initial value for the tag.
     */
    public ByteArrayTag(byte @Nullable [] value) {
        super();
        this.value = value;
    }

    /**
     * Create a ByteArrayTag with no value (null).
     */
    public ByteArrayTag() {
        super();
    }

    @Override
    @NotNull
    public TagType<ByteArrayTag, byte[]> getType() {
        return TagType.BYTE_ARRAY;
    }

    @Override
    protected int valueHashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean valueEquals(byte[] boxedValue) {
        return Arrays.equals(value, boxedValue) || value == null && boxedValue.length == 0 || boxedValue == null && value.length == 0;
    }

    @Override
    public boolean valueEquals(Tag<byte[]> tag) {
        return valueEquals(((ByteArrayTag) tag).getValue());
    }

    @Override
    public byte[] getBoxedValue() {
        return getValue();
    }

    @Override
    public ByteArrayTag clone() {
        return new ByteArrayTag(value == null ? null : Arrays.copyOf(value, value.length));
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        if (value != null) {
            writer.writeInt(value.length);
            writer.writeBytes(value);
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
        value = new byte[length];
        reader.readBytes(value);
    }

    @Override
    public String toSNBT() {
        StringJoiner joiner = new StringJoiner(",", "[B;", "]");
        if (value != null) {
            for (byte value : value) {
                joiner.add(value + "b");
            }
        }
        return joiner.toString();
    }

    /**
     * Get the value held by this tag.
     *
     * @return the value.
     */
    public byte @Nullable [] getValue() {
        return value;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(byte @Nullable [] value) {
        this.value = value;
    }
}
