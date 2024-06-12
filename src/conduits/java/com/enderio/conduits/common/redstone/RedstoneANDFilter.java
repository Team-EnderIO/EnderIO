package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;
import com.enderio.conduits.common.init.ConduitComponents;
import net.minecraft.world.item.ItemStack;

public class RedstoneANDFilter extends DoubleRedstoneChannel implements RedstoneInsertFilter {

    public RedstoneANDFilter(ItemStack stack) {
        super(stack, ConduitComponents.REDSTONE_AND_FILTER);
    }

    @Override
    public int getOutputSignal(RedstoneExtendedData data, ColorControl control) {
        boolean b = data.isActive(getFirstChannel()) && data.isActive(getSecondChannel());
        return b ? 15 : 0 ;
    }
}
