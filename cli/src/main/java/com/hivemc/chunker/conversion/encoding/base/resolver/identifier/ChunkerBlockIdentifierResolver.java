package com.hivemc.chunker.conversion.encoding.base.resolver.identifier;

import com.google.common.base.Preconditions;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateLookupFunction;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateMappingGroup;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.VersionedStateMappingGroup;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.PreservedIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerCustomBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.resolver.MappingsFileResolvers;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.CollectionComparator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Resolver to convert a format specific Identifier into a ChunkerBlockIdentifier.
 * It uses mapping rules to handle the mappings between various blocks, using groups to keep similar groups mapped
 * together.
 * Note: When working with ChunkerVanillaBlockType, it is expected that every state has a value.
 */
public abstract class ChunkerBlockIdentifierResolver implements Resolver<Identifier, ChunkerBlockIdentifier> {
    protected final Converter converter;
    protected final Version version;
    protected final boolean customIdentifiersAllowed;
    protected final boolean reader;
    protected final Map<String, InputStateGrouping<String, Object, ChunkerBlockType, BlockState<?>, BlockStateValue>> inputToChunker = new Object2ObjectOpenHashMap<>();
    protected final Map<ChunkerBlockType, InputStateGrouping<BlockState<?>, BlockStateValue, String, String, Object>> chunkerToInput = new Object2ObjectOpenHashMap<>();
    protected StateMappingGroup extraStateMappingGroup;

    /**
     * Create a new chunker block identifier resolver.
     *
     * @param converter                the converter instance.
     * @param version                  the version this resolver handles.
     * @param reader                   whether this is the reader (true) or writer (false).
     * @param customIdentifiersAllowed whether custom identifiers are processed as custom.
     */
    public ChunkerBlockIdentifierResolver(Converter converter, Version version, boolean reader, boolean customIdentifiersAllowed) {
        this.converter = converter;
        this.version = version;
        this.reader = reader;
        this.customIdentifiersAllowed = customIdentifiersAllowed;
        registerMappings(version);
    }

    private <I, IK extends Comparable<? super IK>, IV, O, OK, OV> void createMapping(Map<I, InputStateGrouping<IK, IV, O, OK, OV>> lookup, I inputIdentifier, NavigableMap<IK, IV> inputStates, O outputIdentifier, NavigableMap<OK, OV> outputStates, StateMappingGroup stateMappingGroup, boolean override) {
        // Create the group if it's not present to lookup the inputs (note: by default when states aren't present chunker will use it's own BlockState defaults for output -> input)
        InputStateGrouping<IK, IV, O, OK, OV> inputStateGrouping = lookup.computeIfAbsent(inputIdentifier, (ignored) -> new InputStateGrouping<>(true));
        OutputMapping<O, OK, OV> outputMapping = new OutputMapping<>(
                outputIdentifier,
                outputStates,
                stateMappingGroup
        );

        // Fetch the group used for the input keys
        Map<List<IV>, OutputMapping<O, OK, OV>> inputStateLookup = inputStateGrouping.getGroups().computeIfAbsent(inputStates.navigableKeySet(), (ignored) -> new Object2ObjectOpenHashMap<>());
        List<IV> inputValues = new ArrayList<>(inputStates.values());

        // If override is enabled, we want to be explicit to find mistakes
        if (override) {
            // Use put
            if (inputStateLookup.put(inputValues, outputMapping) == null) {
                // We can't have an output collision, throw an error
                throw new IllegalArgumentException("No existing mapping to override, identifier: " + inputIdentifier + " states: " + inputStates);
            }
        } else {
            // Use putIfAbsent
            if (inputStateLookup.putIfAbsent(inputValues, outputMapping) != null) {
                // We can't have an output collision, throw an error
                throw new IllegalArgumentException("Duplicate state lookup entry, identifier: " + inputIdentifier + " states: " + inputStates);
            }
        }
    }

    /**
     * Register a block mapping where it is a duplicate input but should override the existing Chunker -> Input
     * mapping.
     *
     * @param mapping the block mapping.
     */
    protected void registerDuplicateOverrideOutput(BlockMapping mapping) {
        // Create chunker -> input
        createMapping(chunkerToInput, mapping.getBlockType(), mapping.getBlockStates(), mapping.getIdentifier(), mapping.getStates(), mapping.getStateMappingGroup(version), true);
    }

    /**
     * Register a block mapping where it is a duplicate output and should override the existing Chunker -> Output
     * mapping.
     *
     * @param mapping the block mapping.
     */
    protected void registerOverrideOutput(BlockMapping mapping) {
        // Create input -> chunker
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getBlockType(), mapping.getBlockStates(), mapping.getStateMappingGroup(version), false);

        // Create chunker -> input
        createMapping(chunkerToInput, mapping.getBlockType(), mapping.getBlockStates(), mapping.getIdentifier(), mapping.getStates(), mapping.getStateMappingGroup(version), true);
    }

    /**
     * Register a block mapping where it is a duplicate output but should override the existing Input -> Chunker
     * mapping.
     *
     * @param mapping the block mapping.
     */
    protected void registerDuplicateOverrideInput(BlockMapping mapping) {
        // Create input -> chunker
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getBlockType(), mapping.getBlockStates(), mapping.getStateMappingGroup(version), true);
    }

    /**
     * Register a block mapping where it is a duplicate input and should override the existing Input -> Chunker mapping.
     *
     * @param mapping the block mapping.
     */
    protected void registerOverrideInput(BlockMapping mapping) {
        // Create input -> chunker
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getBlockType(), mapping.getBlockStates(), mapping.getStateMappingGroup(version), true);

        // Create chunker -> input
        createMapping(chunkerToInput, mapping.getBlockType(), mapping.getBlockStates(), mapping.getIdentifier(), mapping.getStates(), mapping.getStateMappingGroup(version), false);
    }

    /**
     * Register a block mapping where it is a duplicate input and should override the existing input and output
     * mapping.
     *
     * @param mapping the block mapping.
     */
    protected void registerOverrideInputOutput(BlockMapping mapping) {
        // Create input -> chunker
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getBlockType(), mapping.getBlockStates(), mapping.getStateMappingGroup(version), true);

        // Create chunker -> input
        createMapping(chunkerToInput, mapping.getBlockType(), mapping.getBlockStates(), mapping.getIdentifier(), mapping.getStates(), mapping.getStateMappingGroup(version), true);
    }

    /**
     * Register an array of block mappings where they use duplicate outputs, and it should override the existing
     * Chunker -> input mapping.
     *
     * @param mappings the block mappings.
     */
    protected void registerOverrideOutput(BlockMapping[] mappings) {
        for (BlockMapping mapping : mappings) {
            registerOverrideOutput(mapping);
        }
    }

    /**
     * Register an array of block mappings where they use duplicate inputs, and it should override the existing
     * input -> Chunker mapping.
     *
     * @param mappings the block mappings.
     */
    protected void registerOverrideInput(BlockMapping[] mappings) {
        for (BlockMapping mapping : mappings) {
            registerOverrideInput(mapping);
        }
    }

    /**
     * Register a block mapping where it is a duplicate input and shouldn't override the existing
     * Input -> Chunker mapping.
     *
     * @param mapping the block mapping.
     */
    protected void registerDuplicateInput(BlockMapping mapping) {
        // Only register chunker -> input, since this is a duplicate
        createMapping(chunkerToInput, mapping.getBlockType(), mapping.getBlockStates(), mapping.getIdentifier(), mapping.getStates(), mapping.getStateMappingGroup(version), false);
    }

    /**
     * Register an array of block mappings where they use duplicate inputs and shouldn't override the existing
     * Input -> Chunker mapping.
     *
     * @param mappings the block mappings.
     */
    protected void registerDuplicateInput(BlockMapping[] mappings) {
        for (BlockMapping mapping : mappings) {
            registerDuplicateInput(mapping);
        }
    }

    /**
     * Register a block mapping where it is a duplicate output and shouldn't override the existing
     * Chunker -> Input mapping.
     *
     * @param mapping the block mapping.
     */
    protected void registerDuplicateOutput(BlockMapping mapping) {
        // Only register input -> chunker, since this is a duplicate
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getBlockType(), mapping.getBlockStates(), mapping.getStateMappingGroup(version), false);
    }

    /**
     * Register an array of block mappings where they use duplicate outputs and shouldn't override the existing
     * Chunker -> input mapping.
     *
     * @param mappings the block mappings.
     */
    protected void registerDuplicateOutput(BlockMapping[] mappings) {
        for (BlockMapping mapping : mappings) {
            registerDuplicateOutput(mapping);
        }
    }

    /**
     * Register a block mapping.
     *
     * @param mapping the block mapping.
     */
    protected void register(BlockMapping mapping) {
        // Create input -> chunker
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getBlockType(), mapping.getBlockStates(), mapping.getStateMappingGroup(version), false);

        // Create chunker -> input
        createMapping(chunkerToInput, mapping.getBlockType(), mapping.getBlockStates(), mapping.getIdentifier(), mapping.getStates(), mapping.getStateMappingGroup(version), false);
    }


    /**
     * Register an array of block mappings.
     *
     * @param mappings the block mappings.
     */
    protected void register(BlockMapping[] mappings) {
        for (BlockMapping mapping : mappings) {
            register(mapping);
        }
    }

    /**
     * Set the state mapping group which should be used for all blocks.
     *
     * @param stateMappingGroup the mapping group.
     */
    protected void extraStateMappingGroup(VersionedStateMappingGroup stateMappingGroup) {
        Preconditions.checkArgument(extraStateMappingGroup == null, "Only a single extra state mapping group is allowed.");
        extraStateMappingGroup = stateMappingGroup.getStateMappingGroup(version);
    }

    /**
     * Remove all the mappings from chunker -> input of a specific type.
     *
     * @param chunkerBlockType the chunker (output) type.
     */
    protected void removeOutputMapping(ChunkerBlockType chunkerBlockType) {
        chunkerToInput.remove(chunkerBlockType);
    }

    /**
     * Called on construction to register all the block mappings.
     *
     * @param version the version being used.
     */
    public abstract void registerMappings(Version version);

    /**
     * Check if it's possible that an identifier is supported by this resolver.
     *
     * @param identifier the input identifier in the format namespace:key.
     * @return true if the block can potentially be resolved.
     */
    public boolean isSupported(String identifier) {
        return inputToChunker.containsKey(identifier);
    }

    /**
     * Check if it's possible that an identifier is supported by this resolver.
     *
     * @param chunkerBlockType the block type object (usually the ChunkerVanillaBlockType enum entry).
     * @return true if the block can potentially be resolved.
     */
    public boolean isSupported(ChunkerBlockType chunkerBlockType) {
        return chunkerToInput.containsKey(chunkerBlockType);
    }

    /**
     * Handle any user input identifier conversion mappings.
     *
     * @param input  the original input.
     * @param output the output this resolver produced.
     * @return the output with any user mappings applied.
     */
    protected Optional<ChunkerBlockIdentifier> handleConverterMapping(Identifier input, Optional<ChunkerBlockIdentifier> output) {
        // Handle the converter mappings which go from input -> output
        MappingsFileResolvers mappingsFileResolvers = converter.getBlockMappings();
        if (mappingsFileResolvers == null) return output; // No mappings

        // Convert the block (using the inverse mappings if it's the writer)
        Optional<Identifier> mappedIdentifier = (reader ? mappingsFileResolvers.getMappings() : mappingsFileResolvers.getInverseMappings()).convertBlock(input);
        if (mappedIdentifier.isEmpty()) return output; // No custom mapping for this block

        // Attach the preserved identifier (the custom mapping for the output)
        return output.map(chunkerBlockIdentifier -> new ChunkerBlockIdentifier(
                chunkerBlockIdentifier.getType(),
                chunkerBlockIdentifier.getPresentStates(),
                new PreservedIdentifier(reader, mappedIdentifier.get())
        )).or(() -> Optional.of(new ChunkerBlockIdentifier(
                ChunkerVanillaBlockType.STONE, // Use stone as a placeholder
                Collections.emptyMap(),
                new PreservedIdentifier(reader, mappedIdentifier.get())
        )));
    }

    /**
     * Handle any user chunker input conversion mappings.
     *
     * @param input  the original input.
     * @param output the output this resolver produced.
     * @return the output with any user mappings applied.
     */
    protected Optional<Identifier> handleConverterMapping(ChunkerBlockIdentifier input, Optional<Identifier> output) {
        // If there is no preserved identifier, return the original
        // Otherwise if the preserved identifier is the same as this, don't apply it as it's for the writer
        if (input.getPreservedIdentifier() == null || reader == input.getPreservedIdentifier().fromReader())
            return output;

        // Convert the preserved identifier to the chunker format
        Optional<ChunkerBlockIdentifier> preservedAsChunker = resolveTo(input.getPreservedIdentifier().identifier());
        if (preservedAsChunker.isPresent()) {
            Map<BlockState<?>, BlockStateValue> states = new Object2ObjectOpenHashMap<>(preservedAsChunker.get().getPresentStates());

            // Apply the input states
            states.putAll(input.getPresentStates());

            // Make a new copy with the preserved identifier + the states as a hashmap
            ChunkerBlockIdentifier merged = new ChunkerBlockIdentifier(
                    preservedAsChunker.get().getType(),
                    states
            );

            // Now convert it back to an identifier
            Optional<Identifier> preservedConverted = resolveFrom(merged);

            // If it's possible to convert, we can merge any states produced
            if (preservedConverted.isPresent()) {
                Map<String, StateValue<?>> newOutputStates = new Object2ObjectOpenHashMap<>(preservedConverted.get().getStates());

                // Replace states with the original preserved
                newOutputStates.putAll(input.getPreservedIdentifier().identifier().getStates());

                return Optional.of(new Identifier(
                        input.getPreservedIdentifier().identifier().getIdentifier(),
                        newOutputStates
                ));
            }
        }

        // Directly use the preserved identifier as it's not possible to merge any states
        return Optional.of(input.getPreservedIdentifier().identifier());
    }

    /**
     * Called when an input identifier could not be mapped.
     *
     * @param identifier the identifier that couldn't be mapped.
     * @return the custom block type if it's supported.
     */
    protected Optional<ChunkerBlockIdentifier> handleFallback(Identifier identifier) {
        // Custom mappings aren't supported on minecraft: or when not namespaced or when not enabled
        if (identifier.getIdentifier().startsWith("minecraft:") || !identifier.getIdentifier().contains(":") || !customIdentifiersAllowed)
            return Optional.empty();

        // Create the custom block wrapper
        return Optional.of(ChunkerBlockIdentifier.custom(identifier.getIdentifier(), identifier.getBoxedStates()));
    }

    /**
     * Called when an input chunker identifier could not be mapped.
     *
     * @param input the block identifier that couldn't be mapped.
     * @return the custom block type if it's supported.
     */
    protected Optional<Identifier> handleFallback(ChunkerBlockIdentifier input) {
        // Custom mappings aren't supported on non ChunkerCustomBlockType or when not enabled
        if (!(input.getType() instanceof ChunkerCustomBlockType chunkerCustomBlockType) || !customIdentifiersAllowed)
            return Optional.empty();

        // Convert the chunker states into keys and values
        Map<BlockState<?>, BlockStateValue> states = input.getPresentStates();
        Map<String, StateValue<?>> newMap = new Object2ObjectOpenHashMap<>(states.size());
        for (Map.Entry<BlockState<?>, BlockStateValue> entry : states.entrySet()) {
            if (entry.getValue() instanceof ChunkerCustomBlockType.CustomBlockStateValue<?> customBlockStateValue) {
                newMap.put(entry.getKey().getName(), StateValue.fromBoxed(customBlockStateValue.getStateValue()));
            }
        }

        // Return the new identifier
        return Optional.of(new Identifier(
                chunkerCustomBlockType.getIdentifier(),
                newMap
        ));
    }

    @Override
    public Optional<ChunkerBlockIdentifier> to(Identifier input) {
        return handleConverterMapping(input, resolveTo(input));
    }

    /**
     * Convert an input identifier using the mappings to an output, calling the fallback method if the output is
     * absent.
     *
     * @param input the input identifier.
     * @return the output if present.
     */
    protected Optional<ChunkerBlockIdentifier> resolveTo(Identifier input) {
        Optional<ChunkerBlockIdentifier> result = resolve(
                inputToChunker,
                input.getIdentifier(),
                (StateLookupFunction.DefaultlessStateLookupFunction<String, Object>) input.getBoxedStates()::get,
                StateMappingGroup::applyInput,
                ChunkerBlockIdentifier::new
        );

        // Run fallback behaviour
        if (result.isEmpty()) {
            result = handleFallback(input);
        }
        return result;
    }

    @Override
    public Optional<Identifier> from(ChunkerBlockIdentifier input) {
        return handleConverterMapping(input, resolveFrom(input));
    }

    /**
     * Convert an input chunker identifier using the mappings to an output, calling the fallback method if the output is
     * absent.
     *
     * @param input the input chunker block identifier.
     * @return the output if present.
     */
    protected Optional<Identifier> resolveFrom(ChunkerBlockIdentifier input) {
        Optional<Identifier> result = resolve(
                chunkerToInput,
                input.getType(),
                input::getState,
                StateMappingGroup::applyOutput,
                Identifier::fromBoxed
        );

        // Run fallback behaviour
        if (result.isEmpty()) {
            result = handleFallback(input);
        }
        return result;
    }

    private <I, IK extends Comparable<? super IK>, IV, O, OK, OV, T> Optional<T> resolve(Map<I, InputStateGrouping<IK, IV, O, OK, OV>> lookup, I inputIdentifier, StateLookupFunction<IK, IV> inputStateLookup, StateMappingGroupHandler<IK, IV, OK, OV> mappingGroupHandler, BiFunction<O, Map<OK, OV>, T> outputConstructor) {
        InputStateGrouping<IK, IV, O, OK, OV> grouping = lookup.get(inputIdentifier);

        // If the grouping is null, return empty
        if (grouping == null) {
            return Optional.empty();
        }

        // Find the first matching InputStateGroup
        List<IV> tempList = new ArrayList<>();
        groups:
        for (Map.Entry<SortedSet<IK>, Map<List<IV>, OutputMapping<O, OK, OV>>> group : grouping.getGroups().entrySet()) {
            for (IK stateName : group.getKey()) {
                IV stateValue = inputStateLookup.getState(stateName, grouping.isDefaultsAllowed());
                if (stateValue == null) {
                    // Field not found
                    tempList.clear();
                    continue groups;
                }

                // Add the boxed value to our list (as we use raw types and not state values here)
                tempList.add(stateValue);
            }

            // Check if the list of values was found
            OutputMapping<O, OK, OV> mapping = group.getValue().get(tempList);
            if (mapping != null) {
                // Use this mapping
                Map<OK, OV> states = new Object2ObjectOpenHashMap<>(mapping.getOutputs().size());

                // Add all the states from the mapping
                states.putAll(mapping.getOutputs());

                // Now apply the StateGroupMapping
                StateMappingGroup stateMappingGroup = mapping.getGroup();
                if (stateMappingGroup != null) {
                    mappingGroupHandler.apply(stateMappingGroup, inputStateLookup, states);
                }

                // Process using the extra state processor (applies states like waterlogged for Bedrock)
                if (extraStateMappingGroup != null) {
                    mappingGroupHandler.apply(extraStateMappingGroup, inputStateLookup, states);
                }

                // Create the identifier and return it
                return Optional.of(outputConstructor.apply(mapping.getIdentifier(), states));
            } else {
                // Otherwise continue looking
                tempList.clear();
            }
        }

        // No specific mapping found
        return Optional.empty();
    }

    /**
     * Interface to allow state mapping groups to use the same code for resolving inputs and outputs.
     *
     * @param <IK> the input key type.
     * @param <IV> the input value type.
     * @param <OK> the output key type.
     * @param <OV> the output value type.
     */
    interface StateMappingGroupHandler<IK, IV, OK, OV> {
        void apply(StateMappingGroup group, StateLookupFunction<IK, IV> inputStateLookup, Map<OK, OV> outputs);
    }

    /**
     * A grouping of inputs so that it can be determined which mapping matches.
     *
     * @param <IK> the input state key type.
     * @param <IV> the input state value type.
     * @param <O>  the output identifier.
     * @param <OK> the output state key type.
     * @param <OV> the output state value type.
     */
    public static class InputStateGrouping<IK extends Comparable<? super IK>, IV, O, OK, OV> {
        private final boolean defaultsAllowed;
        private final TreeMap<SortedSet<IK>, Map<List<IV>, OutputMapping<O, OK, OV>>> groups = new TreeMap<>(
                // Sorted by the largest set first
                new CollectionComparator<Set<IK>, IK>().reversed()
        );

        /**
         * Create a new input state group.
         *
         * @param defaultsAllowed whether defaults are allowed to be used before looking up the group to use.
         */
        public InputStateGrouping(boolean defaultsAllowed) {
            this.defaultsAllowed = defaultsAllowed;
        }

        public TreeMap<SortedSet<IK>, Map<List<IV>, OutputMapping<O, OK, OV>>> getGroups() {
            return groups;
        }

        public boolean isDefaultsAllowed() {
            return defaultsAllowed;
        }
    }

    /**
     * Represents an output which is produced from mappings.
     *
     * @param <O>  the type of the identifier.
     * @param <OK> the state key type.
     * @param <OV> the state value type.
     */
    public static class OutputMapping<O, OK, OV> {
        private final O identifier;
        private final Map<OK, OV> outputs;
        private final StateMappingGroup group;

        public OutputMapping(O identifier, Map<OK, OV> outputs, StateMappingGroup group) {
            this.identifier = identifier;
            this.outputs = outputs;
            this.group = group;
        }

        public O getIdentifier() {
            return identifier;
        }

        public Map<OK, OV> getOutputs() {
            return outputs;
        }

        @Nullable
        public StateMappingGroup getGroup() {
            return group;
        }
    }
}
