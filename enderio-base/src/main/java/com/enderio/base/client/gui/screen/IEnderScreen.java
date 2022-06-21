package com.enderio.base.client.gui.screen;

import com.enderio.base.client.gui.IIcon;
import com.enderio.core.common.util.Vector2i;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public interface IEnderScreen {

    default Screen getScreen() {
        return (Screen) this;
    }

    static void renderIcon(PoseStack poseStack, Vector2i pos, IIcon icon) {
        if (!icon.shouldRender())
            return;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, icon.getTextureLocation());
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, pos.x(), pos.y(), icon.getRenderSize().x(), icon.getRenderSize().y(), icon.getTexturePosition().x(), icon.getTexturePosition().y(), icon.getIconSize().x(),  icon.getIconSize().y(), icon.getTextureSize().x(), icon.getTextureSize().y());
    }

    default void renderSimpleArea(PoseStack pPoseStack, Vector2i pos, Vector2i pos2) {
        GuiComponent.fill(pPoseStack, pos.x(), pos.y(), pos2.x(), pos2.y(), 0xFF8B8B8B);
        GuiComponent.fill(pPoseStack, pos.x(), pos.y(), pos2.x() - 1, pos2.y() - 1, 0xFF373737);
        GuiComponent.fill(pPoseStack, pos.x() + 1, pos.y() + 1, pos2.x(), pos2.y(), 0xFFFFFFFF);
        GuiComponent.fill(pPoseStack, pos.x() + 1, pos.y() + 1, pos2.x() - 1, pos2.y() - 1, 0xFF8B8B8B);
    }

    default void renderIconBackground(PoseStack pPoseStack, Vector2i pos, IIcon icon) {
        renderSimpleArea(pPoseStack, pos , pos.add(icon.getRenderSize()).expand(2));
    }
     default void renderTooltipAfterEverything(PoseStack pPoseStack, Component pText, int pMouseX, int pMouseY) {
         addTooltip(new LateTooltipData(pPoseStack, pText, pMouseX, pMouseY));
    }

    void addTooltip(LateTooltipData data);

    class LateTooltipData {
        private final PoseStack poseStack;
        private final Component text;
        private final int mouseX;
        private final int mouseY;

        LateTooltipData(PoseStack poseStack, Component text, int mouseX, int mouseY) {
            this.poseStack = poseStack;
            this.text = text;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public PoseStack getPoseStack() {
            return poseStack;
        }

        public Component getText() {
            return text;
        }

        public int getMouseX() {
            return mouseX;
        }

        public int getMouseY() {
            return mouseY;
        }
    }
}
