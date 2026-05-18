/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common;

public final class BuildInfo {
    public static final String NAME = "OpenFriend Plus";
    public static final String MOD_ID = "openfriendplus";
    public static final String VERSION = resolveVersion();

    private static volatile String runtimeLoader = "unknown";
    private static volatile String runtimeMinecraftVersion = "unknown";

    private BuildInfo() {}

    public static void setRuntimeInfo(String loader, String minecraftVersion) {
        if (loader != null && !loader.trim().isEmpty()) runtimeLoader = loader.trim();
        if (minecraftVersion != null && !minecraftVersion.trim().isEmpty()) {
            runtimeMinecraftVersion = minecraftVersion.trim();
        }
    }

    public static String runtimeLoader() {
        return runtimeLoader;
    }

    public static String runtimeMinecraftVersion() {
        return runtimeMinecraftVersion;
    }

    private static String resolveVersion() {
        String property = System.getProperty("openfriendplus.version");
        if (property != null && !property.trim().isEmpty()) return property.trim();

        Package pkg = BuildInfo.class.getPackage();
        String implementation = pkg == null ? null : pkg.getImplementationVersion();
        if (implementation != null && !implementation.trim().isEmpty()) return implementation.trim();

        return "1.0.0";
    }
}
