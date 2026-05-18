/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.diagnostics;

import dev.gnustella.openfriendplus.common.BuildInfo;
import dev.gnustella.openfriendplus.common.config.OpenFriendPlusConfigStore;
import dev.gnustella.openfriendplus.common.ipc.IpcClient;
import dev.gnustella.openfriendplus.common.state.FriendsState;

import java.nio.file.Path;

public final class DiagnosticsState {
    private final IpcClient ipc;
    private final FriendsState state;
    private final OpenFriendPlusConfigStore configStore;
    private final Path dataDir;

    public DiagnosticsState(IpcClient ipc, FriendsState state, OpenFriendPlusConfigStore configStore, Path dataDir) {
        this.ipc = ipc;
        this.state = state;
        this.configStore = configStore;
        this.dataDir = dataDir;
    }

    public String[] lines() {
        return new String[] {
                "Mod: " + BuildInfo.NAME + " " + BuildInfo.VERSION,
                "Mod id: " + BuildInfo.MOD_ID,
                "Java: " + System.getProperty("java.version", "unknown"),
                "OS/Arch: " + System.getProperty("os.name", "unknown") + " / " + System.getProperty("os.arch", "unknown"),
                "Core running: " + yesNo(ipc != null && ipc.isRunning()),
                "IPC pending: " + (ipc == null ? 0 : ipc.getPendingCount()),
                "IPC last error: " + value(ipc == null ? null : ipc.getLastError()),
                "Last Core log: " + value(ipc == null ? null : ipc.getLastStderrLine()),
                "Data directory: " + value(dataDir),
                "Config file: " + value(configStore == null ? null : configStore.path()),
                "Auth: " + (state != null && state.auth().authenticated ? "signed in" : "unknown / needs sign in"),
                "Friends: " + (state == null ? 0 : state.friends().size()),
                "Incoming requests: " + (state == null ? 0 : state.incoming().size()),
                "Outgoing requests: " + (state == null ? 0 : state.outgoing().size()),
                "Blocked users: " + (state == null ? 0 : state.blocks().size()),
                "Local join: " + (configStore == null ? "127.0.0.1:25577" : configStore.config().resolveJoinListenAddress())
        };
    }

    public String copyText() {
        return String.join(System.lineSeparator(), lines());
    }

    private static String yesNo(boolean value) { return value ? "yes" : "no"; }
    private static String value(Object value) { return value == null ? "none" : String.valueOf(value); }
}
