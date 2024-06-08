package com.enderio.conduits.common.redstone;

import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;

public interface RedstoneInsertFilter extends ResourceFilter {

    int getOutputSignal(RedstoneExtendedData data, ColorControl control);
}
