package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.SpawnerBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Handler for Mob Spawner Block Entities.
 */
public class JavaLegacySpawnerBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, SpawnerBlockEntity> {
    public JavaLegacySpawnerBlockEntityHandler() {
        super("MobSpawner", SpawnerBlockEntity.class, SpawnerBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull SpawnerBlockEntity value) {
        value.setDelay(input.getShort("Delay", (short) 20));
        value.setSpawnRange(input.getShort("SpawnRange", (short) 4));
        value.setMaxSpawnDelay(input.getShort("MaxSpawnDelay", (short) 800));
        value.setMinSpawnDelay(input.getShort("MinSpawnDelay", (short) 200));
        value.setSpawnCount(input.getShort("SpawnCount", (short) 4));
        value.setRequiredPlayerRange(input.getShort("RequiredPlayerRange", (short) 16));
        value.setMaxNearbyEntities(input.getShort("MaxNearbyEntities", (short) 6));

        // Get the entity type
        CompoundTag spawnData = input.getCompound("SpawnData");
        if (spawnData != null) {
            if (spawnData.contains("id")) {
                String identifier = spawnData.getString("id");
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
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull SpawnerBlockEntity value) {
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
            resolvers.converter().logMissingMapping(Converter.MissingMappingType.ENTITY_TYPE, value.getEntityType().toString());

            // Use null
            return null;
        }));

        if (identifier.isPresent()) {
            // Write the entity type
            CompoundTag spawnData = new CompoundTag();
            spawnData.put("id", identifier.get());

            output.put("SpawnData", spawnData);
        }
    }
}
