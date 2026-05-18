/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.mc.mixin;

import dev.gnustella.openfriendplus.mc.OpenFriendPlusMod;
import dev.gnustella.openfriendplus.mc.ui.OpenFriendPlusToastOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastComponent.class)
public abstract class GuiMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void openfriend$renderOurToasts(GuiGraphics graphics, CallbackInfo ci) {
        try {
            Minecraft mc = Minecraft.getInstance();
            int sw = mc == null ? 320 : mc.getWindow().getGuiScaledWidth();
            OpenFriendPlusToastOverlay.render(graphics, sw);
        } catch (Throwable t) {
            OpenFriendPlusMod.LOG.error("OpenFriend Plus toast overlay render failed", t);
        }
    }
}
