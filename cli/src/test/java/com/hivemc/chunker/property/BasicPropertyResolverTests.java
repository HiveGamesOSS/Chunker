package com.hivemc.chunker.property;

import com.hivemc.chunker.resolver.property.Property;
import com.hivemc.chunker.resolver.property.PropertyBasedResolver;
import com.hivemc.chunker.resolver.property.PropertyHandler;
import com.hivemc.chunker.resolver.property.PropertyHolder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure the functionality of PropertyResolver works for resolving different properties
 */
public class BasicPropertyResolverTests {
    @Test
    public void testResolveTo() {
        BasicPropertyResolver resolver = new BasicPropertyResolver();
        Optional<PropertyHolder<?>> result = resolver.to(new StringBuilder("5"));
        assertTrue(result.isPresent());
        assertEquals(5, result.get().get(BasicProperty.TEST_INTEGER));
    }

    @Test
    public void testResolveTo2() {
        BasicPropertyResolver resolver = new BasicPropertyResolver();
        Optional<PropertyHolder<?>> result = resolver.to(new StringBuilder()); // Should end up absent
        assertTrue(result.isPresent());
        assertNull(result.get().get(BasicProperty.TEST_INTEGER));
    }

    @Test
    public void testResolveFrom() {
        BasicPropertyResolver resolver = new BasicPropertyResolver();
        PropertyHolder<PropertyHolder<?>> input = new PropertyHolder<>();
        input.put(BasicProperty.TEST_INTEGER, 1);

        Optional<StringBuilder> result = resolver.from(input);
        assertTrue(result.isPresent());
        assertEquals("1", result.get().toString());
    }

    @Test
    public void testResolveFrom2() {
        BasicPropertyResolver resolver = new BasicPropertyResolver();
        PropertyHolder<PropertyHolder<?>> input = new PropertyHolder<>();

        Optional<StringBuilder> result = resolver.from(input);
        assertTrue(result.isPresent());
        assertEquals("", result.get().toString());
    }

    @Test
    public void testGetProperties() {
        BasicPropertyResolver resolver = new BasicPropertyResolver();
        assertEquals(1, resolver.getSupportedProperties().size());
        assertTrue(resolver.getSupportedProperties().contains(BasicProperty.TEST_INTEGER));
    }

    @Test
    public void testResolveFromEmpty() {
        EmptyPropertyResolver resolver = new EmptyPropertyResolver();
        PropertyHolder<PropertyHolder<?>> input = new PropertyHolder<>();
        input.put(BasicProperty.TEST_INTEGER, 1);

        Optional<String> result = resolver.from(input);
        assertFalse(result.isPresent());
    }

    @Test
    public void testResolveToEmpty() {
        EmptyPropertyResolver resolver = new EmptyPropertyResolver();

        Optional<PropertyHolder<?>> result = resolver.to("1");
        assertFalse(result.isPresent());
    }

    static class BasicProperty<V> extends Property<PropertyHolder<?>, V> {
        public static final BasicProperty<Integer> TEST_INTEGER = new BasicProperty<>("TestInteger", Integer.class);

        protected BasicProperty(String name, Class<V> typeClass) {
            super(name, typeClass);
        }
    }

    static class BasicPropertyResolver extends PropertyBasedResolver<Void, StringBuilder, PropertyHolder<?>> {
        public BasicPropertyResolver() {
            super(null);
        }

        @Override
        protected void registerHandlers(Void ignored) {
            registerHandler(BasicProperty.TEST_INTEGER, new PropertyHandler<>() {
                @Override
                public Optional<Integer> read(@NotNull StringBuilder state) {
                    try {
                        return Optional.of(Integer.parseInt(state.toString()));
                    } catch (NumberFormatException exception) {
                        return Optional.empty();
                    }
                }

                @Override
                public void write(@NotNull StringBuilder state, @NotNull Integer value) {
                    state.append(value);
                }
            });
        }

        @Override
        protected Optional<PropertyHolder<?>> createPropertyHolder(StringBuilder input) {
            return Optional.of(new PropertyHolder<>());
        }

        @Override
        protected Optional<StringBuilder> createOutput(PropertyHolder<?> input) {
            return Optional.of(new StringBuilder());
        }
    }

    static class EmptyPropertyResolver extends PropertyBasedResolver<Void, String, PropertyHolder<?>> {
        public EmptyPropertyResolver() {
            super(null);
        }

        @Override
        protected void registerHandlers(Void ignored) {
        }

        @Override
        protected Optional<PropertyHolder<?>> createPropertyHolder(String input) {
            return Optional.empty();
        }

        @Override
        protected Optional<String> createOutput(PropertyHolder<?> input) {
            return Optional.empty();
        }
    }
}
