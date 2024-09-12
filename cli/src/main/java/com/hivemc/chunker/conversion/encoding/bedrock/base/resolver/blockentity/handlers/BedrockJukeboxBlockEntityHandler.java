package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.JukeboxBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for jukeboxes which also updates the block state with whether a record is present.
 */
public class BedrockJukeboxBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, JukeboxBlockEntity> implements UpdateBeforeProcessBlockEntityHandler<BedrockResolvers, JukeboxBlockEntity> {
    public BedrockJukeboxBlockEntityHandler() {
        super("Jukebox", JukeboxBlockEntity.class, JukeboxBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull JukeboxBlockEntity value) {
        CompoundTag recordItem = input.getCompound("RecordItem");
        if (recordItem != null) {
            value.setRecord(resolvers.readItem(recordItem));
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull JukeboxBlockEntity value) {
        if (value.getRecord() != null && !value.getRecord().getIdentifier().isAir()) {
            resolvers.writeItem(value.getRecord()).ifPresent(record -> output.put("RecordItem", record));
        }
    }

    @Override
    public JukeboxBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, JukeboxBlockEntity blockEntity) {
        ChunkerBlockIdentifier identifier = column.getBlock(x, y, z);

        // Don't update anything if the block isn't a jukebox
        if (identifier.getType() != ChunkerVanillaBlockType.JUKEBOX) return blockEntity;

        // Check for the record
        boolean hasRecord = blockEntity.getRecord() != null && !blockEntity.getRecord().getIdentifier().isAir();
        column.setBlock(x, y, z, identifier.copyWith(VanillaBlockStates.HAS_RECORD, hasRecord ? Bool.TRUE : Bool.FALSE));

        // No changes to the block entity
        return blockEntity;
    }
}
