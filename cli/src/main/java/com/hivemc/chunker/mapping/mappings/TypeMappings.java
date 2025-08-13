package com.hivemc.chunker.mapping.mappings;

import com.google.gson.*;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.*;

/**
 * Encapsulates a type which can map different inputs to outputs.
 * e.g. [A -> B, C -> D]
 */
public class TypeMappings {
    private final String name;
    private final Map<List<StateValue<?>>, TypeMapping> inputsToOutputsLookup;
    private final List<TypeMapping> redundant;

    /**
     * Create new type mappings.
     *
     * @param name                  the name of the group of type mappings.
     * @param inputsToOutputsLookup the lookup for converting inputs into outputs.
     * @param redundant             the mappings which overlap with the inputs and can't be used unless inversed.
     */
    public TypeMappings(String name, Map<List<StateValue<?>>, TypeMapping> inputsToOutputsLookup, List<TypeMapping> redundant) {
        this.name = name;
        this.inputsToOutputsLookup = inputsToOutputsLookup;
        this.redundant = redundant;
    }

    /**
     * Deserialize type mappings from json.
     *
     * @param name        the index in the parent array this entry is.
     * @param jsonElement the json element to be decoded.
     * @param context     the current deserialization context.
     * @return type mappings based on the json.
     * @throws JsonParseException if it failed to parse the entries.
     */
    public static TypeMappings deserialize(String name, JsonElement jsonElement, JsonDeserializationContext context) throws JsonParseException {
        Map<List<StateValue<?>>, TypeMapping> inputsToOutputsLookup = new Object2ObjectOpenHashMap<>();
        List<TypeMapping> redundant = new ArrayList<>();

        // Loop through each entry
        int index = 0;
        for (JsonElement entry : jsonElement.getAsJsonArray()) {
            TypeMapping typeMapping = TypeMapping.deserialize(index++, entry, context);

            // Try adding otherwise add to redundant
            if (inputsToOutputsLookup.putIfAbsent(typeMapping.getInput(), typeMapping) != null) {
                redundant.add(typeMapping);
            }
        }

        return new TypeMappings(name, inputsToOutputsLookup, redundant);
    }

    /**
     * Get the name of these type mappings.
     *
     * @return the name used.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the lookup used for finding the outputs to use based on the inputs.
     *
     * @return the backing map.
     */
    public Map<List<StateValue<?>>, TypeMapping> getInputsToOutputsLookup() {
        return inputsToOutputsLookup;
    }

    /**
     * Get the mappings which were made redundant when loading the file.
     *
     * @return a list of mappings which inputs overlap with the inputsToOutputsLookup.
     */
    public List<TypeMapping> getRedundant() {
        return redundant;
    }

    /**
     * Create an inverse copy of these TypeMappings.
     *
     * @return a copy where the output is the input and input is output for each enclosed mapping.
     */
    public TypeMappings inverse() {
        Map<List<StateValue<?>>, TypeMapping> inverseInputsToOutputsLookup = new Object2ObjectOpenHashMap<>(inputsToOutputsLookup.size());
        ArrayList<TypeMapping> inverseRedundant = new ArrayList<>(redundant.size());

        for (Map.Entry<List<StateValue<?>>, TypeMapping> oldEntry : inputsToOutputsLookup.entrySet()) {
            TypeMapping inverseTypeMapping = oldEntry.getValue().inverse();

            TypeMapping existing = inverseInputsToOutputsLookup.putIfAbsent(inverseTypeMapping.getInput(), inverseTypeMapping);
            if (existing != null) {
                // Add to redundant if it's a duplicate (and the index is higher than the existing)
                if (existing.getIndex() <= inverseTypeMapping.getIndex()) {
                    inverseRedundant.add(inverseTypeMapping);
                } else {
                    // If the index is lower of the new value, we should replace it and add the existing to redundant
                    inverseInputsToOutputsLookup.replace(inverseTypeMapping.getInput(), existing, inverseTypeMapping);
                    inverseRedundant.add(existing);
                }
            }
        }

        // Redundant Types (duplicates)
        for (TypeMapping oldRedundantEntry : redundant) {
            TypeMapping inverseTypeMapping = oldRedundantEntry.inverse();

            TypeMapping existing = inverseInputsToOutputsLookup.putIfAbsent(inverseTypeMapping.getInput(), inverseTypeMapping);
            if (existing != null) {
                // Add to redundant if it's a duplicate (and the index is higher than the existing)
                if (existing.getIndex() <= inverseTypeMapping.getIndex()) {
                    inverseRedundant.add(inverseTypeMapping);
                } else {
                    // If the index is lower of the new value, we should replace it and add the existing to redundant
                    inverseInputsToOutputsLookup.replace(inverseTypeMapping.getInput(), existing, inverseTypeMapping);
                    inverseRedundant.add(existing);
                }
            }
        }

        return new TypeMappings(
                name,
                inverseInputsToOutputsLookup,
                inverseRedundant
        );
    }

    @Override
    public String toString() {
        return "TypeMappings{" +
                "name='" + name + '\'' +
                ", inputsToOutputsLookup=" + inputsToOutputsLookup +
                ", redundant=" + redundant +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeMappings that = (TypeMappings) o;
        return Objects.equals(inputsToOutputsLookup, that.inputsToOutputsLookup) && Objects.equals(redundant, that.redundant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputsToOutputsLookup, redundant);
    }

    /**
     * Serialize the type mappings into json.
     *
     * @param context the current context for serialization.
     * @return a json array of all the contained type mappings.
     */
    public JsonElement serialize(JsonSerializationContext context) {
        // Sort type mappings
        List<TypeMapping> fullTypeMappings = new ArrayList<>(inputsToOutputsLookup.size() + redundant.size());
        fullTypeMappings.addAll(inputsToOutputsLookup.values());
        fullTypeMappings.addAll(redundant);
        fullTypeMappings.sort(Comparator.comparing(TypeMapping::getIndex));

        // Serialize
        JsonArray jsonArray = new JsonArray(fullTypeMappings.size());
        for (TypeMapping typeMapping : fullTypeMappings) {
            jsonArray.add(typeMapping.serialize(context));
        }
        return jsonArray;
    }
}
