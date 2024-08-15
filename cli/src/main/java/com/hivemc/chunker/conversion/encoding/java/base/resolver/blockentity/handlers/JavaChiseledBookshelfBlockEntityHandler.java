package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.ChiseledBookshelfBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Handler for Chiseled Bookshelf Block Entities.
 */
public class JavaChiseledBookshelfBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, ChiseledBookshelfBlockEntity> {
    public JavaChiseledBookshelfBlockEntityHandler() {
        super("minecraft:chiseled_bookshelf", ChiseledBookshelfBlockEntity.class, ChiseledBookshelfBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull ChiseledBookshelfBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = input.getList("Items", CompoundTag.class, null);

        if (items != null) {
            byte index = 0;
            for (CompoundTag itemTag : items) {
                ChunkerItemStack item = resolvers.readItem(itemTag);
                if (!item.getIdentifier().isAir()) {
                    byte slot = itemTag.getByte("Slot", index++);
                    if (slot >= 0 && slot < 6) {
                        // Read item
                        value.getBooks()[slot] = item;
                    }
                }
            }
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull ChiseledBookshelfBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, new ArrayList<>(4));
        for (int i = 0; i < value.getBooks().length; i++) {
            ChunkerItemStack itemStack = value.getBooks()[i];

            // Don't write air to inventories
            if (itemStack == null || itemStack.getIdentifier().isAir()) continue; // Skip air

            // Write the item
            Optional<CompoundTag> item = resolvers.writeItem(itemStack);
            if (item.isEmpty()) continue;

            // Add the slot
            item.get().put("Slot", (byte) i);
            items.add(item.get());
        }
        output.put("Items", items);
    }
}
