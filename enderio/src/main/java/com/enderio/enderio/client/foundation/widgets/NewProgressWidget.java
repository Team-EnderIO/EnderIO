package com.enderio.enderio.client.foundation.widgets;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.compat.ModCompatHelper;
import com.enderio.enderio.content.machines.MachinesLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Supplier;

// TODO: Configurable tooltip text
public class NewProgressWidget extends AbstractWidget {

    public enum FlowDirection {
        BOTTOM_UP, TOP_DOWN, LEFT_RIGHT,
    }

    private final Identifier sprite;
    private final FlowDirection flowDirection;
    private final Supplier<Float> progressSupplier;

    private final boolean showTooltip;

    private NewProgressWidget(int x, int y, int width, int height, Identifier sprite, FlowDirection flowDirection,
            Supplier<Float> progressSupplier, boolean showTooltip) {
        super(x, y, width, height, Component.empty());
        this.flowDirection = flowDirection;
        this.sprite = sprite;
        this.progressSupplier = progressSupplier;
        this.showTooltip = showTooltip;
    }

    public static NewProgressWidget bottomUp(int x, int y, int width, int height, Identifier sprite,
            Supplier<Float> progressSupplier, boolean showTooltip) {
        return new NewProgressWidget(x, y, width, height, sprite, FlowDirection.BOTTOM_UP, progressSupplier,
                showTooltip);
    }

    public static NewProgressWidget topDown(int x, int y, int width, int height, Identifier sprite,
            Supplier<Float> progressSupplier, boolean showTooltip) {
        return new NewProgressWidget(x, y, width, height, sprite, FlowDirection.TOP_DOWN, progressSupplier,
                showTooltip);
    }

    public static NewProgressWidget leftRight(int x, int y, int width, int height, Identifier sprite,
            Supplier<Float> progressSupplier, boolean showTooltip) {
        return new NewProgressWidget(x, y, width, height, sprite, FlowDirection.LEFT_RIGHT, progressSupplier,
                showTooltip);
    }

    // Stop the click sound
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        return false;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
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

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, width, height, u, v, x, y, uWidth, vHeight);

        // Update the contents of the tooltip whenever its hovered, don't waste any time
        // doing it when not hovered.
        // Should also mean when tooltip is false it never gets populated
        if (this.isHovered() && showTooltip && !ModCompatHelper.hasRecipeViewer()) {
            Minecraft minecraft = Minecraft.getInstance();

            graphics.setTooltipForNextFrame(minecraft.font,
                    TooltipUtil.withArgs(MachinesLang.TOOLTIP_PROGRESS, (int) (progressSupplier.get() * 100)), mouseX,
                    mouseY);
        }
    }
}
