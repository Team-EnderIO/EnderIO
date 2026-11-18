package com.enderio.core.client.item;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

public class ItemBarRenderer {

    /**
     * The row (relative to the item's top) that vanilla draws the durability bar on.
     */
    public static final int DURABILITY_BAR_ROW = 13;

    public static void renderBar(GuiGraphics guiGraphics, float fillRatio, int xOffset, int yOffset, int blitOffset,
            int color) {
        RenderSystem.disableBlend();

        int i = Math.round(13.0F - fillRatio * 13.0F);
        int x = xOffset + 2;
        int y = yOffset + 12;

        guiGraphics.fill(x, y, x + i, y + 1, blitOffset + 190, color);

        RenderSystem.enableBlend();
    }

    /**
     * Renders a bar in the same style as vanilla's durability bar: a 13x2 black background with a 1 pixel high coloured bar on top.
     * @param filledRatio the portion of the bar that is filled, from 0 to 1.
     * @param row the row relative to the item's top to draw the bar on. Use {@link #DURABILITY_BAR_ROW} for vanilla's position.
     */
    public static void renderBarWithBackground(GuiGraphics guiGraphics, float filledRatio, int xOffset, int yOffset, int row, int color) {
        RenderSystem.disableBlend();

        int width = Math.round(Math.clamp(filledRatio, 0.0F, 1.0F) * 13.0F);
        int x = xOffset + 2;
        int y = yOffset + row;

        guiGraphics.fill(x, y, x + 13, y + 2, 190, 0xFF000000);
        guiGraphics.fill(x, y, x + width, y + 1, 190, color | 0xFF000000);

        RenderSystem.enableBlend();
    }

}
