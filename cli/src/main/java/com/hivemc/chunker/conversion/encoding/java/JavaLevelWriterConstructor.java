package com.hivemc.chunker.conversion.encoding.java;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaLevelWriter;

import java.io.File;

/**
 * Constructor for creating a JavaLevelWriter.
 *
 * @param <T> the type of the level writer.
 */
public interface JavaLevelWriterConstructor<T extends JavaLevelWriter> {
    /**
     * Create a new level writer.
     *
     * @param outputDirectory the output world directory.
     * @param outputVersion   the output version of the world.
     * @param converter       the converter instance.
     * @return a newly constructed LevelWriter.
     */
    T construct(File outputDirectory, Version outputVersion, Converter converter);
}
