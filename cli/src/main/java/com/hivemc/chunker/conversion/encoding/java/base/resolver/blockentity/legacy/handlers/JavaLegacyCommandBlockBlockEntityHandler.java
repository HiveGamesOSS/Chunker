package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.CommandBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.JsonTextUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Command Block Entities.
 */
public class JavaLegacyCommandBlockBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, CommandBlockEntity> {
    public JavaLegacyCommandBlockBlockEntityHandler() {
        super("Control", CommandBlockEntity.class, CommandBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull CommandBlockEntity value) {
        value.setCommand(input.getString("Command", ""));
        value.setAuto(input.getByte("auto", (byte) 0) == (byte) 1);
        value.setTrackOutput(input.getByte("TrackOutput", (byte) 1) == (byte) 1);

        value.setCustomName(input.getOptionalValue("CustomName", String.class)
                .map(JsonTextUtil::fromJSON)
                .orElse(null));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull CommandBlockEntity value) {
        output.put("Command", value.getCommand());
        output.put("auto", value.isAuto() ? (byte) 1 : (byte) 0);
        output.put("TrackOutput", value.isTrackOutput() ? (byte) 1 : (byte) 0);

        if (value.getCustomName() != null) {
            output.put("CustomName", JsonTextUtil.toJSON(value.getCustomName()));
        }
    }
}
