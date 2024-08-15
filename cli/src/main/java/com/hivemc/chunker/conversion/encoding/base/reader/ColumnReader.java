package com.hivemc.chunker.conversion.encoding.base.reader;

import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;

/**
 * A reader which reads a column from a format.
 */
public interface ColumnReader {
    /**
     * Invoked when the column that this handler was constructed with should be read.
     *
     * @param columnConversionHandler the output handler to call the relevant methods of.
     */
    void readColumn(ColumnConversionHandler columnConversionHandler);
}
