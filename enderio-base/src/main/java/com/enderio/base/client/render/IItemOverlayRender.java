package com.enderio.base.client.render;

import net.minecraft.world.item.ItemStack;

public interface IItemOverlayRender {
    void renderOverlay(ItemStack pStack, int pXPosition, int pYPosition);
}
