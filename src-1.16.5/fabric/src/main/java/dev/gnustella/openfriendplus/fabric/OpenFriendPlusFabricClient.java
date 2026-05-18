/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.fabric;

import dev.gnustella.openfriendplus.mc.OpenFriendPlusMod;
import net.fabricmc.api.ClientModInitializer;

public final class OpenFriendPlusFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OpenFriendPlusMod.bootstrap();
    }
}
