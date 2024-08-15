package com.hivemc.chunker.nbt.io;

import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.IOException;

/**
 * Implements a DataInput based reader which decodes using big endian.
 */
class DataInputReaderBE implements Reader {
    private final DataInput dataInput;

    protected DataInputReaderBE(DataInput dataInput) {
        this.dataInput = dataInput;
    }

    @Override
    public short readShort() throws IOException {
        return dataInput.readShort();
    }

    @Override
    public void readBytes(byte @NotNull [] array) throws IOException {
        dataInput.readFully(array);
    }

    @Override
    public byte readByte() throws IOException {
        return dataInput.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return dataInput.readUnsignedByte();
    }

    @Override
    public int readInt() throws IOException {
        return dataInput.readInt();
    }

    @Override
    public int readUnsignedInt24() throws IOException {
        byte byte1 = dataInput.readByte();
        byte byte2 = dataInput.readByte();
        byte byte3 = dataInput.readByte();
        return (byte1 & 0xff) << 16 | (byte2 & 0xff) << 8 | byte3 & 0xff;
    }

    @Override
    public long readLong() throws IOException {
        return dataInput.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return dataInput.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return dataInput.readDouble();
    }
}
