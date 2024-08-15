package com.hivemc.chunker.conversion.encoding.java.base.writer.pretransform.legacy;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;

/**
 * Java Legacy Pre-Transform Manager which is called before columns are written. It is returned through the ColumnWriter
 * so that the pipeline handlers can call the solve method.
 */
public class JavaLegacyWriterPreTransformManager extends PreTransformManager {
    /**
     * Create a new java legacy writer pre-transform manager.
     *
     * @param version the version being written.
     */
    public JavaLegacyWriterPreTransformManager(Version version) {
        super(version);
    }

    @Override
    public void registerHandlers(Version version) {
        // Currently no handlers
    }
}
