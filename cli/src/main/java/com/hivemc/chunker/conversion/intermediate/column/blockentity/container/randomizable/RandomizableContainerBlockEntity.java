package com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.ContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A block entity which has a loot table that is run when the inventory is accessed.
 */
public abstract class RandomizableContainerBlockEntity extends ContainerBlockEntity {
    @Nullable
    private String lootTable;

    /**
     * Get the loot table path for this block entity.
     *
     * @return the qualified path for the loot table.
     */
    @Nullable
    public String getLootTable() {
        return lootTable;
    }

    /**
     * Set the loot table path for this block entity.
     *
     * @param lootTable the qualified path for the loot table.
     */
    public void setLootTable(@Nullable String lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RandomizableContainerBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getLootTable(), that.getLootTable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLootTable());
    }
}
