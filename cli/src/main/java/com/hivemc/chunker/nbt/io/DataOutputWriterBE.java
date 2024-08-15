package com.hivemc.chunker.nbt.io;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Implements a DataOutput based writer which encodes using big endian.
 */
class DataOutputWriterBE implements Writer {
    private final DataOutput dataOutput;

    protected DataOutputWriterBE(DataOutput dataOutput) {
        this.dataOutput = dataOutput;
    }

    @Override
    public void writeShort(short value) throws IOException {
        dataOutput.writeShort(value);
    }

    @Override
    public void writeBytes(byte[] value) throws IOException {
        dataOutput.write(value);
    }

    @Override
    public void writeByte(byte value) throws IOException {
        dataOutput.write(value);
    }

    @Override
    public void writeByte(int value) throws IOException {
        dataOutput.writeByte(value);
    }

    @Override
    public void writeInt(int value) throws IOException {
        dataOutput.writeInt(value);
    }

    @Override
    public void writeUnsignedInt24(int value) throws IOException {
        dataOutput.writeByte((value >> 16));
        dataOutput.writeByte((value >> 8));
        dataOutput.writeByte((value));
    }

    @Override
    public void writeLong(long value) throws IOException {
        dataOutput.writeLong(value);
    }

    @Override
    public void writeFloat(float value) throws IOException {
        dataOutput.writeFloat(value);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        dataOutput.writeDouble(value);
    }
}
