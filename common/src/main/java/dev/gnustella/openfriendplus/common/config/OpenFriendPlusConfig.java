/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.config;

public final class OpenFriendPlusConfig {
    public int schemaVersion = 1;
    public boolean streamerMode = false;
    public boolean hideGamertags = false;
    public boolean hideProfileIdsInLogs = true;
    public boolean showJoinToasts = true;
    public boolean showPresenceToasts = true;
    public boolean showRequestToasts = true;
    public boolean autoRefreshFriends = true;
    public int refreshIntervalSeconds = 60;
    public int localJoinPort = 25577;
    public boolean randomizeJoinPort = false;
    public boolean diagnosticsOverlay = true;
    public String language = "auto";
    public boolean compactOverlay = false;
    public boolean useOriginalCoreBinaryName = true;
    public boolean clickOutsideToClose = true;

    public int effectiveRefreshIntervalSeconds() {
        return clamp(refreshIntervalSeconds, 15, 300);
    }

    public int effectiveLocalJoinPort() {
        return clamp(localJoinPort, 1024, 65535);
    }

    public String resolveJoinListenAddress() {
        return "127.0.0.1:" + effectiveLocalJoinPort();
    }

    public void sanitize() {
        schemaVersion = 1;
        refreshIntervalSeconds = effectiveRefreshIntervalSeconds();
        localJoinPort = effectiveLocalJoinPort();
        if (language == null || language.trim().isEmpty()) language = "auto";
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
