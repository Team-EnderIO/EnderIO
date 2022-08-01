package com.enderio.base.common.item.misc;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EnderfaceItem extends Item {
    public EnderfaceItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }
}
