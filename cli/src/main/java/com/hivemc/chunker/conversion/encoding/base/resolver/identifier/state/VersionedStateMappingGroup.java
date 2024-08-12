package com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state;

import com.google.common.base.Preconditions;
import com.hivemc.chunker.conversion.encoding.base.Version;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents a mapping group that changes depending on version. This is useful for when states change between versions.
 */
public interface VersionedStateMappingGroup {
    /**
     * Get the state mapping group that should be used for a version.
     *
     * @param version the version.
     * @return the state mapping group to use for the version.
     */
    StateMappingGroup getStateMappingGroup(Version version);

    /**
     * Implementation of the VersionedStateMappingGroup that uses a sorted map for lookup. It falls back to the default
     * provided, otherwise rounds down the version.
     */
    class Impl implements VersionedStateMappingGroup {
        private final StateMappingGroup defaultStateMappingGroup;
        private final SortedMap<Version, StateMappingGroup> versions;

        private Impl(StateMappingGroup defaultStateMappingGroup, SortedMap<Version, StateMappingGroup> versions) {
            this.defaultStateMappingGroup = defaultStateMappingGroup;
            this.versions = versions;
        }

        @Override
        public StateMappingGroup getStateMappingGroup(Version version) {
            for (Map.Entry<Version, StateMappingGroup> entry : versions.entrySet()) {
                if (entry.getKey().isLessThanOrEqual(version)) {
                    return entry.getValue();
                }
            }
            return defaultStateMappingGroup;
        }
    }

    /**
     * Builder for creating VersionedStateMappingGroups.
     */
    class Builder {
        private final SortedMap<Version, StateMappingGroup> versions = new TreeMap<>(Comparator.reverseOrder());
        private StateMappingGroup defaultStateMappingGroup;

        /**
         * Create a new builder.
         */
        public Builder() {
        }

        /**
         * Set the default mapping group to use when there isn't a version that is greater than or equal to.
         *
         * @param group the default group.
         * @return the current builder.
         */
        public VersionedStateMappingGroup.Builder defaults(StateMappingGroup group) {
            Preconditions.checkArgument(defaultStateMappingGroup == null, "Cannot set more than one default group!");
            defaultStateMappingGroup = group;
            return this;
        }

        /**
         * Set a group to use for all versions equal to and above.
         *
         * @param version the version, which is used where input >= version.
         * @param group   the group to use for the version.
         * @return the current builder.
         */
        public VersionedStateMappingGroup.Builder version(Version version, StateMappingGroup group) {
            Preconditions.checkArgument(versions.put(version, group) == null, "Duplicate group for version " + version);
            return this;
        }

        /**
         * Make a VersionedStateMappingGroup based on the settings.
         *
         * @return the newly created instance.
         */
        public VersionedStateMappingGroup build() {
            Preconditions.checkArgument(defaultStateMappingGroup != null || !versions.isEmpty(), "No state groups have been set!");
            return new Impl(defaultStateMappingGroup, versions);
        }
    }
}
