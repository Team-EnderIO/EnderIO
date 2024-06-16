package com.enderio.api.conduit;

import com.enderio.api.misc.ColorControl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

@FunctionalInterface
public interface ColoredRedstoneProvider {
    boolean isRedstoneActive(ServerLevel serverLevel, BlockPos pos, ColorControl color);
}
