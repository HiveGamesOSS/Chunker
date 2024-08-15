package com.hivemc.chunker.conversion.encoding.base;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a version in the form major.minor.patch.
 */
public class Version implements Comparable<Version> {
    /**
     * Static version used to indicate the latest that is supported, used for tests.
     */
    public static final Version LATEST = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final int major;
    private final int minor;
    private final int patch;

    /**
     * Create a new version using the major.minor.patch.
     *
     * @param major the major version (usually the first).
     * @param minor the minor version (usually the second).
     * @param patch the patch version (usually the third, sometimes omitted)
     */
    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Create a version from a String in the form "major.minor.patch", if any of the components are missing it assumes 0.
     *
     * @param input the input string.
     * @return the parsed version.
     */
    public static Version fromString(String input) {
        String[] parts = input.split(Pattern.quote("."));
        int major = 0;
        int minor = 0;
        int patch = 0;
        if (parts.length > 0) {
            major = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                minor = Integer.parseInt(parts[1]);
            }
            if (parts.length > 2) {
                patch = Integer.parseInt(parts[2]);
            }
        }
        return new Version(major, minor, patch);
    }

    /**
     * Get the major version.
     *
     * @return integer representing the major version (non-negative).
     */
    public int getMajor() {
        return major;
    }

    /**
     * Get the minor version.
     *
     * @return integer representing the minor version (non-negative).
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Get the patch version.
     *
     * @return integer representing the patch version (non-negative).
     */
    public int getPatch() {
        return patch;
    }

    /**
     * Check if this is greater than or equal to the version in the parameters.
     *
     * @param version the version to check against.
     * @return true if this is greater than or equal to the provided.
     */
    public boolean isGreaterThanOrEqual(Version version) {
        return isGreaterThanOrEqual(version.getMajor(), version.getMinor(), version.getPatch());
    }

    /**
     * Check if this is greater than or equal to the version in the parameters.
     *
     * @param major the major version.
     * @param minor the minor version.
     * @param patch the patch version.
     * @return true if this is greater than or equal to the provided.
     */
    public boolean isGreaterThanOrEqual(int major, int minor, int patch) {
        if (this.major > major) return true;
        if (this.major < major) return false;
        if (this.minor > minor) return true;
        if (this.minor < minor) return false;
        if (this.patch > patch) return true;
        return this.patch >= patch;
    }

    /**
     * Check if this is greater than the version in the parameters.
     *
     * @param version the version to check against.
     * @return true if this is greater than the provided.
     */
    public boolean isGreaterThan(Version version) {
        return isGreaterThan(version.getMajor(), version.getMinor(), version.getPatch());
    }

    /**
     * Check if this is greater than the version in the parameters.
     *
     * @param major the major version.
     * @param minor the minor version.
     * @param patch the patch version.
     * @return true if this is greater than the provided.
     */
    public boolean isGreaterThan(int major, int minor, int patch) {
        if (this.major > major) return true;
        if (this.major < major) return false;
        if (this.minor > minor) return true;
        if (this.minor < minor) return false;
        return this.patch > patch;
    }

    /**
     * Check if this is less than or equal to the version in the parameters.
     *
     * @param version the version to check against.
     * @return true if this is less than or equal to the provided.
     */
    public boolean isLessThanOrEqual(Version version) {
        return isLessThanOrEqual(version.getMajor(), version.getMinor(), version.getPatch());
    }

    /**
     * Check if this is less than or equal to the version in the parameters.
     *
     * @param major the major version.
     * @param minor the minor version.
     * @param patch the patch version.
     * @return true if this is less than or equal to the provided.
     */
    public boolean isLessThanOrEqual(int major, int minor, int patch) {
        if (this.major < major) return true;
        if (this.major > major) return false;
        if (this.minor < minor) return true;
        if (this.minor > minor) return false;
        if (this.patch < patch) return true;
        return this.patch <= patch;
    }

    /**
     * Check if this is less than the version in the parameters.
     *
     * @param version the version to check against.
     * @return true if this is less than the provided.
     */
    public boolean isLessThan(Version version) {
        return isLessThan(version.getMajor(), version.getMinor(), version.getPatch());
    }

    /**
     * Check if this is less than the version in the parameters.
     *
     * @param major the major version.
     * @param minor the minor version.
     * @param patch the patch version.
     * @return true if this is less than the provided.
     */
    public boolean isLessThan(int major, int minor, int patch) {
        if (this.major < major) return true;
        if (this.major > major) return false;
        if (this.minor < minor) return true;
        if (this.minor > minor) return false;
        return this.patch < patch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version version)) return false;
        return getMajor() == version.getMajor() && getMinor() == version.getMinor() && getPatch() == version.getPatch();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMajor(), getMinor(), getPatch());
    }

    @Override
    public String toString() {
        return String.valueOf(major) + '.' + minor + '.' + patch;
    }

    @Override
    public int compareTo(@NotNull Version o) {
        if (equals(o)) return 0;
        if (isLessThan(o.getMajor(), o.getMinor(), o.getPatch())) return -1;
        return 1;
    }
}
