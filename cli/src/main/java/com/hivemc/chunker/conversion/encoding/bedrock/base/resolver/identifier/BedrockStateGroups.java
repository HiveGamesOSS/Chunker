package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateMappingGroup;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.VersionedStateMappingGroup;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy.BedrockLegacyStateTypes;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.*;

import java.util.List;

/**
 * A list of groups of states used to convert between Chunker and Bedrock.
 */
public class BedrockStateGroups {
    public static final StateMappingGroup AGE_3 = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_3, BedrockStateTypes.AGE_3)
            .build();
    public static final VersionedStateMappingGroup ANVIL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .build())
            // 1.20.30 migrated these blocks to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .build()
            ).build();
    public static final StateMappingGroup BAMBOO = new StateMappingGroup.Builder()
            .state("age_bit", VanillaBlockStates.AGE_1, BedrockStateTypes.AGE_1)
            .state("bamboo_leaf_size", VanillaBlockStates.BAMBOO_LEAVES, BedrockStateTypes.LEAVES)
            .state("bamboo_stalk_thickness", VanillaBlockStates.STAGE, BedrockStateTypes.BAMBOO_STALK_THICKNESS)
            .build();
    public static final VersionedStateMappingGroup BAMBOO_SAPLING = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("age_bit", VanillaBlockStates.AGE_1, BedrockStateTypes.AGE_1)
                    .defaultInput("sapling_type", "oak") // Unused
                    .build())
            // 1.20.80 removed the unused state
            .version(new Version(1, 20, 80), new StateMappingGroup.Builder()
                    .state("age_bit", VanillaBlockStates.AGE_1, BedrockStateTypes.AGE_1)
                    .build())
            .build();
    public static final StateMappingGroup BANNER = new StateMappingGroup.Builder()
            .state("ground_sign_direction", VanillaBlockStates.ROTATION, BedrockStateTypes.ROTATION)
            .build();
    public static final StateMappingGroup BARREL = new StateMappingGroup.Builder()
            .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FACING_DIRECTION_LEGACY)
            .state("open_bit", VanillaBlockStates.OPEN, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup BED = new StateMappingGroup.Builder()
            .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
            .state("occupied_bit", VanillaBlockStates.OCCUPIED, BedrockStateTypes.BOOL)
            .state("head_piece_bit", VanillaBlockStates.BED_PART, BedrockStateTypes.BED_PART)
            .build();
    public static final StateMappingGroup BEDROCK = new StateMappingGroup.Builder()
            .state("infiniburn_bit", VanillaBlockStates.INFINIBURN, BedrockStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup BEE_NEST = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
                    .state("honey_level", VanillaBlockStates.HONEY_LEVEL, BedrockStateTypes.HONEY_LEVEL)
                    .build())
            // 1.16.2 switched facing_direction to direction
            .version(new Version(1, 16, 2), new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("honey_level", VanillaBlockStates.HONEY_LEVEL, BedrockStateTypes.HONEY_LEVEL)
                    .build())
            .build();
    public static final StateMappingGroup BELL = new StateMappingGroup.Builder()
            .state("attachment", VanillaBlockStates.BELL_ATTACHMENT, BedrockStateTypes.BELL_ATTACHMENT)
            .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY_INVERTED)
            .defaultInput("toggle_bit", false)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final VersionedStateMappingGroup BIG_DRIPLEAF = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("big_dripleaf_tilt", VanillaBlockStates.TILT, BedrockStateTypes.TILT)
                    .build())
            // 1.20.30 migrated big dripleaf to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("big_dripleaf_tilt", VanillaBlockStates.TILT, BedrockStateTypes.TILT)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup BIG_DRIPLEAF_HEAD = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .defaultInput("big_dripleaf_tilt", "none")
                    .build())
            // 1.20.30 migrated big dripleaf to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .defaultInput("big_dripleaf_tilt", "none")
                    .build()
            ).build();
    public static final StateMappingGroup BREWING_STAND = new StateMappingGroup.Builder()
            .state("brewing_stand_slot_a_bit", VanillaBlockStates.HAS_BOTTLE_0, BedrockStateTypes.BOOL)
            .state("brewing_stand_slot_b_bit", VanillaBlockStates.HAS_BOTTLE_1, BedrockStateTypes.BOOL)
            .state("brewing_stand_slot_c_bit", VanillaBlockStates.HAS_BOTTLE_2, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup BUBBLE_COLUMN = new StateMappingGroup.Builder()
            .state("drag_down", VanillaBlockStates.DRAG, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup BULB = new StateMappingGroup.Builder()
            .state("lit", VanillaBlockStates.LIT, BedrockStateTypes.BOOL)
            .state("powered_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup BUTTON = new StateMappingGroup.Builder()
            // Defaults are used for items (1.12)
            .defaultInput("facing_direction", 5)
            .combineOutputStates(
                    "facing_direction",
                    VanillaBlockStates.ATTACHMENT_TYPE,
                    VanillaBlockStates.FACING_HORIZONTAL,
                    BedrockStateTypes.FACING_DIRECTION_TO_ATTACHMENT_FACING_DIRECTION
            )
            .state("button_pressed_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup CAKE = new StateMappingGroup.Builder()
            .state("bite_counter", VanillaBlockStates.BITES, BedrockStateTypes.BITES)
            .build();
    public static final VersionedStateMappingGroup CALIBRATED_SCULK_SENSOR = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("powered_bit", VanillaBlockStates.SCULK_SENSOR_PHASE, BedrockStateTypes.BOOL_TO_SCULK_SENSOR_PHASE)
                    .defaultOutput(VanillaBlockStates.POWER, Power._0)
                    .build())
            // 1.20.0 migrated powered_bit to sculk_sensor_phase
            .version(new Version(1, 20, 0), new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("sculk_sensor_phase", VanillaBlockStates.SCULK_SENSOR_PHASE, BedrockStateTypes.SCULK_SENSOR_PHASE)
                    .defaultOutput(VanillaBlockStates.POWER, Power._0)
                    .build())
            // 1.20.30 migrated calibrated sculk sensor to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("sculk_sensor_phase", VanillaBlockStates.SCULK_SENSOR_PHASE, BedrockStateTypes.SCULK_SENSOR_PHASE)
                    .defaultOutput(VanillaBlockStates.POWER, Power._0)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup CAMPFIRE = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("extinguished", VanillaBlockStates.LIT, BedrockStateTypes.INVERSE_BOOL)
                    .defaultOutput(VanillaBlockStates.SIGNAL_FIRE, Bool.FALSE)
                    .build())
            // 1.20.30 migrated campfires to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("extinguished", VanillaBlockStates.LIT, BedrockStateTypes.INVERSE_BOOL)
                    .defaultOutput(VanillaBlockStates.SIGNAL_FIRE, Bool.FALSE)
                    .build()
            ).build();
    public static final StateMappingGroup CANDLE = new StateMappingGroup.Builder()
            .state("candles", VanillaBlockStates.CANDLES, BedrockStateTypes.CANDLES)
            .state("lit", VanillaBlockStates.LIT, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup CANDLE_CAKE = new StateMappingGroup.Builder()
            .state("lit", VanillaBlockStates.LIT, BedrockStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup CARDINAL_DIRECTION = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
                    .build())
            // 1.20.30 migrated these blocks to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .build()
            ).build();
    public static final StateMappingGroup CAULDRON = new StateMappingGroup.Builder()
            .state("fill_level", VanillaBlockStates.CAULDRON_LEVEL, BedrockStateTypes.CAULDRON_LEVEL)
            .build();
    public static final StateMappingGroup CAVE_VINES = new StateMappingGroup.Builder()
            .state("growing_plant_age", VanillaBlockStates.AGE_25, BedrockStateTypes.AGE_25)
            .build();
    public static final VersionedStateMappingGroup CHAIN = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.AXIS, Axis.Y)
                    .build())
            // 1.16.100 added pillar_axis
            .version(new Version(1, 16, 100), new StateMappingGroup.Builder()
                    .state("pillar_axis", VanillaBlockStates.AXIS, BedrockStateTypes.AXIS)
                    .build())
            .build();
    public static final VersionedStateMappingGroup CHEST = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
                    .defaultOutput(VanillaBlockStates.CHEST_TYPE, ChestType.SINGLE)
                    .build())
            // 1.20.40 migrated chest blocks to the cardinal_direction state
            .version(new Version(1, 20, 40), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .defaultOutput(VanillaBlockStates.CHEST_TYPE, ChestType.SINGLE)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup CHISELED_BOOKSHELF = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .multiState(
                            "books_stored",
                            List.of(
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED,
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED,
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED,
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED,
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED,
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED
                            ),
                            BedrockStateTypes.BOOKS_STORED
                    )
                    .defaultInput("last_interacted_slot", 0)
                    .build())
            // 1.19.60 removed the last_interacted_slot and made books_stored have position data
            .version(new Version(1, 19, 60), new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .multiState(
                            "books_stored",
                            List.of(
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED,
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED,
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED,
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED,
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED,
                                    VanillaBlockStates.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED
                            ),
                            BedrockStateTypes.MULTIFACE_DIRECTION
                    )
                    .build()
            ).build();
    public static final StateMappingGroup CHORUS_FLOWER = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_5, BedrockStateTypes.AGE_5)
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
            .state("age", VanillaBlockStates.AGE_2, BedrockStateTypes.AGE_2)
            .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
            .build();
    public static final StateMappingGroup COMMAND_BLOCK = new StateMappingGroup.Builder()
            .state("conditional_bit", VanillaBlockStates.CONDITIONAL, BedrockStateTypes.BOOL)
            .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FACING_DIRECTION_LEGACY)
            .build();
    public static final VersionedStateMappingGroup COMPARATOR = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("output_subtract_bit", VanillaBlockStates.MODE_COMPARATOR, BedrockStateTypes.COMPARATOR_MODE)
                    .state("output_lit_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
                    .build())
            // 1.20.30 migrated comparator to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("output_subtract_bit", VanillaBlockStates.MODE_COMPARATOR, BedrockStateTypes.COMPARATOR_MODE)
                    .state("output_lit_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup COMPOSTER = new StateMappingGroup.Builder()
            .state("composter_fill_level", VanillaBlockStates.COMPOSTER_LEVEL, BedrockStateTypes.COMPOSTER_LEVEL)
            .build();
    public static final StateMappingGroup CONNECTABLE_HORIZONTAL = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .build();
    public static final StateMappingGroup CORAL_FAN = new StateMappingGroup.Builder()
            .state("coral_fan_direction", VanillaBlockStates.CORAL_FAN_DIRECTION, BedrockStateTypes.CORAL_FAN_DIRECTION)
            .build();
    public static final StateMappingGroup CORAL_HANGING = new StateMappingGroup.Builder()
            .state("coral_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CORAL_DIRECTION)
            .build();
    public static final StateMappingGroup CRAFTER = new StateMappingGroup.Builder()
            .state("crafting", VanillaBlockStates.CRAFTING, BedrockStateTypes.BOOL)
            .state("triggered_bit", VanillaBlockStates.TRIGGERED, BedrockStateTypes.BOOL)
            .state("orientation", VanillaBlockStates.ORIENTATION, BedrockStateTypes.ORIENTATION)
            .build();
    public static final VersionedStateMappingGroup CREAKING_HEART = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("pillar_axis", VanillaBlockStates.AXIS, BedrockStateTypes.AXIS)
                    .state("active", VanillaBlockStates.CREAKING, BedrockStateTypes.CREAKING_BOOL)
                    .state("natural", VanillaBlockStates.NATURAL, BedrockStateTypes.BOOL)
                    .build())
            // 1.21.60 added creaking_heart_state instead of active
            .version(new Version(1, 21, 60), new StateMappingGroup.Builder()
                    .state("pillar_axis", VanillaBlockStates.AXIS, BedrockStateTypes.AXIS)
                    .state("creaking_heart_state", VanillaBlockStates.CREAKING, BedrockStateTypes.CREAKING)
                    .state("natural", VanillaBlockStates.NATURAL, BedrockStateTypes.BOOL)
                    .build())
            .build();
    public static final StateMappingGroup CROP = new StateMappingGroup.Builder()
            .state("growth", VanillaBlockStates.AGE_7, BedrockStateTypes.AGE_7)
            .build();
    public static final StateMappingGroup DAYLIGHT_DETECTOR = new StateMappingGroup.Builder()
            .state("redstone_signal", VanillaBlockStates.POWER, BedrockStateTypes.POWER)
            .build();
    public static final StateMappingGroup DECORATED_POT = new StateMappingGroup.Builder()
            .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY_INVERTED)
            .defaultOutput(VanillaBlockStates.CRACKED, Bool.FALSE)
            .build();
    public static final VersionedStateMappingGroup DOOR = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .multiState(
                            List.of(
                                    "door_hinge_bit",
                                    "upper_block_bit",
                                    "direction",
                                    "open_bit"
                            ),
                            List.of(
                                    VanillaBlockStates.DOOR_HINGE,
                                    VanillaBlockStates.HALF,
                                    VanillaBlockStates.FACING_HORIZONTAL,
                                    VanillaBlockStates.OPEN
                            ),
                            BedrockLegacyStateTypes.DOOR.mapOutputsToInputs(
                                    BedrockStateTypes.HINGE,
                                    BedrockStateTypes.HALF,
                                    BedrockStateTypes.DOOR_DIRECTION,
                                    BedrockStateTypes.BOOL
                            )
                    )
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .build())
            // 1.13 added the door states in both parts
            .version(new Version(1, 13, 0), new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.DOOR_DIRECTION)
                    .state("upper_block_bit", VanillaBlockStates.HALF, BedrockStateTypes.HALF)
                    .state("door_hinge_bit", VanillaBlockStates.DOOR_HINGE, BedrockStateTypes.HINGE)
                    .state("open_bit", VanillaBlockStates.OPEN, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .build())
            // 1.21.60 migrated direction to minecraft:cardinal_direction
            .version(new Version(1, 21, 60), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_DOOR)
                    .state("upper_block_bit", VanillaBlockStates.HALF, BedrockStateTypes.HALF)
                    .state("door_hinge_bit", VanillaBlockStates.DOOR_HINGE, BedrockStateTypes.HINGE)
                    .state("open_bit", VanillaBlockStates.OPEN, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .build())
            .build();
    public static final StateMappingGroup DOUBLE_BLOCK = new StateMappingGroup.Builder()
            .state("upper_block_bit", VanillaBlockStates.HALF, BedrockStateTypes.HALF)
            .build();
    public static final StateMappingGroup DRIED_GHAST = new StateMappingGroup.Builder()
            .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
            .state("rehydration_level", VanillaBlockStates.REHYDRATION_LEVEL, BedrockStateTypes.REHYDRATION_LEVEL)
            .build();
    public static final VersionedStateMappingGroup ENDER_CHEST = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
                    .build())
            // 1.20.40 migrated these blocks to the cardinal_direction state
            .version(new Version(1, 20, 40), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .build())
            .build();
    public static final VersionedStateMappingGroup END_PORTAL_FRAME = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("end_portal_eye_bit", VanillaBlockStates.EYE, BedrockStateTypes.BOOL)
                    .build())
            // 1.20.30 migrated end portal frame to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("end_portal_eye_bit", VanillaBlockStates.EYE, BedrockStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup END_ROD = new StateMappingGroup.Builder()
            .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FLIPPED_FACING_DIRECTION)
            .build();
    public static final StateMappingGroup FACING_DIRECTION_HORIZONTAL = new StateMappingGroup.Builder()
            .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
            .build();
    public static final StateMappingGroup FACING_DIRECTION_TRIGGERED = new StateMappingGroup.Builder()
            .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FACING_DIRECTION_LEGACY)
            .state("triggered_bit", VanillaBlockStates.TRIGGERED, BedrockStateTypes.BOOL)
            .build();
    public static final VersionedStateMappingGroup FACING_TO_BLOCK_FACE = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FACING_DIRECTION_LEGACY)
                    .build())
            // 1.20.30 migrated these blocks to block_face
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:block_face", VanillaBlockStates.FACING_ALL, BedrockStateTypes.BLOCK_FACE)
                    .build()
            ).build();
    public static final StateMappingGroup FARMLAND = new StateMappingGroup.Builder()
            .state("moisturized_amount", VanillaBlockStates.MOISTURE, BedrockStateTypes.MOISTURE)
            .build();
    public static final VersionedStateMappingGroup FENCE_GATE = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("in_wall_bit", VanillaBlockStates.IN_WALL, BedrockStateTypes.BOOL)
                    .state("open_bit", VanillaBlockStates.OPEN, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .build())
            // 1.21.60 migrated direction to minecraft:cardinal_direction
            .version(new Version(1, 21, 60), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("in_wall_bit", VanillaBlockStates.IN_WALL, BedrockStateTypes.BOOL)
                    .state("open_bit", VanillaBlockStates.OPEN, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .build()
            ).build();
    public static final StateMappingGroup FIRE = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_15, BedrockStateTypes.AGE_15)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.UP, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .build();
    public static final StateMappingGroup FLOWER_POT = new StateMappingGroup.Builder()
            .defaultInput("update_bit", false)
            .build();
    public static final VersionedStateMappingGroup FRAME = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("weirdo_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.WEIRDO_DIRECTION_ALL)
                    .state("item_frame_map_bit", VanillaBlockStates.BEDROCK_FRAME_MAP_BIT, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.BEDROCK_FRAME_PHOTO_BIT, Bool.FALSE)
                    .build())
            // 1.13 added further directions
            .version(new Version(1, 13, 0), new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FACING_DIRECTION_LEGACY)
                    .state("item_frame_map_bit", VanillaBlockStates.BEDROCK_FRAME_MAP_BIT, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.BEDROCK_FRAME_PHOTO_BIT, Bool.FALSE)
                    .build())
            // 1.17.30 added photo bit
            .version(new Version(1, 17, 30), new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FACING_DIRECTION_LEGACY)
                    .state("item_frame_map_bit", VanillaBlockStates.BEDROCK_FRAME_MAP_BIT, BedrockStateTypes.BOOL)
                    .state("item_frame_photo_bit", VanillaBlockStates.BEDROCK_FRAME_PHOTO_BIT, BedrockStateTypes.BOOL)
                    .build())
            .build();
    public static final VersionedStateMappingGroup FROGLIGHT = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.AXIS, Axis.Y)
                    .build())
            // 1.18.30 added pillar_axis
            .version(new Version(1, 18, 30), new StateMappingGroup.Builder()
                    .state("pillar_axis", VanillaBlockStates.AXIS, BedrockStateTypes.AXIS)
                    .build())
            .build();
    public static final StateMappingGroup GRINDSTONE = new StateMappingGroup.Builder()
            .state("attachment", VanillaBlockStates.GRINDSTONE_ATTACHMENT_TYPE, BedrockStateTypes.GRINDSTONE_ATTACHMENT)
            .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
            .build();
    public static final StateMappingGroup GROWTH = new StateMappingGroup.Builder()
            .state("growth", VanillaBlockStates.AGE_7, BedrockStateTypes.AGE_7)
            .build();
    public static final StateMappingGroup HANGING_SIGN = new StateMappingGroup.Builder()
            // Use ground_sign_direction first but still convert facing_direction
            .state("ground_sign_direction", VanillaBlockStates.ROTATION, BedrockStateTypes.ROTATION)
            .state("facing_direction", VanillaBlockStates.ROTATION, BedrockStateTypes.FACING_DIRECTION_TO_ROTATION)
            .state("attached_bit", VanillaBlockStates.ATTACHED, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup HANGING_WALL_SIGN = new StateMappingGroup.Builder()
            // Use facing_direction first but still convert ground_sign_direction
            .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
            .state("ground_sign_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
            .state("attached_bit", VanillaBlockStates.ATTACHED, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup HOPPER = new StateMappingGroup.Builder()
            .state("toggle_bit", VanillaBlockStates.ENABLED, BedrockStateTypes.INVERSE_BOOL)
            .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL_DOWN, BedrockStateTypes.FACING_DIRECTION_LEGACY_DOWN)
            .build();
    public static final VersionedStateMappingGroup JIGSAW = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.ORIENTATION, BedrockStateTypes.FACING_DIRECTION_TO_ORIENTATION)
                    .build())
            // 1.16.0 added rotation
            .version(new Version(1, 16, 0), new StateMappingGroup.Builder()
                    .combineInputStates("facing_direction", "rotation", VanillaBlockStates.ORIENTATION, BedrockStateTypes.FACING_DIRECTION_ROTATION_TO_ORIENTATION)
                    .build()
            ).build();
    public static final StateMappingGroup JUKEBOX = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.HAS_RECORD, Bool.FALSE)
            .build();
    public static final VersionedStateMappingGroup KELP = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("age", VanillaBlockStates.AGE_25, BedrockStateTypes.AGE_15_TO_25)
                    .build())
            // 1.14.0 migrated age to kelp_age
            .version(new Version(1, 14, 0), new StateMappingGroup.Builder()
                    .state("kelp_age", VanillaBlockStates.AGE_25, BedrockStateTypes.AGE_25)
                    .build()
            ).build();
    public static final StateMappingGroup LANTERN = new StateMappingGroup.Builder()
            .state("hanging", VanillaBlockStates.HANGING, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup LAYER_BLOCK = new StateMappingGroup.Builder()
            .state("height", VanillaBlockStates.LAYERS, BedrockStateTypes.LAYERS)
            .build();
    public static final VersionedStateMappingGroup LEAF_LITTER = new StateMappingGroup.Builder()
            .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
            .state("growth", VanillaBlockStates.SEGMENT_AMOUNT, BedrockStateTypes.SEGMENT_COUNT)
            .build();
    public static final StateMappingGroup LEAVES = new StateMappingGroup.Builder()
            .state("persistent_bit", VanillaBlockStates.PERSISTENT, BedrockStateTypes.BOOL)
            .state("update_bit", VanillaBlockStates.UPDATE, BedrockStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.DISTANCE, Distance._1)
            .build();
    public static final VersionedStateMappingGroup LECTERN = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("powered_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.HAS_BOOK, Bool.FALSE)
                    .build())
            // 1.20.30 migrated lectern to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("powered_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.HAS_BOOK, Bool.FALSE)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup LEVER = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .multiState(
                            List.of("facing_direction", "open_bit"),
                            List.of(VanillaBlockStates.ATTACHMENT_TYPE, VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.POWERED),
                            BedrockStateTypes.LEVER_FACING_DIRECTION_POWER_LEGACY
                    )
                    .build())
            // 1.13 added the lever_direction state
            .version(new Version(1, 13, 0), new StateMappingGroup.Builder()
                    .multiState(
                            List.of("lever_direction", "open_bit"),
                            List.of(VanillaBlockStates.ATTACHMENT_TYPE, VanillaBlockStates.FACING_HORIZONTAL, VanillaBlockStates.POWERED),
                            BedrockStateTypes.LEVER_DIRECTION_POWER
                    )
                    .build()
            ).build();
    public static final StateMappingGroup LIGHTNING_ROD = new StateMappingGroup.Builder()
            .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FACING_DIRECTION_LEGACY)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final StateMappingGroup LIGHT_BLOCK = new StateMappingGroup.Builder()
            .state("block_light_level", VanillaBlockStates.LIGHT_LEVEL, BedrockStateTypes.LIGHT)
            .build();
    public static final StateMappingGroup LIQUID = new StateMappingGroup.Builder()
            .state("liquid_depth", VanillaBlockStates.LIQUID_LEVEL, BedrockStateTypes.FLOWING_LEVEL)
            .build();
    public static final StateMappingGroup LOOM = new StateMappingGroup.Builder()
            .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
            .build();
    public static final VersionedStateMappingGroup MANGROVE_PROPAGULE = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("growth", VanillaBlockStates.AGE_4, BedrockStateTypes.AGE_7_TO_4)
                    .defaultOutput(VanillaBlockStates.STAGE, Stage._0)
                    .defaultInput("facing_direction", 0)
                    .build())
            // 1.19.0 added hanging, removed facing_direction and uses propagule_stage
            .version(new Version(1, 19, 0), new StateMappingGroup.Builder()
                    .state("propagule_stage", VanillaBlockStates.AGE_4, BedrockStateTypes.AGE_4)
                    .defaultOutput(VanillaBlockStates.STAGE, Stage._0)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup MUDDY_MANGROVE_ROOTS = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.AXIS, Axis.Y)
                    .build())
            // 1.19.20 added pillar_axis
            .version(new Version(1, 19, 20), new StateMappingGroup.Builder()
                    .state("pillar_axis", VanillaBlockStates.AXIS, BedrockStateTypes.AXIS)
                    .build())
            .build();
    public static final VersionedStateMappingGroup MULTIFACE = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .multiState(
                            "multi_face_direction_bits",
                            List.of(
                                    VanillaBlockStates.DOWN,
                                    VanillaBlockStates.UP,
                                    VanillaBlockStates.NORTH,
                                    VanillaBlockStates.SOUTH,
                                    VanillaBlockStates.WEST,
                                    VanillaBlockStates.EAST
                            ),
                            BedrockStateTypes.MULTIFACE_DIRECTION
                    )
                    .build())
            // 1.18.10 changed the order of faces for multiface
            .version(new Version(1, 18, 10), new StateMappingGroup.Builder()
                    .multiState(
                            "multi_face_direction_bits",
                            List.of(
                                    VanillaBlockStates.DOWN,
                                    VanillaBlockStates.UP,
                                    VanillaBlockStates.SOUTH,
                                    VanillaBlockStates.WEST,
                                    VanillaBlockStates.NORTH,
                                    VanillaBlockStates.EAST
                            ),
                            BedrockStateTypes.MULTIFACE_DIRECTION
                    )
                    .build())
            .build();
    public static final StateMappingGroup MUSHROOM_BLOCK = new StateMappingGroup.Builder()
            .multiState(
                    "huge_mushroom_bits",
                    List.of(
                            VanillaBlockStates.NORTH,
                            VanillaBlockStates.EAST,
                            VanillaBlockStates.SOUTH,
                            VanillaBlockStates.WEST,
                            VanillaBlockStates.UP,
                            VanillaBlockStates.DOWN
                    ),
                    BedrockStateTypes.MUSHROOM_FACE_BITS
            )
            // Default states of all present in the case this is an item and doesn't have data
            .defaultInput("huge_mushroom_bits", 14)
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
    public static final VersionedStateMappingGroup OBSERVER = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FACING_DIRECTION_LEGACY)
                    .state("powered_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
                    .build())
            // 1.20.10 migrated observers to facing_direction
            .version(new Version(1, 20, 10), new StateMappingGroup.Builder()
                    .state("minecraft:facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FACING_DIRECTION)
                    .state("powered_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup PALE_HANGING_MOSS = new StateMappingGroup.Builder()
            .state("tip", VanillaBlockStates.TIP, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup PALE_MOSS_CARPET = new StateMappingGroup.Builder()
            .state("pale_moss_carpet_side_north", VanillaBlockStates.WALL_NORTH, BedrockStateTypes.WALL_CONNECTION)
            .state("pale_moss_carpet_side_east", VanillaBlockStates.WALL_EAST, BedrockStateTypes.WALL_CONNECTION)
            .state("pale_moss_carpet_side_south", VanillaBlockStates.WALL_SOUTH, BedrockStateTypes.WALL_CONNECTION)
            .state("pale_moss_carpet_side_west", VanillaBlockStates.WALL_WEST, BedrockStateTypes.WALL_CONNECTION)
            .state("upper_block_bit", VanillaBlockStates.BOTTOM, BedrockStateTypes.INVERSE_BOOL)
            .build();
    public static final VersionedStateMappingGroup PILLAR_BLOCK = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.AXIS, BedrockStateTypes.AXIS_DIRECTION)
                    .build())
            // 1.13 uses a new pillar_axis state
            .version(new Version(1, 13, 0), new StateMappingGroup.Builder()
                    .state("pillar_axis", VanillaBlockStates.AXIS, BedrockStateTypes.AXIS)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup PINK_PETALS = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("growth", VanillaBlockStates.FLOWER_AMOUNT, BedrockStateTypes.FLOWER_COUNT)
                    .build())
            // 1.20.30 migrated pink petals to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("growth", VanillaBlockStates.FLOWER_AMOUNT, BedrockStateTypes.FLOWER_COUNT)
                    .build()
            ).build();
    public static final StateMappingGroup PISTON = new StateMappingGroup.Builder()
            .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FLIPPED_FACING_DIRECTION)
            .defaultOutput(VanillaBlockStates.EXTENDED, Bool.FALSE)
            .build();
    public static final StateMappingGroup PISTON_HEAD = new StateMappingGroup.Builder()
            .state("facing_direction", VanillaBlockStates.FACING_ALL, BedrockStateTypes.FLIPPED_FACING_DIRECTION)
            .defaultOutput(VanillaBlockStates.SHORT, Bool.FALSE)
            .build();
    public static final StateMappingGroup PITCHER_CROP = new StateMappingGroup.Builder()
            .state("growth", VanillaBlockStates.AGE_4, BedrockStateTypes.AGE_7_TO_4)
            .state("upper_block_bit", VanillaBlockStates.HALF, BedrockStateTypes.HALF)
            .build();
    public static final StateMappingGroup POINTED_DRIPSTONE = new StateMappingGroup.Builder()
            .state("dripstone_thickness", VanillaBlockStates.DRIPSTONE_THICKNESS, BedrockStateTypes.DRIPSTONE_THICKNESS)
            .state("hanging", VanillaBlockStates.VERTICAL_DIRECTION, BedrockStateTypes.VERTICAL_DIRECTION)
            .build();
    public static final StateMappingGroup PORTAL = new StateMappingGroup.Builder()
            .state("portal_axis", VanillaBlockStates.AXIS_HORIZONTAL, BedrockStateTypes.AXIS_HORIZONTAL)
            .build();
    public static final StateMappingGroup POWERED_RAIL = new StateMappingGroup.Builder()
            .state("rail_data_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
            .state("rail_direction", VanillaBlockStates.RAIL_SHAPE_STRAIGHT, BedrockStateTypes.STRAIGHT_RAIL_SHAPE)
            .build();
    public static final StateMappingGroup PRESSURE_PLATE = new StateMappingGroup.Builder()
            .state("redstone_signal", VanillaBlockStates.POWERED, BedrockStateTypes.REDSTONE_SIGNAL_TO_BOOL)
            .build();
    public static final VersionedStateMappingGroup PUMPKIN = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .build())
            // 1.20.0 migrated pumpkins to cardinal_direction
            .version(new Version(1, 20, 0), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .build()
            ).build();
    public static final StateMappingGroup RAIL = new StateMappingGroup.Builder()
            .state("rail_direction", VanillaBlockStates.RAIL_SHAPE, BedrockStateTypes.RAIL_SHAPE)
            .build();
    public static final VersionedStateMappingGroup REPEATER = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("repeater_delay", VanillaBlockStates.DELAY, BedrockStateTypes.DELAY)
                    .defaultOutput(VanillaBlockStates.LOCKED, Bool.FALSE)
                    .build())
            // 1.20.30 migrated repeater to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("repeater_delay", VanillaBlockStates.DELAY, BedrockStateTypes.DELAY)
                    .defaultOutput(VanillaBlockStates.LOCKED, Bool.FALSE)
                    .build()
            ).build();
    public static final StateMappingGroup RESPAWN_ANCHOR = new StateMappingGroup.Builder()
            .state("respawn_anchor_charge", VanillaBlockStates.RESPAWN_ANCHOR_CHARGES, BedrockStateTypes.RESPAWN_ANCHOR_CHARGE)
            .build();
    public static final StateMappingGroup SAPLING = new StateMappingGroup.Builder()
            .state("age_bit", VanillaBlockStates.STAGE, BedrockStateTypes.STAGE)
            .build();
    public static final StateMappingGroup SCAFFOLDING = new StateMappingGroup.Builder()
            .state("stability_check", VanillaBlockStates.BOTTOM, BedrockStateTypes.BOOL)
            .state("stability", VanillaBlockStates.STABILITY_DISTANCE, BedrockStateTypes.STABILITY_DISTANCE)
            .build();
    public static final VersionedStateMappingGroup SCULK_CATALYST = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.BLOOM, Bool.FALSE)
                    .build())
            // 1.17.40 added bloom
            .version(new Version(1, 17, 40), new StateMappingGroup.Builder()
                    .state("bloom", VanillaBlockStates.BLOOM, BedrockStateTypes.BOOL)
                    .build())
            .build();
    public static final VersionedStateMappingGroup SCULK_SENSOR = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("powered_bit", VanillaBlockStates.SCULK_SENSOR_PHASE, BedrockStateTypes.BOOL_TO_SCULK_SENSOR_PHASE)
                    .defaultOutput(VanillaBlockStates.POWER, Power._0)
                    .build())
            // 1.20.0 changed powered_bit to phase_remap
            .version(new Version(1, 20, 0), new StateMappingGroup.Builder()
                    .state("sculk_sensor_phase", VanillaBlockStates.SCULK_SENSOR_PHASE, BedrockStateTypes.SCULK_SENSOR_PHASE)
                    .defaultOutput(VanillaBlockStates.POWER, Power._0)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup SCULK_SHRIEKER = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("active", VanillaBlockStates.SHRIEKING, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.CAN_SUMMON, Bool.FALSE)
                    .build())
            // 1.19.0 added can_summon
            .version(new Version(1, 19, 0), new StateMappingGroup.Builder()
                    .state("active", VanillaBlockStates.SHRIEKING, BedrockStateTypes.BOOL)
                    .state("can_summon", VanillaBlockStates.CAN_SUMMON, BedrockStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup SEA_PICKLE = new StateMappingGroup.Builder()
            .state("cluster_count", VanillaBlockStates.PICKLES, BedrockStateTypes.PICKLES)
            .state("dead_bit", VanillaBlockStates.DEAD, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup SHULKER_BOX = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.FACING_ALL, FacingDirection.UP)
            .build();
    public static final StateMappingGroup SIGN = new StateMappingGroup.Builder()
            .state("ground_sign_direction", VanillaBlockStates.ROTATION, BedrockStateTypes.ROTATION)
            .build();
    public static final VersionedStateMappingGroup SKULL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.ROTATION, BedrockStateTypes.ROTATION)
                    .state("no_drop_bit", VanillaBlockStates.NO_DROP, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .build())
            // 1.18.10 removed no_drop_bit
            .version(new Version(1, 18, 10), new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.ROTATION, BedrockStateTypes.ROTATION)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .defaultOutput(VanillaBlockStates.NO_DROP, Bool.FALSE)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup SLAB_DOUBLE = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultInput("top_slot_bit", false)
                    .build())
            // 1.20.30 migrated slabs to use vertical_half state
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .defaultInput("minecraft:vertical_half", "bottom")
                    .build())
            .build();
    public static final VersionedStateMappingGroup SLAB_HALF = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("top_slot_bit", VanillaBlockStates.SLAB_TYPE, BedrockStateTypes.SLAB_TYPE_LEGACY)
                    .build())
            // 1.20.30 migrated slabs to use vertical_half state
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:vertical_half", VanillaBlockStates.SLAB_TYPE, BedrockStateTypes.SLAB_TYPE)
                    .build())
            .build();
    public static final VersionedStateMappingGroup SMALL_DRIPLEAF = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
                    .state("upper_block_bit", VanillaBlockStates.HALF, BedrockStateTypes.HALF)
                    .build())
            // 1.20.30 migrated small dripleaf to cardinal_direction
            .version(new Version(1, 20, 30), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("upper_block_bit", VanillaBlockStates.HALF, BedrockStateTypes.HALF)
                    .build()
            ).build();
    public static final StateMappingGroup SNIFFER_EGG = new StateMappingGroup.Builder()
            .state("cracked_state", VanillaBlockStates.HATCH, BedrockStateTypes.HATCH)
            .build();
    public static final StateMappingGroup SNOWY_BLOCK = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.SNOWY, Bool.FALSE)
            .build();
    public static final StateMappingGroup SOUL_FIRE = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_15, BedrockStateTypes.AGE_15)
            .build();
    public static final StateMappingGroup STAIRS = new StateMappingGroup.Builder()
            .state("weirdo_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.WEIRDO_DIRECTION)
            .state("upside_down_bit", VanillaBlockStates.HALF, BedrockStateTypes.HALF)
            .defaultOutput(VanillaBlockStates.STAIR_SHAPE, StairShape.STRAIGHT)
            .build();
    public static final VersionedStateMappingGroup STEM_FACING = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.FACING_HORIZONTAL, FacingDirectionHorizontal.NORTH)
                    .build())
            // 1.16.0 added facing_direction
            .version(new Version(1, 16, 0), new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup STONECUTTER = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
                    .build())
            // 1.20.40 migrated stonecutter to the cardinal_direction state
            .version(new Version(1, 20, 40), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .build())
            .build();
    public static final VersionedStateMappingGroup STRIPPED_NETHER_WOOD = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("pillar_axis", VanillaBlockStates.AXIS, BedrockStateTypes.AXIS)
                    .defaultInput("deprecated", 0)
                    .build())
            // 1.16.210 removed deprecated
            .version(new Version(1, 16, 210), new StateMappingGroup.Builder()
                    .state("pillar_axis", VanillaBlockStates.AXIS, BedrockStateTypes.AXIS)
                    .build())
            .build();
    public static final StateMappingGroup STRUCTURE_BLOCK = new StateMappingGroup.Builder()
            .state("structure_block_type", VanillaBlockStates.STRUCTURE_BLOCK_MODE, BedrockStateTypes.STRUCTURE_BLOCK_MODE)
            .build();
    public static final VersionedStateMappingGroup STRUCTURE_VOID = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("structure_void_type", VanillaBlockStates.STRUCTURE_VOID_TYPE, BedrockStateTypes.STRUCTURE_VOID_TYPE)
                    .build())
            // 1.21.30 removed structure_void_type
            .version(new Version(1, 21, 30), new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.STRUCTURE_VOID_TYPE, StructureVoidType.VOID)
                    .build())
            .build();
    public static final StateMappingGroup SUSPICIOUS_BLOCK = new StateMappingGroup.Builder()
            .state("brushed_progress", VanillaBlockStates.DUSTED, BedrockStateTypes.BRUSHED_PROGRESS)
            .defaultInput("hanging", false)
            .build();
    public static final VersionedStateMappingGroup TNT = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("explode_bit", VanillaBlockStates.UNSTABLE, BedrockStateTypes.BOOL)
                    .state("allow_underwater_bit", VanillaBlockStates.UNDERWATER, BedrockStateTypes.BOOL)
                    .build())
            // 1.21.30 moved allow_underwater_bit to an identifier
            .version(new Version(1, 21, 30), new StateMappingGroup.Builder()
                    .state("explode_bit", VanillaBlockStates.UNSTABLE, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.UNDERWATER, Bool.FALSE)
                    .build())
            .build();
    public static final StateMappingGroup TORCHFLOWER_CROP = new StateMappingGroup.Builder()
            .state("growth", VanillaBlockStates.AGE_1, BedrockStateTypes.AGE_7_TO_1)
            .build();
    public static final StateMappingGroup TORCH_FACING = new StateMappingGroup.Builder()
            .state("torch_facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.TORCH_FACING_DIRECTION)
            .build();
    public static final StateMappingGroup TRAPDOOR = new StateMappingGroup.Builder()
            .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.WEIRDO_DIRECTION)
            .state("upside_down_bit", VanillaBlockStates.HALF, BedrockStateTypes.HALF)
            .state("open_bit", VanillaBlockStates.OPEN, BedrockStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
            .build();
    public static final VersionedStateMappingGroup TRIAL_SPAWNER = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("trial_spawner_state", VanillaBlockStates.TRIAL_SPAWNER_STATE, BedrockStateTypes.TRIAL_SPAWNER_STATE)
                    .defaultOutput(VanillaBlockStates.OMINOUS, Bool.FALSE)
                    .build())
            // 1.21.0 added ominous
            .version(new Version(1, 21, 0), new StateMappingGroup.Builder()
                    .state("trial_spawner_state", VanillaBlockStates.TRIAL_SPAWNER_STATE, BedrockStateTypes.TRIAL_SPAWNER_STATE)
                    .state("ominous", VanillaBlockStates.OMINOUS, BedrockStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup TRIPWIRE = new StateMappingGroup.Builder()
            .state("attached_bit", VanillaBlockStates.ATTACHED, BedrockStateTypes.BOOL)
            .state("disarmed_bit", VanillaBlockStates.DISARMED, BedrockStateTypes.BOOL)
            .state("powered_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
            .state("suspended_bit", VanillaBlockStates.SUSPENDED, BedrockStateTypes.BOOL)
            .defaultOutput(VanillaBlockStates.NORTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.EAST, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.SOUTH, Bool.FALSE)
            .defaultOutput(VanillaBlockStates.WEST, Bool.FALSE)
            .build();
    public static final StateMappingGroup TRIPWIRE_HOOK = new StateMappingGroup.Builder()
            .state("attached_bit", VanillaBlockStates.ATTACHED, BedrockStateTypes.BOOL)
            .state("direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION_LEGACY)
            .state("powered_bit", VanillaBlockStates.POWERED, BedrockStateTypes.BOOL)
            .build();
    public static final StateMappingGroup TURTLE_EGG = new StateMappingGroup.Builder()
            .state("turtle_egg_count", VanillaBlockStates.EGGS, BedrockStateTypes.EGGS)
            .state("cracked_state", VanillaBlockStates.HATCH, BedrockStateTypes.HATCH)
            .build();
    public static final StateMappingGroup TWISTING_VINES = new StateMappingGroup.Builder()
            .state("twisting_vines_age", VanillaBlockStates.AGE_25, BedrockStateTypes.AGE_25)
            .build();
    public static final VersionedStateMappingGroup VAULT = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("vault_state", VanillaBlockStates.VAULT_STATE, BedrockStateTypes.VAULT_STATE)
                    .defaultOutput(VanillaBlockStates.OMINOUS, Bool.FALSE)
                    .build())
            // 1.21.0 added ominous
            .version(new Version(1, 21, 0), new StateMappingGroup.Builder()
                    .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
                    .state("vault_state", VanillaBlockStates.VAULT_STATE, BedrockStateTypes.VAULT_STATE)
                    .state("ominous", VanillaBlockStates.OMINOUS, BedrockStateTypes.BOOL)
                    .build()
            ).build();
    public static final StateMappingGroup VERTICAL_GROWING = new StateMappingGroup.Builder()
            .state("age", VanillaBlockStates.AGE_15, BedrockStateTypes.AGE_15)
            .build();
    public static final StateMappingGroup VINE = new StateMappingGroup.Builder()
            .multiState(
                    "vine_direction_bits",
                    List.of(
                            VanillaBlockStates.SOUTH,
                            VanillaBlockStates.WEST,
                            VanillaBlockStates.NORTH,
                            VanillaBlockStates.EAST
                    ),
                    BedrockStateTypes.BOOLS_TO_VINE_DIRECTION)
            .build();
    public static final VersionedStateMappingGroup WALL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.WALL_NORTH, WallHeight.NONE)
                    .defaultOutput(VanillaBlockStates.WALL_EAST, WallHeight.NONE)
                    .defaultOutput(VanillaBlockStates.WALL_SOUTH, WallHeight.NONE)
                    .defaultOutput(VanillaBlockStates.WALL_WEST, WallHeight.NONE)
                    .defaultOutput(VanillaBlockStates.UP, Bool.FALSE)
                    .build())
            // 1.16.0 added wall_connection_types and wall_post_bit
            .version(new Version(1, 16, 0), new StateMappingGroup.Builder()
                    .state("wall_connection_type_north", VanillaBlockStates.WALL_NORTH, BedrockStateTypes.WALL_CONNECTION)
                    .state("wall_connection_type_east", VanillaBlockStates.WALL_EAST, BedrockStateTypes.WALL_CONNECTION)
                    .state("wall_connection_type_south", VanillaBlockStates.WALL_SOUTH, BedrockStateTypes.WALL_CONNECTION)
                    .state("wall_connection_type_west", VanillaBlockStates.WALL_WEST, BedrockStateTypes.WALL_CONNECTION)
                    .state("wall_post_bit", VanillaBlockStates.UP, BedrockStateTypes.BOOL)
                    .build()
            ).build();
    public static final VersionedStateMappingGroup WALL_SKULL = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
                    .state("no_drop_bit", VanillaBlockStates.NO_DROP, BedrockStateTypes.BOOL)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .build())
            // 1.18.10 removed no_drop_bit
            .version(new Version(1, 18, 10), new StateMappingGroup.Builder()
                    .state("facing_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.FACING_DIRECTION_LEGACY_HORIZONTAL)
                    .defaultOutput(VanillaBlockStates.POWERED, Bool.FALSE)
                    .defaultOutput(VanillaBlockStates.NO_DROP, Bool.FALSE)
                    .build()
            ).build();
    public static final StateMappingGroup WEEPING_VINES = new StateMappingGroup.Builder()
            .state("weeping_vines_age", VanillaBlockStates.AGE_25, BedrockStateTypes.AGE_25)
            .build();
    public static final StateMappingGroup WEIGHTED_PRESSURE_PLATE = new StateMappingGroup.Builder()
            .state("redstone_signal", VanillaBlockStates.POWER, BedrockStateTypes.POWER)
            .build();
    public static final VersionedStateMappingGroup WILDFLOWERS = new StateMappingGroup.Builder()
            .state("minecraft:cardinal_direction", VanillaBlockStates.FACING_HORIZONTAL, BedrockStateTypes.CARDINAL_DIRECTION)
            .state("growth", VanillaBlockStates.FLOWER_AMOUNT, BedrockStateTypes.FLOWER_COUNT)
            .build();
    public static final StateMappingGroup WIRE = new StateMappingGroup.Builder()
            .state("redstone_signal", VanillaBlockStates.POWER, BedrockStateTypes.POWER)
            .defaultOutput(VanillaBlockStates.REDSTONE_NORTH, RedstoneConnection.NONE)
            .defaultOutput(VanillaBlockStates.REDSTONE_EAST, RedstoneConnection.NONE)
            .defaultOutput(VanillaBlockStates.REDSTONE_SOUTH, RedstoneConnection.NONE)
            .defaultOutput(VanillaBlockStates.REDSTONE_WEST, RedstoneConnection.NONE)
            .build();
    public static final VersionedStateMappingGroup WOOD_BLOCK = new VersionedStateMappingGroup.Builder()
            .defaults(new StateMappingGroup.Builder()
                    .defaultOutput(VanillaBlockStates.AXIS, Axis.Y)
                    .build())
            // 1.13 has an axis state
            .version(new Version(1, 13, 0), new StateMappingGroup.Builder()
                    .state("pillar_axis", VanillaBlockStates.AXIS, BedrockStateTypes.AXIS)
                    .build()
            ).build();
}