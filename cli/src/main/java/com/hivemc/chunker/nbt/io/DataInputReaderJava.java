package com.hivemc.chunker.nbt.io;

import org.jspecify.annotations.NonNull;

import java.io.DataInput;
import java.io.IOException;

/**
 * Implements a DataOutput based reader which decodes using big endian and uses modified UTF-8 based Strings.
 */
class DataInputReaderJava extends DataInputReaderBE {
    protected DataInputReaderJava(DataInput dataInput) {
        super(dataInput);
    }

    @Override
    public ByteString readShortPrefixedByteString(int maxLength) throws IOException {
        // We have to check the length afterward since it uses Java's readUTF
        String str = dataInput.readUTF();
        if (str.length() > maxLength) {
            throw new IllegalArgumentException("Could not read String with length " + str.length());
        }
        return new ByteString(str);
    }

    @Override
    public @NonNull String readString(int maxLength) throws IOException {
        // We have to check the length afterward since it uses Java's readUTF
        String str = dataInput.readUTF();
        if (str.length() > maxLength) {
            throw new IllegalArgumentException("Could not read String with length " + str.length());
        }
        return str;
    }
}
