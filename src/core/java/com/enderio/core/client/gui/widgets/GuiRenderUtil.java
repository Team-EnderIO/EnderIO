package com.enderio.core.client.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;

public class GuiRenderUtil {
    public static void renderSlotArea(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, 0xFF8B8B8B);
        guiGraphics.fill(x, y, x + width - 1, y + height - 1, 0xFF373737);
        guiGraphics.fill(x + 1, y + 1, x + width, y + height, 0xFFFFFFFF);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF8B8B8B);
    }
}
