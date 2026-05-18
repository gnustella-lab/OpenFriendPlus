/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public final class LocalPortPicker {
    private static final String LOOPBACK = "127.0.0.1";

    private LocalPortPicker() {}

    public static int findFreePort() {
        try {
            InetAddress address = InetAddress.getByName(LOOPBACK);
            try (ServerSocket socket = new ServerSocket(0, 0, address)) {
                socket.setReuseAddress(false);
                return socket.getLocalPort();
            }
        } catch (IOException ignored) {
            return 25577;
        }
    }

    public static boolean isPortAvailable(int port) {
        if (port < 1024 || port > 65535) return false;
        try {
            InetAddress address = InetAddress.getByName(LOOPBACK);
            try (ServerSocket socket = new ServerSocket(port, 0, address)) {
                socket.setReuseAddress(false);
                return true;
            }
        } catch (IOException ignored) {
            return false;
        }
    }
}
