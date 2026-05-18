/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.i18n;

import java.util.Locale;

public final class Lang {
    private static volatile Translation active = Translation.load("en_us", null);

    private Lang() {}

    public static synchronized void configure(String language) {
        Translation en = Translation.load("en_us", null);
        String code = resolve(language);
        active = "en_us".equals(code) ? en : Translation.load(code, en);
    }

    public static String tr(String key) {
        return tr(key, key);
    }

    public static String tr(String key, String defaultValue) {
        return active.get(key, defaultValue);
    }

    private static String resolve(String language) {
        if (language == null || language.trim().isEmpty() || "auto".equalsIgnoreCase(language.trim())) {
            Locale locale = Locale.getDefault();
            if ("pt".equalsIgnoreCase(locale.getLanguage()) && "BR".equalsIgnoreCase(locale.getCountry())) {
                return "pt_br";
            }
            return "en_us";
        }
        return language.trim().toLowerCase(Locale.ROOT).replace('-', '_');
    }
}
