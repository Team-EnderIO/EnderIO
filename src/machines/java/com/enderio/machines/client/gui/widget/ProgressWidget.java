package com.enderio.machines.client.gui.widget;

import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.common.lang.MachineLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

// TODO: Configurable tooltip text
public abstract class ProgressWidget extends AbstractWidget {

    public static class BottomUp extends ProgressWidget {
        public BottomUp(EIOScreen<?> screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v) {
            super(screen, getter, x, y, width, height, u, v);
        }

        public BottomUp(EIOScreen<?> screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v, boolean tooltip) {
            super(screen, getter, x, y, width, height, u, v, tooltip);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            float progress = progressSupplier.get();
            int yOffset = (int)(this.height * (1.0f - progress));
            renderSprite(guiGraphics, getX(), getY() + yOffset, u, v + yOffset, width, (int) (this.height * progress));

            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    public static class TopDown extends ProgressWidget {
        public TopDown(EIOScreen<?> screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v, boolean tooltip) {
            super(screen, getter, x, y, width, height, u, v, tooltip);
        }

        public TopDown(EIOScreen<?> screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v) {
            super(screen, getter, x, y, width, height, u, v);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            float progress = progressSupplier.get();
            renderSprite(guiGraphics, getX(), getY(), u, v, width, (int) (this.height * progress));
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    public static class LeftRight extends ProgressWidget {
        public LeftRight(EIOScreen<?> screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v, boolean tooltip) {
            super(screen, getter, x, y, width, height, u, v, tooltip);
        }

        public LeftRight(EIOScreen<?> screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v) {
            super(screen, getter, x, y, width, height, u, v);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            float progress = progressSupplier.get();
            renderSprite(guiGraphics, getX(), getY(), u, v, (int) (this.width * progress), height);
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    protected final Supplier<Float> progressSupplier;

    private final EIOScreen<?> screen;
    protected final int u;
    protected final int v;
    private final boolean showTooltip;

    protected ProgressWidget(EIOScreen<?> screen, Supplier<Float> progressSupplier, int x, int y, int width, int height, int u, int v, boolean showTooltip) {
        super(x, y, width, height, Component.empty());
        this.screen = screen;
        this.progressSupplier = progressSupplier;
        this.u = u;
        this.v = v;
        this.showTooltip = showTooltip;
    }

    protected ProgressWidget(EIOScreen<?> screen, Supplier<Float> progressSupplier, int x, int y, int width, int height, int u, int v) {
        this(screen, progressSupplier, x, y, width, height, u, v, true);
    }

    // Stop the click sound
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return false;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Update the contents of the tooltip whenever its hovered, don't waste any time doing it when not hovered.
        // Should also mean when tooltip is false it never gets populated
        if (this.isHovered() && showTooltip) {
            guiGraphics.renderTooltip(screen.getMinecraft().font, TooltipUtil.withArgs(MachineLang.PROGRESS_TOOLTIP, (int) (progressSupplier.get() * 100)),
                mouseX, mouseY);
        }

    }

    protected void renderSprite(GuiGraphics guiGraphics, int x, int y, int u, int v, int w, int h) {
        guiGraphics.blit(screen.getBackgroundImage(), x, y, u, v, w, h);
    }
}
