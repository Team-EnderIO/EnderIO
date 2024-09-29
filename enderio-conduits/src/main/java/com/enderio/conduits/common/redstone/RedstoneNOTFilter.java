package com.enderio.conduits.common.redstone;

import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import net.minecraft.world.item.DyeColor;

public class RedstoneNOTFilter implements RedstoneInsertFilter {

    public static final RedstoneNOTFilter INSTANCE = new RedstoneNOTFilter();

    private RedstoneNOTFilter() {
    }

    @Override
    public int getOutputSignal(RedstoneConduitData data, DyeColor control) {
        return data.isActive(control) ? 0 : 15;
    }
}
