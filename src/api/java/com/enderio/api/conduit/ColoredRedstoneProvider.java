package com.enderio.api.conduit;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;

@FunctionalInterface
public interface ColoredRedstoneProvider {
    boolean isRedstoneActive(ServerLevel serverLevel, BlockPos pos, DyeColor color);
}
