/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.mc.toast;

import com.google.gson.JsonObject;
import dev.gnustella.openfriendplus.common.config.OpenFriendPlusConfig;
import dev.gnustella.openfriendplus.common.ipc.IpcListener;
import dev.gnustella.openfriendplus.common.privacy.PrivacyFormatter;
import dev.gnustella.openfriendplus.mc.OpenFriendPlusMod;
import dev.gnustella.openfriendplus.mc.ui.SignInScreen;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public final class ToastDispatcher implements IpcListener {

    @Override
    public void onNotification(String method, JsonObject params) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        String name = params.has("name") && !params.get("name").isJsonNull()
                ? params.get("name").getAsString()
                : "";
        OpenFriendPlusConfig config = OpenFriendPlusMod.config();
        PrivacyFormatter privacy = new PrivacyFormatter(config);
        switch (method) {
            case "auth.deviceCode": {
                String code = params.has("userCode") ? params.get("userCode").getAsString() : "";
                String uri  = params.has("verificationUri") ? params.get("verificationUri").getAsString() : "";
                OpenFriendPlusMod.LOG.info("OpenFriend Plus sign-in: visit {} and enter {}", uri, privacy.maskDeviceCode(code));
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
                final String displayName = privacy.maskName(name);
                mc.execute(() -> {
                    SignInScreen cur = SignInScreen.current();
                    if (cur != null) cur.markSignedIn(displayName);
                    ToastComponent toasts = mc.getToasts();
                    if (toasts != null) {
                        toasts.addToast(new FriendsToast(
                                new TextComponent("Signed in"),
                                new TextComponent(displayName)));
                    }
                });
                break;
            }
            case "friend.requestIncoming":
            case "friend.added":
            case "friend.joined": {
                if (method.equals("friend.joined")) {
                    if (config != null && !config.showJoinToasts) return;
                } else {
                    if (config != null && !config.showRequestToasts) return;
                }
                ToastComponent toasts = mc.getToasts();
                if (toasts == null) return;
                Component title;
                Component body;
                if (method.equals("friend.requestIncoming")) {
                    if (name.isEmpty()) return;
                    title = new TextComponent("Friend request");
                    body  = new TextComponent(privacy.maskName(name));
                } else if (method.equals("friend.added")) {
                    if (name.isEmpty()) return;
                    title = new TextComponent("Friend added");
                    body  = new TextComponent(privacy.maskName(name));
                } else {
                    String peer = params.has("pmid") ? privacy.maskUuid(params.get("pmid").getAsString()) : "Someone";
                    title = new TextComponent("Friend joined");
                    body  = new TextComponent(peer);
                }
                toasts.addToast(new FriendsToast(title, body));
                break;
            }
            default:
        }
    }
}
