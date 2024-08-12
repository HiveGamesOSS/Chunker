package com.hivemc.chunker.conversion.encoding.preview;

import com.hivemc.chunker.conversion.encoding.EncodingType;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;

import java.io.File;

/**
 * PreviewLevelWriter is a level writer that outputs a folder with preview images / a binary of present chunks.
 */
public class PreviewLevelWriter implements LevelWriter {
    private final File outputFolder;

    /**
     * Create a new preview level writer.
     *
     * @param outputFolder the output folder to write the data to.
     */
    public PreviewLevelWriter(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    @Override
    public WorldWriter writeLevel(ChunkerLevel chunkerLevel) {
        // Create a new world writer with the created worldData
        return new PreviewWorldWriter(outputFolder);
    }

    @Override
    public EncodingType getEncodingType() {
        return EncodingType.PREVIEW;
    }

    @Override
    public Version getVersion() {
        return new Version(1, 0, 0);
    }
}
