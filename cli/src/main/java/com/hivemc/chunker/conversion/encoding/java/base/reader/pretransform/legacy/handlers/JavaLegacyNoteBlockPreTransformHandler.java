package com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.pretransform.handlers.BedrockNoteBlockPreTransformHandler;
import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.NoteBlockInstrument;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the instrument that should be used for the note block.
 */
public class JavaLegacyNoteBlockPreTransformHandler implements BlockPreTransformHandler {
    @Override
    public Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        return Collections.emptySet();
    }

    @Override
    public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        // Fetch the block below
        ChunkerBlockIdentifier below = getRelative(column, neighbours, x, y, z, Direction.DOWN);
        NoteBlockInstrument instrument = BedrockNoteBlockPreTransformHandler.INSTRUMENTS.getOrDefault(below.getType(), NoteBlockInstrument.HARP);

        // Update with the instrument
        return blockIdentifier.copyWith(VanillaBlockStates.NOTE_BLOCK_INSTRUMENT, instrument);
    }
}
