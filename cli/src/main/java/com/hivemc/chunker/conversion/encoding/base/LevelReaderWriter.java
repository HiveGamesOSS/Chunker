package com.hivemc.chunker.conversion.encoding.base;

import com.hivemc.chunker.conversion.encoding.EncodingType;

/**
 * An interface that represents either a LevelReader or a LevelWriter.
 */
public interface LevelReaderWriter {
    /**
     * Check if this class is a reader.
     *
     * @return true if this class is a reader.
     */
    boolean isReader();

    /**
     * Get the encoding type that this level reader/writer uses.
     *
     * @return the encoding type.
     */
    EncodingType getEncodingType();

    /**
     * Get the version handled by this reader / writer.
     *
     * @return the version of the game.
     */
    Version getVersion();

    /**
     * Called when the conversion is complete, whether it error'd or not.
     */
    default void free() throws Exception {
        // By default, does nothing, useful for closing resources.
    }
}
