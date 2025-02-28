package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerCustomBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.SingleValuePalette;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * A block identifier with a set of states.
 */
public class ChunkerBlockIdentifier implements ChunkerItemStackIdentifier {
    public static final ChunkerBlockIdentifier AIR = new ChunkerBlockIdentifier(ChunkerVanillaBlockType.AIR);
    private final ChunkerBlockType type;
    private final Map<BlockState<?>, BlockStateValue> states;
    @Nullable
    private final PreservedIdentifier preservedIdentifier;

    /**
     * Create a new block identifier with states.
     *
     * @param type                the block type.
     * @param states              the states.
     * @param preservedIdentifier whether a preserved identifier is set to use after conversion.
     */
    public ChunkerBlockIdentifier(@NotNull ChunkerBlockType type, @NotNull Map<BlockState<?>, BlockStateValue> states, @Nullable PreservedIdentifier preservedIdentifier) {
        this.type = type;
        this.states = states;
        this.preservedIdentifier = preservedIdentifier;
    }

    /**
     * Create a new block identifier with states.
     *
     * @param type   the block type.
     * @param states the states.
     */
    public ChunkerBlockIdentifier(ChunkerBlockType type, Map<BlockState<?>, BlockStateValue> states) {
        this(type, states, null);
    }

    /**
     * Create a new block identifier.
     *
     * @param type the block type.
     */
    public ChunkerBlockIdentifier(ChunkerBlockType type) {
        this(type, Collections.emptyMap(), null);
    }

    /**
     * Create a new block identifier from a custom block.
     *
     * @param identifier the namespaced identifier for the block.
     * @param states     any states which are present for the block.
     * @return a block identifier holding the custom block with states.
     */
    public static ChunkerBlockIdentifier custom(String identifier, Map<String, Object> states) {
        Map<BlockState<?>, BlockStateValue> customStates = new Object2ObjectOpenHashMap<>(states.size());
        for (Map.Entry<String, Object> entry : states.entrySet()) {
            BlockState<?> state;
            BlockStateValue value;

            // If the state is named waterlogged, we accept that as the vanilla waterlogged state
            // This allows custom blocks to persist the waterlogging state from Java -> Bedrock
            if (entry.getKey().equals("waterlogged")) {
                // We accept a boolean, string or integer for indicating it is true, otherwise false
                if (entry.getValue().equals("true") || entry.getValue().equals(true) || entry.getValue().equals(1)) {
                    value = Bool.TRUE;
                } else {
                    value = Bool.FALSE;
                }
                state = VanillaBlockStates.WATERLOGGED;
            } else {
                // Create a wrapper for the value
                value = new ChunkerCustomBlockType.CustomBlockStateValue<>(
                        entry.getValue()
                );

                // Create a single block state with the key and value
                state = new BlockState<>(
                        entry.getKey(),
                        value,
                        new BlockStateValue[]{value}
                );
            }

            // Add the custom state
            customStates.put(state, value);
        }

        // Return the new custom block
        return new ChunkerBlockIdentifier(
                new ChunkerCustomBlockType(identifier, customStates.keySet()),
                customStates,
                null
        );
    }

    /**
     * Check if a state is present in the ChunkerBlockIdentifier.
     *
     * @param state the state to check.
     * @return true if the state has a value, note: getState returns the default of the type when it fails.
     */
    public boolean containsState(BlockState<?> state) {
        return states.containsKey(state);
    }

    /**
     * Get a state from this ChunkerBlockIdentifier.
     *
     * @param blockState         the block state to fetch.
     * @param returnDefaultValue whether the default value is allowed to be returned if there can be a valid default
     *                           value if the state is absent.
     * @param <U>                the type held by the block state.
     * @return the value if it was set otherwise the default value for the block state (and returnDefaultValue is
     * enabled), can be null if the block doesn't support the state or if returnDefaultValue is false and no state is
     * present.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <U extends BlockStateValue> U getState(BlockState<U> blockState, boolean returnDefaultValue) {
        U value = (U) states.get(blockState);

        // Only return the default if the type contains this blockState
        if (value == null && returnDefaultValue && getType().getStates().contains(blockState)) {
            value = blockState.getDefault();
        }

        return value;
    }

    /**
     * Get a state from this ChunkerBlockIdentifier.
     *
     * @param blockState the block state to fetch.
     * @param <U>        the type held by the block state.
     * @return the value if it was set otherwise the default value for the block state, can be null if the block doesn't
     * support the state.
     */
    @Nullable
    public <U extends BlockStateValue> U getState(BlockState<U> blockState) {
        return getState(blockState, true);
    }

    /**
     * Get the block type used by this identifier.
     *
     * @return the block type.
     */
    public ChunkerBlockType getType() {
        return type;
    }

    /**
     * Get an immutable map of the states which have been set (not default).
     *
     * @return a map of the states.
     */
    public Map<BlockState<?>, BlockStateValue> getPresentStates() {
        return Collections.unmodifiableMap(states);
    }

    /**
     * Check whether this block identifier is air.
     *
     * @return true if this block identifier matches air.
     */
    public boolean isAir() {
        return type == ChunkerVanillaBlockType.AIR;
    }

    /**
     * Get the RGB color of the block.
     *
     * @return the RGB color of the block.
     */
    public int getRGBColor() {
        return type.getRGBColor();
    }

    /**
     * Check whether the block has an RGB color.
     *
     * @return true if an RGB color is present.
     */
    public boolean hasRGBColor() {
        return getRGBColor() != 0;
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

    /**
     * Create a 16x16 palette filled with this block type.
     *
     * @return the newly created palette.
     */
    public Palette<ChunkerBlockIdentifier> asFilledChunkPalette() {
        // Create the palette with the keys
        return SingleValuePalette.chunk(this);
    }

    /**
     * Copy this block identifier with a specific state set.
     *
     * @param blockState the block state key.
     * @param value      the value for the block state.
     * @param <T>        the type of the state.
     * @return a new block identifier with the same states and preservedIdentifier but with the new state set.
     */
    public <T extends BlockStateValue> ChunkerBlockIdentifier copyWith(BlockState<T> blockState, T value) {
        Map<BlockState<?>, BlockStateValue> statesCopy = new Object2ObjectOpenHashMap<>(states);
        statesCopy.put(blockState, value);
        return new ChunkerBlockIdentifier(type, statesCopy, preservedIdentifier);
    }

    /**
     * Copy this block identifier without a specific state set.
     *
     * @param blockState the block state key.
     * @param <T>        the type of the state.
     * @return a new block identifier with the same states and preservedIdentifier but without the state set.
     */
    public <T extends BlockStateValue> ChunkerBlockIdentifier copyWithout(BlockState<T> blockState) {
        if (!states.containsKey(blockState)) return this;
        Map<BlockState<?>, BlockStateValue> statesCopy = new Object2ObjectOpenHashMap<>(states);
        statesCopy.remove(blockState);
        return new ChunkerBlockIdentifier(type, statesCopy, preservedIdentifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkerBlockIdentifier that)) return false;
        return Objects.equals(getType(), that.getType()) && Objects.equals(states, that.states) && Objects.equals(getPreservedIdentifier(), that.getPreservedIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), states, getPreservedIdentifier());
    }

    @Override
    public ChunkerBlockType getItemStackType() {
        return type;
    }

    @Override
    public String toString() {
        return "ChunkerBlockIdentifier{" +
                "type=" + type +
                ", states=" + toStateString() +
                (preservedIdentifier == null ? "" : ", preservedIdentifier=" + preservedIdentifier) +
                '}';
    }

    /**
     * Get the states for this identifier serialized in the form [x=y].
     *
     * @return the serialized states, will be [] if no states are present.
     */
    public String toStateString() {
        if (states.isEmpty()) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<BlockState<?>, BlockStateValue> entry : states.entrySet()) {
            if (!builder.isEmpty()) {
                builder.append(",");
            }
            builder.append(entry.getKey().getName()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }
}
