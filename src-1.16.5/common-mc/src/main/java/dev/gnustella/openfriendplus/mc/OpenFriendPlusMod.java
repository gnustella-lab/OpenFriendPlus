/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.mc;

import com.google.gson.JsonObject;
import dev.gnustella.openfriendplus.common.ipc.IpcClient;
import dev.gnustella.openfriendplus.common.screen.FriendsController;
import dev.gnustella.openfriendplus.common.state.FriendsState;
import dev.gnustella.openfriendplus.helper.CoreLauncher;
import dev.gnustella.openfriendplus.mc.toast.ToastDispatcher;
import dev.gnustella.openfriendplus.mc.ui.MCScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.UUID;

public final class OpenFriendPlusMod {

    public static final String MOD_ID = "openfriendplus";
    public static final String MOD_VERSION = "1.0.0";
    public static final Logger LOG = LogManager.getLogger("openfriend");

    private static volatile boolean bootstrapped;
    private static volatile IpcClient ipcClient;
    private static volatile FriendsState state;
    private static volatile FriendsController controller;
    private static volatile MCScreenOpener opener;

    private OpenFriendPlusMod() {}

    public static IpcClient ipc()                { return ipcClient; }
    public static FriendsState state()           { return state; }
    public static FriendsController controller() { return controller; }
    public static MCScreenOpener opener()        { return opener; }

    private static UUID resolveProfileId(User user) {
        if (user == null) return null;
        try {
            com.mojang.authlib.GameProfile p = user.getGameProfile();
            if (p != null) return p.getId();
        } catch (Throwable t) {
            LOG.warn("getGameProfile() failed: {}", t.getMessage());
        }
        return null;
    }

    private static void handOffMinecraftSessionOrSignIn(IpcClient ipc, FriendsState s) {
        Minecraft mc = Minecraft.getInstance();
        User user = mc != null ? mc.getUser() : null;
        String token = user != null ? user.getAccessToken() : null;
        UUID profileId = resolveProfileId(user);

        if (token != null && !token.isEmpty() && profileId != null) {
            String name = user.getName();
            LOG.info("OpenFriend Plus Plus: handing Minecraft session to Core (user={})", name);
            JsonObject params = IpcClient.params(
                    "accessToken", token,
                    "profileId", profileId.toString(),
                    "name", name);
            ipc.requestAsync("auth.useMojangSession", params).whenComplete((result, err) -> {
                if (err == null) {
                    LOG.info("OpenFriend Plus Plus: Core accepted Minecraft session");
                    s.primeFromList(ipc);
                    probeFriendsList(ipc);
                    ipc.requestAsync("presence.set", IpcClient.params("status", "ONLINE"));
                    ipc.requestAsync("presence.watch", IpcClient.params("intervalSeconds", 12));
                } else {
                    LOG.warn("OpenFriend Plus Plus: Core rejected Minecraft session ({}); falling back to device-code", err.getMessage());
                    triggerDeviceCodeSignIn(ipc, s);
                }
            });
        } else {
            LOG.info("OpenFriend Plus Plus: no Minecraft session available; using device-code sign-in");
            triggerDeviceCodeSignIn(ipc, s);
        }
    }

    private static void probeFriendsList(IpcClient ipc) {
        ipc.requestAsync("friends.list", null).whenComplete((r, e) -> {
            if (e != null) {
                LOG.warn("OpenFriend Plus Plus: friends.list failed: {}", e.getMessage());
            } else if (r != null) {
                int f = r.has("friends")  ? r.getAsJsonArray("friends").size()  : 0;
                int i = r.has("incoming") ? r.getAsJsonArray("incoming").size() : 0;
                int o = r.has("outgoing") ? r.getAsJsonArray("outgoing").size() : 0;
                LOG.info("OpenFriend Plus Plus: friends.list = {} friends, {} incoming, {} outgoing", f, i, o);
            }
        });
    }

    private static void triggerDeviceCodeSignIn(IpcClient ipc, FriendsState s) {
        ipc.requestAsync("auth.signIn", null).whenComplete((result, err) -> {
            if (err == null) s.primeFromList(ipc);
            else LOG.warn("OpenFriend Plus sign-in did not complete: {}", err.getMessage());
        });
    }

    public static synchronized void bootstrap() {
        if (bootstrapped) return;
        bootstrapped = true;
        LOG.info("OpenFriend Plus {} initialising (client)", MOD_VERSION);

        FriendsState s = new FriendsState();
        MCScreenOpener so = new MCScreenOpener();
        OpenFriendPlusMod.state  = s;
        OpenFriendPlusMod.opener = so;

        try {
            CoreLauncher launcher = new CoreLauncher(
                    CoreLauncher.defaultDataDir(MOD_ID),
                    OpenFriendPlusMod.class.getClassLoader());

            IpcClient ipc = new IpcClient(() -> {
                try {
                    return launcher.spawnIpc();
                } catch (Exception e) {
                    LOG.error("Failed to launch OpenFriend Plus Core: {}", e.getMessage());
                    return null;
                }
            }, line -> LOG.info("core: {}", line));

            ipc.addListener(s);
            ipc.addListener(new ToastDispatcher());
            ipc.addListener((method, params) -> {
                if (!"log".equals(method) || params == null) return;
                String level = params.has("level") ? params.get("level").getAsString() : "INFO";
                String msg = params.has("msg") ? params.get("msg").getAsString() : "";
                String detail = msg;
                if (params.has("attrs") && params.get("attrs").isJsonObject()) {
                    com.google.gson.JsonObject attrs = params.getAsJsonObject("attrs");
                    if (attrs.size() > 0) detail = detail + " " + attrs.toString();
                }
                switch (level) {
                    case "ERROR": LOG.error("core: {}", detail); break;
                    case "WARN":  LOG.warn ("core: {}", detail); break;
                    case "DEBUG": LOG.debug("core: {}", detail); break;
                    default:      LOG.info ("core: {}", detail); break;
                }
            });

            try {
                ipc.start();
            } catch (Throwable t) {
                LOG.warn("OpenFriend Plus Core did not start: {} — UI will run in offline mode", t.getMessage());
            }
            if (ipc.isRunning()) {
                handOffMinecraftSessionOrSignIn(ipc, s);
            }

            FriendsController fc = new FriendsController(ipc, s, so::connectToLocalAddress, so, so);
            so.setController(fc);
            OpenFriendPlusMod.ipcClient  = ipc;
            OpenFriendPlusMod.controller = fc;

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { ipc.stop(); } catch (Throwable ignored) {}
            }, "openfriendplus-shutdown"));

            LOG.info("OpenFriend Plus ready (core running: {})", ipc.isRunning());
        } catch (Throwable t) {
            LOG.error("OpenFriend Plus Core bridge unavailable; UI will open in offline mode", t);
        }
    }
}
