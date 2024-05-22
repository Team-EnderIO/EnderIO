package com.enderio.api.filter;

import net.minecraft.world.item.ItemStack;

public interface ItemStackFilter extends ResourceFilter {
    boolean test(ItemStack stack);
}
