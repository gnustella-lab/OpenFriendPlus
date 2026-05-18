/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.mc.toast;

import com.google.gson.JsonObject;
import dev.gnustella.openfriendplus.common.ipc.IpcListener;
import dev.gnustella.openfriendplus.mc.OpenFriendPlusMod;
import dev.gnustella.openfriendplus.mc.ui.SignInScreen;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

public final class ToastDispatcher implements IpcListener {

    @Override
    public void onNotification(String method, JsonObject params) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        String name = params.has("name") && !params.get("name").isJsonNull()
                ? params.get("name").getAsString()
                : "";
        switch (method) {
            case "auth.deviceCode": {
                String code = params.has("userCode") ? params.get("userCode").getAsString() : "";
                String uri  = params.has("verificationUri") ? params.get("verificationUri").getAsString() : "";
                OpenFriendPlusMod.LOG.info("OpenFriend sign-in: visit {} and enter {}", uri, code);
                mc.execute(() -> {
                    SignInScreen scr = new SignInScreen(uri, code);
                    SignInScreen.setCurrent(scr);
                    mc.setScreen(scr);
                    if (!uri.isEmpty()) {
                        try { Util.getPlatform().openUri(uri); } catch (Throwable ignored) {}
                    }
                });
                break;
            }
            case "auth.signedIn": {
                final String displayName = name;
                mc.execute(() -> {
                    SignInScreen cur = SignInScreen.current();
                    if (cur != null) cur.markSignedIn(displayName);
                    deferAddToast(mc, new FriendsToast(
                            Component.literal("Signed in"),
                            Component.literal(displayName)));
                });
                break;
            }
            case "friend.requestIncoming":
            case "friend.added":
            case "friend.joined": {
                Component title;
                Component body;
                if (method.equals("friend.requestIncoming")) {
                    if (name.isEmpty()) return;
                    title = Component.literal("Friend request");
                    body  = Component.literal(name);
                } else if (method.equals("friend.added")) {
                    if (name.isEmpty()) return;
                    title = Component.literal("Friend added");
                    body  = Component.literal(name);
                } else {
                    String peer = params.has("pmid") ? params.get("pmid").getAsString().substring(0, 8) : "Someone";
                    title = Component.literal("Friend joined");
                    body  = Component.literal(peer + "...");
                }
                deferAddToast(mc, new FriendsToast(title, body));
                break;
            }
            default:
        }
    }

    private static void deferAddToast(Minecraft mc, FriendsToast toast) {
        Thread t = new Thread(() -> {
            int attempts = 0;
            while (mc.getOverlay() != null && attempts < 200) {
                try { Thread.sleep(50); } catch (InterruptedException e) { return; }
                attempts++;
            }
            mc.execute(() -> {
                ToastComponent toasts = mc.getToasts();
                if (toasts != null) toasts.addToast(toast);
            });
        }, "openfriend-toast-defer");
        t.setDaemon(true);
        t.start();
    }
}
