package com.hivemc.chunker.nbt.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.function.Function;

/**
 * Tests for reading/writing Bedrock data.
 */
public class BedrockReaderWriterTests extends ReaderWriterTestsBase {
    @Override
    public Function<DataOutput, Writer> getWriter() {
        return Writer::toBedrockWriter;
    }

    @Override
    public Function<DataInput, Reader> getReader() {
        return Reader::toBedrockReader;
    }
}
