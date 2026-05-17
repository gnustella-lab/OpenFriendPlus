/*
 * OpenFriend — Copyright (c) 2026 ZSHARE. Licensed under the MIT License.
 */
package jp.zpw.openfriend.mc.ui;

//#if MC>=26000
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import jp.zpw.openfriend.common.ui.UTheme;
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.resources.ResourceLocation;
//#elseif MC>=12111
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.network.chat.Component;
//#elseif MC>=12109
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.network.chat.Component;
//#elseif MC>=12106
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.client.renderer.RenderPipelines;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.resources.ResourceLocation;
//#elseif MC>=12102
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.client.renderer.RenderType;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.resources.ResourceLocation;
//#elseif MC>=12100
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.resources.ResourceLocation;
//#elseif MC>=12000
import com.mojang.blaze3d.systems.RenderSystem;
import jp.zpw.openfriend.common.ui.UTheme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
//#elseif MC>=11904
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.gui.GuiComponent;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.client.renderer.GameRenderer;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.resources.ResourceLocation;
//#elseif MC>=11903
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.gui.GuiComponent;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.client.renderer.GameRenderer;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.resources.ResourceLocation;
//#elseif MC>=11900
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.gui.GuiComponent;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.client.renderer.GameRenderer;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.resources.ResourceLocation;
//#elseif MC>=11800
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.gui.GuiComponent;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.client.renderer.GameRenderer;
//$$ import net.minecraft.network.chat.TextComponent;
//$$ import net.minecraft.resources.ResourceLocation;
//#elseif MC>=11700
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.gui.GuiComponent;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.client.renderer.GameRenderer;
//$$ import net.minecraft.network.chat.TextComponent;
//$$ import net.minecraft.resources.ResourceLocation;
//#else
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.client.gui.GuiComponent;
//$$ import net.minecraft.client.gui.components.Button;
//$$ import net.minecraft.network.chat.TextComponent;
//$$ import net.minecraft.resources.ResourceLocation;
//#endif

public final class OpenFriendIconButton extends Button {

    //#if MC>=26000
    //$$ private static final ResourceLocation TEX =
    //$$         new ResourceLocation("openfriend", "textures/gui/openfriend_icon.png");
    //#elseif MC>=12111
    //$$
    //#elseif MC>=12109
    //$$
    //#elseif MC>=12106
    //$$ private static final ResourceLocation TEX =
    //$$         ResourceLocation.fromNamespaceAndPath("openfriend", "textures/gui/openfriend_icon.png");
    //#elseif MC>=12102
    //$$ private static final ResourceLocation TEX =
    //$$         ResourceLocation.fromNamespaceAndPath("openfriend", "textures/gui/openfriend_icon.png");
    //#elseif MC>=12100
    //$$ private static final ResourceLocation TEX =
    //$$         ResourceLocation.fromNamespaceAndPath("openfriend", "textures/gui/openfriend_icon.png");
    //#elseif MC>=12000
    private static final ResourceLocation TEX =
            new ResourceLocation("openfriend", "textures/gui/openfriend_icon.png");
    //#else
    //$$ private static final ResourceLocation TEX =
    //$$         new ResourceLocation("openfriend", "textures/gui/openfriend_icon.png");
    //#endif

    //#if MC>=26000
    //$$ public OpenFriendIconButton(int x, int y, int w, int h, OnPress onPress) {
    //$$     super(x, y, w, h, Component.literal("Friends"), onPress, DEFAULT_NARRATION);
    //$$ }
    //#elseif MC>=12111
    //$$ public OpenFriendIconButton(int x, int y, int w, int h, OnPress onPress) {
    //$$     super(x, y, w, h, Component.literal("F"), onPress, DEFAULT_NARRATION);
    //$$ }
    //#elseif MC>=12109
    //$$ public OpenFriendIconButton(int x, int y, int w, int h, OnPress onPress) {
    //$$     super(x, y, w, h, Component.literal("F"), onPress, DEFAULT_NARRATION);
    //$$ }
    //#elseif MC>=11903
    //$$ public OpenFriendIconButton(int x, int y, int w, int h, OnPress onPress) {
    //$$     super(x, y, w, h, Component.literal("Friends"), onPress, DEFAULT_NARRATION);
    //$$ }
    //#elseif MC>=11900
    //$$ public OpenFriendIconButton(int x, int y, int w, int h, OnPress onPress) {
    //$$     super(x, y, w, h, Component.literal("Friends"), onPress);
    //$$ }
    //#elseif MC>=11700
    //$$ public OpenFriendIconButton(int x, int y, int w, int h, OnPress onPress) {
    //$$     super(x, y, w, h, new TextComponent("Friends"), onPress);
    //$$ }
    //#elseif MC>=12000
    public OpenFriendIconButton(int x, int y, int w, int h, OnPress onPress) {
        super(x, y, w, h, Component.literal("Friends"), onPress, DEFAULT_NARRATION);
    }
    //#else
    //$$ public OpenFriendIconButton(int x, int y, int w, int h, OnPress onPress) {
    //$$     super(x, y, w, h, new TextComponent("Friends"), onPress);
    //$$ }
    //#endif

    //#if MC>=26000
    //$$ @Override
    //$$ protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
    //$$     if (this.isHoveredOrFocused()) {
    //$$         g.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, UTheme.HOVER_GHOST);
    //$$     }
    //$$     RenderSystem.enableBlend();
    //$$     float a = this.active ? 1.0f : 0.5f;
    //$$     g.setColor(1.0f, 1.0f, 1.0f, a);
    //$$     g.blit(TEX, getX(), getY(), width, height, 0.0f, 0.0f, 512, 512, 512, 512);
    //$$     g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    //$$     RenderSystem.disableBlend();
    //$$ }
    //#elseif MC>=12111
    //$$ @Override
    //$$ protected void renderContents(GuiGraphics g, int mouseX, int mouseY, float partial) {
    //$$     net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
    //$$     if (mc == null) return;
    //$$     int tw = mc.font.width("F");
    //$$     int tx = getX() + (width - tw) / 2;
    //$$     int ty = getY() + (height - mc.font.lineHeight) / 2;
    //$$     g.drawString(mc.font, "F", tx, ty, 0xFFFFFFFF, false);
    //$$ }
    //#elseif MC>=12109
    //$$ @Override
    //$$ protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
    //$$     int bg = this.isHoveredOrFocused() ? 0xFFFFCC2E : 0xFFE5B520;
    //$$     g.fill(getX(), getY(), getX() + width, getY() + height, bg);
    //$$     g.fill(getX(), getY(), getX() + width, getY() + 1, 0xFF7A5A00);
    //$$     g.fill(getX(), getY() + height - 1, getX() + width, getY() + height, 0xFF7A5A00);
    //$$     g.fill(getX(), getY(), getX() + 1, getY() + height, 0xFF7A5A00);
    //$$     g.fill(getX() + width - 1, getY(), getX() + width, getY() + height, 0xFF7A5A00);
    //$$     net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
    //$$     if (mc != null) {
    //$$         int tw = mc.font.width("F");
    //$$         int tx = getX() + (width - tw) / 2;
    //$$         int ty = getY() + (height - mc.font.lineHeight) / 2;
    //$$         g.drawString(mc.font, "F", tx, ty, 0xFF000000, false);
    //$$     }
    //$$ }
    //#elseif MC>=12106
    //$$ @Override
    //$$ protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
    //$$     if (this.isHoveredOrFocused()) {
    //$$         g.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, 0x40FFFFFF);
    //$$     }
    //$$     int color = this.active ? 0xFFFFFFFF : 0x80FFFFFF;
    //$$     g.blit(RenderPipelines.GUI_TEXTURED, TEX, getX(), getY(), 0.0f, 0.0f, width, height, 512, 512, 512, 512, color);
    //$$ }
    //#elseif MC>=12102
    //$$ @Override
    //$$ protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
    //$$     if (this.isHoveredOrFocused()) {
    //$$         g.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, 0x40FFFFFF);
    //$$     }
    //$$     int color = this.active ? 0xFFFFFFFF : 0x80FFFFFF;
    //$$     g.blit(RenderType::guiTextured, TEX, getX(), getY(), 0.0f, 0.0f, width, height, 512, 512, 512, 512, color);
    //$$ }
    //#elseif MC>=12100
    //$$ @Override
    //$$ protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
    //$$     if (this.isHoveredOrFocused()) {
    //$$         g.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, 0x40FFFFFF);
    //$$     }
    //$$     RenderSystem.enableBlend();
    //$$     float a = this.active ? 1.0f : 0.5f;
    //$$     g.setColor(1.0f, 1.0f, 1.0f, a);
    //$$     g.blit(TEX, getX(), getY(), width, height, 0.0f, 0.0f, 512, 512, 512, 512);
    //$$     g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    //$$     RenderSystem.disableBlend();
    //$$ }
    //#elseif MC>=12001
    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
        if (this.isHoveredOrFocused()) {
            g.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, UTheme.HOVER_GHOST);
        }
        RenderSystem.enableBlend();
        float a = this.active ? 1.0f : 0.5f;
        g.setColor(1.0f, 1.0f, 1.0f, a);
        g.blit(TEX, getX(), getY(), width, height, 0.0f, 0.0f, 512, 512, 512, 512);
        g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
    //#elseif MC>=12000
    //$$ @Override
    //$$ public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
    //$$     if (this.isHoveredOrFocused()) {
    //$$         g.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, UTheme.HOVER_GHOST);
    //$$     }
    //$$     RenderSystem.enableBlend();
    //$$     float a = this.active ? 1.0f : 0.5f;
    //$$     g.setColor(1.0f, 1.0f, 1.0f, a);
    //$$     g.blit(TEX, getX(), getY(), width, height, 0.0f, 0.0f, 512, 512, 512, 512);
    //$$     g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    //$$     RenderSystem.disableBlend();
    //$$ }
    //#elseif MC>=11904
    //$$ @Override
    //$$ public void renderWidget(PoseStack pose, int mouseX, int mouseY, float partial) {
    //$$     int x = this.getX();
    //$$     int y = this.getY();
    //$$     if (this.isHoveredOrFocused()) {
    //$$         GuiComponent.fill(pose, x - 1, y - 1, x + this.width + 1, y + this.height + 1, 0x40FFFFFF);
    //$$     }
    //$$     RenderSystem.setShader(GameRenderer::getPositionTexShader);
    //$$     RenderSystem.setShaderTexture(0, TEX);
    //$$     float a = this.active ? 1.0f : 0.5f;
    //$$     RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, a);
    //$$     RenderSystem.enableBlend();
    //$$     GuiComponent.blit(pose, x, y, 0.0f, 0.0f, this.width, this.height, this.width, this.height);
    //$$     RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    //$$     RenderSystem.disableBlend();
    //$$ }
    //#elseif MC>=11903
    //$$ @Override
    //$$ public void renderButton(PoseStack pose, int mouseX, int mouseY, float partial) {
    //$$     int x = this.getX();
    //$$     int y = this.getY();
    //$$     if (this.isHoveredOrFocused()) {
    //$$         GuiComponent.fill(pose, x - 1, y - 1, x + this.width + 1, y + this.height + 1, 0x40FFFFFF);
    //$$     }
    //$$     RenderSystem.setShader(GameRenderer::getPositionTexShader);
    //$$     RenderSystem.setShaderTexture(0, TEX);
    //$$     float a = this.active ? 1.0f : 0.5f;
    //$$     RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, a);
    //$$     RenderSystem.enableBlend();
    //$$     GuiComponent.blit(pose, x, y, 0.0f, 0.0f, this.width, this.height, this.width, this.height);
    //$$     RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    //$$     RenderSystem.disableBlend();
    //$$ }
    //#elseif MC>=11800
    //$$ @Override
    //$$ public void renderButton(PoseStack pose, int mouseX, int mouseY, float partial) {
    //$$     if (this.isHoveredOrFocused()) {
    //$$         GuiComponent.fill(pose, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 0x40FFFFFF);
    //$$     }
    //$$     RenderSystem.setShader(GameRenderer::getPositionTexShader);
    //$$     RenderSystem.setShaderTexture(0, TEX);
    //$$     float a = this.active ? 1.0f : 0.5f;
    //$$     RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, a);
    //$$     RenderSystem.enableBlend();
    //$$     GuiComponent.blit(pose, this.x, this.y, 0.0f, 0.0f, this.width, this.height, this.width, this.height);
    //$$     RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    //$$     RenderSystem.disableBlend();
    //$$ }
    //#elseif MC>=11700
    //$$ @Override
    //$$ public void renderButton(PoseStack pose, int mouseX, int mouseY, float partial) {
    //$$     if (this.isHovered() || this.isFocused()) {
    //$$         GuiComponent.fill(pose, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 0x40FFFFFF);
    //$$     }
    //$$     RenderSystem.setShader(GameRenderer::getPositionTexShader);
    //$$     RenderSystem.setShaderTexture(0, TEX);
    //$$     float a = this.active ? 1.0f : 0.5f;
    //$$     RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, a);
    //$$     RenderSystem.enableBlend();
    //$$     GuiComponent.blit(pose, this.x, this.y, 0.0f, 0.0f, this.width, this.height, this.width, this.height);
    //$$     RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    //$$     RenderSystem.disableBlend();
    //$$ }
    //#else
    //$$ @Override
    //$$ public void renderButton(PoseStack pose, int mouseX, int mouseY, float partial) {
    //$$     if (this.isHovered || this.isFocused()) {
    //$$         GuiComponent.fill(pose, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 0x40FFFFFF);
    //$$     }
    //$$     Minecraft.getInstance().getTextureManager().bind(TEX);
    //$$     float a = this.active ? 1.0f : 0.5f;
    //$$     RenderSystem.color4f(1.0f, 1.0f, 1.0f, a);
    //$$     RenderSystem.enableBlend();
    //$$     GuiComponent.blit(pose, this.x, this.y, 0.0f, 0.0f, this.width, this.height, this.width, this.height);
    //$$     RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    //$$     RenderSystem.disableBlend();
    //$$ }
    //#endif
}
