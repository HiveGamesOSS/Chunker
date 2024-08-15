package com.hivemc.chunker.conversion.intermediate.column.blockentity.container;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap;

import java.util.Objects;

/**
 * A block entity which has an inventory.
 */
public abstract class ContainerBlockEntity extends BlockEntity {
    private final Byte2ObjectAVLTreeMap<ChunkerItemStack> items = new Byte2ObjectAVLTreeMap<>();

    /**
     * A map containing the items in the container.
     *
     * @return a map of each item with a slot ID as a byte.
     */
    public Byte2ObjectAVLTreeMap<ChunkerItemStack> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContainerBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getItems());
    }
}
