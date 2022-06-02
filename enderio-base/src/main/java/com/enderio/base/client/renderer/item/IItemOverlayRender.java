package com.enderio.base.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;

public interface IItemOverlayRender {
    /**
     *
     * @param pStack the ItemStack to render
     * @param pXPosition renderPos X
     * @param pYPosition renderPos Y
     * @param poseStack a prefabricated PoseStack moved to x and y as well as the basic blitheight. add 201 to render above count and 199 (and below) for anything below
     */
    void renderOverlay(ItemStack pStack, int pXPosition, int pYPosition, PoseStack poseStack);
}
