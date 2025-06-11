package com.hivemc.chunker.conversion.encoding.bedrock;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * An encodable Bedrock format version.
 */
public class BedrockDataVersion implements Comparable<BedrockDataVersion> {
    // Lookup used for turning any data version into the closest named version
    private static final TreeMap<Integer, BedrockDataVersion> PROTOCOL_VERSION_LOOKUP = new TreeMap<>();
    private static final TreeMap<Version, BedrockDataVersion> VERSION_LOOKUP = new TreeMap<>();

    // 1.12
    public static final BedrockDataVersion V1_12_0 = register(361, 8, new Version(1, 12, 0));

    // 1.13
    public static final BedrockDataVersion V1_13_0 = register(388, 8, new Version(1, 13, 0));

    // 1.14
    public static final BedrockDataVersion V1_14_0 = register(389, 8, new Version(1, 14, 0));
    public static final BedrockDataVersion V1_14_20 = register(389, 8, new Version(1, 14, 20));
    public static final BedrockDataVersion V1_14_30 = register(389, 8, new Version(1, 14, 30));
    public static final BedrockDataVersion V1_14_60 = register(390, 8, new Version(1, 14, 60));

    // 1.16
    public static final BedrockDataVersion V1_16_0 = register(407, 8, new Version(1, 16, 0));
    public static final BedrockDataVersion V1_16_20 = register(408, 8, new Version(1, 16, 20));
    public static final BedrockDataVersion V1_16_40 = register(408, 8, new Version(1, 16, 40));
    public static final BedrockDataVersion V1_16_100 = register(419, 8, new Version(1, 16, 100));
    public static final BedrockDataVersion V1_16_200 = register(422, 8, new Version(1, 16, 200));
    public static final BedrockDataVersion V1_16_210 = register(428, 8, new Version(1, 16, 210));
    public static final BedrockDataVersion V1_16_220 = register(431, 8, new Version(1, 16, 220));

    // 1.17
    public static final BedrockDataVersion V1_17_0 = register(440, 8, new Version(1, 17, 0));
    public static final BedrockDataVersion V1_17_10 = register(448, 8, new Version(1, 17, 10));
    public static final BedrockDataVersion V1_17_30 = register(465, 8, new Version(1, 17, 30));
    public static final BedrockDataVersion V1_17_40 = register(471, 8, new Version(1, 17, 40));

    // 1.18
    public static final BedrockDataVersion V1_18_0 = register(475, 8, new Version(1, 18, 0));
    public static final BedrockDataVersion V1_18_10 = register(486, 8, new Version(1, 18, 10));
    public static final BedrockDataVersion V1_18_30 = register(503, 9, new Version(1, 18, 30));

    // 1.19
    public static final BedrockDataVersion V1_19_0 = register(526, 9, new Version(1, 19, 0));
    public static final BedrockDataVersion V1_19_10 = register(534, 9, new Version(1, 19, 10));
    public static final BedrockDataVersion V1_19_20 = register(544, 9, new Version(1, 19, 20));
    public static final BedrockDataVersion V1_19_30 = register(554, 9, new Version(1, 19, 30));
    public static final BedrockDataVersion V1_19_40 = register(557, 9, new Version(1, 19, 40));
    public static final BedrockDataVersion V1_19_50 = register(560, 9, new Version(1, 19, 50));
    public static final BedrockDataVersion V1_19_60 = register(568, 9, new Version(1, 19, 60));
    public static final BedrockDataVersion V1_19_70 = register(575, 9, new Version(1, 19, 70));
    public static final BedrockDataVersion V1_19_80 = register(582, 9, new Version(1, 19, 80));

    // 1.20 (1.20 itself needs an extra state version to avoid blocks being incorrectly upgraded)
    public static final BedrockDataVersion V1_20_0 = register(588, 9, 33, new Version(1, 20, 0));
    public static final BedrockDataVersion V1_20_10 = register(594, 9, new Version(1, 20, 10));
    public static final BedrockDataVersion V1_20_30 = register(618, 9, new Version(1, 20, 30));
    public static final BedrockDataVersion V1_20_40 = register(622, 9, new Version(1, 20, 40));
    public static final BedrockDataVersion V1_20_50 = register(630, 9, new Version(1, 20, 50));
    public static final BedrockDataVersion V1_20_60 = register(649, 9, new Version(1, 20, 60));
    public static final BedrockDataVersion V1_20_70 = register(662, 9, new Version(1, 20, 70));
    public static final BedrockDataVersion V1_20_80 = register(671, 9, new Version(1, 20, 80));

    // 1.21
    public static final BedrockDataVersion V1_21_0 = register(685, 9, new Version(1, 21, 0));
    public static final BedrockDataVersion V1_21_20 = register(712, 9, new Version(1, 21, 20));
    public static final BedrockDataVersion V1_21_30 = register(729, 9, new Version(1, 21, 30));
    public static final BedrockDataVersion V1_21_40 = register(748, 9, new Version(1, 21, 40));
    public static final BedrockDataVersion V1_21_50 = register(766, 9, new Version(1, 21, 50));
    public static final BedrockDataVersion V1_21_60 = register(776, 9, new Version(1, 21, 60));
    public static final BedrockDataVersion V1_21_70 = register(786, 9, new Version(1, 21, 70));
    public static final BedrockDataVersion V1_21_80 = register(800, 9, new Version(1, 21, 80));
    public static final BedrockDataVersion V1_21_90 = register(818, 9, new Version(1, 21, 90));

    private final int protocolVersion;
    private final int storageVersion;
    private final Version version;
    private final int stateVersion;

    /**
     * Create a new BedrockDataVersion.
     *
     * @param protocolVersion      the network protocol version.
     * @param storageVersion       the storage version.
     * @param statePatchSubVersion the extra value for state versioning, used for specific state control in the world.
     * @param version              the version.
     */
    public BedrockDataVersion(int protocolVersion, int storageVersion, int statePatchSubVersion, Version version) {
        this.protocolVersion = protocolVersion;
        this.storageVersion = storageVersion;
        this.version = version;
        stateVersion = (version.getMajor() << 24) | (version.getMinor() << 16) | (version.getPatch() << 8) | statePatchSubVersion;
    }

    /**
     * Create a new BedrockDataVersion.
     *
     * @param protocolVersion the network protocol version.
     * @param storageVersion  the storage version.
     * @param version         the version.
     */
    public BedrockDataVersion(int protocolVersion, int storageVersion, Version version) {
        this(protocolVersion, storageVersion, 0, version);
    }

    /**
     * Register a new supported version.
     *
     * @param protocolVersion      the network protocol version.
     * @param storageVersion       the storage version.
     * @param statePatchSubVersion the extra value for state versioning, used for specific state control in the world.
     * @param bedrockVersion       the version.
     * @return the newly created instance.
     */
    public static BedrockDataVersion register(int protocolVersion, int storageVersion, int statePatchSubVersion, Version bedrockVersion) {
        BedrockDataVersion bedrockDataVersion = new BedrockDataVersion(protocolVersion, storageVersion, statePatchSubVersion, bedrockVersion);
        PROTOCOL_VERSION_LOOKUP.put(protocolVersion, bedrockDataVersion);
        VERSION_LOOKUP.put(bedrockDataVersion.getVersion(), bedrockDataVersion);
        return bedrockDataVersion;
    }

    /**
     * Register a new supported version.
     *
     * @param protocolVersion the network protocol version.
     * @param storageVersion  the storage version.
     * @param bedrockVersion  the version.
     * @return the newly created instance.
     */
    public static BedrockDataVersion register(int protocolVersion, int storageVersion, Version bedrockVersion) {
        return register(protocolVersion, storageVersion, 0, bedrockVersion);
    }

    /**
     * Get the oldest version.
     *
     * @return the first supported version.
     */
    public static BedrockDataVersion oldest() {
        return VERSION_LOOKUP.firstEntry().getValue();
    }

    /**
     * Get the latest version.
     *
     * @return the last supported version.
     */
    public static BedrockDataVersion latest() {
        return VERSION_LOOKUP.lastEntry().getValue();
    }

    /**
     * Get the nearest data version from a protocol.
     *
     * @param protocolVersion the network protocol.
     * @return the nearest version, rounded down, if it can't find the version the oldest is used.
     */
    public static BedrockDataVersion getNearestProtocol(int protocolVersion) {
        Map.Entry<Integer, BedrockDataVersion> entry = PROTOCOL_VERSION_LOOKUP.floorEntry(protocolVersion);

        // If the entry wasn't found use the latest version
        return entry == null ? oldest() : entry.getValue();
    }

    /**
     * Get the nearest data version.
     *
     * @param version the version.
     * @return the nearest version, rounded down, if it can't find the version the oldest is used.
     */
    public static BedrockDataVersion getNearestVersion(Version version) {
        Map.Entry<Version, BedrockDataVersion> entry = VERSION_LOOKUP.floorEntry(version);

        // If the entry wasn't found use the latest version
        return entry == null ? oldest() : entry.getValue();
    }

    /**
     * Get a collection of all the available versions.
     *
     * @return an immutable collection of all valid versions.
     */
    public static Collection<BedrockDataVersion> getVersions() {
        return Collections.unmodifiableCollection(VERSION_LOOKUP.values());
    }

    /**
     * Detect the nearest data version that should be used when reading a world.
     *
     * @param directory the world directory.
     * @return the optional of the nearest (rounded down), or empty if there is no valid version.
     */
    public static Optional<BedrockDataVersion> detect(File directory) {
        try {
            CompoundTag level = Objects.requireNonNull(Tag.readBedrockNBT(new File(directory, "level.dat")));

            // Find DataVersion tag
            List<Integer> versionTag = level.getListValues("lastOpenedWithVersion", IntTag.class, null);
            if (versionTag == null) {
                versionTag = level.getListValues("MinimumCompatibleClientVersion", IntTag.class, null);
            }

            // If neither tag is found fallback to legacy support
            if (versionTag == null || versionTag.isEmpty()) {
                // Use oldest version
                return Optional.ofNullable(BedrockDataVersion.oldest());
            }

            // Construct a version
            Version version = new Version(versionTag.get(0), versionTag.get(1), versionTag.get(2));

            // Find the nearest version
            return Optional.ofNullable(BedrockDataVersion.getNearestVersion(version));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Get the network protocol version.
     *
     * @return the network protocol version used for this data version for multiplayer.
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Get the version for this data version.
     *
     * @return the version.
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Get the Bedrock storage version to be used.
     *
     * @return the integer storage version.
     */
    public int getStorageVersion() {
        return storageVersion;
    }

    /**
     * Get the state version to use for this version.
     *
     * @return the encoded state version, made by shifting the major minor and patch version.
     */
    public int getStateVersion() {
        return stateVersion;
    }

    @Override
    public String toString() {
        return "BedrockDataVersion{" +
                "protocolVersion=" + protocolVersion +
                ", version=" + version +
                '}';
    }

    @Override
    public int compareTo(@NotNull BedrockDataVersion o) {
        return version.compareTo(o.getVersion());
    }
}
