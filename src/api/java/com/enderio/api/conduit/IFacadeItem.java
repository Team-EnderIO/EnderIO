package com.enderio.api.conduit;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface IFacadeItem {

    BlockState getTexture(ItemStack stack);
}
