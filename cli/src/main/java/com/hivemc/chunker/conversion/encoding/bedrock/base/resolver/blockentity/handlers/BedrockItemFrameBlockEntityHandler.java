package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.DoNotProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockItemFrameBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.primitive.ByteTag;
import com.hivemc.chunker.nbt.tags.primitive.FloatTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for item frames which turns it into an entity when converting to chunker.
 */
public class BedrockItemFrameBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockItemFrameBlockEntity> implements DoNotProcessBlockEntityHandler<BedrockItemFrameBlockEntity> {
    public BedrockItemFrameBlockEntityHandler() {
        super("ItemFrame", BedrockItemFrameBlockEntity.class, BedrockItemFrameBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockItemFrameBlockEntity value) {
        // Read item
        CompoundTag item = input.getCompound("Item");
        if (item != null) {
            value.setItem(resolvers.readItem(item));
        }

        // Read rotation
        Tag<?> rotation = input.get("ItemRotation");
        if (rotation != null) {
            if (rotation instanceof FloatTag floatTag) {
                value.setItemRotation((byte) Math.round(((floatTag.getValue() + 360) % 360) / 45F));
            } else if (rotation instanceof ByteTag byteTag) {
                value.setItemRotation(byteTag.getValue());
            }
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockItemFrameBlockEntity value) {
        // Write item
        if (value.getItem() != null && !value.getItem().getIdentifier().isAir()) {
            resolvers.writeItem(value.getItem()).ifPresent(item -> output.put("Item", item));
        }

        // Write rotation
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 13, 0)) {
            output.put("ItemRotation", value.getItemRotation() * 45F);
        } else {
            output.put("ItemRotation", value.getItemRotation());
        }
    }

    @Override
    public boolean shouldRemoveBeforeProcess(ChunkerColumn column, int x, int y, int z, BedrockItemFrameBlockEntity blockEntity) {
        // Turn into an entity
        ChunkerBlockIdentifier block = column.getBlock(x, y, z);

        // Only add the item frame if there is a backing item frame block
        if (block.getType() == ChunkerVanillaBlockType.ITEM_FRAME_BEDROCK) {
            column.getEntities().add(blockEntity.toChunker(block));

            // Replace the block with air
            column.setBlock(x, y, z, ChunkerBlockIdentifier.AIR);
        }

        // Remove
        return true;
    }
}
