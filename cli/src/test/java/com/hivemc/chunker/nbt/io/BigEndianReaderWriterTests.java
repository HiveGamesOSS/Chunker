package com.hivemc.chunker.nbt.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.function.Function;

/**
 * Tests for reading/writing big endian data.
 */
public class BigEndianReaderWriterTests extends ReaderWriterTestsBase {
    @Override
    public Function<DataOutput, Writer> getWriter() {
        return Writer::toBigEndianWriter;
    }

    @Override
    public Function<DataInput, Reader> getReader() {
        return Reader::toBigEndianReader;
    }
}
