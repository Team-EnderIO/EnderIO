package com.enderio.enderio.api.farm;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface FarmTask {

    FarmInteraction farm(BlockPos soil, FarmingStation farmBlockEntity);
}
