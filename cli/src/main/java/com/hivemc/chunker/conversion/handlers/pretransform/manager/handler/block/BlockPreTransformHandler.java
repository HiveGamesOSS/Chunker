package com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirection;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirectionHorizontal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public interface BlockPreTransformHandler {
    /**
     * Get the edges which are required to run the pre-transform handler.
     *
     * @param column          the column being pre-transformed.
     * @param x               the block X.
     * @param y               the block Y.
     * @param z               the block Z.
     * @param blockIdentifier the identifier of the block.
     * @return a set of the edges, if it is empty the handler is run immediately otherwise it is queued for when the
     * neighbours are loaded.
     */
    Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier);

    /**
     * Handle the pre-transformation for the entity.
     *
     * @param column          the column being pre-transformed.
     * @param neighbours      the neighbouring chunks, note: not all required columns may be present if they do not exist or
     *                        if this was called immediately and preTransform isn't enabled.
     * @param x               the block X.
     * @param y               the block Y.
     * @param z               the block Z.
     * @param blockIdentifier the identifier of the block.
     * @return the new identifier to use for the block.
     */
    ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier);

    /**
     * Get a relative block.
     *
     * @param column     the column being pre-transformed.
     * @param neighbours the neighbours which are present.
     * @param x          the origin block X.
     * @param y          the origin block Y.
     * @param z          the origin block Z.
     * @param direction  the direction in which to get the block.
     * @return the block or air if it couldn't be found.
     */
    default ChunkerBlockIdentifier getRelative(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, Direction direction) {
        ChunkerColumn target;
        if (direction.getEdge() != null) {
            int newChunkX = (x + direction.getEdge().getX()) >> 4;
            int newChunkZ = (z + direction.getEdge().getZ()) >> 4;

            // The edge is required if the chunk ZX differs
            if (newChunkX != column.getPosition().chunkX() || newChunkZ != column.getPosition().chunkZ()) {
                target = neighbours.get(direction.getEdge());
                if (target == null) return ChunkerBlockIdentifier.AIR; // Return air if the edge isn't present

                // Safety check to ensure that it is the target
                if (newChunkX != target.getPosition().chunkX() || newChunkZ != target.getPosition().chunkZ()) {
                    throw new IllegalArgumentException("Relative request is outside of adjacent chunks!");
                }
            } else {
                target = column; // Same chunk
            }
        } else {
            target = column; // Same chunk
        }
        return target.getBlock(x + direction.getX(), y + direction.getY(), z + direction.getZ());
    }

    /**
     * Get a relative block entity.
     *
     * @param column     the column being pre-transformed.
     * @param neighbours the neighbours which are present.
     * @param x          the origin block X.
     * @param y          the origin block Y.
     * @param z          the origin block Z.
     * @param direction  the direction in which to get the block.
     * @return the block entity or null if it couldn't be found.
     */
    @Nullable
    default BlockEntity getRelativeBlockEntity(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, Direction direction) {
        ChunkerColumn target;
        if (direction.getEdge() != null) {
            int newChunkX = (x + direction.getEdge().getX()) >> 4;
            int newChunkZ = (z + direction.getEdge().getZ()) >> 4;

            // The edge is required if the chunk ZX differs
            if (newChunkX != column.getPosition().chunkX() || newChunkZ != column.getPosition().chunkZ()) {
                target = neighbours.get(direction.getEdge());
                if (target == null) return null; // Return null if the edge isn't present

                // Safety check to ensure that it is the target
                if (newChunkX != target.getPosition().chunkX() || newChunkZ != target.getPosition().chunkZ()) {
                    throw new IllegalArgumentException("Relative request is outside of adjacent chunks!");
                }
            } else {
                target = column; // Same chunk
            }
        } else {
            target = column; // Same chunk
        }
        return target.getBlockEntity(x + direction.getX(), y + direction.getY(), z + direction.getZ());
    }

    /**
     * Calculate what edges are needed based on relative directions from a block.
     *
     * @param x          the block X.
     * @param y          the block Y.
     * @param z          the block Z.
     * @param directions the directions which are needed.
     * @return a set of edges which are needed or empty if no neighbours are needed.
     */
    default Set<Edge> calculateEdges(int x, int y, int z, Direction... directions) {
        EnumSet<Edge> set = EnumSet.noneOf(Edge.class);
        int originalChunkX = x >> 4;
        int originalChunkZ = z >> 4;

        for (Direction direction : directions) {
            if (direction.getEdge() == null) continue; // Skip if there is no edge linked to the direction
            int newChunkX = (x + direction.getEdge().getX()) >> 4;
            int newChunkZ = (z + direction.getEdge().getZ()) >> 4;

            // The edge is required if the chunk ZX differs
            if (newChunkX != originalChunkX || newChunkZ != originalChunkZ) {
                set.add(direction.getEdge());
            }
        }
        return set;
    }

    /**
     * A relative direction from one block to another.
     */
    enum Direction {
        NORTH(Edge.NEGATIVE_Z),
        EAST(Edge.POSITIVE_X),
        SOUTH(Edge.POSITIVE_Z),
        WEST(Edge.NEGATIVE_X),
        UP(1),
        DOWN(-1);

        /**
         * Every direction.
         */
        public static final Direction[] ALL = new Direction[]{
                NORTH, EAST, SOUTH, WEST, UP, DOWN
        };

        /**
         * All horizontal directions.
         */
        public static final Direction[] ALL_HORIZONTAL = new Direction[]{
                NORTH, EAST, SOUTH, WEST
        };
        /**
         * East / West directions.
         */
        public static final Direction[] DIRECTION_EAST_WEST = new Direction[]{Direction.EAST, Direction.WEST};
        /**
         * North / South directions.
         */
        public static final Direction[] DIRECTION_NORTH_SOUTH = new Direction[]{Direction.NORTH, Direction.SOUTH};
        private final int x;
        private final int y;
        private final int z;
        private final Edge edge;

        Direction(Edge edge) {
            x = edge.getX();
            y = 0;
            z = edge.getZ();
            this.edge = edge;
        }

        Direction(int y) {
            x = 0;
            this.y = y;
            z = 0;
            edge = null;
        }

        /**
         * Get the adjacent faces to a direction.
         *
         * @param direction the direction.
         * @return the two adjacent faces.
         */
        public static Direction[] getAdjacentFaces(FacingDirectionHorizontal direction) {
            if (direction == FacingDirectionHorizontal.NORTH || direction == FacingDirectionHorizontal.SOUTH) {
                return DIRECTION_EAST_WEST;
            } else {
                return DIRECTION_NORTH_SOUTH;
            }
        }

        /**
         * Get the edge required for this direction.
         *
         * @return the edge or null if it's up/down.
         */
        @Nullable
        public Edge getEdge() {
            return edge;
        }

        /**
         * The X offset for this direction.
         *
         * @return the offset (-1 to 1).
         */
        public int getX() {
            return x;
        }

        /**
         * The Y offset for this direction.
         *
         * @return the offset (-1 to 1).
         */
        public int getY() {
            return y;
        }

        /**
         * The Z offset for this direction.
         *
         * @return the offset (-1 to 1).
         */
        public int getZ() {
            return z;
        }

        /**
         * Get the direction as a horizontal facing direction.
         *
         * @return the direction or an exception if it's not horizontal.
         */
        public FacingDirectionHorizontal asFacingDirectionHorizontal() {
            return switch (this) {
                case NORTH -> FacingDirectionHorizontal.NORTH;
                case EAST -> FacingDirectionHorizontal.EAST;
                case SOUTH -> FacingDirectionHorizontal.SOUTH;
                case WEST -> FacingDirectionHorizontal.WEST;
                default -> throw new IllegalArgumentException("Unable to get facing direction horizontal");
            };
        }

        /**
         * Get the direction as a facing direction.
         *
         * @return the direction.
         */
        public FacingDirection asFacingDirection() {
            return switch (this) {
                case NORTH -> FacingDirection.NORTH;
                case EAST -> FacingDirection.EAST;
                case SOUTH -> FacingDirection.SOUTH;
                case WEST -> FacingDirection.WEST;
                case UP -> FacingDirection.UP;
                case DOWN -> FacingDirection.DOWN;
            };
        }

        /**
         * Get the opposite face.
         *
         * @return the opposite direction.
         */
        public Direction getOpposite() {
            return switch (this) {
                case NORTH -> SOUTH;
                case EAST -> WEST;
                case SOUTH -> NORTH;
                case WEST -> EAST;
                case UP -> DOWN;
                case DOWN -> UP;
            };
        }
    }
}
