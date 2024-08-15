package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.entity.HangingEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Hanging entity handler which sets the tile XYZ from the position.
 */
public class BedrockHangingEntityHandler extends EntityHandler<BedrockResolvers, CompoundTag, HangingEntity> {
    public BedrockHangingEntityHandler() {
        super(null, HangingEntity.class, () -> {
            throw new IllegalArgumentException("Unable to construct HangingEntity, invalid type!");
        });
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull HangingEntity value) {
        // Tile XYZ
        value.setTileX((int) Math.floor(value.getPositionX()));
        value.setTileY((int) Math.floor(value.getPositionY()));
        value.setTileZ((int) Math.floor(value.getPositionZ()));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull HangingEntity value) {
    }
}
