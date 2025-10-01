package com.enderio.enderio.api.filter;

import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface RedstoneOutputFilterContext {
    boolean isActive(DyeColor color);
    int getSignal(DyeColor color);
}
