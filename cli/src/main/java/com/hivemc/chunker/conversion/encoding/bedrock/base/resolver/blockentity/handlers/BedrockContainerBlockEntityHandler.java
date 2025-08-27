package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.BrewingStandBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.ContainerBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Handler for base inventories (converting items).
 */
public class BedrockContainerBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, ContainerBlockEntity> {
    public BedrockContainerBlockEntityHandler() {
        super("Container", ContainerBlockEntity.class, () -> {
            throw new IllegalArgumentException("Unable to initialize type of container");
        });
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull ContainerBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = input.getList("Items", CompoundTag.class, null);
        if (items != null) {
            byte index = 0;
            for (CompoundTag itemTag : items) {
                byte slot = remapSlot(value, true, itemTag.getByte("Slot", index));

                // Read item
                ChunkerItemStack item = resolvers.readItem(itemTag);
                value.getItems().put(slot, item);

                // Increment index
                index++;
            }
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull ContainerBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, value.getItems().size());
        for (Byte2ObjectMap.Entry<ChunkerItemStack> tag : value.getItems().byte2ObjectEntrySet()) {
            // Don't write air to inventories
            if (tag.getValue().getIdentifier().isAir()) continue;

            // Write the item with slot
            Optional<CompoundTag> item = resolvers.writeItem(tag.getValue());
            if (item.isEmpty()) continue; // Don't write air

            item.get().put("Slot", remapSlot(value, false, tag.getByteKey()));

            // Add to items
            items.add(item.get());
        }
        output.put("Items", items);
    }

    /**
     * Get the remapping to use for the slot.
     *
     * @param blockEntity the block entity being read/written.
     * @param reading     whether it is being read.
     * @param slot        the input slot.
     * @return the remapped slot (can be the original).
     */
    protected byte remapSlot(ContainerBlockEntity blockEntity, boolean reading, byte slot) {
        // Brewing stand needs to be ordered with bottles first then ingredient then fuel
        if (blockEntity instanceof BrewingStandBlockEntity) {
            if (reading) {
                switch (slot) {
                    case 0: // Ingredient
                        return 3;
                    case 1: // Bottle 1
                        return 0;
                    case 2: // Bottle 2
                        return 1;
                    case 3: // Bottle 3
                        return 2;
                    case 4: // Fuel
                        return 4;
                }
            } else {
                switch (slot) {
                    case 3: // Ingredient
                        return 0;
                    case 0: // Bottle 1
                        return 1;
                    case 1: // Bottle 2
                        return 2;
                    case 2: // Bottle 3
                        return 3;
                    case 4: // Fuel
                        return 4;
                }
            }
        }

        // Normal slot layout
        return slot;
    }
}
