package com.hivemc.chunker.conversion.encoding.base.resolver.identifier;

/**
 * Indicates a comparable item property that can be used in a ChunkerItemIdentifierResolver and also indicates it is
 * Comparable to itself and other comparable item properties.
 * <p>
 * When implementing this interface, you will need to ensure the implementation of ChunkerItemIdentifierResolver can
 * find the property.
 *
 * @param <V> the type for possible values of the property.
 */
public interface ComparableItemProperty<V> extends Comparable<ComparableItemProperty<?>> {
}
