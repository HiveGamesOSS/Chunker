package com.hivemc.chunker.conversion.encoding.base.writer;

import com.hivemc.chunker.conversion.encoding.base.LevelReaderWriter;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevelSettings;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.Nullable;

/**
 * A writer which can write the level data.
 */
public interface LevelWriter extends LevelReaderWriter {
    /**
     * Called when the level information should be written.
     *
     * @param chunkerLevel the level data.
     * @return the world writer to use.
     * @throws Exception if something went wrong.
     */
    WorldWriter writeLevel(ChunkerLevel chunkerLevel) throws Exception;

    /**
     * Called when the level has finished writing.
     *
     * @throws Exception if something went wrong.
     */
    default void flushLevel() throws Exception {
        // It isn't required to implement this method, it can be useful for flushing level writing
    }

    /**
     * Write a custom level setting which is annotated as custom.
     *
     * @param chunkerLevelSettings the settings object.
     * @param output               the output root NBT.
     * @param targetName           the target name of the setting.
     * @param value                the value to write.
     */
    default void writeCustomLevelSetting(ChunkerLevelSettings chunkerLevelSettings, CompoundTag output, String targetName, Object value) {
        throw new IllegalArgumentException("Custom setting writing is not implemented for this writer");
    }

    @Override
    default boolean isReader() {
        return false;
    }

    @Nullable
    default PreTransformManager getPreTransformManager() {
        return null;
    }
}
