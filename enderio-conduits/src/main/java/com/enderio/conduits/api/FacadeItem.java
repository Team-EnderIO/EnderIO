package com.enderio.conduits.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public interface FacadeItem {

    Block getTexture(ItemStack stack);
}
