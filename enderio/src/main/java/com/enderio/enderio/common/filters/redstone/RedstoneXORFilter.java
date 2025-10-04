package com.enderio.enderio.common.filters.redstone;

import com.enderio.enderio.api.filter.RedstoneOutputFilter;
import com.enderio.enderio.api.filter.RedstoneOutputFilterContext;
import com.enderio.enderio.common.init.ConduitComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class RedstoneXORFilter extends DoubleRedstoneChannel implements RedstoneOutputFilter {

    public RedstoneXORFilter(ItemStack stack) {
        super(stack, ConduitComponents.REDSTONE_XOR_FILTER);
    }

    @Override
    public int getOutputSignal(RedstoneOutputFilterContext context, DyeColor control) {
        boolean b = context.isActive(getFirstChannel()) ^ context.isActive(getSecondChannel());
        return b ? 15 : 0;
    }
}
