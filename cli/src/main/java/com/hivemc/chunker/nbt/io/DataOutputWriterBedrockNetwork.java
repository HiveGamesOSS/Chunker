package com.hivemc.chunker.nbt.io;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Implements a DataOutput based writer which encodes using the Bedrock network format (little endian with VarInts).
 */
class DataOutputWriterBedrockNetwork extends DataOutputWriterLE {
    protected DataOutputWriterBedrockNetwork(DataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void writeShortPrefixedBytes(byte[] value) throws IOException {
        writeUnsignedVarLong(value.length);
        writeBytes(value);
    }

    @Override
    public void writeInt(int value) throws IOException {
        writeUnsignedVarLong(((value << 1) ^ (value >> 31)) & 0xFFFFFFFFL);
    }

    @Override
    public void writeLong(long value) throws IOException {
        writeUnsignedVarLong((value << 1) ^ (value >> 63));
    }

    @Override
    public void writeFloat(float value) throws IOException {
        super.writeInt(Float.floatToIntBits(value));
    }

    @Override
    public void writeDouble(double value) throws IOException {
        super.writeLong(Double.doubleToLongBits(value));
    }

    /**
     * Write an unsigned VarLong.
     *
     * @param value the value to write.
     * @throws IOException if it failed to write.
     */
    private void writeUnsignedVarLong(long value) throws IOException {
        while ((value & ~0x7FL) != 0) {
            writeByte((int) (value & 0x7F) | 0x80);
            value >>>= 7;
        }
        writeByte((int) value);
    }
}
