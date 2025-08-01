package com.hivemc.chunker.util;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockGroups;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStateGroups;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test will do basic checks to ensure ChunkerVanillaBlockGroups are updated.
 */
public class ChunkerVanillaBlockGroupsTests {
    public static void checkGroup(String groupName, Set<ChunkerBlockType> group, Predicate<ChunkerBlockType> groupPredicate) {
        // Check for blocks which may be missing
        for (ChunkerVanillaBlockType type : ChunkerVanillaBlockType.values()) {
            if (!groupPredicate.test(type)) continue;
            assertTrue(group.contains(type), () -> "Missing " + type + " in ChunkerVanillaBlockGroups." + groupName);
        }

        // Check for blocks which may be accidentally added
        for (ChunkerBlockType type : group) {
            assertTrue(groupPredicate.test(type), () -> "Invalid block " + type + " in ChunkerVanillaBlockGroups." + groupName);
        }
    }

    @Test
    public void testSnowyGrassBlocks() {
        checkGroup("SNOWY_GRASS_BLOCKS", ChunkerVanillaBlockGroups.SNOWY_GRASS_BLOCKS, (type) -> {
            return type.getStates().contains(VanillaBlockStates.SNOWY);
        });
    }

    @Test
    public void testSnowyBlocks() {
        for (ChunkerVanillaBlockType type : ChunkerVanillaBlockType.values()) {
            if (type == ChunkerVanillaBlockType.POWDER_SNOW_CAULDRON) continue; // Cauldron is not a snow block
            if (!type.name().contains("SNOW")) continue;
            assertTrue(ChunkerVanillaBlockGroups.SNOWY_BLOCKS.contains(type), () -> "Missing " + type + " in ChunkerVanillaBlockGroups.SNOWY_BLOCKS");
        }
    }

    @Test
    public void testWoodenFences() {
        checkGroup("WOODEN_FENCES", ChunkerVanillaBlockGroups.WOODEN_FENCES, (type) -> {
            // Nether brick fences are the only non-wood fences
            return type != ChunkerVanillaBlockType.NETHER_BRICK_FENCE
                    && type instanceof ChunkerVanillaBlockType vanillaBlockType && vanillaBlockType.name().endsWith("_FENCE");
        });
    }

    @Test
    public void testFenceGates() {
        checkGroup("FENCE_GATES", ChunkerVanillaBlockGroups.FENCE_GATES, (type) -> {
            return type instanceof ChunkerVanillaBlockType vanillaBlockType && vanillaBlockType.name().endsWith("_FENCE_GATE");
        });
    }

    @Test
    public void testWalls() {
        checkGroup("WALLS", ChunkerVanillaBlockGroups.WALLS, (type) -> {
            return type instanceof ChunkerVanillaBlockType vanillaBlockType && vanillaBlockType.name().endsWith("_WALL")
                    || type.getStates().containsAll(VanillaBlockStateGroups.WALL);
        });
    }

    @Test
    public void testStairs() {
        checkGroup("STAIRS", ChunkerVanillaBlockGroups.STAIRS, (type) -> {
            return type instanceof ChunkerVanillaBlockType vanillaBlockType && vanillaBlockType.name().endsWith("_STAIRS");
        });
    }

    @Test
    public void testDoors() {
        for (ChunkerVanillaBlockType type : ChunkerVanillaBlockType.values()) {
            if (!type.name().endsWith("_DOOR") && !type.getStates().containsAll(VanillaBlockStateGroups.DOOR)) {
                continue;
            }
            assertTrue(ChunkerVanillaBlockGroups.DOORS.contains(type), () -> "Missing " + type + " in ChunkerVanillaBlockGroups.DOORS");
        }
    }

    @Test
    public void testRedstoneConnectable() {
        checkGroup("REDSTONE_CONNECTABLE", ChunkerVanillaBlockGroups.REDSTONE_CONNECTABLE, (type) -> {
            if (type == ChunkerVanillaBlockType.TRAPPED_CHEST) return true; // Trapped chest connects
            if (type == ChunkerVanillaBlockType.JUKEBOX) return true; // Jukebox connects
            if (type == ChunkerVanillaBlockType.TRIPWIRE) return false; // Wire doesn't connect
            if (type == ChunkerVanillaBlockType.NOTE_BLOCK) return false; // Note block doesn't connect
            if (type == ChunkerVanillaBlockType.BELL) return false; // Bell doesn't connect
            if (type == ChunkerVanillaBlockType.REDSTONE_ORE || type == ChunkerVanillaBlockType.DEEPSLATE_REDSTONE_ORE)
                return false;
            if (type == ChunkerVanillaBlockType.REDSTONE_LAMP) return false;
            if (type == ChunkerVanillaBlockType.POWERED_RAIL || type == ChunkerVanillaBlockType.ACTIVATOR_RAIL)
                return false;
            if (!(type instanceof ChunkerVanillaBlockType vanillaBlockType))
                return false; // Only vanilla blocks

            if (vanillaBlockType.name().endsWith("DOOR") || vanillaBlockType.name().endsWith("GATE"))
                return false; // Trapdoors / Doors / Gates don't connect
            if (vanillaBlockType.name().endsWith("_SKULL") || vanillaBlockType.name().endsWith("_HEAD"))
                return false; // Skulls heads don't connect
            if (vanillaBlockType.name().endsWith("_BULB")) return false; // Bulbs don't connect

            // Connect with blocks with POWERED / POWER or REDSTONE in name
            return type.getStates().contains(VanillaBlockStates.POWERED)
                    || type.getStates().contains(VanillaBlockStates.POWER)
                    || vanillaBlockType.name().contains("REDSTONE");
        });
    }

    @Test
    public void testDoubleChests() {
        checkGroup("DOUBLE_CHESTS", ChunkerVanillaBlockGroups.DOUBLE_CHESTS, (type) -> {
            // Any chest which ends with _CHEST that isn't an ENDER_CHEST can be made double
            return type != ChunkerVanillaBlockType.ENDER_CHEST && (type == ChunkerVanillaBlockType.CHEST
                    || type instanceof ChunkerVanillaBlockType vanillaBlockType && vanillaBlockType.name().endsWith("_CHEST"));
        });
    }
}
