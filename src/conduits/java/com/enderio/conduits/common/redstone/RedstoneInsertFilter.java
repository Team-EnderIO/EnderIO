package com.enderio.conduits.common.redstone;

import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;

public interface RedstoneInsertFilter extends ResourceFilter {

    int getOutputSignal(RedstoneConduitData data, ColorControl control);
}
