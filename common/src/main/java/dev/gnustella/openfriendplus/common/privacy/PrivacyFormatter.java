/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.privacy;

import dev.gnustella.openfriendplus.common.config.OpenFriendPlusConfig;

import java.util.UUID;

public final class PrivacyFormatter {
    private final OpenFriendPlusConfig config;

    public PrivacyFormatter(OpenFriendPlusConfig config) {
        this.config = config;
    }

    public String maskName(String name) {
        if (!isPrivacyEnabled() || name == null || name.length() <= 2) return name;
        StringBuilder masked = new StringBuilder();
        masked.append(name.charAt(0));
        for (int i = 1; i < name.length() - 1; i++) masked.append('*');
        masked.append(name.charAt(name.length() - 1));
        return masked.toString();
    }

    public String maskUuid(UUID uuid) {
        return uuid == null ? "null" : maskUuid(uuid.toString());
    }

    public String maskUuid(String uuid) {
        if (!isPrivacyEnabled() || uuid == null || uuid.length() <= 8) return uuid;
        return uuid.substring(0, 8) + "-****";
    }

    public String maskAddress(String hostPort) {
        if (!isPrivacyEnabled() || hostPort == null) return hostPort;
        int colon = hostPort.lastIndexOf(':');
        if (colon < 0) return "***";
        return hostPort.substring(0, colon + 1) + "*****";
    }

    public String maskDeviceCode(String code) {
        if (!isPrivacyEnabled() || code == null || code.length() < 4) return code;
        return code.substring(0, Math.min(2, code.length())) + "**-****";
    }

    private boolean isPrivacyEnabled() {
        return config != null && (config.streamerMode || config.hideGamertags || config.hideProfileIdsInLogs);
    }
}
