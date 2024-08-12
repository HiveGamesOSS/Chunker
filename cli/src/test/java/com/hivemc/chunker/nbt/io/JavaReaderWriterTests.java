package com.hivemc.chunker.nbt.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.function.Function;

/**
 * Tests for reading/writing Java data.
 */
public class JavaReaderWriterTests extends ReaderWriterTestsBase {
    @Override
    public Function<DataOutput, Writer> getWriter() {
        return Writer::toJavaWriter;
    }

    @Override
    public Function<DataInput, Reader> getReader() {
        return Reader::toJavaReader;
    }
}
