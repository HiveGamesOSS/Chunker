package com.hivemc.chunker.conversion.encoding.java;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaLevelReader;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaLevelWriter;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * JavaEncoders contains the readers/writers to use for various versions. An encoder isn't required for every version
 * just when a different LevelReader / LevelWriter is needed.
 */
public class JavaEncoders {
    // Lookup used for turning java data version into the closest reader/writer
    private static final TreeMap<JavaDataVersion, JavaEncoder> ENCODER_LOOKUP = new TreeMap<>();

    static {
        // This is a list of encoders (not required for every version) but versions where format changes
        register(
                JavaDataVersion.V1_8_8,
                JavaLevelReader::new,
                JavaLevelWriter::new
        );
        register(
                JavaDataVersion.V1_11,
                com.hivemc.chunker.conversion.encoding.java.v1_11.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_11.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_13,
                com.hivemc.chunker.conversion.encoding.java.v1_13.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_13.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_14,
                com.hivemc.chunker.conversion.encoding.java.v1_14.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_14.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_15,
                com.hivemc.chunker.conversion.encoding.java.v1_15.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_15.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_16,
                com.hivemc.chunker.conversion.encoding.java.v1_16.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_16.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_17,
                com.hivemc.chunker.conversion.encoding.java.v1_17.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_17.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_18,
                com.hivemc.chunker.conversion.encoding.java.v1_18.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_18.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_19_3,
                com.hivemc.chunker.conversion.encoding.java.v1_19_3.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_19_3.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_20,
                com.hivemc.chunker.conversion.encoding.java.v1_20.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_20.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_20_3,
                com.hivemc.chunker.conversion.encoding.java.v1_20_3.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_20_3.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_20_5,
                com.hivemc.chunker.conversion.encoding.java.v1_20_5.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_20_5.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_21,
                com.hivemc.chunker.conversion.encoding.java.v1_21.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_21.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_21_2,
                com.hivemc.chunker.conversion.encoding.java.v1_21_2.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_21_2.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_21_4,
                com.hivemc.chunker.conversion.encoding.java.v1_21_4.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_21_4.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_21_6,
                com.hivemc.chunker.conversion.encoding.java.v1_21_6.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_21_6.writer.LevelWriter::new
        );
        register(
                JavaDataVersion.V1_21_9,
                com.hivemc.chunker.conversion.encoding.java.v1_21_9.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.java.v1_21_9.writer.LevelWriter::new
        );
    }

    /**
     * Register a new encoder to handle the version and above.
     *
     * @param dataVersion       the data version which is handled.
     * @param readerConstructor the constructor for creating the reader.
     * @param writerConstructor the constructor for creating the writer.
     */
    public static void register(JavaDataVersion dataVersion, JavaLevelReaderConstructor<?> readerConstructor, JavaLevelWriterConstructor<?> writerConstructor) {
        ENCODER_LOOKUP.put(dataVersion, new JavaEncoder(readerConstructor, writerConstructor));
    }

    /**
     * Get the oldest encoder for java.
     *
     * @return the oldest encoder.
     */
    public static JavaEncoder oldest() {
        return ENCODER_LOOKUP.firstEntry().getValue();
    }

    /**
     * Get the latest encoder for java.
     *
     * @return the latest encoder.
     */
    public static JavaEncoder latest() {
        return ENCODER_LOOKUP.lastEntry().getValue();
    }

    /**
     * Get a list of supported versions.
     *
     * @return a list of supported versions based on the JavaDataVersions.
     */
    public static Collection<Version> getSupportedVersions() {
        return JavaDataVersion.getVersions().stream().map(JavaDataVersion::getVersion).collect(Collectors.toList());
    }

    /**
     * Get the nearest encoder for a version.
     *
     * @param dataVersion the data version.
     * @return the encoder or the oldest if no version is found.
     */
    public static JavaEncoder getNearestEncoder(JavaDataVersion dataVersion) {
        Map.Entry<JavaDataVersion, JavaEncoder> entry = ENCODER_LOOKUP.floorEntry(dataVersion);

        // If the entry wasn't found use the latest version
        return entry == null ? oldest() : entry.getValue();
    }

    /**
     * Create a reader for an input world.
     *
     * @param directory the input directory with the world.
     * @param converter the converter instance to use for the reader.
     * @return a reader if it's possible to read the version otherwise empty if the version/format is unsupported.
     */
    public static Optional<JavaLevelReader> createReader(File directory, Converter converter) {
        Optional<JavaDataVersion> javaDataVersion = JavaDataVersion.detect(directory);

        // If no data version was found return empty
        if (javaDataVersion.isEmpty()) return Optional.empty();

        // Find the nearest encoder
        JavaDataVersion dataVersion = javaDataVersion.get();
        JavaEncoder nearest = getNearestEncoder(dataVersion);
        if (nearest == null) return Optional.empty();

        // Create the reader
        return Optional.ofNullable(nearest.readerConstructor().construct(directory, dataVersion.getVersion(), converter));
    }

    /**
     * Create a writer for a specific output version.
     *
     * @param directory the output directory for the world to be written to.
     * @param converter the converter instance to use for the writer.
     * @return a writer if it's possible to write the version otherwise empty if the version/format is unsupported.
     */
    public static Optional<JavaLevelWriter> createWriter(File directory, Version outputVersion, Converter converter) {
        // Find the nearest data version
        JavaDataVersion dataVersion = JavaDataVersion.getNearestVersion(outputVersion);
        if (dataVersion == null) return Optional.empty();

        // Find the nearest encoder
        JavaEncoder nearest = getNearestEncoder(dataVersion);
        if (nearest == null) return Optional.empty();

        // Create the writer
        return Optional.ofNullable(nearest.writerConstructor().construct(directory, dataVersion.getVersion(), converter));
    }

    /**
     * An encoder for a specific version.
     *
     * @param readerConstructor the constructor for LevelReaders.
     * @param writerConstructor the constructor for LevelWriters.
     */
    public record JavaEncoder(JavaLevelReaderConstructor<?> readerConstructor,
                              JavaLevelWriterConstructor<?> writerConstructor) {
    }
}
