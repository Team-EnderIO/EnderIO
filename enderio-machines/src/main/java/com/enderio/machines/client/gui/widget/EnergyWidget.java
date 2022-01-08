package com.enderio.machines.client.gui.widget;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.TooltipUtil;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.EIOMachines;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class EnergyWidget extends AbstractWidget {

    // TODO: Will need some way of displaying no tooltip and instead asking for a capacitor on non-simple machines.

    private static final ResourceLocation WIDGETS = EIOMachines.loc("textures/gui/widgets.png");

    private final Screen displayOn;
    private final Supplier<Vector2i> getEnergy;

    public EnergyWidget(Screen displayOn, Supplier<Vector2i> getEnergy, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight, TextComponent.EMPTY);
        this.displayOn = displayOn;
        this.getEnergy = getEnergy;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    // Stop the click sound
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return false;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, WIDGETS);

        float filledVolume = getEnergy.get().getX() / (float) getEnergy.get().getY();
        int renderableHeight = (int)(filledVolume * height);

        pPoseStack.pushPose();
        pPoseStack.translate(0, height-16, 0);
        for (int i = 0; i < Math.ceil(renderableHeight / 16f); i++) {
            int drawingHeight = Math.min(16, renderableHeight - 16*i);
            int notDrawingHeight = 16 - drawingHeight;
            blit(pPoseStack, x, y + notDrawingHeight, displayOn.getBlitOffset(), 0, 128 + notDrawingHeight, width, drawingHeight, 256, 256);
            pPoseStack.translate(0,-16, 0);
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        pPoseStack.popPose();

        renderToolTip(pPoseStack, pMouseX, pMouseY);
    }
    @Override
    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (isActive() && isHovered) {
            displayOn.renderTooltip(pPoseStack, TooltipUtil.withArgs(EIOLang.ENERGY_AMOUNT, getEnergy.get().getX() + "/" + getEnergy.get().getY()), pMouseX, pMouseY);
        }
    }
}

