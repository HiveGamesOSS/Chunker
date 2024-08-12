package com.hivemc.chunker.mapping.mappings;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.identifier.states.StateValueString;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A state mapping which indicates what state names to change into other state names as well as the type mappings to use
 * for those states.
 */
public class StateMapping {
    private static final Type TYPE_STRING_LIST = new TypeToken<List<String>>() {
    }.getType();

    private final List<String> oldNames;
    private final List<String> newNames;
    private final TypeMappings typeMappings;

    /**
     * Create a new state mapping.
     *
     * @param oldNames     the names to match against.
     * @param newNames     the names to use for new states produced by the type mappings.
     * @param typeMappings the type mappings to use when mapping, null will map the input values to output.
     */
    public StateMapping(List<String> oldNames, List<String> newNames, TypeMappings typeMappings) {
        this.oldNames = oldNames;
        this.newNames = newNames;
        this.typeMappings = typeMappings;
    }

    /**
     * Turn a list of strings into a single json primitive or a json array of strings.
     *
     * @param input   the input list of strings.
     * @param context the serialization context.
     * @return a primitive string or json array which has string primitives.
     * @throws JsonParseException if there was an issue serializing the array.
     */
    private static JsonElement serializeStringList(List<String> input, JsonSerializationContext context) throws JsonParseException {
        if (input.size() == 1) {
            return new JsonPrimitive(input.iterator().next());
        } else {
            return context.serialize(input, TYPE_STRING_LIST);
        }
    }

    /**
     * Deserialize a StateMapping from a JSON object.
     *
     * @param types       the type lookup used for looking up types which are present in the state mapping.
     * @param jsonElement the element being deserialized that should be a json object.
     * @param context     the deserialization context.
     * @return the newly deserialized state mapping.
     * @throws JsonParseException if it failed to deserialize the input json.
     */
    public static StateMapping deserialize(Map<String, TypeMappings> types, JsonElement jsonElement, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        List<String> oldNames = deserializeStringList(jsonObject.get("old_state"), context);
        List<String> newNames = deserializeStringList(jsonObject.get("new_state"), context);

        // Create type
        String typeMappingName = jsonObject.has("type") ? jsonObject.get("type").getAsString() : null;
        TypeMappings typeMappings;
        if (typeMappingName == null || typeMappingName.isEmpty()) {
            typeMappings = null;
        } else {
            typeMappings = types.get(typeMappingName);
            if (typeMappings == null) {
                throw new JsonParseException("Could not find type " + typeMappingName);
            }
        }

        // Create the mapping
        return new StateMapping(oldNames, newNames, typeMappings);
    }

    /**
     * Turn either a list of strings or a single string into a String List.
     *
     * @param jsonElement the json element to parse (either a json array or primitive string).
     * @param context     the deserialization context.
     * @return the newly parsed string list.
     * @throws JsonParseException if there was an issue during deserialization.
     */
    private static List<String> deserializeStringList(JsonElement jsonElement, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement.isJsonArray()) {
            return context.deserialize(jsonElement, TYPE_STRING_LIST);
        } else {
            return List.of(jsonElement.getAsString());
        }
    }

    /**
     * A list of names which should be matched from the input, usually this is just a single value. If the empty string
     * is provided then it will be matched regardless and passthrough an empty string value for the type mapping.
     *
     * @return a list of names to look for in the states.
     */
    public List<String> getOldNames() {
        return oldNames;
    }

    /**
     * A list of names to be used as new names, these should match the number of outputs the type mapping has if one is
     * used or alternatively the same number as the old names if there is no type mapping.
     *
     * @return the list of names to set as output state names.
     */
    public List<String> getNewNames() {
        return newNames;
    }

    /**
     * Get the type mapping to use if all the old names are present.
     *
     * @return the mapping, can be null which would indicate to passthrough any input values as output values.
     */
    public TypeMappings getTypeMapping() {
        return typeMappings;
    }

    /**
     * Create an inverse copy of this StateMapping.
     *
     * @param inverseTypeMappingLookup the lookup used for replacing types with inverted types.
     * @return a copy where the state mapping has been inversed.
     */
    public StateMapping inverse(Function<TypeMappings, TypeMappings> inverseTypeMappingLookup) {
        return new StateMapping(
                newNames,
                oldNames,
                typeMappings == null ? null : inverseTypeMappingLookup.apply(typeMappings)
        );
    }

    @Override
    public String toString() {
        return "StateMapping{" +
                "oldNames=" + oldNames +
                ", newNames=" + newNames +
                ", typeMapping=" + typeMappings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateMapping that = (StateMapping) o;
        return Objects.equals(oldNames, that.oldNames) && Objects.equals(newNames, that.newNames) && Objects.equals(typeMappings, that.typeMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldNames, newNames, typeMappings);
    }

    /**
     * Apply the state mapping to given input states.
     *
     * @param inputStates  the input states provided which should be matched against.
     * @param outputStates the output map to put results into.
     */
    public void apply(Map<String, StateValue<?>> inputStates, Map<String, StateValue<?>> outputStates) {
        // Loop through each name and get the value
        // Skip remapping if not all fields present
        // If the name is empty, then it's a default, e.g. adding waterlogging by default
        // For defaults, we provide the empty value as that's what the type will use.
        List<StateValue<?>> oldValues = new ArrayList<>(oldNames.size());
        for (String name : oldNames) {
            StateValue<?> value = name.isEmpty() ? new StateValueString("") : inputStates.get(name);
            if (value == null) return; // Value not found meaning this mapping cannot be applied, return!

            // Otherwise add the value to the oldValues
            oldValues.add(value);
        }

        // If it contains a type, apply the type
        List<StateValue<?>> outputValues;
        if (typeMappings != null) {
            TypeMapping typeMapping = typeMappings.getInputsToOutputsLookup().get(oldValues);
            if (typeMapping == null) {
                return; // Skip as values were not found
            }

            // Apply mappings since they were found
            outputValues = typeMapping.getOutput();
        } else {
            outputValues = oldValues; // Use the old values since there is no type
        }

        // Apply the field mapping
        for (int i = 0; i < newNames.size(); i++) {
            String name = newNames.get(i);

            // If the name isn't empty (a default in inverse), then add it if it isn't present (as we use the first valid mapping for each name)
            if (!name.isEmpty()) {
                outputStates.putIfAbsent(name, outputValues.get(i));
            }
        }
    }

    /**
     * Serialize the state mapping as JSON.
     *
     * @param context the serialization context.
     * @return a json object representing the mapping.
     */
    public JsonElement serialize(JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("old_state", serializeStringList(getOldNames(), context));
        jsonObject.add("new_state", serializeStringList(getNewNames(), context));
        if (typeMappings != null) {
            jsonObject.addProperty("type", typeMappings.getName());
        }
        return jsonObject;
    }
}
