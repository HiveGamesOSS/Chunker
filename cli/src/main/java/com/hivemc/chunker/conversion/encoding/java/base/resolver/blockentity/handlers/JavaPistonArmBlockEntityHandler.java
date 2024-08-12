package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.DoNotProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.GenerateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.type.JavaPistonMovingBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Handler for generating a piston arm entity (Java only).
 */
public class JavaPistonArmBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, JavaPistonMovingBlockEntity> implements GenerateBeforeWriteBlockEntityHandler<JavaPistonMovingBlockEntity>, DoNotProcessBlockEntityHandler<JavaPistonMovingBlockEntity> {
    private static final Set<ChunkerBlockType> GENERATE_BLOCK_TYPES = Set.of(ChunkerVanillaBlockType.MOVING_PISTON_JAVA);

    public JavaPistonArmBlockEntityHandler() {
        super("minecraft:piston", JavaPistonMovingBlockEntity.class, JavaPistonMovingBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull JavaPistonMovingBlockEntity value) {

    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull JavaPistonMovingBlockEntity value) {

    }

    @Override
    public Set<ChunkerBlockType> getGenerateBeforeWriteBlockTypes() {
        return GENERATE_BLOCK_TYPES;
    }

    @Override
    public void generateBeforeWrite(ChunkerColumn column, int x, int y, int z, JavaPistonMovingBlockEntity blockEntity, ChunkerBlockIdentifier blockIdentifier) {

    }
}
