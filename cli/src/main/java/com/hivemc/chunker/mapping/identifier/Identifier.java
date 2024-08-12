package com.hivemc.chunker.mapping.identifier;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.identifier.states.StateValueInt;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * Represents an identifier with states attached, for example:
 * - minecraft:stone
 * - minecraft:stone[type=granite]
 * - minecraft:stone_sword[data=5]
 * - my_mod:wool[colored=true]
 */
public class Identifier {
    private final String identifier;
    private final Map<String, StateValue<?>> states;

    /**
     * Create a new identifier with states.
     *
     * @param identifier the identifier in the format `namespace:key` / `key`.
     * @param states     the states to use.
     */

    public Identifier(String identifier, Map<String, StateValue<?>> states) {
        this.identifier = identifier;
        this.states = states;
    }

    /**
     * Create a new identifier with no states (not modifiable).
     *
     * @param identifier the identifier in the format `namespace:key` / `key`.
     */

    public Identifier(String identifier) {
        this(identifier, Collections.emptyMap());
    }

    /**
     * Create a new identifier from a string identifier and a boxed map.
     *
     * @param identifier the identifier in the format `namespace:key` / `key`.
     * @param boxedMap   the boxed map which should be transformed into state values.
     * @return a new identifier with a new hash map with remapped values.
     */
    public static Identifier fromBoxed(String identifier, Map<String, Object> boxedMap) {
        Map<String, StateValue<?>> newMap = new Object2ObjectOpenHashMap<>(boxedMap.size());
        for (Map.Entry<String, Object> entry : boxedMap.entrySet()) {
            newMap.put(entry.getKey(), StateValue.fromBoxed(entry.getValue()));
        }
        return new Identifier(identifier, newMap);
    }

    /**
     * Create a new identifier from a string identifier and a data value.
     *
     * @param identifier   the identifier in the format `namespace:key` / `key`.
     * @param optionalData an optional data value which sets the "data" property of the identifier.
     * @return a new identifier with the value field set if it was present.
     */
    public static Identifier fromData(String identifier, OptionalInt optionalData) {
        if (optionalData.isEmpty()) {
            return new Identifier(identifier);
        }
        return new Identifier(identifier, Map.of("data", new StateValueInt(optionalData.getAsInt())));
    }

    /**
     * Get the key (and namespace if present) used for this identifier.
     *
     * @return the raw value, e.g. minecraft:stone, stone.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get the states for the current identifier.
     *
     * @return the backing map with states (allows modification depending on input map).
     */
    public Map<String, StateValue<?>> getStates() {
        return states;
    }

    /**
     * Get the states for the current identifier.
     *
     * @return a view of getStates which unboxes the values.
     */
    public Map<String, Object> getBoxedStates() {
        return Maps.transformValues(states, StateValue::getBoxed);
    }

    /**
     * Attempt to get the underlying data value of this identifier, this only works if the identifier has a "data" state
     * set to a StateValueInt. This is used for legacy data / items.
     *
     * @return the int if it is present otherwise absent if the data isn't an int or if it's not present.
     */
    public OptionalInt getDataValue() {
        StateValue<?> data = states.get("data");
        if (!(data instanceof StateValueInt stateValueInt)) return OptionalInt.empty();
        return OptionalInt.of(stateValueInt.getValue());
    }

    /**
     * Return whether this identifier is in the Minecraft namespace.
     *
     * @return true if the value starts with minecraft: or has no namespace specified.
     */
    public boolean isVanilla() {
        return !identifier.contains(":") || identifier.startsWith("minecraft:");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(identifier, that.identifier) && Objects.equals(states, that.states);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, states);
    }

    @Override
    public String toString() {
        return identifier + (states.isEmpty() ? "" : toStateString());
    }

    /**
     * Get the states for this identifier serialized in the form [x=y].
     *
     * @return the serialized states, will be [] if no states are present.
     */
    public String toStateString() {
        return (states.isEmpty() ? "[]" : "[" + Joiner.on(",").withKeyValueSeparator("=").join(states) + "]");
    }
}
