package com.hivemc.chunker.conversion.encoding.base.resolver.identifier;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Multimap;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifierType;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.OptionalInt;

/**
 * A mapping which turns an input identifier with optional data into a Chunker output with properties.
 */
public class ItemMapping {
    private final String identifier;
    private final ChunkerItemStackIdentifierType itemStackIdentifierType;
    private final NavigableMap<String, Object> states;
    private final NavigableMap<ComparableItemProperty<?>, Object> properties;

    /**
     * Create a new item mapping with states.
     *
     * @param identifier              the input identifier.
     * @param itemStackIdentifierType the output Chunker type (block / item).
     * @param inputStates             the input states, if empty it will be the default.
     * @param properties              the properties to set for the output (item properties / block states).
     */
    public ItemMapping(String identifier, ChunkerItemStackIdentifierType itemStackIdentifierType, NavigableMap<String, Object> inputStates, NavigableMap<ComparableItemProperty<?>, Object> properties) {
        this.identifier = identifier;
        this.itemStackIdentifierType = itemStackIdentifierType;
        states = inputStates;
        this.properties = properties;
    }

    /**
     * Create a new item mapping with data.
     *
     * @param identifier              the input identifier.
     * @param itemStackIdentifierType the output Chunker type (block / item).
     * @param data                    the input data value (or absent), if absent it will be fallen back to if there is a data value.
     * @param properties              the properties to set for the output (item properties / block states).
     */
    public ItemMapping(String identifier, ChunkerItemStackIdentifierType itemStackIdentifierType, OptionalInt data, NavigableMap<ComparableItemProperty<?>, Object> properties) {
        this.identifier = identifier;
        this.itemStackIdentifierType = itemStackIdentifierType;
        states = data.isEmpty() ? Collections.emptyNavigableMap() : ImmutableSortedMap.of("data", data.getAsInt());
        this.properties = properties;
    }

    /**
     * Create a mapping that maps an identifier with no data to an item type.
     *
     * @param identifier the input identifier.
     * @param itemType   the output identifier.
     * @return the newly created item mapping.
     */
    public static ItemMapping of(String identifier, ChunkerItemStackIdentifierType itemType) {
        return new ItemMapping(identifier, itemType, OptionalInt.empty(), Collections.emptyNavigableMap());
    }

    /**
     * Create a mapping that maps an identifier with states to an item type.
     *
     * @param identifier  the input identifier.
     * @param inputStates input block states.
     * @param itemType    the output identifier.
     * @return the newly created item mapping.
     */
    public static ItemMapping of(String identifier, Map<String, Object> inputStates, ChunkerItemStackIdentifierType itemType) {
        return new ItemMapping(identifier, itemType, ImmutableSortedMap.copyOf(inputStates), Collections.emptyNavigableMap());
    }

    /**
     * Create a mapping that maps an identifier with a data value to an item type.
     *
     * @param identifier the input identifier.
     * @param dataValue  the input data value.
     * @param itemType   the output identifier.
     * @return the newly created item mapping.
     */
    public static ItemMapping of(String identifier, int dataValue, ChunkerItemStackIdentifierType itemType) {
        return new ItemMapping(identifier, itemType, OptionalInt.of(dataValue), Collections.emptyNavigableMap());
    }

    /**
     * Create a mapping that maps an identifier with a data value to an item type with a property.
     *
     * @param identifier    the input identifier.
     * @param dataValue     the input data value.
     * @param itemType      the output identifier.
     * @param property      the property type to set (item property / block state).
     * @param propertyValue the value to set.
     * @param <P>           the type of the property.
     * @param <V>           the type of the value of the property.
     * @return the newly created item mapping.
     */
    public static <P extends ComparableItemProperty<? extends V>, V> ItemMapping of(String identifier, int dataValue, ChunkerItemStackIdentifierType itemType, P property, V propertyValue) {
        return new ItemMapping(identifier, itemType, OptionalInt.of(dataValue), ImmutableSortedMap.of(property, propertyValue));
    }

    /**
     * Create a mapping that maps an identifier with no data value to an item type with properties.
     *
     * @param identifier the input identifier.
     * @param itemType   the output identifier.
     * @param properties a map of properties (item property / block state).
     * @param <P>        the type of the property.
     * @param <V>        the type of the value of the property.
     * @return the newly created item mapping.
     */
    public static <P extends ComparableItemProperty<? extends V>, V> ItemMapping of(String identifier, ChunkerItemStackIdentifierType itemType, Map<P, V> properties) {
        return new ItemMapping(identifier, itemType, OptionalInt.empty(), ImmutableSortedMap.copyOf(properties));
    }

    /**
     * Create a mapping that maps an identifier with a data value to an item type with properties.
     *
     * @param identifier the input identifier.
     * @param itemType   the output identifier.
     * @param dataValue  the input data value.
     * @param properties a map of properties (item property / block state).
     * @param <P>        the type of the property.
     * @param <V>        the type of the value of the property.
     * @return the newly created item mapping.
     */
    public static <P extends ComparableItemProperty<? extends V>, V> ItemMapping of(String identifier, int dataValue, ChunkerItemStackIdentifierType itemType, Map<P, V> properties) {
        return new ItemMapping(identifier, itemType, OptionalInt.of(dataValue), ImmutableSortedMap.copyOf(properties));
    }

    /**
     * Create a mapping that maps an identifier with no data value to an item type with a property.
     *
     * @param identifier    the input identifier.
     * @param itemType      the output identifier.
     * @param property      the property type to set (item property / block state).
     * @param propertyValue the value to set.
     * @param <P>           the type of the property.
     * @param <V>           the type of the value of the property.
     * @return the newly created item mapping.
     */
    public static <P extends ComparableItemProperty<? extends V>, V> ItemMapping of(String identifier, ChunkerItemStackIdentifierType itemType, P property, V propertyValue) {
        return new ItemMapping(identifier, itemType, OptionalInt.empty(), ImmutableSortedMap.of(property, propertyValue));
    }

    /**
     * Group several mappings together using a map.
     *
     * @param types a map of the input identifier to output type.
     * @return the newly created item mappings.
     */
    public static ItemMapping[] group(Multimap<String, ? extends ChunkerItemStackIdentifierType> types) {
        ItemMapping[] mappings = new ItemMapping[types.size()];
        int i = 0;
        for (Map.Entry<String, ? extends ChunkerItemStackIdentifierType> entry : types.entries()) {
            mappings[i++] = new ItemMapping(entry.getKey(), entry.getValue(), OptionalInt.empty(), Collections.emptyNavigableMap());
        }
        return mappings;
    }

    /**
     * Flatten an identifier which has several data values to multiple output types.
     *
     * @param identifier     the input identifier.
     * @param dataToItemType a map of the data values to the output type.
     * @return the newly created item mappings.
     */
    public static ItemMapping[] flatten(String identifier, Multimap<Integer, ? extends ChunkerItemStackIdentifierType> dataToItemType) {
        ItemMapping[] mappings = new ItemMapping[dataToItemType.size()];
        int i = 0;
        for (Map.Entry<Integer, ? extends ChunkerItemStackIdentifierType> entry : dataToItemType.entries()) {
            mappings[i++] = new ItemMapping(identifier, entry.getValue(), OptionalInt.of(entry.getKey()), Collections.emptyNavigableMap());
        }
        return mappings;
    }

    /**
     * Flatten an identifier which has several data values to multiple output types.
     *
     * @param identifier     the input identifier.
     * @param dataToItemType a map of the data values to the output type.
     * @param property       the property type to set (item property / block state).
     * @param propertyValue  the value to set.
     * @param <P>            the type of the property.
     * @param <V>            the type of the value of the property.
     * @return the newly created item mappings.
     */
    public static <P extends ComparableItemProperty<? extends V>, V> ItemMapping[] flatten(String identifier, Multimap<Integer, ? extends ChunkerItemStackIdentifierType> dataToItemType, P property, V propertyValue) {
        ItemMapping[] mappings = new ItemMapping[dataToItemType.size()];
        int i = 0;
        NavigableMap<ComparableItemProperty<?>, Object> properties = ImmutableSortedMap.of(property, propertyValue);
        for (Map.Entry<Integer, ? extends ChunkerItemStackIdentifierType> entry : dataToItemType.entries()) {
            mappings[i++] = new ItemMapping(identifier, entry.getValue(), OptionalInt.of(entry.getKey()), properties);
        }
        return mappings;
    }

    /**
     * Get the input identifier.
     *
     * @return the input identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get the output Chunker type.
     *
     * @return the type to use for the item stack (item / block).
     */
    public ChunkerItemStackIdentifierType getItemStackIdentifierType() {
        return itemStackIdentifierType;
    }

    /**
     * Get the input states (using "data" for data values).
     *
     * @return a map of the input states that should be set / matched for this mapping.
     */
    public NavigableMap<String, Object> getStates() {
        return states;
    }

    /**
     * Get the output Chunker properties for the mapping.
     *
     * @return a map of the properties (item properties / block states) which should be matched / set.
     */
    public NavigableMap<ComparableItemProperty<?>, Object> getProperties() {
        return properties;
    }
}
