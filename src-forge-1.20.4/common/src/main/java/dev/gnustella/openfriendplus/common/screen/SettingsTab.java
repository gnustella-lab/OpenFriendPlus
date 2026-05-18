/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.screen;

import dev.gnustella.openfriendplus.common.config.OpenFriendPlusConfig;
import dev.gnustella.openfriendplus.common.config.OpenFriendPlusConfigStore;
import dev.gnustella.openfriendplus.common.i18n.Lang;
import dev.gnustella.openfriendplus.common.i18n.TranslationKey;
import dev.gnustella.openfriendplus.common.notice.NoticeSink;
import dev.gnustella.openfriendplus.common.ui.UButton;
import dev.gnustella.openfriendplus.common.ui.UComponent;
import dev.gnustella.openfriendplus.common.ui.UInput;
import dev.gnustella.openfriendplus.common.ui.UPanel;
import dev.gnustella.openfriendplus.common.ui.URenderer;
import dev.gnustella.openfriendplus.common.ui.UTheme;

public final class SettingsTab implements FriendsOverlayScreen.Tab {
    public interface Actions {
        default void openDataFolder() {}
        default void clearLocalAuthAndCache() {}
    }

    private final OpenFriendPlusConfig config;
    private final OpenFriendPlusConfigStore store;
    private final NoticeSink notice;
    private final Actions actions;

    public SettingsTab(OpenFriendPlusConfig config, OpenFriendPlusConfigStore store, NoticeSink notice, Actions actions) {
        this.config = config;
        this.store = store;
        this.notice = notice;
        this.actions = actions == null ? new Actions() {} : actions;
    }

    @Override public String id() { return "settings"; }
    @Override public String label() { return Lang.tr(TranslationKey.TAB_SETTINGS, "Settings"); }
    @Override public int badge() { return 0; }
    @Override public UComponent body() { return new BodyPanel(); }

    private final class BodyPanel extends UPanel {
        private final UButton streamer = toggle("openfriendplus.privacy.streamer_mode", "Streamer mode", () -> config.streamerMode, v -> config.streamerMode = v);
        private final UButton hideNames = toggle("openfriendplus.privacy.hide_gamertags", "Hide gamertags", () -> config.hideGamertags, v -> config.hideGamertags = v);
        private final UButton joinToasts = toggle("openfriendplus.setting.join_toasts", "Join notifications", () -> config.showJoinToasts, v -> config.showJoinToasts = v);
        private final UButton presenceToasts = toggle("openfriendplus.setting.presence_toasts", "Presence notifications", () -> config.showPresenceToasts, v -> config.showPresenceToasts = v);
        private final UButton requestToasts = toggle("openfriendplus.setting.request_toasts", "Request notifications", () -> config.showRequestToasts, v -> config.showRequestToasts = v);
        private final UButton diagnostics = toggle("openfriendplus.setting.diagnostics_overlay", "Diagnostics overlay", () -> config.diagnosticsOverlay, v -> config.diagnosticsOverlay = v);
        private final UButton randomPort = toggle("openfriendplus.setting.random_port", "Random join port", () -> config.randomizeJoinPort, v -> config.randomizeJoinPort = v);
        private final UButton clickClose = toggle("openfriendplus.setting.click_outside", "Click outside closes", () -> config.clickOutsideToClose, v -> config.clickOutsideToClose = v);
        private final UInput refresh = new UInput().setDigitsOnly(true).setMaxLength(3).setText(String.valueOf(config.refreshIntervalSeconds));
        private final UInput port = new UInput().setDigitsOnly(true).setMaxLength(5).setText(String.valueOf(config.localJoinPort));
        private final UInput language = new UInput().setMaxLength(8).setText(config.language == null ? "auto" : config.language);
        private final UButton save = new UButton(Lang.tr("openfriendplus.action.save", "Save"), this::save).setStyle(UButton.Style.PRIMARY);
        private final UButton reset = new UButton(Lang.tr("openfriendplus.action.defaults", "Defaults"), this::reset).setStyle(UButton.Style.SUBTLE);
        private final UButton openFolder = new UButton(Lang.tr("openfriendplus.action.open_folder", "Open folder"), () -> actions.openDataFolder()).setStyle(UButton.Style.SUBTLE);
        private final UButton clearData = new UButton(Lang.tr("openfriendplus.action.clear_data", "Clear data"), () -> actions.clearLocalAuthAndCache()).setStyle(UButton.Style.DANGER);

        BodyPanel() {
            setBackground(Background.NONE);
            addChild(streamer);
            addChild(hideNames);
            addChild(joinToasts);
            addChild(presenceToasts);
            addChild(requestToasts);
            addChild(diagnostics);
            addChild(randomPort);
            addChild(clickClose);
            addChild(refresh);
            addChild(port);
            addChild(language);
            addChild(save);
            addChild(reset);
            addChild(openFolder);
            addChild(clearData);
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
            requestToasts.setBounds(x + pad, yy, leftW, rowH);
            diagnostics.setBounds(rightX, yy, rightW, rowH);
            yy += rowH + 4;
            randomPort.setBounds(x + pad, yy, leftW, rowH);
            clickClose.setBounds(rightX, yy, rightW, rowH);
            yy += rowH + 18;
            refresh.setBounds(x + pad + 112, yy - 2, 54, rowH);
            port.setBounds(rightX + 66, yy - 2, 62, rowH);
            language.setBounds(x + width - pad - 52, yy - 2, 52, rowH);
            yy += rowH + 10;
            int bw = Math.max(68, (width - pad * 2 - 18) / 4);
            save.setBounds(x + pad, yy, bw, rowH);
            reset.setBounds(x + pad + bw + 6, yy, bw, rowH);
            openFolder.setBounds(x + pad + (bw + 6) * 2, yy, bw, rowH);
            clearData.setBounds(x + pad + (bw + 6) * 3, yy, bw, rowH);
        }

        @Override
        public void render(URenderer r) {
            super.render(r);
            int yy = y + 10 + (22 + 4) * 4 + 14;
            r.drawText(x + 10, yy, Lang.tr("openfriendplus.setting.refresh_interval", "Refresh interval"), UTheme.TEXT_DIM);
            r.drawText(x + Math.max(120, (width - 28) / 2) + 8, yy, Lang.tr("openfriendplus.setting.join_port", "Join port"), UTheme.TEXT_DIM);
            r.drawTextRight(x, yy, width - 66, Lang.tr("openfriendplus.setting.language", "Language"), UTheme.TEXT_DIM);
            r.drawText(x + 10, y + height - 16, Lang.tr(TranslationKey.NOTICE_UNOFFICIAL, "Unofficial fork of OpenFriendMod"), UTheme.TEXT_FAINT);
        }

        private void save() {
            config.refreshIntervalSeconds = parse(refresh.getText(), config.refreshIntervalSeconds);
            config.localJoinPort = parse(port.getText(), config.localJoinPort);
            config.language = language.getText() == null || language.getText().trim().isEmpty() ? "auto" : language.getText().trim();
            config.sanitize();
            Lang.configure(config.language);
            refresh.setText(String.valueOf(config.refreshIntervalSeconds));
            port.setText(String.valueOf(config.localJoinPort));
            language.setText(config.language);
            syncToggleLabels();
            try {
                store.save();
                notice.success("Settings saved", "OpenFriend Plus config updated.");
            } catch (Exception e) {
                notice.error("Could not save settings", e.getMessage());
            }
        }

        private void reset() {
            OpenFriendPlusConfig defaults = new OpenFriendPlusConfig();
            config.copyFrom(defaults);
            refresh.setText(String.valueOf(config.refreshIntervalSeconds));
            port.setText(String.valueOf(config.localJoinPort));
            language.setText(config.language);
            syncToggleLabels();
        }

        private UButton toggle(String key, String fallback, BoolGetter getter, BoolSetter setter) {
            UButton button = new UButton("", null).setStyle(UButton.Style.SUBTLE);
            button.setOnClick(() -> {
                setter.set(!getter.get());
                button.setLabel(toggleLabel(key, fallback, getter.get()));
            });
            button.setLabel(toggleLabel(key, fallback, getter.get()));
            return button;
        }

        private void syncToggleLabels() {
            streamer.setLabel(toggleLabel("openfriendplus.privacy.streamer_mode", "Streamer mode", config.streamerMode));
            hideNames.setLabel(toggleLabel("openfriendplus.privacy.hide_gamertags", "Hide gamertags", config.hideGamertags));
            joinToasts.setLabel(toggleLabel("openfriendplus.setting.join_toasts", "Join notifications", config.showJoinToasts));
            presenceToasts.setLabel(toggleLabel("openfriendplus.setting.presence_toasts", "Presence notifications", config.showPresenceToasts));
            requestToasts.setLabel(toggleLabel("openfriendplus.setting.request_toasts", "Request notifications", config.showRequestToasts));
            diagnostics.setLabel(toggleLabel("openfriendplus.setting.diagnostics_overlay", "Diagnostics overlay", config.diagnosticsOverlay));
            randomPort.setLabel(toggleLabel("openfriendplus.setting.random_port", "Random join port", config.randomizeJoinPort));
            clickClose.setLabel(toggleLabel("openfriendplus.setting.click_outside", "Click outside closes", config.clickOutsideToClose));
        }

        private String toggleLabel(String key, String fallback, boolean value) {
            return Lang.tr(key, fallback) + ": " + (value ? "ON" : "OFF");
        }

        private int parse(String value, int fallback) {
            try { return Integer.parseInt(value); } catch (Exception ignored) { return fallback; }
        }
    }

    private interface BoolGetter { boolean get(); }
    private interface BoolSetter { void set(boolean value); }
}
