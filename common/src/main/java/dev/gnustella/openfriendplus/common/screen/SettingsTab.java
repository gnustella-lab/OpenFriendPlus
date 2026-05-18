/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.screen;

import dev.gnustella.openfriendplus.common.config.OpenFriendPlusConfig;
import dev.gnustella.openfriendplus.common.config.OpenFriendPlusConfigStore;
import dev.gnustella.openfriendplus.common.notice.NoticeSink;
import dev.gnustella.openfriendplus.common.ui.UButton;
import dev.gnustella.openfriendplus.common.ui.UComponent;
import dev.gnustella.openfriendplus.common.ui.UInput;
import dev.gnustella.openfriendplus.common.ui.UPanel;
import dev.gnustella.openfriendplus.common.ui.URenderer;
import dev.gnustella.openfriendplus.common.ui.UTheme;

public final class SettingsTab implements FriendsOverlayScreen.Tab {
    private final OpenFriendPlusConfig config;
    private final OpenFriendPlusConfigStore store;
    private final NoticeSink notice;

    public SettingsTab(OpenFriendPlusConfig config, OpenFriendPlusConfigStore store, NoticeSink notice) {
        this.config = config;
        this.store = store;
        this.notice = notice;
    }

    @Override public String id() { return "settings"; }
    @Override public String label() { return "Settings"; }
    @Override public int badge() { return 0; }
    @Override public UComponent body() { return new BodyPanel(); }

    private final class BodyPanel extends UPanel {
        private final UButton streamer = toggle("Streamer mode", () -> config.streamerMode, v -> config.streamerMode = v);
        private final UButton hideNames = toggle("Hide gamertags", () -> config.hideGamertags, v -> config.hideGamertags = v);
        private final UButton joinToasts = toggle("Join notifications", () -> config.showJoinToasts, v -> config.showJoinToasts = v);
        private final UButton presenceToasts = toggle("Presence notifications", () -> config.showPresenceToasts, v -> config.showPresenceToasts = v);
        private final UButton diagnostics = toggle("Diagnostics overlay", () -> config.diagnosticsOverlay, v -> config.diagnosticsOverlay = v);
        private final UInput refresh = new UInput().setDigitsOnly(true).setMaxLength(3).setText(String.valueOf(config.refreshIntervalSeconds));
        private final UInput port = new UInput().setDigitsOnly(true).setMaxLength(5).setText(String.valueOf(config.localJoinPort));
        private final UButton save = new UButton("Save", this::save).setStyle(UButton.Style.PRIMARY);
        private final UButton reset = new UButton("Defaults", this::reset).setStyle(UButton.Style.SUBTLE);

        BodyPanel() {
            setBackground(Background.NONE);
            addChild(streamer);
            addChild(hideNames);
            addChild(joinToasts);
            addChild(presenceToasts);
            addChild(diagnostics);
            addChild(refresh);
            addChild(port);
            addChild(save);
            addChild(reset);
        }

        @Override
        protected void onLayout() {
            int pad = 10;
            int colGap = 8;
            int rowH = 22;
            int leftW = Math.max(120, (width - pad * 2 - colGap) / 2);
            int rightX = x + pad + leftW + colGap;
            int rightW = width - pad * 2 - leftW - colGap;
            int yy = y + pad;
            streamer.setBounds(x + pad, yy, leftW, rowH);
            hideNames.setBounds(rightX, yy, rightW, rowH);
            yy += rowH + 4;
            joinToasts.setBounds(x + pad, yy, leftW, rowH);
            presenceToasts.setBounds(rightX, yy, rightW, rowH);
            yy += rowH + 4;
            diagnostics.setBounds(x + pad, yy, leftW, rowH);
            yy += rowH + 18;
            refresh.setBounds(x + pad + 116, yy - 2, 64, rowH);
            port.setBounds(rightX + 86, yy - 2, 70, rowH);
            yy += rowH + 10;
            save.setBounds(x + pad, yy, 78, rowH);
            reset.setBounds(x + pad + 86, yy, 78, rowH);
        }

        @Override
        public void render(URenderer r) {
            super.render(r);
            int yy = y + 10 + (22 + 4) * 3 + 2;
            r.drawText(x + 10, yy, "Refresh interval", UTheme.TEXT_DIM);
            r.drawText(x + Math.max(120, (width - 28) / 2) + 28, yy, "Join port", UTheme.TEXT_DIM);
            r.drawText(x + 10, y + height - 16, "OpenFriend Plus is an unofficial fork.", UTheme.TEXT_FAINT);
        }

        private void save() {
            config.refreshIntervalSeconds = parse(refresh.getText(), config.refreshIntervalSeconds);
            config.localJoinPort = parse(port.getText(), config.localJoinPort);
            config.sanitize();
            refresh.setText(String.valueOf(config.refreshIntervalSeconds));
            port.setText(String.valueOf(config.localJoinPort));
            try {
                store.save();
                notice.success("Settings saved", "OpenFriend Plus config updated.");
            } catch (Exception e) {
                notice.error("Could not save settings", e.getMessage());
            }
        }

        private void reset() {
            OpenFriendPlusConfig defaults = new OpenFriendPlusConfig();
            config.streamerMode = defaults.streamerMode;
            config.hideGamertags = defaults.hideGamertags;
            config.showJoinToasts = defaults.showJoinToasts;
            config.showPresenceToasts = defaults.showPresenceToasts;
            config.diagnosticsOverlay = defaults.diagnosticsOverlay;
            config.refreshIntervalSeconds = defaults.refreshIntervalSeconds;
            config.localJoinPort = defaults.localJoinPort;
            refresh.setText(String.valueOf(config.refreshIntervalSeconds));
            port.setText(String.valueOf(config.localJoinPort));
            syncToggleLabels();
        }

        private UButton toggle(String label, BoolGetter getter, BoolSetter setter) {
            UButton button = new UButton("", null).setStyle(UButton.Style.SUBTLE);
            button.setOnClick(() -> {
                setter.set(!getter.get());
                button.setLabel(label + ": " + (getter.get() ? "ON" : "OFF"));
            });
            button.setLabel(label + ": " + (getter.get() ? "ON" : "OFF"));
            return button;
        }

        private void syncToggleLabels() {
            streamer.setLabel("Streamer mode: " + (config.streamerMode ? "ON" : "OFF"));
            hideNames.setLabel("Hide gamertags: " + (config.hideGamertags ? "ON" : "OFF"));
            joinToasts.setLabel("Join notifications: " + (config.showJoinToasts ? "ON" : "OFF"));
            presenceToasts.setLabel("Presence notifications: " + (config.showPresenceToasts ? "ON" : "OFF"));
            diagnostics.setLabel("Diagnostics overlay: " + (config.diagnosticsOverlay ? "ON" : "OFF"));
        }

        private int parse(String value, int fallback) {
            try { return Integer.parseInt(value); } catch (Exception ignored) { return fallback; }
        }
    }

    private interface BoolGetter { boolean get(); }
    private interface BoolSetter { void set(boolean value); }
}
