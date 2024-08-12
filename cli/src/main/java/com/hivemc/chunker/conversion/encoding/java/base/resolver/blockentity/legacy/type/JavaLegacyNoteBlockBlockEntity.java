package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Note;

/**
 * Java Note Block entity with a note property.
 */
public class JavaLegacyNoteBlockBlockEntity extends BlockEntity {
    private Note note;

    /**
     * Get the note for the note block.
     *
     * @return the note to use.
     */
    public Note getNote() {
        return note;
    }

    /**
     * Set the note for the note block.
     *
     * @param note the note to use.
     */
    public void setNote(Note note) {
        this.note = note;
    }
}
