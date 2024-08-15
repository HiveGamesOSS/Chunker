package com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state;

import com.google.common.base.Preconditions;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A mapping of some input to some output.
 *
 * @param <I> the input types.
 * @param <O> the output types.
 */
public class TypeMapping<I, O extends BlockStateValue> {
    private final Map<List<I>, List<O>> inputToOutput;
    private final Map<List<O>, List<I>> outputToInput;
    @Nullable
    private final List<I> defaultInput;
    @Nullable
    private final List<O> defaultOutput;
    private final List<Class<? super I>> inputTypes;
    private final List<Class<? super O>> outputTypes;

    /**
     * Create a new type mappings.
     *
     * @param inputToOutput the map to use for turning an input into an output.
     * @param outputToInput the map to use for turning an output into an input.
     * @param defaultInput  the default to use if no input is present.
     * @param defaultOutput the default to use if no output is present.
     * @param inputTypes    the types used for the input.
     * @param outputTypes   the types used for the output.
     */
    public TypeMapping(Map<List<I>, List<O>> inputToOutput, Map<List<O>, List<I>> outputToInput, @Nullable List<I> defaultInput, @Nullable List<O> defaultOutput, List<Class<? super I>> inputTypes, List<Class<? super O>> outputTypes) {
        this.inputToOutput = inputToOutput;
        this.outputToInput = outputToInput;
        this.defaultInput = defaultInput;
        this.defaultOutput = defaultOutput;
        this.inputTypes = inputTypes;
        this.outputTypes = outputTypes;
    }

    /**
     * Get the map used for mapping inputs to outputs.
     *
     * @return the map of inputs to outputs.
     */
    public Map<List<I>, List<O>> getInputToOutput() {
        return inputToOutput;
    }

    /**
     * Get the map used for mapping outputs to inputs.
     *
     * @return the map of outputs to inputs.
     */
    public Map<List<O>, List<I>> getOutputToInput() {
        return outputToInput;
    }

    /**
     * Get the default input to use if the value isn't present.
     *
     * @return the default input.
     */
    @Nullable
    public List<I> getDefaultInput() {
        return defaultInput;
    }

    /**
     * Get the default output to use if the value isn't present.
     *
     * @return the default output.
     */
    @Nullable
    public List<O> getDefaultOutput() {
        return defaultOutput;
    }

    /**
     * Get a list of the input types this mapping handles.
     *
     * @return a list of the input types.
     */
    public List<Class<? super I>> getInputTypes() {
        return inputTypes;
    }

    /**
     * Get a list of the output types this mapping handles.
     *
     * @return a list of the output types.
     */
    public List<Class<? super O>> getOutputTypes() {
        return outputTypes;
    }

    /**
     * Map this type mapping to another using the present outputs. This is useful when only specific outputs can be
     * mapped by an input. e.g. missing states.
     *
     * @param outputMappings the mappings to apply to the current mappings to get from O -> I2
     * @param <I2>           the new input type.
     * @return a new type mapping.
     */
    @SafeVarargs
    public final <I2> TypeMapping<I2, O> mapOutputsToInputs(TypeMapping<? extends I2, ? extends O>... outputMappings) {
        Builder<I2, O> builder = new TypeMapping.Builder<>();
        Preconditions.checkArgument(getOutputTypes().size() == outputMappings.length, "The number of outputs for the type must match outputMappings");

        // Loop through all the inputs -> outputs
        for (Map.Entry<List<I>, List<O>> entry : getInputToOutput().entrySet()) {
            List<I> inputs = getOutputToInput().get(entry.getValue());
            if (!inputs.equals(entry.getKey())) {
                continue; // Skip entries which are duplicates
            }

            // Transform all the outputs -> inputs
            List<I2> newInputs = new ArrayList<>(outputMappings.length);
            List<O> outputs = entry.getValue();
            for (int i = 0; i < outputs.size(); i++) {
                O output = outputs.get(i);

                // Fetch the list for this input
                List<? extends I2> newInputList = outputMappings[i].getOutputToInput().get(List.of(output));
                Preconditions.checkNotNull(newInputList, "Missing output " + output + " from inputMappings[" + i + "]");
                Preconditions.checkArgument(newInputList.size() == 1, "Mapping of inputs can only use types with a single input and output");

                // Now add the mapped input to the new list
                newInputs.add(newInputList.get(0));
            }

            // Create the mapping for the inputs
            builder.mapping(newInputs, outputs);
        }

        // Loop through all the outputs -> inputs (these are duplicates and also accounting for all chunker combinations)
        for (Map.Entry<List<O>, List<I>> entry : getOutputToInput().entrySet()) {
            // Use the input to find the right entry to use
            List<O> outputs = entry.getKey();
            List<O> reversedOutputs = getInputToOutput().get(entry.getValue());
            if (reversedOutputs.equals(outputs))
                continue; // Skip entries we've already mapped (where the key is the same as the outputs)

            // Transform all the outputs -> inputs
            List<I2> newInputs = new ArrayList<>(outputMappings.length);
            for (int i = 0; i < reversedOutputs.size(); i++) {
                O output = reversedOutputs.get(i);

                // Fetch the list for this input
                List<? extends I2> newInputList = outputMappings[i].getOutputToInput().get(List.of(output));
                Preconditions.checkNotNull(newInputList, "Missing output " + output + " from inputMappings[" + i + "]");
                Preconditions.checkArgument(newInputList.size() == 1, "Mapping of inputs can only use types with a single input and output");

                // Now add the mapped input to the new list
                newInputs.add(newInputList.get(0));
            }

            // Create the mapping for the inputs
            builder.mapping(newInputs, outputs);
        }
        return builder.build();
    }

    /**
     * Builder for creating type mappings.
     *
     * @param <I> the type of the input.
     * @param <O> the type of the output.
     */
    public static final class Builder<I, O extends BlockStateValue> {
        private final Map<List<I>, List<O>> inputToOutput = new Object2ObjectOpenHashMap<>();
        private final Map<List<O>, List<I>> outputToInput = new Object2ObjectOpenHashMap<>();
        private List<I> defaultInput;
        private List<O> defaultOutput;
        private List<Class<? super I>> inputTypes;
        private List<Class<? super O>> outputTypes;

        /**
         * Create a new TypeMapping builder.
         */
        public Builder() {
        }

        @SuppressWarnings("unchecked")
        private void checkInputSize(List<I> inputs) {
            if (inputs == null) return; // Don't check null values, they just mean a mapping is marked as invalid
            if (inputTypes == null) {
                inputTypes = new ArrayList<>(inputs.size());
                for (I input : inputs) {
                    inputTypes.add((Class<? super I>) input.getClass());
                }
            } else {
                // Check size
                Preconditions.checkArgument(
                        inputTypes.size() == inputs.size(),
                        "The number of state inputs must be consistent across all mappings, expected %s inputs.",
                        inputTypes.size()
                );

                // Check types
                for (int i = 0; i < inputs.size(); i++) {
                    I input = inputs.get(i);
                    Class<? super I> expectedType = inputTypes.get(i);
                    Preconditions.checkArgument(
                            expectedType.isAssignableFrom(input.getClass()),
                            "Input parameter must be the same type throughout the type mapping, got %s expected %s.",
                            input.getClass(),
                            expectedType
                    );
                }
            }
        }

        @SuppressWarnings("unchecked")
        private void checkOutputSize(List<O> outputs) {
            if (outputs == null) return; // Don't check null values, they just mean a mapping is marked as invalid
            if (outputTypes == null) {
                outputTypes = new ArrayList<>(outputs.size());
                for (O output : outputs) {
                    outputTypes.add((Class<? super O>) output.getClass());
                }
            } else {
                // Check size
                Preconditions.checkArgument(
                        outputTypes.size() == outputs.size(),
                        "The number of state outputs must be consistent across all mappings, expected %s outputs.",
                        outputTypes.size()
                );

                // Check types
                for (int i = 0; i < outputs.size(); i++) {
                    O output = outputs.get(i);
                    Class<? super O> expectedType = outputTypes.get(i);
                    Preconditions.checkArgument(
                            expectedType.isAssignableFrom(output.getClass()),
                            "Output parameter must be the same type throughout the type mapping, got %s expected %s.",
                            output.getClass(),
                            expectedType
                    );
                }
            }
        }

        /**
         * Set the defaults for this mapping.
         *
         * @param inputs  the list of default input parameters which are set if the output is absent.
         * @param outputs the list of default output parameters which are set if the input is absent.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> defaults(List<I> inputs, List<O> outputs) {
            Preconditions.checkArgument(defaultInput == null, "Default inputs have already been set!");
            Preconditions.checkArgument(defaultOutput == null, "Default outputs have already been set!");

            // Check input/output sizes
            checkInputSize(inputs);
            checkOutputSize(outputs);

            defaultInput = inputs;
            defaultOutput = outputs;
            return this;
        }

        /**
         * Set the defaults for this mapping.
         *
         * @param input  the input to set if it wasn't present.
         * @param output the output to set if it wasn't present.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> defaults(I input, O output) {
            return defaults(List.of(input), List.of(output));
        }

        /**
         * Set the defaults for this mapping.
         *
         * @param input1 the input to set if the first input wasn't present.
         * @param input2 the input to set if the second input wasn't present.
         * @param output the output to set if it wasn't present.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> defaults(I input1, I input2, O output) {
            return defaults(List.of(input1, input2), List.of(output));
        }

        /**
         * Set the defaults for this mapping.
         *
         * @param input   the input to set if it wasn't present.
         * @param output1 the output to set if the first output wasn't present.
         * @param output2 the output to set if the first output wasn't present.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> defaults(I input, O output1, O output2) {
            return defaults(List.of(input), List.of(output1, output2));
        }

        /**
         * Set the defaults for this mapping.
         *
         * @param inputs the list of default input parameters which are set if the output is absent.
         * @param output the output to set if it wasn't present.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> defaults(List<I> inputs, O output) {
            return defaults(inputs, List.of(output));
        }

        /**
         * Set the defaults for this mapping.
         *
         * @param input   the input to set if it wasn't present.
         * @param outputs the list of default output parameters which are set if the input is absent.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> defaults(I input, List<O> outputs) {
            return defaults(List.of(input), outputs);
        }

        public TypeMapping.Builder<I, O> mapping(List<I> inputs, List<O> outputs) {
            // Check input/output sizes
            checkInputSize(inputs);
            checkOutputSize(outputs);

            // Add to each map
            List<O> originalInputToOutput = inputToOutput.putIfAbsent(inputs, outputs);
            List<I> originalOutputToInput = outputToInput.putIfAbsent(outputs, inputs);

            // If it wasn't added to one of the maps, it's a duplicate
            if (originalInputToOutput != null && originalOutputToInput != null) {
                throw new IllegalArgumentException("Duplicate mapping of " + inputs + " -> " + outputs);
            }

            return this;
        }

        /**
         * Mark an output as invalid, this is used when you want to ensure every enum value is handled but the value
         * shouldn't happen, e.g. it has a specific mapping and goes to a different type.
         * This is only possible for outputs as they are checked when adding them to state mappings to ensure all
         * outputs are handled.
         *
         * @param output the output which should be marked as invalid.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> invalidOutput(O output) {
            return mapping((List<I>) null, List.of(output));
        }

        /**
         * Create a mapping of an input to an output.
         *
         * @param input  the input value.
         * @param output the output value.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> mapping(I input, O output) {
            return mapping(List.of(input), List.of(output));
        }

        /**
         * Create a mapping of two inputs to an output.
         *
         * @param input1 the first input value.
         * @param input2 the second input value.
         * @param output the output value.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> mapping(I input1, I input2, O output) {
            return mapping(List.of(input1, input2), List.of(output));
        }

        /**
         * Create a mapping of an input to two outputs.
         *
         * @param input   the input value.
         * @param output1 the first output value.
         * @param output2 the second output value.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> mapping(I input, O output1, O output2) {
            return mapping(List.of(input), List.of(output1, output2));
        }

        /**
         * Create a mapping of multiple inputs to an output.
         *
         * @param inputs the input values.
         * @param output the output value.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> mapping(List<I> inputs, O output) {
            return mapping(inputs, List.of(output));
        }

        /**
         * Create a mapping of an input to multiple outputs.
         *
         * @param input   the input value.
         * @param outputs the output values.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> mapping(I input, List<O> outputs) {
            return mapping((List.of(input)), outputs);
        }

        /**
         * Generate the mappings from a consumer.
         *
         * @param immediateConsumer a consumer that is immediately run.
         * @return the current builder.
         */
        public TypeMapping.Builder<I, O> generate(Consumer<TypeMapping.Builder<I, O>> immediateConsumer) {
            immediateConsumer.accept(this);
            return this;
        }

        /**
         * Build the type mapping (must contain mappings).
         *
         * @return the newly made type mapping.
         */
        public TypeMapping<I, O> build() {
            Preconditions.checkArgument(!inputTypes.isEmpty() || !outputTypes.isEmpty(), "Cannot build type mapping with no determined inputs/outputs!");
            return new TypeMapping<>(inputToOutput, outputToInput, defaultInput, defaultOutput, inputTypes, outputTypes);
        }
    }

}
