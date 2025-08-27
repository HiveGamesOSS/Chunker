package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.ShelfBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Handler for shelves.
 */
public class BedrockShelfBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, ShelfBlockEntity> {
    public BedrockShelfBlockEntityHandler() {
        super("Shelf", ShelfBlockEntity.class, ShelfBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull ShelfBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = input.getList("Items", CompoundTag.class, null);
        if (items != null) {
            byte index = 0;
            for (CompoundTag itemTag : items) {
                // Read item
                ChunkerItemStack item = resolvers.readItem(itemTag);
                value.getItems()[index] = item;

                // Increment index
                index++;
            }
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull ShelfBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, value.getItems().length);
        for (ChunkerItemStack itemStack : value.getItems()) {
            // Write the item
            Optional<CompoundTag> item = resolvers.writeItem(itemStack == null ? new ChunkerItemStack(ChunkerBlockIdentifier.AIR) : itemStack);

            // Add to items
            item.ifPresent(items::add);
        }
        output.put("Items", items);
    }
}
