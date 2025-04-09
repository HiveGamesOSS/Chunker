package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.*;

import java.util.Set;

/**
 * Block states which exist in Minecraft vanilla. Some of these states may exist in Java Edition, Bedrock Edition or
 * both.
 */
public class VanillaBlockStates {
    /**
     * Default states which are applied to every block. This is initialized below but contains WATERLOGGED. This is
     * because every block in Bedrock is waterloggable but only some in Java are.
     */
    public static final Set<BlockState<?>> DEFAULT_BLOCK_STATES;

    // States which are always present
    public static final BlockState<Bool> WATERLOGGED = new BlockState<>("waterlogged", Bool.FALSE, Bool::values);

    // States
    public static final BlockState<Age_1> AGE_1 = new BlockState<>("age", Age_1._0, Age_1::values);
    public static final BlockState<Age_15> AGE_15 = new BlockState<>("age", Age_15._0, Age_15::values);
    public static final BlockState<Age_2> AGE_2 = new BlockState<>("age", Age_2._0, Age_2::values);
    public static final BlockState<Age_25> AGE_25 = new BlockState<>("age", Age_25._0, Age_25::values);
    public static final BlockState<Age_3> AGE_3 = new BlockState<>("age", Age_3._0, Age_3::values);
    public static final BlockState<Age_4> AGE_4 = new BlockState<>("age", Age_4._0, Age_4::values);
    public static final BlockState<Age_5> AGE_5 = new BlockState<>("age", Age_5._0, Age_5::values);
    public static final BlockState<Age_7> AGE_7 = new BlockState<>("age", Age_7._0, Age_7::values);
    public static final BlockState<Bool> ATTACHED = new BlockState<>("attached", Bool.FALSE, Bool::values);
    public static final BlockState<AttachmentType> ATTACHMENT_TYPE = new BlockState<>("face", AttachmentType.FLOOR, AttachmentType::values);
    public static final BlockState<Axis> AXIS = new BlockState<>("axis", Axis.Y, Axis::values);
    public static final BlockState<AxisHorizontal> AXIS_HORIZONTAL = new BlockState<>("axis", AxisHorizontal.X, AxisHorizontal::values);
    public static final BlockState<BambooLeafSize> BAMBOO_LEAVES = new BlockState<>("leaves", BambooLeafSize.NONE, BambooLeafSize::values);
    public static final BlockState<Bool> BEDROCK_FRAME_MAP_BIT = new BlockState<>("bedrock_frame_map_bit", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> BEDROCK_FRAME_PHOTO_BIT = new BlockState<>("bedrock_frame_photo_bit", Bool.FALSE, Bool::values);
    public static final BlockState<BedPart> BED_PART = new BlockState<>("part", BedPart.FOOT, BedPart::values);
    public static final BlockState<BellAttachmentType> BELL_ATTACHMENT = new BlockState<>("attachment", BellAttachmentType.FLOOR, BellAttachmentType::values);
    public static final BlockState<Bool> BERRIES = new BlockState<>("berries", Bool.FALSE, Bool::values);
    public static final BlockState<Bites> BITES = new BlockState<>("bites", Bites._0, Bites::values);
    public static final BlockState<Bool> BLOOM = new BlockState<>("bloom", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> BOTTOM = new BlockState<>("bottom", Bool.FALSE, Bool::values);
    public static final BlockState<Candles> CANDLES = new BlockState<>("candles", Candles._1, Candles::values);
    public static final BlockState<Bool> CAN_SUMMON = new BlockState<>("can_summon", Bool.FALSE, Bool::values);
    public static final BlockState<CauldronLevel> CAULDRON_LEVEL = new BlockState<>("level", CauldronLevel._1, CauldronLevel::values);
    public static final BlockState<ChestType> CHEST_TYPE = new BlockState<>("type", ChestType.SINGLE, ChestType::values);
    public static final BlockState<Bool> CHISELED_BOOKSHELF_SLOT_0_OCCUPIED = new BlockState<>("slot_0_occupied", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> CHISELED_BOOKSHELF_SLOT_1_OCCUPIED = new BlockState<>("slot_1_occupied", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> CHISELED_BOOKSHELF_SLOT_2_OCCUPIED = new BlockState<>("slot_2_occupied", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> CHISELED_BOOKSHELF_SLOT_3_OCCUPIED = new BlockState<>("slot_3_occupied", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> CHISELED_BOOKSHELF_SLOT_4_OCCUPIED = new BlockState<>("slot_4_occupied", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> CHISELED_BOOKSHELF_SLOT_5_OCCUPIED = new BlockState<>("slot_5_occupied", Bool.FALSE, Bool::values);
    public static final BlockState<ComposterLevel> COMPOSTER_LEVEL = new BlockState<>("level", ComposterLevel._0, ComposterLevel::values);
    public static final BlockState<Bool> CONDITIONAL = new BlockState<>("conditional", Bool.FALSE, Bool::values);
    public static final BlockState<CoralFanDirection> CORAL_FAN_DIRECTION = new BlockState<>("coral_fan_direction", CoralFanDirection.NORTH_SOUTH, CoralFanDirection::values);
    public static final BlockState<Bool> CRACKED = new BlockState<>("cracked", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> CRAFTING = new BlockState<>("crafting", Bool.FALSE, Bool::values);
    public static final BlockState<Creaking> CREAKING = new BlockState<>("creaking", Creaking.DISABLED, Creaking::values);
    public static final BlockState<Bool> DEAD = new BlockState<>("dead", Bool.FALSE, Bool::values);
    public static final BlockState<Delay> DELAY = new BlockState<>("delay", Delay._1, Delay::values);
    public static final BlockState<Bool> DISARMED = new BlockState<>("disarmed", Bool.FALSE, Bool::values);
    public static final BlockState<Distance> DISTANCE = new BlockState<>("distance", Distance._1, Distance::values);
    public static final BlockState<HingeSide> DOOR_HINGE = new BlockState<>("hinge", HingeSide.LEFT, HingeSide::values);
    public static final BlockState<Bool> DOWN = new BlockState<>("down", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> DRAG = new BlockState<>("drag", Bool.FALSE, Bool::values);
    public static final BlockState<DripstoneThickness> DRIPSTONE_THICKNESS = new BlockState<>("thickness", DripstoneThickness.TIP, DripstoneThickness::values);
    public static final BlockState<Dusted> DUSTED = new BlockState<>("dusted", Dusted._0, Dusted::values);
    public static final BlockState<Bool> EAST = new BlockState<>("east", Bool.FALSE, Bool::values);
    public static final BlockState<Eggs> EGGS = new BlockState<>("eggs", Eggs._1, Eggs::values);
    public static final BlockState<Bool> ENABLED = new BlockState<>("enabled", Bool.TRUE, Bool::values);
    public static final BlockState<Bool> EXTENDED = new BlockState<>("extended", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> EYE = new BlockState<>("eye", Bool.FALSE, Bool::values);
    public static final BlockState<FacingDirection> FACING_ALL = new BlockState<>("facing", FacingDirection.NORTH, FacingDirection::values);
    public static final BlockState<FacingDirectionHorizontal> FACING_HORIZONTAL = new BlockState<>("facing", FacingDirectionHorizontal.NORTH, FacingDirectionHorizontal::values);
    public static final BlockState<FacingDirectionHorizontalDown> FACING_HORIZONTAL_DOWN = new BlockState<>("facing", FacingDirectionHorizontalDown.DOWN, FacingDirectionHorizontalDown::values);
    public static final BlockState<Flowers> FLOWER_AMOUNT = new BlockState<>("flower_amount", Flowers._1, Flowers::values);
    public static final BlockState<Bool> FLOWING = new BlockState<>("flowing", Bool.FALSE, Bool::values);
    public static final BlockState<GrindstoneAttachmentType> GRINDSTONE_ATTACHMENT_TYPE = new BlockState<>("grindstone_face", GrindstoneAttachmentType.FLOOR, GrindstoneAttachmentType::values);
    public static final BlockState<Half> HALF = new BlockState<>("half", Half.BOTTOM, Half::values);
    public static final BlockState<Bool> HANGING = new BlockState<>("hanging", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> HAS_BOOK = new BlockState<>("has_book", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> HAS_BOTTLE_0 = new BlockState<>("has_bottle_0", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> HAS_BOTTLE_1 = new BlockState<>("has_bottle_1", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> HAS_BOTTLE_2 = new BlockState<>("has_bottle_2", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> HAS_RECORD = new BlockState<>("has_record", Bool.FALSE, Bool::values);
    public static final BlockState<Hatch> HATCH = new BlockState<>("hatch", Hatch._0, Hatch::values);
    public static final BlockState<HoneyLevel> HONEY_LEVEL = new BlockState<>("honey_level", HoneyLevel._0, HoneyLevel::values);
    public static final BlockState<Bool> INFINIBURN = new BlockState<>("infiniburn", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> INVERTED = new BlockState<>("inverted", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> IN_WALL = new BlockState<>("in_wall", Bool.FALSE, Bool::values);
    public static final BlockState<Layers> LAYERS = new BlockState<>("layers", Layers._1, Layers::values);
    public static final BlockState<LightLevel> LIGHT_LEVEL = new BlockState<>("level", LightLevel._0, LightLevel::values);
    public static final BlockState<LiquidLevel> LIQUID_LEVEL = new BlockState<>("level", LiquidLevel._0, LiquidLevel::values);
    public static final BlockState<Bool> LIT = new BlockState<>("lit", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> LOCKED = new BlockState<>("locked", Bool.FALSE, Bool::values);
    public static final BlockState<ComparatorMode> MODE_COMPARATOR = new BlockState<>("mode", ComparatorMode.COMPARE, ComparatorMode::values);
    public static final BlockState<Moisture> MOISTURE = new BlockState<>("moisture", Moisture._0, Moisture::values);
    public static final BlockState<Bool> NATURAL = new BlockState<>("natural", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> NORTH = new BlockState<>("north", Bool.FALSE, Bool::values);
    public static final BlockState<Note> NOTE = new BlockState<>("note", Note._0, Note::values);
    public static final BlockState<NoteBlockInstrument> NOTE_BLOCK_INSTRUMENT = new BlockState<>("instrument", NoteBlockInstrument.HARP, NoteBlockInstrument::values);
    public static final BlockState<Bool> NO_DROP = new BlockState<>("drop_bit", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> OCCUPIED = new BlockState<>("occupied", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> OMINOUS = new BlockState<>("ominous", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> OPEN = new BlockState<>("open", Bool.FALSE, Bool::values);
    public static final BlockState<Orientation> ORIENTATION = new BlockState<>("orientation", Orientation.NORTH_UP, Orientation::values);
    public static final BlockState<Bool> PERSISTENT = new BlockState<>("persistent", Bool.FALSE, Bool::values);
    public static final BlockState<Pickles> PICKLES = new BlockState<>("pickles", Pickles._1, Pickles::values);
    public static final BlockState<PistonType> PISTON_TYPE = new BlockState<>("type", PistonType.NORMAL, PistonType::values);
    public static final BlockState<Power> POWER = new BlockState<>("power", Power._0, Power::values);
    public static final BlockState<Bool> POWERED = new BlockState<>("powered", Bool.FALSE, Bool::values);
    public static final BlockState<RailShape> RAIL_SHAPE = new BlockState<>("shape", RailShape.NORTH_SOUTH, RailShape::values);
    public static final BlockState<RailShapeStraight> RAIL_SHAPE_STRAIGHT = new BlockState<>("shape", RailShapeStraight.NORTH_SOUTH, RailShapeStraight::values);
    public static final BlockState<RedstoneConnection> REDSTONE_EAST = new BlockState<>("east", RedstoneConnection.NONE, RedstoneConnection::values);
    public static final BlockState<RedstoneConnection> REDSTONE_NORTH = new BlockState<>("north", RedstoneConnection.NONE, RedstoneConnection::values);
    public static final BlockState<RedstoneConnection> REDSTONE_SOUTH = new BlockState<>("south", RedstoneConnection.NONE, RedstoneConnection::values);
    public static final BlockState<RedstoneConnection> REDSTONE_WEST = new BlockState<>("west", RedstoneConnection.NONE, RedstoneConnection::values);
    public static final BlockState<RehydrationLevel> REHYDRATION_LEVEL = new BlockState<>("rehydration_level", RehydrationLevel._0, RehydrationLevel::values);
    public static final BlockState<RespawnAnchorCharges> RESPAWN_ANCHOR_CHARGES = new BlockState<>("charges", RespawnAnchorCharges._0, RespawnAnchorCharges::values);
    public static final BlockState<Rotation> ROTATION = new BlockState<>("rotation", Rotation._0, Rotation::values);
    public static final BlockState<SculkSensorPhase> SCULK_SENSOR_PHASE = new BlockState<>("sculk_sensor_phase", SculkSensorPhase.INACTIVE, SculkSensorPhase::values);
    public static final BlockState<Segments> SEGMENT_AMOUNT = new BlockState<>("segment_amount", Segments._1, Segments::values);
    public static final BlockState<Bool> SHORT = new BlockState<>("short", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> SHRIEKING = new BlockState<>("shrieking", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> SIGNAL_FIRE = new BlockState<>("signal_fire", Bool.FALSE, Bool::values);
    public static final BlockState<SlabType> SLAB_TYPE = new BlockState<>("type", SlabType.BOTTOM, SlabType::values);
    public static final BlockState<Bool> SNOWY = new BlockState<>("snowy", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> SOUTH = new BlockState<>("south", Bool.FALSE, Bool::values);
    public static final BlockState<StabilityDistance> STABILITY_DISTANCE = new BlockState<>("distance", StabilityDistance._0, StabilityDistance::values);
    public static final BlockState<Stage> STAGE = new BlockState<>("stage", Stage._0, Stage::values);
    public static final BlockState<StairShape> STAIR_SHAPE = new BlockState<>("shape", StairShape.STRAIGHT, StairShape::values);
    public static final BlockState<StructureBlockMode> STRUCTURE_BLOCK_MODE = new BlockState<>("mode", StructureBlockMode.LOAD, StructureBlockMode::values);
    public static final BlockState<StructureVoidType> STRUCTURE_VOID_TYPE = new BlockState<>("structure_void_type", StructureVoidType.VOID, StructureVoidType::values);
    public static final BlockState<Bool> SUSPENDED = new BlockState<>("suspended", Bool.FALSE, Bool::values);
    public static final BlockState<TestBlockMode> TEST_BLOCK_MODE = new BlockState<>("mode", TestBlockMode.START, TestBlockMode::values);
    public static final BlockState<Tilt> TILT = new BlockState<>("tilt", Tilt.NONE, Tilt::values);
    public static final BlockState<Bool> TIP = new BlockState<>("tip", Bool.TRUE, Bool::values);
    public static final BlockState<TrialSpawnerState> TRIAL_SPAWNER_STATE = new BlockState<>("trial_spawner_state", TrialSpawnerState.INACTIVE, TrialSpawnerState::values);
    public static final BlockState<Bool> TRIGGERED = new BlockState<>("triggered", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> UNDERWATER = new BlockState<>("underwater", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> UNSTABLE = new BlockState<>("unstable", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> UP = new BlockState<>("up", Bool.FALSE, Bool::values);
    public static final BlockState<Bool> UPDATE = new BlockState<>("update", Bool.FALSE, Bool::values);
    public static final BlockState<VaultState> VAULT_STATE = new BlockState<>("vault_state", VaultState.INACTIVE, VaultState::values);
    public static final BlockState<VerticalDirection> VERTICAL_DIRECTION = new BlockState<>("vertical_direction", VerticalDirection.UP, VerticalDirection::values);
    public static final BlockState<WallHeight> WALL_EAST = new BlockState<>("east", WallHeight.NONE, WallHeight::values);
    public static final BlockState<WallHeight> WALL_NORTH = new BlockState<>("north", WallHeight.NONE, WallHeight::values);
    public static final BlockState<WallHeight> WALL_SOUTH = new BlockState<>("south", WallHeight.NONE, WallHeight::values);
    public static final BlockState<WallHeight> WALL_WEST = new BlockState<>("west", WallHeight.NONE, WallHeight::values);
    public static final BlockState<Bool> WEST = new BlockState<>("west", Bool.FALSE, Bool::values);

    // Initialize default block states
    static {
        DEFAULT_BLOCK_STATES = Set.of(VanillaBlockStates.WATERLOGGED);
    }
}
