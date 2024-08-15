package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BeaconBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerEffectType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.JsonTextUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Beacon Block Entities.
 */
public class JavaLegacyBeaconBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, BeaconBlockEntity> {
    public JavaLegacyBeaconBlockEntityHandler() {
        super("Beacon", BeaconBlockEntity.class, BeaconBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull BeaconBlockEntity value) {
        value.setPrimaryEffect(resolvers.readEffectID(input.getInt("Primary", 0)));
        value.setSecondaryEffect(resolvers.readEffectID(input.getInt("Secondary", 0)));

        value.setCustomName(input.getOptionalValue("CustomName", String.class)
                .map(JsonTextUtil::fromJSON)
                .orElse(null));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull BeaconBlockEntity value) {
        if (value.getPrimaryEffect() != ChunkerEffectType.EMPTY) {
            output.put("Primary", resolvers.writeEffectID(value.getPrimaryEffect()));
        }

        if (value.getSecondaryEffect() != ChunkerEffectType.EMPTY) {
            output.put("Secondary", resolvers.writeEffectID(value.getSecondaryEffect()));
        }

        if (value.getCustomName() != null) {
            output.put("CustomName", JsonTextUtil.toJSON(value.getCustomName()));
        }
    }
}
