package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import net.minecraft.world.item.ItemStack;

public class RedstoneORFilter extends DoubleRedstoneChannel implements RedstoneInsertFilter {

    public RedstoneORFilter(ItemStack stack) {
        super(stack);
    }

    @Override
    public int getOutputSignal(RedstoneConduitData data, ColorControl control) {
        boolean b = data.isActive(getFirstChannel()) || data.isActive(getSecondChannel());
        return b ? 15 : 0;
    }
}
