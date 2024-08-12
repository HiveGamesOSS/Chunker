package com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;

/**
 * Java Pre-Transform Manager which is called after chunks are read. It handles inferring block states based on
 * neighbouring blocks. It is called in the postProcess method of the Java reader.
 */
public class JavaReaderPreTransformManager extends PreTransformManager {
    /**
     * Create a new pre-transform manager used for reading.
     *
     * @param version the input version.
     */
    public JavaReaderPreTransformManager(Version version) {
        super(version);
    }

    @Override
    public void registerHandlers(Version version) {
        // Currently no handlers
    }
}
