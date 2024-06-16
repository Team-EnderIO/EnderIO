package com.enderio.api.farm;

import net.minecraft.core.BlockPos;

public interface FarmTask {

    FarmInteraction farm(BlockPos soil, FarmingStation farmBlockEntity);
}
