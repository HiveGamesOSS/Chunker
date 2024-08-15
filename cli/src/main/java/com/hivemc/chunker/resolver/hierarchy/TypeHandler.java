package com.hivemc.chunker.resolver.hierarchy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A type-handler which handles a type in a class-hierarchy.
 * Super-class TypeHandlers -> This -> Sub-class TypeHandlers (if any are registered)
 * e.g. BlockEntity -> ContainerBlockEntity -> ChestBlockEntity
 *
 * @param <R> the resolvers which can be used by this type.
 * @param <K> the key type used to identify the type.
 * @param <D> the data-type used for input/output.
 * @param <T> the specific type which is handled by this handler.
 */
public interface TypeHandler<R, K, D, T> {
    /**
     * Get the class which is handled by this type handler.
     *
     * @return the class handled by this class.
     */
    Class<? extends T> getHandledClass();

    /**
     * Create an instance of the underlying type handled by this handler, this is used by the resolver when this is the
     * specific handler resolved.
     *
     * @return a new empty instance of the type.
     */
    @Nullable
    T construct();

    /**
     * The key associated with this type.
     *
     * @return the key.
     */
    K getKey();

    /**
     * Read an input value and apply class specific data to the value.
     * After this read is performed, any subclasses will then be read.
     *
     * @param resolvers the resolvers which can be used by this type.
     * @param input     the input value to read.
     * @param value     the output value, which will be constructed via construct() or by a subclass.
     */
    void read(@NotNull R resolvers, @NotNull D input, @NotNull T value);

    /**
     * Write class specific data to an output.
     * After this write is performed, any subclasses will then be written.
     *
     * @param resolvers the resolvers which can be used by this type.
     * @param output    the output to which data should be written to.
     * @param value     the input type to read data from.
     */
    void write(@NotNull R resolvers, @NotNull D output, @NotNull T value);
}
