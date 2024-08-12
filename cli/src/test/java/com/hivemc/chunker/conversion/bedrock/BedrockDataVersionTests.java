package com.hivemc.chunker.conversion.bedrock;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.BedrockDataVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the functionality of BedrockDataVersion.
 */
public class BedrockDataVersionTests {
    public static Collection<BedrockDataVersion> getBedrockDataVersions() {
        return BedrockDataVersion.getVersions();
    }

    @ParameterizedTest
    @MethodSource("getBedrockDataVersions")
    public void testProtocol(BedrockDataVersion dataVersion) {
        // Check if the protocol matches (there are multiple versions with the same protocol in some cases)
        assertEquals(dataVersion.getProtocolVersion(), Objects.requireNonNull(BedrockDataVersion.getNearestProtocol(dataVersion.getProtocolVersion())).getProtocolVersion());
    }

    @ParameterizedTest
    @MethodSource("getBedrockDataVersions")
    public void testVersion(BedrockDataVersion dataVersion) {
        assertEquals(dataVersion, BedrockDataVersion.getNearestVersion(dataVersion.getVersion()));
    }

    @Test
    public void testOlderVersion() {
        BedrockDataVersion oldest = BedrockDataVersion.oldest();
        assertEquals(oldest, BedrockDataVersion.getNearestVersion(new Version(
                oldest.getVersion().getMajor(),
                oldest.getVersion().getMinor() - 1,
                oldest.getVersion().getPatch()
        )));
    }

    @Test
    public void testFutureVersion() {
        BedrockDataVersion latest = BedrockDataVersion.latest();
        assertEquals(latest, BedrockDataVersion.getNearestVersion(new Version(
                latest.getVersion().getMajor(),
                latest.getVersion().getMinor() + 1,
                latest.getVersion().getPatch()
        )));
    }

    @Test
    public void testProtocolVersionRange() {
        int lastProtocolVersion = 0;
        BedrockDataVersion last = BedrockDataVersion.oldest();
        for (BedrockDataVersion version : getBedrockDataVersions()) {
            for (int i = lastProtocolVersion; i < version.getProtocolVersion(); i++) {
                assertEquals(last.getProtocolVersion(), Objects.requireNonNull(BedrockDataVersion.getNearestProtocol(i)).getProtocolVersion());
            }

            // Set dataVersion to new one
            last = version;
            lastProtocolVersion = version.getProtocolVersion();
        }
    }
}
