package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.PreservedIdentifier;
import com.hivemc.chunker.resolver.property.Property;
import com.hivemc.chunker.resolver.property.PropertyHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * A stack of items in Minecraft which can be inside an inventory / entity / block entity.
 */
public class ChunkerItemStack extends PropertyHolder<ChunkerItemStack> {
    private final ChunkerItemStackIdentifier identifier;
    @Nullable
    private final PreservedIdentifier preservedIdentifier;

    /**
     * Create a new item stack.
     *
     * @param identifier          the identifier which the stack holds.
     * @param preservedIdentifier whether a preserved identifier is set to use after conversion.
     */
    public ChunkerItemStack(ChunkerItemStackIdentifier identifier, @Nullable PreservedIdentifier preservedIdentifier) {
        this.identifier = identifier;
        this.preservedIdentifier = preservedIdentifier;
    }

    /**
     * Create a new item stack.
     *
     * @param identifier the identifier which the stack holds.
     */
    public ChunkerItemStack(ChunkerItemStackIdentifier identifier) {
        this(identifier, (PreservedIdentifier) null);
    }

    /**
     * Create a new item stack.
     *
     * @param identifier          the identifier which the stack holds.
     * @param preservedIdentifier whether a preserved identifier is set to use after conversion.
     * @param properties          the properties which are set on the item.
     */
    public ChunkerItemStack(ChunkerItemStackIdentifier identifier, @Nullable PreservedIdentifier preservedIdentifier, Map<Property<? super ChunkerItemStack, ?>, Object> properties) {
        super(properties);
        this.identifier = identifier;
        this.preservedIdentifier = preservedIdentifier;
    }

    /**
     * Create a new item stack.
     *
     * @param identifier the identifier which the stack holds.
     * @param properties the properties which are set on the item.
     */
    public ChunkerItemStack(ChunkerItemStackIdentifier identifier, Map<Property<? super ChunkerItemStack, ?>, Object> properties) {
        this(identifier, null, properties);
    }

    /**
     * Get the item used for this identifier.
     *
     * @return the item/block identifier.
     */
    public ChunkerItemStackIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * The preserved identifier which should replace the output when this block is converted.
     *
     * @return the identifier otherwise null if one is not set.
     */
    @Nullable
    public PreservedIdentifier getPreservedIdentifier() {
        return preservedIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkerItemStack that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getIdentifier(), that.getIdentifier()) && Objects.equals(preservedIdentifier, that.preservedIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIdentifier(), preservedIdentifier);
    }

    @Override
    public String toString() {
        return "ChunkerItemStack{" +
                "identifier=" + identifier +
                ", properties=" + properties +
                (preservedIdentifier == null ? "" : ", preservedIdentifier=" + preservedIdentifier) +
                '}';
    }
}
