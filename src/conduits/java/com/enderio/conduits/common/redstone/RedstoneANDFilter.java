package com.enderio.conduits.common.redstone;

import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import com.enderio.conduits.common.init.ConduitComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class RedstoneANDFilter extends DoubleRedstoneChannel implements RedstoneInsertFilter {

    public RedstoneANDFilter(ItemStack stack) {
        super(stack, ConduitComponents.REDSTONE_AND_FILTER);
    }

    @Override
    public int getOutputSignal(RedstoneConduitData data, DyeColor control) {
        boolean b = data.isActive(getFirstChannel()) && data.isActive(getSecondChannel());
        return b ? 15 : 0 ;
    }
}
