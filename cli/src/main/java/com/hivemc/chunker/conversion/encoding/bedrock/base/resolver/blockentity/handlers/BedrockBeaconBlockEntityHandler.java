package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BeaconBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Beacons.
 */
public class BedrockBeaconBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BeaconBlockEntity> {
    public BedrockBeaconBlockEntityHandler() {
        super("Beacon", BeaconBlockEntity.class, BeaconBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BeaconBlockEntity value) {
        value.setPrimaryEffect(resolvers.readEffectID(input.getInt("primary", 0)));
        value.setSecondaryEffect(resolvers.readEffectID(input.getInt("secondary", (short) 0)));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BeaconBlockEntity value) {
        output.put("primary", resolvers.writeEffectID(value.getPrimaryEffect()));
        output.put("secondary", resolvers.writeEffectID(value.getSecondaryEffect()));
    }
}
