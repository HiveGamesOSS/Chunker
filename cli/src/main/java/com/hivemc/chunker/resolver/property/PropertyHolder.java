package com.hivemc.chunker.resolver.property;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * A class which holds properties, this is usually extended for usage but can be used as a base class.
 *
 * @param <H> the type of the current property holder, this is used for ensuring only properties applicable to this
 *            holder can be stored (at a compile level).
 */
public class PropertyHolder<H extends PropertyHolder<?>> {
    protected final Map<Property<? super H, ?>, Object> properties;

    /**
     * Create the property holder with a pre-defined map.
     *
     * @param properties the property map.
     */
    public PropertyHolder(Map<Property<? super H, ?>, Object> properties) {
        this.properties = properties;
    }

    /**
     * Create the property holder with an empty backing map.
     */
    public PropertyHolder() {
        this(new Object2ObjectOpenHashMap<>());
    }

    /**
     * Get a property value from this property holder.
     *
     * @param property     the property type to get.
     * @param defaultValue the default value to use if the property is not present.
     * @param <P>          the type of the property.
     * @param <V>          the value type of the property.
     * @return the value currently set or the default value.
     */
    @SuppressWarnings("unchecked")
    public <P extends Property<? super H, ? super V>, V> V get(P property, V defaultValue) {
        Object value = properties.get(property);
        return value == null ? defaultValue : (V) value;
    }

    /**
     * Get a property value from this property holder.
     *
     * @param property the property type to get.
     * @param <P>      the type of the property.
     * @param <V>      the value type of the property.
     * @return the value currently set or null if there is no value set.
     */
    @Nullable
    public <P extends Property<? super H, ? super V>, V> V get(P property) {
        return get(property, null);
    }

    /**
     * Set a property value for this property holder.
     *
     * @param property the property type to put.
     * @param value    the value to put.
     * @param <P>      the type of the property.
     * @param <V>      the value type of the property.
     * @return the property value which was previously used or null if no property was removed.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <P extends Property<? super H, ? super V>, V> V put(P property, V value) {
        return (V) properties.put(property, value);
    }

    /**
     * Remove a property from this property holder.
     *
     * @param property the property type to remove.
     * @param <P>      the type of the property.
     * @param <V>      the value type of the property.
     * @return the property value which was removed or null if no property was removed.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <P extends Property<? super H, ? super V>, V> V remove(P property) {
        return (V) properties.remove(property);
    }

    /**
     * Get the backing properties map.
     *
     * @return the backing map.
     */
    public Map<Property<? super H, ?>, Object> getProperties() {
        return properties;
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyHolder<?> that)) return false;
        return Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    @Override
    public String toString() {
        return "PropertyHolder{" +
                "properties=" + properties +
                '}';
    }
}
