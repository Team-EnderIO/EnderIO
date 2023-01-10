package com.enderio.machines.client.gui.widget;

import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.screen.IEnderScreen;
import com.enderio.core.common.util.Vector2i;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IOConfigWidget<U extends EIOScreen> extends AbstractWidget {
    private final U addedOn;
    private static int renderBoxHeight = 80;

    private final int xTexStart;
    private final int yTexStart;
    private final int textureWidth;
    private final ResourceLocation resLoc;
    private final int textureHeight;

    private boolean isVisible = false;
    private final IOConfigRendererWidget rendererWidget;
    private final Consumer<Boolean> hideInventory;

    // Rebase on ToggleButton if possible
    public IOConfigWidget(U addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation, Rect2i bounds,
        Consumer<Boolean> hideInventory) {
        this(addedOn, x, y, width, height, xTexStart, yTexStart, resourceLocation, 256, 256, bounds, hideInventory);
    }

    public IOConfigWidget(U addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation, int textureWidth,
        int textureHeight, Rect2i bounds, Consumer<Boolean> hideInventory) {
        super(x, y, width, height, Component.empty());
        this.addedOn = addedOn;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.resLoc = resourceLocation;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.hideInventory = hideInventory;
        this.rendererWidget = new IOConfigRendererWidget(addedOn, bounds, () -> isVisible);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        if (isVisible) {
            rendererWidget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Vector2i pos = new Vector2i(x, y);
        addedOn.renderSimpleArea(pPoseStack, pos, pos.add(new Vector2i(width, height)));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resLoc);
        // Coordinates based on whether toggledOn or not
        int xTex = this.xTexStart;
        int yTex = this.yTexStart;

        RenderSystem.enableDepthTest();
        blit(pPoseStack, this.x, this.y, (float) xTex, (float) yTex, this.width, this.height, this.textureWidth, this.textureHeight);

        if (this.isHovered) {
            renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        isVisible = !isVisible;
        hideInventory.accept(isVisible);
    }

    public static Rect2i genRenderBox(int leftPos, int topPos, int imageWidth, int imageHeight) {
        return new Rect2i(leftPos + 5, topPos + imageHeight - renderBoxHeight - 5, imageWidth - 10, renderBoxHeight);
    }

    public class IOConfigRendererWidget<S extends Screen & IEnderScreen> {

        private final S addedOn;
        private final Supplier<Boolean> getter;
        private final Rect2i bounds;

        public IOConfigRendererWidget(S addedon, Rect2i bounds, Supplier<Boolean> getter) {
            this.addedOn = addedon;
            this.getter = getter;
            this.bounds = bounds;
        }

        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (getter.get()) {
                GuiComponent.fill(pPoseStack, bounds.getX(), bounds.getY(), bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), 0xFF000000);
            }
        }
    }

}
