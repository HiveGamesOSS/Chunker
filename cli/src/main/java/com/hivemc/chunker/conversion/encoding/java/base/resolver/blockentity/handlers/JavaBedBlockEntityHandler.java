package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.DoNotWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.GenerateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BedBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Handler for generating bed block entities (since 26.2 made beds no longer block entities).
 */
public class JavaBedBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, BedBlockEntity> implements DoNotWriteBlockEntityHandler<BedBlockEntity>, GenerateBeforeProcessBlockEntityHandler<BedBlockEntity> {
    private static final Set<ChunkerBlockType> BEDS = Set.of(
            ChunkerVanillaBlockType.WHITE_BED,
            ChunkerVanillaBlockType.ORANGE_BED,
            ChunkerVanillaBlockType.MAGENTA_BED,
            ChunkerVanillaBlockType.LIGHT_BLUE_BED,
            ChunkerVanillaBlockType.YELLOW_BED,
            ChunkerVanillaBlockType.LIME_BED,
            ChunkerVanillaBlockType.PINK_BED,
            ChunkerVanillaBlockType.GRAY_BED,
            ChunkerVanillaBlockType.LIGHT_GRAY_BED,
            ChunkerVanillaBlockType.CYAN_BED,
            ChunkerVanillaBlockType.PURPLE_BED,
            ChunkerVanillaBlockType.BLUE_BED,
            ChunkerVanillaBlockType.BROWN_BED,
            ChunkerVanillaBlockType.GREEN_BED,
            ChunkerVanillaBlockType.RED_BED,
            ChunkerVanillaBlockType.BLACK_BED
    );

    public JavaBedBlockEntityHandler() {
        // Not written on Java 26.2
        super("minecraft:bed", BedBlockEntity.class, BedBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull BedBlockEntity value) {
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull BedBlockEntity value) {
    }

    @Override
    public Set<ChunkerBlockType> getGenerateBeforeProcessBlockTypes() {
        return BEDS;
    }

    @Override
    public void generateBeforeProcess(ChunkerColumn column, int x, int y, int z, BedBlockEntity blockEntity, ChunkerBlockIdentifier blockIdentifier) {
        // Just use the defaults
    }
}
