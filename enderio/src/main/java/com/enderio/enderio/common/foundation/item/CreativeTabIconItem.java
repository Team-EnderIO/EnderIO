package com.enderio.enderio.common.foundation.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CreativeTabIconItem extends Item {
    public CreativeTabIconItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }
}
