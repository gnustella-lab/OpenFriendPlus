/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.forge;

import dev.gnustella.openfriendplus.mc.OpenFriendPlusMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("openfriendplus")
public final class OpenFriendPlusForgeMod {
    public OpenFriendPlusForgeMod() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            OpenFriendPlusMod.bootstrap();
            MinecraftForge.EVENT_BUS.register(new ForgeScreenHandler());
        }
    }
}
