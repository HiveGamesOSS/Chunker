package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.CampfireBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Handler for Campfires.
 */
public class BedrockCampfireBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, CampfireBlockEntity> {
    public BedrockCampfireBlockEntityHandler() {
        super("Campfire", CampfireBlockEntity.class, CampfireBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull CampfireBlockEntity value) {
        for (int i = 1; i <= 4; i++) {
            CompoundTag itemTag = input.get("Item" + i);
            if (itemTag != null) {
                // Read item
                ChunkerItemStack item = resolvers.readItem(itemTag);
                value.getItems()[i - 1] = item;
            }

            // Read cooking time
            value.getCookingTimes()[i - 1] = input.getInt("ItemTime" + i, 0);
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull CampfireBlockEntity value) {
        for (int i = 0; i < value.getItems().length; i++) {
            ChunkerItemStack itemStack = value.getItems()[i];
            // Write the item
            Optional<CompoundTag> item = resolvers.writeItem(itemStack == null ? new ChunkerItemStack(ChunkerBlockIdentifier.AIR) : itemStack);
            if (item.isEmpty()) continue; // Shouldn't happen but continue otherwise
            output.put("Item" + (i + 1), item.get());

            // Write cooking time
            output.put("ItemTime" + (i + 1), value.getCookingTimes()[i]);
        }
    }
}
