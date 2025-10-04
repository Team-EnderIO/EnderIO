package com.enderio.enderio.common.filter.redstone;

import com.enderio.enderio.api.filter.RedstoneOutputFilter;
import com.enderio.enderio.api.filter.RedstoneOutputFilterContext;
import com.enderio.enderio.common.init.ConduitComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class RedstoneXNORFilter extends DoubleRedstoneChannel implements RedstoneOutputFilter {

    public RedstoneXNORFilter(ItemStack stack) {
        super(stack, ConduitComponents.REDSTONE_XNOR_FILTER);
    }

    @Override
    public int getOutputSignal(RedstoneOutputFilterContext context, DyeColor control) {
        boolean b = context.isActive(getFirstChannel()) ^ context.isActive(getSecondChannel());
        return b ? 0 : 15;
    }

}
