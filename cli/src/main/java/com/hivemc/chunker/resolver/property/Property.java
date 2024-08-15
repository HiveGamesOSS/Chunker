package com.hivemc.chunker.resolver.property;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a property on a given holder. When a property is initialized it is registered in the ALL_PROPERTIES Map.
 * This implements comparable so it can be reliably sorted.
 *
 * @param <H> the class which holds the property.
 * @param <V> the type of the property.
 */
public abstract class Property<H extends PropertyHolder<?>, V> {
    private static final Map<Class<? extends Property<?, ?>>, Map<String, Property<?, ?>>> ALL_PROPERTIES = new Object2ObjectOpenHashMap<>();

    private final String name;
    private final Type type;

    /**
     * Create a new property and register it.
     *
     * @param name the name of the property (should be unique to the class).
     */
    @SuppressWarnings("unchecked")
    protected Property(String name, Type type) {
        this.name = name;
        this.type = type;

        Class<? extends Property<?, ?>> propertyType = (Class<? extends Property<?, ?>>) getClass();

        // Get property list
        Map<String, Property<?, ?>> list = ALL_PROPERTIES.computeIfAbsent(propertyType, (ignored) -> new Object2ObjectOpenHashMap<>());

        // Add property
        if (list.putIfAbsent(name, this) != null) {
            throw new IllegalArgumentException("Duplicate property named " + name);
        }
    }

    /**
     * Get all the properties defined by a specific property class (not including subclasses or super-classes).
     *
     * @param propertyClass the property class to lookup.
     * @param <P>           the type of the property.
     * @return all the properties found or a new empty map of the holder which will be updated, this allows the map
     * to be stored.
     */
    @SuppressWarnings("unchecked")
    public static <P extends Property<?, ?>> Map<String, P> getProperties(Class<P> propertyClass) {
        return (Map<String, P>) ALL_PROPERTIES.computeIfAbsent(propertyClass, (ignored) -> new Object2ObjectOpenHashMap<>());
    }

    /**
     * Get the name of the property, this should be unique to the property as it is used for comparison with the class.
     *
     * @return the string name of the property.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type held by the property.
     *
     * @return the class of the type held by the property.
     */
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property<?, ?> that = (Property<?, ?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, getClass().getName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                '}';
    }
}
