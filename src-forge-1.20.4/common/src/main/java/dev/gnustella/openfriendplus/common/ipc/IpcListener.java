/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.ipc;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface IpcListener {
    void onNotification(String method, JsonObject params);
}
