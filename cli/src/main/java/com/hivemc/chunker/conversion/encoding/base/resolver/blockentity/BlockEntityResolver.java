package com.hivemc.chunker.conversion.encoding.base.resolver.blockentity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.UnknownBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.resolver.hierarchy.KeyedHierarchyBasedResolver;
import com.hivemc.chunker.resolver.hierarchy.TypeHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A resolver base which can turn a data type into a chunker block entity.
 * The resolver will call handlers from base to child type, using the child type to construct the object. This allows
 * logic to be shared, e.g. a Container handler would just write Items where a chest handler may write if it's a double.
 *
 * @param <R> the resolvers used by this format which are provided to this resolver.
 * @param <D> the data class holding the encoded entity (e.g. NBT).
 */
public abstract class BlockEntityResolver<R, D> extends KeyedHierarchyBasedResolver<R, String, D, BlockEntity> {
    protected final boolean preserveUnknownBlockEntities;
    protected Map<TypeHandler<R, String, D, ? extends BlockEntity>, Map<Class<?>, Collection<Object>>> interfaceHandlerCache;
    protected Map<ChunkerBlockType, TypeHandler<R, String, D, ? extends BlockEntity>> generateBeforeProcessBlockEntityHandlers;
    protected Map<ChunkerBlockType, TypeHandler<R, String, D, ? extends BlockEntity>> generateBeforeWriteBlockEntityHandlers;

    /**
     * Create a new block entity resolver.
     *
     * @param version                      the format version.
     * @param resolvers                    the resolvers used by this format which are provided to this resolver.
     * @param preserveUnknownBlockEntities whether unknown block entities should be read/written as UnknownBlockEntity.
     */
    public BlockEntityResolver(Version version, R resolvers, boolean preserveUnknownBlockEntities) {
        super(version, resolvers);
        this.preserveUnknownBlockEntities = preserveUnknownBlockEntities;
    }

    @Override
    protected void init() {
        interfaceHandlerCache = new ConcurrentHashMap<>();
        generateBeforeProcessBlockEntityHandlers = new ConcurrentHashMap<>();
        generateBeforeWriteBlockEntityHandlers = new ConcurrentHashMap<>();
    }

    @Override
    protected void register(TypeHandler<R, String, D, ? extends BlockEntity> typeHandler) {
        super.register(typeHandler);

        if (typeHandler instanceof GenerateBeforeWriteBlockEntityHandler<?> handler) {
            for (ChunkerBlockType type : handler.getGenerateBeforeWriteBlockTypes()) {
                generateBeforeWriteBlockEntityHandlers.put(type, typeHandler);
            }
        }
        if (typeHandler instanceof GenerateBeforeProcessBlockEntityHandler<?> handler) {
            for (ChunkerBlockType type : handler.getGenerateBeforeProcessBlockTypes()) {
                generateBeforeProcessBlockEntityHandlers.put(type, typeHandler);
            }
        }
        if (typeHandler instanceof UpdateBeforeWriteBlockEntityHandler<?, ?> handler) {
            // Add another class lookup for this handler
            // This allows format specific types, e.g. BedrockBannerBlockEntity to handle BannerBlockEntity
            if (handler.getAdditionalHandledClass() != null && handler.getAdditionalHandledClass() != typeHandler.getHandledClass()) {
                classHandlerLookup.putIfAbsent(handler.getAdditionalHandledClass(), typeHandler);
            }
        }
        interfaceHandlerCache.clear();
    }

    private <T> Collection<Object> generateInterfaceHandlers(Class<T> interfaceClass, TypeHandler<R, String, D, ? extends BlockEntity> lastHandler) {
        Collection<TypeHandler<R, String, D, ? extends BlockEntity>> handlers = getTypeHandlers(lastHandler);
        if (handlers.isEmpty()) return Collections.emptyList(); // No handlers

        // Add entries matching the interface to the list
        List<Object> copy = new ArrayList<>(handlers.size());
        for (TypeHandler<R, String, D, ? extends BlockEntity> handler : handlers) {
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
    public <T, U extends T> Collection<U> getInterfaceHandlers(Class<T> interfaceClass, TypeHandler<R, String, D, ? extends BlockEntity> lastHandler) {
        Map<Class<?>, Collection<Object>> interfaceLookup = interfaceHandlerCache.computeIfAbsent(lastHandler, (handler) -> new ConcurrentHashMap<>());
        Collection<Object> handlers = interfaceLookup.computeIfAbsent(interfaceClass, (handler) -> generateInterfaceHandlers(interfaceClass, lastHandler));

        // Cast to Collection then U as we can't safely cast otherwise
        return (Collection<U>) (Collection) handlers;
    }

    /**
     * Remove any block entities that need removing before the column is submitted.
     *
     * @param column      the column holding the block entity.
     * @param x           the global x co-ordinate.
     * @param y           the global y co-ordinate.
     * @param z           the global z co-ordinate.
     * @param blockEntity the block entity.
     * @param <T>         the type of the block entity.
     * @return true if the block entity should be removed.
     */
    public <T extends BlockEntity> boolean shouldRemoveBeforeProcess(ChunkerColumn column, int x, int y, int z, T blockEntity) {
        Optional<TypeHandler<R, String, D, ? extends BlockEntity>> lastHandler = getFromTypeHandler(blockEntity);
        if (lastHandler.isEmpty()) return false; // Don't remove

        // Find the handlers to call
        Collection<DoNotProcessBlockEntityHandler<T>> handlers = getInterfaceHandlers(DoNotProcessBlockEntityHandler.class, lastHandler.get());
        if (handlers.isEmpty()) return false; // Don't remove

        // Run each handler
        for (DoNotProcessBlockEntityHandler<T> handler : handlers) {
            if (handler.shouldRemoveBeforeProcess(column, x, y, z, blockEntity)) return true;
        }

        return false; // Don't remove
    }

    /**
     * Remove any block entities that need removing before writing.
     *
     * @param column      the column holding the block entity.
     * @param x           the global x co-ordinate.
     * @param y           the global y co-ordinate.
     * @param z           the global z co-ordinate.
     * @param blockEntity the block entity.
     * @param <T>         the type of the block entity.
     * @return true if the block entity should be removed.
     */
    public <T extends BlockEntity> boolean shouldRemoveBeforeWrite(ChunkerColumn column, int x, int y, int z, T blockEntity) {
        Optional<TypeHandler<R, String, D, ? extends BlockEntity>> lastHandler = getFromTypeHandler(blockEntity);
        if (lastHandler.isEmpty()) return false; // Don't remove

        // Find the handlers to call
        Collection<DoNotWriteBlockEntityHandler<T>> handlers = getInterfaceHandlers(DoNotWriteBlockEntityHandler.class, lastHandler.get());
        if (handlers.isEmpty()) return false; // Don't remove

        // Run each handler
        for (DoNotWriteBlockEntityHandler<T> handler : handlers) {
            if (handler.shouldRemoveBeforeWrite(column, x, y, z, blockEntity)) return true;
        }

        return false; // Don't remove
    }

    /**
     * Update any block entities which need updating before the column is processed.
     *
     * @param column      the column holding the block entity.
     * @param x           the global x co-ordinate.
     * @param y           the global y co-ordinate.
     * @param z           the global z co-ordinate.
     * @param blockEntity the block entity.
     * @param <T>         the type of the block entity.
     * @return the new block entity which should replace the old one.
     */
    public <T extends BlockEntity> T updateBeforeProcess(ChunkerColumn column, int x, int y, int z, T blockEntity) {
        Optional<TypeHandler<R, String, D, ? extends BlockEntity>> lastHandler = getFromTypeHandler(blockEntity);
        if (lastHandler.isEmpty()) return blockEntity; // Don't update

        // Find the handlers to call
        Collection<UpdateBeforeProcessBlockEntityHandler<R, T>> handlers = getInterfaceHandlers(UpdateBeforeProcessBlockEntityHandler.class, lastHandler.get());
        if (handlers.isEmpty()) return blockEntity; // Don't update

        // Run each handler
        for (UpdateBeforeProcessBlockEntityHandler<R, T> handler : handlers) {
            blockEntity = handler.updateBeforeProcess(resolvers, column, x, y, z, blockEntity);
        }

        return blockEntity;
    }

    /**
     * Update any block entities which need updating before the column is written.
     *
     * @param column      the column holding the block entity.
     * @param x           the global x co-ordinate.
     * @param y           the global y co-ordinate.
     * @param z           the global z co-ordinate.
     * @param blockEntity the block entity.
     * @param <T>         the type of the block entity.
     * @return the new block entity which should replace the old one.
     */
    public <T extends BlockEntity> T updateBeforeWrite(ChunkerColumn column, int x, int y, int z, T blockEntity) {
        Optional<TypeHandler<R, String, D, ? extends BlockEntity>> lastHandler = getFromTypeHandler(blockEntity);
        if (lastHandler.isEmpty()) return blockEntity; // Don't update

        // Find the handlers to call
        Collection<UpdateBeforeWriteBlockEntityHandler<R, T>> handlers = getInterfaceHandlers(UpdateBeforeWriteBlockEntityHandler.class, lastHandler.get());
        if (handlers.isEmpty()) return blockEntity; // Don't update

        // Run each handler
        for (UpdateBeforeWriteBlockEntityHandler<R, T> handler : handlers) {
            blockEntity = handler.updateBeforeWrite(resolvers, column, x, y, z, blockEntity);
        }

        return blockEntity;
    }

    /**
     * Attempt to use block entity specific handlers to read NBT, run after normal decoding.
     *
     * @param blockEntityClass the block entity class type being read.
     * @param itemStack        the item stack being read.
     * @param output           the output to write to, may be null if it was not read from the tag.
     * @param input            the input to read from.
     * @param <T>              the type of the block entity.
     * @return an optional of if a block entity was read, if no results it should return the input.
     */
    public <T extends BlockEntity> Optional<T> generateFromItemNBT(Class<? extends T> blockEntityClass, @NotNull ChunkerItemStack itemStack, @Nullable T output, @NotNull CompoundTag input) {
        Optional<? extends TypeHandler<R, String, D, ? extends T>> lastHandler = getFromClassTypeHandler(blockEntityClass);
        if (lastHandler.isEmpty()) return Optional.ofNullable(output); // Return the input

        // Find the handlers to call
        Collection<CustomItemNBTBlockEntityHandler<R, T>> handlers = getInterfaceHandlers(CustomItemNBTBlockEntityHandler.class, lastHandler.get());
        if (handlers.isEmpty()) return Optional.ofNullable(output); // Return the input

        // Track whether the output should be returned (aka properties read)
        boolean returnOutput = output != null;

        // Construct an empty object for the handlers
        if (output == null) {
            output = Objects.requireNonNull((T) lastHandler.get().construct());
        }

        // Loop through each handler
        for (CustomItemNBTBlockEntityHandler<R, T> handler : handlers) {
            if (handler.generateFromItemNBT(resolvers, itemStack, output, input)) {
                returnOutput = true;
            }
        }

        return returnOutput ? Optional.of(output) : Optional.empty();
    }

    /**
     * Attempt to use block entity specific handlers to write NBT.
     *
     * @param itemStack the item stack being written.
     * @param input     the block entity.
     * @param output    the output to write to.
     * @param <T>       the type of the block entity.
     * @return whether the normal block entity data should also be written.
     */
    public <T extends BlockEntity> boolean writeToItemNBT(@NotNull ChunkerItemStack itemStack, @NotNull T input, @NotNull CompoundTag output) {
        Optional<TypeHandler<R, String, D, ? extends BlockEntity>> lastHandler = getFromTypeHandler(input);
        if (lastHandler.isEmpty()) return true; // No handler, write any block entity data

        // Find the handlers to call
        Collection<CustomItemNBTBlockEntityHandler<R, T>> handlers = getInterfaceHandlers(CustomItemNBTBlockEntityHandler.class, lastHandler.get());
        if (handlers.isEmpty()) return true; // No handler, write any block entity data

        // Track whether the block entity data should be written
        boolean writeBlockEntityData = true;

        // Loop through each handler
        for (CustomItemNBTBlockEntityHandler<R, T> handler : handlers) {
            if (!handler.writeToItemNBT(resolvers, itemStack, input, output)) {
                writeBlockEntityData = false;
            }
        }

        return writeBlockEntityData;
    }

    /**
     * Generate any block entities from blocks before the chunk is submitted for processing.
     *
     * @param column the column being read.
     * @param chunk  the chunk being read.
     */
    public void generateBeforeProcessBlockEntities(ChunkerColumn column, ChunkerChunk chunk) {
        Palette<ChunkerBlockIdentifier> palette = chunk.getPalette();
        // Check if the chunk contains any block entity handlers
        if (!chunk.getPalette().containsKey(a -> generateBeforeProcessBlockEntityHandlers.containsKey(a.getType())))
            return;

        // Loop through every block in the chunk
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    ChunkerBlockIdentifier entry = palette.get(x, y, z, ChunkerBlockIdentifier.AIR);
                    TypeHandler<R, String, D, ? extends BlockEntity> lastHandler = generateBeforeProcessBlockEntityHandlers.get(entry.getType());

                    // Create the block entity
                    if (lastHandler != null && column.getBlockEntity(x, chunk.getY() << 4 | y, z) == null) {
                        BlockEntity blockEntity = Objects.requireNonNull(lastHandler.construct());

                        // Set the position
                        blockEntity.setX(column.getPosition().chunkX() << 4 | x);
                        blockEntity.setY(chunk.getY() << 4 | y);
                        blockEntity.setZ(column.getPosition().chunkZ() << 4 | z);

                        // Add it to the block entities
                        column.getBlockEntities().add(blockEntity);

                        // Call the handlers
                        Collection<GenerateBeforeProcessBlockEntityHandler<BlockEntity>> handlers = getInterfaceHandlers(GenerateBeforeProcessBlockEntityHandler.class, lastHandler);
                        if (handlers.isEmpty()) return; // Don't update

                        // Run each handler
                        for (GenerateBeforeProcessBlockEntityHandler<BlockEntity> handler : handlers) {
                            handler.generateBeforeProcess(column, x, y, z, blockEntity, entry);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generate any block entities from blocks before the chunk is written.
     *
     * @param column the column being written.
     * @param chunk  the chunk being written.
     */
    public void generateBeforeWriteBlockEntities(ChunkerColumn column, ChunkerChunk chunk) {
        Palette<ChunkerBlockIdentifier> palette = chunk.getPalette();
        // Check if the chunk contains any block entity handlers
        if (!chunk.getPalette().containsKey(a -> generateBeforeWriteBlockEntityHandlers.containsKey(a.getType())))
            return;

        // Loop through every block in the chunk
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    ChunkerBlockIdentifier entry = palette.get(x, y, z, ChunkerBlockIdentifier.AIR);
                    TypeHandler<R, String, D, ? extends BlockEntity> lastHandler = generateBeforeWriteBlockEntityHandlers.get(entry.getType());

                    // Create the block entity
                    if (lastHandler != null && column.getBlockEntity(x, chunk.getY() << 4 | y, z) == null) {
                        BlockEntity blockEntity = Objects.requireNonNull(lastHandler.construct());

                        // Set the position
                        blockEntity.setX(column.getPosition().chunkX() << 4 | x);
                        blockEntity.setY(chunk.getY() << 4 | y);
                        blockEntity.setZ(column.getPosition().chunkZ() << 4 | z);

                        // Add it to the block entities
                        column.getBlockEntities().add(blockEntity);

                        // Call the handlers
                        Collection<GenerateBeforeWriteBlockEntityHandler<BlockEntity>> handlers = getInterfaceHandlers(GenerateBeforeWriteBlockEntityHandler.class, lastHandler);
                        if (handlers.isEmpty()) return; // Don't update

                        // Run each handler
                        for (GenerateBeforeWriteBlockEntityHandler<BlockEntity> handler : handlers) {
                            handler.generateBeforeWrite(column, x, y, z, blockEntity, entry);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Optional<D> from(BlockEntity input) {
        // Special handling for unknown block entities
        if (input instanceof UnknownBlockEntity unknownBlockEntity && preserveUnknownBlockEntities) {
            // Use the BlockEntity handler to write the fields for unknown
            Optional<TypeHandler<R, String, D, BlockEntity>> blockEntityHandler = getFromClassTypeHandler(BlockEntity.class);
            if (blockEntityHandler.isPresent()) {
                D output = constructDataType(unknownBlockEntity.getType());
                if (output != null) {
                    blockEntityHandler.get().write(resolvers, output, input);

                    // Return the written block entity
                    return Optional.of(output);
                }
            }
        }
        return super.from(input);
    }

    @Override
    public Optional<BlockEntity> to(Class<? extends BlockEntity> type, D input) {
        // Special handling for unknown block entities
        if (type == UnknownBlockEntity.class && preserveUnknownBlockEntities) {
            UnknownBlockEntity unknownBlockEntity = new UnknownBlockEntity(getKey(input).orElse(""));

            // Use the BlockEntity handler to read the fields for unknown
            Optional<TypeHandler<R, String, D, BlockEntity>> blockEntityHandler = getFromClassTypeHandler(BlockEntity.class);
            if (blockEntityHandler.isPresent()) {
                blockEntityHandler.get().read(resolvers, input, unknownBlockEntity);

                // Return the read block entity
                return Optional.of(unknownBlockEntity);
            }
        }
        return super.to(type, input);
    }

    @Override
    public Optional<BlockEntity> to(D input) {
        Optional<BlockEntity> result = super.to(input);

        // If preserving originals is enabled, turn it into an unknown block entity
        if (result.isEmpty() && preserveUnknownBlockEntities) {
            return to(UnknownBlockEntity.class, input);
        }

        // Otherwise return the original
        return result;
    }
}
