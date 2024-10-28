package com.hivemc.chunker.conversion.encoding.base.resolver.entity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.column.entity.UnknownEntity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.resolver.hierarchy.KeyedHierarchyBasedResolver;
import com.hivemc.chunker.resolver.hierarchy.TypeHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A resolver base which can turn a data type into a chunker entity.
 * The resolver will call handlers from base to child type, using the child type to construct the object. This allows
 * logic to be shared, e.g. base entity properties don't need to be handled every time.
 *
 * @param <R> the resolvers used by this format which are provided to this resolver.
 * @param <D> the data class holding the encoded entity (e.g. NBT).
 */
public abstract class EntityResolver<R, D> extends KeyedHierarchyBasedResolver<R, ChunkerEntityType, D, Entity> {
    protected final boolean preserveUnknownEntities;
    protected Map<TypeHandler<R, ChunkerEntityType, D, ? extends Entity>, Map<Class<?>, Collection<Object>>> interfaceHandlerCache;
    protected Map<ChunkerBlockType, TypeHandler<R, ChunkerEntityType, D, ? extends Entity>> generateBeforeProcessEntityHandlers;
    protected Map<ChunkerBlockType, TypeHandler<R, ChunkerEntityType, D, ? extends Entity>> generateBeforeWriteEntityHandlers;

    /**
     * Create a new entity resolver.
     *
     * @param version                 the format version.
     * @param resolvers               the resolvers used by this format which are provided to this resolver.
     * @param preserveUnknownEntities whether unknown block entities should be read/written as UnknownEntity.
     */
    public EntityResolver(Version version, R resolvers, boolean preserveUnknownEntities) {
        super(version, resolvers);
        this.preserveUnknownEntities = preserveUnknownEntities;
    }

    @Override
    protected void init() {
        interfaceHandlerCache = new ConcurrentHashMap<>();
        generateBeforeProcessEntityHandlers = new ConcurrentHashMap<>();
        generateBeforeWriteEntityHandlers = new ConcurrentHashMap<>();
    }

    @Override
    protected void register(TypeHandler<R, ChunkerEntityType, D, ? extends Entity> typeHandler) {
        super.register(typeHandler);

        if (typeHandler instanceof GenerateBeforeWriteEntityHandler<?> handler) {
            for (ChunkerBlockType type : handler.getGenerateBeforeWriteBlockTypes()) {
                generateBeforeWriteEntityHandlers.put(type, typeHandler);
            }
        }
        if (typeHandler instanceof GenerateBeforeProcessEntityHandler<?> handler) {
            for (ChunkerBlockType type : handler.getGenerateBeforeProcessBlockTypes()) {
                generateBeforeProcessEntityHandlers.put(type, typeHandler);
            }
        }
        if (typeHandler instanceof UpdateBeforeWriteEntityHandler<?, ?> handler) {
            // Add another class lookup for this handler
            // This allows format specific types, e.g. BedrockPaintingEntity to handle PaintingBlockEntity
            if (handler.getAdditionalHandledClass() != null && handler.getAdditionalHandledClass() != typeHandler.getHandledClass()) {
                classHandlerLookup.putIfAbsent(handler.getAdditionalHandledClass(), typeHandler);
            }
        }
        interfaceHandlerCache.clear();
    }

    private <T> Collection<Object> generateInterfaceHandlers(Class<T> interfaceClass, TypeHandler<R, ChunkerEntityType, D, ? extends Entity> lastHandler) {
        Collection<TypeHandler<R, ChunkerEntityType, D, ? extends Entity>> handlers = getTypeHandlers(lastHandler);
        if (handlers.isEmpty()) return Collections.emptyList(); // No handlers

        // Add entries matching the interface to the list
        List<Object> copy = new ArrayList<>(handlers.size());
        for (TypeHandler<R, ChunkerEntityType, D, ? extends Entity> handler : handlers) {
            if (interfaceClass.isInstance(handler)) {
                copy.add(interfaceClass.cast(handler));
            }
        }
        return copy;
    }

    /**
     * Get all the type handlers which implement a specific interface.
     *
     * @param interfaceClass the interface to find.
     * @param lastHandler    the main handler for the type.
     * @param <T>            the interface class type.
     * @param <U>            the type handlers type which implement the class.
     * @return a collection of type handlers implementing the interface class.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T, U extends T> Collection<U> getInterfaceHandlers(Class<T> interfaceClass, TypeHandler<R, ChunkerEntityType, D, ? extends Entity> lastHandler) {
        Map<Class<?>, Collection<Object>> interfaceLookup = interfaceHandlerCache.computeIfAbsent(lastHandler, (handler) -> new ConcurrentHashMap<>());
        Collection<Object> handlers = interfaceLookup.computeIfAbsent(interfaceClass, (handler) -> generateInterfaceHandlers(interfaceClass, lastHandler));

        // Cast to Collection then U as we can't safely cast otherwise
        return (Collection<U>) (Collection) handlers;
    }

    /**
     * Remove any entities which need removing before the column is submitted for processing.
     *
     * @param column the column holding the entity.
     * @param entity the entity.
     * @param <T>    the type of the entity.
     * @return true if the entity should be removed.
     */
    public <T extends Entity> boolean shouldRemoveBeforeProcess(ChunkerColumn column, T entity) {
        Optional<TypeHandler<R, ChunkerEntityType, D, ? extends Entity>> lastHandler = getFromTypeHandler(entity);
        if (lastHandler.isEmpty()) return false; // Don't remove

        // Find the handlers to call
        Collection<DoNotProcessEntityHandler<T>> handlers = getInterfaceHandlers(DoNotProcessEntityHandler.class, lastHandler.get());
        if (handlers.isEmpty()) return false; // Don't remove

        // Run each handler
        for (DoNotProcessEntityHandler<T> handler : handlers) {
            if (handler.shouldRemoveBeforeProcess(column, entity)) return true;
        }

        return false; // Don't remove
    }

    /**
     * Remove any entities which need removing before the column is written.
     *
     * @param column the column holding the entity.
     * @param entity the entity.
     * @param <T>    the type of the entity.
     * @return true if the entity should be removed.
     */
    public <T extends Entity> boolean shouldRemoveBeforeWrite(ChunkerColumn column, T entity) {
        Optional<TypeHandler<R, ChunkerEntityType, D, ? extends Entity>> lastHandler = getFromTypeHandler(entity);
        if (lastHandler.isEmpty()) return false; // Don't remove

        // Find the handlers to call
        Collection<DoNotWriteEntityHandler<T>> handlers = getInterfaceHandlers(DoNotWriteEntityHandler.class, lastHandler.get());
        if (handlers.isEmpty()) return false; // Don't remove

        // Run each handler
        for (DoNotWriteEntityHandler<T> handler : handlers) {
            if (handler.shouldRemoveBeforeWrite(column, entity)) return true;
        }

        return false; // Don't remove
    }

    /**
     * Update any entities which need updating before the column is processed.
     *
     * @param column the column holding the entity.
     * @param entity the entity.
     * @param <T>    the type of the entity.
     * @return the new entity which should replace the old one.
     */
    public <T extends Entity> T updateBeforeProcess(ChunkerColumn column, T entity) {
        Optional<TypeHandler<R, ChunkerEntityType, D, ? extends Entity>> lastHandler = getFromTypeHandler(entity);
        if (lastHandler.isEmpty()) return entity; // Don't update

        // Find the handlers to call
        Collection<UpdateBeforeProcessEntityHandler<R, T>> handlers = getInterfaceHandlers(UpdateBeforeProcessEntityHandler.class, lastHandler.get());
        if (handlers.isEmpty()) return entity; // Don't update

        // Run each handler
        for (UpdateBeforeProcessEntityHandler<R, T> handler : handlers) {
            entity = handler.updateBeforeProcess(resolvers, column, entity);
        }

        return entity;
    }

    /**
     * Update any entities which need updating before the column is written.
     *
     * @param column the column holding the entity.
     * @param entity the entity.
     * @param <T>    the type of the entity.
     * @return the new entity which should replace the old one.
     */
    public <T extends Entity> T updateBeforeWrite(ChunkerColumn column, T entity) {
        Optional<TypeHandler<R, ChunkerEntityType, D, ? extends Entity>> lastHandler = getFromTypeHandler(entity);
        if (lastHandler.isEmpty()) return entity; // Don't update

        // Find the handlers to call
        Collection<UpdateBeforeWriteEntityHandler<R, T>> handlers = getInterfaceHandlers(UpdateBeforeWriteEntityHandler.class, lastHandler.get());
        if (handlers.isEmpty()) return entity; // Don't update

        // Run each handler
        for (UpdateBeforeWriteEntityHandler<R, T> handler : handlers) {
            entity = handler.updateBeforeWrite(resolvers, column, entity);
        }

        return entity;
    }

    /**
     * Generate any entities from blocks before the chunk is submitted for processing.
     *
     * @param column the column being read.
     * @param chunk  the chunk being read.
     */
    public void generateBeforeProcessEntities(ChunkerColumn column, ChunkerChunk chunk) {
        Palette<ChunkerBlockIdentifier> palette = chunk.getPalette();
        // Check if the chunk contains any entity handlers
        if (!chunk.getPalette().containsKey(a -> generateBeforeProcessEntityHandlers.containsKey(a.getType())))
            return;

        // Loop through every block in the chunk
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    ChunkerBlockIdentifier entry = palette.get(x, y, z, ChunkerBlockIdentifier.AIR);
                    TypeHandler<R, ChunkerEntityType, D, ? extends Entity> lastHandler = generateBeforeProcessEntityHandlers.get(entry.getType());

                    // Create the entity
                    if (lastHandler != null) {
                        Entity entity = Objects.requireNonNull(lastHandler.construct());

                        // Set the position
                        entity.setPositionX(column.getPosition().chunkX() << 4 | x);
                        entity.setPositionY(chunk.getY() << 4 | y);
                        entity.setPositionZ(column.getPosition().chunkZ() << 4 | z);

                        // Add it to the entities
                        column.getEntities().add(entity);

                        // Call the handlers
                        Collection<GenerateBeforeProcessEntityHandler<Entity>> handlers = getInterfaceHandlers(GenerateBeforeProcessEntityHandler.class, lastHandler);
                        if (handlers.isEmpty()) return; // Don't update

                        // Run each handler
                        for (GenerateBeforeProcessEntityHandler<Entity> handler : handlers) {
                            handler.generateBeforeProcess(column, x, y, z, entity, entry);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generate any entities from blocks before the chunk is written.
     *
     * @param column the column being written.
     * @param chunk  the chunk being written.
     */
    public void generateBeforeWriteEntities(ChunkerColumn column, ChunkerChunk chunk) {
        Palette<ChunkerBlockIdentifier> palette = chunk.getPalette();
        // Check if the chunk contains any entity handlers
        if (!chunk.getPalette().containsKey(a -> generateBeforeWriteEntityHandlers.containsKey(a.getType())))
            return;

        // Loop through every block in the chunk
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    ChunkerBlockIdentifier entry = palette.get(x, y, z, ChunkerBlockIdentifier.AIR);
                    TypeHandler<R, ChunkerEntityType, D, ? extends Entity> lastHandler = generateBeforeWriteEntityHandlers.get(entry.getType());

                    // Create the entity
                    if (lastHandler != null) {
                        Entity entity = Objects.requireNonNull(lastHandler.construct());

                        // Set the position
                        entity.setPositionX(column.getPosition().chunkX() << 4 | x);
                        entity.setPositionY(chunk.getY() << 4 | y);
                        entity.setPositionZ(column.getPosition().chunkZ() << 4 | z);

                        // Add it to the entities
                        column.getEntities().add(entity);

                        // Call the handlers
                        Collection<GenerateBeforeWriteEntityHandler<Entity>> handlers = getInterfaceHandlers(GenerateBeforeWriteEntityHandler.class, lastHandler);
                        if (handlers.isEmpty()) return; // Don't update

                        // Run each handler
                        for (GenerateBeforeWriteEntityHandler<Entity> handler : handlers) {
                            handler.generateBeforeWrite(column, x, y, z, entity, entry);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Optional<D> from(Entity input) {
        // Special handling for unknown entities
        if (input instanceof UnknownEntity && preserveUnknownEntities) {
            // Use the Entity handler to write the fields for unknown
            Optional<TypeHandler<R, ChunkerEntityType, D, Entity>> entityHandler = getFromClassTypeHandler(Entity.class);
            if (entityHandler.isPresent()) {
                D output = constructDataType(input.getEntityType());
                if (output != null) {
                    entityHandler.get().write(resolvers, output, input);

                    // Return the written entity
                    return Optional.of(output);
                }
            }
        }
        return super.from(input);
    }

    @Override
    public Optional<Entity> to(Class<? extends Entity> type, D input) {
        // Special handling for unknown entities
        if (type == UnknownEntity.class && preserveUnknownEntities) {
            UnknownEntity unknownEntity = new UnknownEntity(getKey(input).orElse(null));

            // Use the Entity handler to read the fields for unknown
            Optional<TypeHandler<R, ChunkerEntityType, D, Entity>> blockEntityHandler = getFromClassTypeHandler(Entity.class);
            if (blockEntityHandler.isPresent()) {
                blockEntityHandler.get().read(resolvers, input, unknownEntity);

                // Return the read entity
                return Optional.of(unknownEntity);
            }
        }
        return super.to(type, input);
    }

    @Override
    public Optional<Entity> to(D input) {
        Optional<Entity> result = super.to(input);

        // If preserving originals is enabled, turn it into an unknown entity
        if (result.isEmpty() && preserveUnknownEntities) {
            return to(UnknownEntity.class, input);
        }

        // Otherwise return the original
        return result;
    }
}
