package com.hivemc.chunker.mapping.mappings;

import com.google.gson.*;
import com.hivemc.chunker.mapping.identifier.states.StateValue;

import java.util.List;
import java.util.Objects;

/**
 * A single mapping of a list of inputs to a list of outputs, this is usually just a single input to a single output.
 * e.g. red -> 0
 */
public class TypeMapping {
    private final int index;
    private final List<StateValue<?>> input;
    private final List<StateValue<?>> output;

    /**
     * Create a new type mapping.
     *
     * @param index  the index of the type mapping in the parent array.
     * @param input  the input values required to match this mapping.
     * @param output the output values set if this mapping is matched.
     */
    public TypeMapping(int index, List<StateValue<?>> input, List<StateValue<?>> output) {
        this.index = index;
        this.input = input;
        this.output = output;
    }

    /**
     * Deserialize a type mapping from json.
     *
     * @param index       the index in the parent array this entry is.
     * @param jsonElement the json element to be decoded.
     * @param context     the current deserialization context.
     * @return a new type mapping based on the json.
     * @throws JsonParseException if it failed to parse the entry.
     */
    public static TypeMapping deserialize(int index, JsonElement jsonElement, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        // Each entry has a list or single value of inputs to outputs
        List<StateValue<?>> input = StateValue.Adapter.deserializeStateValues(object.get("input"), context);
        List<StateValue<?>> output = StateValue.Adapter.deserializeStateValues(object.get("output"), context);

        return new TypeMapping(index, input, output);
    }

    /**
     * Get the index of this item in the TypeMappings, this is used to ensure order is retained when rebuilding the
     * list (when inverse is called).
     *
     * @return the array index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the inputs required to apply this type mapping, the input can be an empty string new StateValueString("") if
     * the state mapping is using an empty name (indicating this is a default value).
     *
     * @return a list of state values.
     */
    public List<StateValue<?>> getInput() {
        return input;
    }

    /**
     * Get the outputs to be set if this type mapping applies, the output can be an empty string
     * new StateValueString("") if the state mapping is using an empty name for output (indicating this is a default
     * value when inversed).
     *
     * @return a list of state values.
     */
    public List<StateValue<?>> getOutput() {
        return output;
    }

    /**
     * Create an inverse copy of this TypeMapping.
     *
     * @return a copy where the output is the input and input is output.
     */
    public TypeMapping inverse() {
        return new TypeMapping(
                index,
                output,
                input
        );
    }

    @Override
    public String toString() {
        return "TypeMapping{" +
                "index=" + index +
                ", input=" + input +
                ", output=" + output +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeMapping that = (TypeMapping) o;
        return index == that.index && Objects.equals(input, that.input) && Objects.equals(output, that.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, input, output);
    }

    /**
     * Serialize the current type mapping into json.
     *
     * @param context the current context for serialization.
     * @return a json object representation of the type mapping.
     */
    public JsonElement serialize(JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("input", StateValue.Adapter.serializeStateValues(getInput(), context));
        jsonObject.add("output", StateValue.Adapter.serializeStateValues(getOutput(), context));
        return jsonObject;
    }
}
