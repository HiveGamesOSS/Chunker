package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a Jukebox Block Entity.
 */
public class JukeboxBlockEntity extends BlockEntity {
    @Nullable
    private ChunkerItemStack record;

    /**
     * Get the record held in the Jukebox.
     *
     * @return null/air if no record.
     */
    @Nullable
    public ChunkerItemStack getRecord() {
        return record;
    }

    /**
     * Set the record held in the Jukebox.
     *
     * @param record the record, null/air if no record.
     */
    public void setRecord(@Nullable ChunkerItemStack record) {
        this.record = record;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JukeboxBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getRecord(), that.getRecord());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getRecord());
    }
}
