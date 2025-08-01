package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.CustomItemNBTBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.ShelfBlockEntity;
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
public class JavaShelfBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, ShelfBlockEntity> implements CustomItemNBTBlockEntityHandler<JavaResolvers, ShelfBlockEntity> {
    public JavaShelfBlockEntityHandler() {
        super("minecraft:shelf", ShelfBlockEntity.class, ShelfBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull ShelfBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = input.getList("Items", CompoundTag.class, null);

        if (items != null) {
            byte index = 0;
            for (CompoundTag itemTag : items) {
                ChunkerItemStack item = resolvers.readItem(itemTag);
                if (!item.getIdentifier().isAir()) {
                    byte slot = itemTag.getByte("Slot", index++);
                    if (slot >= 0 && slot < 3) {
                        // Read item
                        value.getItems()[slot] = item;
                    }
                }
            }
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull ShelfBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, new ArrayList<>(3));
        for (int i = 0; i < value.getItems().length; i++) {
            ChunkerItemStack itemStack = value.getItems()[i];

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

    @Override
    public boolean generateFromItemNBT(@NotNull JavaResolvers resolvers, @NotNull ChunkerItemStack itemStack, @NotNull ShelfBlockEntity output, @NotNull CompoundTag input) {
        if (resolvers.dataVersion().getVersion().isLessThan(1, 20, 5)) return false; // Components not needed
        CompoundTag components = input.getCompound("components");
        if (components == null) return false;

        // Get the container component
        ListTag<CompoundTag, Map<String, Tag<?>>> items = components.getList("minecraft:container", CompoundTag.class, null);
        if (items != null) {
            byte index = 0;
            for (CompoundTag itemTag : items) {
                byte slot = (byte) itemTag.getInt("slot", index);

                // Read item
                itemTag = itemTag.getCompound("item");
                if (itemTag == null) continue;

                // Read the tag
                ChunkerItemStack item = resolvers.readItem(itemTag);
                if (slot < 0 || slot >= output.getItems().length) continue;
                output.getItems()[slot] = item;

                // Increment index
                index++;
            }
        }

        return true; // Success
    }

    @Override
    public boolean writeToItemNBT(@NotNull JavaResolvers resolvers, @NotNull ChunkerItemStack itemStack, @NotNull ShelfBlockEntity input, @NotNull CompoundTag output) {
        if (resolvers.dataVersion().getVersion().isLessThan(1, 20, 5))
            return true; // Components not needed (write normally)

        CompoundTag components = output.getOrCreateCompound("components");

        // Write items
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, new ArrayList<>(input.getItems().length));
        for (int i = 0; i < input.getItems().length; i++) {
            ChunkerItemStack chunkerItem = input.getItems()[i];
            // Don't write air to inventories
            if (chunkerItem == null || chunkerItem.getIdentifier().isAir()) continue;

            // Write the item with slot
            Optional<CompoundTag> item = resolvers.writeItem(chunkerItem);
            if (item.isEmpty()) continue;

            // Add the slot
            CompoundTag itemTag = new CompoundTag();
            itemTag.put("slot", i);
            itemTag.put("item", item.get());

            // Add to items
            items.add(itemTag);
        }
        components.put("minecraft:container", items);

        return false; // Block entity not needed
    }
}
