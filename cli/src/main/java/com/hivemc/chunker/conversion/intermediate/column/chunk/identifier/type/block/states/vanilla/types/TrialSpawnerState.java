package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The current state of a trial spawner.
 */
public enum TrialSpawnerState implements BlockStateValue {
    INACTIVE,
    WAITING_FOR_PLAYERS,
    ACTIVE,
    WAITING_FOR_EJECTION,
    EJECTING_REWARD,
    COOLDOWN
}
