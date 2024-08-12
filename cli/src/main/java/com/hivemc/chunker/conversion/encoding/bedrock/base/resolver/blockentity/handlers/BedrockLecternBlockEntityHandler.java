package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.LecternBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Handler for lecterns which also updates the block state with whether a book is present.
 */
public class BedrockLecternBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, LecternBlockEntity> implements UpdateBeforeProcessBlockEntityHandler<BedrockResolvers, LecternBlockEntity> {
    public BedrockLecternBlockEntityHandler() {
        super("Lectern", LecternBlockEntity.class, LecternBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull LecternBlockEntity value) {
        value.setPage(input.getInt("page", 0) * 2); // Chunker format assumes 2 open pages
        CompoundTag book = input.getCompound("book");
        if (book != null) {
            value.setBook(resolvers.readItem(book));
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull LecternBlockEntity value) {
        output.put("page", value.getPage() / 2);

        if (value.getBook() != null && !value.getBook().getIdentifier().isAir()) {
            Optional<CompoundTag> item = resolvers.writeItem(value.getBook());
            if (item.isPresent()) {
                output.put("hasBook", (byte) 1);
                output.put("book", item.get());
                output.put("totalPages", 0);
                return;
            }
        }
        output.put("hasBook", (byte) 0);
    }

    @Override
    public LecternBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, LecternBlockEntity blockEntity) {
        ChunkerBlockIdentifier identifier = column.getBlock(x, y, z);
        boolean hasBook = blockEntity.getBook() != null && !blockEntity.getBook().getIdentifier().isAir();
        column.setBlock(x, y, z, identifier.copyWith(VanillaBlockStates.HAS_BOOK, hasBook ? Bool.TRUE : Bool.FALSE));

        // No changes to the block entity
        return blockEntity;
    }
}
