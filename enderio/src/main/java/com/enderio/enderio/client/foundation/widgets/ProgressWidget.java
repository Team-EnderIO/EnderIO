package com.enderio.enderio.client.foundation.widgets;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.compat.ModCompatHelper;
import com.enderio.enderio.content.machines.MachinesLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

// TODO: Configurable tooltip text
@Deprecated(forRemoval = true, since = "7.0")
public abstract class ProgressWidget extends AbstractWidget {

    public static class BottomUp extends ProgressWidget {
        public BottomUp(Identifier backgroundImage, Supplier<Float> getter, int x, int y, int width, int height,
                int u, int v) {
            super(backgroundImage, getter, x, y, width, height, u, v);
        }

        public BottomUp(Identifier backgroundImage, Supplier<Float> getter, int x, int y, int width, int height,
                int u, int v, boolean tooltip) {
            super(backgroundImage, getter, x, y, width, height, u, v, tooltip);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            float progress = progressSupplier.get();
            int yOffset = (int) (this.height * (1.0f - progress));
            renderSprite(guiGraphics, getX(), getY() + yOffset, u, v + yOffset, width, (int) (this.height * progress));

            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    public static class TopDown extends ProgressWidget {
        public TopDown(Identifier backgroundImage, Supplier<Float> getter, int x, int y, int width, int height,
                int u, int v, boolean tooltip) {
            super(backgroundImage, getter, x, y, width, height, u, v, tooltip);
        }

        public TopDown(Identifier backgroundImage, Supplier<Float> getter, int x, int y, int width, int height,
                int u, int v) {
            super(backgroundImage, getter, x, y, width, height, u, v);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            float progress = progressSupplier.get();
            renderSprite(guiGraphics, getX(), getY(), u, v, width, (int) (this.height * progress));
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    public static class LeftRight extends ProgressWidget {
        public LeftRight(Identifier backgroundImage, Supplier<Float> getter, int x, int y, int width, int height,
                int u, int v, boolean tooltip) {
            super(backgroundImage, getter, x, y, width, height, u, v, tooltip);
        }

        public LeftRight(Identifier backgroundImage, Supplier<Float> getter, int x, int y, int width, int height,
                int u, int v) {
            super(backgroundImage, getter, x, y, width, height, u, v);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            float progress = progressSupplier.get();
            renderSprite(guiGraphics, getX(), getY(), u, v, (int) (this.width * progress), height);
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    protected final Supplier<Float> progressSupplier;

    private final Identifier backgroundImage;
    protected final int u;
    protected final int v;
    private final boolean showTooltip;

    protected ProgressWidget(Identifier backgroundImage, Supplier<Float> progressSupplier, int x, int y,
            int width, int height, int u, int v, boolean showTooltip) {
        super(x, y, width, height, Component.empty());
        this.backgroundImage = backgroundImage;
        this.progressSupplier = progressSupplier;
        this.u = u;
        this.v = v;
        this.showTooltip = showTooltip;
    }

    protected ProgressWidget(Identifier backgroundImage, Supplier<Float> progressSupplier, int x, int y,
            int width, int height, int u, int v) {
        this(backgroundImage, progressSupplier, x, y, width, height, u, v, true);
    }

    // Stop the click sound
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return false;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Update the contents of the tooltip whenever its hovered, don't waste any time
        // doing it when not hovered.
        // Should also mean when tooltip is false it never gets populated
        if (this.isHovered() && showTooltip && !ModCompatHelper.hasRecipeViewer()) {
            Minecraft minecraft = Minecraft.getInstance();

            guiGraphics.setTooltipForNextFrame(minecraft.font,
                    TooltipUtil.withArgs(MachinesLang.TOOLTIP_PROGRESS, (int) (progressSupplier.get() * 100)), mouseX,
                    mouseY);
        }

    }

    protected void renderSprite(GuiGraphics guiGraphics, int x, int y, int u, int v, int w, int h) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, backgroundImage, x, y, u, v, w, h, 256, 256);
    }
}
