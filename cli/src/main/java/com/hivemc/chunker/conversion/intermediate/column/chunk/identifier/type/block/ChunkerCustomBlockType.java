package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block;

import com.google.common.collect.Sets;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A block type which is custom and uses an identifier.
 * This assumes the block doesn't have a color / isn't solid.
 */
public class ChunkerCustomBlockType implements ChunkerBlockType {
    private final String identifier;
    private final Set<BlockState<?>> blockStates;

    /**
     * Create a new custom block type.
     *
     * @param identifier  the identifier for the block.
     * @param blockStates the block states which are supported.
     */
    public ChunkerCustomBlockType(String identifier, Set<BlockState<?>> blockStates) {
        this.identifier = identifier;

        // All custom blocks should also have the default block states used for every block
        // This allows waterlogging to correctly be stored and default to false
        this.blockStates = Sets.union(VanillaBlockStates.DEFAULT_BLOCK_STATES, blockStates);
    }

    /**
     * Get the namespaced identifier for the block.
     *
     * @return the identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int getRGBColor() {
        return 0; // Use transparent for now
    }

    @Override
    public boolean isAllFacesSolid() {
        return false; // Use false for now
    }

    @Override
    public Optional<Class<? extends BlockEntity>> getBlockEntityClass() {
        return Optional.empty(); // Custom Block Entities not supported
    }

    @Override
    public Set<BlockState<?>> getStates() {
        return blockStates;
    }

    @Override
    public String toString() {
        return "ChunkerCustomBlockType{" +
                "identifier='" + getIdentifier() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkerCustomBlockType that)) return false;
        return Objects.equals(getIdentifier(), that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifier());
    }

    /**
     * Custom block state used for any state.
     * Because we don't know the definition ahead of time, we only assume it has one value.
     *
     * @param <T> the type held by the block state.
     */
    public static class CustomBlockStateValue<T> implements BlockStateValue {
        private final T stateValue;

        /**
         * Create a new custom block state from a value.
         *
         * @param stateValue the single value is set.
         */
        public CustomBlockStateValue(T stateValue) {
            this.stateValue = stateValue;
        }

        /**
         * Get the state value which is set.
         *
         * @return the value.
         */
        public T getStateValue() {
            return stateValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CustomBlockStateValue<?> that)) return false;
            return Objects.equals(getStateValue(), that.getStateValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getStateValue());
        }

        @Override
        public String toString() {
            return "CustomBlockStateValue{" +
                    "stateValue=" + getStateValue() +
                    '}';
        }
    }
}
