package com.hivemc.chunker.conversion.integration;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.hivemc.chunker.conversion.WorldConverter;
import com.hivemc.chunker.conversion.encoding.EncodingType;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.reader.LevelReader;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.scheduling.task.TrackedTask;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests to check various combinations of conversions, it checks if the level.dat / if exceptions were reported.
 * The test runs a small sample by default (maven), but runs every combination when run otherwise using the tag
 * "LongRunning" to indicate this.
 */
@Execution(ExecutionMode.CONCURRENT)
public class WorldConversionIntegrationTests {
    public static List<String> getWorldNames() {
        return List.of(
                "JAVA_1_20_5",
                "JAVA_1_19_4",
                "JAVA_1_15_2",
                "JAVA_1_16_5",
                "JAVA_1_12_2",
                "BEDROCK_R20_80",
                "BEDROCK_R19_30",
                "BEDROCK_R18_30",
                "BEDROCK_R12"
        );
    }

    public static List<Arguments> getAllWorldConversions() {
        List<Arguments> tests = new ArrayList<>();
        for (String worldName : getWorldNames()) {
            for (EncodingType encodingType : EncodingType.getWriteableTypes()) {
                if (encodingType.isInternal()) continue; // Don't use preview/settings
                for (Version version : encodingType.getSupportedVersions()) {
                    // Add each version
                    tests.add(Arguments.of(worldName, encodingType, version));
                }
            }
        }
        return tests;
    }

    public static List<Arguments> getSampleWorldConversions() {
        List<Arguments> tests = new ArrayList<>();
        for (String worldName : getWorldNames()) {
            for (EncodingType encodingType : EncodingType.getWriteableTypes()) {
                if (encodingType.isInternal()) continue; // Don't use preview/settings

                // Grab the minimum / maximum version (only test the newest and oldest)
                Set<Version> sampled = new HashSet<>();
                Optional<Version> min = encodingType.getSupportedVersions().stream().min(Version::compareTo);
                min.ifPresent(sampled::add);
                Optional<Version> max = encodingType.getSupportedVersions().stream().max(Version::compareTo);
                max.ifPresent(sampled::add);

                // Loop through sampled
                for (Version version : sampled) {
                    // Add each version
                    tests.add(Arguments.of(worldName, encodingType, version));
                }
            }
        }
        return tests;
    }

    public static void remove(Path path) throws IOException {
        // Walk the files and delete in reverse order to ensure directories are empty
        try (Stream<Path> pathStream = Files.walk(path)) {
            pathStream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    public static Path unzip(URL inputResource) throws IOException {
        // Use a temp folder
        Path output = tempFolder();

        // Unzip the resource
        try (InputStream inputStream = inputResource.openStream();
             ZipInputStream zipIn = new ZipInputStream(inputStream)) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                Path filePath = output.resolve(entry.getName()).normalize();
                if (!filePath.startsWith(output)) {
                    throw new IllegalArgumentException("Invalid path " + entry.getName());
                }

                // Create the directory otherwise copy the file
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    try (OutputStream out = Files.newOutputStream(filePath)) {
                        ByteStreams.copy(zipIn, out);
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        return output;
    }

    public static Path tempFolder() throws IOException {
        return Files.createTempDirectory("resource");
    }

    @ParameterizedTest
    @MethodSource("getSampleWorldConversions")
    public void testWorldConversionSample(String inputWorldName, EncodingType outputType, Version outputVersion) throws IOException {
        URL worldZip = Resources.getResource("integration/worlds/" + inputWorldName + ".zip");
        Path unzipped = unzip(worldZip);
        Path output = tempFolder();
        try {
            // Create a new world converter for our world
            WorldConverter converter = new WorldConverter(UUID.randomUUID()) {
                @Override
                public void logMissingMapping(MissingMappingType type, String identifier) {
                    // Don't log missing mappings for our tests
                }
            };
            convertWorld(converter, unzipped, output, outputType, outputVersion);

            // Assert that no errors happened
            assertFalse(converter.isExceptions());

            // Assert that world data was written
            assertTrue(output.resolve("level.dat").toFile().exists());
        } finally {
            remove(unzipped);
            remove(output);
        }
    }

    @Tag("LongRunning")
    @ParameterizedTest
    @MethodSource("getAllWorldConversions")
    public void testEveryWorldConversion(String inputWorldName, EncodingType outputType, Version outputVersion) throws IOException {
        URL worldZip = Resources.getResource("integration/worlds/" + inputWorldName + ".zip");
        Path unzipped = unzip(worldZip);
        Path output = tempFolder();
        try {
            // Create a new world converter for our world
            WorldConverter converter = new WorldConverter(UUID.randomUUID()) {
                @Override
                public void logMissingMapping(MissingMappingType type, String identifier) {
                    // Don't log missing mappings for our tests
                }
            };
            convertWorld(converter, unzipped, output, outputType, outputVersion);

            // Assert that no errors happened
            assertFalse(converter.isExceptions());

            // Assert that world data was written
            assertTrue(output.resolve("level.dat").toFile().exists());
        } finally {
            remove(unzipped);
            remove(output);
        }
    }

    @ParameterizedTest
    @MethodSource("getWorldNames")
    public void testWorldSettings(String inputWorldName) throws IOException {
        URL worldZip = Resources.getResource("integration/worlds/" + inputWorldName + ".zip");
        Path unzipped = unzip(worldZip);
        Path output = tempFolder();
        try {
            // Create a new world converter for our world
            WorldConverter converter = new WorldConverter(UUID.randomUUID());
            convertWorld(converter, unzipped, output, EncodingType.SETTINGS, new Version(1, 0, 0));

            // Assert that no errors happened
            assertFalse(converter.isExceptions());

            // Assert that setting data was written
            assertTrue(output.resolve("data.json").toFile().exists());
        } finally {
            remove(unzipped);
            remove(output);
        }
    }

    @ParameterizedTest
    @MethodSource("getWorldNames")
    public void testWorldPreview(String inputWorldName) throws IOException {
        URL worldZip = Resources.getResource("integration/worlds/" + inputWorldName + ".zip");
        Path unzipped = unzip(worldZip);
        Path output = tempFolder();
        try {
            // Create a new world converter for our world
            WorldConverter converter = new WorldConverter(UUID.randomUUID()) {
                @Override
                public void logMissingMapping(MissingMappingType type, String identifier) {
                    // Don't log missing mappings for our tests
                }
            };
            convertWorld(converter, unzipped, output, EncodingType.PREVIEW, new Version(1, 0, 0));

            // Assert that no errors happened
            assertFalse(converter.isExceptions());

            // Assert that map data was written
            assertTrue(output.resolve("map.bin").toFile().exists());

            // Assert that image data was written
            assertTrue(Objects.requireNonNull(output.toFile().listFiles((dir, file) -> file.endsWith(".png"))).length > 0);
        } finally {
            remove(unzipped);
            remove(output);
        }
    }

    public void convertWorld(WorldConverter converter, Path input, Path output, EncodingType outputType, Version outputVersion) {
        // Create the reader / writer (note: converter settings cannot be set after construction)
        Optional<? extends LevelReader> reader = EncodingType.findReader(input.toFile(), converter);
        if (reader.isEmpty()) {
            throw new IllegalArgumentException("Failed to find suitable reader for the world.");
        }

        // If the format is the same enable NBT copying (recreate the reader with the setting)
        if (reader.get().getEncodingType() == outputType && reader.get().getVersion().equals(outputVersion)) {
            converter.setAllowNBTCopying(true);
            reader = EncodingType.findReader(input.toFile(), converter);
        }

        // Create the writer
        Optional<? extends LevelWriter> writer = outputType.createWriter(output.toFile(), outputVersion, converter);
        if (writer.isEmpty()) {
            throw new IllegalArgumentException("Failed to find suitable writer for the world.");
        }

        // Run the conversion
        TrackedTask<Void> conversionTask = converter.convert(reader.get(), writer.get());

        // Join the future as we need to be sync
        conversionTask.future().join();
    }
}
