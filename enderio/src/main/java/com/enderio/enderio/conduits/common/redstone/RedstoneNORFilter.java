package com.enderio.enderio.conduits.common.redstone;

import com.enderio.enderio.api.filter.RedstoneOutputFilterContext;
import com.enderio.enderio.api.filter.RedstoneOutputFilter;
import com.enderio.enderio.conduits.common.init.ConduitComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class RedstoneNORFilter extends DoubleRedstoneChannel implements RedstoneOutputFilter {

    public RedstoneNORFilter(ItemStack stack) {
        super(stack, ConduitComponents.REDSTONE_NOR_FILTER);
    }

    @Override
    public int getOutputSignal(RedstoneOutputFilterContext context, DyeColor control) {
        boolean b = context.isActive(getFirstChannel()) || context.isActive(getSecondChannel());
        return b ? 0 : 15;
    }
}
