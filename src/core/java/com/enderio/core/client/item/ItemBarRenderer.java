package com.enderio.core.client.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FastColor;

public class ItemBarRenderer {

    public static void renderBar(float fillRatio, int xOffset, int yOffset, float blitOffset, int color) {
//        RenderSystem.disableTexture(); TODO: 1.19.4: FIXME?
        RenderSystem.disableBlend();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        int i = Math.round(13.0F - fillRatio * 13.0F);
        // TODO: See if there's a way for us to underlay this, if not then thats fine.
//        fillRect(bufferbuilder, xOffset + 2, yOffset + 12, blitOffset + 189, 13, 2, 0, 0, 0);
        fillRect(bufferbuilder, xOffset + 2, yOffset + 12, blitOffset + 190, i, 1, FastColor.ARGB32.red(color), FastColor.ARGB32.green(color),
            FastColor.ARGB32.blue(color));
        RenderSystem.enableBlend();
//        RenderSystem.enableTexture(); TODO: 1.19.4: FIXME?
    }

    private static void fillRect(BufferBuilder renderer, int x, int y, float z, int width, int height, int red, int green, int blue) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderer.vertex(x, y, z).color(red, green, blue, 255).endVertex();
        renderer.vertex(x, y + height, z).color(red, green, blue, 255).endVertex();
        renderer.vertex(x + width, y + height, z).color(red, green, blue, 255).endVertex();
        renderer.vertex(x + width, y, z).color(red, green, blue, 255).endVertex();
        BufferUploader.drawWithShader(renderer.end());
    }

}
