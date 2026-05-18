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
    public boolean quietFirstBoot = false;
    public boolean lockLocalJoinPort = false;
    public boolean lockLanguage = false;

    private transient int resolvedRandomJoinPort = -1;

    public int effectiveRefreshIntervalSeconds() {
        return clamp(refreshIntervalSeconds, 15, 300);
    }

    public int effectiveLocalJoinPort() {
        if (randomizeJoinPort) {
            if (resolvedRandomJoinPort < 1024 || resolvedRandomJoinPort > 65535) {
                resolvedRandomJoinPort = LocalPortPicker.findFreePort();
            }
            return resolvedRandomJoinPort;
        }
        return clamp(localJoinPort, 1024, 65535);
    }

    public String resolveJoinListenAddress() {
        return "127.0.0.1:" + effectiveLocalJoinPort();
    }

    public void sanitize() {
        schemaVersion = 1;
        refreshIntervalSeconds = effectiveRefreshIntervalSeconds();
        if (!randomizeJoinPort) localJoinPort = effectiveLocalJoinPort();
        if (language == null || language.trim().isEmpty()) language = "auto";
    }

    public void copyFrom(OpenFriendPlusConfig other) {
        if (other == null) return;
        schemaVersion = other.schemaVersion;
        streamerMode = other.streamerMode;
        hideGamertags = other.hideGamertags;
        hideProfileIdsInLogs = other.hideProfileIdsInLogs;
        showJoinToasts = other.showJoinToasts;
        showPresenceToasts = other.showPresenceToasts;
        showRequestToasts = other.showRequestToasts;
        autoRefreshFriends = other.autoRefreshFriends;
        refreshIntervalSeconds = other.refreshIntervalSeconds;
        localJoinPort = other.localJoinPort;
        randomizeJoinPort = other.randomizeJoinPort;
        diagnosticsOverlay = other.diagnosticsOverlay;
        language = other.language;
        compactOverlay = other.compactOverlay;
        useOriginalCoreBinaryName = other.useOriginalCoreBinaryName;
        clickOutsideToClose = other.clickOutsideToClose;
        quietFirstBoot = other.quietFirstBoot;
        lockLocalJoinPort = other.lockLocalJoinPort;
        lockLanguage = other.lockLanguage;
        resolvedRandomJoinPort = -1;
        sanitize();
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
