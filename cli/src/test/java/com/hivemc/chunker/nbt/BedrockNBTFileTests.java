package com.hivemc.chunker.nbt;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure Bedrock NBT with files works as expected.
 */
class BedrockNBTFileTests {
    public static File tempFile() throws IOException {
        File tempFile = File.createTempFile("resource", ".dat");
        tempFile.deleteOnExit();
        return tempFile;
    }

    public static File getTempFileForResource(String resource) throws IOException {
        File tempFile = tempFile();
        Resources.asByteSource(Resources.getResource(resource)).copyTo(Files.asByteSink(tempFile));
        return tempFile;
    }

    @Test
    void testBedrockLevelDat() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        assertNotNull(Tag.readBedrockNBT(input));
    }

    @Test
    void testBedrockLevelDatTypeByte() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        CompoundTag nbt = Tag.readBedrockNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals((byte) 0, nbt.getByte("bonusChestEnabled"));
        assertEquals((byte) 1, nbt.getByte("commandblockoutput"));
    }

    @Test
    void testBedrockLevelDatTypeFloat() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        CompoundTag nbt = Tag.readBedrockNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals(0F, nbt.getFloat("lightningLevel"));

        // Grab abilities
        CompoundTag abilities = nbt.getCompound("abilities");
        assertNotNull(abilities);

        assertEquals(0.05F, abilities.getFloat("flySpeed"));
        assertEquals(0.1F, abilities.getFloat("walkSpeed"));
    }

    @Test
    void testBedrockLevelDatTypeInt() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        CompoundTag nbt = Tag.readBedrockNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals(-178, nbt.getInt("LimitedWorldOriginZ"));
        assertEquals(10000, nbt.getInt("functioncommandlimit"));
        assertEquals(2, nbt.getInt("Difficulty"));
    }

    @Test
    void testBedrockLevelDatTypeLong() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        CompoundTag nbt = Tag.readBedrockNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals(-290482550374668472L, nbt.getLong("RandomSeed"));
        assertEquals(4294967294L, nbt.getLong("worldStartCount"));
        assertEquals(236L, nbt.getLong("currentTick"));
    }

    @Test
    void testBedrockLevelDatTypeString() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        CompoundTag nbt = Tag.readBedrockNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals("Example World", nbt.getString("LevelName"));
        assertEquals("*", nbt.getString("baseGameVersion"));
        assertEquals("1.20.30", nbt.getString("InventoryVersion"));
    }

    @Test
    void testBedrockLevelDatTypeCompound() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        CompoundTag nbt = Tag.readBedrockNBT(input);
        assertNotNull(nbt);

        CompoundTag experiments = nbt.getCompound("experiments");
        assertNotNull(experiments);

        // Check fields
        assertEquals(3, experiments.getValue().size());
        assertTrue(experiments.contains("experiments_ever_used"));
        assertTrue(experiments.contains("data_driven_vanilla_blocks_and_items"));
        assertTrue(experiments.contains("saved_with_toggled_experiments"));
    }

    @Test
    void testBedrockLevelDatTypeList() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        CompoundTag nbt = Tag.readBedrockNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals(5, nbt.getList("lastOpenedWithVersion", IntTag.class).size());
        assertTrue(nbt.getList("lastOpenedWithVersion", IntTag.class).contains(1));
        assertTrue(nbt.getList("MinimumCompatibleClientVersion", IntTag.class).contains(30));
        assertEquals(5, nbt.getList("MinimumCompatibleClientVersion", IntTag.class).size());
    }

    @Test
    void testBedrockDecodeEncode() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        CompoundTag nbt = Tag.readBedrockNBT(input);

        // Write to temp file
        File tempFile = tempFile();
        Tag.writeBedrockNBT(tempFile, 0, nbt);

        // Read it back
        CompoundTag readNBT = Tag.readBedrockNBT(tempFile);
        assertEquals(nbt, readNBT);
    }

    @Test
    void testBedrockDecodeEncodeBytes() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        CompoundTag nbt = Tag.readBedrockNBT(input);

        // Write to temp file
        byte[] bytes = Tag.writeBedrockNBT(nbt);

        // Read it back
        CompoundTag readNBT = Tag.readBedrockNBT(bytes);
        assertEquals(nbt, readNBT);
    }

    @Test
    void testBedrockDecodeEncodeByteBuffer() throws IOException {
        File input = getTempFileForResource("nbt/bedrock_level.dat");
        CompoundTag nbt = Tag.readBedrockNBT(input);

        // Write to temp file
        ByteBuffer bytes = ByteBuffer.wrap(Tag.writeBedrockNBT(nbt));

        // Read it back
        CompoundTag readNBT = Tag.readBedrockNBT(bytes);
        assertEquals(nbt, readNBT);
    }
}
