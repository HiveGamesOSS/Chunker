package com.hivemc.chunker.nbt.io;

import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.IOException;

/**
 * Implements a DataInput based reader which decodes using little endian.
 */
class DataInputReaderLE implements Reader {
    private final DataInput dataInput;

    protected DataInputReaderLE(DataInput dataInput) {
        this.dataInput = dataInput;
    }

    @Override
    public short readShort() throws IOException {
        byte byte1 = dataInput.readByte();
        byte byte2 = dataInput.readByte();
        return (short) (byte1 & 0xff | byte2 << 8);
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
        byte byte1 = dataInput.readByte();
        byte byte2 = dataInput.readByte();
        byte byte3 = dataInput.readByte();
        byte byte4 = dataInput.readByte();
        return (byte4 & 0xff) << 24 | (byte3 & 0xff) << 16 | (byte2 & 0xff) << 8 | byte1 & 0xff;
    }

    @Override
    public int readUnsignedInt24() throws IOException {
        byte byte1 = dataInput.readByte();
        byte byte2 = dataInput.readByte();
        byte byte3 = dataInput.readByte();
        return (byte3 & 0xff) << 16 | (byte2 & 0xff) << 8 | byte1 & 0xff;
    }

    @Override
    public long readLong() throws IOException {
        byte byte1 = dataInput.readByte();
        byte byte2 = dataInput.readByte();
        byte byte3 = dataInput.readByte();
        byte byte4 = dataInput.readByte();
        byte byte5 = dataInput.readByte();
        byte byte6 = dataInput.readByte();
        byte byte7 = dataInput.readByte();
        byte byte8 = dataInput.readByte();

        return (((long) byte8 & 0xff) << 56) |
                (((long) byte7 & 0xff) << 48) |
                (((long) byte6 & 0xff) << 40) |
                (((long) byte5 & 0xff) << 32) |
                (((long) byte4 & 0xff) << 24) |
                (((long) byte3 & 0xff) << 16) |
                (((long) byte2 & 0xff) << 8) |
                ((long) byte1 & 0xff);
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());  // Use our Int method to do this
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong()); // Use our Long method to do this
    }
}
