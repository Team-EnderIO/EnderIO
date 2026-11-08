package com.enderio.enderio.client.foundation.tooltip;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Horizontal energy bar, filling left to right, labelled with the fill percentage (or the stored amount). Sprites are the
 * alloy smelter's energy gauge turned on its side.
 * <p>
 * The bar widens to fit its label: the sprites are drawn as a fixed left cap, a repeating middle and a fixed right cap.
 */
public class ClientEnergyBarTooltip implements ClientTooltipComponent {

    private static final ResourceLocation BACKGROUND_SPRITE = EnderIO.rl("tooltip/energy_bar_background");
    private static final ResourceLocation FILL_SPRITE = EnderIO.rl("tooltip/energy_bar_fill");

    // Source sprite size.
    private static final int SPRITE_WIDTH = 104;
    private static final int BAR_HEIGHT = 16;

    // Where each sprite's middle section starts, how wide its right cap is, and the width of its repeating pattern.
    private static final int BACKGROUND_LEFT_CAP = 2;
    private static final int BACKGROUND_RIGHT_CAP = 2;
    private static final int BACKGROUND_TILE = 16;
    private static final int FILL_LEFT_CAP = 12;
    private static final int FILL_RIGHT_CAP = 8;
    private static final int FILL_TILE = 16;

    private static final int LABEL_PADDING = 4;
    private static final int LABEL_COLOR = 0xFFFFFFFF; // TODO: This is a bad colour choice
    private static final int MARGIN_BOTTOM = 2;

    private final long stored;
    private final long max;
    private final Component label;

    public ClientEnergyBarTooltip(EnergyBarTooltip tooltip) {
        this.stored = tooltip.stored();
        this.max = tooltip.max();
        this.label = tooltip.showAmount()
            ? TooltipUtil.withArgs(EIOCommonLang.ENERGY_AMOUNT, String.format("%,d", stored))
            : Component.literal((int) Math.floor(fraction() * 100) + "%");
    }

    @Override
    public int getHeight() {
        return BAR_HEIGHT + MARGIN_BOTTOM;
    }

    @Override
    public int getWidth(Font font) {
        return barWidth(font);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int width = barWidth(font);

        blitBar(guiGraphics, BACKGROUND_SPRITE, x, y, width, width, BACKGROUND_LEFT_CAP, BACKGROUND_RIGHT_CAP,
            BACKGROUND_TILE);

        int filledWidth = (int) Math.round(fraction() * width);
        if (filledWidth > 0) {
            blitBar(guiGraphics, FILL_SPRITE, x, y, width, filledWidth, FILL_LEFT_CAP, FILL_RIGHT_CAP, FILL_TILE);
        }

        int labelX = x + (width - font.width(label)) / 2;
        int labelY = y + (BAR_HEIGHT - font.lineHeight) / 2 + 1;
        guiGraphics.drawString(font, label, labelX, labelY, LABEL_COLOR, true);
    }

    private double fraction() {
        if (max <= 0) {
            return 0;
        }

        // Clamp in case an item reports more than its capacity (GH-1106).
        return Math.min(1.0, Math.max(0.0, stored / (double) max));
    }

    private int barWidth(Font font) {
        return Math.max(SPRITE_WIDTH, font.width(label) + LABEL_PADDING * 2);
    }

    /**
     * Draws the leftmost {@code visibleWidth} columns of a bar {@code width} wide, repeating the sprite's middle section
     * to fill any extra width.
     */
    private static void blitBar(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int visibleWidth,
        int leftCap, int rightCap, int tile) {
        int rightStart = width - rightCap;
        int column = 0;
        while (column < visibleWidth) {
            int u;
            int runEnd;
            if (column < leftCap) {
                u = column;
                runEnd = leftCap;
            } else if (column >= rightStart) {
                u = SPRITE_WIDTH - (width - column);
                runEnd = width;
            } else {
                int offset = (column - leftCap) % tile;
                u = leftCap + offset;
                runEnd = Math.min(column + tile - offset, rightStart);
            }

            int run = Math.min(runEnd, visibleWidth) - column;
            guiGraphics.blitSprite(sprite, SPRITE_WIDTH, BAR_HEIGHT, u, 0, x + column, y, run, BAR_HEIGHT);
            column += run;
        }
    }
}
