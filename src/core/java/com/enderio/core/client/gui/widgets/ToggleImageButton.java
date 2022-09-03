package com.enderio.core.client.gui.widgets;

import com.enderio.core.client.gui.screen.IEnderScreen;
import com.enderio.core.common.util.Vector2i;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToggleImageButton<U extends Screen & IEnderScreen> extends AbstractWidget {

    private final U addedOn;

    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int xDiffTex;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    private boolean isToggledOn = false;
    protected final ToggleImageButton.OnPress onPress;

    public ToggleImageButton(U addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex,
        ResourceLocation resourceLocation, ToggleImageButton.OnPress onPress) {
        this(addedOn, x, y, width, height, xTexStart, yTexStart, xDiffTex, yDiffTex, resourceLocation, 256, 256, onPress);
    }

    public ToggleImageButton(U addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex,
        ResourceLocation resourceLocation, int textureWidth, int textureHeight, ToggleImageButton.OnPress onPress) {
        this(addedOn, x, y, width, height, xTexStart, yTexStart, xDiffTex, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress,
            CommonComponents.EMPTY);
    }

    public ToggleImageButton(U addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex,
        ResourceLocation resourceLocation, int textureWidth, int textureHeight, ToggleImageButton.OnPress onPress, Component message) {
        super(x, y, width, height, message);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.xDiffTex = xDiffTex;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = resourceLocation;
        this.addedOn = addedOn;
        this.onPress = onPress;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float partialTick) {
        Vector2i pos = new Vector2i(x, y);
        addedOn.renderSimpleArea(pPoseStack, pos, pos.add(new Vector2i(width, height)));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        // Coordinates based on whether toggledOn or not
        int xTex = this.xTexStart;
        int yTex = this.yTexStart;

        if (this.isToggledOn) {
            xTex += xDiffTex;
            yTex += yDiffTex;
        }

        RenderSystem.enableDepthTest();
        blit(pPoseStack, this.x, this.y, (float) xTex, (float) yTex, this.width, this.height, this.textureWidth, this.textureHeight);

        if (this.isHovered) {
            renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        isToggledOn = !isToggledOn;
        this.onPress.onPress(isToggledOn);
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}

    @OnlyIn(Dist.CLIENT)
    public interface OnPress {
        void onPress(boolean toggle);
    }

}
