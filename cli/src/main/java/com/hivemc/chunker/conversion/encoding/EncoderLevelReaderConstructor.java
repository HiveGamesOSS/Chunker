package com.hivemc.chunker.conversion.encoding;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.reader.LevelReader;

import java.io.File;
import java.util.Optional;

/**
 * Construct a level reader in a specific format from an input directory.
 *
 * @param <T> the type of level reader the format creates.
 */
public interface EncoderLevelReaderConstructor<T extends LevelReader> {
    /**
     * Create a new LevelReader from an input directory.
     *
     * @param inputDirectory the input directory to detect the version of.
     * @param converter      the converter instance to use.
     * @return a newly created level reader or empty if it's not possible to read the format.
     */
    Optional<? extends T> construct(File inputDirectory, Converter converter);
}
