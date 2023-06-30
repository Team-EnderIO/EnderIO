package com.enderio.core.client.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

public class ItemBarRenderer {

    public static void renderBar(GuiGraphics guiGraphics, float fillRatio, int xOffset, int yOffset, int blitOffset, int color) {
        RenderSystem.disableBlend();

        int i = Math.round(13.0F - fillRatio * 13.0F);
        int x = xOffset + 2;
        int y = yOffset + 12;

        guiGraphics.fill(x, y, x + i, y + 1, blitOffset + 190, color);

        RenderSystem.enableBlend();
    }

}
