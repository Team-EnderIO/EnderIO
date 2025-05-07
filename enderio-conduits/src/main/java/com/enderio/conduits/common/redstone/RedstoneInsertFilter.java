package com.enderio.conduits.common.redstone;

import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitNetworkContext;
import net.minecraft.world.item.DyeColor;

public interface RedstoneInsertFilter {

    int getOutputSignal(RedstoneConduitNetworkContext context, DyeColor control);
}
