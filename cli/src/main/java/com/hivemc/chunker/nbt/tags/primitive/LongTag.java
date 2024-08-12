package com.hivemc.chunker.nbt.tags.primitive;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents a Long based NBT tag.
 */
public class LongTag extends Tag<Long> {
    protected long value;

    /**
     * Create a LongTag with an existing Long.
     *
     * @param value the initial value for the tag.
     */
    public LongTag(long value) {
        super();
        this.value = value;
    }

    /**
     * Create a LongTag with the default value 0.
     */
    public LongTag() {
        super();
    }

    @Override
    @NotNull
    public TagType<LongTag, Long> getType() {
        return TagType.LONG;
    }

    @Override
    public boolean valueEquals(Tag<Long> tag) {
        return Objects.equals(value, (((LongTag) tag).getValue()));
    }

    @Override
    public Long getBoxedValue() {
        return value;
    }

    @Override
    public LongTag clone() {
        return new LongTag(value);
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        writer.writeLong(value);
    }

    @Override
    public void decodeValue(Reader reader) throws IOException {
        value = reader.readLong();
    }

    @Override
    public String toSNBT() {
        return value + "l";
    }

    /**
     * Get the value held by this tag.
     *
     * @return the value.
     */
    public long getValue() {
        return value;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(long value) {
        this.value = value;
    }
}
