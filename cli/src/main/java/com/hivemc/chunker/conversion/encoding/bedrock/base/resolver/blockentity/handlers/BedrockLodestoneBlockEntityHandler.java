package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.DoNotProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.GenerateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockLodestoneBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Generates an empty Bedrock only block entity for the Lodestone.
 */
public class BedrockLodestoneBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockLodestoneBlockEntity> implements GenerateBeforeWriteBlockEntityHandler<BedrockLodestoneBlockEntity>, DoNotProcessBlockEntityHandler<BedrockLodestoneBlockEntity> {
    private static final Set<ChunkerBlockType> GENERATE_BLOCK_TYPES = Set.of(ChunkerVanillaBlockType.LODESTONE);

    public BedrockLodestoneBlockEntityHandler() {
        super("Lodestone", BedrockLodestoneBlockEntity.class, BedrockLodestoneBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockLodestoneBlockEntity value) {

    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockLodestoneBlockEntity value) {

    }

    @Override
    public Set<ChunkerBlockType> getGenerateBeforeWriteBlockTypes() {
        return GENERATE_BLOCK_TYPES;
    }

    @Override
    public void generateBeforeWrite(ChunkerColumn column, int x, int y, int z, BedrockLodestoneBlockEntity blockEntity, ChunkerBlockIdentifier blockIdentifier) {
        // Currently do nothing, the initial blockEntity tag is fine
    }
}
