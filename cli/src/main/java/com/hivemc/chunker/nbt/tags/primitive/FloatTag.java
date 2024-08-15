package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents a Float based NBT tag.
 */
public class FloatTag extends Tag<Float> {
    protected float value;

    /**
     * Create a FloatTag with an existing Float.
     *
     * @param value the initial value for the tag.
     */
    public FloatTag(float value) {
        super();
        this.value = value;
    }

    /**
     * Create a FloatTag with the default value 0.
     */
    public FloatTag() {
        super();
    }

    @Override
    @NotNull
    public TagType<FloatTag, Float> getType() {
        return TagType.FLOAT;
    }

    @Override
    public boolean valueEquals(Tag<Float> tag) {
        return Objects.equals(value, (((FloatTag) tag).getValue()));
    }

    @Override
    public Float getBoxedValue() {
        return value;
    }

    @Override
    public FloatTag clone() {
        return new FloatTag(value);
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        writer.writeFloat(value);
    }

    @Override
    public void decodeValue(Reader reader) throws IOException {
        value = reader.readFloat();
    }

    @Override
    public String toSNBT() {
        return value + "f";
    }

    /**
     * Get the value held by this tag.
     *
     * @return the value.
     */
    public float getValue() {
        return value;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(float value) {
        this.value = value;
    }
}
