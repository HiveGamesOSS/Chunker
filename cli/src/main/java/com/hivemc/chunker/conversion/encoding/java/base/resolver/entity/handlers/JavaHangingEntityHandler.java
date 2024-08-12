package com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.entity.HangingEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Hanging entity handler which reads/writes the tile position.
 */
public class JavaHangingEntityHandler extends EntityHandler<JavaResolvers, CompoundTag, HangingEntity> {
    public JavaHangingEntityHandler() {
        super(null, HangingEntity.class, () -> {
            throw new IllegalArgumentException("Unable to construct HangingEntity, invalid type!");
        });
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull HangingEntity value) {
        // Tile XYZ
        value.setTileX(input.getInt("TileX", (int) Math.floor(value.getPositionX())));
        value.setTileY(input.getInt("TileY", (int) Math.floor(value.getPositionY())));
        value.setTileZ(input.getInt("TileZ", (int) Math.floor(value.getPositionZ())));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull HangingEntity value) {
        // Tile XYZ
        output.put("TileX", value.getTileX());
        output.put("TileY", value.getTileY());
        output.put("TileZ", value.getTileZ());
    }
}
