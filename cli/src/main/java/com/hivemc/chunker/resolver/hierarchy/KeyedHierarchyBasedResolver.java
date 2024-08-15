package com.hivemc.chunker.resolver.hierarchy;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.resolver.Resolver;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A resolver which uses class hierarchy to fill an output type. The handlers are processed in the order of the base
 * class to the parent.
 *
 * @param <R> the resolvers used by this format which are provided to this resolver.
 * @param <K> the key type used to identify the type.
 * @param <D> the data-type used for input/output.
 * @param <T> the output type which is produced (likely a Chunker type), which has subclasses to be handled by the type
 *            handlers.
 */
public abstract class KeyedHierarchyBasedResolver<R, K, D, T> implements Resolver<D, T> {
    protected final R resolvers;
    protected final Version version;
    protected final Map<K, TypeHandler<R, K, D, ? extends T>> keyLookup = new ConcurrentHashMap<>();
    protected final Map<Class<?>, TypeHandler<R, K, D, ? extends T>> classHandlerLookup = new ConcurrentHashMap<>();
    protected final Map<TypeHandler<R, K, D, ? extends T>, Collection<TypeHandler<R, K, D, ? extends T>>> classHierarchyLookupCache = new ConcurrentHashMap<>();

    /**
     * Create a new KeyedHierarchyBasedResolver.
     *
     * @param version   the format version being handled by this resolver.
     * @param resolvers the resolvers which should be provided as context to the type handlers.
     */
    public KeyedHierarchyBasedResolver(Version version, R resolvers) {
        this.resolvers = resolvers;
        this.version = version;

        // Call init
        init();

        // Register the types
        registerTypeHandlers(version);
    }

    /**
     * Called before registerTypeHandlers to ensure fields are initialized.
     */
    protected abstract void init();

    /**
     * Get the to-type handler for a specific input.
     *
     * @param input the data-type object.
     * @return the handler to use for the object.
     */
    protected Optional<TypeHandler<R, K, D, ? extends T>> getToTypeHandler(D input) {
        return getKey(input).map(keyLookup::get);
    }

    /**
     * Get the from-type handler for a specific input.
     *
     * @param input the output object.
     * @return the handler to use for the object.
     */
    protected Optional<TypeHandler<R, K, D, ? extends T>> getFromTypeHandler(T input) {
        return Optional.ofNullable(classHandlerLookup.get(input.getClass()));
    }

    /**
     * Get the type handler to use for an output type.
     *
     * @param input the class used for the output type.
     * @param <U>   the type this type handler should be.
     * @return the handler to use for the class.
     */
    @SuppressWarnings("unchecked")
    protected <U extends T> Optional<TypeHandler<R, K, D, U>> getFromClassTypeHandler(Class<U> input) {
        return Optional.ofNullable((TypeHandler<R, K, D, U>) classHandlerLookup.get(input));
    }

    /**
     * Construct the dataType to be used for a specific type handler.
     *
     * @param key the key to use for the data type.
     * @return a new type D which a property set to indicate its type.
     */
    @Nullable
    protected abstract D constructDataType(K key);

    /**
     * Register the types which are handled by this resolver, they should all be part of the class hierarchy for the type T.
     * Note: This is called on class initialization.
     *
     * @param version the version being used for this resolver.
     */
    protected abstract void registerTypeHandlers(Version version);

    /**
     * Register a type handler to handle a specific subclass.
     *
     * @param typeHandler the type handler to register, note if the handled class already exists it will be replaced.
     */
    protected void register(TypeHandler<R, K, D, ? extends T> typeHandler) {
        classHandlerLookup.put(typeHandler.getHandledClass(), typeHandler);

        // Register name lookup
        if (typeHandler.getKey() != null) {
            keyLookup.put(typeHandler.getKey(), typeHandler);
        }

        // Ensure cache is clear
        classHierarchyLookupCache.clear();
    }

    private Collection<TypeHandler<R, K, D, ? extends T>> generateTypeHandlerHierarchy(TypeHandler<R, K, D, ? extends T> lastHandler) {
        // Generate a list of handlers in the order of visiting the base class then going to the final class
        List<TypeHandler<R, K, D, ? extends T>> typeHandlers = new ArrayList<>();
        typeHandlers.add(lastHandler);

        // Loop through the class parents
        Class<?> parent = lastHandler.getHandledClass();
        while (parent != null && parent != Object.class) {
            parent = parent.getSuperclass();

            // Add the handler if it's present
            TypeHandler<R, K, D, ? extends T> typeHandler = classHandlerLookup.get(parent);
            if (typeHandler != null) {
                typeHandlers.add(typeHandler);
            }
        }

        // Reverse the list, so it's base -> final handler ordered
        Collections.reverse(typeHandlers);

        // Return the handlers
        return typeHandlers;
    }

    /**
     * Get all the type handlers which should be used from an initial type handler.
     *
     * @param lastHandler the handler used for lookup which should be the class specific type handler.
     * @return a list of handlers which should be used for the type.
     */
    protected Collection<TypeHandler<R, K, D, ? extends T>> getTypeHandlers(TypeHandler<R, K, D, ? extends T> lastHandler) {
        return classHierarchyLookupCache.computeIfAbsent(lastHandler, this::generateTypeHandlerHierarchy);
    }

    /**
     * Get the key from the data type which identifies what type to use for deserialization.
     *
     * @param input the input data type.
     * @return an optional containing the key which is inside the data that can identify the type.
     */
    public abstract Optional<K> getKey(D input);

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> to(D input) {
        // Get the handler used for the final type
        Optional<TypeHandler<R, K, D, ? extends T>> optionalTypeHandler = getToTypeHandler(input);
        if (optionalTypeHandler.isEmpty()) return Optional.empty();

        // Use the last handler to construct the class
        TypeHandler<R, K, D, ? extends T> lastTypeHandler = optionalTypeHandler.get();
        T output = lastTypeHandler.construct();
        if (output == null) return Optional.empty();

        // Loop through each handler in the hierarchy from base -> final
        Collection<TypeHandler<R, K, D, ? extends T>> typeHandlers = getTypeHandlers(lastTypeHandler);
        for (TypeHandler<R, K, D, ? extends T> typeHandler : typeHandlers) {
            //noinspection unchecked
            ((TypeHandler<R, K, D, T>) typeHandler).read(resolvers, input, output);
        }
        return Optional.of(output);
    }

    /**
     * Use a specific class instead of reading the key for converting a data type.
     *
     * @param type  the class to use to decode the input.
     * @param input the input to convert from the data-type.
     * @return present if it managed to convert using the type handler otherwise absent.
     */
    @SuppressWarnings("unchecked")
    public Optional<T> to(Class<? extends T> type, D input) {
        // Get the handler used for the final type
        Optional<? extends TypeHandler<R, K, D, ? extends T>> optionalTypeHandler = getFromClassTypeHandler(type);
        if (optionalTypeHandler.isEmpty()) return Optional.empty();

        // Use the last handler to construct the class
        TypeHandler<R, K, D, ? extends T> lastTypeHandler = optionalTypeHandler.get();

        // Safety check, ensure this can actually be read by the handler
        if (!type.isAssignableFrom(lastTypeHandler.getHandledClass())) return Optional.empty();

        // Construct the output
        T output = lastTypeHandler.construct();
        if (output == null) return Optional.empty();

        // Loop through each handler in the hierarchy from base -> final
        Collection<TypeHandler<R, K, D, ? extends T>> typeHandlers = getTypeHandlers(lastTypeHandler);
        for (TypeHandler<R, K, D, ? extends T> typeHandler : typeHandlers) {
            //noinspection unchecked
            ((TypeHandler<R, K, D, T>) typeHandler).read(resolvers, input, output);
        }
        return Optional.of(output);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<D> from(T input) {
        // Get the handler used for the final type
        Optional<TypeHandler<R, K, D, ? extends T>> optionalTypeHandler = getFromTypeHandler(input);
        if (optionalTypeHandler.isEmpty()) return Optional.empty();

        // Use the last handler to construct the class
        TypeHandler<R, K, D, ? extends T> lastTypeHandler = optionalTypeHandler.get();

        // Safety check, ensure this can actually be written by the handler
        if (!lastTypeHandler.getHandledClass().isInstance(input)) return Optional.empty();

        // Construct the output
        D output = constructDataType(lastTypeHandler.getKey());
        if (output == null) return Optional.empty();

        // Loop through each handler in the hierarchy from base -> final
        Collection<TypeHandler<R, K, D, ? extends T>> typeHandlers = getTypeHandlers(lastTypeHandler);
        for (TypeHandler<R, K, D, ? extends T> typeHandler : typeHandlers) {
            //noinspection unchecked
            ((TypeHandler<R, K, D, T>) typeHandler).write(resolvers, output, input);
        }
        return Optional.of(output);
    }
}
