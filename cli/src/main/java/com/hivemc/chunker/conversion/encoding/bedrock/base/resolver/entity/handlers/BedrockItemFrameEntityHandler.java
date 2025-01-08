package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.entity.DoNotWriteEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.conversion.intermediate.column.entity.GlowItemFrameEntity;
import com.hivemc.chunker.conversion.intermediate.column.entity.ItemFrameEntity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

/**
 * Handler which turns Chunker item frames into block entities.
 */
public class BedrockItemFrameEntityHandler extends EntityHandler<BedrockResolvers, CompoundTag, ItemFrameEntity> implements DoNotWriteEntityHandler<ItemFrameEntity> {
    public BedrockItemFrameEntityHandler() {
        super(ChunkerVanillaEntityType.ITEM_FRAME, ItemFrameEntity.class, () -> {
            throw new IllegalArgumentException("Item frames cannot be read from Bedrock.");
        });
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull ItemFrameEntity value) {
        throw new IllegalArgumentException("Reading item frames is not supported as an entity.");
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull ItemFrameEntity value) {
        throw new IllegalArgumentException("Writing item frames is not supported as an entity.");
    }

    @Override
    public boolean shouldRemoveBeforeWrite(ChunkerColumn column, ItemFrameEntity entity) {
        // Turn into a block entity
        column.getBlockEntities().add(entity.toBedrockBlockEntity());

        // Fetch the original block which is turned into an item frame
        ChunkerBlockIdentifier oldBlock = column.getBlock(entity.getTileX(), entity.getTileY(), entity.getTileZ());

        // Replace the block with the bedrock item frame block type
        ChunkerBlockIdentifier blockIdentifier = new ChunkerBlockIdentifier(
                ChunkerVanillaBlockType.ITEM_FRAME_BEDROCK,
                Map.of(
                        VanillaBlockStates.FACING_ALL, entity.getDirection(),
                        VanillaBlockStates.BEDROCK_FRAME_PHOTO_BIT, entity.isPhoto() ? Bool.TRUE : Bool.FALSE,
                        VanillaBlockStates.BEDROCK_FRAME_MAP_BIT, entity.isMap() ? Bool.TRUE : Bool.FALSE,
                        VanillaBlockStates.LIT, entity instanceof GlowItemFrameEntity ? Bool.TRUE : Bool.FALSE,
                        VanillaBlockStates.WATERLOGGED, Objects.requireNonNull(oldBlock.getState(VanillaBlockStates.WATERLOGGED))
                )
        );
        column.setBlock(entity.getTileX(), entity.getTileY(), entity.getTileZ(), blockIdentifier);

        // Remove
        return true;
    }
}
