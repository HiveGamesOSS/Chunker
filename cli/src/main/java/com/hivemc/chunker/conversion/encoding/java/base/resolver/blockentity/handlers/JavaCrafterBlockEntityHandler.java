package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.CrafterBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Crafter Block Entities.
 */
public class JavaCrafterBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, CrafterBlockEntity> {
    public JavaCrafterBlockEntityHandler() {
        super("minecraft:crafter", CrafterBlockEntity.class, CrafterBlockEntity::new);
    }

    public static short fromIntArray(int[] disabledSlots) {
        if (disabledSlots == null) return 0;
        short bits = 0;
        for (int bitIndex : disabledSlots) {
            bits |= (short) (1 << bitIndex);
        }

        return bits;
    }

    public static int[] toIntArray(short bits) {
        IntList disabledSlots = new IntArrayList();

        for (int i = 0; i < 16; i++) {
            if ((bits & (1 << i)) != 0) {
                disabledSlots.add(i);
            }
        }

        return disabledSlots.toIntArray();
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull CrafterBlockEntity value) {
        value.setDisabledSlots(fromIntArray(input.getIntArray("disabled_slots", null)));
        value.setCraftingTicksRemaining(input.getInt("crafting_ticks_remaining", 0));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull CrafterBlockEntity value) {
        output.put("disabled_slots", toIntArray(value.getDisabledSlots()));
        output.put("crafting_ticks_remaining", value.getCraftingTicksRemaining());
    }
}
