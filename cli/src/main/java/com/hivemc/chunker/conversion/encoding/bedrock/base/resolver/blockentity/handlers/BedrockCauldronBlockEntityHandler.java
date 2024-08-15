package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.CauldronBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionBottleType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Handler for Cauldrons (Bedrock only data).
 */
public class BedrockCauldronBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, CauldronBlockEntity> {
    public BedrockCauldronBlockEntityHandler() {
        super("Cauldron", CauldronBlockEntity.class, CauldronBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull CauldronBlockEntity value) {
        // Find the type of the potion (if there is one)
        if (input.getByte("IsSplash", (byte) 0) == (byte) 1) {
            value.setPotionBottleType(ChunkerPotionBottleType.SPLASH);
        } else {
            value.setPotionBottleType(ChunkerPotionBottleType.fromID(input.getShort("PotionType", (short) -1)));
        }
        value.setPotionType(resolvers.readPotionTypeID(input.getShort("PotionId", (short) -1)));
        if (input.contains("CustomColor")) {
            value.setCustomColor(new Color(input.getInt("CustomColor"), true));
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull CauldronBlockEntity value) {
        output.put("IsSplash", value.getPotionBottleType() == ChunkerPotionBottleType.SPLASH ? (byte) 1 : (byte) 0);
        output.put("PotionType", (short) value.getPotionBottleType().getID());
        output.put("PotionId", resolvers.writePotionTypeID(value.getPotionType()));
        if (value.getCustomColor() != null) {
            output.put("CustomColor", value.getCustomColor().getRGB());
        }
    }
}
