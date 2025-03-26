package com.hivemc.chunker.conversion.bedrock.resolver;

import com.hivemc.chunker.conversion.WorldConverter;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * MockConverter that allows settings of the level.
 */
public class MockConverter extends WorldConverter {
    public MockConverter(@Nullable ChunkerLevel mockLevel) {
        super(UUID.randomUUID());
        level = mockLevel;
    }
}
