package com.enderio.core.client.item;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

public class ItemBarRenderer {

    public static void extractBar(GuiGraphicsExtractor graphics, float fillRatio, int xOffset, int yOffset, int color) {
        int i = Math.round(13.0F - fillRatio * 13.0F);
        int x = xOffset + 2;
        int y = yOffset + 12;

        graphics.fill(RenderPipelines.GUI, x, y, x + i, y + 1, color);
    }

}
