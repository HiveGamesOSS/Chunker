package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.FloatTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Base entity handler which converts position, motion and rotation tags.
 */
public class BedrockEntityHandler extends EntityHandler<BedrockResolvers, CompoundTag, Entity> {
    public BedrockEntityHandler() {
        super(null, Entity.class, () -> {
            throw new IllegalArgumentException("Unable to construct Entity, invalid type!");
        });
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull Entity entity) {
        // Save original NBT
        if (resolvers.converter().shouldAllowNBTCopying()) {
            entity.setOriginalNBT(input);
        }

        // Position
        List<Float> position = input.getListValues("Pos", FloatTag.class, null);
        if (position != null && position.size() >= 3) {
            entity.setPositionX(position.get(0));
            entity.setPositionY(position.get(1));
            entity.setPositionZ(position.get(2));
        }

        // Motion
        List<Float> motion = input.getListValues("Motion", FloatTag.class, null);
        if (motion != null && motion.size() >= 3) {
            entity.setMotionX(motion.get(0));
            entity.setMotionY(motion.get(1));
            entity.setMotionZ(motion.get(2));
        }

        // Rotation
        List<Float> rotation = input.getListValues("Rotation", FloatTag.class, null);
        if (rotation != null && rotation.size() >= 2) {
            entity.setYaw(rotation.get(0));
            entity.setPitch(rotation.get(1));
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull Entity entity) {
        // Restore original NBT
        if (resolvers.converter().shouldAllowNBTCopying() && entity.getOriginalNBT() != null) {
            // Copy all the original tags
            for (Map.Entry<String, Tag<?>> namedPair : entity.getOriginalNBT()) {
                output.put(namedPair.getKey(), namedPair.getValue().clone());
            }
        }

        // Position
        ListTag<FloatTag, Float> pos = new ListTag<>(TagType.FLOAT, 3);
        pos.add(new FloatTag((float) entity.getPositionX()));
        pos.add(new FloatTag((float) entity.getPositionY()));
        pos.add(new FloatTag((float) entity.getPositionZ()));
        output.put("Pos", pos);

        // Motion
        ListTag<FloatTag, Float> motion = new ListTag<>(TagType.FLOAT, 3);
        motion.add(new FloatTag((float) entity.getMotionX()));
        motion.add(new FloatTag((float) entity.getMotionY()));
        motion.add(new FloatTag((float) entity.getMotionZ()));
        output.put("Motion", motion);

        // Motion
        ListTag<FloatTag, Float> rotation = new ListTag<>(TagType.FLOAT, 2);
        rotation.add(new FloatTag(entity.getYaw()));
        rotation.add(new FloatTag(entity.getPitch()));
        output.put("Rotation", rotation);
    }
}
