/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.mc.mixin;

import dev.gnustella.openfriendplus.mc.ui.OpenFriendPlusToastOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerEventHandler.class)
public abstract class ScreenMouseMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void openfriend$interceptToastClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        if ((Object) this != mc.screen) return;
        int sw = mc.getWindow().getGuiScaledWidth();
        if (OpenFriendPlusToastOverlay.handleClick((int) mouseX, (int) mouseY, sw)) {
            cir.setReturnValue(true);
        }
    }
}
