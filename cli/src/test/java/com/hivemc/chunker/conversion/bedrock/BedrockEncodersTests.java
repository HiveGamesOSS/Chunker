package com.hivemc.chunker.conversion.bedrock;

import com.hivemc.chunker.conversion.bedrock.resolver.MockConverter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.BedrockDataVersion;
import com.hivemc.chunker.conversion.encoding.bedrock.BedrockEncoders;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockChunkReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockColumnReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockLevelReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockWorldReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockChunkWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockColumnWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockLevelWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockWorldWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests to ensure Bedrock encoders correctly inherit from the previous.
 */
public class BedrockEncodersTests {
    public static Collection<BedrockDataVersion> getBedrockDataVersions() {
        return BedrockDataVersion.getVersions();
    }

    @ParameterizedTest
    @MethodSource("getBedrockDataVersions")
    public void ensureBedrockDataVersionSupported(BedrockDataVersion dataVersion) {
        BedrockEncoders.BedrockEncoder bedrockEncoder = BedrockEncoders.getNearestEncoder(dataVersion);
        assertNotNull(bedrockEncoder);
    }

    @Test
    public void validateReaderInheritance() {
        MockConverter mockConverter = new MockConverter(null);

        // BedrockEncoders should inherit from the previous, this ensures that fallback behaviour can be implemented
        Class<? extends BedrockLevelReader> lastLevelReader = BedrockLevelReader.class;
        Class<? extends BedrockWorldReader> lastWorldReader = BedrockWorldReader.class;
        Class<? extends BedrockColumnReader> lastColumnReader = BedrockColumnReader.class;
        Class<? extends BedrockChunkReader> lastChunkReader = BedrockChunkReader.class;

        // Loop through every data version
        for (BedrockDataVersion dataVersion : getBedrockDataVersions()) {
            Version version = dataVersion.getVersion();
            BedrockEncoders.BedrockEncoder bedrockEncoder = BedrockEncoders.getNearestEncoder(dataVersion);
            if (bedrockEncoder == null) continue;

            // Check level reader
            BedrockLevelReader levelReader = bedrockEncoder.readerConstructor().construct(null, version, mockConverter);
            assertInstanceOf(lastLevelReader, levelReader, "LevelReader " + levelReader.getClass() + " should extend " + lastLevelReader);
            lastLevelReader = levelReader.getClass();

            // Check world reader
            BedrockWorldReader worldReader = levelReader.createWorldReader(null, null);
            assertInstanceOf(lastWorldReader, worldReader, "WorldReader " + worldReader.getClass() + " should extend " + lastWorldReader);
            lastWorldReader = worldReader.getClass();

            // Check column reader
            BedrockColumnReader columnReader = worldReader.createColumnReader(null);
            assertInstanceOf(lastColumnReader, columnReader, "ColumnReader " + columnReader.getClass() + " should extend " + lastColumnReader);
            lastColumnReader = columnReader.getClass();

            // Check chunk reader
            BedrockChunkReader chunkReader = columnReader.createChunkReader(null);
            assertInstanceOf(lastChunkReader, chunkReader, "ChunkReader " + chunkReader.getClass() + " should extend " + lastChunkReader);
            lastChunkReader = chunkReader.getClass();

        }
    }

    @Test
    public void validateWriterInheritance() {
        MockConverter mockConverter = new MockConverter(null);

        // BedrockEncoders should inherit from the previous, this ensures that fallback behaviour can be implemented
        Class<? extends BedrockLevelWriter> lastLevelWriter = BedrockLevelWriter.class;
        Class<? extends BedrockWorldWriter> lastWorldWriter = BedrockWorldWriter.class;
        Class<? extends BedrockColumnWriter> lastColumnWriter = BedrockColumnWriter.class;
        Class<? extends BedrockChunkWriter> lastChunkWriter = BedrockChunkWriter.class;

        // Loop through every data version
        for (BedrockDataVersion dataVersion : getBedrockDataVersions()) {
            Version version = dataVersion.getVersion();
            BedrockEncoders.BedrockEncoder bedrockEncoder = BedrockEncoders.getNearestEncoder(dataVersion);
            if (bedrockEncoder == null) continue;

            // Check level reader
            BedrockLevelWriter levelWriter = bedrockEncoder.writerConstructor().construct(null, version, mockConverter);
            assertInstanceOf(lastLevelWriter, levelWriter, "LevelWriter " + levelWriter.getClass() + " should extend " + lastLevelWriter);
            lastLevelWriter = levelWriter.getClass();

            // Check world reader
            BedrockWorldWriter worldWriter = levelWriter.createWorldWriter();
            assertInstanceOf(lastWorldWriter, worldWriter, "WorldWriter " + worldWriter.getClass() + " should extend " + lastWorldWriter);
            lastWorldWriter = worldWriter.getClass();

            // Check column reader
            BedrockColumnWriter columnWriter = worldWriter.createColumnWriter(null);
            assertInstanceOf(lastColumnWriter, columnWriter, "ColumnWriter " + columnWriter.getClass() + " should extend " + lastColumnWriter);
            lastColumnWriter = columnWriter.getClass();

            // Check chunk reader
            BedrockChunkWriter chunkWriter = columnWriter.createChunkWriter(null);
            assertInstanceOf(lastChunkWriter, chunkWriter, "ChunkWriter " + chunkWriter.getClass() + " should extend " + lastChunkWriter);
            lastChunkWriter = chunkWriter.getClass();

        }
    }
}
