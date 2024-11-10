package com.hivemc.chunker.nbt.tags.primitive;

import com.google.common.escape.CharEscaperBuilder;
import com.google.common.escape.Escaper;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.util.ControlCodeEscaper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a String based NBT tag which is stored as a byte array to preserve encoding.
 */
public class StringTag extends Tag<String> {
    public static final Escaper CONTROL_CODE_ESCAPER = new ControlCodeEscaper();
    public static final int MAX_STRING_LENGTH = Short.MAX_VALUE;
    public static final Escaper SNBT_ESCAPER = new CharEscaperBuilder()
            .addEscape('"', "\\\"")
            .toEscaper();
    protected byte[] value;
    @Nullable
    protected String stringCache;

    /**
     * Create a StringTag with an existing String.
     *
     * @param value the initial value for the tag.
     */
    public StringTag(String value) {
        super();
        this.stringCache = value;
        this.value = value.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Create a StringTag with an existing byte[].
     *
     * @param value the initial value for the tag.
     */
    public StringTag(byte[] value) {
        super();
        this.stringCache = null;
        this.value = value;
    }

    /**
     * Create a StringTag with no value (null).
     */
    public StringTag() {
        super();
    }

    @Override
    @NotNull
    public TagType<StringTag, String> getType() {
        return TagType.STRING;
    }

    @Override
    public boolean valueEquals(Tag<String> tag) {
        return Arrays.equals(getByteArrayValue(), (((StringTag) tag).getByteArrayValue()));
    }

    @Override
    protected int valueHashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String getBoxedValue() {
        return getValue();
    }

    @Override
    public StringTag clone() {
        return new StringTag(value);
    }

    @Override
    public void encodeValue(Writer writer) throws IOException {
        writer.writeShortPrefixedBytes(value);
    }

    @Override
    public void decodeValue(Reader reader) throws IOException {
        stringCache = null;
        value = reader.readShortPrefixedBytes(MAX_STRING_LENGTH);
    }

    @Override
    public String toSNBT() {
        // Turn into a string but ensure we escape control code / quotes.
        return String.format("\"%s\"", SNBT_ESCAPER.escape(CONTROL_CODE_ESCAPER.escape(getBoxedValue())));
    }

    /**
     * Get the String value held by this tag.
     *
     * @return the value.
     */
    @Nullable
    public String getValue() {
        // Parse the string and cache it
        if (stringCache == null && value != null) {
            stringCache = new String(value, StandardCharsets.UTF_8);
        }
        return stringCache;
    }

    /**
     * Get the byte[] value held by this tag.
     *
     * @return the value.
     */
    public byte @Nullable [] getByteArrayValue() {
        return value;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(@Nullable String value) {
        this.stringCache = value;
        this.value = value != null ? value.getBytes(StandardCharsets.UTF_8) : null;
    }

    /**
     * Set the value held by this tag.
     *
     * @param value the new value to be set.
     */
    public void setValue(byte @Nullable [] value) {
        this.stringCache = null;
        this.value = value;
    }
}
