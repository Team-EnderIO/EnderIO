package com.enderio.conduits.common.redstone;

import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import com.enderio.conduits.common.init.ConduitComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class RedstoneXNORFilter extends DoubleRedstoneChannel implements RedstoneInsertFilter {

    public RedstoneXNORFilter(ItemStack stack) {
        super(stack, ConduitComponents.REDSTONE_XNOR_FILTER);
    }

    @Override
    public int getOutputSignal(RedstoneConduitData data, DyeColor control) {
        boolean b = data.isActive(getFirstChannel()) ^ data.isActive(getSecondChannel());
        return b ? 0 : 15;
    }

}
