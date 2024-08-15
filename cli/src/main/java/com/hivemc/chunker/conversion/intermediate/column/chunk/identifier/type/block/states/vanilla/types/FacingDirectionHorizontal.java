package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The direction that this block faces (only horizontal directions also known as cardinal directions).
 */
public enum FacingDirectionHorizontal implements BlockStateValue {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    private final int x;
    private final int z;

    FacingDirectionHorizontal(int x, int z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Get the facing direction from a 2D byte.
     *
     * @param direction the byte representation for this in the order SOUTH, WEST, NORTH, EAST.
     */
    public static FacingDirectionHorizontal from2DByte(byte direction) {
        return switch (direction % 4) {
            case 0 -> FacingDirectionHorizontal.SOUTH;
            case 1 -> FacingDirectionHorizontal.WEST;
            case 2 -> FacingDirectionHorizontal.NORTH;
            case 3 -> FacingDirectionHorizontal.EAST;
            default -> FacingDirectionHorizontal.NORTH;
        };
    }

    /**
     * Get the difference in the X axis for this direction.
     *
     * @return the difference in co-ordinates to move to go in this direction.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the difference in the Z axis for this direction.
     *
     * @return the difference in co-ordinates to move to go in this direction.
     */
    public int getZ() {
        return z;
    }

    /**
     * Rotate this direction clockwise.
     *
     * @return the clockwise rotated state.
     */
    public FacingDirectionHorizontal rotateClockwise() {
        return switch (this) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case EAST -> SOUTH;
        };
    }

    /**
     * Rotate this direction anti-clockwise.
     *
     * @return the anti-clockwise rotated state.
     */
    public FacingDirectionHorizontal rotateAntiClockwise() {
        return switch (this) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            case EAST -> NORTH;
        };
    }

    /**
     * Check whether another face is next to this one.
     *
     * @param input the other face.
     * @return true if it is next to it, e.g. adjacent to NORTH is EAST and WEST.
     */
    public boolean isAdjacent(FacingDirectionHorizontal input) {
        int ordinalDiff = Math.abs(ordinal() - input.ordinal());
        return ordinalDiff == 1 || ordinalDiff == 3;
    }

    /**
     * Convert this direction to a pre-transform handler direction.
     *
     * @return the direction equivalent.
     */
    public BlockPreTransformHandler.Direction asDirection() {
        return switch (this) {
            case NORTH -> BlockPreTransformHandler.Direction.NORTH;
            case EAST -> BlockPreTransformHandler.Direction.EAST;
            case SOUTH -> BlockPreTransformHandler.Direction.SOUTH;
            case WEST -> BlockPreTransformHandler.Direction.WEST;
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
        };
    }
}
