package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents an Integer based NBT tag.
 */
public class IntTag extends Tag<Integer> {
    protected int value;

    /**
     * Create a IntTag with an existing Integer.
     *
     * @param value the initial value for the tag.
     */
    public IntTag(int value) {
        super();
        this.value = value;
    }

    /**
     * Create a IntTag with the default value 0.
     */
    public IntTag() {
        super();
    }

    @Override
    @NotNull
    public TagType<IntTag, Integer> getType() {
        return TagType.INT;
    }

    @Override
    public boolean valueEquals(Tag<Integer> tag) {
        return Objects.equals(value, (((IntTag) tag).getValue()));
    }

    @Override
    public Integer getBoxedValue() {
        return value;
    }

    @Override
    public IntTag clone() {
        return new IntTag(value);
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        writer.writeInt(value);
    }

    @Override
    public void decodeValue(Reader reader) throws IOException {
        value = reader.readInt();
    }

    @Override
    public String toSNBT() {
        return String.valueOf(value);
    }

    /**
     * Get the value held by this tag.
     *
     * @return the value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(int value) {
        this.value = value;
    }
}
