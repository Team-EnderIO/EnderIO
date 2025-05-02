package com.enderio.base.common.item.filter;

import com.enderio.base.api.new_filter.ItemStackFilter;
import com.enderio.base.common.filter.SimpleItemStackFilter;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class SimpleItemFilterItem extends Item {

    public static ICapabilityProvider<ItemStack, Void, ItemStackFilter> ITEM_STACK_FILTER_PROVIDER =
        (stack, v) -> stack.getOrDefault(EIODataComponents.SIMPLE_ITEM_STACK_FILTER, SimpleItemStackFilter.EMPTY);

    public SimpleItemFilterItem(Properties properties) {
        super(properties);
    }

    // TODO: Open the menu
}
