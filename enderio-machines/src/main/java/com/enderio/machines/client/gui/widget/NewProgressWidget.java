package com.enderio.machines.client.gui.widget;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.common.integrations.IntegrationUtility;
import com.enderio.machines.common.lang.MachineLang;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.NotImplementedException;

// TODO: Configurable tooltip text
public class NewProgressWidget extends AbstractWidget {

    public enum FlowDirection {
        BOTTOM_UP, TOP_DOWN, LEFT_RIGHT,
    }

    private final ResourceLocation sprite;
    private final FlowDirection flowDirection;
    private final Supplier<Float> progressSupplier;

    private final boolean showTooltip;

    private NewProgressWidget(int x, int y, int width, int height, ResourceLocation sprite, FlowDirection flowDirection,
            Supplier<Float> progressSupplier, boolean showTooltip) {
        super(x, y, width, height, Component.empty());
        this.flowDirection = flowDirection;
        this.sprite = sprite;
        this.progressSupplier = progressSupplier;
        this.showTooltip = showTooltip;
    }

    public static NewProgressWidget bottomUp(int x, int y, int width, int height, ResourceLocation sprite,
            Supplier<Float> progressSupplier, boolean showTooltip) {
        return new NewProgressWidget(x, y, width, height, sprite, FlowDirection.BOTTOM_UP, progressSupplier,
                showTooltip);
    }

    public static NewProgressWidget topDown(int x, int y, int width, int height, ResourceLocation sprite,
            Supplier<Float> progressSupplier, boolean showTooltip) {
        return new NewProgressWidget(x, y, width, height, sprite, FlowDirection.TOP_DOWN, progressSupplier,
                showTooltip);
    }

    public static NewProgressWidget leftRight(int x, int y, int width, int height, ResourceLocation sprite,
            Supplier<Float> progressSupplier, boolean showTooltip) {
        return new NewProgressWidget(x, y, width, height, sprite, FlowDirection.LEFT_RIGHT, progressSupplier,
                showTooltip);
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

        float progress = progressSupplier.get();

        int u = 0;
        int v = 0;
        int x;
        int y;
        int uWidth;
        int vHeight;
        switch (flowDirection) {
        case BOTTOM_UP -> {
            int yOffset = (int) (this.height * (1.0f - progress));
            x = getX();
            y = getY() + yOffset;
            v = yOffset;
            uWidth = width;
            vHeight = (int) (this.height * progress);
        }
        case TOP_DOWN -> {
            x = getX();
            y = getY();
            uWidth = width;
            vHeight = (int) (this.height * progress);
        }
        case LEFT_RIGHT -> {
            x = getX();
            y = getY();
            uWidth = (int) (this.width * progress);
            vHeight = height;
        }
        default -> throw new NotImplementedException();
        }

        guiGraphics.blitSprite(sprite, width, height, u, v, x, y, uWidth, vHeight);

        // Update the contents of the tooltip whenever its hovered, don't waste any time
        // doing it when not hovered.
        // Should also mean when tooltip is false it never gets populated
        if (this.isHovered() && showTooltip && !IntegrationUtility.hasRecipeViewer()) {
            Minecraft minecraft = Minecraft.getInstance();

            guiGraphics.renderTooltip(minecraft.font,
                    TooltipUtil.withArgs(MachineLang.PROGRESS_TOOLTIP, (int) (progressSupplier.get() * 100)), mouseX,
                    mouseY);
        }
    }
}
