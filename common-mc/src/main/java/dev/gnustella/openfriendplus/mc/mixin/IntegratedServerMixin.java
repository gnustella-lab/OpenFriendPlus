/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.mc.mixin;

import dev.gnustella.openfriendplus.mc.OpenFriendPlusMod;
import dev.gnustella.openfriendplus.mc.ui.MCScreenOpener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {

    @Inject(method = "publishServer", at = @At("RETURN"))
    private void openfriend$onPublish(GameType gameType, boolean cheats, int port,
                                      CallbackInfoReturnable<Boolean> cir) {
        try {
            if (!Boolean.TRUE.equals(cir.getReturnValue())) return;
            IntegratedServer self = (IntegratedServer) (Object) this;
            int actualPort = self.getPort();
            MCScreenOpener opener = OpenFriendPlusMod.opener();
            if (opener != null) opener.onServerPublished(actualPort);
        } catch (Throwable t) {
            OpenFriendPlusMod.LOG.error("OpenFriend Plus publish hook failed", t);
        }
    }
}
