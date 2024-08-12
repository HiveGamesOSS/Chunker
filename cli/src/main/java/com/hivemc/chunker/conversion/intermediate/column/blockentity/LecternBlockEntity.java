package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a Lectern Block Entity.
 */
public class LecternBlockEntity extends BlockEntity {
    @Nullable
    private ChunkerItemStack book;
    private int page;

    /**
     * Get the book held in the Lectern.
     *
     * @return null/air if no book.
     */
    @Nullable
    public ChunkerItemStack getBook() {
        return book;
    }

    /**
     * Set the book held in the Lectern.
     *
     * @param book the book, null/air if no book.
     */
    public void setBook(@Nullable ChunkerItemStack book) {
        this.book = book;
    }

    /**
     * Get the page number the lectern is currently turned to.
     * Note: Chunker format assumes 2 sides per open page.
     *
     * @return the page number.
     */
    public int getPage() {
        return page;
    }

    /**
     * Set the page number the lectern is turned to.
     *
     * @param page the page number, assuming 2 sides per open page.
     */
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LecternBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return getPage() == that.getPage() && Objects.equals(getBook(), that.getBook());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBook(), getPage());
    }
}
