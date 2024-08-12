package com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A mapping for how to turn some input states into some output states.
 *
 * @param <I> the input type.
 * @param <O> the output type (Chunker).
 */
public class StateMapping<I, O extends BlockStateValue> {
    private final List<String> inputStateNames;
    private final List<BlockState<? extends O>> outputBlockStates;
    private final TypeMapping<I, O> typeMapping;

    /**
     * Create a new state mapping.
     *
     * @param inputStateNames the list of input names.
     * @param blockStates     the list of output states.
     * @param typeMapping     the type mapping with the same number of inputs and outputs to the inputStateNames and
     *                        blockStates.
     */
    public StateMapping(List<String> inputStateNames, List<BlockState<? extends O>> blockStates, TypeMapping<I, O> typeMapping) {
        this.inputStateNames = inputStateNames;
        outputBlockStates = blockStates;
        this.typeMapping = typeMapping;

        // Check the input/output size is the same as the type mapping
        Preconditions.checkArgument(
                typeMapping.getInputTypes().size() == inputStateNames.size(),
                "The number of provided input names does not match the number of states the type supports, found %s expected %s",
                inputStateNames.size(),
                typeMapping.getInputTypes().size()
        );
        Preconditions.checkArgument(
                typeMapping.getOutputTypes().size() == blockStates.size(),
                "The number of provided output states does not match the number of states the type supports, found %s expected %s",
                blockStates.size(),
                typeMapping.getOutputTypes().size()
        );

        // Check the output types match
        int stateCombinations = 1;
        for (int i = 0; i < blockStates.size(); i++) {
            BlockState<?> blockState = blockStates.get(i);

            // Update the total number of combinations
            stateCombinations = stateCombinations * blockState.getValues().length;

            // Check the type
            Class<? super O> expectedType = typeMapping.getOutputTypes().get(i);
            Preconditions.checkArgument(
                    expectedType.isAssignableFrom(blockState.getValues()[0].getClass()),
                    "The BlockState provided does not provide a class matching the type mapping output, got %s, expected %s.",
                    blockState.getValues()[0].getClass(),
                    expectedType
            );
        }

        // Check all the output combinations are present
        if (typeMapping.getOutputToInput().size() != stateCombinations) {
            // If it's not equal, we can tell the developer which combinations are missing
            Set<List<? extends O>> combinations = new HashSet<>(Lists.cartesianProduct(blockStates.stream().map(a -> List.of(a.getValues())).collect(Collectors.toList())));

            // Remove any combinations known
            combinations.removeAll(typeMapping.getOutputToInput().keySet());

            throw new IllegalArgumentException(
                    "Missing TypeMapping combinations " + combinations + ", expected " + stateCombinations +
                            " but got " + typeMapping.getOutputToInput().size()
            );
        }
    }

    /**
     * Get the names of the handled input states.
     *
     * @return the list of names.
     */
    public List<String> getInputStateNames() {
        return inputStateNames;
    }

    /**
     * Get the output states that are handled.
     *
     * @return the list of states.
     */
    public List<BlockState<? extends O>> getOutputBlockStates() {
        return outputBlockStates;
    }

    /**
     * Get the mapping used to get from the inputs to the outputs.
     *
     * @return the type mapping.
     */
    public TypeMapping<I, O> getTypeMapping() {
        return typeMapping;
    }

    /**
     * Attempt to apply this state mapping to an input.
     *
     * @param inputStateLookup  the lookup to get the input states, returning null if absent.
     * @param outputs           the outputs to write the mapped states or defaults to.
     * @param allowDefaultValue whether a default value can be used by either this method applying it or by the inputStateLookup.
     */
    public void applyInput(StateLookupFunction<String, Object> inputStateLookup, Map<BlockState<?>, BlockStateValue> outputs, boolean allowDefaultValue) {
        List<Object> inputValues = new ArrayList<>(inputStateNames.size());

        // Create the input values list
        for (String name : inputStateNames) {
            // Get the state (use the default if there is no default output for this mapping)
            Object value = inputStateLookup.getState(name, allowDefaultValue && typeMapping.getDefaultOutput() == null);
            if (value == null) {
                // If the field doesn't exist then this state mapping can't be applied
                // Use the defaults if they are present
                inputValues = null;
                break;
            }

            inputValues.add(value);
        }

        // Fetch the output based on inputValues or use the default
        List<O> typeOutput = inputValues == null ? null : typeMapping.getInputToOutput().get(inputValues);
        if (typeOutput == null) {
            if (allowDefaultValue) {
                // Use the default output as the values
                typeOutput = typeMapping.getDefaultOutput();
            }

            // If there is no type output, there is nothing to do, return
            if (typeOutput == null) return;
        }

        // Apply the outputValues
        Iterator<BlockState<? extends O>> outputKeyIterator = outputBlockStates.iterator();
        Iterator<O> outputValueIterator = typeOutput.iterator();
        while (outputKeyIterator.hasNext()) {
            outputs.putIfAbsent(outputKeyIterator.next(), outputValueIterator.next());
        }
    }

    /**
     * Attempt to apply this state mapping to an output.
     *
     * @param outputStateLookup the lookup to get the output states, returning null if absent.
     * @param inputs            the inputs to write the mapped states or defaults to.
     * @param allowDefaultValue whether a default value can be used by either this method applying it or by the inputStateLookup.
     */
    public void applyOutput(StateLookupFunction<BlockState<?>, BlockStateValue> outputStateLookup, Map<String, Object> inputs, boolean allowDefaultValue) {
        List<BlockStateValue> outputValues = new ArrayList<>(outputBlockStates.size());

        // Create the input values list
        for (BlockState<? extends O> blockState : outputBlockStates) {
            // Get the state (use the default if there is no default input for this mapping)
            BlockStateValue value = outputStateLookup.getState(blockState, allowDefaultValue && typeMapping.getDefaultInput() == null);
            if (value == null) {
                // If the field doesn't exist then this state mapping can't be applied
                // Use the defaults if they are present
                outputValues = null;
                break;
            }

            outputValues.add(value);
        }

        // Fetch the input based on outputValues or use the default
        List<I> typeInput = outputValues == null ? null : typeMapping.getOutputToInput().get(outputValues);
        if (typeInput == null) {
            if (allowDefaultValue) {
                // Use the default input as the values
                typeInput = typeMapping.getDefaultInput();
            }

            // If there is no type input, there is nothing to do, return
            if (typeInput == null) return;
        }

        // Apply the outputValues
        Iterator<String> inputKeyIterator = inputStateNames.iterator();
        Iterator<I> inputValueIterator = typeInput.iterator();
        while (inputKeyIterator.hasNext()) {
            inputs.putIfAbsent(inputKeyIterator.next(), inputValueIterator.next());
        }
    }
}
