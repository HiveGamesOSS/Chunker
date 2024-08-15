package com.hivemc.chunker.conversion.java;

import com.hivemc.chunker.conversion.bedrock.resolver.MockConverter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.JavaDataVersion;
import com.hivemc.chunker.conversion.encoding.java.JavaEncoders;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaChunkReader;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaColumnReader;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaLevelReader;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaWorldReader;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaChunkWriter;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaColumnWriter;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaLevelWriter;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests to ensure Java encoders correctly inherit from the previous.
 */
public class JavaEncodersTests {
    public static Collection<JavaDataVersion> getJavaDataVersions() {
        return JavaDataVersion.getVersions();
    }

    @ParameterizedTest
    @MethodSource("getJavaDataVersions")
    public void ensureJavaDataVersionSupported(JavaDataVersion dataVersion) {
        JavaEncoders.JavaEncoder javaEncoder = JavaEncoders.getNearestEncoder(dataVersion);
        assertNotNull(javaEncoder);
    }

    @Test
    public void validateReaderInheritance() {
        MockConverter mockConverter = new MockConverter(null);

        // JavaEncoders should inherit from the previous, this ensures that fallback behaviour can be implemented
        Class<? extends JavaLevelReader> lastLevelReader = JavaLevelReader.class;
        Class<? extends JavaWorldReader> lastWorldReader = JavaWorldReader.class;
        Class<? extends JavaColumnReader> lastColumnReader = JavaColumnReader.class;
        Class<? extends JavaChunkReader> lastChunkReader = JavaChunkReader.class;

        // Loop through every data version
        for (JavaDataVersion dataVersion : getJavaDataVersions()) {
            Version version = dataVersion.getVersion();
            JavaEncoders.JavaEncoder javaEncoder = JavaEncoders.getNearestEncoder(dataVersion);
            if (javaEncoder == null) continue;

            // Check level reader
            JavaLevelReader levelReader = javaEncoder.readerConstructor().construct(null, version, mockConverter);
            assertInstanceOf(lastLevelReader, levelReader, "LevelReader " + levelReader.getClass() + " should extend " + lastLevelReader);
            lastLevelReader = levelReader.getClass();

            // Check world reader
            JavaWorldReader worldReader = levelReader.createWorldReader(null, null);
            assertInstanceOf(lastWorldReader, worldReader, "WorldReader " + worldReader.getClass() + " should extend " + lastWorldReader);
            lastWorldReader = worldReader.getClass();

            // Check column reader
            JavaColumnReader columnReader = worldReader.createColumnReader(null, null);
            assertInstanceOf(lastColumnReader, columnReader, "ColumnReader " + columnReader.getClass() + " should extend " + lastColumnReader);
            lastColumnReader = columnReader.getClass();

            // Check chunk reader
            JavaChunkReader chunkReader = columnReader.createChunkReader(null, null);
            assertInstanceOf(lastChunkReader, chunkReader, "ChunkReader " + chunkReader.getClass() + " should extend " + lastChunkReader);
            lastChunkReader = chunkReader.getClass();

        }
    }

    @Test
    public void validateWriterInheritance() {
        MockConverter mockConverter = new MockConverter(null);

        // JavaEncoders should inherit from the previous, this ensures that fallback behaviour can be implemented
        Class<? extends JavaLevelWriter> lastLevelWriter = JavaLevelWriter.class;
        Class<? extends JavaWorldWriter> lastWorldWriter = JavaWorldWriter.class;
        Class<? extends JavaColumnWriter> lastColumnWriter = JavaColumnWriter.class;
        Class<? extends JavaChunkWriter> lastChunkWriter = JavaChunkWriter.class;

        // Loop through every data version
        for (JavaDataVersion dataVersion : getJavaDataVersions()) {
            Version version = dataVersion.getVersion();
            JavaEncoders.JavaEncoder javaEncoder = JavaEncoders.getNearestEncoder(dataVersion);
            if (javaEncoder == null) continue;

            // Check level reader
            JavaLevelWriter levelWriter = javaEncoder.writerConstructor().construct(null, version, mockConverter);
            assertInstanceOf(lastLevelWriter, levelWriter, "LevelWriter " + levelWriter.getClass() + " should extend " + lastLevelWriter);
            lastLevelWriter = levelWriter.getClass();

            // Check world reader
            JavaWorldWriter worldWriter = levelWriter.createWorldWriter();
            assertInstanceOf(lastWorldWriter, worldWriter, "WorldWriter " + worldWriter.getClass() + " should extend " + lastWorldWriter);
            lastWorldWriter = worldWriter.getClass();

            // Check column reader
            JavaColumnWriter columnWriter = worldWriter.createColumnWriter(null);
            assertInstanceOf(lastColumnWriter, columnWriter, "ColumnWriter " + columnWriter.getClass() + " should extend " + lastColumnWriter);
            lastColumnWriter = columnWriter.getClass();

            // Check chunk reader
            JavaChunkWriter chunkWriter = columnWriter.createChunkWriter(null);
            assertInstanceOf(lastChunkWriter, chunkWriter, "ChunkWriter " + chunkWriter.getClass() + " should extend " + lastChunkWriter);
            lastChunkWriter = chunkWriter.getClass();

        }
    }
}
