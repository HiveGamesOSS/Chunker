package com.hivemc.chunker.conversion.encoding.java.util;

import org.jetbrains.annotations.Nullable;

/**
 * Util methods for writing various types of Java palettes.
 */
public class PaletteUtil {
    /**
     * Magic values from Java Edition for calculating palette indexes.
     */
    public static final int[] MAGIC = {
            -1, -1, 0, Integer.MIN_VALUE, 0, 0, 1431655765, 1431655765, 0, Integer.MIN_VALUE,
            0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756,
            0, Integer.MIN_VALUE, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0,
            390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378,
            306783378, 0, 286331153, 286331153, 0, Integer.MIN_VALUE, 0, 3, 252645135, 252645135,
            0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0,
            204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970,
            178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862,
            0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0,
            138547332, 138547332, 0, Integer.MIN_VALUE, 0, 4, 130150524, 130150524, 0, 126322567,
            126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197,
            0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0,
            104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893,
            97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282,
            0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0,
            84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431,
            79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303,
            0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0,
            70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, Integer.MIN_VALUE,
            0, 5
    };
    /**
     * The minimum amount of bits for a biome palette.
     */
    public static final int MINIMUM_BITS_PER_ENTRY_BIOMES = 1;
    /**
     * The minimum amount of bits for a block palette.
     */
    public static final int MINIMUM_BITS_PER_ENTRY_BLOCKS = 4;

    /**
     * Write values to a 1.13 formatted palette.
     *
     * @param minimumBitsPerEntry the minimum number of bits allowed per entry.
     * @param dimensionSize       the dimension size of the array, e.g. [16][16][16] for a normal chunk.
     * @param keyCount            the number of possible keys.
     * @param values              the lookup to encode, if null it uses empty values.
     * @return the encoded palette values.
     */
    public static long[] writePaletteValues1_13(int minimumBitsPerEntry, int dimensionSize, int keyCount, short @Nullable [][][] values) {
        // Calculate bitsPerEntry
        int bitsPerEntry = minimumBitsPerEntry;
        while (keyCount > 1 << bitsPerEntry) bitsPerEntry++;
        long maxEntryValue = (1L << bitsPerEntry) - 1;

        int valuesPerPalette = dimensionSize * dimensionSize * dimensionSize;
        int dimensionBitMask = dimensionSize - 1;
        int zDimensionBitShift = dimensionSize >> 2;
        int yDimensionBitShift = zDimensionBitShift + zDimensionBitShift;

        // Based on code from https://github.com/ViaVersion/ViaVersion/blob/9ea6c34543d6052f1e91b5035e87b756cb2358b8/common/src/main/java/us/myles/ViaVersion/util/CompactArrayUtil.java
        long[] encodedValues = new long[(int) Math.ceil(valuesPerPalette * bitsPerEntry / 64.0)];
        if (values != null) {
            for (int i = 0; i < valuesPerPalette; i++) {
                // Get entry
                int x = i & dimensionBitMask;
                int z = (i >> zDimensionBitShift) & dimensionBitMask;
                int y = (i >> yDimensionBitShift) & dimensionBitMask;

                short value = values[x][y][z];
                int bitIndex = i * bitsPerEntry;
                int startIndex = bitIndex / 64;
                int endIndex = ((i + 1) * bitsPerEntry - 1) / 64;
                int startBitSubIndex = bitIndex % 64;
                encodedValues[startIndex] = (encodedValues[startIndex] & ~(maxEntryValue << startBitSubIndex)) | ((value & maxEntryValue) << startBitSubIndex);
                if (startIndex != endIndex) {
                    int endBitSubIndex = 64 - startBitSubIndex;
                    encodedValues[endIndex] = ((encodedValues[endIndex] >> endBitSubIndex) << endBitSubIndex) | ((value & maxEntryValue) >> endBitSubIndex);
                }
            }
        }

        return encodedValues;
    }

    /**
     * Write values to a 1.16 formatted palette.
     *
     * @param minimumBitsPerEntry the minimum number of bits allowed per entry.
     * @param dimensionSize       the dimension size of the array, e.g. [16][16][16] for a normal chunk.
     * @param keyCount            the number of possible keys.
     * @param values              the lookup to encode, if null it uses empty values.
     * @return the encoded palette values.
     */
    public static long[] writePaletteValues1_16(int minimumBitsPerEntry, int dimensionSize, int keyCount, short @Nullable [][][] values) {
        // Calculate bitsPerEntry
        int bitsPerEntry = minimumBitsPerEntry;
        while (keyCount > 1 << bitsPerEntry) bitsPerEntry++;
        long maxEntryValue = (1L << bitsPerEntry) - 1;

        int valuesPerPalette = dimensionSize * dimensionSize * dimensionSize;
        int dimensionBitMask = dimensionSize - 1;
        int zDimensionBitShift = dimensionSize >> 2;
        int yDimensionBitShift = zDimensionBitShift + zDimensionBitShift;

        // Calculate magic value (based on Minecraft)
        char valuesPerLong = (char) (64 / bitsPerEntry);
        int magicIndex = 3 * (valuesPerLong - 1);
        long divideMul = Integer.toUnsignedLong(MAGIC[magicIndex]);
        long divideAdd = Integer.toUnsignedLong(MAGIC[magicIndex + 1]);
        int divideShift = MAGIC[magicIndex + 2];

        long[] encodedValues = new long[(valuesPerPalette + valuesPerLong - 1) / valuesPerLong];
        if (values != null) {
            for (int i = 0; i < valuesPerPalette; i++) {
                // Get entry
                int x = i & dimensionBitMask;
                int z = (i >> zDimensionBitShift) & dimensionBitMask;
                int y = (i >> yDimensionBitShift) & dimensionBitMask;

                // Grab value + indexes
                short value = values[x][y][z];
                int cellIndex = (int) ((i * divideMul + divideAdd >> 32) >> divideShift);
                int bitIndex = (i - cellIndex * valuesPerLong) * bitsPerEntry;
                encodedValues[cellIndex] = (encodedValues[cellIndex] & ~(maxEntryValue << bitIndex)) | ((value & maxEntryValue) << bitIndex);
            }
        }

        return encodedValues;
    }

    /**
     * Read values from a palette and use the length to detect the version.
     *
     * @param minimumBitsPerEntry the minimum number of bits per an entry.
     * @param dimensionSize       the dimension size of the array, e.g. [16][16][16] for a normal chunk.
     * @param encodedValues       the encoded values.
     * @return the decoded palette values.
     */
    public static short[][][] readPaletteValues(int minimumBitsPerEntry, int dimensionSize, int keyCount, long[] encodedValues) {
        // Calculate bitsPerEntry
        int bitsPerEntry = minimumBitsPerEntry;
        while (keyCount > 1 << bitsPerEntry) bitsPerEntry++;

        // Calculate the values per long
        char valuesPerLong = (char) (64 / bitsPerEntry);
        int valuesPerPalette = dimensionSize * dimensionSize * dimensionSize;

        // If the values line up then it is likely the 1.16 format and can be read using that algorithm, otherwise fallback
        double expectedLength = Math.ceil((double) valuesPerPalette / valuesPerLong);
        if (encodedValues.length != expectedLength) {
            bitsPerEntry = (int) Math.floor(encodedValues.length * 64 / (double) valuesPerPalette);
            return readPaletteValues1_13(bitsPerEntry, dimensionSize, encodedValues); // Fallback to 1.13
        } else {
            return readPaletteValues1_16(bitsPerEntry, dimensionSize, encodedValues);
        }
    }

    /**
     * Read values from a 1.13 formatted palette.
     *
     * @param bitsPerEntry  the number of bits used per palette key index.
     * @param dimensionSize the dimension size of the array, e.g. [16][16][16] for a normal chunk.
     * @param encodedValues the encoded values.
     * @return the decoded palette values.
     */
    public static short[][][] readPaletteValues1_13(int bitsPerEntry, int dimensionSize, long[] encodedValues) {
        short[][][] output = new short[dimensionSize][dimensionSize][dimensionSize];
        int valuesPerPalette = dimensionSize * dimensionSize * dimensionSize;
        int dimensionBitMask = dimensionSize - 1;
        int zDimensionBitShift = dimensionSize >> 2;
        int yDimensionBitShift = zDimensionBitShift + zDimensionBitShift;

        // Based on code from https://github.com/ViaVersion/ViaVersion/blob/9ea6c34543d6052f1e91b5035e87b756cb2358b8/common/src/main/java/us/myles/ViaVersion/util/CompactArrayUtil.java
        long maxEntryValue = (1L << bitsPerEntry) - 1;
        for (int i = 0; i < valuesPerPalette; i++) {
            int bitIndex = i * bitsPerEntry;
            int startIndex = bitIndex / 64;
            int endIndex = ((i + 1) * bitsPerEntry - 1) / 64;
            int startBitSubIndex = bitIndex % 64;
            short value;
            if (startIndex == endIndex) {
                value = (short) (((encodedValues[startIndex] >>> startBitSubIndex) & maxEntryValue) & 0xFFFF);
            } else {
                int endBitSubIndex = 64 - startBitSubIndex;
                value = (short) ((((encodedValues[startIndex] >>> startBitSubIndex) | (encodedValues[endIndex] << endBitSubIndex)) & maxEntryValue) & 0xFFFF);
            }

            // Set entry
            int x = i & dimensionBitMask;
            int z = (i >> zDimensionBitShift) & dimensionBitMask;
            int y = (i >> yDimensionBitShift) & dimensionBitMask;
            output[x][y][z] = value;
        }

        return output;
    }

    /**
     * Read values from a 1.16 formatted palette.
     *
     * @param bitsPerEntry  the number of bits used per palette key index.
     * @param dimensionSize the dimension size of the array, e.g. [16][16][16] for a normal chunk.
     * @param encodedValues the encoded values.
     * @return the decoded palette values.
     */
    public static short[][][] readPaletteValues1_16(int bitsPerEntry, int dimensionSize, long[] encodedValues) {
        short[][][] output = new short[dimensionSize][dimensionSize][dimensionSize];
        int valuesPerPalette = dimensionSize * dimensionSize * dimensionSize;
        int dimensionBitMask = dimensionSize - 1;
        int zDimensionBitShift = dimensionSize >> 2;
        int yDimensionBitShift = zDimensionBitShift + zDimensionBitShift;

        // Use maxEntryValue for bitmask
        long maxEntryValue = (1L << bitsPerEntry) - 1;
        char valuesPerLong = (char) (64 / bitsPerEntry);

        // Calculate magic value (based on Minecraft)
        int magicIndex = 3 * (valuesPerLong - 1);
        long divideMul = Integer.toUnsignedLong(MAGIC[magicIndex]);
        long divideAdd = Integer.toUnsignedLong(MAGIC[magicIndex + 1]);
        int divideShift = MAGIC[magicIndex + 2];

        for (int i = 0; i < valuesPerPalette; i++) {
            int cellIndex = (int) (i * divideMul + divideAdd >> 32 >> divideShift);
            int bitIndex = (i - cellIndex * valuesPerLong) * bitsPerEntry;
            short value = (short) ((encodedValues[cellIndex] >>> bitIndex) & maxEntryValue);

            // Set entry
            int x = i & dimensionBitMask;
            int z = (i >> zDimensionBitShift) & dimensionBitMask;
            int y = (i >> yDimensionBitShift) & dimensionBitMask;
            output[x][y][z] = value;
        }

        return output;
    }
}
