package com.hivemc.chunker.conversion.encoding.base.reader;

import com.hivemc.chunker.conversion.handlers.WorldConversionHandler;

/**
 * A reader which can read a world.
 */
public interface WorldReader {
    /**
     * Invoked when the world should be read.
     *
     * @param worldConversionHandler the output handler to call the relevant methods of.
     */
    void readWorld(WorldConversionHandler worldConversionHandler);
}
