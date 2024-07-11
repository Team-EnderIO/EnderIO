package com.enderio.base.api.filter;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface ItemStackFilter extends ResourceFilter, Predicate<ItemStack> {
}
