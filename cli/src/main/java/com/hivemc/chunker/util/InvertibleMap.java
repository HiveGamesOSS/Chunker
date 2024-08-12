package com.hivemc.chunker.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Similar to Guava BiMap but allows directional duplicates, e.g. K -> V, V -> K, V1 -> K.
 * This implementation contains useful methods for adding to the map for mappings and is considered minimal.
 *
 * @param forward the map to used for forward lookups.
 * @param inverse the map to use for inverse lookups.
 * @param <K>     the key type.
 * @param <V>     the value type.
 */
public record InvertibleMap<K, V>(Map<K, V> forward, Map<V, K> inverse) {
    /**
     * Create a new enum key based invertible map.
     *
     * @param <K> the key type.
     * @param <V> the value type.
     * @return a new invertible map with an enum map for the forward map.
     */
    public static <K extends Enum<K>, V> InvertibleMap<K, V> enumKeys(Class<K> enumClass) {
        return new InvertibleMap<>(new EnumMap<>(enumClass), new HashMap<>());
    }

    /**
     * Create a new int value based invertible map.
     *
     * @param <K> the key type.
     * @return a new invertible map with an enum map for the forward map.
     */
    public static <K> InvertibleMap<K, Integer> intValues() {
        return new InvertibleMap<>(new Object2IntOpenHashMap<>(), new Int2ObjectOpenHashMap<>());
    }

    /**
     * Create a new hashmap based invertible map.
     *
     * @param <K> the key type.
     * @param <V> the value type.
     * @return a new invertible map.
     */
    public static <K, V> InvertibleMap<K, V> create() {
        return new InvertibleMap<>(new HashMap<>(), new HashMap<>());
    }

    /**
     * Put a key into both maps.
     *
     * @param key   the key to add.
     * @param value the value for the key.
     * @return the old value if one was replaced (may be a key or a value).
     */
    public Object put(K key, V value) {
        V originalForward = forward.put(key, value);
        K originalInverse = inverse.put(value, key);

        // Return the last value
        if (originalForward != null) return originalForward;
        return originalInverse;
    }
}
