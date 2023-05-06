package com.enderio.machines.client.gui.widget;

import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.common.lang.MachineLang;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

// TODO: Configurable tooltip text
public abstract class ProgressWidget extends AbstractWidget {

    public static class BottomUp extends ProgressWidget {
        public BottomUp(EIOScreen screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v) {
            super(screen, getter, x, y, width, height, u, v);
        }

        public BottomUp(EIOScreen screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v, boolean tooltip) {
            super(screen, getter, x, y, width, height, u, v, tooltip);
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            float progress = progressSupplier.get();
            int yOffset = (int)(this.height * (1.0f - progress));
            render(poseStack, x, y + yOffset, u, v + yOffset, width, (int) (this.height * progress));

            super.renderButton(poseStack, mouseX, mouseY, partialTick);
        }
    }

    public static class TopDown extends ProgressWidget {
        public TopDown(EIOScreen screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v, boolean tooltip) {
            super(screen, getter, x, y, width, height, u, v, tooltip);
        }

        public TopDown(EIOScreen screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v) {
            super(screen, getter, x, y, width, height, u, v);
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            float progress = progressSupplier.get();
            render(poseStack, x, y, u, v, width, (int) (this.height * progress));
            super.renderButton(poseStack, mouseX, mouseY, partialTick);
        }
    }

    public static class LeftRight extends ProgressWidget {
        public LeftRight(EIOScreen screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v, boolean tooltip) {
            super(screen, getter, x, y, width, height, u, v, tooltip);
        }

        public LeftRight(EIOScreen screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v) {
            super(screen, getter, x, y, width, height, u, v);
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            float progress = progressSupplier.get();
            render(poseStack, x, y, u, v, (int) (this.width * progress), height);
            super.renderButton(poseStack, mouseX, mouseY, partialTick);
        }
    }

    protected final Supplier<Float> progressSupplier;

    private final EIOScreen screen;
    protected final int u;
    protected final int v;
    private final boolean tooltip;

    protected ProgressWidget(EIOScreen screen, Supplier<Float> progressSupplier, int x, int y, int width, int height, int u, int v, boolean tooltip) {
        super(x, y, width, height, Component.empty());
        this.screen = screen;
        this.progressSupplier = progressSupplier;
        this.u = u;
        this.v = v;
        this.tooltip = tooltip;
    }

    protected ProgressWidget(EIOScreen screen, Supplier<Float> progressSupplier, int x, int y, int width, int height, int u, int v) {
        this(screen, progressSupplier, x, y, width, height, u, v, true);
    }

    // Stop the click sound
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return false;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (this.isHoveredOrFocused() && tooltip) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    protected void render(PoseStack poseStack, int x, int y, int u, int v, int w, int h) {
        poseStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, screen.getBackgroundImage());
        blit(poseStack, x, y, u, v, w, h);
        poseStack.popPose();
    }

    @Override
    public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
        if (isHovered && isActive()) {
            screen.renderTooltip(poseStack, TooltipUtil.withArgs(MachineLang.PROGRESS_TOOLTIP, (int) (progressSupplier.get() * 100)), mouseX, mouseY);
        }
    }
}
