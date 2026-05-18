/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.mc.mixin;

import dev.gnustella.openfriendplus.mc.OpenFriendPlusMod;
import dev.gnustella.openfriendplus.mc.ui.MCScreenOpener;
import dev.gnustella.openfriendplus.mc.ui.OpenFriendPlusIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin() { super(Component.empty()); }

    @Inject(method = "init", at = @At("RETURN"))
    private void openfriend$addFriendsButton(CallbackInfo ci) {
        OpenFriendPlusIconButton btn = new OpenFriendPlusIconButton(this.width - 26, 6, 20, 20,
                b -> {
                    try {
                        MCScreenOpener o = OpenFriendPlusMod.opener();
                        if (o != null) o.openFriendsOverlay();
                    } catch (Throwable t) {
                        OpenFriendPlusMod.LOG.error("Friends button click failed", t);
                    }
                });
        btn.setTooltip(Tooltip.create(Component.literal("Friends")));
        addRenderableWidget(btn);
    }
}
