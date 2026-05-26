package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.JigsawBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.InvertibleMap;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Jigsaw Blocks.
 */
public class BedrockJigsawBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, JigsawBlockEntity> {
    /**
     * Mapping of joint types to bedrock strings.
     */
    public static final InvertibleMap<JigsawBlockEntity.Joint, String> JOINT_TO_STRING = InvertibleMap.enumKeys(JigsawBlockEntity.Joint.class);

    static {
        JOINT_TO_STRING.put(JigsawBlockEntity.Joint.ROLLABLE, "rollable");
        JOINT_TO_STRING.put(JigsawBlockEntity.Joint.ALIGNED, "aligned");
    }

    public BedrockJigsawBlockEntityHandler() {
        super("JigsawBlock", JigsawBlockEntity.class, JigsawBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull JigsawBlockEntity value) {
        value.setName(input.getString("name", "minecraft:empty"));
        value.setTarget(input.getString("target", "minecraft:empty"));
        value.setPool(input.getString("target_pool", "minecraft:empty"));
        value.setFinalState(input.getString("final_state", "minecraft:air"));
        value.setJoint(JOINT_TO_STRING.inverse().getOrDefault(input.getString("joint", "rollable"), JigsawBlockEntity.Joint.ROLLABLE));
        value.setSelectionPriority(input.getInt("selection_priority", 0));
        value.setPlacementPriority(input.getInt("placement_priority", 0));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull JigsawBlockEntity value) {
        output.put("name", value.getName());
        output.put("target", value.getTarget());
        output.put("target_pool", value.getPool());
        output.put("final_state", value.getFinalState());
        output.put("joint", JOINT_TO_STRING.forward().getOrDefault(value.getJoint(), "rollable"));
        output.put("selection_priority", value.getSelectionPriority());
        output.put("placement_priority", value.getPlacementPriority());
    }
}
