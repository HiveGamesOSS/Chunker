package com.hivemc.chunker.mapping;

import com.google.gson.*;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.mappings.IdentifierMapping;
import com.hivemc.chunker.mapping.mappings.IdentifierMappings;
import com.hivemc.chunker.mapping.mappings.StateMappings;
import com.hivemc.chunker.mapping.mappings.TypeMappings;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/**
 * Represents a JSON formatted file which contains mappings that can turn item/block identifiers into another.
 * There are three main components:
 * - IdentifierMappings, these turn an identifier into another and can introduce or filter based on old_state_values/new_state_values.
 * - StateMappings, these are groups of states commonly used together for example you could group all stairs together.
 * - Types, these are a mapping of input -> output.
 * <p>
 * Example which changes wool from orange to red:
 * <pre>
 * {
 *     "identifiers": [
 *         {
 *             "old_identifier": "minecraft:wool",
 *             "new_identifier": "minecraft:wool",
 *             "state_list": "wool"
 *         }
 *     ],
 *     "state_lists": {
 *         "wool": [
 *             {
 *                 "old_state": "color",
 *                 "new_state": "color",
 *                 "type": "color_changer"
 *             }
 *         ]
 *     },
 *     "types": {
 *         "color_changer": [
 *             {
 *                 "input": "orange",
 *                 "output": "red"
 *             }
 *         ]
 *     }
 * }
 * </pre>
 */
public class MappingsFile {
    public static final String CUSTOM_BLOCK_HANDLER = "$custom_block";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(MappingsFile.class, new MappingsFile.Adapter())
            .registerTypeHierarchyAdapter(StateValue.class, new StateValue.Adapter())
            .setPrettyPrinting()
            .create();

    private final Map<String, TypeMappings> typeMappings;
    private final Map<String, StateMappings> stateMappings;
    private final Map<String, IdentifierMappings> blockIdentifierLookup;
    private final Map<String, IdentifierMappings> itemIdentifierLookup;

    private MappingsFile(Map<String, TypeMappings> typeMappings, Map<String, StateMappings> stateMappings, Map<String, IdentifierMappings> blockIdentifierLookup, Map<String, IdentifierMappings> itemIdentifierLookup) {
        this.typeMappings = typeMappings;
        this.stateMappings = stateMappings;
        this.blockIdentifierLookup = blockIdentifierLookup;
        this.itemIdentifierLookup = itemIdentifierLookup;
    }

    /**
     * Load a MappingsFile from a json file.
     *
     * @param file the input json file in the format of a mappings file.
     * @return the loaded MappingsFile, throwing JsonParseException if something went wrong.
     */
    public static MappingsFile load(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            return GSON.fromJson(fileReader, MappingsFile.class);
        }
    }

    /**
     * Load a MappingsFile from a json string.
     *
     * @param json the input json in the format of a mappings file.
     * @return the loaded MappingsFile, throwing JsonParseException if something went wrong.
     */
    public static MappingsFile load(String json) {
        return GSON.fromJson(json, MappingsFile.class);
    }

    /**
     * Load a MappingsFile from a JsonElement.
     *
     * @param jsonElement the input element in the format of a mappings file.
     * @return the loaded MappingsFile, throwing JsonParseException if something went wrong.
     */
    public static MappingsFile load(JsonElement jsonElement) {
        return GSON.fromJson(jsonElement, MappingsFile.class);
    }

    /**
     * Check whether a state is marked as special, special states are used to hold additional data which should
     * persist through conversion.
     *
     * @param stateName the state name.
     * @return true if the state begins with meta: or virtual:
     */
    public static boolean isSpecialState(String stateName) {
        // Meta is used to attach additional metadata to a state
        // Virtual is used to provide state data which does not have parity and is inferred by Chunker
        return stateName.startsWith("meta:") || stateName.startsWith("virtual:");
    }

    /**
     * Get the type mappings lookup used for converting types.
     *
     * @return the raw map used.
     */
    public Map<String, TypeMappings> getTypeMappings() {
        return typeMappings;
    }

    /**
     * Get the state mappings lookup used for converting different groupings of states.
     *
     * @return the raw map used.
     */
    public Map<String, StateMappings> getStateMappings() {
        return stateMappings;
    }

    /**
     * Get the block lookup used for converting block identifiers.
     *
     * @return the raw map used.
     */
    public Map<String, IdentifierMappings> getBlockIdentifierLookup() {
        return blockIdentifierLookup;
    }

    /**
     * Get the item lookup used for converting item identifiers.
     *
     * @return the raw map used.
     */
    public Map<String, IdentifierMappings> getItemIdentifierLookup() {
        return itemIdentifierLookup;
    }

    /**
     * Convert an input block identifier using this mappings file.
     *
     * @param inputBlock the input block identifier.
     * @return empty if there is no mapping found otherwise an output identifier based on the mappings.
     */
    public Optional<Identifier> convertBlock(Identifier inputBlock) {
        return convert(blockIdentifierLookup, inputBlock);
    }

    /**
     * Convert an input item identifier using this mappings file.
     *
     * @param inputItem the input item identifier.
     * @return empty if there is no mapping found otherwise an output identifier based on the mappings.
     */
    public Optional<Identifier> convertItem(Identifier inputItem) {
        Optional<Identifier> itemResult = convert(itemIdentifierLookup, inputItem);

        // If the result is present return it
        if (itemResult.isPresent()) {
            return itemResult;
        }

        // Fallback to blocks
        return convertBlock(inputItem);
    }

    /**
     * Convert an identifier using a specific identifierLookup.
     *
     * @param identifierLookup the lookup to use for the initial identifier conversion.
     * @param input            the input identifier to convert.
     * @return empty if there is no mapping found otherwise an output identifier based on the mappings.
     */
    protected Optional<Identifier> convert(Map<String, IdentifierMappings> identifierLookup, Identifier input) {
        Map<String, StateValue<?>> outputStates = new Object2ObjectOpenHashMap<>();

        // Lookup identifier
        IdentifierMappings identifierMappings = identifierLookup.get(input.getIdentifier());
        if (identifierMappings == null) {
            // Give it a second chance for custom blocks
            if (!input.isVanilla()) {
                // Lookup identifier
                identifierMappings = identifierLookup.get(CUSTOM_BLOCK_HANDLER);
                if (identifierMappings == null) {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        }

        // If it depends on the state values, calculate it otherwise pick first value
        IdentifierMapping identifierMapping = null;
        for (Map.Entry<Set<String>, Map<List<StateValue<?>>, IdentifierMapping>> entry : identifierMappings.getLookup().entrySet()) {
            // If the input doesn't have all the keys, skip it
            if (!input.getStates().keySet().containsAll(entry.getKey())) {
                continue;
            }

            // Collect the inputValues
            List<StateValue<?>> inputValues = new ArrayList<>(entry.getKey().size());
            for (String name : entry.getKey()) {
                inputValues.add(input.getStates().get(name));
            }

            // Apply the identifier mapping
            identifierMapping = entry.getValue().get(inputValues);
            if (identifierMapping != null) {
                break; // Found value
            }
        }

        // Check if not found
        if (identifierMapping == null) {
            return Optional.empty();
        }

        // Keep the old identifier if the new one is null / custom block
        String outputIdentifier;
        if (identifierMapping.getNewIdentifier() == null || identifierMapping.getNewIdentifier().equals(CUSTOM_BLOCK_HANDLER)) {
            outputIdentifier = input.getIdentifier();
        } else {
            outputIdentifier = identifierMapping.getNewIdentifier();
        }

        // If it contains a stateList, apply the stateList
        if (identifierMapping.getStateMapping() != null) {
            identifierMapping.getStateMapping().apply(input.getStates(), outputStates);
        } else {
            // Null means we pass through all the input states (*)
            outputStates.putAll(input.getStates());
        }

        // Apply any new state values (replace)
        outputStates.putAll(identifierMapping.getNewStateValues());

        // Apply any not set special states
        for (Map.Entry<String, StateValue<?>> entry : input.getStates().entrySet()) {
            if (isSpecialState(entry.getKey()) && !outputStates.containsKey(entry.getKey())) {
                outputStates.put(entry.getKey(), entry.getValue());
            }
        }

        return Optional.of(new Identifier(outputIdentifier, outputStates));
    }

    /**
     * Generate the inverse of the current mapping file, where the current file is A -> B, the result is B -> A.
     *
     * @return a copy of the current mappings inverted to allow backwards mapping.
     */
    public MappingsFile inverse() {
        // Inverse types first
        Map<String, TypeMappings> inverseTypeMappings = new Object2ObjectOpenHashMap<>(typeMappings.size());
        for (Map.Entry<String, TypeMappings> typeMapping : typeMappings.entrySet()) {
            inverseTypeMappings.put(typeMapping.getKey(), typeMapping.getValue().inverse());
        }

        Function<TypeMappings, TypeMappings> inverseTypeMappingLookup = (type) -> inverseTypeMappings.get(type.getName());

        // Then inverse state mappings
        Map<String, StateMappings> inverseStateMappings = new Object2ObjectOpenHashMap<>(stateMappings.size());
        for (Map.Entry<String, StateMappings> stateMapping : stateMappings.entrySet()) {
            inverseStateMappings.put(stateMapping.getKey(), stateMapping.getValue().inverse(inverseTypeMappingLookup));
        }

        Function<StateMappings, StateMappings> inverseStateMappingLookup = (type) -> inverseStateMappings.get(type.getName());

        // Create the mapping file and inverse the identifiers
        return new MappingsFile(
                inverseTypeMappings,
                inverseStateMappings,
                inverseIdentifiers(blockIdentifierLookup, inverseStateMappingLookup),
                inverseIdentifiers(itemIdentifierLookup, inverseStateMappingLookup)
        );
    }

    /**
     * Inverse a Map containing identifiers.
     *
     * @param identifierLookup          the identifier mapping to invert.
     * @param inverseStateMappingLookup a lookup function to convert the state mapping to an inverted copy.
     * @return a new map with all the identifiers inverted.
     */
    protected Map<String, IdentifierMappings> inverseIdentifiers(Map<String, IdentifierMappings> identifierLookup, Function<StateMappings, StateMappings> inverseStateMappingLookup) {
        Map<String, IdentifierMappings> inverseIdentifierLookup = new Object2ObjectOpenHashMap<>(identifierLookup.size());

        for (Map.Entry<String, IdentifierMappings> identifierMappings : identifierLookup.entrySet()) {
            // Invert lookup
            for (Map.Entry<Set<String>, Map<List<StateValue<?>>, IdentifierMapping>> identifierMapping : identifierMappings.getValue().getLookup().entrySet()) {
                for (Map.Entry<List<StateValue<?>>, IdentifierMapping> mapping : identifierMapping.getValue().entrySet()) {
                    inverseIdentifier(
                            inverseIdentifierLookup,
                            mapping.getValue(),
                            inverseStateMappingLookup
                    );
                }
            }

            // Invert duplicates
            for (IdentifierMapping identifierMapping : identifierMappings.getValue().getRedundant()) {
                inverseIdentifier(
                        inverseIdentifierLookup,
                        identifierMapping,
                        inverseStateMappingLookup
                );
            }
        }

        return inverseIdentifierLookup;
    }

    /**
     * Invert a single identifier
     *
     * @param outputIdentifierLookup    the output identifier lookup to put results into.
     * @param oldIdentifierMapping      the old mapping that needs to be inverted.
     * @param inverseStateMappingLookup a lookup function to convert the state mapping to an inverted copy.
     */
    protected void inverseIdentifier(Map<String, IdentifierMappings> outputIdentifierLookup, IdentifierMapping oldIdentifierMapping, Function<StateMappings, StateMappings> inverseStateMappingLookup) {
        // Find the inverse state mappings
        StateMappings stateMapping;
        if (oldIdentifierMapping.getStateMapping() == null || oldIdentifierMapping.getStateMapping().getMappings().isEmpty()) {
            stateMapping = oldIdentifierMapping.getStateMapping();
        } else {
            stateMapping = inverseStateMappingLookup.apply(oldIdentifierMapping.getStateMapping());
        }

        // Create a new IdentifierMapping
        IdentifierMapping identifierMapping = new IdentifierMapping(
                oldIdentifierMapping.getIndex(),
                oldIdentifierMapping.getNewIdentifier(),
                oldIdentifierMapping.getOldIdentifier(),
                oldIdentifierMapping.getNewStateValues(),
                oldIdentifierMapping.getOldStateValues(),
                stateMapping
        );

        // Now we need to either create or get the IdentifierMappings that hold this identifier
        IdentifierMappings identifierMappings = outputIdentifierLookup.computeIfAbsent(
                identifierMapping.getOldIdentifier(),
                (ignored) -> new IdentifierMappings(identifierMapping.getOldIdentifier())
        );
        identifierMappings.addMapping(identifierMapping);
    }

    /**
     * Convert the MappingsFile into a json representation.
     *
     * @return the json element representation of these mappings.
     */
    public JsonElement toJson() {
        return GSON.toJsonTree(this);
    }

    /**
     * Convert the MappingsFile into a json representation.
     *
     * @return the json string representation (pretty-printed) of these mappings.
     */
    public String toJsonString() {
        return GSON.toJson(this);
    }

    @Override
    public String toString() {
        return "BlockStateMappings{" +
                "typeMappings=" + typeMappings +
                ", stateMappings=" + stateMappings +
                ", blockIdentifierLookup=" + blockIdentifierLookup +
                ", itemIdentifierLookup=" + itemIdentifierLookup +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingsFile that = (MappingsFile) o;
        return Objects.equals(typeMappings, that.typeMappings) && Objects.equals(stateMappings, that.stateMappings) && Objects.equals(blockIdentifierLookup, that.blockIdentifierLookup) && Objects.equals(itemIdentifierLookup, that.itemIdentifierLookup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeMappings, stateMappings, blockIdentifierLookup, itemIdentifierLookup);
    }

    /**
     * Adapter used to turn MappingsFile into JSON and into an object.
     */
    static class Adapter implements JsonSerializer<MappingsFile>, JsonDeserializer<MappingsFile> {
        @Override
        public MappingsFile deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();

            // Deserialize types
            Map<String, TypeMappings> types;
            if (object.has("types")) {
                JsonObject typesObject = object.get("types").getAsJsonObject();
                types = new Object2ObjectOpenHashMap<>(typesObject.size());
                for (Map.Entry<String, JsonElement> entry : typesObject.entrySet()) {
                    TypeMappings typeMappings = TypeMappings.deserialize(entry.getKey(), entry.getValue(), context);

                    // Add to the lookup
                    types.put(entry.getKey(), typeMappings);
                }
            } else {
                types = Collections.emptyMap();
            }

            // Deserialize state_lists
            Map<String, StateMappings> stateMappingsLookup;
            if (object.has("state_lists")) {
                JsonObject stateLists = object.get("state_lists").getAsJsonObject();
                stateMappingsLookup = new Object2ObjectOpenHashMap<>(stateLists.size());
                for (Map.Entry<String, JsonElement> entry : stateLists.entrySet()) {
                    StateMappings stateMappings = StateMappings.deserialize(entry.getKey(), types, entry.getValue(), context);

                    // Add to the lookup
                    stateMappingsLookup.put(entry.getKey(), stateMappings);
                }
            } else {
                stateMappingsLookup = Collections.emptyMap();
            }

            // Deserialize blocks
            Map<String, IdentifierMappings> blockIdentifierMappings;
            if (object.has("identifiers")) {
                blockIdentifierMappings = deserializeIdentifiers(stateMappingsLookup, object.get("identifiers"), context);
            } else {
                blockIdentifierMappings = Collections.emptyMap();
            }

            // Deserialize items
            Map<String, IdentifierMappings> itemIdentifierMappings;
            if (object.has("items")) {
                itemIdentifierMappings = deserializeIdentifiers(stateMappingsLookup, object.get("items"), context);
            } else {
                itemIdentifierMappings = Collections.emptyMap();
            }

            // Return the new mappings
            return new MappingsFile(
                    types,
                    stateMappingsLookup,
                    blockIdentifierMappings,
                    itemIdentifierMappings
            );
        }

        /**
         * Method to deserialize a JsonObject with identifiers.
         *
         * @param stateMappings the deserialized state mappings.
         * @param jsonElement   the input JsonObject.
         * @param context       the deserialization context.
         * @return a map with deserialized mappings.
         * @throws JsonParseException if something went wrong deserializing the identifiers.
         */
        protected Map<String, IdentifierMappings> deserializeIdentifiers(Map<String, StateMappings> stateMappings, JsonElement jsonElement, JsonDeserializationContext context) throws JsonParseException {
            Map<String, IdentifierMappings> identifierLookup = new Object2ObjectOpenHashMap<>();

            // Loop through each entry
            int index = 0;
            for (JsonElement entry : jsonElement.getAsJsonArray()) {
                // Parse the identifier
                IdentifierMapping identifierMapping = IdentifierMapping.deserialize(stateMappings, index++, entry, context);

                // Now we need to either create or get the IdentifierMappings that hold this identifier
                IdentifierMappings identifierMappings = identifierLookup.computeIfAbsent(
                        identifierMapping.getOldIdentifier(),
                        (ignored) -> new IdentifierMappings(identifierMapping.getOldIdentifier())
                );
                identifierMappings.addMapping(identifierMapping);
            }

            return identifierLookup;
        }

        @Override
        public JsonElement serialize(MappingsFile mappingsFile, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            // Serialize blocks
            if (!mappingsFile.getBlockIdentifierLookup().isEmpty()) {
                object.add("identifiers", serializeIdentifiers(mappingsFile.getBlockIdentifierLookup(), context));
            }

            // Serialize items
            if (!mappingsFile.getItemIdentifierLookup().isEmpty()) {
                object.add("items", serializeIdentifiers(mappingsFile.getItemIdentifierLookup(), context));
            }

            // Seserialize state_lists
            if (!mappingsFile.getStateMappings().isEmpty()) {
                JsonObject stateLists = new JsonObject();
                for (Map.Entry<String, StateMappings> entry : mappingsFile.getStateMappings().entrySet()) {
                    stateLists.add(entry.getKey(), entry.getValue().serialize(context));
                }
                object.add("state_lists", stateLists);
            }

            // Seerialize types
            if (!mappingsFile.getTypeMappings().isEmpty()) {
                JsonObject stateLists = new JsonObject();
                for (Map.Entry<String, TypeMappings> entry : mappingsFile.getTypeMappings().entrySet()) {
                    stateLists.add(entry.getKey(), entry.getValue().serialize(context));
                }
                object.add("types", stateLists);
            }

            return object;
        }


        /**
         * Method to serialize an identifier lookup into a JsonObject.
         *
         * @param identifierLookup the identifier lookup to serialize.
         * @param context          the serialization context.
         * @return the resulting object with all the identifiers.
         */
        protected JsonElement serializeIdentifiers(Map<String, IdentifierMappings> identifierLookup, JsonSerializationContext context) {
            // Add all the identifier mappings
            List<IdentifierMapping> fullIdentifierMappings = new ArrayList<>();
            for (IdentifierMappings identifierMappings : identifierLookup.values()) {
                for (Map<List<StateValue<?>>, IdentifierMapping> lookup : identifierMappings.getLookup().values()) {
                    fullIdentifierMappings.addAll(lookup.values());
                }
                fullIdentifierMappings.addAll(identifierMappings.getRedundant());
            }

            // Sort them
            fullIdentifierMappings.sort(Comparator.comparing(IdentifierMapping::getIndex));

            // Serialize
            JsonArray jsonArray = new JsonArray(fullIdentifierMappings.size());
            for (IdentifierMapping identifierMapping : fullIdentifierMappings) {
                jsonArray.add(identifierMapping.serialize(context));
            }

            return jsonArray;
        }
    }
}
