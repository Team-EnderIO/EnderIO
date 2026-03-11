package com.enderio.enderio.content.filters.redstone;

import com.enderio.enderio.api.filter.RedstoneOutputFilter;
import com.enderio.enderio.api.filter.RedstoneOutputFilterContext;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class RedstoneNANDFilter extends DoubleRedstoneChannel implements RedstoneOutputFilter {

    public RedstoneNANDFilter(ItemStack stack) {
        super(stack, EIODataComponents.REDSTONE_FILTER_DOUBLE_CHANNEL);
    }

    @Override
    public int getOutputSignal(RedstoneOutputFilterContext context, DyeColor control) {
        boolean b = context.isActive(getFirstChannel()) && context.isActive(getSecondChannel());
        return b ? 0 : 15;
    }
}
