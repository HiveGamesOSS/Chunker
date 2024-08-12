package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The direction that this block faces (any of the 6 directions).
 */
public enum FacingDirection implements BlockStateValue {
    DOWN,
    UP,
    NORTH,
    SOUTH,
    WEST,
    EAST;

    /**
     * Get the facing direction from a 3D byte.
     *
     * @param direction the byte representation for this in the order DOWN, UP, NORTH, SOUTH, WEST, EAST.
     */
    public static FacingDirection from3DByte(byte direction) {
        return switch (direction % 6) {
            case 0 -> FacingDirection.DOWN;
            case 1 -> FacingDirection.UP;
            case 2 -> FacingDirection.NORTH;
            case 3 -> FacingDirection.SOUTH;
            case 4 -> FacingDirection.WEST;
            case 5 -> FacingDirection.EAST;
            default -> FacingDirection.NORTH;
        };
    }

    /**
     * Get the facing direction from a 2D byte.
     *
     * @param direction the byte representation for this in the order SOUTH, WEST, NORTH, EAST.
     */
    public static FacingDirection from2DByte(byte direction) {
        return switch (direction % 4) {
            case 0 -> FacingDirection.SOUTH;
            case 1 -> FacingDirection.WEST;
            case 2 -> FacingDirection.NORTH;
            case 3 -> FacingDirection.EAST;
            default -> FacingDirection.NORTH;
        };
    }

    /**
     * Get the facing direction as a 3D byte.
     *
     * @return the byte representation for this in the order DOWN, UP, NORTH, SOUTH, WEST, EAST.
     */
    public byte to3DByte() {
        return switch (this) {
            case SOUTH -> 3;
            case WEST -> 4;
            case NORTH -> 2;
            case EAST -> 5;
            case UP -> 1;
            case DOWN -> 0;
        };
    }

    /**
     * Get the facing direction as a 2D byte.
     *
     * @return the byte representation for this in the order SOUTH, WEST, NORTH, EAST.
     */
    public byte to2DByte() {
        return switch (this) {
            case SOUTH -> (byte) 0;
            case WEST -> (byte) 1;
            case NORTH -> (byte) 2;
            case EAST -> (byte) 3;
            default -> (byte) 0;
        };
    }
}
