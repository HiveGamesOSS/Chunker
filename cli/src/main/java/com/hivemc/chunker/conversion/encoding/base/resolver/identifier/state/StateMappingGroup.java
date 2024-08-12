package com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state;

import com.google.common.base.Preconditions;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A group of several state mappings that should be applied to an input.
 */
public class StateMappingGroup implements VersionedStateMappingGroup {
    private final List<StateMapping<?, ?>> states;
    private final Map<String, Object> defaultInputs;
    private final Map<BlockState<?>, BlockStateValue> defaultOutputs;

    /**
     * Create a new state mapping group.
     *
     * @param states         a list of state mappings which are handled by this group.
     * @param defaultInputs  default inputs that should be used if the state mappings do not provide them.
     * @param defaultOutputs default outputs that should be used if the state mappings do not provide them.
     */
    public StateMappingGroup(List<StateMapping<?, ?>> states, Map<String, Object> defaultInputs, Map<BlockState<?>, BlockStateValue> defaultOutputs) {
        this.states = states;
        this.defaultInputs = defaultInputs;
        this.defaultOutputs = defaultOutputs;
    }

    /**
     * Apply this state mapping group to an input, applying the state mappings then adding any missing defaults.
     *
     * @param inputStateLookup the lookup to get states from the input.
     * @param outputs          the map to write outputs to.
     */
    public void applyInput(StateLookupFunction<String, Object> inputStateLookup, Map<BlockState<?>, BlockStateValue> outputs) {
        // Apply each state mapping
        for (StateMapping<?, ?> state : states) {
            // The default value can be used if we don't have a valid default value ourselves
            boolean allowDefaultValue = !defaultOutputs.keySet().containsAll(state.getOutputBlockStates());
            state.applyInput(inputStateLookup, outputs, allowDefaultValue);
        }

        // Apply defaults
        for (Map.Entry<BlockState<?>, BlockStateValue> defaultOutput : defaultOutputs.entrySet()) {
            outputs.putIfAbsent(defaultOutput.getKey(), defaultOutput.getValue());
        }
    }

    /**
     * Apply this state mapping group to an output, applying the state mappings then adding any missing defaults.
     *
     * @param inputStateLookup the lookup to get states from the input.
     * @param outputs          the map to write outputs to.
     */
    public void applyOutput(StateLookupFunction<BlockState<?>, BlockStateValue> inputStateLookup, Map<String, Object> outputs) {
        // Apply each state mapping
        for (StateMapping<?, ?> state : states) {
            // The default value can be used if we don't have a valid default value ourselves
            boolean allowDefaultValue = !defaultInputs.keySet().containsAll(state.getInputStateNames());
            state.applyOutput(inputStateLookup, outputs, allowDefaultValue);
        }

        // Apply defaults
        for (Map.Entry<String, Object> defaultInput : defaultInputs.entrySet()) {
            outputs.putIfAbsent(defaultInput.getKey(), defaultInput.getValue());
        }
    }

    @Override
    public StateMappingGroup getStateMappingGroup(Version version) {
        return this; // This state mapping group doesn't depend on version
    }

    /**
     * Builder for making StateMappingGroups.
     */
    public static final class Builder {
        private final List<StateMapping<?, ?>> states = new ArrayList<>();
        private final Map<String, Object> defaultInputs = new Object2ObjectOpenHashMap<>();
        private final Map<BlockState<?>, BlockStateValue> defaultOutputs = new Object2ObjectOpenHashMap<>();

        /**
         * Create a new instance of the builder.
         */
        public Builder() {
        }

        /**
         * Set the default input if a state isn't present.
         *
         * @param inputStateName the input state name.
         * @param value          the default value for the input.
         * @param <T>            the type of the value.
         * @return the current builder.
         */
        public <T> Builder defaultInput(String inputStateName, T value) {
            Preconditions.checkArgument(defaultInputs.put(inputStateName, value) == null, "Duplicate default output for " + inputStateName);
            return this;
        }

        /**
         * Set the default output if a state isn't present.
         *
         * @param blockState the state type.
         * @param value      the default value for the output.
         * @param <O>        the type of the value.
         * @return the current builder.
         */
        public <O extends BlockStateValue> Builder defaultOutput(BlockState<O> blockState, O value) {
            Preconditions.checkArgument(defaultOutputs.put(blockState, value) == null, "Duplicate default output for " + blockState);
            return this;
        }

        /**
         * Set the mapping to use for a state.
         *
         * @param inputStateName the input state name.
         * @param blockState     the output state.
         * @param typeMapping    the mapping to use to get from the input to output.
         * @param <I>            the input type.
         * @param <O>            the output type.
         * @return the current builder.
         */
        public <I, O extends BlockStateValue> Builder state(String inputStateName, BlockState<O> blockState, TypeMapping<I, O> typeMapping) {
            states.add(new StateMapping<>(List.of(inputStateName), List.of(blockState), typeMapping));
            return this;
        }

        /**
         * Single input to multiple outputs mapping.
         *
         * @param inputStateName the input state name.
         * @param blockStates    the output states.
         * @param typeMapping    the mappings to use to get from a single input to the same number of outputs.
         * @param <I>            the input type.
         * @param <O>            the general type for the outputs.
         * @return the current builder.
         */
        public <I, O extends BlockStateValue> Builder multiState(String inputStateName, List<BlockState<? extends O>> blockStates, TypeMapping<I, O> typeMapping) {
            states.add(new StateMapping<>(List.of(inputStateName), blockStates, typeMapping));
            return this;
        }

        /**
         * Multiple inputs to a single output mapping.
         *
         * @param inputStateNames the input state names.
         * @param blockState      the output state.
         * @param typeMapping     the mappings to use to get from multiple inputs to a single output.
         * @param <I>             the general type for the inputs.
         * @param <O>             the output type.
         * @return the current builder.
         */
        public <I, O extends BlockStateValue> Builder multiState(List<String> inputStateNames, BlockState<O> blockState, TypeMapping<I, O> typeMapping) {
            states.add(new StateMapping<>(inputStateNames, List.of(blockState), typeMapping));
            return this;
        }

        /**
         * Multiple inputs to a multiple outputs mapping.
         *
         * @param inputStateNames the input state names.
         * @param blockStates     the output states.
         * @param typeMapping     the mappings to use to get from multiple inputs to multiple outputs.
         * @param <I>             the general type for the inputs.
         * @param <O>             the general type for the outputs.
         * @return the current builder.
         */
        public <I, O extends BlockStateValue> Builder multiState(List<String> inputStateNames, List<BlockState<? extends O>> blockStates, TypeMapping<I, O> typeMapping) {
            states.add(new StateMapping<>(inputStateNames, blockStates, typeMapping));
            return this;
        }

        /**
         * Combine two input states into an output state.
         *
         * @param inputStateName1 the first input state.
         * @param inputStateName2 the second input state.
         * @param blockState      the output state.
         * @param typeMapping     a type mapping which takes the two inputs and produces the output.
         * @param <I>             the general type of both the inputs.
         * @param <O>             the output type.
         * @return the current builder.
         */
        public <I, O extends BlockStateValue> Builder combineInputStates(String inputStateName1, String inputStateName2, BlockState<O> blockState, TypeMapping<I, O> typeMapping) {
            states.add(new StateMapping<>(List.of(inputStateName1, inputStateName2), List.of(blockState), typeMapping));
            return this;
        }

        /**
         * Combine two output states to an input state.
         *
         * @param inputStateName the input state.
         * @param blockState1    the first output state.
         * @param blockState2    the second output state.
         * @param typeMapping    the type mapping which takes the input and produces two outputs.
         * @param <I>            the input type.
         * @param <O>            the general type of both the outputs.
         * @return the current builder.
         */
        public <I, O extends BlockStateValue> Builder combineOutputStates(String inputStateName, BlockState<? extends O> blockState1, BlockState<? extends O> blockState2, TypeMapping<I, O> typeMapping) {
            states.add(new StateMapping<>(List.of(inputStateName), List.of(blockState1, blockState2), typeMapping));
            return this;
        }

        /**
         * Create the state mapping group.
         *
         * @return the new instance.
         */
        public StateMappingGroup build() {
            return new StateMappingGroup(states, defaultInputs, defaultOutputs);
        }
    }
}
