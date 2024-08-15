package com.hivemc.chunker.conversion.java;

import com.hivemc.chunker.conversion.encoding.java.JavaDataVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the functionality of JavaDataVersion.
 */
public class JavaDataVersionTests {
    public static Collection<JavaDataVersion> getJavaDataVersions() {
        return JavaDataVersion.getVersions();
    }

    @ParameterizedTest
    @MethodSource("getJavaDataVersions")
    public void testVersion(JavaDataVersion dataVersion) {
        assertEquals(dataVersion, JavaDataVersion.getNearestVersion(dataVersion.getDataVersion()));
    }

    @Test
    public void testOlderVersion() {
        JavaDataVersion oldest = JavaDataVersion.oldest();
        assertEquals(oldest, JavaDataVersion.getNearestVersion(oldest.getDataVersion() - 1));
    }

    @Test
    public void testFutureVersion() {
        JavaDataVersion latest = JavaDataVersion.latest();
        assertEquals(latest, JavaDataVersion.getNearestVersion(latest.getDataVersion() + 1));
    }

    @Test
    public void testVersionRange() {
        int lastDataVersion = 0;
        JavaDataVersion last = JavaDataVersion.oldest();
        for (JavaDataVersion version : getJavaDataVersions()) {
            for (int i = lastDataVersion; i < version.getDataVersion(); i++) {
                assertEquals(last.getDataVersion(), Objects.requireNonNull(JavaDataVersion.getNearestVersion(i)).getDataVersion());
            }

            // Set dataVersion to new one
            last = version;
            lastDataVersion = version.getDataVersion();
        }
    }
}
