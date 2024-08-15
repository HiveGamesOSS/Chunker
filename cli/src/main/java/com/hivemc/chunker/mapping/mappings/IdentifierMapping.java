package com.hivemc.chunker.mapping.mappings;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hivemc.chunker.mapping.identifier.states.StateValue;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;

/**
 * A mapping of a single identifier and an output identifier, optionally providing required states and output states. It
 * can also provide StateMappings to be used when applying the mapping to transform the states.
 */
public class IdentifierMapping {
    private static final Type STATE_VALUE_MAP_TYPE = new TypeToken<SortedMap<String, StateValue<?>>>() {
    }.getType();

    private final int index;
    private final String oldIdentifier;
    private final String newIdentifier;
    private final SortedMap<String, StateValue<?>> oldStateValues;
    private final SortedMap<String, StateValue<?>> newStateValues;
    private final StateMappings stateMapping;

    /**
     * Create a new identifier mapping.
     *
     * @param index          the index of this mapping in the parent array.
     * @param oldIdentifier  the old identifier to match against.
     * @param newIdentifier  the new identifier to use (can be null if the same as the old identifier).
     * @param oldStateValues the state values to match against.
     * @param newStateValues the new states to use if this state is matched.
     * @param stateMapping   the state mapping to use if this mapping is matched, if null it passes through all input
     *                       states.
     */
    public IdentifierMapping(int index, String oldIdentifier, String newIdentifier, SortedMap<String, StateValue<?>> oldStateValues, SortedMap<String, StateValue<?>> newStateValues, StateMappings stateMapping) {
        this.index = index;
        this.oldIdentifier = oldIdentifier;
        this.newIdentifier = newIdentifier;
        this.oldStateValues = oldStateValues;
        this.newStateValues = newStateValues;
        this.stateMapping = stateMapping;
    }

    /**
     * Deserialize a identifier mapping.
     *
     * @param stateMappings the state mappings used to look up the state_list in the mapping.
     * @param index         the index of this entry in the parent array.
     * @param jsonElement   the element to deserialize.
     * @param context       the deserialization context.
     * @return a new identifier mapping based on the JSON.
     * @throws JsonParseException if it failed to deserialize the identifier mapping.
     */
    public static IdentifierMapping deserialize(Map<String, StateMappings> stateMappings, int index, JsonElement jsonElement, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        String oldIdentifier = object.get("old_identifier").getAsString();
        String newIdentifier = object.has("new_identifier") ? object.get("new_identifier").getAsString() : oldIdentifier;
        SortedMap<String, StateValue<?>> oldStateValues = object.has("old_state_values") ? context.deserialize(object.get("old_state_values"), STATE_VALUE_MAP_TYPE) : Collections.emptySortedMap();
        SortedMap<String, StateValue<?>> newStateValues = object.has("new_state_values") ? context.deserialize(object.get("new_state_values"), STATE_VALUE_MAP_TYPE) : Collections.emptySortedMap();

        // Find the state_list to use
        String stateListName = object.has("state_list") ? object.get("state_list").getAsString() : null;
        StateMappings stateMapping;
        if (stateListName == null || stateListName.isEmpty()) {
            // Empty value means we should pass through no states, so use an empty state mapping
            stateMapping = new StateMappings("", Collections.emptyList());
        } else if (stateListName.equals("*")) {
            // Special-case to indicate to passthrough all the states (we use null to indicate this)
            stateMapping = null;
        } else {
            stateMapping = stateMappings.get(stateListName);
            if (stateMapping == null) {
                throw new JsonParseException("Could not find state_list " + stateListName);
            }
        }

        // Create the identifier mapping for this entry
        return new IdentifierMapping(
                index,
                oldIdentifier,
                newIdentifier,
                oldStateValues,
                newStateValues,
                stateMapping
        );
    }

    /**
     * The index of this identifier mapping in the full list.
     *
     * @return the array index in the parent identifier list.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the identifier that this mapping should become.
     *
     * @return the identifier, can be null to indicate to use the old identifier.
     */
    public String getNewIdentifier() {
        return newIdentifier;
    }

    /**
     * Get new states that should be added to the mapping after state mapping has been completed.
     *
     * @return a sorted map of the values, sorting ensures consistency when looking for values.
     */
    public SortedMap<String, StateValue<?>> getNewStateValues() {
        return newStateValues;
    }

    /**
     * The state mapping to use for this state.
     *
     * @return the mapping, can be null indicating for all states to be passed through.
     */
    public StateMappings getStateMapping() {
        return stateMapping;
    }

    /**
     * Get the identifier to be matched for this mapping.
     *
     * @return the identifier, should never be null.
     */
    public String getOldIdentifier() {
        return oldIdentifier;
    }

    /**
     * Get states that should be matched for this identifier mapping.
     *
     * @return a sorted map of the values, sorting ensures consistency when looking for values.
     */
    public SortedMap<String, StateValue<?>> getOldStateValues() {
        return oldStateValues;
    }

    @Override
    public String toString() {
        return "IdentifierMapping{" +
                "index=" + index +
                ", oldIdentifier='" + oldIdentifier + '\'' +
                ", newIdentifier='" + newIdentifier + '\'' +
                ", oldStateValues=" + oldStateValues +
                ", newStateValues=" + newStateValues +
                ", stateMapping=" + stateMapping +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifierMapping that = (IdentifierMapping) o;
        return index == that.index && Objects.equals(oldIdentifier, that.oldIdentifier) && Objects.equals(newIdentifier, that.newIdentifier) && Objects.equals(oldStateValues, that.oldStateValues) && Objects.equals(newStateValues, that.newStateValues) && Objects.equals(stateMapping, that.stateMapping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, oldIdentifier, newIdentifier, oldStateValues, newStateValues, stateMapping);
    }

    /**
     * Serialize this identifier to JSON.
     *
     * @param context the serialization context to use.
     * @return a json object representation of this identifier.
     */
    public JsonElement serialize(JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("old_identifier", oldIdentifier);
        if (!oldIdentifier.equals(newIdentifier)) {
            jsonObject.addProperty("new_identifier", newIdentifier);
        }
        if (!oldStateValues.isEmpty()) {
            jsonObject.add("old_state_values", context.serialize(oldStateValues, STATE_VALUE_MAP_TYPE));
        }
        if (!newStateValues.isEmpty()) {
            jsonObject.add("new_state_values", context.serialize(newStateValues, STATE_VALUE_MAP_TYPE));
        }
        if (getStateMapping() != null) {
            jsonObject.addProperty("state_list", getStateMapping().getName());
        }
        return jsonObject;
    }
}
