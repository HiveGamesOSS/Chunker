package com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateMappingGroup;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.VersionedStateMappingGroup;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.*;

/**
 * A list of groups of states used to convert between Chunker and Java.
 */
public class JavaStateGroups {
    public static final StateMappingGroup AGE_15 = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_15, JavaStateTypes.AGE_15)
            .build();
    public static final StateMappingGroup AGE_25 = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_25, JavaStateTypes.AGE_25)
            .build();
    public static final StateMappingGroup AGE_3 = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_3, JavaStateTypes.AGE_3)
            .build();
    public static final StateMappingGroup AGE_3_TO_7 = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_7, JavaStateTypes.AGE_3_TO_7)
            .build();
    public static final StateMappingGroup AGE_7 = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_7, JavaStateTypes.AGE_7)
            .build();
    public static final StateMappingGroup AXIS = new StateMappingGroup.Builder()
            .state("axis", VanillaBlockStates.AXIS, JavaStateTypes.AXIS)
            .build();
    public static final StateMappingGroup BAMBOO = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_1, JavaStateTypes.AGE_1)
            .state("leaves", VanillaBlockStates.BAMBOO_LEAVES, JavaStateTypes.BAMBOO_LEAF_SIZE)
            .state("stage", VanillaBlockStates.STAGE, JavaStateTypes.STAGE)
            .build();
    public static final StateMappingGroup BAMBOO_SAPLING = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.AGE_1, Age_1._0)
            .build();
    public static final StateMappingGroup BARREL = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_ALL, JavaStateTypes.FACING_ALL)
            .state("open", VanillaBlockStates.OPEN, JavaStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup BARRIER = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.FALSE)
                    .build())
            // 1.20.2 added the waterlogged field
            .version(new Version(1, 20, 2), new StateMappingGroup.Builder()
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup BED = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("occupied", VanillaBlockStates.OCCUPIED, JavaStateTypes.BOOL)
            .state("part", VanillaBlockStates.BED_PART, JavaStateTypes.BED_PART)
            .build();
    public static final VersionedStateMappingGroup BEDROCK = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.INFINIBURN, Bool.FALSE)
            .build();
    public static final StateMappingGroup BEE_NEST = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("honey_level", VanillaBlockStates.HONEY_LEVEL, JavaStateTypes.HONEY_LEVEL)
            .build();
    public static final VersionedStateMappingGroup BELL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("attachment", VanillaBlockStates.BELL_ATTACHMENT, JavaStateTypes.BELL_ATTACHMENT)
                    .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .build())
            // 1.15 added the powered field
            .version(new Version(1, 15, 0), new StateMappingGroup.Builder()
                    .state("attachment", VanillaBlockStates.BELL_ATTACHMENT, JavaStateTypes.BELL_ATTACHMENT)
                    .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
                    .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup BIG_DRIPLEAF = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("tilt", VanillaBlockStates.TILT, JavaStateTypes.TILT)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup BREWING_STAND = new StateMappingGroup.Builder()
            .state("has_bottle_0", VanillaBlockStates.HAS_BOTTLE_0, JavaStateTypes.BOOL)
            .state("has_bottle_1", VanillaBlockStates.HAS_BOTTLE_1, JavaStateTypes.BOOL)
            .state("has_bottle_2", VanillaBlockStates.HAS_BOTTLE_2, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup BUBBLE_COLUMN = new StateMappingGroup.Builder()
            .state("drag", VanillaBlockStates.DRAG, JavaStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.TRUE)
            .build();
    public static final StateMappingGroup BULB = new StateMappingGroup.Builder()
            .state("lit", VanillaBlockStates.LIT, JavaStateTypes.BOOL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup BUTTON = new StateMappingGroup.Builder()
            .state("face", VanillaBlockStates.ATTACHMENT_TYPE, JavaStateTypes.ATTACHMENT_TYPE)
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup CAKE = new StateMappingGroup.Builder()
            .state("bites", VanillaBlockStates.BITES, JavaStateTypes.BITES)
            .build();
    public static final StateMappingGroup CALIBRATED_SCULK_SENSOR = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("power", VanillaBlockStates.POWER, JavaStateTypes.POWER)
            .state("sculk_sensor_phase", VanillaBlockStates.SCULK_SENSOR_PHASE, JavaStateTypes.SCULK_SENSOR_PHASE)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup CAMPFIRE = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("lit", VanillaBlockStates.LIT, JavaStateTypes.BOOL)
            .state("signal_fire", VanillaBlockStates.SIGNAL_FIRE, JavaStateTypes.BOOL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup CANDLE = new StateMappingGroup.Builder()
            .state("candles", VanillaBlockStates.CANDLES, JavaStateTypes.CANDLES)
            .state("lit", VanillaBlockStates.LIT, JavaStateTypes.BOOL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup CAULDRON = new StateMappingGroup.Builder()
            .state("level", VanillaBlockStates.CAULDRON_LEVEL, JavaStateTypes.CAULDRON_LEVEL)
            .build();
    public static final StateMappingGroup CAVE_VINES_BODY = new StateMappingGroup.Builder()
            .state("berries", VanillaBlockStates.BERRIES, JavaStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.AGE_25, Age_25._25)
            .build();
    public static final StateMappingGroup CAVE_VINES_HEAD = new StateMappingGroup.Builder()
            .state("berries", VanillaBlockStates.BERRIES, JavaStateTypes.BOOL)
            .state("age", VanillaBlockStates.AGE_25, JavaStateTypes.AGE_25)
            .build();
    public static final VersionedStateMappingGroup CHAIN = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.AXIS, Axis.Y)
                    .build())
            // 1.16.2 added the axis field
            .version(new Version(1, 16, 2), new StateMappingGroup.Builder()
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .state("axis", VanillaBlockStates.AXIS, JavaStateTypes.AXIS)
                    .build()
            ).build();
    public static final StateMappingGroup CHEST = new StateMappingGroup.Builder()
            .state("type", VanillaBlockStates.CHEST_TYPE, JavaStateTypes.CHEST_TYPE)
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup CHISELED_BOOKSHELF = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("slot_0_occupied", VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED, JavaStateTypes.BOOL)
            .state("slot_1_occupied", VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED, JavaStateTypes.BOOL)
            .state("slot_2_occupied", VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED, JavaStateTypes.BOOL)
            .state("slot_3_occupied", VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED, JavaStateTypes.BOOL)
            .state("slot_4_occupied", VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED, JavaStateTypes.BOOL)
            .state("slot_5_occupied", VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup CHORUS_FLOWER = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_5, JavaStateTypes.AGE_5)
            .build();
    public static final StateMappingGroup COCOA = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_2, JavaStateTypes.AGE_2)
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .build();
    public static final StateMappingGroup COMMAND_BLOCK = new StateMappingGroup.Builder()
            .state("conditional", VanillaBlockStates.CONDITIONAL, JavaStateTypes.BOOL)
            .state("facing", VanillaBlockStates.FACING_ALL, JavaStateTypes.FACING_ALL)
            .build();
    public static final StateMappingGroup COMPARATOR = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("mode", VanillaBlockStates.MODE_COMPARATOR, JavaStateTypes.COMPARATOR_MODE)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup COMPOSTER = new StateMappingGroup.Builder()
            .state("level", VanillaBlockStates.COMPOSTER_LEVEL, JavaStateTypes.COMPOSTER_LEVEL)
            .build();
    public static final VersionedStateMappingGroup CONDUIT = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.TRUE)
                    .build())
            // 1.13.1 made conduit waterloggable
            .version(new Version(1, 13, 1), new StateMappingGroup.Builder()
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup CONNECTABLE = new StateMappingGroup.Builder()
            .state("north", VanillaBlockStates.NORTH, JavaStateTypes.BOOL)
            .state("east", VanillaBlockStates.EAST, JavaStateTypes.BOOL)
            .state("south", VanillaBlockStates.SOUTH, JavaStateTypes.BOOL)
            .state("west", VanillaBlockStates.WEST, JavaStateTypes.BOOL)
            .state("up", VanillaBlockStates.UP, JavaStateTypes.BOOL)
            .state("down", VanillaBlockStates.DOWN, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup CONNECTABLE_HORIZONTAL = new StateMappingGroup.Builder()
            .state("north", VanillaBlockStates.NORTH, JavaStateTypes.BOOL)
            .state("east", VanillaBlockStates.EAST, JavaStateTypes.BOOL)
            .state("south", VanillaBlockStates.SOUTH, JavaStateTypes.BOOL)
            .state("west", VanillaBlockStates.WEST, JavaStateTypes.BOOL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup CONNECTABLE_WATERLOGGED = new StateMappingGroup.Builder()
            .state("north", VanillaBlockStates.NORTH, JavaStateTypes.BOOL)
            .state("east", VanillaBlockStates.EAST, JavaStateTypes.BOOL)
            .state("south", VanillaBlockStates.SOUTH, JavaStateTypes.BOOL)
            .state("west", VanillaBlockStates.WEST, JavaStateTypes.BOOL)
            .state("up", VanillaBlockStates.UP, JavaStateTypes.BOOL)
            .state("down", VanillaBlockStates.DOWN, JavaStateTypes.BOOL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup COPPER_GOLEM = new StateMappingGroup.Builder()
            .state("copper_golem_pose", VanillaBlockStates.COPPER_GOLEM_POSE, JavaStateTypes.COPPER_GOLEM_POSE)
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup CORAL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.FALSE)
                    .build())
            // 1.13.1 made coral waterloggable
            .version(new Version(1, 13, 1), new StateMappingGroup.Builder()
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup CORAL_FAN = new StateMappingGroup.Builder()
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.CORAL_FAN_DIRECTION, CoralFanDirection.EAST_WEST)
            .build();
    public static final StateMappingGroup CRAFTER = new StateMappingGroup.Builder()
            .state("crafting", VanillaBlockStates.CRAFTING, JavaStateTypes.BOOL)
            .state("orientation", VanillaBlockStates.ORIENTATION, JavaStateTypes.ORIENTATION)
            .state("triggered", VanillaBlockStates.TRIGGERED, JavaStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup CREAKING_HEART = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("axis", VanillaBlockStates.AXIS, JavaStateTypes.AXIS)
                    .state("creaking", VanillaBlockStates.CREAKING, JavaStateTypes.CREAKING)
                    .defaultOutput(VanillaBlockStates.NATURAL, Bool.FALSE)
                    .build())
            // 1.21.4 added the natural field / changed creaking to active
            .version(new Version(1, 21, 4), new StateMappingGroup.Builder()
                    .state("axis", VanillaBlockStates.AXIS, JavaStateTypes.AXIS)
                    .state("active", VanillaBlockStates.CREAKING, JavaStateTypes.CREAKING_BOOL)
                    .state("natural", VanillaBlockStates.NATURAL, JavaStateTypes.BOOL)
                    .build()
            )
            // 1.21.5 removed the active state and added creaking_heart_state
            .version(new Version(1, 21, 5), new StateMappingGroup.Builder()
                    .state("axis", VanillaBlockStates.AXIS, JavaStateTypes.AXIS)
                    .state("creaking_heart_state", VanillaBlockStates.CREAKING, JavaStateTypes.CREAKING_HEART_STATE)
                    .state("natural", VanillaBlockStates.NATURAL, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup DAYLIGHT_DETECTOR = new StateMappingGroup.Builder()
            .state("inverted", VanillaBlockStates.INVERTED, JavaStateTypes.BOOL)
            .state("power", VanillaBlockStates.POWER, JavaStateTypes.POWER)
            .defaultOutput(VanillaBlockStates.INVERTED, Bool.FALSE)
            .build();
    public static final VersionedStateMappingGroup DECORATED_POT = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.CRACKED, Bool.FALSE)
                    .build())
            // 1.20 added the cracked field
            .version(new Version(1, 20, 0), new StateMappingGroup.Builder()
                    .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .state("cracked", VanillaBlockStates.CRACKED, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup DOOR = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("half", VanillaBlockStates.HALF, JavaStateTypes.UPPER_LOWER_TO_HALF)
            .state("hinge", VanillaBlockStates.DOOR_HINGE, JavaStateTypes.HINGE)
            .state("open", VanillaBlockStates.OPEN, JavaStateTypes.BOOL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup DRIED_GHAST = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("hydration", VanillaBlockStates.REHYDRATION_LEVEL, JavaStateTypes.REHYDRATION_LEVEL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup END_PORTAL_FRAME = new StateMappingGroup.Builder()
            .state("eye", VanillaBlockStates.EYE, JavaStateTypes.BOOL)
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .build();
    public static final StateMappingGroup FACING_ALL = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_ALL, JavaStateTypes.FACING_ALL)
            .build();
    public static final StateMappingGroup FACING_ALL_WATERLOGGED = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_ALL, JavaStateTypes.FACING_ALL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup FACING_HORIZONTAL = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .build();
    public static final StateMappingGroup FACING_HORIZONTAL_LIT = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("lit", VanillaBlockStates.LIT, JavaStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.LIT, Bool.FALSE)
            .build();
    public static final StateMappingGroup FACING_HORIZONTAL_WATERLOGGED = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup FACING_TRIGGERED = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_ALL, JavaStateTypes.FACING_ALL)
            .state("triggered", VanillaBlockStates.TRIGGERED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup FARMLAND = new StateMappingGroup.Builder()
            .state("moisture", VanillaBlockStates.MOISTURE, JavaStateTypes.MOISTURE)
            .build();
    public static final StateMappingGroup FENCE_GATE = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("in_wall", VanillaBlockStates.IN_WALL, JavaStateTypes.BOOL)
            .state("open", VanillaBlockStates.OPEN, JavaStateTypes.BOOL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup FIRE = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_15, JavaStateTypes.AGE_15)
            .state("north", VanillaBlockStates.NORTH, JavaStateTypes.BOOL)
            .state("east", VanillaBlockStates.EAST, JavaStateTypes.BOOL)
            .state("south", VanillaBlockStates.SOUTH, JavaStateTypes.BOOL)
            .state("west", VanillaBlockStates.WEST, JavaStateTypes.BOOL)
            .state("up", VanillaBlockStates.UP, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup GRINDSTONE = new StateMappingGroup.Builder()
            .state("face", VanillaBlockStates.GRINDSTONE_ATTACHMENT_TYPE, JavaStateTypes.GRINDSTSTONE_ATTACHMENT_TYPE)
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .build();
    public static final StateMappingGroup HALF = new StateMappingGroup.Builder()
            .state("half", VanillaBlockStates.HALF, JavaStateTypes.UPPER_LOWER_TO_HALF)
            .build();
    public static final StateMappingGroup HANGING_SIGN = new StateMappingGroup.Builder()
            .state("attached", VanillaBlockStates.ATTACHED, JavaStateTypes.BOOL)
            .state("rotation", VanillaBlockStates.ROTATION, JavaStateTypes.ROTATION)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup HOPPER = new StateMappingGroup.Builder()
            .state("enabled", VanillaBlockStates.ENABLED, JavaStateTypes.BOOL)
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL_DOWN, JavaStateTypes.FACING_HORIZONTAL_DOWN)
            .build();
    public static final VersionedStateMappingGroup JIGSAW = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing", VanillaBlockStates.ORIENTATION, JavaStateTypes.FACING_TO_ORIENTATION)
                    .build())
            // 1.16 added further directions introducing orientation
            .version(new Version(1, 16, 0), new StateMappingGroup.Builder()
                    .state("orientation", VanillaBlockStates.ORIENTATION, JavaStateTypes.ORIENTATION)
                    .build()
            ).build();
    public static final StateMappingGroup JUKEBOX = new StateMappingGroup.Builder()
            .state("has_record", VanillaBlockStates.HAS_RECORD, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup KELP = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_25, JavaStateTypes.AGE_25)
            .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.TRUE)
            .build();
    public static final VersionedStateMappingGroup LANTERN = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("hanging", VanillaBlockStates.HANGING, JavaStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.FALSE)
                    .build())
            // 1.16.2 added the waterlogging to lanterns
            .version(new Version(1, 16, 2), new StateMappingGroup.Builder()
                    .state("hanging", VanillaBlockStates.HANGING, JavaStateTypes.BOOL)
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup LAVA_CAULDRON = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.CAULDRON_LEVEL, CauldronLevel._6)
            .build();
    public static final StateMappingGroup LEAF_LITTER = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("segment_amount", VanillaBlockStates.SEGMENT_AMOUNT, JavaStateTypes.SEGMENT_AMOUNT)
            .build();
    public static final VersionedStateMappingGroup LEAVES = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("distance", VanillaBlockStates.DISTANCE, JavaStateTypes.DISTANCE_6)
                    .state("persistent", VanillaBlockStates.PERSISTENT, JavaStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.FALSE)
                    .defaultOutput(VanillaBlockStates.UPDATE, Bool.FALSE)
                    .build())
            // 1.19 added the waterlogging to leaves
            .version(new Version(1, 19, 0), new StateMappingGroup.Builder()
                    .state("distance", VanillaBlockStates.DISTANCE, JavaStateTypes.DISTANCE_6)
                    .state("persistent", VanillaBlockStates.PERSISTENT, JavaStateTypes.BOOL)
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.UPDATE, Bool.FALSE)
                    .build()
            ).build();
    public static final StateMappingGroup LECTERN = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("has_book", VanillaBlockStates.HAS_BOOK, JavaStateTypes.BOOL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup LIGHTNING_ROD = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_ALL, JavaStateTypes.FACING_ALL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup LIGHT_BLOCK = new StateMappingGroup.Builder()
            .state("level", VanillaBlockStates.LIGHT_LEVEL, JavaStateTypes.LIGHT_LEVEL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup LIQUID = new StateMappingGroup.Builder()
            .state("level", VanillaBlockStates.LIQUID_LEVEL, JavaStateTypes.LIQUID_LEVEL)
            .defaultOutput(VanillaBlockStates.FLOWING, Bool.FALSE)
            .build();
    public static final StateMappingGroup LIT_DEFAULT_FALSE = new StateMappingGroup.Builder()
            .state("lit", VanillaBlockStates.LIT, JavaStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.LIT, Bool.FALSE)
            .build();
    public static final StateMappingGroup LIT_DEFAULT_TRUE = new StateMappingGroup.Builder()
            .state("lit", VanillaBlockStates.LIT, JavaStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.LIT, Bool.TRUE)
            .build();
    public static final StateMappingGroup MANGROVE_PROPAGULE = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_4, JavaStateTypes.AGE_4)
            .state("hanging", VanillaBlockStates.HANGING, JavaStateTypes.BOOL)
            .state("stage", VanillaBlockStates.STAGE, JavaStateTypes.STAGE)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.HANGING, Bool.FALSE)
            .build();
    public static final StateMappingGroup MOVING_PISTON = new StateMappingGroup.Builder()
            .state("type", VanillaBlockStates.PISTON_TYPE, JavaStateTypes.PISTON_TYPE)
            .state("facing", VanillaBlockStates.FACING_ALL, JavaStateTypes.FACING_ALL)
            .build();
    public static final StateMappingGroup NETHER_PORTAL = new StateMappingGroup.Builder()
            .state("axis", VanillaBlockStates.AXIS_HORIZONTAL, JavaStateTypes.AXIS_HORIZONTAL)
            .build();
    public static final VersionedStateMappingGroup NOTE_BLOCK = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("instrument", VanillaBlockStates.NOTE_BLOCK_INSTRUMENT, JavaStateTypes.INSTRUMENT_PRE_1_14)
                    .state("note", VanillaBlockStates.NOTE, JavaStateTypes.NOTE)
                    .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
                    .build())
            // 1.14 added several new instruments
            .version(new Version(1, 14, 0), new StateMappingGroup.Builder()
                    .state("instrument", VanillaBlockStates.NOTE_BLOCK_INSTRUMENT, JavaStateTypes.INSTRUMENT_PRE_1_19_3)
                    .state("note", VanillaBlockStates.NOTE, JavaStateTypes.NOTE)
                    .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
                    .build()
            )
            // 1.19.3 added several skull based noteblock sounds
            .version(new Version(1, 19, 3), new StateMappingGroup.Builder()
                    .state("instrument", VanillaBlockStates.NOTE_BLOCK_INSTRUMENT, JavaStateTypes.INSTRUMENT)
                    .state("note", VanillaBlockStates.NOTE, JavaStateTypes.NOTE)
                    .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup OBSERVER = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_ALL, JavaStateTypes.FACING_ALL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup PALE_HANGING_MOSS = new StateMappingGroup.Builder()
            .state("tip", VanillaBlockStates.TIP, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup PALE_MOSS_CARPET = new StateMappingGroup.Builder()
            .state("north", VanillaBlockStates.WALL_NORTH, JavaStateTypes.WALL_HEIGHT)
            .state("east", VanillaBlockStates.WALL_EAST, JavaStateTypes.WALL_HEIGHT)
            .state("south", VanillaBlockStates.WALL_SOUTH, JavaStateTypes.WALL_HEIGHT)
            .state("west", VanillaBlockStates.WALL_WEST, JavaStateTypes.WALL_HEIGHT)
            .state("bottom", VanillaBlockStates.BOTTOM, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup PINK_PETALS = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("flower_amount", VanillaBlockStates.FLOWER_AMOUNT, JavaStateTypes.FLOWERS)
            .build();
    public static final StateMappingGroup PISTON = new StateMappingGroup.Builder()
            .state("extended", VanillaBlockStates.EXTENDED, JavaStateTypes.BOOL)
            .state("facing", VanillaBlockStates.FACING_ALL, JavaStateTypes.FACING_ALL)
            .build();
    public static final StateMappingGroup PISTON_HEAD = new StateMappingGroup.Builder()
            .state("type", VanillaBlockStates.PISTON_TYPE, JavaStateTypes.PISTON_TYPE)
            .state("facing", VanillaBlockStates.FACING_ALL, JavaStateTypes.FACING_ALL)
            .state("short", VanillaBlockStates.SHORT, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup PITCHER_CROP = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_4, JavaStateTypes.AGE_4)
            .state("half", VanillaBlockStates.HALF, JavaStateTypes.UPPER_LOWER_TO_HALF)
            .build();
    public static final StateMappingGroup POINTED_DRIPSTONE = new StateMappingGroup.Builder()
            .state("thickness", VanillaBlockStates.DRIPSTONE_THICKNESS, JavaStateTypes.DRIPSTONE_THICKNESS)
            .state("vertical_direction", VanillaBlockStates.VERTICAL_DIRECTION, JavaStateTypes.VERTICAL_DIRECTION)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup POWER = new StateMappingGroup.Builder()
            .state("power", VanillaBlockStates.POWER, JavaStateTypes.POWER)
            .build();
    public static final StateMappingGroup POWERED = new StateMappingGroup.Builder()
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup POWERED_RAIL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
                    .state("shape", VanillaBlockStates.RAIL_SHAPE_STRAIGHT, JavaStateTypes.RAIL_SHAPE_STRAIGHT)
                    .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.FALSE)
                    .build())
            // 1.17 added the waterlogging to rails
            .version(new Version(1, 17, 0), new StateMappingGroup.Builder()
                    .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
                    .state("shape", VanillaBlockStates.RAIL_SHAPE_STRAIGHT, JavaStateTypes.RAIL_SHAPE_STRAIGHT)
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup RAIL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("shape", VanillaBlockStates.RAIL_SHAPE, JavaStateTypes.RAIL_SHAPE)
                    .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.FALSE)
                    .build())
            // 1.17 added the waterlogging to rails
            .version(new Version(1, 17, 0), new StateMappingGroup.Builder()
                    .state("shape", VanillaBlockStates.RAIL_SHAPE, JavaStateTypes.RAIL_SHAPE)
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup REDSTONE_WIRE = new StateMappingGroup.Builder()
            .state("power", VanillaBlockStates.POWER, JavaStateTypes.POWER)
            .state("north", VanillaBlockStates.REDSTONE_NORTH, JavaStateTypes.REDSTONE_CONNECTION)
            .state("east", VanillaBlockStates.REDSTONE_EAST, JavaStateTypes.REDSTONE_CONNECTION)
            .state("south", VanillaBlockStates.REDSTONE_SOUTH, JavaStateTypes.REDSTONE_CONNECTION)
            .state("west", VanillaBlockStates.REDSTONE_WEST, JavaStateTypes.REDSTONE_CONNECTION)
            .build();
    public static final StateMappingGroup REPEATER = new StateMappingGroup.Builder()
            .state("delay", VanillaBlockStates.DELAY, JavaStateTypes.DELAY)
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("locked", VanillaBlockStates.LOCKED, JavaStateTypes.BOOL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup RESPAWN_ANCHOR = new StateMappingGroup.Builder()
            .state("charges", VanillaBlockStates.RESPAWN_ANCHOR_CHARGES, JavaStateTypes.CHARGES)
            .build();
    public static final StateMappingGroup ROTATION = new StateMappingGroup.Builder()
            .state("rotation", VanillaBlockStates.ROTATION, JavaStateTypes.ROTATION)
            .build();
    public static final StateMappingGroup SAPLING = new StateMappingGroup.Builder()
            .state("stage", VanillaBlockStates.STAGE, JavaStateTypes.STAGE)
            .build();
    public static final StateMappingGroup SCAFFOLDING = new StateMappingGroup.Builder()
            .state("bottom", VanillaBlockStates.BOTTOM, JavaStateTypes.BOOL)
            .state("distance", VanillaBlockStates.STABILITY_DISTANCE, JavaStateTypes.STABILITY_DISTANCE)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup SCULK_CATALYST = new StateMappingGroup.Builder()
            .state("bloom", VanillaBlockStates.BLOOM, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup SCULK_SENSOR = new StateMappingGroup.Builder()
            .state("power", VanillaBlockStates.POWER, JavaStateTypes.POWER)
            .state("sculk_sensor_phase", VanillaBlockStates.SCULK_SENSOR_PHASE, JavaStateTypes.SCULK_SENSOR_PHASE)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup SCULK_SHRIEKER = new StateMappingGroup.Builder()
            .state("can_summon", VanillaBlockStates.CAN_SUMMON, JavaStateTypes.BOOL)
            .state("shrieking", VanillaBlockStates.SHRIEKING, JavaStateTypes.BOOL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup SEA_PICKLE = new StateMappingGroup.Builder()
            .state("pickles", VanillaBlockStates.PICKLES, JavaStateTypes.PICKLES)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            // In Java if the pickle is waterlogged it is not dead
            .state("waterlogged", VanillaBlockStates.DEAD, JavaStateTypes.INVERSE_BOOL)
            .build();
    public static final StateMappingGroup SHELF = new StateMappingGroup.Builder()
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("side_chain", VanillaBlockStates.SIDE_CHAIN, JavaStateTypes.SIDE_CHAIN)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup SIGN = new StateMappingGroup.Builder()
            .state("rotation", VanillaBlockStates.ROTATION, JavaStateTypes.ROTATION)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup SKULL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("rotation", VanillaBlockStates.ROTATION, JavaStateTypes.ROTATION)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .defaultOutput(VanillaBlockStates.NO_DROP, Bool.FALSE)
                    .build())
            // 1.20.2 added the powered field
            .version(new Version(1, 20, 2), new StateMappingGroup.Builder()
                    .state("rotation", VanillaBlockStates.ROTATION, JavaStateTypes.ROTATION)
                    .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.NO_DROP, Bool.FALSE)
                    .build()
            ).build();
    public static final StateMappingGroup SLAB = new StateMappingGroup.Builder()
            .state("type", VanillaBlockStates.SLAB_TYPE, JavaStateTypes.SLAB_TYPE)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup SMALL_DRIPLEAF = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("half", VanillaBlockStates.HALF, JavaStateTypes.UPPER_LOWER_TO_HALF)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup SNIFFER_EGG = new StateMappingGroup.Builder()
            .state("hatch", VanillaBlockStates.HATCH, JavaStateTypes.HATCH)
            .build();
    public static final StateMappingGroup SNOW = new StateMappingGroup.Builder()
            .state("layers", VanillaBlockStates.LAYERS, JavaStateTypes.LAYERS)
            .build();
    public static final StateMappingGroup SNOWY = new StateMappingGroup.Builder()
            .state("snowy", VanillaBlockStates.SNOWY, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup SOUL_FIRE = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.AGE_15, Age_15._0)
            .build();
    public static final StateMappingGroup STAIRS = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("half", VanillaBlockStates.HALF, JavaStateTypes.HALF)
            .state("shape", VanillaBlockStates.STAIR_SHAPE, JavaStateTypes.STAIR_SHAPE)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup STRUCTURE_BLOCK = new StateMappingGroup.Builder()
            .state("mode", VanillaBlockStates.STRUCTURE_BLOCK_MODE, JavaStateTypes.STRUCTURE_BLOCK_MODE)
            .build();
    public static final StateMappingGroup STRUCTURE_VOID = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.STRUCTURE_VOID_TYPE, StructureVoidType.VOID)
            .build();
    public static final StateMappingGroup SUSPICIOUS_BLOCK = new StateMappingGroup.Builder()
            .state("dusted", VanillaBlockStates.DUSTED, JavaStateTypes.DUSTED)
            .build();
    public static final StateMappingGroup TALL_SEAGRASS = new StateMappingGroup.Builder()
            .state("half", VanillaBlockStates.HALF, JavaStateTypes.UPPER_LOWER_TO_HALF)
            .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.TRUE)
            .build();
    public static final StateMappingGroup TEST_BLOCK = new StateMappingGroup.Builder()
            .state("mode", VanillaBlockStates.TEST_BLOCK_MODE, JavaStateTypes.TEST_BLOCK_MODE)
            .build();
    public static final VersionedStateMappingGroup TNT = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.UNDERWATER, Bool.FALSE)
                    .defaultOutput(VanillaBlockStates.UNSTABLE, Bool.FALSE)
                    .build())
            // 1.13.1 added unstable to tnt
            .version(new Version(1, 13, 1), new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.UNDERWATER, Bool.FALSE)
                    .state("unstable", VanillaBlockStates.UNSTABLE, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup TORCHFLOWER_CROP = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("age", VanillaBlockStates.AGE_1, JavaStateTypes.AGE_2_TO_1)
                    .build())
            // 1.20 changed the age field
            .version(new Version(1, 20, 0), new StateMappingGroup.Builder()
                    .state("age", VanillaBlockStates.AGE_1, JavaStateTypes.AGE_1)
                    .build()
            ).build();
    public static final StateMappingGroup TRAPDOOR = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("half", VanillaBlockStates.HALF, JavaStateTypes.HALF)
            .state("open", VanillaBlockStates.OPEN, JavaStateTypes.BOOL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup TRIAL_SPAWNER = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("trial_spawner_state", VanillaBlockStates.TRIAL_SPAWNER_STATE, JavaStateTypes.TRIAL_SPAWNER_STATE)
                    .defaultOutput(VanillaBlockStates.OMINOUS, Bool.FALSE)
                    .build())
            // 1.20.5 added ominous
            .version(new Version(1, 20, 5), new StateMappingGroup.Builder()
                    .state("trial_spawner_state", VanillaBlockStates.TRIAL_SPAWNER_STATE, JavaStateTypes.TRIAL_SPAWNER_STATE)
                    .state("ominous", VanillaBlockStates.OMINOUS, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup TRIPWIRE = new StateMappingGroup.Builder()
            .state("attached", VanillaBlockStates.ATTACHED, JavaStateTypes.BOOL)
            .state("disarmed", VanillaBlockStates.DISARMED, JavaStateTypes.BOOL)
            .state("east", VanillaBlockStates.EAST, JavaStateTypes.BOOL)
            .state("north", VanillaBlockStates.NORTH, JavaStateTypes.BOOL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .state("south", VanillaBlockStates.SOUTH, JavaStateTypes.BOOL)
            .state("west", VanillaBlockStates.WEST, JavaStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.SUSPENDED, Bool.FALSE)
            .build();
    public static final StateMappingGroup TRIPWIRE_HOOK = new StateMappingGroup.Builder()
            .state("attached", VanillaBlockStates.ATTACHED, JavaStateTypes.BOOL)
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup TURTLE_EGG = new StateMappingGroup.Builder()
            .state("eggs", VanillaBlockStates.EGGS, JavaStateTypes.EGGS)
            .state("hatch", VanillaBlockStates.HATCH, JavaStateTypes.HATCH)
            .build();
    public static final VersionedStateMappingGroup VAULT = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("vault_state", VanillaBlockStates.VAULT_STATE, JavaStateTypes.VAULT_STATE)
            .state("ominous", VanillaBlockStates.OMINOUS, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup VINE = new StateMappingGroup.Builder()
            .state("east", VanillaBlockStates.EAST, JavaStateTypes.BOOL)
            .state("north", VanillaBlockStates.NORTH, JavaStateTypes.BOOL)
            .state("south", VanillaBlockStates.SOUTH, JavaStateTypes.BOOL)
            .state("up", VanillaBlockStates.UP, JavaStateTypes.BOOL)
            .state("west", VanillaBlockStates.WEST, JavaStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup WALL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .state("north", VanillaBlockStates.WALL_NORTH, JavaStateTypes.BOOL_TO_WALL_HEIGHT)
                    .state("east", VanillaBlockStates.WALL_EAST, JavaStateTypes.BOOL_TO_WALL_HEIGHT)
                    .state("south", VanillaBlockStates.WALL_SOUTH, JavaStateTypes.BOOL_TO_WALL_HEIGHT)
                    .state("west", VanillaBlockStates.WALL_WEST, JavaStateTypes.BOOL_TO_WALL_HEIGHT)
                    .state("up", VanillaBlockStates.UP, JavaStateTypes.BOOL)
                    .build())
            // 1.16 added tall wall height (switching from a boolean)
            .version(new Version(1, 16, 0), new StateMappingGroup.Builder()
                    .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
                    .state("north", VanillaBlockStates.WALL_NORTH, JavaStateTypes.WALL_HEIGHT)
                    .state("east", VanillaBlockStates.WALL_EAST, JavaStateTypes.WALL_HEIGHT)
                    .state("south", VanillaBlockStates.WALL_SOUTH, JavaStateTypes.WALL_HEIGHT)
                    .state("west", VanillaBlockStates.WALL_WEST, JavaStateTypes.WALL_HEIGHT)
                    .state("up", VanillaBlockStates.UP, JavaStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup WALL_HANGING_SIGN = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.ATTACHED, Bool.FALSE)
            .build();
    public static final VersionedStateMappingGroup WALL_SKULL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .defaultOutput(VanillaBlockStates.NO_DROP, Bool.FALSE)
                    .build())
            // 1.20.2 added the powered field
            .version(new Version(1, 20, 2), new StateMappingGroup.Builder()
                    .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
                    .state("powered", VanillaBlockStates.POWERED, JavaStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.NO_DROP, Bool.FALSE)
                    .build()
            ).build();
    public static final StateMappingGroup WATERLOGGED = new StateMappingGroup.Builder()
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, JavaStateTypes.BOOL)
            .build();
    public static final StateMappingGroup WATERLOGGED_DEFAULT_TRUE = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.TRUE)
            .build();
    public static final StateMappingGroup WILDFLOWERS = new StateMappingGroup.Builder()
            .state("facing", VanillaBlockStates.FACING_HORIZONTAL, JavaStateTypes.FACING_HORIZONTAL)
            .state("flower_amount", VanillaBlockStates.FLOWER_AMOUNT, JavaStateTypes.FLOWERS)
            .build();
}
