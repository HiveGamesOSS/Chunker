package com.hivemc.chunker.conversion.encoding.base.resolver.identifier;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Multimap;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateMappingGroup;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.VersionedStateMappingGroup;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

/**
 * A mapping which turns an input identifier with states into a Chunker output with properties.
 */
public class BlockMapping {
    private final String identifier;
    private final ChunkerBlockType blockType;
    private final NavigableMap<String, Object> states;
    private final @Nullable VersionedStateMappingGroup versionedStateMappingGroup;
    private final NavigableMap<BlockState<?>, BlockStateValue> blockStates;

    /**
     * Create a new block mapping.
     *
     * @param identifier                 the input identifier.
     * @param blockType                  the output block type.
     * @param states                     the states used for matching / applying to the input.
     * @param versionedStateMappingGroup the state mapping group to use for the mapping or null if not present.
     * @param blockStates                the block states to set / match for the output.
     */
    public BlockMapping(String identifier, ChunkerBlockType blockType, NavigableMap<String, Object> states, @Nullable VersionedStateMappingGroup versionedStateMappingGroup, NavigableMap<BlockState<?>, BlockStateValue> blockStates) {
        this.identifier = identifier;
        this.blockType = blockType;
        this.states = states;
        this.versionedStateMappingGroup = versionedStateMappingGroup;
        this.blockStates = blockStates;
    }

    /**
     * Create a mapping that maps an identifier with no states to a block type.
     *
     * @param identifier the input identifier.
     * @param blockType  the output identifier.
     * @return the newly created block mapping.
     */
    public static BlockMapping of(String identifier, ChunkerBlockType blockType) {
        return new BlockMapping(identifier, blockType, Collections.emptyNavigableMap(), null, Collections.emptyNavigableMap());
    }

    /**
     * Create a mapping that maps an identifier with a state to a block type.
     *
     * @param identifier the input identifier.
     * @param stateName  the input state name.
     * @param stateValue the input state value.
     * @param blockType  the output identifier.
     * @param <S>        the type of the input state value.
     * @return the newly created block mapping.
     */
    public static <S> BlockMapping of(String identifier, String stateName, S stateValue, ChunkerBlockType blockType) {
        return new BlockMapping(identifier, blockType, ImmutableSortedMap.of(stateName, stateValue), null, Collections.emptyNavigableMap());
    }

    /**
     * Create a mapping that maps an identifier with a state to a block type with a state.
     *
     * @param identifier      the input identifier.
     * @param stateName       the input state name.
     * @param stateValue      the input state value.
     * @param blockType       the output identifier.
     * @param blockState      the output state type.
     * @param blockStateValue the output state value.
     * @param <S>             the type of the input state value.
     * @param <V>             the type of the output state value.
     * @return the newly created block mapping.
     */
    public static <S, V extends BlockStateValue> BlockMapping of(String identifier, String stateName, S stateValue, ChunkerBlockType blockType, BlockState<V> blockState, V blockStateValue) {
        return new BlockMapping(identifier, blockType, ImmutableSortedMap.of(stateName, stateValue), null, ImmutableSortedMap.of(blockState, blockStateValue));
    }

    /**
     * Create a mapping that maps an identifier with a state to a block type with a state and uses a state mapping group
     * for the remaining states.
     *
     * @param identifier        the input identifier.
     * @param stateName         the input state name.
     * @param stateValue        the input state value.
     * @param blockType         the output identifier.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param blockState        the output state type.
     * @param blockStateValue   the output state value.
     * @param <S>               the type of the input state value.
     * @param <V>               the type of the output state value.
     * @return the newly created block mapping.
     */
    public static <S, V extends BlockStateValue> BlockMapping of(String identifier, String stateName, S stateValue, ChunkerBlockType blockType, VersionedStateMappingGroup stateMappingGroup, BlockState<V> blockState, V blockStateValue) {
        return new BlockMapping(identifier, blockType, ImmutableSortedMap.of(stateName, stateValue), stateMappingGroup, ImmutableSortedMap.of(blockState, blockStateValue));
    }

    /**
     * Create a mapping that maps an identifier to a block type with states.
     *
     * @param identifier       the input identifier.
     * @param blockType        the output identifier.
     * @param blockStateValues the output states (state type to state value).
     * @param <V>              the type of the output state value.
     * @return the newly created block mapping.
     */
    public static <V extends BlockStateValue> BlockMapping of(String identifier, ChunkerBlockType blockType, Map<BlockState<? extends V>, ? extends V> blockStateValues) {
        return new BlockMapping(identifier, blockType, Collections.emptyNavigableMap(), null, ImmutableSortedMap.copyOf(blockStateValues));
    }

    /**
     * Create a mapping that maps an identifier with a state to a block type with states.
     *
     * @param identifier       the input identifier.
     * @param stateName        the input state name.
     * @param stateValue       the input state value.
     * @param blockType        the output identifier.
     * @param blockStateValues the output states (state type to state value).
     * @param <S>              the type of the input state value.
     * @param <V>              the type of the output state value.
     * @return the newly created block mapping.
     */
    public static <S, V extends BlockStateValue> BlockMapping of(String identifier, String stateName, S stateValue, ChunkerBlockType blockType, Map<BlockState<? extends V>, ? extends V> blockStateValues) {
        return new BlockMapping(identifier, blockType, ImmutableSortedMap.of(stateName, stateValue), null, ImmutableSortedMap.copyOf(blockStateValues));
    }

    /**
     * Create a mapping that maps an identifier with a state to a block type with states and uses a state mapping group
     * for the remaining states.
     *
     * @param identifier        the input identifier.
     * @param stateName         the input state name.
     * @param stateValue        the input state value.
     * @param blockType         the output identifier.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param blockStateValues  the output states (state type to state value).
     * @param <S>               the type of the input state value.
     * @param <V>               the type of the output state value.
     * @return the newly created block mapping.
     */
    public static <S, V extends BlockStateValue> BlockMapping of(String identifier, String stateName, S stateValue, ChunkerBlockType blockType, VersionedStateMappingGroup stateMappingGroup, Map<BlockState<? extends V>, ? extends V> blockStateValues) {
        return new BlockMapping(identifier, blockType, ImmutableSortedMap.of(stateName, stateValue), stateMappingGroup, ImmutableSortedMap.copyOf(blockStateValues));
    }

    /**
     * Create a mapping that maps an identifier with two states to a block type with a state.
     *
     * @param identifier      the input identifier.
     * @param stateName1      the first input state name.
     * @param stateValue1     the first input state value.
     * @param stateName2      the second input state name.
     * @param stateValue2     the second input state value.
     * @param blockType       the output identifier.
     * @param blockState      the output state type.
     * @param blockStateValue the output state value.
     * @param <S1>            the type of the first input state value.
     * @param <S2>            the type of the first second state value.
     * @param <V>             the type of the output state value.
     * @return the newly created block mapping.
     */
    public static <S1, S2, V extends BlockStateValue> BlockMapping of(String identifier, String stateName1, S1 stateValue1, String stateName2, S2 stateValue2, ChunkerBlockType blockType, BlockState<V> blockState, V blockStateValue) {
        return new BlockMapping(identifier, blockType, ImmutableSortedMap.of(stateName1, stateValue1, stateName2, stateValue2), null, ImmutableSortedMap.of(blockState, blockStateValue));
    }

    /**
     * Create a mapping that maps an identifier with two states to a block type with a state and uses a state mapping
     * group for the remaining states.
     *
     * @param identifier        the input identifier.
     * @param stateName1        the first input state name.
     * @param stateValue1       the first input state value.
     * @param stateName2        the second input state name.
     * @param stateValue2       the second input state value.
     * @param blockType         the output identifier.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param blockState        the output state type.
     * @param blockStateValue   the output state value.
     * @param <S1>              the type of the first input state value.
     * @param <S2>              the type of the first second state value.
     * @param <V>               the type of the output state value.
     * @return the newly created block mapping.
     */
    public static <S1, S2, V extends BlockStateValue> BlockMapping of(String identifier, String stateName1, S1 stateValue1, String stateName2, S2 stateValue2, ChunkerBlockType blockType, VersionedStateMappingGroup stateMappingGroup, BlockState<V> blockState, V blockStateValue) {
        return new BlockMapping(identifier, blockType, ImmutableSortedMap.of(stateName1, stateValue1, stateName2, stateValue2), stateMappingGroup, ImmutableSortedMap.of(blockState, blockStateValue));
    }

    /**
     * Create a mapping that maps an identifier with two states to a block type and uses a state mapping group for the
     * remaining states.
     *
     * @param identifier        the input identifier.
     * @param stateName1        the first input state name.
     * @param stateValue1       the first input state value.
     * @param stateName2        the second input state name.
     * @param stateValue2       the second input state value.
     * @param blockType         the output identifier.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param <S1>              the type of the first input state value.
     * @param <S2>              the type of the first second state value.
     * @return the newly created block mapping.
     */
    public static <S1, S2> BlockMapping of(String identifier, String stateName1, S1 stateValue1, String stateName2, S2 stateValue2, ChunkerBlockType blockType, VersionedStateMappingGroup stateMappingGroup) {
        return new BlockMapping(identifier, blockType, ImmutableSortedMap.of(stateName1, stateValue1, stateName2, stateValue2), stateMappingGroup, Collections.emptyNavigableMap());
    }

    /**
     * Create a mapping that maps an identifier with two states to a block type.
     *
     * @param identifier  the input identifier.
     * @param stateName1  the first input state name.
     * @param stateValue1 the first input state value.
     * @param stateName2  the second input state name.
     * @param stateValue2 the second input state value.
     * @param blockType   the output identifier.
     * @param <S1>        the type of the first input state value.
     * @param <S2>        the type of the first second state value.
     * @return the newly created block mapping.
     */
    public static <S1, S2> BlockMapping of(String identifier, String stateName1, S1 stateValue1, String stateName2, S2 stateValue2, ChunkerBlockType blockType) {
        return new BlockMapping(identifier, blockType, ImmutableSortedMap.of(stateName1, stateValue1, stateName2, stateValue2), null, Collections.emptyNavigableMap());
    }

    /**
     * Create a mapping that maps an identifier to a block type with a state.
     *
     * @param identifier      the input identifier.
     * @param blockType       the output identifier.
     * @param blockState      the output state type.
     * @param blockStateValue the output state value.
     * @param <V>             the type of the output state value.
     * @return the newly created block mapping.
     */
    public static <V extends BlockStateValue> BlockMapping of(String identifier, ChunkerBlockType blockType, BlockState<V> blockState, V blockStateValue) {
        return new BlockMapping(identifier, blockType, Collections.emptyNavigableMap(), null, ImmutableSortedMap.of(blockState, blockStateValue));
    }

    /**
     * Create a mapping that maps an identifier with a state to a block type and uses a state mapping group for the
     * remaining states.
     *
     * @param identifier        the input identifier.
     * @param stateName         the input state name.
     * @param stateValue        the input state value.
     * @param blockType         the output identifier.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param <S>               the type of the input state value.
     * @return the newly created block mapping.
     */
    public static <S> BlockMapping of(String identifier, String stateName, S stateValue, ChunkerBlockType blockType, VersionedStateMappingGroup stateMappingGroup) {
        return new BlockMapping(identifier, blockType, ImmutableSortedMap.of(stateName, stateValue), stateMappingGroup, Collections.emptyNavigableMap());
    }

    /**
     * Create a mapping that maps an identifier to a block type and uses a state mapping group for the states.
     *
     * @param identifier        the input identifier.
     * @param blockType         the output identifier.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @return the newly created block mapping.
     */
    public static BlockMapping of(String identifier, ChunkerBlockType blockType, VersionedStateMappingGroup stateMappingGroup) {
        return new BlockMapping(identifier, blockType, Collections.emptyNavigableMap(), stateMappingGroup, Collections.emptyNavigableMap());
    }

    /**
     * Create a mapping that maps an identifier to a block type with a state and uses a state mapping
     * group for the remaining states.
     *
     * @param identifier        the input identifier.
     * @param blockType         the output identifier.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param blockState        the output state type.
     * @param blockStateValue   the output state value.
     * @param <V>               the type of the output state value.
     * @return the newly created block mapping.
     */
    public static <V extends BlockStateValue> BlockMapping of(String identifier, ChunkerBlockType blockType, VersionedStateMappingGroup stateMappingGroup, BlockState<V> blockState, V blockStateValue) {
        return new BlockMapping(identifier, blockType, Collections.emptyNavigableMap(), stateMappingGroup, ImmutableSortedMap.of(blockState, blockStateValue));
    }

    /**
     * Create mappings for several identifiers which merge into the same Chunker block type with a state.
     *
     * @param blockType          the output identifier.
     * @param blockState         the output state type.
     * @param identifiersToState a map of input identifiers to the output state.
     * @param <V>                the type of the output state value.
     * @return the newly created block mappings.
     */
    public static <V extends BlockStateValue> BlockMapping[] merge(ChunkerBlockType blockType, BlockState<V> blockState, Multimap<String, V> identifiersToState) {
        BlockMapping[] mappings = new BlockMapping[identifiersToState.size()];
        int i = 0;
        for (Map.Entry<String, V> entry : identifiersToState.entries()) {
            mappings[i++] = new BlockMapping(entry.getKey(), blockType, Collections.emptyNavigableMap(), null, ImmutableSortedMap.of(blockState, entry.getValue()));
        }
        return mappings;
    }

    /**
     * Create mappings for several identifiers which merge into the same Chunker block type with a state.
     *
     * @param blockType          the output identifier.
     * @param blockState         the output state type.
     * @param identifiersToState a map of input identifiers to the output state.
     * @param stateMappingGroup  the state mapping group to use for mapping states.
     * @param <V>                the type of the output state value.
     * @return the newly created block mappings.
     */
    public static <V extends BlockStateValue> BlockMapping[] merge(ChunkerBlockType blockType, BlockState<V> blockState, Multimap<String, V> identifiersToState, VersionedStateMappingGroup stateMappingGroup) {
        BlockMapping[] mappings = new BlockMapping[identifiersToState.size()];
        int i = 0;
        for (Map.Entry<String, V> entry : identifiersToState.entries()) {
            mappings[i++] = new BlockMapping(entry.getKey(), blockType, Collections.emptyNavigableMap(), stateMappingGroup, ImmutableSortedMap.of(blockState, entry.getValue()));
        }
        return mappings;
    }

    /**
     * Create mappings for several identifiers which share the same state mapping group and the same output block state.
     *
     * @param types             a map of the input identifiers to block types.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param blockState        the output state type.
     * @param blockStateValue   the output state value.
     * @param <V>               the type of the output state value.
     * @return the newly created block mappings.
     */
    public static <V extends BlockStateValue> BlockMapping[] group(Multimap<String, ? extends ChunkerBlockType> types, VersionedStateMappingGroup stateMappingGroup, BlockState<V> blockState, V blockStateValue) {
        BlockMapping[] mappings = new BlockMapping[types.size()];
        int i = 0;
        for (Map.Entry<String, ? extends ChunkerBlockType> entry : types.entries()) {
            mappings[i++] = new BlockMapping(entry.getKey(), entry.getValue(), Collections.emptyNavigableMap(), stateMappingGroup, ImmutableSortedMap.of(blockState, blockStateValue));
        }
        return mappings;
    }

    /**
     * Create mappings for several identifiers.
     *
     * @param types a map of the input identifiers to block types.
     * @return the newly created block mappings.
     */
    public static BlockMapping[] group(Multimap<String, ? extends ChunkerBlockType> types) {
        BlockMapping[] mappings = new BlockMapping[types.size()];
        int i = 0;
        for (Map.Entry<String, ? extends ChunkerBlockType> entry : types.entries()) {
            mappings[i++] = new BlockMapping(entry.getKey(), entry.getValue(), Collections.emptyNavigableMap(), null, Collections.emptyNavigableMap());
        }
        return mappings;
    }

    /**
     * Create mappings for several identifiers which share the same state mapping group.
     *
     * @param types             a map of the input identifiers to block types.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @return the newly created block mappings.
     */
    public static BlockMapping[] group(Multimap<String, ? extends ChunkerBlockType> types, VersionedStateMappingGroup stateMappingGroup) {
        BlockMapping[] mappings = new BlockMapping[types.size()];
        int i = 0;
        for (Map.Entry<String, ? extends ChunkerBlockType> entry : types.entries()) {
            mappings[i++] = new BlockMapping(entry.getKey(), entry.getValue(), Collections.emptyNavigableMap(), stateMappingGroup, Collections.emptyNavigableMap());
        }
        return mappings;
    }

    /**
     * Create mappings for several identifiers which share the same state mapping group and the same input block state
     * name / value.
     *
     * @param stateName         the input state name.
     * @param stateValue        the input state value.
     * @param types             a map of the input identifiers to block types.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param <S>               the type of the input state value.
     * @return the newly created block mappings.
     */
    public static <S> BlockMapping[] group(String stateName, S stateValue, Multimap<String, ? extends ChunkerBlockType> types, VersionedStateMappingGroup stateMappingGroup) {
        BlockMapping[] mappings = new BlockMapping[types.size()];
        int i = 0;
        for (Map.Entry<String, ? extends ChunkerBlockType> entry : types.entries()) {
            mappings[i++] = new BlockMapping(entry.getKey(), entry.getValue(), ImmutableSortedMap.of(stateName, stateValue), stateMappingGroup, Collections.emptyNavigableMap());
        }
        return mappings;
    }

    /**
     * Create mappings for several identifiers which share the same state mapping group and the same input block state
     * name / value and same output block state.
     *
     * @param stateName         the input state name.
     * @param stateValue        the input state value.
     * @param types             a map of the input identifiers to block types.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param blockState        the output state type.
     * @param blockStateValue   the output state value.
     * @param <V>               the type of the output state value.
     * @param <S>               the type of the input state value.
     * @return the newly created block mappings.
     */
    public static <S, V extends BlockStateValue> BlockMapping[] group(String stateName, S stateValue, Multimap<String, ? extends ChunkerBlockType> types, VersionedStateMappingGroup stateMappingGroup, BlockState<V> blockState, V blockStateValue) {
        BlockMapping[] mappings = new BlockMapping[types.size()];
        NavigableMap<BlockState<?>, BlockStateValue> blockStates = ImmutableSortedMap.of(blockState, blockStateValue);
        int i = 0;
        for (Map.Entry<String, ? extends ChunkerBlockType> entry : types.entries()) {
            mappings[i++] = new BlockMapping(entry.getKey(), entry.getValue(), ImmutableSortedMap.of(stateName, stateValue), stateMappingGroup, blockStates);
        }
        return mappings;
    }

    /**
     * Create mappings for an identifier which becomes multiple output types based on the input state.
     *
     * @param identifier       the input identifier.
     * @param stateName        the input state name.
     * @param stateToBlockType a map of the input state value to the output block type.
     * @param <S>              the type of the input state.
     * @return the newly created block mappings.
     */
    public static <S> BlockMapping[] flatten(String identifier, String stateName, Multimap<S, ? extends ChunkerBlockType> stateToBlockType) {
        BlockMapping[] mappings = new BlockMapping[stateToBlockType.size()];
        int i = 0;
        for (Map.Entry<S, ? extends ChunkerBlockType> entry : stateToBlockType.entries()) {
            mappings[i++] = new BlockMapping(identifier, entry.getValue(), ImmutableSortedMap.of(stateName, entry.getKey()), null, Collections.emptyNavigableMap());
        }
        return mappings;
    }

    /**
     * Create mappings for an identifier which becomes multiple output types based on the input state and has an output
     * state.
     *
     * @param identifier       the input identifier.
     * @param stateName        the input state name.
     * @param stateToBlockType a map of the input state value to the output block type.
     * @param blockState       the output state type.
     * @param blockStateValue  the output state value.
     * @param <S>              the type of the input state.
     * @param <V>              the type of the output state value.
     * @return the newly created block mappings.
     */
    public static <S, V extends BlockStateValue> BlockMapping[] flatten(String identifier, String stateName, Multimap<S, ? extends ChunkerBlockType> stateToBlockType, BlockState<V> blockState, V blockStateValue) {
        BlockMapping[] mappings = new BlockMapping[stateToBlockType.size()];
        int i = 0;
        NavigableMap<BlockState<?>, BlockStateValue> blockStates = ImmutableSortedMap.of(blockState, blockStateValue);
        for (Map.Entry<S, ? extends ChunkerBlockType> entry : stateToBlockType.entries()) {
            mappings[i++] = new BlockMapping(identifier, entry.getValue(), ImmutableSortedMap.of(stateName, entry.getKey()), null, blockStates);
        }
        return mappings;
    }

    /**
     * Create mappings for an identifier which becomes multiple output types based on the input state and has an output
     * state with a state mapping group to handle remaining states.
     *
     * @param identifier        the input identifier.
     * @param stateName         the input state name.
     * @param stateToBlockType  a map of the input state value to the output block type.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param blockState        the output state type.
     * @param blockStateValue   the output state value.
     * @param <S>               the type of the input state.
     * @param <V>               the type of the output state value.
     * @return the newly created block mappings.
     */
    public static <S, V extends BlockStateValue> BlockMapping[] flatten(String identifier, String stateName, Multimap<S, ? extends ChunkerBlockType> stateToBlockType, VersionedStateMappingGroup stateMappingGroup, BlockState<V> blockState, V blockStateValue) {
        BlockMapping[] mappings = new BlockMapping[stateToBlockType.size()];
        int i = 0;
        NavigableMap<BlockState<?>, BlockStateValue> blockStates = ImmutableSortedMap.of(blockState, blockStateValue);
        for (Map.Entry<S, ? extends ChunkerBlockType> entry : stateToBlockType.entries()) {
            mappings[i++] = new BlockMapping(identifier, entry.getValue(), ImmutableSortedMap.of(stateName, entry.getKey()), stateMappingGroup, blockStates);
        }
        return mappings;
    }

    /**
     * Create mappings for an identifier which becomes multiple output types/states based on the input state.
     *
     * @param identifier       the input identifier.
     * @param stateName        the input state name.
     * @param blockState       the output state type.
     * @param stateToBlockType a map of the input state value to a pair of the output state value and output block type.
     * @param <S>              the type of the input state.
     * @param <V>              the type of the output state value.
     * @return the newly created block mappings.
     */
    public static <S, V extends BlockStateValue> BlockMapping[] flatten(String identifier, String stateName, BlockState<V> blockState, Multimap<S, Pair<V, ? extends ChunkerBlockType>> stateToBlockType) {
        BlockMapping[] mappings = new BlockMapping[stateToBlockType.size()];
        int i = 0;
        for (Map.Entry<S, Pair<V, ? extends ChunkerBlockType>> entry : stateToBlockType.entries()) {
            mappings[i++] = new BlockMapping(identifier, entry.getValue().right(), ImmutableSortedMap.of(stateName, entry.getKey()), null, ImmutableSortedMap.of(blockState, entry.getValue().left()));
        }
        return mappings;
    }

    /**
     * Create mappings for an identifier which becomes multiple output types/states based on the input state.
     *
     * @param identifier       the input identifier.
     * @param stateName        the input state names.
     * @param stateToBlockType a map of the input state value to a pair of the output states and output block type.
     * @param <S>              the type of the input state.
     * @return the newly created block mappings.
     */
    public static <S> BlockMapping[] flatten(String identifier, String stateName, List<BlockState<?>> blockStates, Multimap<S, Pair<List<BlockStateValue>, ? extends ChunkerBlockType>> stateToBlockType) {
        BlockMapping[] mappings = new BlockMapping[stateToBlockType.size()];
        int i = 0;
        for (Map.Entry<S, Pair<List<BlockStateValue>, ? extends ChunkerBlockType>> entry : stateToBlockType.entries()) {
            Preconditions.checkArgument(entry.getValue().left().size() == blockStates.size(), "Number of states does not match");
            ImmutableSortedMap.Builder<BlockState<?>, BlockStateValue> mapBuilder = ImmutableSortedMap.naturalOrder();
            for (int j = 0; j < blockStates.size(); j++) {
                mapBuilder.put(blockStates.get(j), entry.getValue().left().get(j));
            }
            mappings[i++] = new BlockMapping(identifier, entry.getValue().right(), ImmutableSortedMap.of(stateName, entry.getKey()), null, mapBuilder.build());
        }
        return mappings;
    }

    /**
     * Create mappings for an identifier which becomes multiple output types/states based on the input state with a
     * state mapping group to handle remaining states.
     *
     * @param identifier        the input identifier.
     * @param stateName         the input state names.
     * @param stateToBlockType  a map of the input state value to a pair of the output states and output block type.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param <S>               the type of the input state.
     * @return the newly created block mappings.
     */
    public static <S> BlockMapping[] flatten(String identifier, String stateName, List<BlockState<?>> blockStates, Multimap<S, Pair<List<BlockStateValue>, ? extends ChunkerBlockType>> stateToBlockType, VersionedStateMappingGroup stateMappingGroup) {
        BlockMapping[] mappings = new BlockMapping[stateToBlockType.size()];
        int i = 0;
        for (Map.Entry<S, Pair<List<BlockStateValue>, ? extends ChunkerBlockType>> entry : stateToBlockType.entries()) {
            Preconditions.checkArgument(entry.getValue().left().size() == blockStates.size(), "Number of states does not match");
            ImmutableSortedMap.Builder<BlockState<?>, BlockStateValue> mapBuilder = ImmutableSortedMap.naturalOrder();
            for (int j = 0; j < blockStates.size(); j++) {
                mapBuilder.put(blockStates.get(j), entry.getValue().left().get(j));
            }
            mappings[i++] = new BlockMapping(identifier, entry.getValue().right(), ImmutableSortedMap.of(stateName, entry.getKey()), stateMappingGroup, mapBuilder.build());
        }
        return mappings;
    }

    /**
     * Create mappings for an identifier which becomes multiple output types based on the input state with a state
     * mapping group to handle remaining states.
     *
     * @param identifier        the input identifier.
     * @param stateName         the input state name.
     * @param stateToBlockType  a map of the input state value to the output block type.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @param <S>               the type of the input state.
     * @return the newly created block mappings.
     */
    public static <S> BlockMapping[] flatten(String identifier, String stateName, Multimap<S, ? extends ChunkerBlockType> stateToBlockType, VersionedStateMappingGroup stateMappingGroup) {
        BlockMapping[] mappings = new BlockMapping[stateToBlockType.size()];
        int i = 0;
        for (Map.Entry<S, ? extends ChunkerBlockType> entry : stateToBlockType.entries()) {
            mappings[i++] = new BlockMapping(identifier, entry.getValue(), ImmutableSortedMap.of(stateName, entry.getKey()), stateMappingGroup, Collections.emptyNavigableMap());
        }
        return mappings;
    }

    /**
     * Create mappings for an identifier which becomes multiple output types based on the two input states.
     *
     * @param identifier       the input identifier.
     * @param stateName1       the first input state name.
     * @param stateName2       the second input state name.
     * @param stateToBlockType a map of the input state value to the output block type.
     * @param <S1>             the type of the first input state.
     * @param <S2>             the type of the second input state.
     * @return the newly created block mappings.
     */
    public static <S1, S2> BlockMapping[] flatten(String identifier, String stateName1, String stateName2, Multimap<Pair<S1, S2>, ? extends ChunkerBlockType> stateToBlockType) {
        BlockMapping[] mappings = new BlockMapping[stateToBlockType.size()];
        int i = 0;
        for (Map.Entry<Pair<S1, S2>, ? extends ChunkerBlockType> entry : stateToBlockType.entries()) {
            mappings[i++] = new BlockMapping(identifier, entry.getValue(), ImmutableSortedMap.of(stateName1, entry.getKey().left(), stateName2, entry.getKey().right()), null, Collections.emptyNavigableMap());
        }
        return mappings;
    }

    /**
     * Create mappings for an identifier which becomes multiple output types based on the two input states with a state
     * mapping group to handle remaining states.
     *
     * @param identifier        the input identifier.
     * @param stateName1        the first input state name.
     * @param stateName2        the second input state name.
     * @param stateToBlockType  a map of the input state value to the output block type.
     * @param <S1>              the type of the first input state.
     * @param <S2>              the type of the second input state.
     * @param stateMappingGroup the state mapping group to use for mapping states.
     * @return the newly created block mappings.
     */
    public static <S1, S2> BlockMapping[] flatten(String identifier, String stateName1, String stateName2, Multimap<Pair<S1, S2>, ? extends ChunkerBlockType> stateToBlockType, VersionedStateMappingGroup stateMappingGroup) {
        BlockMapping[] mappings = new BlockMapping[stateToBlockType.size()];
        int i = 0;
        for (Map.Entry<Pair<S1, S2>, ? extends ChunkerBlockType> entry : stateToBlockType.entries()) {
            mappings[i++] = new BlockMapping(identifier, entry.getValue(), ImmutableSortedMap.of(stateName1, entry.getKey().left(), stateName2, entry.getKey().right()), stateMappingGroup, Collections.emptyNavigableMap());
        }
        return mappings;
    }

    /**
     * Get the input identifier.
     *
     * @return the input identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get the output Chunker type.
     *
     * @return the type of the output block.
     */
    public ChunkerBlockType getBlockType() {
        return blockType;
    }

    /**
     * Get the input states.
     *
     * @return a map of the input states that should be set / matched for this mapping.
     */
    public NavigableMap<String, Object> getStates() {
        return states;
    }

    /**
     * Get the state mapping group for this block mapping.
     *
     * @param version the version to use for the mapping.
     * @return the state mapping group if it's present otherwise null.
     */
    @Nullable
    public StateMappingGroup getStateMappingGroup(Version version) {
        return versionedStateMappingGroup == null ? null : versionedStateMappingGroup.getStateMappingGroup(version);
    }

    /**
     * Get the output states.
     *
     * @return a map of the output states that should be set / matched for this mapping.
     */
    public NavigableMap<BlockState<?>, BlockStateValue> getBlockStates() {
        return blockStates;
    }
}
