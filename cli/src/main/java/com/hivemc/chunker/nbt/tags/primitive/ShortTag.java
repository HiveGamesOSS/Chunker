package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents a Short based NBT tag.
 */
public class ShortTag extends Tag<Short> {
    protected short value;

    /**
     * Create a ShortTag with an existing Short.
     *
     * @param value the initial value for the tag.
     */
    public ShortTag(short value) {
        super();
        this.value = value;
    }

    /**
     * Create a ShortTag with the default value 0.
     */
    public ShortTag() {
        super();
    }

    @Override
    @NotNull
    public TagType<ShortTag, Short> getType() {
        return TagType.SHORT;
    }

    @Override
    public boolean valueEquals(Tag<Short> tag) {
        return Objects.equals(value, (((ShortTag) tag).getValue()));
    }

    @Override
    public Short getBoxedValue() {
        return value;
    }

    @Override
    public ShortTag clone() {
        return new ShortTag(value);
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        writer.writeShort(value);
    }

    @Override
    public void decodeValue(Reader reader) throws IOException {
        value = reader.readShort();
    }

    @Override
    public String toSNBT() {
        return value + "s";
    }

    /**
     * Get the value held by this tag.
     *
     * @return the value.
     */
    public short getValue() {
        return value;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(short value) {
        this.value = value;
    }
}
