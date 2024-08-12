package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.DoNotProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.GenerateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockPistonArmBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Handler for the bedrock only piston arm block entity which transfers the states between the NBT and block state.
 */
public class BedrockPistonArmBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockPistonArmBlockEntity> implements GenerateBeforeWriteBlockEntityHandler<BedrockPistonArmBlockEntity>, DoNotProcessBlockEntityHandler<BedrockPistonArmBlockEntity> {
    private static final Set<ChunkerBlockType> GENERATE_BLOCK_TYPES = Set.of(ChunkerVanillaBlockType.PISTON, ChunkerVanillaBlockType.STICKY_PISTON);

    public BedrockPistonArmBlockEntityHandler() {
        super("PistonArm", BedrockPistonArmBlockEntity.class, BedrockPistonArmBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockPistonArmBlockEntity value) {
        value.setSticky(input.getByte("Sticky", (byte) 0) == (byte) 1);
        value.setExtended(input.getByte("State", (byte) 0) == (byte) 2);
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockPistonArmBlockEntity value) {
        if (value.isExtended()) {
            // Extended
            output.put("isMovable", (byte) 1);
            output.put("LastProgress", 1f);
            output.put("NewState", (byte) 2);
            output.put("Progress", 1f);
            output.put("State", (byte) 2);
        } else {
            // Not extended
            output.put("isMovable", (byte) 1);
            output.put("LastProgress", 0f);
            output.put("NewState", (byte) 0);
            output.put("Progress", 0f);
            output.put("State", (byte) 0);
        }

        output.put("Sticky", (byte) (value.isSticky() ? 1 : 0));
    }

    @Override
    public Set<ChunkerBlockType> getGenerateBeforeWriteBlockTypes() {
        return GENERATE_BLOCK_TYPES;
    }

    @Override
    public void generateBeforeWrite(ChunkerColumn column, int x, int y, int z, BedrockPistonArmBlockEntity blockEntity, ChunkerBlockIdentifier blockIdentifier) {
        blockEntity.setExtended(blockIdentifier.getState(VanillaBlockStates.EXTENDED) == Bool.TRUE);
        blockEntity.setSticky(blockIdentifier.getType() == ChunkerVanillaBlockType.STICKY_PISTON);
    }

    @Override
    public boolean shouldRemoveBeforeProcess(ChunkerColumn column, int x, int y, int z, BedrockPistonArmBlockEntity blockEntity) {
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);
        if (blockIdentifier.getType() == ChunkerVanillaBlockType.STICKY_PISTON || blockIdentifier.getType() == ChunkerVanillaBlockType.PISTON) {
            // Set the block in the column with the note
            column.setBlock(x, y, z, blockIdentifier.copyWith(VanillaBlockStates.EXTENDED, blockEntity.isExtended() ? Bool.TRUE : Bool.FALSE));
        }

        // Remove before processing
        return true;
    }
}
