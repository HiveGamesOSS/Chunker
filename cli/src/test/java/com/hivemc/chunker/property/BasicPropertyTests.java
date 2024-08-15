package com.hivemc.chunker.property;

import com.hivemc.chunker.resolver.property.Property;
import com.hivemc.chunker.resolver.property.PropertyHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for PropertyHolder to ensure all functionality is covered.
 */
public class BasicPropertyTests {
    @BeforeAll
    public static void setup() {
        // Ensure the fields have been initialized
        BasicProperty.TEST_INTEGER.toString();
    }

    @Test
    public void testPut() {
        PropertyHolder<?> holder = new PropertyHolder<>();
        assertNull(holder.put(BasicProperty.TEST_INTEGER, 1));
    }

    @Test
    public void testGet() {
        PropertyHolder<?> holder = new PropertyHolder<>();
        holder.put(BasicProperty.TEST_INTEGER, 2);
        holder.put(BasicProperty.TEST_STRING, "Hi");

        assertEquals(2, holder.get(BasicProperty.TEST_INTEGER));
    }

    @Test
    public void testGetDefault() {
        PropertyHolder<?> holder = new PropertyHolder<>();
        assertEquals(1, holder.get(BasicProperty.TEST_INTEGER, 1));
    }

    @Test
    public void testGetDefault2() {
        PropertyHolder<?> holder = new PropertyHolder<>();
        assertEquals("hi", holder.get(BasicProperty.TEST_STRING, "hi"));
    }

    @Test
    public void testRemove() {
        PropertyHolder<?> holder = new PropertyHolder<>();
        holder.put(BasicProperty.TEST_INTEGER, 2);

        assertEquals(2, holder.remove(BasicProperty.TEST_INTEGER));
        assertNull(holder.get(BasicProperty.TEST_INTEGER));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGetProperties() {
        Map<String, BasicProperty> properties = Property.getProperties(BasicProperty.class);
        assertEquals(2, properties.size());
        assertTrue(properties.containsValue(BasicProperty.TEST_INTEGER));
        assertTrue(properties.containsValue(BasicProperty.TEST_STRING));
    }

    @Test
    public void testPropertyName() {
        assertEquals("TestInteger", BasicProperty.TEST_INTEGER.getName());
    }

    @Test
    public void testPropertyEquality() {
        assertEquals(BasicProperty.TEST_INTEGER, BasicProperty.TEST_INTEGER);
        assertNotEquals(BasicProperty.TEST_STRING, BasicProperty.TEST_INTEGER);
    }

    @Test
    public void testPropertyType() {
        assertEquals(Integer.class, BasicProperty.TEST_INTEGER.getType());
        assertEquals(String.class, BasicProperty.TEST_STRING.getType());
    }

    @Test
    public void testPropertyDuplicate() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new BasicProperty<>("TestInteger", Integer.class));
    }

    static class BasicProperty<V> extends Property<PropertyHolder<?>, V> {
        public static final BasicProperty<Integer> TEST_INTEGER = new BasicProperty<>("TestInteger", Integer.class);
        public static final BasicProperty<String> TEST_STRING = new BasicProperty<>("TestString", String.class);

        protected BasicProperty(String name, Class<V> typeClass) {
            super(name, typeClass);
        }
    }
}
