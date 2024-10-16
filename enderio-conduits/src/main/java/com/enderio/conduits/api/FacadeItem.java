package com.enderio.conduits.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface FacadeItem {

    Block getTexture(ItemStack stack);
}
