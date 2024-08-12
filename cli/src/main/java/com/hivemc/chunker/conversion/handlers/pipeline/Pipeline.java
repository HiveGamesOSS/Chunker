package com.hivemc.chunker.conversion.handlers.pipeline;

import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.handlers.LevelConversionHandler;
import com.hivemc.chunker.conversion.handlers.WorldConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A pipeline with handlers to allow different handlers to be used when level, world and column data is processed.
 */
public class Pipeline {
    private final LevelConversionHandler base;
    private Function<LevelConversionHandler, LevelConversionHandler> levelConversionTransformer;
    private BiFunction<WorldConversionHandler, ChunkerLevel, WorldConversionHandler> worldConversionTransformer;
    private BiFunction<ColumnConversionHandler, ChunkerWorld, ColumnConversionHandler> columnConversionTransformer;

    /**
     * Create a new pipeline.
     *
     * @param base the conversion handler to delegate to.
     */
    public Pipeline(LevelConversionHandler base) {
        this.base = base;
    }

    /**
     * Compose functions together.
     *
     * @param handlers the handlers to compose using .andThen()
     * @param <T>      the type handled by the function.
     * @return a composed function, null if there is no handlers.
     */
    @SafeVarargs
    @Nullable
    protected static <T> Function<T, T> compose(Function<T, T>... handlers) {
        if (handlers.length == 0) return null; // Return empty if no handlers
        Function<T, T> function = handlers[0];
        for (int i = 1; i < handlers.length; i++) {
            function = function.andThen(handlers[i]);
        }
        return function;
    }

    /**
     * Compose bi-functions together.
     *
     * @param handlers the handlers to compose.
     * @param <T>      the type handled by the function.
     * @return a composed function, null if there is no handlers.
     */
    @SafeVarargs
    @Nullable
    protected static <T, U> BiFunction<T, U, T> compose(BiFunction<T, U, T>... handlers) {
        if (handlers.length == 0) return null; // Return empty if no handlers
        BiFunction<T, U, T> function = handlers[0];
        for (int i = 1; i < handlers.length; i++) {
            final BiFunction<T, U, T> previous = function;
            final BiFunction<T, U, T> current = handlers[i];

            // Combine the functions and passthrough U
            function = (t, u) -> current.apply(previous.apply(t, u), u);
        }
        return function;
    }

    /**
     * Set the level handlers to use for when a level is processed.
     *
     * @param handlers the handlers which are called in order to nest the delegate.
     */
    @SafeVarargs
    public final void levelHandlers(Function<LevelConversionHandler, LevelConversionHandler>... handlers) {
        levelConversionTransformer = compose(handlers);
    }

    /**
     * Set the world handlers to use for when a world is processed.
     *
     * @param handlers the handlers which are called in order to nest the delegate.
     */
    @SafeVarargs
    public final void worldHandlers(BiFunction<WorldConversionHandler, ChunkerLevel, WorldConversionHandler>... handlers) {
        worldConversionTransformer = compose(handlers);
    }

    /**
     * Set the column handlers to use for when a column is processed.
     *
     * @param handlers the handlers which are called in order to nest the delegate.
     */
    @SafeVarargs
    public final void columnHandlers(BiFunction<ColumnConversionHandler, ChunkerWorld, ColumnConversionHandler>... handlers) {
        columnConversionTransformer = compose(handlers);
    }

    /**
     * Build a LevelConversionHandler which invokes this pipeline.
     *
     * @return the newly created LevelConversionHandler, returning null if there is no base.
     */
    @Nullable
    public LevelConversionHandler build() {
        if (base == null) return null; // Don't apply pipeline if there's no base
        LevelConversionHandler delegate = levelConversionTransformer != null ? levelConversionTransformer.apply(base) : base;
        return new PipelineLevelConversionHandler(delegate);
    }

    /**
     * ColumnConversionHandler which calls the delegate asynchronously.
     */
    static class PipelineColumnConversionHandler implements ColumnConversionHandler {
        protected final ColumnConversionHandler delegate;

        /**
         * Create a new conversion handler which just delegates calls.
         *
         * @param delegate the delegate to call for column conversion.
         */
        public PipelineColumnConversionHandler(ColumnConversionHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public void convertColumn(ChunkerColumn column) {
            Task.asyncConsume("Running column middleware", TaskWeight.NORMAL, delegate::convertColumn, column);
        }

        @Override
        public void flushRegion(RegionCoordPair regionCoordPair) {
            Task.asyncConsume("Running column middleware", TaskWeight.NORMAL, delegate::flushRegion, regionCoordPair);
        }

        @Override
        public void flushColumns() {
            Task.async("Running column middleware", TaskWeight.NORMAL, delegate::flushColumns);
        }
    }

    /**
     * LevelConversionHandler which calls the delegate asynchronously.
     */
    class PipelineLevelConversionHandler implements LevelConversionHandler {
        protected final LevelConversionHandler delegate;

        /**
         * Create a new conversion handler which calls the delegate and uses the pipeline for the WorldConversionHandler.
         *
         * @param delegate the delegate to call for level conversion.
         */
        public PipelineLevelConversionHandler(LevelConversionHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public Task<WorldConversionHandler> convertLevel(ChunkerLevel level) {
            return Task.asyncUnwrap("Running level middleware", TaskWeight.NORMAL, () -> {
                return delegate.convertLevel(level).then("Applying level pipeline", TaskWeight.NONE, (base) -> {
                    if (base == null) return null; // Don't apply pipeline if there's no base
                    WorldConversionHandler delegate = worldConversionTransformer != null ? worldConversionTransformer.apply(base, level) : base;
                    return new PipelineWorldConversionHandler(delegate);
                });
            });
        }

        @Override
        public void flushLevel() {
            Task.async("Running level middleware", TaskWeight.NORMAL, delegate::flushLevel);
        }
    }

    /**
     * WorldConversionHandler which calls the delegate asynchronously.
     */
    class PipelineWorldConversionHandler implements WorldConversionHandler {
        protected final WorldConversionHandler delegate;

        /**
         * Create a new conversion handler which calls the delegate and uses the pipeline for the ColumnConversionHandler.
         *
         * @param delegate the delegate to call for world conversion.
         */
        public PipelineWorldConversionHandler(WorldConversionHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public Task<ColumnConversionHandler> convertWorld(ChunkerWorld world) {
            return Task.asyncUnwrap("Running world middleware", TaskWeight.NORMAL, () -> {
                return delegate.convertWorld(world).then("Applying world pipeline", TaskWeight.NONE, (base) -> {
                    if (base == null) return null; // Don't apply pipeline if there's no base
                    ColumnConversionHandler delegate = columnConversionTransformer != null ? columnConversionTransformer.apply(base, world) : base;
                    return new PipelineColumnConversionHandler(delegate);
                });
            });
        }

        @Override
        public void flushWorld(ChunkerWorld chunkerWorld) {
            Task.asyncConsume("Running world middleware", TaskWeight.NORMAL, delegate::flushWorld, chunkerWorld);
        }

        @Override
        public void flushWorlds() {
            Task.async("Running world middleware", TaskWeight.NORMAL, delegate::flushWorlds);
        }
    }
}
