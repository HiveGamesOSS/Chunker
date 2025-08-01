package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;

import java.util.Set;

/**
 * Common states which are used together as groups.
 */
public class VanillaBlockStateGroups {
    public static final Set<BlockState<?>> AXIS = Set.of(VanillaBlockStates.AXIS);
    public static final Set<BlockState<?>> BED = Set.of(VanillaBlockStates.BED_PART, VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.OCCUPIED);
    public static final Set<BlockState<?>> BEE_HIVE = Set.of(VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.HONEY_LEVEL);
    public static final Set<BlockState<?>> CAMPFIRE = Set.of(VanillaBlockStates.SIGNAL_FIRE, VanillaBlockStates.LIT, VanillaBlockStates.FACING_HORIZONTAL);
    public static final Set<BlockState<?>> CANDLE = Set.of(VanillaBlockStates.LIT, VanillaBlockStates.CANDLES);
    public static final Set<BlockState<?>> CHEST = Set.of(VanillaBlockStates.CHEST_TYPE, VanillaBlockStates.FACING_HORIZONTAL);
    public static final Set<BlockState<?>> COMMAND_BLOCK = Set.of(VanillaBlockStates.CONDITIONAL, VanillaBlockStates.FACING_ALL);
    public static final Set<BlockState<?>> CONNECTABLE_ALL = Set.of(VanillaBlockStates.DOWN, VanillaBlockStates.UP, VanillaBlockStates.NORTH, VanillaBlockStates.SOUTH, VanillaBlockStates.EAST, VanillaBlockStates.WEST);
    public static final Set<BlockState<?>> CONNECTABLE_HORIZONTAL = Set.of(VanillaBlockStates.NORTH, VanillaBlockStates.SOUTH, VanillaBlockStates.EAST, VanillaBlockStates.WEST);
    public static final Set<BlockState<?>> COPPER_GOLEM = Set.of(VanillaBlockStates.COPPER_GOLEM_POSE, VanillaBlockStates.FACING_HORIZONTAL);
    public static final Set<BlockState<?>> CORAL_FAN = Set.of(VanillaBlockStates.CORAL_FAN_DIRECTION);
    public static final Set<BlockState<?>> DOOR = Set.of(VanillaBlockStates.OPEN, VanillaBlockStates.POWERED, VanillaBlockStates.HALF, VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.DOOR_HINGE);
    public static final Set<BlockState<?>> FACING_ALL = Set.of(VanillaBlockStates.FACING_ALL);
    public static final Set<BlockState<?>> FACING_ALL_POWERED = Set.of(VanillaBlockStates.FACING_ALL, VanillaBlockStates.POWERED);
    public static final Set<BlockState<?>> FACING_HORIZONTAL = Set.of(VanillaBlockStates.FACING_HORIZONTAL);
    public static final Set<BlockState<?>> FENCE_GATE = Set.of(VanillaBlockStates.POWERED, VanillaBlockStates.OPEN, VanillaBlockStates.IN_WALL, VanillaBlockStates.FACING_HORIZONTAL);
    public static final Set<BlockState<?>> HANGING_SIGN = Set.of(VanillaBlockStates.ATTACHED, VanillaBlockStates.ROTATION);
    public static final Set<BlockState<?>> LANTERN = Set.of(VanillaBlockStates.HANGING);
    public static final Set<BlockState<?>> LEAVES = Set.of(VanillaBlockStates.DISTANCE, VanillaBlockStates.PERSISTENT, VanillaBlockStates.UPDATE);
    public static final Set<BlockState<?>> LIT = Set.of(VanillaBlockStates.LIT);
    public static final Set<BlockState<?>> LIT_FACING_HORIZONTAL = Set.of(VanillaBlockStates.LIT, VanillaBlockStates.FACING_HORIZONTAL);
    public static final Set<BlockState<?>> PISTON = Set.of(VanillaBlockStates.EXTENDED, VanillaBlockStates.FACING_ALL);
    public static final Set<BlockState<?>> POWERED = Set.of(VanillaBlockStates.POWERED);
    public static final Set<BlockState<?>> POWERED_ATTACHABLE = Set.of(VanillaBlockStates.POWERED, VanillaBlockStates.ATTACHMENT_TYPE, VanillaBlockStates.FACING_HORIZONTAL);
    public static final Set<BlockState<?>> REDSTONE_RAIL = Set.of(VanillaBlockStates.POWERED, VanillaBlockStates.RAIL_SHAPE_STRAIGHT);
    public static final Set<BlockState<?>> ROTATION = Set.of(VanillaBlockStates.ROTATION);
    public static final Set<BlockState<?>> SAPLING = Set.of(VanillaBlockStates.STAGE);
    public static final Set<BlockState<?>> SHELF = Set.of(VanillaBlockStates.POWERED, VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.SIDE_CHAIN);
    public static final Set<BlockState<?>> SKULL = Set.of(VanillaBlockStates.POWERED, VanillaBlockStates.NO_DROP, VanillaBlockStates.ROTATION);
    public static final Set<BlockState<?>> SLAB = Set.of(VanillaBlockStates.SLAB_TYPE);
    public static final Set<BlockState<?>> STAIRS = Set.of(VanillaBlockStates.STAIR_SHAPE, VanillaBlockStates.HALF, VanillaBlockStates.FACING_HORIZONTAL);
    public static final Set<BlockState<?>> SUSPICIOUS_BLOCK = Set.of(VanillaBlockStates.DUSTED);
    public static final Set<BlockState<?>> TRAPDOOR = Set.of(VanillaBlockStates.OPEN, VanillaBlockStates.HALF, VanillaBlockStates.POWERED, VanillaBlockStates.FACING_HORIZONTAL);
    public static final Set<BlockState<?>> TRIGGERED_BLOCK = Set.of(VanillaBlockStates.FACING_ALL, VanillaBlockStates.TRIGGERED);
    public static final Set<BlockState<?>> WALL = Set.of(VanillaBlockStates.WALL_NORTH, VanillaBlockStates.WALL_EAST, VanillaBlockStates.WALL_SOUTH, VanillaBlockStates.WALL_WEST, VanillaBlockStates.UP);
    public static final Set<BlockState<?>> WALL_HANGING_SIGN = Set.of(VanillaBlockStates.ATTACHED, VanillaBlockStates.FACING_HORIZONTAL);
    public static final Set<BlockState<?>> WALL_SKULL = Set.of(VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.POWERED, VanillaBlockStates.NO_DROP);
}
