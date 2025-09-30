package com.enderio.enderio.api.conduits.redstone;

import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface RedstoneFilterContext {
    boolean isActive(DyeColor color);
    int getSignal(DyeColor color);
}
