package com.hivemc.chunker.nbt.io;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Implements a DataOutput based writer which encodes using big endian and uses modified UTF-8 based Strings.
 */
class DataOutputWriterJava extends DataOutputWriterBE {
    protected DataOutputWriterJava(DataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void writeShortPrefixedByteString(@NotNull ByteString value) throws IOException {
        dataOutput.writeUTF(value.getString());
    }

    @Override
    public void writeString(@NotNull String value) throws IOException {
        dataOutput.writeUTF(value);
    }
}
