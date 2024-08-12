package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.end.EndGatewayBlockEntity;
import com.hivemc.chunker.nbt.tags.array.IntArrayTag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for End Gateway Block Entities.
 */
public class JavaEndGatewayBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, EndGatewayBlockEntity> {
    public JavaEndGatewayBlockEntityHandler() {
        super("minecraft:end_gateway", EndGatewayBlockEntity.class, EndGatewayBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull EndGatewayBlockEntity value) {
        value.setAge(input.getLong("Age", 0L));

        int[] position = input.getIntArray("exit_portal", null);
        if (position != null) {
            value.setExitX(position[0]);
            value.setExitY(position[1]);
            value.setExitZ(position[2]);
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull EndGatewayBlockEntity value) {
        output.put("Age", value.getAge());
        output.put("exit_portal", new IntArrayTag(new int[]{
                value.getExitX(),
                value.getExitY(),
                value.getExitZ()
        }));
    }
}
