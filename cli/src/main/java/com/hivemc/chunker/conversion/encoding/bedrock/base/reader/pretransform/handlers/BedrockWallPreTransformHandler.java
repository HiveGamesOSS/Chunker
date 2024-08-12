package com.hivemc.chunker.conversion.encoding.bedrock.base.reader.pretransform.handlers;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockGroups;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.WallHeight;

import java.util.Map;
import java.util.Set;

/**
 * Calculate the height of the wall based on neighbouring walls and if there needs to be a post above the wall.
 */
public class BedrockWallPreTransformHandler implements BlockPreTransformHandler {
    public static final Map<Direction, BlockState<WallHeight>> DIRECTION_TO_STATE = Map.of(
            Direction.NORTH, VanillaBlockStates.WALL_NORTH,
            Direction.EAST, VanillaBlockStates.WALL_EAST,
            Direction.SOUTH, VanillaBlockStates.WALL_SOUTH,
            Direction.WEST, VanillaBlockStates.WALL_WEST
    );

    private final boolean needsWallConnections;

    /**
     * Create a new wall pre-transform handler.
     *
     * @param version the version (only post bit is needed on newer versions to be calculated).
     */
    public BedrockWallPreTransformHandler(Version version) {
        needsWallConnections = version.isLessThan(1, 16, 0);
    }

    @Override
    public Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        // We need to check connections based on version
        return needsWallConnections ? calculateEdges(x, y, z, Direction.ALL) : calculateEdges(x, y, z, Direction.UP);
    }

    @Override
    public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        ChunkerBlockIdentifier above = getRelative(column, neighbours, x, y, z, Direction.UP);

        // Connect the sides if they're needed
        if (needsWallConnections) {
            for (Direction direction : Direction.ALL_HORIZONTAL) {
                ChunkerBlockIdentifier relative = getRelative(column, neighbours, x, y, z, direction);
                ChunkerBlockIdentifier relativeAbove = getRelative(column, neighbours, x, y + 1, z, direction);

                // Update the state with the connection status
                BlockState<WallHeight> state = DIRECTION_TO_STATE.get(direction);
                WallHeight wallHeight = calculateWallHeight(blockIdentifier, above, direction, relative, relativeAbove);
                blockIdentifier = blockIdentifier.copyWith(state, wallHeight);
            }
        }

        // Connect post
        boolean post = canConnectPost(blockIdentifier, Direction.UP, above);

        // Update the state with the connection status
        blockIdentifier = blockIdentifier.copyWith(VanillaBlockStates.UP, post ? Bool.TRUE : Bool.FALSE);

        // Return the block identifier to be updated
        return blockIdentifier;
    }

    public WallHeight calculateWallHeight(ChunkerBlockIdentifier source, ChunkerBlockIdentifier above, Direction direction, ChunkerBlockIdentifier relative, ChunkerBlockIdentifier relativeAbove) {
        // Check for tall walls
        if (ChunkerVanillaBlockGroups.WALLS.contains(relative.getType())) {
            // Use tall if this is multiple walls
            return ChunkerVanillaBlockGroups.WALLS.contains(relativeAbove.getType())
                    && ChunkerVanillaBlockGroups.WALLS.contains(above.getType()) ? WallHeight.TALL : WallHeight.LOW;
        }

        // Connects to solids
        if (relative.getType().isAllFacesSolid()) return WallHeight.LOW;

        // Special case for iron bar like blocks
        if (ChunkerVanillaBlockGroups.IRON_BARS_AND_GLASS_PANES.contains(relative.getType())) return WallHeight.LOW;
        if (ChunkerVanillaBlockGroups.FENCE_GATES.contains(relative.getType())) return WallHeight.LOW;

        // Otherwise, don't connect
        return WallHeight.NONE;
    }

    public boolean canConnectPost(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative) {
        // Special logic for the posts on walls, if north && south || east && west || block above can connect
        int north = source.getState(VanillaBlockStates.WALL_NORTH) == WallHeight.NONE ? 0 : 1;
        int east = source.getState(VanillaBlockStates.WALL_EAST) == WallHeight.NONE ? 0 : 1;
        int south = source.getState(VanillaBlockStates.WALL_SOUTH) == WallHeight.NONE ? 0 : 1;
        int west = source.getState(VanillaBlockStates.WALL_WEST) == WallHeight.NONE ? 0 : 1;
        if (north + east + south + west != 2) return true; // Post will always show on other than 2 connections

        if (!(north == 1 && south == 1 || east == 1 && west == 1)) {
            return true; // Posts are present if not straight
        }

        // Otherwise use the relative block
        return ChunkerVanillaBlockGroups.WALLS.contains(relative.getType()) ||
                ChunkerVanillaBlockGroups.WOODEN_FENCES.contains(relative.getType()) ||
                relative.getType().isAllFacesSolid();
    }
}
