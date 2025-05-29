package com.hivemc.chunker.conversion.encoding.bedrock;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockLevelReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockLevelWriter;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * BedrockEncoders contains the readers/writers to use for various versions. An encoder isn't required for every version
 * just when a different LevelReader / LevelWriter is needed.
 */
public class BedrockEncoders {
    // Lookup used for turning bedrock data version into the closest reader/writer
    private static final TreeMap<BedrockDataVersion, BedrockEncoder> ENCODER_LOOKUP = new TreeMap<>();

    static {
        register(
                BedrockDataVersion.V1_12_0,
                BedrockLevelReader::new,
                BedrockLevelWriter::new
        );
        register(
                BedrockDataVersion.V1_13_0,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_13.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_13.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_14_0,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_14.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_14.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_16_0,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_16.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_16.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_17_0,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_17.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_17.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_17_30,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_17_30.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_17_30.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_18_0,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_18.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_18.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_18_30,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_18_30.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_18_30.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_19_0,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_19.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_19.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_19_80,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_19_80.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_19_80.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_20_0,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_20.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_20.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_20_30,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_20_30.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_20_30.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_20_50,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_20_50.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_20_50.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_20_60,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_20_60.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_20_60.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_21_0,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_21_40,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21_40.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21_40.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_21_50,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21_50.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21_50.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_21_60,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21_60.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21_60.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_21_80,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21_80.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21_80.writer.LevelWriter::new
        );
        register(
                BedrockDataVersion.V1_21_90,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21_90.reader.LevelReader::new,
                com.hivemc.chunker.conversion.encoding.bedrock.v1_21_90.writer.LevelWriter::new
        );
    }

    /**
     * Register a new encoder to handle the version and above.
     *
     * @param dataVersion       the data version which is handled.
     * @param readerConstructor the constructor for creating the reader.
     * @param writerConstructor the constructor for creating the writer.
     */
    public static void register(BedrockDataVersion dataVersion, BedrockLevelReaderConstructor<?> readerConstructor, BedrockLevelWriterConstructor<?> writerConstructor) {
        ENCODER_LOOKUP.put(dataVersion, new BedrockEncoder(readerConstructor, writerConstructor));
    }

    /**
     * Get the oldest encoder for bedrock.
     *
     * @return the oldest encoder.
     */
    public static BedrockEncoder oldest() {
        return ENCODER_LOOKUP.firstEntry().getValue();
    }

    /**
     * Get the latest encoder for bedrock.
     *
     * @return the latest encoder.
     */
    public static BedrockEncoder latest() {
        return ENCODER_LOOKUP.lastEntry().getValue();
    }

    /**
     * Get a list of supported versions.
     *
     * @return a list of supported versions based on the BedrockDataVersions.
     */
    public static Collection<Version> getSupportedVersions() {
        return BedrockDataVersion.getVersions().stream().map(BedrockDataVersion::getVersion).collect(Collectors.toList());
    }

    /**
     * Get the nearest encoder for a version.
     *
     * @param dataVersion the data version.
     * @return the encoder or the oldest if no version is found.
     */
    public static BedrockEncoder getNearestEncoder(BedrockDataVersion dataVersion) {
        Map.Entry<BedrockDataVersion, BedrockEncoder> entry = ENCODER_LOOKUP.floorEntry(dataVersion);

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
    public static Optional<BedrockLevelReader> createReader(File directory, Converter converter) {
        Optional<BedrockDataVersion> bedrockDataVersion = BedrockDataVersion.detect(directory);

        // If no data version was found return empty
        if (bedrockDataVersion.isEmpty()) return Optional.empty();

        // Find the nearest encoder
        BedrockDataVersion dataVersion = bedrockDataVersion.get();
        BedrockEncoder nearest = getNearestEncoder(dataVersion);
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
    public static Optional<BedrockLevelWriter> createWriter(File directory, Version outputVersion, Converter converter) {
        // Find the nearest data version
        BedrockDataVersion dataVersion = BedrockDataVersion.getNearestVersion(outputVersion);
        if (dataVersion == null) return Optional.empty();

        // Find the nearest encoder
        BedrockEncoder nearest = getNearestEncoder(dataVersion);
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
    public record BedrockEncoder(BedrockLevelReaderConstructor<?> readerConstructor,
                                 BedrockLevelWriterConstructor<?> writerConstructor) {
    }
}
