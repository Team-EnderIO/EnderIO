package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;
import net.minecraft.world.item.ItemStack;

public class RedstoneNOTFilter implements RedstoneInsertFilter {

    private final ItemStack stack;

    public RedstoneNOTFilter(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getOutputSignal(RedstoneExtendedData data, ColorControl control) {
        return data.isActive(control) ? 0 : 15;
    }
}
