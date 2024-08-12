package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy;

import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateMappingGroup;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy.JavaLegacyStateTypes;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.*;

import java.util.List;

/**
 * A list of groups of states used to convert between Chunker and legacy Bedrock.
 */
public class BedrockLegacyStateGroups {
    public static final StateMappingGroup AGE_3 = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_3, BedrockLegacyStateTypes.AGE_3)
            .build();
    public static final StateMappingGroup BAMBOO = new StateMappingGroup.Builder()
            .multiState("data", List.of(
                    VanillaBlockStates.AGE_1,
                    VanillaBlockStates.BAMBOO_LEAVES,
                    VanillaBlockStates.STAGE
            ), BedrockLegacyStateTypes.BAMBOO)
            .build();
    public static final StateMappingGroup BAMBOO_SAPLING = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_1, BedrockLegacyStateTypes.BAMBOO_SAPLING)
            .build();
    public static final StateMappingGroup BANNER = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.ROTATION, BedrockLegacyStateTypes.ROTATION)
            .build();
    public static final StateMappingGroup BARREL = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_ALL, VanillaBlockStates.OPEN, BedrockLegacyStateTypes.FACING_DIRECTION_BOOL)
            .build();
    public static final StateMappingGroup BED = new StateMappingGroup.Builder()
            .multiState("data", List.of(
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.OCCUPIED,
                    VanillaBlockStates.BED_PART
            ), BedrockLegacyStateTypes.BED)
            .build();
    public static final StateMappingGroup BEDROCK = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.INFINIBURN, BedrockLegacyStateTypes.BOOL)
            .build();
    public static final StateMappingGroup BELL = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.BELL_ATTACHMENT, BedrockLegacyStateTypes.BELL)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final StateMappingGroup BREWING_STAND = new StateMappingGroup.Builder()
            .multiState(
                    "data",
                    List.of(VanillaBlockStates.HAS_BOTTLE_0, VanillaBlockStates.HAS_BOTTLE_1, VanillaBlockStates.HAS_BOTTLE_2),
                    BedrockLegacyStateTypes.BREWING_STAND
            )
            .build();
    public static final StateMappingGroup BUBBLE_COLUMN = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.DRAG, BedrockLegacyStateTypes.BOOL)
            .build();
    public static final StateMappingGroup BUTTON = new StateMappingGroup.Builder()
            .multiState(
                    "data",
                    List.of(
                            VanillaBlockStates.ATTACHMENT_TYPE,
                            VanillaBlockStates.FACING_HORIZONTAL,
                            VanillaBlockStates.POWERED
                    ),
                    BedrockLegacyStateTypes.FACING_DIRECTION_TO_ATTACHMENT_FACING_DIRECTION
            )
            .build();
    public static final StateMappingGroup CAKE = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.BITES, BedrockLegacyStateTypes.BITES)
            .build();
    public static final StateMappingGroup CAMPFIRE = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.LIT, BedrockLegacyStateTypes.CARDINAL_DIRECTION_LEGACY_BOOL_INVERSE)
            .defaultOutput(VanillaBlockStates.SIGNAL_FIRE, Bool.FALSE)
            .build();
    public static final StateMappingGroup CARDINAL_DIRECTION = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, BedrockLegacyStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
            .build();
    public static final StateMappingGroup CHEST = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, BedrockLegacyStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
            .defaultOutput(VanillaBlockStates.CHEST_TYPE, ChestType.SINGLE)
            .build();
    public static final StateMappingGroup CHORUS_FLOWER = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_5, BedrockLegacyStateTypes.AGE_5)
            .build();
    public static final StateMappingGroup CHORUS_PLANT = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.UP, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.DOWN, Bool.FALSE)
            .build();
    public static final StateMappingGroup COCOA = new StateMappingGroup.Builder()
            .combineOutputStates(
                    "data",
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.AGE_2,
                    BedrockLegacyStateTypes.COCOA
            )
            .build();
    public static final StateMappingGroup COMMAND_BLOCK = new StateMappingGroup.Builder()
            .combineOutputStates(
                    "data",
                    VanillaBlockStates.FACING_ALL,
                    VanillaBlockStates.CONDITIONAL,
                    BedrockLegacyStateTypes.FACING_DIRECTION_BOOL
            )
            .build();
    public static final StateMappingGroup COMPARATOR = new StateMappingGroup.Builder()
            .multiState(
                    "data",
                    List.of(
                            VanillaBlockStates.FACING_HORIZONTAL,
                            VanillaBlockStates.MODE_COMPARATOR,
                            VanillaBlockStates.POWERED
                    ),
                    BedrockLegacyStateTypes.COMPARATOR
            )
            .build();
    public static final StateMappingGroup COMPOSTER = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.COMPOSTER_LEVEL, BedrockLegacyStateTypes.COMPOSTER_LEVEL)
            .build();
    public static final StateMappingGroup CONNECTABLE_HORIZONTAL = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .build();
    public static final StateMappingGroup CROP = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_7, BedrockLegacyStateTypes.AGE_7)
            .build();
    public static final StateMappingGroup DAYLIGHT_DETECTOR = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.POWER, BedrockLegacyStateTypes.POWER)
            .build();
    public static final StateMappingGroup DEFAULT_AXIS = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.AXIS, Axis.Y)
            .build();
    public static final StateMappingGroup DOOR = new StateMappingGroup.Builder()
            .multiState("data", List.of(
                    VanillaBlockStates.DOOR_HINGE,
                    VanillaBlockStates.HALF,
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.OPEN
            ), BedrockLegacyStateTypes.DOOR)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final StateMappingGroup ENDER_CHEST = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, BedrockLegacyStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
            .build();
    public static final StateMappingGroup END_PORTAL_FRAME = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.EYE, BedrockLegacyStateTypes.CARDINAL_DIRECTION_LEGACY_BOOL)
            .build();
    public static final StateMappingGroup END_ROD = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_ALL, BedrockLegacyStateTypes.FLIPPED_FACING_DIRECTION)
            .build();
    public static final StateMappingGroup FACING_DIRECTION_HORIZONTAL = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, BedrockLegacyStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
            .build();
    public static final StateMappingGroup FACING_DIRECTION_TRIGGERED = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_ALL, VanillaBlockStates.TRIGGERED, BedrockLegacyStateTypes.FACING_DIRECTION_BOOL)
            .build();
    public static final StateMappingGroup FARMLAND = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.MOISTURE, BedrockLegacyStateTypes.MOISTURE)
            .build();
    public static final StateMappingGroup FENCE_GATE = new StateMappingGroup.Builder()
            .multiState("data", List.of(
                    VanillaBlockStates.IN_WALL,
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.OPEN
            ), BedrockLegacyStateTypes.FENCE_GATE)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final StateMappingGroup FIRE = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_15, BedrockLegacyStateTypes.AGE_15)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.UP, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .build();
    public static final StateMappingGroup FRAME = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_ALL, VanillaBlockStates.BEDROCK_FRAME_MAP_BIT, BedrockLegacyStateTypes.FRAME)
            .defaultOutput(VanillaBlockStates.BEDROCK_FRAME_PHOTO_BIT, Bool.FALSE)
            .build();
    public static final StateMappingGroup GRINDSTONE = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.GRINDSTONE_ATTACHMENT_TYPE, BedrockLegacyStateTypes.GRINDSTONE)
            .build();
    public static final StateMappingGroup GROWTH = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_7, BedrockLegacyStateTypes.AGE_7)
            .build();
    public static final StateMappingGroup HOPPER = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_HORIZONTAL_DOWN, VanillaBlockStates.ENABLED, BedrockLegacyStateTypes.HOPPER)
            .build();
    public static final StateMappingGroup JIGSAW = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.ORIENTATION, BedrockLegacyStateTypes.FACING_DIRECTION_TO_ORIENTATION)
            .build();
    public static final StateMappingGroup JUKEBOX = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.HAS_RECORD, Bool.FALSE)
            .build();
    public static final StateMappingGroup KELP = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_25, BedrockLegacyStateTypes.AGE_15_TO_25)
            .build();
    public static final StateMappingGroup LANTERN = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.HANGING, BedrockLegacyStateTypes.BOOL)
            .build();
    public static final StateMappingGroup LAYER_BLOCK = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.LAYERS, BedrockLegacyStateTypes.LAYERS)
            .build();
    public static final StateMappingGroup LEAVES = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.DISTANCE, Distance._1)
            .build();
    public static final StateMappingGroup LECTERN = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.POWERED, BedrockLegacyStateTypes.CARDINAL_DIRECTION_LEGACY_BOOL)
            .defaultOutput(VanillaBlockStates.HAS_BOOK, Bool.FALSE)
            .build();
    public static final StateMappingGroup LEVER = new StateMappingGroup.Builder()
            .multiState(
                    "data",
                    List.of(VanillaBlockStates.ATTACHMENT_TYPE, VanillaBlockStates.POWERED, VanillaBlockStates.FACING_HORIZONTAL),
                    BedrockLegacyStateTypes.LEVER
            )
            .build();
    public static final StateMappingGroup LIQUID = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.LIQUID_LEVEL, BedrockLegacyStateTypes.FLOWING_LEVEL)
            .build();
    public static final StateMappingGroup LOOM = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, BedrockLegacyStateTypes.CARDINAL_DIRECTION_LEGACY)
            .build();
    public static final StateMappingGroup MUSHROOM_BLOCK = new StateMappingGroup.Builder()
            .multiState(
                    "data",
                    List.of(
                            VanillaBlockStates.NORTH,
                            VanillaBlockStates.EAST,
                            VanillaBlockStates.SOUTH,
                            VanillaBlockStates.WEST,
                            VanillaBlockStates.UP,
                            VanillaBlockStates.DOWN
                    ),
                    BedrockLegacyStateTypes.MUSHROOM_FACE_BITS
            )
            // Default states of all present in the case this is an item and doesn't have data
            .defaultInput("data", 14)
            .defaultOutput(VanillaBlockStates.NORTH, Bool.TRUE)
            .defaultOutput(VanillaBlockStates.EAST, Bool.TRUE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.TRUE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.TRUE)
            .defaultOutput(VanillaBlockStates.UP, Bool.TRUE)
            .defaultOutput(VanillaBlockStates.DOWN, Bool.TRUE)
            .build();
    public static final StateMappingGroup NOTE_BLOCK = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.NOTE, Note._0)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.NOTE_BLOCK_INSTRUMENT, NoteBlockInstrument.HARP)
            .build();
    public static final StateMappingGroup OBSERVER = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_ALL, VanillaBlockStates.POWERED, BedrockLegacyStateTypes.FACING_DIRECTION_BOOL)
            .build();
    public static final StateMappingGroup PILLAR_BLOCK = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AXIS, BedrockLegacyStateTypes.AXIS)
            .build();
    public static final StateMappingGroup PILLAR_BLOCK_DEPRECATED = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AXIS, BedrockLegacyStateTypes.AXIS_DEPRECATED)
            .build();
    public static final StateMappingGroup PISTON = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_ALL, BedrockLegacyStateTypes.FLIPPED_FACING_DIRECTION)
            .defaultOutput(VanillaBlockStates.EXTENDED, Bool.FALSE)
            .build();
    public static final StateMappingGroup PISTON_HEAD = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_ALL, BedrockLegacyStateTypes.FLIPPED_FACING_DIRECTION)
            .defaultOutput(VanillaBlockStates.SHORT, Bool.FALSE)
            .build();
    public static final StateMappingGroup PORTAL = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AXIS_HORIZONTAL, BedrockLegacyStateTypes.AXIS_HORIZONTAL)
            .build();
    public static final StateMappingGroup POWERED_RAIL = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.RAIL_SHAPE_STRAIGHT, VanillaBlockStates.POWERED, BedrockLegacyStateTypes.POWERED_RAIL)
            .build();
    public static final StateMappingGroup PRESSURE_PLATE = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.POWERED, BedrockLegacyStateTypes.REDSTONE_SIGNAL_TO_BOOL)
            .build();
    public static final StateMappingGroup PUMPKIN = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, BedrockLegacyStateTypes.CARDINAL_DIRECTION_LEGACY)
            .build();
    public static final StateMappingGroup RAIL = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.RAIL_SHAPE, BedrockLegacyStateTypes.RAIL_SHAPE)
            .build();
    public static final StateMappingGroup REPEATER = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.DELAY, BedrockLegacyStateTypes.REPEATER)
            .defaultOutput(VanillaBlockStates.LOCKED, Bool.FALSE)
            .build();
    public static final StateMappingGroup SCAFFOLDING = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.STABILITY_DISTANCE, VanillaBlockStates.BOTTOM, BedrockLegacyStateTypes.SCAFFOLDING)
            .build();
    public static final StateMappingGroup SEA_PICKLE = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.PICKLES, VanillaBlockStates.DEAD, BedrockLegacyStateTypes.SEA_PICKLE)
            .build();
    public static final StateMappingGroup SHULKER_BOX = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.FACING_ALL, FacingDirection.UP)
            .build();
    public static final StateMappingGroup SIGN = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.ROTATION, BedrockLegacyStateTypes.ROTATION)
            .build();
    public static final StateMappingGroup SKULL = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.ROTATION, Rotation._0)
            .build();
    public static final StateMappingGroup SNOWY_BLOCK = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.SNOWY, Bool.FALSE)
            .build();
    public static final StateMappingGroup STAIRS = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.HALF, BedrockLegacyStateTypes.STAIRS)
            .defaultOutput(VanillaBlockStates.STAIR_SHAPE, StairShape.STRAIGHT)
            .build();
    public static final StateMappingGroup STEM_FACING = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.FACING_HORIZONTAL, FacingDirectionHorizontal.NORTH)
            .build();
    public static final StateMappingGroup STONECUTTER = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, BedrockLegacyStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
            .build();
    public static final StateMappingGroup STRUCTURE_BLOCK = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.STRUCTURE_BLOCK_MODE, BedrockLegacyStateTypes.STRUCTURE_BLOCK_MODE)
            .build();
    public static final StateMappingGroup TNT = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.UNSTABLE, VanillaBlockStates.UNDERWATER, BedrockLegacyStateTypes.TNT)
            .build();
    public static final StateMappingGroup TORCH_FACING = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, BedrockLegacyStateTypes.TORCH_FACING_DIRECTION)
            .build();
    public static final StateMappingGroup TRAPDOOR = new StateMappingGroup.Builder()
            .multiState("data", List.of(
                    VanillaBlockStates.HALF,
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.OPEN
            ), BedrockLegacyStateTypes.TRAPDOOR)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final StateMappingGroup TRIPWIRE = new StateMappingGroup.Builder()
            .multiState(
                    "data",
                    List.of(
                            VanillaBlockStates.ATTACHED,
                            VanillaBlockStates.DISARMED,
                            VanillaBlockStates.POWERED,
                            VanillaBlockStates.SUSPENDED
                    ),
                    BedrockLegacyStateTypes.TRIPWIRE
            )
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .build();
    public static final StateMappingGroup TRIPWIRE_HOOK = new StateMappingGroup.Builder()
            .multiState("data", List.of(
                    VanillaBlockStates.POWERED,
                    VanillaBlockStates.ATTACHED,
                    VanillaBlockStates.FACING_HORIZONTAL
            ), JavaLegacyStateTypes.TRIPWIRE_HOOK)
            .build();
    public static final StateMappingGroup TURTLE_EGG = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.EGGS, VanillaBlockStates.HATCH, BedrockLegacyStateTypes.TURTLE_EGG)
            .build();
    public static final StateMappingGroup VERTICAL_GROWING = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_15, BedrockLegacyStateTypes.AGE_15)
            .build();
    public static final StateMappingGroup VINE = new StateMappingGroup.Builder()
            .multiState(
                    "data",
                    List.of(
                            VanillaBlockStates.SOUTH,
                            VanillaBlockStates.WEST,
                            VanillaBlockStates.NORTH,
                            VanillaBlockStates.EAST
                    ),
                    BedrockLegacyStateTypes.BOOLS_TO_VINE_DIRECTION)
            .build();
    public static final StateMappingGroup WALL = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.WALL_NORTH, WallHeight.NONE)
            .defaultOutput(VanillaBlockStates.WALL_EAST, WallHeight.NONE)
            .defaultOutput(VanillaBlockStates.WALL_SOUTH, WallHeight.NONE)
            .defaultOutput(VanillaBlockStates.WALL_WEST, WallHeight.NONE)
            .defaultOutput(VanillaBlockStates.UP, Bool.FALSE)
            .build();
    public static final StateMappingGroup WALL_SKULL = new StateMappingGroup.Builder()
            .combineOutputStates("data", VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.NO_DROP, BedrockLegacyStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL_BOOL)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final StateMappingGroup WEIGHTED_PRESSURE_PLATE = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.POWER, BedrockLegacyStateTypes.POWER)
            .build();
    public static final StateMappingGroup WIRE = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.POWER, BedrockLegacyStateTypes.POWER)
            .defaultOutput(VanillaBlockStates.REDSTONE_NORTH, RedstoneConnection.NONE)
            .defaultOutput(VanillaBlockStates.REDSTONE_EAST, RedstoneConnection.NONE)
            .defaultOutput(VanillaBlockStates.REDSTONE_SOUTH, RedstoneConnection.NONE)
            .defaultOutput(VanillaBlockStates.REDSTONE_WEST, RedstoneConnection.NONE)
            .build();
}