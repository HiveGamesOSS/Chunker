package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BannerBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.JsonTextUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Handler which every block entity uses as a base. Reads/Writes basic common block entity properties.
 */
public class BedrockBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BlockEntity> {
    public BedrockBlockEntityHandler() {
        super("BlockEntity", BlockEntity.class, () -> {
            throw new IllegalArgumentException("Unable to construct BlockEntity, invalid type!");
        });
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BlockEntity blockEntity) {
        // Save original NBT
        if (resolvers.converter().shouldAllowNBTCopying()) {
            blockEntity.setOriginalNBT(input);
        }

        // Basic data
        blockEntity.setX(input.getInt("x", 0));
        blockEntity.setY(input.getInt("y", 0));
        blockEntity.setZ(input.getInt("z", 0));
        blockEntity.setMovable(input.getByte("isMovable", (byte) 0) == (byte) 1);
        blockEntity.setCustomName(input.getOptionalValue("CustomName", String.class)
                .map(JsonTextUtil::fromText)
                .orElse(null));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BlockEntity blockEntity) {// Restore original NBT
        if (resolvers.converter().shouldAllowNBTCopying() && blockEntity.getOriginalNBT() != null) {
            // Copy all the original tags
            for (Map.Entry<String, Tag<?>> namedPair : blockEntity.getOriginalNBT()) {
                output.put(namedPair.getKey(), namedPair.getValue().clone());
            }
        }

        // Basic data
        output.put("x", blockEntity.getX());
        output.put("y", blockEntity.getY());
        output.put("z", blockEntity.getZ());
        output.put("isMovable", blockEntity.isMovable() ? (byte) 1 : (byte) 0);

        // Write custom name (excluding banners since there seems to be behaviour on 1.20 Bedrock which can cause block entities to crash)
        if (blockEntity.getCustomName() != null && !(blockEntity instanceof BannerBlockEntity)) {
            output.put("CustomName", JsonTextUtil.toLegacy(blockEntity.getCustomName(), true));
        }
    }
}
