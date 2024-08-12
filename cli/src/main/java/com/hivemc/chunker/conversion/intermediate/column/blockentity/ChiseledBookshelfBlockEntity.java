package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a Chiseled Bookshelf Block Entity.
 */
public class ChiseledBookshelfBlockEntity extends BlockEntity {
    private final ChunkerItemStack[] books = new ChunkerItemStack[6];

    /**
     * Get the modifiable array of books held by this block.
     *
     * @return an array with a length of 6 where null/air indicates a missing book.
     */
    public ChunkerItemStack[] getBooks() {
        return books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiseledBookshelfBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.deepEquals(getBooks(), that.getBooks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), Arrays.hashCode(getBooks()));
    }
}
