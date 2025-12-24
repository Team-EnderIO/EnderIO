package com.enderio.core.client.item;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

public class ItemBarRenderer {

    public static void renderBar(GuiGraphics guiGraphics, float fillRatio, int xOffset, int yOffset, int color) {
        // TODO: 1.21.8: Check this still works.
//        RenderSystem.disableBlend();

        int i = Math.round(13.0F - fillRatio * 13.0F);
        int x = xOffset + 2;
        int y = yOffset + 12;

        guiGraphics.fill(RenderPipelines.GUI, x, y, x + i, y + 1, color);

        // TODO: 1.21.8. GuiGraphics does this for damage bars. Do we copy?
        //int i = x + 2;
        //int j = y + 13;
        //this.fill(RenderPipelines.GUI, i, j, i + 13, j + 2, -16777216);
        //this.fill(RenderPipelines.GUI, i, j, i + stack.getBarWidth(), j + 1, ARGB.opaque(stack.getBarColor()));

//        RenderSystem.enableBlend();
    }

}
