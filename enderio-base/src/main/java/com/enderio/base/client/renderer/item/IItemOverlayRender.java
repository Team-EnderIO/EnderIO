package com.enderio.base.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;

public interface IItemOverlayRender {

     int BLIT_HEIGHT_COUNT = 200;

    /**
     * @param pStack the ItemStack to render
     * @param pXPosition renderPos X
     * @param pYPosition renderPos Y
     * @param poseStack a prefabricated PoseStack moved to x and y as well as the basic blitheight. Add {@code BLIT_HEIGHT_COUNT}
     */
    void renderOverlay(ItemStack pStack, int pXPosition, int pYPosition, PoseStack poseStack);
}
