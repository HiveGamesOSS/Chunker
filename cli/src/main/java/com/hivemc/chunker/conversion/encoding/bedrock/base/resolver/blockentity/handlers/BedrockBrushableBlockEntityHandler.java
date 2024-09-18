package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockBrushableBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BrushableBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Suspicious Blocks which holds an additional type field of the block type.
 */
public class BedrockBrushableBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockBrushableBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<BedrockResolvers, BrushableBlockEntity>, UpdateBeforeProcessBlockEntityHandler<BedrockResolvers, BrushableBlockEntity> {
    public BedrockBrushableBlockEntityHandler() {
        super("BrushableBlock", BedrockBrushableBlockEntity.class, BedrockBrushableBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockBrushableBlockEntity value) {
        value.setType(input.getString("type", "minecraft:suspicious_sand"));

        value.setBrushDirection(input.getByte("brush_direction", (byte) 0));
        value.setBrushCount(input.getInt("brush_count", 0));

        CompoundTag item = input.getCompound("item");
        if (item != null) {
            value.setItem(resolvers.readItem(item));
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockBrushableBlockEntity value) {
        output.put("type", value.getType());
        output.put("brush_direction", value.getBrushDirection());
        output.put("brush_count", value.getBrushCount());

        if (value.getItem() != null && !value.getItem().getIdentifier().isAir()) {
            resolvers.writeItem(value.getItem()).ifPresent(item -> output.put("item", item));
        }
    }

    @Override
    public BrushableBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, BrushableBlockEntity blockEntity) {
        BedrockBrushableBlockEntity bedrockBrushableBlockEntity = new BedrockBrushableBlockEntity(blockEntity);

        ChunkerBlockIdentifier identifier = column.getBlock(x, y, z);

        // Convert the identifier to a string and set it if present
        resolvers.writeItemBlockIdentifier(identifier, true)
                .map(Identifier::getIdentifier)
                .ifPresent(bedrockBrushableBlockEntity::setType);

        // Return the upgraded block entity
        return bedrockBrushableBlockEntity;
    }

    @Override
    public BrushableBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, BrushableBlockEntity blockEntity) {
        return new BedrockBrushableBlockEntity(blockEntity);
    }

    @Override
    public Class<BrushableBlockEntity> getAdditionalHandledClass() {
        return BrushableBlockEntity.class;
    }

    @Override
    public BrushableBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, BrushableBlockEntity blockEntity) {
        if (blockEntity instanceof BedrockBrushableBlockEntity brushableBlockEntity) {
            return brushableBlockEntity.toChunker();
        }
        return blockEntity;
    }

    @Override
    public BrushableBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, BrushableBlockEntity blockEntity) {
        // Ensure the item is written as the chunker type (not the bedrock one)
        if (blockEntity instanceof BedrockBrushableBlockEntity brushableBlockEntity) {
            return brushableBlockEntity.toChunker();
        }
        return blockEntity;
    }
}
