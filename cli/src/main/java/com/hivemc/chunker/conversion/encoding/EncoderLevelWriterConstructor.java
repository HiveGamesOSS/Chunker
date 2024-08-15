package com.hivemc.chunker.conversion.encoding;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;

import java.io.File;
import java.util.Optional;

/**
 * Construct a level writer in a specific format from a target version.
 *
 * @param <T> the type of level writer the format creates.
 */
public interface EncoderLevelWriterConstructor<T extends LevelWriter> {
    /**
     * Create a new LevelWriter for a specific version.
     *
     * @param outputDirectory the directory the writer should write to.
     * @param outputVersion   the target output version for the version.
     * @param converter       the converter instance to use.
     * @return a newly created level writer or empty if it's not possible to write the version.
     */
    Optional<? extends T> construct(File outputDirectory, Version outputVersion, Converter converter);
}
