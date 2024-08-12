package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents a Double based NBT tag.
 */
public class DoubleTag extends Tag<Double> {
    protected double value;

    /**
     * Create a DoubleTag with an existing Double.
     *
     * @param value the initial value for the tag.
     */
    public DoubleTag(double value) {
        super();
        this.value = value;
    }

    /**
     * Create a DoubleTag with the default value 0.
     */
    public DoubleTag() {
        super();
    }

    @Override
    @NotNull
    public TagType<DoubleTag, Double> getType() {
        return TagType.DOUBLE;
    }

    @Override
    public boolean valueEquals(Tag<Double> tag) {
        return Objects.equals(value, (((DoubleTag) tag).getValue()));
    }

    @Override
    public Double getBoxedValue() {
        return value;
    }

    @Override
    public DoubleTag clone() {
        return new DoubleTag(value);
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        writer.writeDouble(value);
    }

    @Override
    public void decodeValue(Reader reader) throws IOException {
        value = reader.readDouble();
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
    public double getValue() {
        return value;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(double value) {
        this.value = value;
    }
}
