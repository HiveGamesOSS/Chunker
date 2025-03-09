package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BeaconBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerEffectType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.JsonTextUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Beacon Block Entities.
 */
public class JavaBeaconBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, BeaconBlockEntity> {
    public JavaBeaconBlockEntityHandler() {
        super("minecraft:beacon", BeaconBlockEntity.class, BeaconBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull BeaconBlockEntity value) {
        if (input.contains("primary_effect")) {
            value.setPrimaryEffect(resolvers.readEffect(input.getString("primary_effect", "minecraft:empty")));
        } else {
            value.setPrimaryEffect(resolvers.readEffectID(input.getInt("Primary", 0)));
        }
        if (input.contains("secondary_effect")) {
            value.setSecondaryEffect(resolvers.readEffect(input.getString("secondary_effect", "minecraft:empty")));
        } else {
            value.setSecondaryEffect(resolvers.readEffectID(input.getInt("Secondary", 0)));
        }

        value.setCustomName(input.getOptional("CustomName", Tag.class)
                .map(JsonTextUtil::fromNBT)
                .orElse(null));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull BeaconBlockEntity value) {
        if (value.getPrimaryEffect() != ChunkerEffectType.EMPTY) {
            if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 2)) {
                output.put("primary_effect", resolvers.writeEffect(value.getPrimaryEffect()));
            } else {
                output.put("Primary", resolvers.writeEffectID(value.getPrimaryEffect()));
            }
        }

        if (value.getSecondaryEffect() != ChunkerEffectType.EMPTY) {
            if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 2)) {
                output.put("secondary_effect", resolvers.writeEffect(value.getSecondaryEffect()));
            } else {
                output.put("Secondary", resolvers.writeEffectID(value.getSecondaryEffect()));
            }
        }

        if (value.getCustomName() != null) {
            output.put("CustomName", JsonTextUtil.toNBT(value.getCustomName(), resolvers.dataVersion()));
        }
    }
}
