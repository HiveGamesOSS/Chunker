package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.sculksensor.SculkSensorBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Sculk Sensor Block Entities.
 */
public class JavaSculkSensorBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, SculkSensorBlockEntity> {
    public JavaSculkSensorBlockEntityHandler() {
        super("minecraft:sculk_sensor", SculkSensorBlockEntity.class, SculkSensorBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull SculkSensorBlockEntity value) {
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull SculkSensorBlockEntity value) {
        CompoundTag listener = new CompoundTag();
        CompoundTag selector = new CompoundTag();
        selector.put("tick", -1L);
        listener.put("event_delay", 0);
        listener.put("selector", selector);
        output.put("listener", listener);
        output.put("last_vibration_frequency", 0);
    }
}
