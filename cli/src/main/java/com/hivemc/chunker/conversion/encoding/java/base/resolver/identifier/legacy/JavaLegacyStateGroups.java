package com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy;

import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateMappingGroup;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.VersionedStateMappingGroup;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.*;

import java.util.List;

/**
 * A list of groups of states used to convert between Chunker and legacy Java.
 */
public class JavaLegacyStateGroups {
    public static final StateMappingGroup AGE_15 = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_15, JavaLegacyStateTypes.AGE_15)
            .build();
    public static final StateMappingGroup AGE_3 = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_3, JavaLegacyStateTypes.AGE_3)
            .build();
    public static final StateMappingGroup AGE_3_TO_7 = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_7, JavaLegacyStateTypes.AGE_3_TO_7)
            .build();
    public static final StateMappingGroup AGE_5 = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_5, JavaLegacyStateTypes.AGE_5)
            .build();
    public static final StateMappingGroup AGE_7 = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_7, JavaLegacyStateTypes.AGE_7)
            .build();
    public static final StateMappingGroup AXIS = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AXIS, JavaLegacyStateTypes.AXIS)
            .build();
    public static final StateMappingGroup BED = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.BED_PART, VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.OCCUPIED), JavaLegacyStateTypes.BED)
            .build();
    public static final VersionedStateMappingGroup BEDROCK = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.INFINIBURN, Bool.FALSE)
            .build();
    public static final StateMappingGroup BREWING_STAND = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(
                    VanillaBlockStates.HAS_BOTTLE_0,
                    VanillaBlockStates.HAS_BOTTLE_1,
                    VanillaBlockStates.HAS_BOTTLE_2
            ), JavaLegacyStateTypes.BREWING_STAND)
            .build();
    public static final StateMappingGroup BUTTON = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(
                    VanillaBlockStates.ATTACHMENT_TYPE,
                    VanillaBlockStates.POWERED,
                    VanillaBlockStates.FACING_HORIZONTAL
            ), JavaLegacyStateTypes.BUTTON)
            .build();
    public static final StateMappingGroup CAKE = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.BITES, JavaLegacyStateTypes.BITES)
            .build();
    public static final StateMappingGroup CAULDRON = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.CAULDRON_LEVEL, JavaLegacyStateTypes.CAULDRON_LEVEL)
            .build();
    public static final VersionedStateMappingGroup CHEST = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, JavaLegacyStateTypes.FACING_HORIZONTAL_UNUSUAL)
            .defaultOutput(VanillaBlockStates.CHEST_TYPE, ChestType.SINGLE)
            .build();
    public static final StateMappingGroup COCOA = new StateMappingGroup.Builder()
            .multiState("data", List.of(
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.AGE_2
            ), JavaLegacyStateTypes.COCOA)
            .build();
    public static final StateMappingGroup COMMAND_BLOCK = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.CONDITIONAL, VanillaBlockStates.FACING_ALL), JavaLegacyStateTypes.BOOL_FACING_ALL)
            .build();
    public static final StateMappingGroup CONNECTABLE = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.UP, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.DOWN, Bool.FALSE)
            .build();
    public static final StateMappingGroup CONNECTABLE_HORIZONTAL = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .build();
    public static final StateMappingGroup DOOR = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(
                    VanillaBlockStates.DOOR_HINGE,
                    VanillaBlockStates.HALF,
                    VanillaBlockStates.POWERED,
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.OPEN
            ), JavaLegacyStateTypes.DOOR)
            .build();
    public static final StateMappingGroup DOOR_2 = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(
                    VanillaBlockStates.DOOR_HINGE,
                    VanillaBlockStates.HALF,
                    VanillaBlockStates.POWERED,
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.OPEN
            ), JavaLegacyStateTypes.DOOR_2)
            .build();
    public static final StateMappingGroup END_PORTAL_FRAME = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.EYE, VanillaBlockStates.FACING_HORIZONTAL), JavaLegacyStateTypes.BOOL_FACING_HORIZONTAL)
            .build();
    public static final StateMappingGroup FACING_ALL = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .state("data", VanillaBlockStates.FACING_ALL, JavaLegacyStateTypes.FACING_DIRECTION)
            .build();
    public static final StateMappingGroup FACING_HORIZONTAL_SWNE = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, JavaLegacyStateTypes.FACING_HORIZONTAL_SWNE)
            .build();
    public static final VersionedStateMappingGroup FACING_HORIZONTAL_UNUSUAL = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, JavaLegacyStateTypes.FACING_HORIZONTAL_UNUSUAL)
            .build();
    public static final StateMappingGroup FACING_POWERED = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.POWERED, VanillaBlockStates.FACING_ALL), JavaLegacyStateTypes.BOOL_FACING_ALL)
            .build();
    public static final StateMappingGroup FACING_TRIGGERED = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.TRIGGERED, VanillaBlockStates.FACING_ALL), JavaLegacyStateTypes.BOOL_FACING_ALL)
            .build();
    public static final StateMappingGroup FARMLAND = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.MOISTURE, JavaLegacyStateTypes.MOISTURE)
            .build();
    public static final StateMappingGroup FENCE_GATE = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(
                    VanillaBlockStates.POWERED,
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.OPEN
            ), JavaLegacyStateTypes.FENCE_GATE)
            .defaultOutput(VanillaBlockStates.IN_WALL, Bool.FALSE)
            .build();
    public static final StateMappingGroup FIRE = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AGE_15, JavaLegacyStateTypes.AGE_15)
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.UP, Bool.FALSE)
            .build();
    public static final StateMappingGroup HOPPER = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.ENABLED, VanillaBlockStates.FACING_HORIZONTAL_DOWN), JavaLegacyStateTypes.HOPPER)
            .build();
    public static final StateMappingGroup JUKEBOX = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.HAS_RECORD, JavaLegacyStateTypes.BOOL)
            .build();
    public static final StateMappingGroup LEAVES = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.DISTANCE, Distance._1)
            .build();
    public static final StateMappingGroup LEVER = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(
                    VanillaBlockStates.ATTACHMENT_TYPE,
                    VanillaBlockStates.POWERED,
                    VanillaBlockStates.FACING_HORIZONTAL
            ), JavaLegacyStateTypes.LEVER)
            .build();
    public static final StateMappingGroup LIQUID = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.LIQUID_LEVEL, JavaLegacyStateTypes.LIQUID_LEVEL)
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
                    JavaLegacyStateTypes.MUSHROOM_FACE_BITS
            )
            .build();
    public static final StateMappingGroup NETHER_PORTAL = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.AXIS_HORIZONTAL, JavaLegacyStateTypes.AXIS_HORIZONTAL)
            .build();
    public static final StateMappingGroup NOTE_BLOCK = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.NOTE, Note._0)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.NOTE_BLOCK_INSTRUMENT, NoteBlockInstrument.HARP)
            .build();
    public static final StateMappingGroup PISTON = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.EXTENDED, VanillaBlockStates.FACING_ALL), JavaLegacyStateTypes.BOOL_FACING_ALL)
            .build();
    public static final StateMappingGroup PISTON_EXTENSION = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.FACING_ALL, VanillaBlockStates.PISTON_TYPE), JavaLegacyStateTypes.FACING_PISTON)
            .build();
    public static final StateMappingGroup PISTON_HEAD = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.FACING_ALL, VanillaBlockStates.PISTON_TYPE), JavaLegacyStateTypes.FACING_PISTON)
            .defaultOutput(VanillaBlockStates.SHORT, Bool.FALSE)
            .build();
    public static final StateMappingGroup POWER = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.POWER, JavaLegacyStateTypes.POWER)
            .build();
    public static final StateMappingGroup POWERED = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.POWERED, JavaLegacyStateTypes.BOOL)
            .build();
    public static final StateMappingGroup POWERED_COMPARATOR = new StateMappingGroup.Builder()
            .multiState("data", List.of(
                    VanillaBlockStates.MODE_COMPARATOR,
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.POWERED
            ), JavaLegacyStateTypes.POWERED_COMPARATOR)
            .build();
    public static final StateMappingGroup POWERED_RAIL = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.POWERED, VanillaBlockStates.RAIL_SHAPE_STRAIGHT), JavaLegacyStateTypes.POWERED_RAIL)
            .build();
    public static final StateMappingGroup RAIL = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .state("data", VanillaBlockStates.RAIL_SHAPE, JavaLegacyStateTypes.RAIL_SHAPE)
            .build();
    public static final StateMappingGroup REDSTONE_WIRE = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.POWER, JavaLegacyStateTypes.POWER)
            .defaultOutput(VanillaBlockStates.REDSTONE_NORTH, RedstoneConnection.NONE)
            .defaultOutput(VanillaBlockStates.REDSTONE_EAST, RedstoneConnection.NONE)
            .defaultOutput(VanillaBlockStates.REDSTONE_SOUTH, RedstoneConnection.NONE)
            .defaultOutput(VanillaBlockStates.REDSTONE_WEST, RedstoneConnection.NONE)
            .build();
    public static final StateMappingGroup REPEATER = new StateMappingGroup.Builder()
            .multiState("data", List.of(
                    VanillaBlockStates.DELAY,
                    VanillaBlockStates.FACING_HORIZONTAL
            ), JavaLegacyStateTypes.REPEATER)
            .defaultOutput(VanillaBlockStates.LOCKED, Bool.FALSE)
            .build();
    public static final StateMappingGroup ROTATION = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.ROTATION, JavaLegacyStateTypes.ROTATION)
            .build();
    public static final StateMappingGroup SKULL = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .defaultOutput(VanillaBlockStates.ROTATION, Rotation._0)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final StateMappingGroup SLAB_HALF = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .state("data", VanillaBlockStates.SLAB_TYPE, JavaLegacyStateTypes.SLAB_TYPE)
            .build();
    public static final StateMappingGroup SNOW = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.LAYERS, JavaLegacyStateTypes.SNOW_LAYERS)
            .build();
    public static final StateMappingGroup SNOWY = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.SNOWY, Bool.FALSE)
            .build();
    public static final StateMappingGroup STAIRS = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.HALF), JavaLegacyStateTypes.STAIRS)
            .defaultOutput(VanillaBlockStates.STAIR_SHAPE, StairShape.STRAIGHT)
            .build();
    public static final StateMappingGroup STEM_FACING = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.FACING_HORIZONTAL, FacingDirectionHorizontal.NORTH)
            .build();
    public static final StateMappingGroup STRUCTURE_BLOCK = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.STRUCTURE_BLOCK_MODE, JavaLegacyStateTypes.STRUCTURE_BLOCK_MODE)
            .build();
    public static final StateMappingGroup STRUCTURE_VOID = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.STRUCTURE_VOID_TYPE, StructureVoidType.VOID)
            .build();
    public static final StateMappingGroup TNT = new StateMappingGroup.Builder()
            .state("data", VanillaBlockStates.UNSTABLE, JavaLegacyStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.UNDERWATER, Bool.FALSE)
            .build();
    public static final VersionedStateMappingGroup TORCH_DIRECTION = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .state("data", VanillaBlockStates.FACING_HORIZONTAL, JavaLegacyStateTypes.TORCH_DIRECTION)
            .build();
    public static final StateMappingGroup TRAPDOOR = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(
                    VanillaBlockStates.HALF,
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.OPEN
            ), JavaLegacyStateTypes.TRAPDOOR)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final StateMappingGroup TRIPWIRE = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(
                    VanillaBlockStates.DISARMED,
                    VanillaBlockStates.POWERED,
                    VanillaBlockStates.ATTACHED
            ), JavaLegacyStateTypes.TRIPWIRE)
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SUSPENDED, Bool.FALSE)
            .build();
    public static final StateMappingGroup TRIPWIRE_HOOK = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(
                    VanillaBlockStates.POWERED,
                    VanillaBlockStates.ATTACHED,
                    VanillaBlockStates.FACING_HORIZONTAL
            ), JavaLegacyStateTypes.TRIPWIRE_HOOK)
            .build();
    public static final StateMappingGroup UNPOWERED_COMPARATOR = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(
                    VanillaBlockStates.MODE_COMPARATOR,
                    VanillaBlockStates.FACING_HORIZONTAL,
                    VanillaBlockStates.POWERED
            ), JavaLegacyStateTypes.UNPOWERED_COMPARATOR)
            .build();
    public static final StateMappingGroup VINE = new StateMappingGroup.Builder()
            .multiState("data", List.of(
                    VanillaBlockStates.SOUTH,
                    VanillaBlockStates.WEST,
                    VanillaBlockStates.NORTH,
                    VanillaBlockStates.EAST
            ), JavaLegacyStateTypes.BOOLS_TO_VINE_DIRECTION)
            .defaultOutput(VanillaBlockStates.UP, Bool.FALSE)
            .build();
    public static final StateMappingGroup WALL = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.WALL_NORTH, WallHeight.NONE)
            .defaultOutput(VanillaBlockStates.WALL_EAST, WallHeight.NONE)
            .defaultOutput(VanillaBlockStates.WALL_SOUTH, WallHeight.NONE)
            .defaultOutput(VanillaBlockStates.WALL_WEST, WallHeight.NONE)
            .defaultOutput(VanillaBlockStates.UP, Bool.FALSE)
            .build();
    public static final StateMappingGroup WALL_SKULL = new StateMappingGroup.Builder()
            .defaultInput("data", 0) // Make sure 0 is the default (used for items)
            .multiState("data", List.of(VanillaBlockStates.NO_DROP, VanillaBlockStates.FACING_HORIZONTAL), JavaLegacyStateTypes.WALL_SKULL)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final StateMappingGroup WOOD = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.AXIS, Axis.Y)
            .build();
}
