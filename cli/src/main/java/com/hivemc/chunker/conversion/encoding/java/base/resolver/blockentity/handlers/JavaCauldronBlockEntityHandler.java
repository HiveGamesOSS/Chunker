package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.DoNotWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.GenerateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.CauldronBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Handler for generating cauldron block entities (since they're not in Java).
 */
public class JavaCauldronBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, CauldronBlockEntity> implements DoNotWriteBlockEntityHandler<CauldronBlockEntity>, GenerateBeforeProcessBlockEntityHandler<CauldronBlockEntity> {
    private static final Set<ChunkerBlockType> GENERATE_BLOCK_TYPES = Set.of(
            ChunkerVanillaBlockType.CAULDRON,
            ChunkerVanillaBlockType.WATER_CAULDRON,
            ChunkerVanillaBlockType.LAVA_CAULDRON,
            ChunkerVanillaBlockType.POWDER_SNOW_CAULDRON
    );

    public JavaCauldronBlockEntityHandler() {
        // Not written on Java
        super("minecraft:cauldron", CauldronBlockEntity.class, CauldronBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull CauldronBlockEntity value) {
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull CauldronBlockEntity value) {
    }

    @Override
    public Set<ChunkerBlockType> getGenerateBeforeProcessBlockTypes() {
        return GENERATE_BLOCK_TYPES;
    }

    @Override
    public void generateBeforeProcess(ChunkerColumn column, int x, int y, int z, CauldronBlockEntity blockEntity, ChunkerBlockIdentifier blockIdentifier) {
        // Just use the defaults
    }
}
