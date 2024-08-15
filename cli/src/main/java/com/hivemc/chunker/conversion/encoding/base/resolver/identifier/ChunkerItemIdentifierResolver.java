package com.hivemc.chunker.conversion.encoding.base.resolver.identifier;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifierType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.PreservedIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerItemType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerVanillaItemType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemProperty;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.identifier.states.StateValueInt;
import com.hivemc.chunker.mapping.resolver.MappingsFileResolvers;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.resolver.property.Property;
import com.hivemc.chunker.util.CollectionComparator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.*;

/**
 * Resolver to convert a format specific Identifier into a ChunkerItemStackIdentifier.
 * It uses mapping rules to handle the mappings between various items. This is very similar to
 * ChunkerBlockIdentifierResolver but allows properties to be used and has no mapping groups.
 */
public abstract class ChunkerItemIdentifierResolver implements Resolver<Identifier, ChunkerItemStack> {
    protected final Converter converter;
    protected final Version version;
    protected final boolean defaultData;
    protected final boolean reader;
    protected final Map<String, InputStateGrouping<String, Object, ChunkerItemStackIdentifierType, ComparableItemProperty<?>, Object>> inputToChunker = new Object2ObjectOpenHashMap<>();
    protected final Map<ChunkerItemStackIdentifierType, InputStateGrouping<ComparableItemProperty<?>, Object, String, String, Object>> chunkerToInput = new Object2ObjectOpenHashMap<>();

    /**
     * Create a new item identifier resolver.
     *
     * @param converter   the converter instance.
     * @param version     the version being resolved.
     * @param reader      whether this is the reader (true) or writer (false).
     * @param defaultData whether the data should default to 0 instead of absent.
     */
    public ChunkerItemIdentifierResolver(Converter converter, Version version, boolean reader, boolean defaultData) {
        this.converter = converter;
        this.version = version;
        this.reader = reader;
        this.defaultData = defaultData;
        registerMappings(version);
    }

    private <I, IK extends Comparable<? super IK>, IV, O, OK, OV> void createMapping(Map<I, InputStateGrouping<IK, IV, O, OK, OV>> lookup, I inputIdentifier, NavigableMap<IK, IV> inputStates, O outputIdentifier, NavigableMap<OK, OV> outputStates, boolean override) {
        // Create the group if it's not present to lookup the inputs
        InputStateGrouping<IK, IV, O, OK, OV> inputStateGrouping = lookup.computeIfAbsent(inputIdentifier, (ignored) -> new InputStateGrouping<>());
        OutputMapping<O, OK, OV> outputMapping = new OutputMapping<>(
                outputIdentifier,
                outputStates
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
     * Register an item mapping where it is a duplicate output and should override the existing Chunker -> Output
     * mapping.
     *
     * @param mapping the item mapping.
     */
    protected void registerOverrideOutput(ItemMapping mapping) {
        // Create input -> chunker
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getItemStackIdentifierType(), mapping.getProperties(), false);

        // Create chunker -> input
        createMapping(chunkerToInput, mapping.getItemStackIdentifierType(), mapping.getProperties(), mapping.getIdentifier(), mapping.getStates(), true);
    }

    /**
     * Register an item mapping where it is a duplicate input and should override the existing Input -> Chunker mapping.
     *
     * @param mapping the item mapping.
     */
    protected void registerOverrideInput(ItemMapping mapping) {
        // Create input -> chunker
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getItemStackIdentifierType(), mapping.getProperties(), true);

        // Create chunker -> input
        createMapping(chunkerToInput, mapping.getItemStackIdentifierType(), mapping.getProperties(), mapping.getIdentifier(), mapping.getStates(), false);
    }

    /**
     * Register an item mapping where it is a duplicate input and should override the existing input and output
     * mapping.
     *
     * @param mapping the item mapping.
     */
    protected void registerOverrideInputOutput(ItemMapping mapping) {
        // Create input -> chunker
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getItemStackIdentifierType(), mapping.getProperties(), true);

        // Create chunker -> input
        createMapping(chunkerToInput, mapping.getItemStackIdentifierType(), mapping.getProperties(), mapping.getIdentifier(), mapping.getStates(), true);
    }

    /**
     * Register an array of item mappings where they use duplicate outputs, and it should override the existing
     * Chunker -> input mapping.
     *
     * @param mappings the item mappings.
     */
    protected void registerOverrideOutput(ItemMapping[] mappings) {
        for (ItemMapping mapping : mappings) {
            registerOverrideOutput(mapping);
        }
    }

    /**
     * Register an array of item mappings where they use duplicate inputs, and it should override the existing
     * input -> Chunker mapping.
     *
     * @param mappings the item mappings.
     */
    protected void registerOverrideInput(ItemMapping[] mappings) {
        for (ItemMapping mapping : mappings) {
            registerOverrideInput(mapping);
        }
    }

    /**
     * Register an item mapping where it is a duplicate input and shouldn't override the existing
     * Input -> Chunker mapping.
     *
     * @param mapping the item mapping.
     */
    protected void registerDuplicateInput(ItemMapping mapping) {
        // Only register chunker -> input, since this is a duplicate
        createMapping(chunkerToInput, mapping.getItemStackIdentifierType(), mapping.getProperties(), mapping.getIdentifier(), mapping.getStates(), false);
    }

    /**
     * Register an array of item mappings where they use duplicate inputs and shouldn't override the existing
     * Input -> Chunker mapping.
     *
     * @param mappings the item mappings.
     */
    protected void registerDuplicateInput(ItemMapping[] mappings) {
        for (ItemMapping mapping : mappings) {
            registerDuplicateInput(mapping);
        }
    }

    /**
     * Register an item mapping where it is a duplicate output and shouldn't override the existing
     * Chunker -> Input mapping.
     *
     * @param mapping the item mapping.
     */
    protected void registerDuplicateOutput(ItemMapping mapping) {
        // Only register input -> chunker, since this is a duplicate
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getItemStackIdentifierType(), mapping.getProperties(), false);
    }

    /**
     * Register an array of item mappings where they use duplicate outputs and shouldn't override the existing
     * Chunker -> input mapping.
     *
     * @param mappings the item mappings.
     */
    protected void registerDuplicateOutput(ItemMapping[] mappings) {
        for (ItemMapping mapping : mappings) {
            registerDuplicateOutput(mapping);
        }
    }

    /**
     * Register a item mapping.
     *
     * @param mapping the item mapping.
     */
    protected void register(ItemMapping mapping) {
        // Create input -> chunker
        createMapping(inputToChunker, mapping.getIdentifier(), mapping.getStates(), mapping.getItemStackIdentifierType(), mapping.getProperties(), false);

        // Create chunker -> input
        createMapping(chunkerToInput, mapping.getItemStackIdentifierType(), mapping.getProperties(), mapping.getIdentifier(), mapping.getStates(), false);
    }

    /**
     * Register an array of item mappings.
     *
     * @param mappings the item mappings.
     */
    protected void register(ItemMapping[] mappings) {
        for (ItemMapping mapping : mappings) {
            register(mapping);
        }
    }

    /**
     * Remove all the mappings from chunker -> input of a specific type.
     *
     * @param chunkerItemStackIdentifierType the chunker (output) type.
     */
    protected void removeOutputMapping(ChunkerItemStackIdentifierType chunkerItemStackIdentifierType) {
        chunkerToInput.remove(chunkerItemStackIdentifierType);
    }

    /**
     * Called on construction to register all the item mappings.
     *
     * @param version the version being used.
     */
    public abstract void registerMappings(Version version);

    /**
     * Check if it's possible that an identifier is supported by this resolver.
     *
     * @param identifier the input identifier in the format namespace:key.
     * @return true if the item can potentially be resolved.
     */
    public boolean isSupported(String identifier) {
        return inputToChunker.containsKey(identifier);
    }

    /**
     * Check if it's possible that an identifier is supported by this resolver.
     *
     * @param chunkerItemType the item type object (usually the ChunkerVanillaItemType enum entry).
     * @return true if the item can potentially be resolved.
     */
    public boolean isSupported(ChunkerItemType chunkerItemType) {
        return chunkerToInput.containsKey(chunkerItemType);
    }

    /**
     * Handle any user input identifier conversion mappings.
     *
     * @param input  the original input.
     * @param output the output this resolver produced.
     * @return the output with any user mappings applied.
     */
    protected Optional<ChunkerItemStack> handleConverterMapping(Identifier input, Optional<ChunkerItemStack> output) {
        // Handle the converter mappings which go from input -> output
        MappingsFileResolvers mappingsFileResolvers = converter.getBlockMappings();
        if (mappingsFileResolvers == null) return output; // No mappings

        // Convert the item (using the inverse mappings if it's the writer)
        Optional<Identifier> mappedIdentifier = (reader ? mappingsFileResolvers.getMappings() : mappingsFileResolvers.getInverseMappings()).convertItem(input);
        if (mappedIdentifier.isEmpty()) return output; // No custom mapping for this block

        // Attach the preserved identifier (the custom mapping for the output)
        return output.map(chunkerItemStack -> new ChunkerItemStack(
                chunkerItemStack.getIdentifier(),
                new PreservedIdentifier(reader, mappedIdentifier.get()),
                chunkerItemStack.getProperties()
        )).or(() -> Optional.of(new ChunkerItemStack(
                ChunkerVanillaItemType.STICK, // Use a stick as a placeholder
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
    protected Optional<Identifier> handleConverterMapping(ChunkerItemStack input, Optional<Identifier> output) {
        // If there is no preserved identifier, return the original
        // Otherwise if the preserved identifier is the same as this, don't apply it as it's for the writer
        if (input.getPreservedIdentifier() == null || reader == input.getPreservedIdentifier().fromReader())
            return output;

        // Convert the preserved identifier to the chunker format
        Optional<ChunkerItemStack> preservedAsChunker = resolveTo(input.getPreservedIdentifier().identifier());
        if (preservedAsChunker.isPresent() && preservedAsChunker.get().getIdentifier() instanceof ChunkerBlockIdentifier blockIdentifier) {
            Map<BlockState<?>, BlockStateValue> states = new Object2ObjectOpenHashMap<>(blockIdentifier.getPresentStates());

            // Apply the input states
            states.putAll(blockIdentifier.getPresentStates());

            // Make a new copy with the preserved identifier + the states as a hashmap
            ChunkerItemStack merged = new ChunkerItemStack(
                    new ChunkerBlockIdentifier(
                            blockIdentifier.getType(),
                            states
                    )
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

    @Override
    public Optional<Identifier> from(ChunkerItemStack input) {
        return handleConverterMapping(input, resolveFrom(input));
    }

    /**
     * Convert an input identifier using the mappings to an output.
     *
     * @param input the input identifier.
     * @return the output if present.
     */
    protected Optional<ChunkerItemStack> resolveTo(Identifier input) {
        InputStateGrouping<String, Object, ChunkerItemStackIdentifierType, ComparableItemProperty<?>, Object> grouping = inputToChunker.get(input.getIdentifier());

        // Custom items are just treated as blocks, so there is no support done here (for now)
        // Just return empty so that it is handled as a block
        if (grouping == null) return Optional.empty();

        // Find the first matching InputStateGroup
        List<Object> tempList = new ArrayList<>();
        groups:
        for (Map.Entry<SortedSet<String>, Map<List<Object>, OutputMapping<ChunkerItemStackIdentifierType, ComparableItemProperty<?>, Object>>> group : grouping.getGroups().entrySet()) {
            for (String stateName : group.getKey()) {
                StateValue<?> stateValue = input.getStates().get(stateName);

                // Check if the value was found
                if (stateValue == null) {
                    // Field not found
                    tempList.clear();
                    continue groups;
                }

                // Add the boxed value to our list (as we use raw types and not state values here)
                tempList.add(stateValue.getBoxed());
            }

            // Check if the list of values was found
            OutputMapping<ChunkerItemStackIdentifierType, ComparableItemProperty<?>, Object> mapping = group.getValue().get(tempList);
            if (mapping != null) {
                // Use this mapping
                Map<Property<? super ChunkerItemStack, ?>, Object> properties = new Object2ObjectOpenHashMap<>(mapping.getOutputs().size());
                Map<BlockState<?>, BlockStateValue> states = new Object2ObjectOpenHashMap<>();

                // Add all the properties from the mapping
                for (Map.Entry<ComparableItemProperty<?>, Object> entry : mapping.getOutputs().entrySet()) {
                    if (entry.getKey() instanceof ChunkerItemProperty<?> property) {
                        properties.put(property, entry.getValue());
                    } else if (entry.getKey() instanceof BlockState<?> blockState && entry.getValue() instanceof BlockStateValue blockStateValue) {
                        states.put(blockState, blockStateValue);
                    } else {
                        throw new IllegalArgumentException("Unsupported property: " + entry.getKey());
                    }
                }

                // Apply block states if it's possible
                ChunkerItemStackIdentifier type;
                if (mapping.getIdentifier() instanceof ChunkerBlockType blockType) {
                    type = new ChunkerBlockIdentifier(
                            blockType,
                            states
                    );
                } else if (mapping.getIdentifier() instanceof ChunkerItemStackIdentifier itemStackIdentifier) {
                    type = itemStackIdentifier;
                } else {
                    // This should never happen, an identifier is either a block or an item.
                    throw new IllegalArgumentException("Unable to create ChunkerItemStackIdentifier from " + mapping.getIdentifier());
                }

                // Create the identifier and return it
                return Optional.of(new ChunkerItemStack(type, properties));
            } else {
                // Otherwise continue looking
                tempList.clear();
            }
        }

        // No specific mapping found
        return Optional.empty();
    }

    @Override
    public Optional<ChunkerItemStack> to(Identifier input) {
        return handleConverterMapping(input, resolveTo(input));
    }

    /**
     * Convert an input chunker item stack using the mappings to an output.
     *
     * @param input the input chunker item stack.
     * @return the output if present.
     */
    protected Optional<Identifier> resolveFrom(ChunkerItemStack input) {
        InputStateGrouping<ComparableItemProperty<?>, Object, String, String, Object> grouping = chunkerToInput.get(input.getIdentifier().getItemStackType());

        // Custom items are just treated as blocks, so there is no support done here (for now)
        // Just return empty so that it is handled as a block
        if (grouping == null) return Optional.empty();

        // Find the first matching InputStateGroup
        List<Object> tempList = new ArrayList<>();
        groups:
        for (Map.Entry<SortedSet<ComparableItemProperty<?>>, Map<List<Object>, OutputMapping<String, String, Object>>> group : grouping.getGroups().entrySet()) {
            for (ComparableItemProperty<?> propertyType : group.getKey()) {
                Object propertyValue;
                if (propertyType instanceof ChunkerItemProperty<?> property) {
                    propertyValue = input.get(property);
                } else if (propertyType instanceof BlockState<?> blockState && input.getIdentifier() instanceof ChunkerBlockIdentifier blockIdentifier) {
                    propertyValue = blockIdentifier.getState(blockState);
                } else {
                    throw new IllegalArgumentException("Unsupported property: " + propertyType);
                }

                // Check if the value was found
                if (propertyValue == null) {
                    // Field not found
                    tempList.clear();
                    continue groups;
                }

                // Add the boxed value to our list (as we use raw types and not state values here)
                tempList.add(propertyValue);
            }

            // Check if the list of values was found
            OutputMapping<String, String, Object> mapping = group.getValue().get(tempList);
            if (mapping != null) {
                // Create the identifier and return it
                Map<String, Object> data = mapping.getOutputs();
                Identifier identifier = Identifier.fromBoxed(mapping.getIdentifier(), new HashMap<>(data));

                // Add default data value if it's missing and needed
                if (identifier.getDataValue().isEmpty() && defaultData) {
                    identifier.getStates().put("data", new StateValueInt(0)); // Use 0 for default
                }
                return Optional.of(identifier);
            } else {
                // Otherwise continue looking
                tempList.clear();
            }
        }

        // No specific mapping found
        return Optional.empty();
    }

    /**
     * A grouping of inputs so that it can be determined which mapping matches.
     *
     * @param <IK> the input state key type.
     * @param <IV> the input state value type.
     * @param <O>  the output identifier.
     * @param <OK> the output state key type.
     * @param <OV> the output value type.
     */
    public static class InputStateGrouping<IK extends Comparable<? super IK>, IV, O, OK, OV> {
        private final TreeMap<SortedSet<IK>, Map<List<IV>, OutputMapping<O, OK, OV>>> groups = new TreeMap<>(
                // Sorted by the largest set first
                new CollectionComparator<Set<IK>, IK>().reversed()
        );

        public TreeMap<SortedSet<IK>, Map<List<IV>, OutputMapping<O, OK, OV>>> getGroups() {
            return groups;
        }
    }

    /**
     * The output mapping.
     *
     * @param <O>  the identifier output.
     * @param <OK> the state key type / property type.
     * @param <OV> the state value type.
     */
    public static class OutputMapping<O, OK, OV> {
        private final O identifier;
        private final Map<OK, OV> outputs;

        public OutputMapping(O identifier, Map<OK, OV> outputs) {
            this.identifier = identifier;
            this.outputs = outputs;
        }

        public O getIdentifier() {
            return identifier;
        }

        public Map<OK, OV> getOutputs() {
            return outputs;
        }
    }
}
