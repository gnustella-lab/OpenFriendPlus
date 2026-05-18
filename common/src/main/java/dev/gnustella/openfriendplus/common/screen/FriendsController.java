/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 */
package dev.gnustella.openfriendplus.common.screen;

import com.google.gson.JsonObject;
import dev.gnustella.openfriendplus.common.config.OpenFriendPlusConfig;
import dev.gnustella.openfriendplus.common.config.OpenFriendPlusConfigStore;
import dev.gnustella.openfriendplus.common.config.LocalPortPicker;
import dev.gnustella.openfriendplus.common.diagnostics.DiagnosticsState;
import dev.gnustella.openfriendplus.common.ipc.IpcClient;
import dev.gnustella.openfriendplus.common.ipc.IpcException;
import dev.gnustella.openfriendplus.common.model.Friend;
import dev.gnustella.openfriendplus.common.notice.NoticeSink;
import dev.gnustella.openfriendplus.common.privacy.PrivacyFormatter;
import dev.gnustella.openfriendplus.common.state.FriendsState;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletionException;
import java.util.UUID;
import java.util.function.Consumer;

public final class FriendsController {

    public interface JoinLauncher { void connectToLocalAddress(String hostPort); }
    public enum GameMode { SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR }
    public interface MultiplayBridge {
        boolean canHost();
        boolean publishToFriends(GameMode mode, boolean allowCheats, int maxPlayers);
    }

    private final IpcClient ipc;
    private final FriendsState state;
    private final JoinLauncher joinLauncher;
    private final MultiplayBridge multiplay;
    private final NoticeSink notice;
    private final OpenFriendPlusConfig config;
    private final OpenFriendPlusConfigStore configStore;
    private final DiagnosticsState diagnostics;
    private final PrivacyFormatter privacy;
    private long lastRefreshAt = 0;
    private long nextAllowedRefreshAt = 0;
    private int consecutiveRefreshErrors = 0;
    private String lastJoinListenAddress;

    public FriendsController(IpcClient ipc, FriendsState state,
                             JoinLauncher joinLauncher,
                             MultiplayBridge multiplay,
                             NoticeSink notice) {
        this(ipc, state, joinLauncher, multiplay, notice, new OpenFriendPlusConfig(), null, null);
    }

    public FriendsController(IpcClient ipc, FriendsState state,
                             JoinLauncher joinLauncher,
                             MultiplayBridge multiplay,
                             NoticeSink notice,
                             OpenFriendPlusConfig config,
                             OpenFriendPlusConfigStore configStore,
                             DiagnosticsState diagnostics) {
        this.ipc = ipc;
        this.state = state;
        this.joinLauncher = joinLauncher;
        this.multiplay = multiplay;
        this.notice = notice == null ? new NoticeSink() {} : notice;
        this.config = config == null ? new OpenFriendPlusConfig() : config;
        this.configStore = configStore;
        this.diagnostics = diagnostics;
        this.privacy = new PrivacyFormatter(this.config);
        this.lastJoinListenAddress = this.config.resolveJoinListenAddress();
    }

    public NoticeSink notice() { return notice; }
    public IpcClient ipc() { return ipc; }
    public FriendsState state() { return state; }
    public synchronized String localJoinListenAddress() { return lastJoinListenAddress; }

    public synchronized void refreshIfStale() {
        refresh(false);
    }

    public synchronized void forceRefresh() {
        refresh(true);
    }

    private synchronized void refresh(boolean force) {
        if (ipc == null || !ipc.isRunning()) return;
        if (!force && !config.autoRefreshFriends) return;
        long now = System.currentTimeMillis();
        if (now < nextAllowedRefreshAt) {
            if (force) {
                long waitSeconds = Math.max(1L, (nextAllowedRefreshAt - now + 999L) / 1000L);
                notice.warn("Refresh delayed", "Rate-limit backoff active for " + waitSeconds + "s.");
            }
            return;
        }
        long interval = Math.max(15_000L, config.effectiveRefreshIntervalSeconds() * 1000L);
        if (!force && now - lastRefreshAt < interval) return;
        lastRefreshAt = now;
        ipc.requestAsync("presence.set", IpcClient.params("status", "ONLINE"));
        state.primeFromList(ipc).whenComplete((ignored, err) -> {
            if (err == null) backoffRefresh(null);
            else backoffRefresh(unwrap(err));
        });
    }

    private synchronized void backoffRefresh(Throwable err) {
        if (err == null) {
            consecutiveRefreshErrors = 0;
            return;
        }
        consecutiveRefreshErrors++;
        String msg = err.getMessage() == null ? "" : err.getMessage().toLowerCase();
        long delay = msg.contains("429") || msg.contains("rate limited")
                ? 90_000L
                : Math.min(300_000L, 15_000L * (1L << Math.min(4, consecutiveRefreshErrors)));
        nextAllowedRefreshAt = System.currentTimeMillis() + delay;
    }

    public FriendsOverlayScreen buildOverlay(Runnable onClose) {
        FriendsOverlayScreen overlay = new FriendsOverlayScreen(state, onClose, config);
        boolean showMultiplay = multiplay != null && multiplay.canHost();
        java.util.List<FriendsOverlayScreen.Tab> tabs = new java.util.ArrayList<>();
        if (showMultiplay) tabs.add(new MultiplayTab(state, multiplayActions()));
        tabs.add(new FriendsTab(state, friendActions(), addActions(), this::refreshIfStale, notice, config));
        tabs.add(new PendingTab(state, pendingActions(), config));
        tabs.add(new BlocksTab(state, blocksActions(), config));
        if (config.diagnosticsOverlay && diagnostics != null) {
            tabs.add(new DiagnosticsTab(diagnostics, this::forceRefresh, this::restartCore, this::stopCore, notice));
        }
        if (configStore != null) tabs.add(new SettingsTab(config, configStore, notice, settingsActions()));
        overlay.setTabs(tabs.toArray(new FriendsOverlayScreen.Tab[0]));
        return overlay;
    }

    private void restartCore() {
        stopCore();
        notice.warn("Core restart", "Restart Minecraft to launch a fresh Core process.");
    }

    private void stopCore() {
        try {
            if (ipc != null) ipc.stop();
            notice.success("Core stopped", "OpenFriend Plus Core bridge was stopped.");
        } catch (Throwable t) {
            notice.error("Could not stop Core", t.getMessage());
        }
    }

    private SettingsTab.Actions settingsActions() {
        return new SettingsTab.Actions() {
            @Override public void openDataFolder() {
                if (configStore == null) return;
                try {
                    Path dir = configStore.dataDir();
                    java.nio.file.Files.createDirectories(dir);
                    if (!Desktop.isDesktopSupported()) throw new IOException("Desktop API is not available.");
                    Desktop.getDesktop().open(dir.toFile());
                    notice.success("Data folder opened", dir.toString());
                } catch (Throwable t) {
                    notice.error("Could not open data folder", t.getMessage());
                }
            }

            @Override public void clearLocalAuthAndCache() {
                if (configStore == null) return;
                try {
                    stopCore();
                    configStore.clearLocalAuthAndCache();
                    notice.success("Local data cleared", "Auth/cache data was removed. Restart Minecraft before signing in again.");
                } catch (Throwable t) {
                    notice.error("Could not clear local data", t.getMessage());
                }
            }
        };
    }

    private FriendEntry.Actions friendActions() {
        return new FriendEntry.Actions() {
            @Override public void onJoin(Friend f) {
                String listen = resolveJoinListenAddress();
                JsonObject params = IpcClient.params("name", f.name, "listen", listen);
                ipc.requestAsync("join.start", params).whenComplete((result, err) -> {
                    if (err != null) {
                        boolean alreadyRunning = err instanceof IpcException && ((IpcException) err).isAlreadyRunning();
                        if (alreadyRunning) {
                            String runningListen = state.join().listen == null || state.join().listen.isEmpty() ? listen : state.join().listen;
                            if (joinLauncher != null) joinLauncher.connectToLocalAddress(runningListen);
                            return;
                        }
                        backoffRefresh(err);
                        java.util.logging.Logger.getLogger("openfriendplus.join")
                                .warning("join.start failed for " + privacy.maskName(f.name) + ": " + err.getMessage());
                        String msg = err.getMessage() == null ? "Unknown error" : err.getMessage();
                        if (msg.contains("429") || msg.toLowerCase().contains("rate limited")) {
                            notice.warn("Mojang rate limit", "Please wait a moment before retrying.");
                        } else {
                            notice.error("Could not join " + privacy.maskName(f.name), msg);
                        }
                        return;
                    }
                    String addr = result != null && result.has("listen") ? result.get("listen").getAsString() : listen;
                    synchronized (FriendsController.this) {
                        lastJoinListenAddress = addr;
                    }
                    if (config.showJoinToasts) notice.success("Joining friend", privacy.maskName(f.name));
                    if (joinLauncher != null) joinLauncher.connectToLocalAddress(addr);
                });
            }

            @Override public void onRemove(Friend f) {
                ipc.requestAsync("friends.remove", IpcClient.params("profileId", f.profileId.toString()))
                   .whenComplete((r, err) -> {
                        if (err == null) {
                            notice.success("Friend removed", privacy.maskName(f.name));
                            forceRefresh();
                        } else {
                            notice.error("Could not remove friend", err.getMessage());
                        }
                   });
            }

            @Override public void onBlock(Friend f) {
                ipc.requestAsync("blocks.add", IpcClient.params("profileId", f.profileId.toString(), "name", f.name))
                   .whenComplete((r, err) -> {
                        if (err == null) notice.success("Player blocked", privacy.maskName(f.name));
                        else notice.error("Could not block " + privacy.maskName(f.name), err.getMessage());
                        refreshBlocks();
                   });
            }

            @Override public void onUnblock(Friend f) {
                ipc.requestAsync("blocks.remove", IpcClient.params("profileId", f.profileId.toString()))
                   .whenComplete((r, err) -> {
                        if (err == null) notice.success("Player unblocked", privacy.maskName(f.name));
                        else notice.error("Could not unblock " + privacy.maskName(f.name), err.getMessage());
                        refreshBlocks();
                   });
            }
        };
    }

    private void refreshBlocks() {
        ipc.requestAsync("blocks.list", null).whenComplete((r, err) -> {
            if (err == null && r != null) state.applyBlocksListResult(r);
            else backoffRefresh(err);
        });
    }

    private PendingEntry.Actions pendingActions() {
        return new PendingEntry.Actions() {
            @Override public void onAccept(Friend f)  { handlePending("friends.accept",  f, "Friend request accepted", "Could not accept request"); }
            @Override public void onDecline(Friend f) { handlePending("friends.decline", f, "Friend request declined", "Could not decline request"); }
            @Override public void onCancel(Friend f)  { handlePending("friends.remove",  f, "Request cancelled",       "Could not cancel request"); }
        };
    }

    private void handlePending(String method, Friend f, String okTitle, String errTitle) {
        ipc.requestAsync(method, IpcClient.params("profileId", f.profileId.toString()))
           .whenComplete((r, err) -> {
                if (err == null) {
                    state.removePending(f.profileId);
                    notice.success(okTitle, privacy.maskName(f.name));
                    forceRefresh();
                } else {
                    notice.error(errTitle, err.getMessage());
                }
           });
    }

    private AddFriendTab.Actions addActions() {
        return new AddFriendTab.Actions() {
            @Override public void search(String name, Consumer<AddFriendTab.SearchResult> cb) {
                ipc.requestAsync("friends.search", IpcClient.params("name", name))
                        .whenComplete((result, err) -> cb.accept(mapSearchResult(name, result, err)));
            }

            @Override public void add(String name, Consumer<Throwable> cb) {
                ipc.requestAsync("friends.add", IpcClient.params("name", name)).whenComplete((result, err) -> {
                    if (err == null) forceRefresh();
                    else {
                        String msg = err.getMessage();
                        if (msg == null || msg.isEmpty()) msg = "Could not send request.";
                        notice.error("Friend request to " + privacy.maskName(name) + " failed", msg);
                    }
                    cb.accept(err);
                });
            }
        };
    }

    private AddFriendTab.SearchResult mapSearchResult(String query, JsonObject result, Throwable err) {
        if (err != null || result == null) return new AddFriendTab.SearchResult(AddFriendTab.State.ERROR, query, err == null ? "Search failed" : err.getMessage());
        boolean found = result.has("found") && result.get("found").getAsBoolean();
        if (!found) return new AddFriendTab.SearchResult(AddFriendTab.State.NOT_FOUND, query, "");
        String name = result.has("name") ? result.get("name").getAsString() : query;
        if (result.has("isFriend") && result.get("isFriend").getAsBoolean()) return new AddFriendTab.SearchResult(AddFriendTab.State.ALREADY_FRIEND, name, "");
        if (result.has("isIncoming") && result.get("isIncoming").getAsBoolean()) return new AddFriendTab.SearchResult(AddFriendTab.State.INCOMING_EXISTS, name, "");
        if (result.has("isOutgoing") && result.get("isOutgoing").getAsBoolean()) return new AddFriendTab.SearchResult(AddFriendTab.State.OUTGOING_EXISTS, name, "");
        return new AddFriendTab.SearchResult(AddFriendTab.State.FOUND, name, "");
    }

    private MultiplayTab.Actions multiplayActions() {
        return new MultiplayTab.Actions() {
            @Override public boolean canHost() { return multiplay != null && multiplay.canHost(); }
            @Override public boolean isHosting() { return state.host().running; }
            @Override public boolean publishToFriends(GameMode mode, boolean allowCheats, int maxPlayers) { return multiplay != null && multiplay.publishToFriends(mode, allowCheats, maxPlayers); }
            @Override public void stopHosting() {
                ipc.requestAsync("host.stop", null).whenComplete((r, err) -> {
                    if (err == null) notice.success("Hosting stopped", "Your world is no longer shared.");
                    else notice.error("Could not stop hosting", err.getMessage());
                });
            }
        };
    }

    private BlocksTab.Actions blocksActions() {
        return profileId -> ipc.requestAsync("blocks.remove", IpcClient.params("profileId", profileId.toString()))
                .whenComplete((r, err) -> {
                    if (err == null) {
                        notice.success("Player unblocked", privacy.maskUuid(profileId).substring(0, Math.min(8, privacy.maskUuid(profileId).length())));
                        refreshBlocks();
                    } else {
                        notice.error("Could not unblock", err.getMessage());
                    }
                });
    }

    private synchronized String resolveJoinListenAddress() {
        if (state.join().running && state.join().listen != null && !state.join().listen.isEmpty()) {
            lastJoinListenAddress = state.join().listen;
            return lastJoinListenAddress;
        }

        int configuredPort = config.effectiveLocalJoinPort();
        int selectedPort = configuredPort;
        if (config.randomizeJoinPort) {
            selectedPort = LocalPortPicker.findFreePort();
        } else if (!LocalPortPicker.isPortAvailable(configuredPort)) {
            selectedPort = LocalPortPicker.findFreePort();
            notice.warn("Join port in use", "Using fallback port " + selectedPort + " for this join.");
        }
        lastJoinListenAddress = "127.0.0.1:" + selectedPort;
        return lastJoinListenAddress;
    }

    private static Throwable unwrap(Throwable err) {
        if (err instanceof CompletionException && err.getCause() != null) return err.getCause();
        return err;
    }
}
