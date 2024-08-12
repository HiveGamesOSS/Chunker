package com.hivemc.chunker.conversion.encoding.java;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaLevelReader;

import java.io.File;

/**
 * Constructor for creating a JavaLevelReader.
 *
 * @param <T> the type of the level reader.
 */
public interface JavaLevelReaderConstructor<T extends JavaLevelReader> {
    /**
     * Create a new level reader.
     *
     * @param inputDirectory the input world directory.
     * @param inputVersion   the input version of the world.
     * @param converter      the converter instance.
     * @return a newly constructed LevelReader.
     */
    T construct(File inputDirectory, Version inputVersion, Converter converter);
}
