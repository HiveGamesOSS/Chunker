package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents a Byte based NBT tag.
 */
public class ByteTag extends Tag<Byte> {
    protected byte value;

    /**
     * Create a ByteTag with an existing Byte.
     *
     * @param value the initial value for the tag.
     */
    public ByteTag(byte value) {
        super();
        this.value = value;
    }

    /**
     * Create a ByteTag with the default value 0.
     */
    public ByteTag() {
        super();
    }

    @Override
    @NotNull
    public TagType<ByteTag, Byte> getType() {
        return TagType.BYTE;
    }

    @Override
    public boolean valueEquals(Tag<Byte> tag) {
        return Objects.equals(value, (((ByteTag) tag).getValue()));
    }

    @Override
    public Byte getBoxedValue() {
        return value;
    }

    @Override
    public ByteTag clone() {
        return new ByteTag(value);
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        writer.writeByte(value);
    }

    @Override
    public void decodeValue(Reader reader) throws IOException {
        value = reader.readByte();
    }

    @Override
    public String toSNBT() {
        return value + "b";
    }

    /**
     * Get the value held by this tag.
     *
     * @return the value.
     */
    public byte getValue() {
        return value;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(byte value) {
        this.value = value;
    }
}
