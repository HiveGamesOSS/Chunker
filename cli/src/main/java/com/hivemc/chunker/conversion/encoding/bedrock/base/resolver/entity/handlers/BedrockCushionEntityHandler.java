package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerDyeColor;
import com.hivemc.chunker.conversion.intermediate.column.entity.CushionEntity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler that reads cushions.
 */
public class BedrockCushionEntityHandler extends EntityHandler<BedrockResolvers, CompoundTag, CushionEntity> {
    public BedrockCushionEntityHandler() {
        super(ChunkerVanillaEntityType.CUSHION, CushionEntity.class, CushionEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull CushionEntity value) {
        value.setColor(input.getOptionalValue("Variant", Integer.class).flatMap(ChunkerDyeColor::getColorByReversedID).orElse(ChunkerDyeColor.WHITE));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull CushionEntity value) {
        output.put("Variant", value.getColor().getReversedID());
    }
}
