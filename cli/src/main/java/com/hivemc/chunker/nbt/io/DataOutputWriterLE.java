package com.hivemc.chunker.nbt.io;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Implements a DataOutput based writer which encodes using little endian.
 */
class DataOutputWriterLE implements Writer {
    private final DataOutput dataOutput;

    protected DataOutputWriterLE(DataOutput dataOutput) {
        this.dataOutput = dataOutput;
    }

    @Override
    public void writeShort(short value) throws IOException {
        dataOutput.writeByte((value));
        dataOutput.writeByte((value >> 8));
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
        dataOutput.writeByte((value));
        dataOutput.writeByte((value >> 8));
        dataOutput.writeByte((value >> 16));
        dataOutput.writeByte((value >> 24));
    }

    @Override
    public void writeUnsignedInt24(int value) throws IOException {
        dataOutput.writeByte((value));
        dataOutput.writeByte((value >> 8));
        dataOutput.writeByte((value >> 16));
    }

    @Override
    public void writeLong(long value) throws IOException {
        dataOutput.writeByte((int) (value));
        dataOutput.writeByte((int) (value >> 8));
        dataOutput.writeByte((int) (value >> 16));
        dataOutput.writeByte((int) (value >> 24));
        dataOutput.writeByte((int) (value >> 32));
        dataOutput.writeByte((int) (value >> 40));
        dataOutput.writeByte((int) (value >> 48));
        dataOutput.writeByte((int) (value >> 56));
    }

    @Override
    public void writeFloat(float value) throws IOException {
        writeInt(Float.floatToIntBits(value));
    }

    @Override
    public void writeDouble(double value) throws IOException {
        writeLong(Double.doubleToLongBits(value));
    }
}
