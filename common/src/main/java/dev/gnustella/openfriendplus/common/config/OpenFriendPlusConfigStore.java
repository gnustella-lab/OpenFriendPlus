/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class OpenFriendPlusConfigStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path path;
    private OpenFriendPlusConfig config = new OpenFriendPlusConfig();

    public OpenFriendPlusConfigStore(Path dataDir) {
        this.path = dataDir.resolve("config.json");
    }

    public synchronized OpenFriendPlusConfig load() {
        try {
            Files.createDirectories(path.getParent());
            if (Files.notExists(path)) {
                config = new OpenFriendPlusConfig();
                save();
                return config;
            }
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                OpenFriendPlusConfig loaded = GSON.fromJson(reader, OpenFriendPlusConfig.class);
                config = loaded == null ? new OpenFriendPlusConfig() : loaded;
                config.sanitize();
                save();
            }
        } catch (Exception ignored) {
            config = new OpenFriendPlusConfig();
        }
        return config;
    }

    public synchronized void save() throws IOException {
        config.sanitize();
        Files.createDirectories(path.getParent());
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        }
    }

    public synchronized OpenFriendPlusConfig config() { return config; }
    public Path path() { return path; }
    public Path dataDir() { return path.getParent(); }
}
