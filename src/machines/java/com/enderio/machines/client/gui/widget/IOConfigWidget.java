package com.enderio.machines.client.gui.widget;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.common.util.Vector2i;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IOConfigWidget<U extends EIOScreen<?>> extends AbstractWidget {
    private final U addedOn;
    private static final int rendererHeight = 80;

    private final ResourceLocation resLoc = EnderIO.loc("textures/gui/40/widgetsv2.png");

    private boolean isRendererVisible = false;
    private final IOConfigRenderer<?> rendererWidget;
    private final Consumer<Boolean> hideInventory;

    // Rebase on ToggleButton if possible
    public IOConfigWidget(U addedOn, int x, int y, int width, int height, Rect2i bounds, Consumer<Boolean> hideInventory) {
        super(x, y, width, height, Component.empty());
        this.addedOn = addedOn;
        this.hideInventory = hideInventory;
        this.rendererWidget = new IOConfigRenderer<>(addedOn, bounds);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        if (isRendererVisible) {
            rendererWidget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Vector2i pos = new Vector2i(x, y);
        addedOn.renderSimpleArea(pPoseStack, pos, pos.add(new Vector2i(width, height)));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resLoc);
        RenderSystem.enableDepthTest();
        blit(pPoseStack, this.x, this.y, 80, 176, this.width, this.height, 256, 256);

        if (this.isHovered) {
            renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        isRendererVisible = !isRendererVisible;
        hideInventory.accept(isRendererVisible);
    }

    public static Rect2i genRendererBounds(int leftPos, int topPos, int imageWidth, int imageHeight) {
        return new Rect2i(leftPos + 5, topPos + imageHeight - rendererHeight - 5, imageWidth - 10, rendererHeight);
    }

}
