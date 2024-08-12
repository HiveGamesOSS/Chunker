package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.DoNotProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.GenerateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.type.JavaLegacyNoteBlockBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Note;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Handler for the Java specific Note Block entity which converts the note between NBT and the block state.
 */
public class JavaLegacyNoteBlockBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, JavaLegacyNoteBlockBlockEntity> implements GenerateBeforeWriteBlockEntityHandler<JavaLegacyNoteBlockBlockEntity>, DoNotProcessBlockEntityHandler<JavaLegacyNoteBlockBlockEntity> {
    private static final Set<ChunkerBlockType> GENERATE_BLOCK_TYPES = Set.of(ChunkerVanillaBlockType.NOTE_BLOCK);

    public JavaLegacyNoteBlockBlockEntityHandler() {
        super("Music", JavaLegacyNoteBlockBlockEntity.class, JavaLegacyNoteBlockBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull JavaLegacyNoteBlockBlockEntity value) {
        value.setNote(Note.class.getEnumConstants()[input.getByte("note", (byte) 0)]);
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull JavaLegacyNoteBlockBlockEntity value) {
        output.put("note", (byte) (value.getNote() == null ? 0 : value.getNote().ordinal()));
    }

    @Override
    public Set<ChunkerBlockType> getGenerateBeforeWriteBlockTypes() {
        return GENERATE_BLOCK_TYPES;
    }

    @Override
    public void generateBeforeWrite(ChunkerColumn column, int x, int y, int z, JavaLegacyNoteBlockBlockEntity blockEntity, ChunkerBlockIdentifier blockIdentifier) {
        blockEntity.setNote(blockIdentifier.getState(VanillaBlockStates.NOTE));
    }

    @Override
    public boolean shouldRemoveBeforeProcess(ChunkerColumn column, int x, int y, int z, JavaLegacyNoteBlockBlockEntity blockEntity) {
        if (blockEntity.getNote() != null) {
            ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);
            if (blockIdentifier.getType() == ChunkerVanillaBlockType.NOTE_BLOCK) {
                // Set the block in the column with the note
                column.setBlock(x, y, z, blockIdentifier.copyWith(VanillaBlockStates.NOTE, blockEntity.getNote()));
            }
        }

        // Remove before processing
        return true;
    }
}
