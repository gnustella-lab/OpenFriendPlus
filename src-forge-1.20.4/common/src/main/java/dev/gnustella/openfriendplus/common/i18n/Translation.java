/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.i18n;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Translation {
    private final Map<String, String> values;
    private final Translation fallback;

    private Translation(Map<String, String> values, Translation fallback) {
        this.values = values;
        this.fallback = fallback;
    }

    public static Translation load(String code, Translation fallback) {
        Map<String, String> values = new HashMap<>();
        String path = "assets/openfriendplus/lang/" + code + ".json";
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = Translation.class.getClassLoader();
        try (InputStream in = cl.getResourceAsStream(path)) {
            if (in != null) {
                try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    JsonElement parsed = new JsonParser().parse(reader);
                    if (parsed != null && parsed.isJsonObject()) {
                        JsonObject object = parsed.getAsJsonObject();
                        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                            JsonElement value = entry.getValue();
                            if (value != null && value.isJsonPrimitive()) {
                                values.put(entry.getKey(), value.getAsString());
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return new Translation(values, fallback);
    }

    public String get(String key, String defaultValue) {
        String value = values.get(key);
        if (value != null) return value;
        if (fallback != null) return fallback.get(key, defaultValue);
        return defaultValue == null ? key : defaultValue;
    }
}
