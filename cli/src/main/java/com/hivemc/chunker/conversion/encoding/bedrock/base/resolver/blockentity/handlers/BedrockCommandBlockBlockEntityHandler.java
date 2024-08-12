package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.CommandBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for command blocks.
 */
public class BedrockCommandBlockBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, CommandBlockEntity> {
    public BedrockCommandBlockBlockEntityHandler() {
        super("CommandBlock", CommandBlockEntity.class, CommandBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull CommandBlockEntity value) {
        value.setCommand(input.getString("Command", ""));
        value.setAuto(input.getByte("auto", (byte) 0) == (byte) 1);
        value.setTrackOutput(input.getByte("TrackOutput", (byte) 1) == (byte) 1);
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull CommandBlockEntity value) {
        output.put("Command", value.getCommand());
        output.put("auto", value.isAuto() ? (byte) 1 : (byte) 0);
        output.put("TrackOutput", value.isTrackOutput() ? (byte) 1 : (byte) 0);
    }
}
