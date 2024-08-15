package com.hivemc.chunker.conversion.encoding.bedrock.base.writer.pretransform;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity.handlers.BedrockPaintingEntityHandler;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.intermediate.column.entity.PaintingEntity;

/**
 * Bedrock Pre-Transform Manager which is called before columns are written. It is returned through the ColumnWriter so
 * that the pipeline handlers can call the solve method.
 */
public class BedrockWriterPreTransformManager extends PreTransformManager {
    /**
     * Create a new bedrock writer pre-transform manager.
     *
     * @param version the version being written.
     */
    public BedrockWriterPreTransformManager(Version version) {
        super(version);
    }

    @Override
    public void registerHandlers(Version version) {
        // Relocate paintings
        registerHandler(new BedrockPaintingEntityHandler.Relocator(true), PaintingEntity.class);
    }
}
