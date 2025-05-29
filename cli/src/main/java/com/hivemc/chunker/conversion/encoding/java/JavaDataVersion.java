package com.hivemc.chunker.conversion.encoding.java;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * An encodable Java format version.
 */
public class JavaDataVersion implements Comparable<JavaDataVersion> {
    // Lookup used for turning any data version into the closest named version
    private static final TreeMap<Integer, JavaDataVersion> DATA_VERSION_LOOKUP = new TreeMap<>();
    private static final TreeMap<Version, JavaDataVersion> VERSION_LOOKUP = new TreeMap<>();

    // 1.8 (no data versions)
    public static final JavaDataVersion V1_8_8 = register(0, new Version(1, 8, 8));

    // 1.9
    public static final JavaDataVersion V1_9 = register(169, new Version(1, 9, 0));
    public static final JavaDataVersion V1_9_1 = register(175, new Version(1, 9, 1));
    public static final JavaDataVersion V1_9_2 = register(176, new Version(1, 9, 2));
    public static final JavaDataVersion V1_9_4 = register(184, new Version(1, 9, 4));
    public static final JavaDataVersion V1_9_3 = register(183, new Version(1, 9, 3));

    // 1.10
    public static final JavaDataVersion V1_10 = register(510, new Version(1, 10, 0));
    public static final JavaDataVersion V1_10_1 = register(511, new Version(1, 10, 1));
    public static final JavaDataVersion V1_10_2 = register(512, new Version(1, 10, 2));

    // 1.11
    public static final JavaDataVersion V1_11 = register(819, new Version(1, 11, 0));
    public static final JavaDataVersion V1_11_1 = register(921, new Version(1, 11, 1));
    public static final JavaDataVersion V1_11_2 = register(922, new Version(1, 11, 2));

    // 1.12
    public static final JavaDataVersion V1_12 = register(1139, new Version(1, 12, 0));
    public static final JavaDataVersion V1_12_1 = register(1241, new Version(1, 12, 1));
    public static final JavaDataVersion V1_12_2 = register(1343, new Version(1, 12, 2));

    // 1.13
    public static final JavaDataVersion V1_13 = register(1519, new Version(1, 13, 0));
    public static final JavaDataVersion V1_13_1 = register(1628, new Version(1, 13, 1));
    public static final JavaDataVersion V1_13_2 = register(1631, new Version(1, 13, 2));

    // 1.14
    public static final JavaDataVersion V1_14 = register(1952, new Version(1, 14, 0));
    public static final JavaDataVersion V1_14_1 = register(1957, new Version(1, 14, 1));
    public static final JavaDataVersion V1_14_2 = register(1963, new Version(1, 14, 2));
    public static final JavaDataVersion V1_14_3 = register(1968, new Version(1, 14, 3));
    public static final JavaDataVersion V1_14_4 = register(1976, new Version(1, 14, 4));

    // 1.15
    public static final JavaDataVersion V1_15 = register(2225, new Version(1, 15, 0));
    public static final JavaDataVersion V1_15_1 = register(2227, new Version(1, 15, 1));
    public static final JavaDataVersion V1_15_2 = register(2230, new Version(1, 15, 2));

    // 1.16
    public static final JavaDataVersion V1_16 = register(2566, new Version(1, 16, 0));
    public static final JavaDataVersion V1_16_1 = register(2567, new Version(1, 16, 1));
    public static final JavaDataVersion V1_16_2 = register(2578, new Version(1, 16, 2));
    public static final JavaDataVersion V1_16_3 = register(2580, new Version(1, 16, 3));
    public static final JavaDataVersion V1_16_4 = register(2584, new Version(1, 16, 4));
    public static final JavaDataVersion V1_16_5 = register(2586, new Version(1, 16, 5));

    // 1.17
    public static final JavaDataVersion V1_17 = register(2724, new Version(1, 17, 0));
    public static final JavaDataVersion V1_17_1 = register(2730, new Version(1, 17, 1));

    // 1.18
    public static final JavaDataVersion V1_18 = register(2860, new Version(1, 18, 0));
    public static final JavaDataVersion V1_18_1 = register(2865, new Version(1, 18, 1));
    public static final JavaDataVersion V1_18_2 = register(2975, new Version(1, 18, 2));

    // 1.19
    public static final JavaDataVersion V1_19 = register(3105, new Version(1, 19, 0));
    public static final JavaDataVersion V1_19_1 = register(3117, new Version(1, 19, 1));
    public static final JavaDataVersion V1_19_2 = register(3120, new Version(1, 19, 2));
    public static final JavaDataVersion V1_19_3 = register(3218, new Version(1, 19, 3));
    public static final JavaDataVersion V1_19_4 = register(3337, new Version(1, 19, 4));

    // 1.20
    public static final JavaDataVersion V1_20 = register(3463, new Version(1, 20, 0));
    public static final JavaDataVersion V1_20_1 = register(3465, new Version(1, 20, 1));
    public static final JavaDataVersion V1_20_2 = register(3578, new Version(1, 20, 2));
    public static final JavaDataVersion V1_20_3 = register(3698, new Version(1, 20, 3));
    public static final JavaDataVersion V1_20_4 = register(3700, new Version(1, 20, 4));
    public static final JavaDataVersion V1_20_5 = register(3837, new Version(1, 20, 5));
    public static final JavaDataVersion V1_20_6 = register(3839, new Version(1, 20, 6));

    // 1.21
    public static final JavaDataVersion V1_21 = register(3953, new Version(1, 21, 0));
    public static final JavaDataVersion V1_21_1 = register(3955, new Version(1, 21, 1));
    public static final JavaDataVersion V1_21_2 = register(4080, new Version(1, 21, 2));
    public static final JavaDataVersion V1_21_3 = register(4082, new Version(1, 21, 3));
    public static final JavaDataVersion V1_21_4 = register(4189, new Version(1, 21, 4));
    public static final JavaDataVersion V1_21_5 = register(4325, new Version(1, 21, 5));
    public static final JavaDataVersion V1_21_6 = register(4432, new Version(1, 21, 6));

    // Last anvil file version
    public static final int LAST_ANVIL_FILE_VERSION = 19133;

    private final int dataVersion;
    private final Version version;

    /**
     * Create a new JavaDataVersion.
     *
     * @param dataVersion the data version used in save files.
     * @param version     the version.
     */
    public JavaDataVersion(int dataVersion, Version version) {
        this.dataVersion = dataVersion;
        this.version = version;
    }

    /**
     * Register a new supported version.
     *
     * @param dataVersion the data version integer.
     * @param javaVersion the version.
     * @return the newly created instance.
     */
    public static JavaDataVersion register(int dataVersion, Version javaVersion) {
        JavaDataVersion javaDataVersion = new JavaDataVersion(dataVersion, javaVersion);
        DATA_VERSION_LOOKUP.put(dataVersion, javaDataVersion);
        VERSION_LOOKUP.put(javaDataVersion.getVersion(), javaDataVersion);
        return javaDataVersion;
    }

    /**
     * Get the oldest version.
     *
     * @return the first supported version.
     */
    public static JavaDataVersion oldest() {
        return DATA_VERSION_LOOKUP.firstEntry().getValue();
    }

    /**
     * Get the latest version.
     *
     * @return the last supported version.
     */
    public static JavaDataVersion latest() {
        return DATA_VERSION_LOOKUP.lastEntry().getValue();
    }

    /**
     * Get the nearest data version from a dataVersion.
     *
     * @param dataVersion the data version integer.
     * @return the nearest version, rounded down, if it can't find the version the oldest is used.
     */
    public static JavaDataVersion getNearestVersion(int dataVersion) {
        Map.Entry<Integer, JavaDataVersion> entry = DATA_VERSION_LOOKUP.floorEntry(dataVersion);

        // If the entry wasn't found use the latest version
        return entry == null ? oldest() : entry.getValue();
    }

    /**
     * Get the nearest data version.
     *
     * @param version the version.
     * @return the nearest version, rounded down, if it can't find the version the oldest is used.
     */
    public static JavaDataVersion getNearestVersion(Version version) {
        Map.Entry<Version, JavaDataVersion> entry = VERSION_LOOKUP.floorEntry(version);

        // If the entry wasn't found use the latest version
        return entry == null ? oldest() : entry.getValue();
    }

    /**
     * Get a collection of all the available versions.
     *
     * @return an immutable collection of all valid versions.
     */
    public static Collection<JavaDataVersion> getVersions() {
        return Collections.unmodifiableCollection(DATA_VERSION_LOOKUP.values());
    }

    /**
     * Detect the nearest data version that should be used when reading a world.
     *
     * @param directory the world directory.
     * @return the optional of the nearest (rounded down), or empty if there is no valid version.
     */
    public static Optional<JavaDataVersion> detect(File directory) {
        try {
            CompoundTag level = Tag.readGZipJavaNBT(new File(directory, "level.dat"));

            // Find DataVersion tag
            CompoundTag versionTag = Objects.requireNonNull(level).getCompound("Version");
            if (versionTag == null) {
                // If this is null, it's likely an older version or not a valid NBT dat
                if (level.getInt("version", -1) != LAST_ANVIL_FILE_VERSION) {
                    // Currently only the last anvil file format is handled (as 1.8.8)
                    return Optional.empty();
                }

                // Use version 0
                return Optional.ofNullable(JavaDataVersion.getNearestVersion(0));
            }

            // use the ID tag if it's present to determine the version
            return versionTag.getOptionalValue("Id", Integer.class).map(JavaDataVersion::getNearestVersion);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Get the data version value.
     *
     * @return the integer data version.
     */
    public int getDataVersion() {
        return dataVersion;
    }

    /**
     * Get the version for this data version.
     *
     * @return the version.
     */
    public Version getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "JavaDataVersion{" +
                "dataVersion=" + dataVersion +
                ", version=" + version +
                '}';
    }

    @Override
    public int compareTo(@NotNull JavaDataVersion o) {
        return version.compareTo(o.getVersion());
    }
}
