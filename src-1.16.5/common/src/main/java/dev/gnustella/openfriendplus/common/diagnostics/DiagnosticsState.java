/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.diagnostics;

import dev.gnustella.openfriendplus.common.BuildInfo;
import dev.gnustella.openfriendplus.common.config.OpenFriendPlusConfigStore;
import dev.gnustella.openfriendplus.common.ipc.IpcClient;
import dev.gnustella.openfriendplus.common.privacy.PrivacyFormatter;
import dev.gnustella.openfriendplus.common.state.FriendsState;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Supplier;

public final class DiagnosticsState {
    private final IpcClient ipc;
    private final FriendsState state;
    private final OpenFriendPlusConfigStore configStore;
    private final Path dataDir;
    private final Path coreBinaryPath;
    private final Supplier<String> localJoinAddress;

    public DiagnosticsState(IpcClient ipc, FriendsState state, OpenFriendPlusConfigStore configStore, Path dataDir) {
        this(ipc, state, configStore, dataDir, null, null);
    }

    public DiagnosticsState(IpcClient ipc, FriendsState state, OpenFriendPlusConfigStore configStore, Path dataDir,
                            Path coreBinaryPath, Supplier<String> localJoinAddress) {
        this.ipc = ipc;
        this.state = state;
        this.configStore = configStore;
        this.dataDir = dataDir;
        this.coreBinaryPath = coreBinaryPath;
        this.localJoinAddress = localJoinAddress;
    }

    public String[] lines() {
        PrivacyFormatter privacy = new PrivacyFormatter(configStore == null ? null : configStore.config());
        String joinAddress = localJoinAddress == null ? defaultJoinAddress() : localJoinAddress.get();
        return new String[] {
                "Mod: " + BuildInfo.NAME + " " + BuildInfo.VERSION,
                "Mod id: " + BuildInfo.MOD_ID,
                "Minecraft: " + BuildInfo.runtimeMinecraftVersion(),
                "Loader: " + BuildInfo.runtimeLoader(),
                "Java: " + System.getProperty("java.version", "unknown"),
                "OS/Arch: " + System.getProperty("os.name", "unknown") + " / " + System.getProperty("os.arch", "unknown"),
                "Core running: " + yesNo(ipc != null && ipc.isRunning()),
                "Core binary: " + value(coreBinaryPath),
                "Core started: " + time(ipc == null ? 0L : ipc.getStartedAt()),
                "IPC pending: " + (ipc == null ? 0 : ipc.getPendingCount()),
                "IPC last error: " + safe(value(ipc == null ? null : ipc.getLastError())),
                "Last Core log: " + safe(value(ipc == null ? null : ipc.getLastStderrLine())),
                "Data directory: " + value(dataDir),
                "Config file: " + value(configStore == null ? null : configStore.path()),
                "Auth: " + (state != null && state.auth().authenticated ? "signed in" : "unknown / needs sign in"),
                "Friends: " + (state == null ? 0 : state.friends().size()),
                "Incoming requests: " + (state == null ? 0 : state.incoming().size()),
                "Outgoing requests: " + (state == null ? 0 : state.outgoing().size()),
                "Blocked users: " + (state == null ? 0 : state.blocks().size()),
                "Presence watch: " + (ipc != null && ipc.isRunning() ? "enabled" : "disabled"),
                "Last refresh: " + time(state == null ? 0L : state.lastRefreshAt()),
                "Local join: " + privacy.maskAddress(joinAddress)
        };
    }

    public String copyText() {
        return String.join(System.lineSeparator(), lines());
    }

    private static String yesNo(boolean value) { return value ? "yes" : "no"; }
    private static String value(Object value) { return value == null ? "none" : String.valueOf(value); }

    private String defaultJoinAddress() {
        return configStore == null ? "127.0.0.1:25577" : configStore.config().resolveJoinListenAddress();
    }

    private static String time(long epochMs) {
        if (epochMs <= 0L) return "never";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(epochMs));
    }

    private static String safe(String value) {
        if (value == null) return "none";
        String lower = value.toLowerCase(Locale.ROOT);
        if (lower.contains("access_token") || lower.contains("refresh_token") || lower.contains("accesstoken")
                || lower.contains("refreshtoken")) {
            return "[redacted token-related message]";
        }
        return value;
    }
}
