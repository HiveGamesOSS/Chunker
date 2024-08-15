package com.hivemc.chunker.conversion.encoding.base.reader;

import com.hivemc.chunker.conversion.encoding.base.LevelReaderWriter;
import com.hivemc.chunker.conversion.handlers.LevelConversionHandler;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A reader which reads the base level data.
 */
public interface LevelReader extends LevelReaderWriter {
    /**
     * Invoked when the level data should be read.
     *
     * @param levelConversionHandler the output handler to call the relevant methods of.
     */
    void readLevel(LevelConversionHandler levelConversionHandler) throws Exception;

    /**
     * Read a custom level setting which is annotated as custom.
     *
     * @param root       the root NBT.
     * @param targetName the target name of the setting.
     * @param type       the setting type.
     * @return the value, by default throws an exception.
     */
    default @Nullable Object readCustomLevelSetting(@NotNull CompoundTag root, @NotNull String targetName, @NotNull Class<?> type) {
        throw new IllegalArgumentException("Custom setting reading is not implemented for this writer");
    }

    @Override
    default boolean isReader() {
        return true;
    }

    @Nullable
    default String getWarnings() {
        return null;
    }
}
