package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.end.EndGatewayBlockEntity;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Handler for end gateways.
 */
public class BedrockEndGatewayBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, EndGatewayBlockEntity> {
    public BedrockEndGatewayBlockEntityHandler() {
        super("EndGateway", EndGatewayBlockEntity.class, EndGatewayBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull EndGatewayBlockEntity value) {
        value.setAge(input.getInt("Age", 0));

        List<Integer> values = input.getListValues("ExitPortal", IntTag.class, null);
        if (values != null && values.size() == 3) {
            value.setExitX(values.get(0));
            value.setExitY(values.get(1));
            value.setExitZ(values.get(2));
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull EndGatewayBlockEntity value) {
        output.put("Age", (int) value.getAge());
        output.put("ExitPortal", ListTag.fromValues(TagType.INT, List.of(
                value.getExitX(),
                value.getExitY(),
                value.getExitZ()
        )));
    }
}
