package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The current state of a vault.
 */
public enum VaultState implements BlockStateValue {
    INACTIVE,
    ACTIVE,
    UNLOCKING,
    EJECTING
}
