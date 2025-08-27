package com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.legacy;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.legacy.handlers.*;
import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.ConnectableBlockPreTransformHandler;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.HorizontalConnectableBlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockGroups;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirectionHorizontal;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Java Legacy Pre-Transform Manager which is called after chunks are read. It handles inferring block states based on
 * neighbouring blocks. It is called in the postProcess method of the Java reader.
 * It only needs to handle connections for blocks present in 1.12.2 and below.
 */
public class JavaLegacyReaderPreTransformManager extends PreTransformManager {
    /**
     * A list of legacy tall plants used for connecting.
     */
    public static final Set<ChunkerBlockType> TALL_PLANTS = Set.of(
            ChunkerVanillaBlockType.SUNFLOWER,
            ChunkerVanillaBlockType.LILAC,
            ChunkerVanillaBlockType.TALL_GRASS,
            ChunkerVanillaBlockType.LARGE_FERN,
            ChunkerVanillaBlockType.ROSE_BUSH,
            ChunkerVanillaBlockType.PEONY
    );

    /**
     * Create a new pre-transform manager used for reading.
     *
     * @param version the input version.
     */
    public JavaLegacyReaderPreTransformManager(Version version) {
        super(version);
    }

    @Override
    public void registerHandlers(Version version) {
        // Chest connector
        registerHandler(
                new JavaLegacyChestPreTransformHandler(),
                ChunkerVanillaBlockType.CHEST,
                ChunkerVanillaBlockType.TRAPPED_CHEST
        );

        // Tall grass needs connecting
        registerHandler(new JavaLegacyTallPlantsPreTransformHandler(), TALL_PLANTS);

        // Pumpkin stems need connecting
        registerHandler(new JavaLegacyStemPreTransformHandler(
                        Set.of(ChunkerVanillaBlockType.MELON)),
                ChunkerVanillaBlockType.ATTACHED_MELON_STEM
        );
        registerHandler(new JavaLegacyStemPreTransformHandler(
                        Set.of(ChunkerVanillaBlockType.PUMPKIN)),
                ChunkerVanillaBlockType.ATTACHED_PUMPKIN_STEM
        );

        // Trip wire handler
        registerHandler(new HorizontalConnectableBlockPreTransformHandler() {
            public boolean canConnect(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative) {
                return relative.getType().equals(ChunkerVanillaBlockType.TRIPWIRE)
                        || relative.getType().equals(ChunkerVanillaBlockType.TRIPWIRE_HOOK);
            }
        }, ChunkerVanillaBlockType.TRIPWIRE);

        // Fence handler
        registerHandler(new HorizontalConnectableBlockPreTransformHandler() {
            public boolean canConnect(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative) {
                if (ChunkerVanillaBlockGroups.FENCE_GATES.contains(relative.getType())) {
                    // If it's a fence gate, the fence connects to either side of it
                    FacingDirectionHorizontal facing = relative.getState(VanillaBlockStates.FACING_HORIZONTAL);
                    return Objects.requireNonNull(facing).isAdjacent(direction.asFacingDirectionHorizontal());
                }
                return ChunkerVanillaBlockGroups.WOODEN_FENCES.contains(relative.getType()) || relative.getType().isAllFacesSolid();
            }
        }, ChunkerVanillaBlockGroups.WOODEN_FENCES);

        // Nether Brick Fence handler
        registerHandler(new HorizontalConnectableBlockPreTransformHandler() {
            public boolean canConnect(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative) {
                if (ChunkerVanillaBlockGroups.FENCE_GATES.contains(relative.getType())) {
                    // If it's a fence gate, the fence connects to either side of it
                    FacingDirectionHorizontal facing = relative.getState(VanillaBlockStates.FACING_HORIZONTAL);
                    return Objects.requireNonNull(facing).isAdjacent(direction.asFacingDirectionHorizontal());
                }
                return relative.getType().equals(ChunkerVanillaBlockType.NETHER_BRICK_FENCE) || relative.getType().isAllFacesSolid();
            }
        }, ChunkerVanillaBlockType.NETHER_BRICK_FENCE);

        // Fence gate handler
        registerHandler(new JavaLegacyFenceGatePreTransformHandler(), ChunkerVanillaBlockGroups.FENCE_GATES);

        // Glass Pane / Iron Bar handler
        registerHandler(new HorizontalConnectableBlockPreTransformHandler() {
            public boolean canConnect(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative) {
                return ChunkerVanillaBlockGroups.BARS_AND_GLASS_PANES.contains(relative.getType()) || ChunkerVanillaBlockGroups.WALLS.contains(relative.getType()) || relative.getType().isAllFacesSolid();
            }
        }, ChunkerVanillaBlockGroups.BARS_AND_GLASS_PANES);

        // Trip Wire handler
        registerHandler(new HorizontalConnectableBlockPreTransformHandler() {
            public boolean canConnect(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative) {
                return relative.getType().equals(source.getType()) || relative.getType().equals(ChunkerVanillaBlockType.TRIPWIRE_HOOK);
            }
        }, ChunkerVanillaBlockType.TRIPWIRE);

        // Chorus Plant handler
        registerHandler(new ConnectableBlockPreTransformHandler() {
            public boolean canConnect(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative) {
                return relative.getType().equals(source.getType()) ||
                        relative.getType().equals(ChunkerVanillaBlockType.CHORUS_FLOWER) ||
                        direction == Direction.DOWN && relative.getType().equals(ChunkerVanillaBlockType.END_STONE);
            }
        }, ChunkerVanillaBlockType.CHORUS_PLANT);

        // Walls handler (adds post aka up and wall height on older versions)
        registerHandler(new JavaLegacyWallPreTransformHandler(), ChunkerVanillaBlockGroups.WALLS);

        // Snowy Grass Blocks (look for snow above the block)
        registerHandler(new BlockPreTransformHandler() {
            @Override
            public Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
                return Collections.emptySet(); // Only the block above is needed
            }

            @Override
            public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
                // Get the block above
                ChunkerBlockIdentifier aboveIdentifier = column.getBlock(x, y + 1, z);
                if (ChunkerVanillaBlockGroups.SNOWY_BLOCKS.contains(aboveIdentifier.getType())) {
                    blockIdentifier = blockIdentifier.copyWith(VanillaBlockStates.SNOWY, Bool.TRUE);
                } else {
                    blockIdentifier = blockIdentifier.copyWith(VanillaBlockStates.SNOWY, Bool.FALSE);
                }
                return blockIdentifier;
            }
        }, ChunkerVanillaBlockGroups.SNOWY_GRASS_BLOCKS);


        // Doors (Adds the other states)
        registerHandler(new JavaLegacyDoorPreTransformHandler(), ChunkerVanillaBlockGroups.DOORS);

        // Stairs (Adds the shape)
        registerHandler(new JavaLegacyStairShapePreTransformHandler(), ChunkerVanillaBlockGroups.STAIRS);

        // Redstone (Wire direction)
        registerHandler(new JavaLegacyRedstonePreTransformHandler(), ChunkerVanillaBlockType.REDSTONE_WIRE);

        // Note block (Instrument)
        registerHandler(new JavaLegacyNoteBlockPreTransformHandler(), ChunkerVanillaBlockType.NOTE_BLOCK);

        // Fire handler (connects to blocks)
        registerHandler(new JavaLegacyFirePreTransformHandler(), ChunkerVanillaBlockType.FIRE);
    }

}
