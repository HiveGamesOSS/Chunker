package com.hivemc.chunker.nbt.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.function.Function;

/**
 * Tests for reading/writing little endian data.
 */
public class LittleEndianReaderWriterTests extends ReaderWriterTestsBase {
    @Override
    public Function<DataOutput, Writer> getWriter() {
        return Writer::toLittleEndianWriter;
    }

    @Override
    public Function<DataInput, Reader> getReader() {
        return Reader::toLittleEndianReader;
    }
}
