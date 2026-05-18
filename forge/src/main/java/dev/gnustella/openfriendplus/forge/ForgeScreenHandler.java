/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 *
 * Adds the OpenFriend "Friends" icon button to TitleScreen, PauseScreen, and
 * JoinMultiplayerScreen via Forge's ScreenEvent.Init.Post — independent of
 * the per-MC-version Mixin path that Fabric uses. Forge does not auto-discover
 * mixin configs, so we use the loader-idiomatic event bus instead.
 */
package dev.gnustella.openfriendplus.forge;

import dev.gnustella.openfriendplus.mc.OpenFriendPlusMod;
import dev.gnustella.openfriendplus.mc.ui.MCScreenOpener;
import dev.gnustella.openfriendplus.mc.ui.OpenFriendPlusIconButton;
import dev.gnustella.openfriendplus.mc.ui.OpenFriendPlusToastOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ForgeScreenHandler {

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof TitleScreen)
                && !(screen instanceof PauseScreen)
                && !(screen instanceof JoinMultiplayerScreen)) {
            return;
        }
        OpenFriendPlusIconButton btn = new OpenFriendPlusIconButton(
                screen.width - 26, 6, 20, 20,
                b -> {
                    try {
                        MCScreenOpener o = OpenFriendPlusMod.opener();
                        if (o != null) o.openFriendsOverlay();
                    } catch (Throwable t) {
                        OpenFriendPlusMod.LOG.error("Friends button click failed", t);
                    }
                });
        event.addListener(btn);
    }

    @SubscribeEvent
    public void onGuiRenderPost(RenderGuiEvent.Post event) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.screen != null) return;
            int sw = mc == null ? 320 : mc.getWindow().getGuiScaledWidth();
            OpenFriendPlusToastOverlay.render(event.getGuiGraphics(), sw);
        } catch (Throwable t) {
            OpenFriendPlusMod.LOG.warn("toast overlay (HUD) render failed: {}", t.getMessage());
        }
    }

    @SubscribeEvent
    public void onScreenRenderPost(ScreenEvent.Render.Post event) {
        try {
            Minecraft mc = Minecraft.getInstance();
            int sw = mc == null ? 320 : mc.getWindow().getGuiScaledWidth();
            OpenFriendPlusToastOverlay.render(event.getGuiGraphics(), sw);
        } catch (Throwable t) {
            OpenFriendPlusMod.LOG.warn("toast overlay (screen) render failed: {}", t.getMessage());
        }
    }
}
