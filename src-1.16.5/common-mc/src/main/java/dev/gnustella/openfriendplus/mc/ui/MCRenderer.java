/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 * Variant for group-b (Minecraft 1.19 - 1.19.4): uses PoseStack + GuiComponent.fill
 * + Font.draw instead of GuiGraphics. Scissor coordinates are converted from
 * GUI-scaled coordinates to framebuffer coordinates before RenderSystem calls.
 */
package dev.gnustella.openfriendplus.mc.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.gnustella.openfriendplus.common.ui.URenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayDeque;
import java.util.Deque;

public final class MCRenderer implements URenderer {

    private final Font font;
    private PoseStack poseStack;
    private int mouseX;
    private int mouseY;
    private float partialTick;
    private final Deque<int[]> clipStack = new ArrayDeque<>();
    private final Deque<int[]> translateStack = new ArrayDeque<>();
    private int translateX;
    private int translateY;

    public MCRenderer(Font font) {
        this.font = font;
    }

    public void beginFrame(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.poseStack = poseStack;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTick = partialTick;
        this.clipStack.clear();
        this.translateStack.clear();
        this.translateX = 0;
        this.translateY = 0;
    }

    public void endFrame() {
        if (!clipStack.isEmpty()) {
            RenderSystem.disableScissor();
            clipStack.clear();
        }
        while (poseStack != null && !translateStack.isEmpty()) {
            poseStack.popPose();
            translateStack.pop();
        }
        this.poseStack = null;
    }

    @Override
    public void fillRect(int x, int y, int width, int height, int argb) {
        if (poseStack == null) return;
        GuiComponent.fill(poseStack, x, y, x + width, y + height, argb);
    }

    @Override
    public void strokeRect(int x, int y, int width, int height, int argb) {
        if (poseStack == null) return;
        GuiComponent.fill(poseStack, x,             y,              x + width,     y + 1,         argb);
        GuiComponent.fill(poseStack, x,             y + height - 1, x + width,     y + height,    argb);
        GuiComponent.fill(poseStack, x,             y,              x + 1,         y + height,    argb);
        GuiComponent.fill(poseStack, x + width - 1, y,              x + width,     y + height,    argb);
    }

    @Override
    public void fillRoundedRect(int x, int y, int width, int height, int radius, int argb) {
        fillRect(x, y, width, height, argb);
    }

    @Override
    public void drawText(int x, int y, String text, int argb) {
        if (poseStack == null || text == null) return;
        font.draw(poseStack, new TextComponent(text), (float) x, (float) y, argb);
    }

    @Override
    public void drawTextCentered(int x, int y, int width, String text, int argb) {
        if (poseStack == null || text == null) return;
        int tx = x + (width - font.width(text)) / 2;
        font.draw(poseStack, new TextComponent(text), (float) tx, (float) y, argb);
    }

    @Override
    public void drawTextRight(int x, int y, int width, String text, int argb) {
        if (poseStack == null || text == null) return;
        int tx = x + width - font.width(text);
        font.draw(poseStack, new TextComponent(text), (float) tx, (float) y, argb);
    }

    @Override
    public void drawTextClipped(int x, int y, int maxWidth, String text, int argb) {
        if (poseStack == null || text == null) return;
        String shown = text;
        if (font.width(shown) > maxWidth) {
            String ell = "...";
            int ellW = font.width(ell);
            int budget = Math.max(0, maxWidth - ellW);
            StringBuilder b = new StringBuilder();
            int acc = 0;
            for (int i = 0; i < text.length(); i++) {
                int cw = font.width(String.valueOf(text.charAt(i)));
                if (acc + cw > budget) break;
                b.append(text.charAt(i));
                acc += cw;
            }
            shown = b + ell;
        }
        font.draw(poseStack, new TextComponent(shown), (float) x, (float) y, argb);
    }

    @Override
    public String translate(String key) {
        if (key == null) return "";
        return I18n.get(key);
    }

    @Override
    public int textWidth(String text) {
        return text == null ? 0 : font.width(text);
    }

    @Override
    public int textHeight() {
        return font.lineHeight;
    }

    @Override
    public void pushClip(int x, int y, int width, int height) {
        if (poseStack == null) return;
        int x0 = x + translateX;
        int y0 = y + translateY;
        int x1 = x0 + Math.max(0, width);
        int y1 = y0 + Math.max(0, height);
        if (!clipStack.isEmpty()) {
            int[] parent = clipStack.peek();
            x0 = Math.max(x0, parent[0]);
            y0 = Math.max(y0, parent[1]);
            x1 = Math.min(x1, parent[2]);
            y1 = Math.min(y1, parent[3]);
        }
        if (x1 < x0) x1 = x0;
        if (y1 < y0) y1 = y0;
        int[] clip = new int[] { x0, y0, x1, y1 };
        clipStack.push(clip);
        applyClip(clip);
    }

    @Override
    public void popClip() {
        if (clipStack.isEmpty()) return;
        clipStack.pop();
        RenderSystem.disableScissor();
        if (!clipStack.isEmpty()) applyClip(clipStack.peek());
    }

    private void applyClip(int[] clip) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        double scale = mc.getWindow().getGuiScale();
        int sx = (int) Math.floor(clip[0] * scale);
        int sy = (int) Math.floor(mc.getWindow().getHeight() - clip[3] * scale);
        int sw = (int) Math.ceil((clip[2] - clip[0]) * scale);
        int sh = (int) Math.ceil((clip[3] - clip[1]) * scale);
        RenderSystem.enableScissor(sx, sy, Math.max(0, sw), Math.max(0, sh));
    }

    @Override
    public void pushTranslate(int dx, int dy) {
        if (poseStack == null) return;
        poseStack.pushPose();
        poseStack.translate((double) dx, (double) dy, 0d);
        translateStack.push(new int[] { translateX, translateY });
        translateX += dx;
        translateY += dy;
    }

    @Override
    public void popTranslate() {
        if (poseStack == null) return;
        poseStack.popPose();
        if (translateStack.isEmpty()) {
            translateX = 0;
            translateY = 0;
        } else {
            int[] previous = translateStack.pop();
            translateX = previous[0];
            translateY = previous[1];
        }
    }

    @Override
    public void drawHead(int x, int y, int size, String profileId) {
        int seed = profileId == null ? 0 : profileId.hashCode();
        int tone = 0xFF000000 | (((seed >> 16) & 0x7F) + 0x40) << 16
                              | (((seed >> 8)  & 0x7F) + 0x40) << 8
                              | ( (seed        & 0x7F) + 0x40);
        fillRect(x, y, size, size, tone);
        strokeRect(x, y, size, size, 0xFF1A1A1A);
    }

    @Override
    public int currentMouseX() { return mouseX; }

    @Override
    public int currentMouseY() { return mouseY; }

    @Override
    public float partialTick() { return partialTick; }
}
