/*
 * OpenFriend Plus modifications — Copyright (c) 2026 gnustella-lab.
 * Licensed under the MIT License.
 */
package dev.gnustella.openfriendplus.common.screen;

import dev.gnustella.openfriendplus.common.diagnostics.DiagnosticsState;
import dev.gnustella.openfriendplus.common.notice.NoticeSink;
import dev.gnustella.openfriendplus.common.ui.UButton;
import dev.gnustella.openfriendplus.common.ui.UComponent;
import dev.gnustella.openfriendplus.common.ui.UPanel;
import dev.gnustella.openfriendplus.common.ui.URenderer;
import dev.gnustella.openfriendplus.common.ui.UScrollPane;
import dev.gnustella.openfriendplus.common.ui.UTheme;

public final class DiagnosticsTab implements FriendsOverlayScreen.Tab {
    private final DiagnosticsState diagnostics;
    private final Runnable refresh;
    private final Runnable restartCore;
    private final Runnable stopCore;
    private final NoticeSink notice;

    public DiagnosticsTab(DiagnosticsState diagnostics, Runnable refresh, Runnable restartCore, Runnable stopCore, NoticeSink notice) {
        this.diagnostics = diagnostics;
        this.refresh = refresh;
        this.restartCore = restartCore;
        this.stopCore = stopCore;
        this.notice = notice;
    }

    @Override public String id() { return "diagnostics"; }
    @Override public String label() { return "Diagnostics"; }
    @Override public int badge() { return 0; }
    @Override public UComponent body() { return new BodyPanel(); }

    private final class BodyPanel extends UPanel {
        private final UScrollPane scroll = new UScrollPane();
        private final LinesPanel lines = new LinesPanel();
        private final UButton refreshBtn = new UButton("Refresh", () -> { if (refresh != null) refresh.run(); }).setStyle(UButton.Style.SUBTLE);
        private final UButton copyBtn = new UButton("Copy", () -> notice.success("Diagnostics ready", diagnostics.copyText())).setStyle(UButton.Style.SUBTLE);
        private final UButton restartBtn = new UButton("Restart Core", () -> { if (restartCore != null) restartCore.run(); }).setStyle(UButton.Style.SUBTLE);
        private final UButton stopBtn = new UButton("Stop Core", () -> { if (stopCore != null) stopCore.run(); }).setStyle(UButton.Style.DANGER);

        BodyPanel() {
            setBackground(Background.NONE);
            scroll.setContent(lines);
            addChild(scroll);
            addChild(refreshBtn);
            addChild(copyBtn);
            addChild(restartBtn);
            addChild(stopBtn);
        }

        @Override
        protected void onLayout() {
            int pad = 8;
            int btnH = 20;
            scroll.setBounds(x, y, width, Math.max(1, height - btnH - pad * 2));
            lines.setBounds(x + pad, y + pad, Math.max(1, width - pad * 2), lines.preferredHeight());
            int by = y + height - btnH - pad;
            refreshBtn.setBounds(x + pad, by, 66, btnH);
            copyBtn.setBounds(x + pad + 72, by, 54, btnH);
            restartBtn.setBounds(x + pad + 132, by, 90, btnH);
            stopBtn.setBounds(x + pad + 228, by, 72, btnH);
        }
    }

    private final class LinesPanel extends UPanel {
        int preferredHeight() { return diagnostics.lines().length * 14 + 8; }

        @Override
        public void render(URenderer r) {
            String[] lines = diagnostics.lines();
            int yy = y + 2;
            for (String line : lines) {
                r.drawText(x + 2, yy, line, UTheme.TEXT);
                yy += 14;
            }
        }
    }
}
