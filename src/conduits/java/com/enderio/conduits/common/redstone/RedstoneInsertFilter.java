package com.enderio.conduits.common.redstone;

import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import net.minecraft.world.item.DyeColor;

public interface RedstoneInsertFilter extends ResourceFilter {

    int getOutputSignal(RedstoneConduitData data, DyeColor control);
}
