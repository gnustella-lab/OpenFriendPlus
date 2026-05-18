/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.fabric;

import dev.gnustella.openfriendplus.common.BuildInfo;
import dev.gnustella.openfriendplus.mc.OpenFriendPlusMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class OpenFriendPlusFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        String minecraftVersion = FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
        BuildInfo.setRuntimeInfo("Fabric", minecraftVersion);
        OpenFriendPlusMod.bootstrap();
    }
}
