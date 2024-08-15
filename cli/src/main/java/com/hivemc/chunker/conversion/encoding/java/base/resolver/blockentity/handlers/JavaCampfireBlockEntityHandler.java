package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.CampfireBlockEntity;
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
 * Handler for Campfire Block Entities.
 */
public class JavaCampfireBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, CampfireBlockEntity> {
    public JavaCampfireBlockEntityHandler() {
        super("minecraft:campfire", CampfireBlockEntity.class, CampfireBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull CampfireBlockEntity value) {
        int[] cookingTimes = input.getIntArray("CookingTimes", null);
        ListTag<CompoundTag, Map<String, Tag<?>>> items = input.getList("Items", CompoundTag.class, null);

        if (items != null) {
            byte index = 0;
            for (CompoundTag itemTag : items) {
                ChunkerItemStack item = resolvers.readItem(itemTag);
                if (!item.getIdentifier().isAir()) {
                    byte slot = itemTag.getByte("Slot", index++);
                    if (slot >= 0 && slot < 4) {
                        // Read item
                        value.getItems()[slot] = item;

                        // Read cooking time
                        if (cookingTimes != null && cookingTimes.length > slot) {
                            value.getCookingTimes()[slot] = cookingTimes[slot];
                        }
                    }
                }
            }
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull CampfireBlockEntity value) {
        int[] cookingTimes = new int[4];
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, new ArrayList<>(4));
        for (int i = 0; i < value.getItems().length; i++) {
            ChunkerItemStack itemStack = value.getItems()[i];

            // Don't write air to inventories
            if (itemStack == null || itemStack.getIdentifier().isAir()) continue; // Skip air

            // Write the item with slot
            Optional<CompoundTag> item = resolvers.writeItem(itemStack);
            if (item.isEmpty()) continue;

            item.get().put("Slot", (byte) i);
            items.add(item.get());

            // Write cooking time
            cookingTimes[i] = value.getCookingTimes()[i];
        }
        output.put("Items", items);
        output.put("CookingTimes", cookingTimes);
    }
}
