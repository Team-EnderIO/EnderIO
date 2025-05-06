package com.enderio.base.api.filter;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.AvailableSince("8.0")
public interface ItemFilter {
    /**
     * Test whether the item stack passes this filter.
     * @param target The target handler which can be used for comparisons using the destination before moving.
     * @param stack The stack being tested.
     * @return The stack that is permitted (can differ in size), use {@link ItemStack#EMPTY} to transfer nothing.
     */
    ItemStack test(@Nullable IItemHandler target, ItemStack stack);
}
