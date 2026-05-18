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
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

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
                config = loadDefaultConfig();
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

    public synchronized void clearLocalAuthAndCache() throws IOException {
        final Path root = dataDir();
        Files.createDirectories(root);
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!shouldPreserve(file)) Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) throw exc;
                if (!dir.equals(root)) {
                    try {
                        Files.deleteIfExists(dir);
                    } catch (DirectoryNotEmptyException ignored) {
                        // Preserved files such as config.json keep their parent directory.
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        save();
    }

    private boolean shouldPreserve(Path file) {
        if (file == null) return false;
        if (file.equals(path)) return true;
        Path name = file.getFileName();
        if (name == null) return false;
        String s = name.toString();
        return "openfriendplus-defaults.json".equals(s) || "openfriendplus.json".equals(s);
    }

    private OpenFriendPlusConfig loadDefaultConfig() {
        for (Path candidate : defaultCandidates()) {
            if (candidate == null || !Files.isRegularFile(candidate)) continue;
            try (Reader reader = Files.newBufferedReader(candidate, StandardCharsets.UTF_8)) {
                OpenFriendPlusConfig defaults = GSON.fromJson(reader, OpenFriendPlusConfig.class);
                if (defaults != null) {
                    defaults.sanitize();
                    return defaults;
                }
            } catch (Exception ignored) {}
        }
        return new OpenFriendPlusConfig();
    }

    private List<Path> defaultCandidates() {
        List<Path> candidates = new ArrayList<>();
        String explicit = System.getProperty("openfriendplus.defaults");
        if (explicit != null && !explicit.trim().isEmpty()) candidates.add(Paths.get(explicit.trim()));
        Path cwd = Paths.get("").toAbsolutePath();
        candidates.add(cwd.resolve("config").resolve("openfriendplus-defaults.json"));
        candidates.add(cwd.resolve("config").resolve("openfriendplus.json"));
        candidates.add(dataDir().resolve("openfriendplus-defaults.json"));
        return candidates;
    }
}
