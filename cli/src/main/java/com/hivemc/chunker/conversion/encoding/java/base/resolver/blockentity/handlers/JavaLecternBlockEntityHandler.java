package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.LecternBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Lectern Block Entities.
 */
public class JavaLecternBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, LecternBlockEntity> {
    public JavaLecternBlockEntityHandler() {
        super("minecraft:lectern", LecternBlockEntity.class, LecternBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull LecternBlockEntity value) {
        value.setPage(input.getInt("Page", 0));
        CompoundTag book = input.getCompound("Book");
        if (book != null) {
            value.setBook(resolvers.readItem(book));
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull LecternBlockEntity value) {
        output.put("Page", value.getPage());

        if (value.getBook() != null && !value.getBook().getIdentifier().isAir()) {
            resolvers.writeItem(value.getBook()).ifPresent(book -> output.put("Book", book));
        }
    }
}
