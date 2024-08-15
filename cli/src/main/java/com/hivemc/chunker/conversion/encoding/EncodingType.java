package com.hivemc.chunker.conversion.encoding;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.reader.LevelReader;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.BedrockEncoders;
import com.hivemc.chunker.conversion.encoding.java.JavaEncoders;
import com.hivemc.chunker.conversion.encoding.preview.PreviewLevelWriter;
import com.hivemc.chunker.conversion.encoding.settings.SettingsLevelWriter;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * An EncodingType is a specific format that worlds can be read / written as.
 */
public class EncodingType {
    // Lookups
    private static final Set<EncodingType> TYPES = new ObjectOpenHashSet<>();
    private static final Set<EncodingType> READABLE_TYPES = new ObjectOpenHashSet<>();
    private static final Set<EncodingType> WRITEABLE_TYPES = new ObjectOpenHashSet<>();

    /**
     * Reader and Writer for the Java Edition format.
     */
    public static final EncodingType JAVA = register(
            "Java",
            false,
            JavaEncoders::createReader,
            JavaEncoders::createWriter,
            JavaEncoders.getSupportedVersions()
    );

    /**
     * Reader and Writer for the Bedrock Edition format.
     */
    public static final EncodingType BEDROCK = register(
            "Bedrock",
            false,
            BedrockEncoders::createReader,
            BedrockEncoders::createWriter,
            BedrockEncoders.getSupportedVersions()
    );

    /**
     * Writer for rendering a preview of the map.
     */
    public static final EncodingType PREVIEW = register(
            "Preview",
            true,
            null,
            (directory, version, settings) -> Optional.of(new PreviewLevelWriter(directory)),
            Collections.emptyList()
    );

    /**
     * Writer for the settings of the world / in-game maps.
     */
    public static final EncodingType SETTINGS = register(
            "Settings",
            true,
            null,
            (directory, version, settings) -> Optional.of(new SettingsLevelWriter(directory)),
            Collections.emptyList()
    );


    // Fields
    private final String name;
    private final boolean internal;
    private final EncoderLevelReaderConstructor<?> readerConstructor;
    private final EncoderLevelWriterConstructor<?> writerConstructor;
    private final Collection<Version> supportedVersions;

    /**
     * Create a new encoding type.
     *
     * @param name              the name of the encoding type in English.
     * @param internal          whether the encoding type should be listed to users.
     * @param readerConstructor the constructor for creating level readers.
     * @param writerConstructor the constructor for creating level writers.
     * @param supportedVersions a list of versions this encoding type supports.
     */
    public EncodingType(String name, boolean internal, EncoderLevelReaderConstructor<?> readerConstructor, EncoderLevelWriterConstructor<?> writerConstructor, Collection<Version> supportedVersions) {
        this.name = name;
        this.internal = internal;
        this.readerConstructor = readerConstructor;
        this.writerConstructor = writerConstructor;
        this.supportedVersions = supportedVersions;
    }

    /**
     * Create and register a new encoding type.
     *
     * @param name              the name of the encoding type in English.
     * @param internal          whether the encoding type should be listed to users.
     * @param reader            the constructor for creating level readers.
     * @param writer            the constructor for creating level writers.
     * @param supportedVersions a list of versions this encoding type supports.
     */
    public static EncodingType register(String name, boolean internal, EncoderLevelReaderConstructor<?> reader, EncoderLevelWriterConstructor<?> writer, Collection<Version> supportedVersions) {
        // Create the encoding type
        EncodingType encodingType = new EncodingType(name, internal, reader, writer, supportedVersions);

        // Add to the main types
        TYPES.add(encodingType);

        // Add to the readable types
        if (reader != null) {
            READABLE_TYPES.add(encodingType);
        }

        // Add to the writeable types
        if (writer != null) {
            WRITEABLE_TYPES.add(encodingType);
        }

        // Return the encoding type
        return encodingType;
    }

    /**
     * Find a potential reader for an input directory. This iterates through all the readable types and uses the first
     * matching type.
     * Note: This includes types marked as internal.
     *
     * @param directory the input folder.
     * @param converter the converter instance to use for the reader.
     * @return the level reader if the format / version was detected otherwise empty.
     */
    public static Optional<? extends LevelReader> findReader(File directory, Converter converter) {
        // Loop through readable types
        for (EncodingType encodingType : READABLE_TYPES) {
            Optional<? extends LevelReader> reader = encodingType.createReader(directory, converter);

            // The first successful reader is used
            if (reader.isPresent()) {
                return reader;
            }
        }

        // Unfortunately none of them satisfy this world
        return Optional.empty();
    }

    /**
     * Get a collection of all the encoding types.
     * (This includes types marked as internal)
     *
     * @return a set of all the encoding types.
     */
    public static Set<EncodingType> getTypes() {
        return Collections.unmodifiableSet(TYPES);
    }

    /**
     * A set of all the types which a LevelReader can be generated for.
     * (This includes types marked as internal)
     *
     * @return a set of encoding types.
     */
    public static Set<EncodingType> getReadableTypes() {
        return Collections.unmodifiableSet(READABLE_TYPES);
    }

    /**
     * A set of all the types which a LevelWriter can be generated for.
     * (This includes types marked as internal)
     *
     * @return a set of encoding types.
     */
    public static Set<EncodingType> getWriteableTypes() {
        return Collections.unmodifiableSet(WRITEABLE_TYPES);
    }

    /**
     * Get the name of the format in English, e.g. "Format"
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Create a new LevelReader from an input directory.
     *
     * @param directory the input directory to detect the version of.
     * @param converter the converter instance to use.
     * @return a newly created level reader or empty if it's not possible to read the format.
     */
    public Optional<? extends LevelReader> createReader(File directory, Converter converter) {
        if (readerConstructor == null) return Optional.empty();
        return readerConstructor.construct(directory, converter);
    }

    /**
     * Create a new LevelWriter for a specific version.
     *
     * @param directory the directory the writer should write to.
     * @param version   the target output version for the version.
     * @param converter the converter instance to use.
     * @return a newly created level writer or empty if it's not possible to write the version.
     */
    public Optional<? extends LevelWriter> createWriter(File directory, Version version, Converter converter) {
        if (writerConstructor == null) return Optional.empty();
        return writerConstructor.construct(directory, version, converter);
    }

    /**
     * Get a collection of supported versions.
     *
     * @return a list of supported versions.
     */
    public Collection<Version> getSupportedVersions() {
        return supportedVersions;
    }

    /**
     * Whether this format should be not listed in the user-interface.
     *
     * @return true if the format is internal, e.g. settings/preview.
     */
    public boolean isInternal() {
        return internal;
    }

    @Override
    public String toString() {
        return "EncodingType{" +
                "name='" + getName() + '\'' +
                '}';
    }
}
