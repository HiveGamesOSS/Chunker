package com.hivemc.chunker.mapping.mappings;

import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.util.CollectionComparator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.*;

/**
 * A lookup for identifier mappings with the same input, used to determine the exact mapping to use.
 */
public class IdentifierMappings {
    public static final Comparator<Set<String>> SET_COMPARATOR = new CollectionComparator<Set<String>, String>().reversed();
    private final String identifier;
    private final SortedMap<Set<String>, Map<List<StateValue<?>>, IdentifierMapping>> lookup;
    private final List<IdentifierMapping> redundant;

    /**
     * Create a new identifier mappings.
     *
     * @param identifier the identifier which is being matched as an input.
     * @param lookup     the lookup used for matching the identifier mapping to use.
     * @param redundant  the redundant values that collide with values in the lookup.
     */
    public IdentifierMappings(String identifier, SortedMap<Set<String>, Map<List<StateValue<?>>, IdentifierMapping>> lookup, List<IdentifierMapping> redundant) {
        this.identifier = identifier;
        this.lookup = lookup;
        this.redundant = redundant;
    }

    /**
     * Create a new identifier mappings with empty collections.
     *
     * @param identifier the identifier being handled as an input for the identifier mappings.
     */
    public IdentifierMappings(String identifier) {
        this(identifier, new TreeMap<>(SET_COMPARATOR), new ArrayList<>(0));
    }

    /**
     * Get the identifier which is matched on input for these mappings.
     *
     * @return the identifier used (not null).
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get the lookup to use when trying to find a matching mapping, the state values are ordered in the same order as the
     * values in the set.
     *
     * @return the backing map.
     */
    public Map<Set<String>, Map<List<StateValue<?>>, IdentifierMapping>> getLookup() {
        return lookup;
    }

    /**
     * Get the mappings which are redundant because the input is the same as an existing mapping.
     *
     * @return the list of the identifier mappings.
     */
    public List<IdentifierMapping> getRedundant() {
        return redundant;
    }

    /**
     * Add a mapping to the identifier mappings (it should have the same old identifier).
     * If the inputs are the same as an existing identifier mapping it will be added to the redundant if the index is
     * greater.
     *
     * @param identifierMapping the identifier to add.
     */
    public void addMapping(IdentifierMapping identifierMapping) {
        Set<String> sortedKeys = identifierMapping.getOldStateValues().keySet();
        List<StateValue<?>> sortedValues = new ArrayList<>(identifierMapping.getOldStateValues().values());

        // Get or create the lookup for values
        Map<List<StateValue<?>>, IdentifierMapping> stateValuesLookup = getLookup().computeIfAbsent(sortedKeys, (ignored) -> new Object2ObjectOpenHashMap<>(1));

        IdentifierMapping existing = stateValuesLookup.putIfAbsent(sortedValues, identifierMapping);
        if (existing != null) {
            // Add to redundant if it's a duplicate (and the index is higher than the existing)
            if (existing.getIndex() <= identifierMapping.getIndex()) {
                getRedundant().add(identifierMapping);
            } else {
                // If the index is lower of the new value, we should replace it and add the existing to redundant
                stateValuesLookup.replace(sortedValues, existing, identifierMapping);
                getRedundant().add(existing);
            }
        }
    }

    @Override
    public String toString() {
        return "IdentifierMappings{" +
                "lookup=" + lookup +
                ", redundant=" + redundant +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifierMappings that = (IdentifierMappings) o;
        return Objects.equals(lookup, that.lookup) && Objects.equals(redundant, that.redundant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lookup, redundant);
    }
}
