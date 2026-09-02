package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.BrewingStandBlockEntity;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.primitive.ByteTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import com.hivemc.chunker.nbt.tags.primitive.ShortTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Brewing Stand Block Entities.
 */
public class JavaBrewingStandBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, BrewingStandBlockEntity> {
    public JavaBrewingStandBlockEntityHandler() {
        super("minecraft:brewing_stand", BrewingStandBlockEntity.class, BrewingStandBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull BrewingStandBlockEntity value) {
        value.setBrewTime(getByteShortOrInt(input, "BrewTime", (short) 0));
        value.setFuel(getByteShortOrInt(input, "Fuel", (short) 0));

        // total_fuel was added in 26.3
        value.setFuelTotal(getByteShortOrInt(input, "total_fuel", BrewingStandBlockEntity.DEFAULT_FUEL_TOTAL));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull BrewingStandBlockEntity value) {
        // 26.3 changed these to integers and added total_fuel (previously Bedrock only)
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(26, 3, 0)) {
            output.put("BrewTime", (int) value.getBrewTime());
            output.put("Fuel", (int) value.getFuel());
            output.put("total_fuel", (int) value.getFuelTotal());
        } else {
            output.put("BrewTime", value.getBrewTime());
            output.put("Fuel", (byte) value.getFuel());
        }
    }

    private static short getByteShortOrInt(@NotNull CompoundTag tag, @NotNull String key, short defaultValue) {
        Tag<?> value = tag.get(key);
        if (value instanceof ByteTag byteTag) {
            return byteTag.getValue();
        } else if (value instanceof ShortTag shortTag) {
            return shortTag.getValue();
        } else if (value instanceof IntTag intTag) {
            return (short) intTag.getValue();
        } else if (value == null) {
            return defaultValue;
        }

        throw new IllegalArgumentException(value.getClass().getName() + " is not of type ByteTag / ShortTag / IntTag");
    }
}
