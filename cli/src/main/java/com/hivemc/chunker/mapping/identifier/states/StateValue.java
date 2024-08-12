package com.hivemc.chunker.mapping.identifier.states;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.primitive.ByteTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents a value used for a state, e.g. minecraft:stone[test=1], in this example the StateValue is 1.
 */
public interface StateValue<T> {
    /**
     * Create a StateValue from a boxed input.
     *
     * @param input the input to convert, must be a String, Boolean or Integer or IllegalArgumentException will be thrown.
     * @param <T>   the type used for the underlying state value.
     * @return the value as a StateValue
     */
    @SuppressWarnings("unchecked")
    static <T> StateValue<T> fromBoxed(@NotNull T input) {
        if (input instanceof Boolean bool) {
            return (StateValue<T>) (bool ? StateValueBoolean.TRUE : StateValueBoolean.FALSE);
        } else if (input instanceof String string) {
            return (StateValue<T>) new StateValueString(string);
        } else if (input instanceof Number number) {
            return (StateValue<T>) new StateValueInt(number.intValue());
        }
        throw new IllegalArgumentException("Unable to unbox value of type " + input.getClass());
    }

    /**
     * Create a StateValue from a NBT tag.
     *
     * @param input the input to convert, must be a StringTag, ByteTag or IntTag or IllegalArgumentException will be thrown.
     * @param <T>   the type used for the underlying tag value.
     * @return the value as a StateValue
     */
    static <T> StateValue<?> fromNBT(@NotNull Tag<T> input) {
        if (input instanceof ByteTag bool) {
            return bool.getValue() == (byte) 1 ? StateValueBoolean.TRUE : StateValueBoolean.FALSE;
        } else if (input instanceof StringTag stringTag) {
            return new StateValueString(stringTag.getValue());
        } else if (input instanceof IntTag intTag) {
            return new StateValueInt(intTag.getValue());
        }
        throw new IllegalArgumentException("Unable to decode tag value of type " + input.getClass());
    }

    /**
     * Gets the underlying value held by this state value.
     *
     * @return the value being held.
     */
    T getBoxed();

    /**
     * Convert this tag value to NBT.
     *
     * @return the NBT tag equivalent of this state value.
     */
    Tag<?> toNBT();

    /**
     * Adapter used to turn a StateValue into JSON and into an object.
     */
    class Adapter implements JsonSerializer<StateValue<?>>, JsonDeserializer<StateValue<?>> {
        private static final Type STATE_VALUE_LIST_TYPE = new TypeToken<List<StateValue<?>>>() {
        }.getType();

        /**
         * Serialize a list of StateValues as either a single entry or an array depending on count.
         *
         * @param input   the list of state values (not null).
         * @param context the serialization context.
         * @return either a json array or a primitive value depending on the count.
         * @throws JsonParseException if it failed to serialize the values.
         */
        public static JsonElement serializeStateValues(List<StateValue<?>> input, JsonSerializationContext context) throws JsonParseException {
            if (input.size() == 1) {
                return context.serialize(input.iterator().next(), StateValue.class);
            } else {
                return context.serialize(input, STATE_VALUE_LIST_TYPE);
            }
        }

        /**
         * Deserialize a list of state values or a single state value as a list.
         *
         * @param jsonElement the input elements to decode either primitive or array value.
         * @param context     the deserialization context.
         * @return a list of the elements (can be a single item or multiple).
         * @throws JsonParseException if it failed to deserialize the values.
         */
        public static List<StateValue<?>> deserializeStateValues(JsonElement jsonElement, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement.isJsonArray()) {
                return context.deserialize(jsonElement, STATE_VALUE_LIST_TYPE);
            } else {
                return List.of((StateValue<?>) context.deserialize(jsonElement, StateValue.class));
            }
        }

        @Override
        public JsonElement serialize(StateValue stateValue, Type type, JsonSerializationContext context) {
            if (stateValue instanceof StateValueBoolean stateValueBoolean) {
                return new JsonPrimitive(stateValueBoolean.getValue());
            } else if (stateValue instanceof StateValueString stateValueString) {
                return new JsonPrimitive(stateValueString.getValue());
            } else if (stateValue instanceof StateValueInt stateValueInt) {
                return new JsonPrimitive(stateValueInt.getValue());
            } else {
                throw new IllegalArgumentException("Unable to serialize " + type);
            }
        }

        @Override
        public StateValue<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
                if (primitive.isBoolean()) {
                    return primitive.getAsBoolean() ? StateValueBoolean.TRUE : StateValueBoolean.FALSE;
                } else if (primitive.isString()) {
                    return new StateValueString(primitive.getAsString());
                } else if (primitive.isNumber()) {
                    return new StateValueInt(primitive.getAsInt());
                }
            }
            throw new JsonParseException("Unknown type for " + jsonElement);
        }
    }
}
