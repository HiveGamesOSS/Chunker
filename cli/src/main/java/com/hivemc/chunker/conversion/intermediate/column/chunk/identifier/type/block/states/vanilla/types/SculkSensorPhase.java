package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The current phase of the sculk sensor.
 */
public enum SculkSensorPhase implements BlockStateValue {
    INACTIVE,
    ACTIVE,
    COOLDOWN
}
