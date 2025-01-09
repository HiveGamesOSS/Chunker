package com.hivemc.chunker.nbt;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure Java NBT with files works as expected.
 */
class JavaNBTFileTests {
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
    void testJavaLevelDat() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        assertNotNull(Tag.readGZipJavaNBT(input));
    }

    @Test
    void testJavaLevelDatWithPossibly() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        assertNotNull(Tag.readPossibleGZipJavaNBT(input));
    }

    @Test
    void testJavaLevelDatTypeByte() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals((byte) 0, nbt.getByte("allowCommands"));
        assertEquals((byte) 1, nbt.getByte("initialized"));
        assertEquals((byte) 2, nbt.getByte("Difficulty"));
    }

    @Test
    void testJavaLevelDatTypeDouble() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals(59999968, nbt.getDouble("BorderSizeLerpTarget"));
        assertEquals(5, nbt.getDouble("BorderSafeZone"));
        assertEquals(0.2, nbt.getDouble("BorderDamagePerBlock"));
    }

    @Test
    void testJavaLevelDatTypeFloat() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals(0F, nbt.getFloat("SpawnAngle"));

        CompoundTag player = nbt.getCompound("Player");
        assertNotNull(player);
        assertEquals(5F, player.getFloat("foodSaturationLevel"));
        assertEquals(20F, player.getFloat("Health"));
    }

    @Test
    void testJavaLevelDatTypeInt() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals(173310, nbt.getInt("rainTime"));
        assertEquals(25, nbt.getInt("WanderingTraderSpawnChance"));
        assertEquals(0, nbt.getInt("SpawnX"));
    }

    @Test
    void testJavaLevelDatTypeLong() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals(1698052528810L, nbt.getLong("LastPlayed"));
        assertEquals(83L, nbt.getLong("DayTime"));
        assertEquals(0L, nbt.getLong("BorderSizeLerpTime"));
    }

    @Test
    void testJavaLevelDatTypeShort() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);
        assertNotNull(nbt);

        // Check fields
        CompoundTag player = nbt.getCompound("Player");
        assertNotNull(player);
        assertEquals((short) -20, player.getShort("Fire"));
        assertEquals((short) 300, player.getShort("Air"));
        assertEquals((short) 0, player.getShort("DeathTime"));
    }

    @Test
    void testJavaLevelDatTypeString() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals("Example World", nbt.getString("LevelName"));

        CompoundTag version = nbt.getCompound("Version");
        assertNotNull(version);
        assertEquals("main", version.getString("Series"));
        assertEquals("1.20.2", version.getString("Name"));
    }

    @Test
    void testJavaLevelDatTypeCompound() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);
        assertNotNull(nbt);

        // Check fields
        CompoundTag version = nbt.getCompound("Version");
        assertNotNull(version);

        assertEquals(4, version.getValue().size());
        assertTrue(version.contains("Id"));
        assertTrue(version.contains("Name"));
        assertTrue(version.contains("Series"));
        assertTrue(version.contains("Snapshot"));
    }

    @Test
    void testJavaLevelDatTypeList() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);
        assertNotNull(nbt);

        // Check fields
        assertEquals(1, nbt.getList("ServerBrands", StringTag.class).size());
        assertTrue(nbt.getList("ServerBrands", StringTag.class).contains("vanilla"));
        assertEquals(0, nbt.getList("ScheduledEvents", StringTag.class).size());
    }

    @Test
    void testJavaLevelDatTypeIntArray() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);
        assertNotNull(nbt);

        // Check fields
        CompoundTag player = nbt.getCompound("Player");
        assertNotNull(player);
        assertArrayEquals(new int[]{
                -1749214166,
                1634291215,
                -2067653831,
                236600778
        }, player.getIntArray("UUID"));
    }

    @Test
    void testJavaDecodeEncodeGZIP() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);

        // Write to temp file
        File tempFile = tempFile();
        Tag.writeGZipJavaNBT(tempFile, nbt);

        // Read it back
        CompoundTag readNBT = Tag.readGZipJavaNBT(tempFile);
        assertEquals(nbt, readNBT);
    }

    @Test
    void testJavaDecodePossiblyEncodeGZIP() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readPossibleGZipJavaNBT(input);

        // Write to temp file
        File tempFile = tempFile();
        Tag.writeGZipJavaNBT(tempFile, nbt);

        // Read it back
        CompoundTag readNBT = Tag.readGZipJavaNBT(tempFile);
        assertEquals(nbt, readNBT);
    }

    @Test
    void testJavaDecodeEncodeBytesGZIP() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);

        // Write to temp file
        byte[] bytes = Tag.writeGZipJavaNBT(nbt);

        // Read it back
        CompoundTag readNBT = Tag.readGZipJavaNBT(bytes);
        assertEquals(nbt, readNBT);
    }

    @Test
    void testJavaDecodeEncodeZLIB() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);

        // Write to temp file
        File tempFile = tempFile();
        Tag.writeZLibJavaNBT(tempFile, nbt);

        // Read it back
        CompoundTag readNBT = Tag.readZLibJavaNBT(tempFile);
        assertEquals(nbt, readNBT);
    }

    @Test
    void testJavaDecodeEncodeBytesZLIB() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);

        // Write to temp file
        byte[] bytes = Tag.writeZLibJavaNBT(nbt);

        // Read it back
        CompoundTag readNBT = Tag.readZLibJavaNBT(bytes);
        assertEquals(nbt, readNBT);
    }

    @Test
    void testJavaDecodeEncodeBytesLZ4() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);

        // Write to temp file
        byte[] bytes = Tag.writeLZ4JavaNBT(nbt);

        // Read it back
        CompoundTag readNBT = Tag.readLZ4JavaNBT(bytes);
        assertEquals(nbt, readNBT);
    }

    @Test
    void testJavaDecodeEncodeUncompressed() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);

        // Write to temp file
        File tempFile = tempFile();
        Tag.writeUncompressedJavaNBT(tempFile, nbt);

        // Read it back
        CompoundTag readNBT = Tag.readUncompressedJavaNBT(tempFile);
        assertEquals(nbt, readNBT);
    }

    @Test
    void testJavaDecodePossiblyEncodeUncompressed() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);

        // Write to temp file
        File tempFile = tempFile();
        Tag.writeUncompressedJavaNBT(tempFile, nbt);

        // Read it back
        CompoundTag readNBT = Tag.readPossibleGZipJavaNBT(tempFile);
        assertEquals(nbt, readNBT);
    }

    @Test
    void testJavaDecodeEncodeBytesUncompressed() throws IOException {
        File input = getTempFileForResource("nbt/java_level.dat");
        CompoundTag nbt = Tag.readGZipJavaNBT(input);

        // Write to temp file
        byte[] bytes = Tag.writeUncompressedJavaNBT(nbt);

        // Read it back
        CompoundTag readNBT = Tag.readUncompressedJavaNBT(bytes);
        assertEquals(nbt, readNBT);
    }
}
