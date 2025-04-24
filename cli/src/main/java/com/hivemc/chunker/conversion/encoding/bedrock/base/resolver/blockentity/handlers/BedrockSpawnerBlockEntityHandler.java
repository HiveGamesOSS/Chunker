package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.SpawnerBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Handler for mob spawners.
 */
public class BedrockSpawnerBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, SpawnerBlockEntity> {
    public BedrockSpawnerBlockEntityHandler() {
        super("MobSpawner", SpawnerBlockEntity.class, SpawnerBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull SpawnerBlockEntity value) {
        value.setDelay(input.getShort("Delay", (short) 20));
        value.setSpawnRange(input.getShort("SpawnRange", (short) 4));
        value.setMaxSpawnDelay(input.getShort("MaxSpawnDelay", (short) 800));
        value.setMinSpawnDelay(input.getShort("MinSpawnDelay", (short) 200));
        value.setSpawnCount(input.getShort("SpawnCount", (short) 4));
        value.setRequiredPlayerRange(input.getShort("RequiredPlayerRange", (short) 16));
        value.setMaxNearbyEntities(input.getShort("MaxNearbyEntities", (short) 6));

        if (input.contains("EntityIdentifier")) {
            String identifier = input.getString("EntityIdentifier");
            if (!identifier.isEmpty() && !identifier.equals("minecraft:")) {
                value.setEntityType(resolvers.entityTypeResolver().to(identifier).orElseGet(() -> {
                    // Report missing mapping
                    resolvers.converter().logMissingMapping(Converter.MissingMappingType.ENTITY_TYPE, identifier);

                    // Use null
                    return null;
                }));
            }
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull SpawnerBlockEntity value) {
        output.put("Delay", value.getDelay());
        output.put("SpawnRange", value.getSpawnRange());
        output.put("MaxSpawnDelay", value.getMaxSpawnDelay());
        output.put("MinSpawnDelay", value.getMinSpawnDelay());
        output.put("SpawnCount", value.getSpawnCount());
        output.put("RequiredPlayerRange", value.getRequiredPlayerRange());
        output.put("MaxNearbyEntities", value.getMaxNearbyEntities());

        // Resolve the entity identifier to use
        Optional<String> identifier = Optional.ofNullable(value.getEntityType()).map(type -> resolvers.entityTypeResolver().from(type).orElseGet(() -> {
            // Report missing mapping
            resolvers.converter().logMissingMapping(Converter.MissingMappingType.ENTITY_TYPE, String.valueOf(value.getEntityType()));

            // Use null
            return null;
        }));
        identifier.ifPresent(s -> output.put("EntityIdentifier", s));
    }
}
