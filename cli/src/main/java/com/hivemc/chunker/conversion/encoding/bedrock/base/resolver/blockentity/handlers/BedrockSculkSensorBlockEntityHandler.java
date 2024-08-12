package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.sculksensor.SculkSensorBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for sculk sensors.
 */
public class BedrockSculkSensorBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, SculkSensorBlockEntity> {
    public BedrockSculkSensorBlockEntityHandler() {
        super("SculkSensor", SculkSensorBlockEntity.class, SculkSensorBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull SculkSensorBlockEntity value) {
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull SculkSensorBlockEntity value) {
        CompoundTag vibrationListener = new CompoundTag();
        vibrationListener.put("selector", new CompoundTag());
        output.put("VibrationListener", vibrationListener);
    }
}
