package com.hivemc.chunker.cli;

import picocli.CommandLine;

/**
 * Version provider which uses the MANIFEST for the version.
 */
public class VersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
        Package pkg = VersionProvider.class.getPackage();
        String version = pkg.getImplementationVersion();
        return new String[]{version};
    }
}
