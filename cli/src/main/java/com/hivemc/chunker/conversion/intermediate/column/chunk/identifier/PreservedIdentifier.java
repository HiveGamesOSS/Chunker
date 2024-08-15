package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier;

import com.hivemc.chunker.mapping.identifier.Identifier;

/**
 * A preserved identifier which should be used for the output after writing.
 * The fromReader field ensures that this value is only applied for the writer.
 *
 * @param fromReader whether this preserved identifier was created by the reader.
 * @param identifier the identifier which should be preserved.
 */
public record PreservedIdentifier(boolean fromReader, Identifier identifier) {
}
