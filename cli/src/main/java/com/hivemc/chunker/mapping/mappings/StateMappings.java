package com.hivemc.chunker.mapping.mappings;

import com.google.gson.*;
import com.hivemc.chunker.mapping.identifier.states.StateValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A list of state mappings to iterate through to apply a state list.
 */
public class StateMappings {
    private final String name;
    private final List<StateMapping> mappings;

    /**
     * Create a new StateMapping list.
     *
     * @param name     the name of the list which is referenced by identifiers.
     * @param mappings the mappings which are used in the list to transform states where matched.
     */
    public StateMappings(String name, List<StateMapping> mappings) {
        this.name = name;
        this.mappings = mappings;
    }

    /**
     * Deserialize StateMappings from a JSON array.
     *
     * @param name        the name of the current state mappings.
     * @param types       the type lookup used for looking up types which are present in the state mapping.
     * @param jsonElement the element being deserialized that should be a json array.
     * @param context     the deserialization context.
     * @return the newly deserialized state mappings.
     * @throws JsonParseException if it failed to deserialize the input json.
     */
    public static StateMappings deserialize(String name, Map<String, TypeMappings> types, JsonElement jsonElement, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        ArrayList<StateMapping> list = new ArrayList<>(jsonArray.size());

        // Loop through each state mapping
        for (JsonElement element : jsonArray) {
            StateMapping mapping = StateMapping.deserialize(types, element, context);
            list.add(mapping);
        }
        return new StateMappings(name, list);
    }

    /**
     * Get the name of these state mappings.
     *
     * @return the name referred by the identifier mappings.
     */
    public String getName() {
        return name;
    }

    /**
     * The mappings included in this state mappings list.
     *
     * @return a list of the mappings.
     */
    public List<StateMapping> getMappings() {
        return mappings;
    }

    /**
     * Create an inverse copy of these StateMappings.
     *
     * @param inverseTypeMappingLookup the lookup used for replacing types with inverted types.
     * @return a copy where the all the state mappings have been inversed.
     */
    public StateMappings inverse(Function<TypeMappings, TypeMappings> inverseTypeMappingLookup) {
        List<StateMapping> inverseMappings = new ArrayList<>(mappings.size());
        for (StateMapping mapping : mappings) {
            inverseMappings.add(mapping.inverse(inverseTypeMappingLookup));
        }

        return new StateMappings(name, inverseMappings);
    }

    @Override
    public String toString() {
        return "StateMappings{" +
                "name='" + name + '\'' +
                ", mappings=" + mappings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateMappings that = (StateMappings) o;
        return Objects.equals(mappings, that.mappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mappings);
    }

    /**
     * Apply the state mappings to given input states.
     *
     * @param inputStates  the input states provided which should be matched against.
     * @param outputStates the output map to put results into.
     */
    public void apply(Map<String, StateValue<?>> inputStates, Map<String, StateValue<?>> outputStates) {
        // Loop through and apply the mappings
        for (StateMapping mapping : mappings) {
            mapping.apply(inputStates, outputStates);
        }
    }

    /**
     * Serialize the state mappings as JSON.
     *
     * @param context the serialization context.
     * @return a json array containing the mappings.
     */
    public JsonElement serialize(JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray(mappings.size());
        for (StateMapping mapping : mappings) {
            jsonArray.add(mapping.serialize(context));
        }
        return jsonArray;
    }
}
