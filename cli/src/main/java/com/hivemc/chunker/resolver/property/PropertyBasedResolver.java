package com.hivemc.chunker.resolver.property;

import com.hivemc.chunker.resolver.Resolver;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Base implementation for resolving properties on a PropertyHolder.
 *
 * @param <R> the type of the resolvers which are passed to handlers.
 * @param <S> the type which is used as an input which properties are extracted from.
 * @param <H> the property holder to use (usually the intermediate format), that will store the properties.
 */
public abstract class PropertyBasedResolver<R, S, H extends PropertyHolder<? extends H>> implements Resolver<S, H> {
    private final Map<Property<H, ?>, HandlerWrapper<H, S>> propertyHandlers = new LinkedHashMap<>();
    protected final R resolvers;

    /**
     * Create a new property resolver, this registers all the property handlers.
     *
     * @param resolvers the resolvers to use.
     */
    public PropertyBasedResolver(R resolvers) {
        this.resolvers = resolvers;
        registerHandlers(resolvers);
    }

    @Override
    public Optional<H> to(S input) {
        Optional<H> optionalHolder = createPropertyHolder(input);
        if (optionalHolder.isPresent()) {
            H holder = optionalHolder.get();

            // Read properties
            for (HandlerWrapper<H, S> propertyHandlerWrapper : propertyHandlers.values()) {
                holder = propertyHandlerWrapper.readProperty(input, holder);
                if (holder == null) break; // Don't write further properties if the holder is now invalid
            }
            return Optional.ofNullable(holder);
        }
        return optionalHolder;
    }

    @Override
    public Optional<S> from(H input) {
        Optional<S> optionalOutput = createOutput(input);
        if (optionalOutput.isPresent()) {
            S output = optionalOutput.get();

            // Write properties
            for (HandlerWrapper<H, S> propertyHandlerWrapper : propertyHandlers.values()) {
                output = propertyHandlerWrapper.writeProperty(input, output);
                if (output == null) break; // Don't write further properties if the output is now invalid
            }
            return Optional.ofNullable(output);
        }
        return optionalOutput;
    }

    /**
     * Register a property handler to be used.
     *
     * @param property the property which should be handled.
     * @param handler  the handler to invoke for the property.
     * @param <V>      the type of the value which is read/written by the property.
     */
    protected <V> void registerHandler(Property<H, ? super V> property, PropertyHandler<S, V> handler) {
        propertyHandlers.put(property, new PropertyHandlerWrapper<>(property, handler));
    }

    /**
     * Register a property handler to be used that is handled with context of the holder.
     *
     * @param property the property which should be handled.
     * @param handler  the handler to invoke for the property.
     * @param <V>      the type of the value which is read/written by the property.
     */
    protected <V> void registerContextualHandler(Property<H, ? super V> property, PropertyHandler<Pair<H, S>, V> handler) {
        propertyHandlers.put(property, new ContextualHandlerWrapper<>(property, handler));
    }

    /**
     * Get the properties which this resolver can resolve.
     *
     * @return the supported properties based on the backing map.
     */
    public Set<Property<H, ?>> getSupportedProperties() {
        return propertyHandlers.keySet();
    }

    /**
     * Register all the property handlers which should be handled by this class.
     *
     * @param resolvers the resolvers to use.
     */
    protected abstract void registerHandlers(R resolvers);

    /**
     * Create a property holder based on an input. This method shouldn't read any properties but just construct the
     * property holder. This may be as simple as using the same identifier from the input.
     *
     * @param input a non-null input of the state input.
     * @return a property holder if it was possible, otherwise empty if it is not possible to make a property holder.
     */
    protected abstract Optional<H> createPropertyHolder(S input);

    /**
     * Create the output from the property holder. This method shouldn't read any properties but just construct the
     * property holder. This may be as simple as using the same identifier from the holder.
     *
     * @param input a non-null input of the property holder.
     * @return the state if it was possible, otherwise empty if it wasn't possible to make.
     */
    protected abstract Optional<S> createOutput(H input);

    /**
     * Wrapper class to handle reading and writing properties.
     *
     * @param <H> the type of the holder.
     * @param <S> the type of the state-based input/output format.
     */
    interface HandlerWrapper<H extends PropertyHolder<? extends H>, S> {
        /**
         * Read the current held property.
         *
         * @param input  the input state system.
         * @param holder the holder which is used as property storage.
         * @return the value of the holder, null if the holder is no longer valid and no further properties should be read.
         */
        @Nullable
        H readProperty(S input, H holder);

        /**
         * Write the current held property.
         *
         * @param holder the holder used as a storage of properties.
         * @param output the input state system value.
         * @return the value of the output, null if the output is no longer valid and no further properties should be read.
         */
        @Nullable
        S writeProperty(H holder, S output);
    }

    /**
     * Wrapper class to handle reading and writing properties with simple read/writing.
     *
     * @param property the property being read and written.
     * @param handler  the handler to call for reading and writing.
     * @param <H>      the type of the holder.
     * @param <S>      the type of the state-based input/output format.
     * @param <V>      the value being read/written by the property.
     */
    record PropertyHandlerWrapper<H extends PropertyHolder<? extends H>, S, V>(Property<? super H, ? super V> property,
                                                                               PropertyHandler<S, V> handler) implements HandlerWrapper<H, S> {
        @Override
        public H readProperty(S input, H holder) {
            Optional<V> value = handler().read(input);
            value.ifPresent(v -> holder.put(property, v));
            return holder; // Holder is still valid by default
        }

        @Override
        public S writeProperty(H holder, S output) {
            V value = holder.get(property());

            // Apply default write value
            if (value == null) {
                value = handler.getDefaultValue(output);
            }

            // Call the handler if the value wasn't null
            if (value != null) {
                handler().write(output, value);
            }
            return output; // Output is still valid by default
        }
    }

    /**
     * Wrapper class to handle reading and writing properties with context and reading/writing.
     *
     * @param property the property being read and written.
     * @param handler  the handler to call for reading and writing.
     * @param <H>      the type of the holder.
     * @param <S>      the type of the state-based input/output format.
     * @param <V>      the value being read/written by the property.
     */
    record ContextualHandlerWrapper<H extends PropertyHolder<? extends H>, S, V>(
            Property<? super H, ? super V> property,
            PropertyHandler<Pair<H, S>, V> handler) implements HandlerWrapper<H, S> {
        @Override
        public H readProperty(S input, H holder) {
            Pair<H, S> state = new ObjectObjectMutablePair<>(holder, input);

            // Read the value
            Optional<V> value = handler().read(state);
            if (state.left() != null) {
                value.ifPresent(v -> state.left().put(property, v));
            }

            // Return the new state
            return state.left();
        }

        @Override
        public S writeProperty(H holder, S output) {
            Pair<H, S> state = new ObjectObjectMutablePair<>(holder, output);
            V value = holder.get(property());

            // Apply default write value
            if (value == null) {
                value = handler.getDefaultValue(state);
            }

            // Call the handler if the value wasn't null and the state is set
            if (value != null && state.right() != null) {
                handler().write(state, value);
            }

            // Return the new state
            return state.right();
        }
    }
}
