package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.DoNotProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.GenerateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockSporeBlossomBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Handler for spore blossom which ensures the block entity is generated for writing to Bedrock / removed otherwise.
 */
public class BedrockSporeBlossomBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockSporeBlossomBlockEntity> implements GenerateBeforeWriteBlockEntityHandler<BedrockSporeBlossomBlockEntity>, DoNotProcessBlockEntityHandler<BedrockSporeBlossomBlockEntity> {
    private static final Set<ChunkerBlockType> GENERATE_BLOCK_TYPES = Set.of(ChunkerVanillaBlockType.SPORE_BLOSSOM);

    public BedrockSporeBlossomBlockEntityHandler() {
        super("SporeBlossom", BedrockSporeBlossomBlockEntity.class, BedrockSporeBlossomBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockSporeBlossomBlockEntity value) {
        // Nothing to write
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockSporeBlossomBlockEntity value) {
        // Nothing to write
    }

    @Override
    public Set<ChunkerBlockType> getGenerateBeforeWriteBlockTypes() {
        return GENERATE_BLOCK_TYPES;
    }

    @Override
    public void generateBeforeWrite(ChunkerColumn column, int x, int y, int z, BedrockSporeBlossomBlockEntity blockEntity, ChunkerBlockIdentifier blockIdentifier) {
        // Currently do nothing, the initial blockEntity tag is fine
    }
}
