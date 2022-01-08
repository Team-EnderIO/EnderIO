package com.enderio.base.client.renderer.item;

import net.minecraft.world.item.ItemStack;

public interface IItemOverlayRender {
    void renderOverlay(ItemStack pStack, int pXPosition, int pYPosition);
}
