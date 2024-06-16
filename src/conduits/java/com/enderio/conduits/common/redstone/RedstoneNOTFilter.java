package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;

public class RedstoneNOTFilter implements RedstoneInsertFilter {

    public static final RedstoneNOTFilter INSTANCE = new RedstoneNOTFilter();

    private RedstoneNOTFilter() {
    }

    @Override
    public int getOutputSignal(RedstoneConduitData data, ColorControl control) {
        return data.isActive(control) ? 0 : 15;
    }
}
