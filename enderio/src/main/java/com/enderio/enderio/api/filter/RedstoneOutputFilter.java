package com.enderio.enderio.api.filter;

import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface RedstoneOutputFilter extends RedstoneFilter {

    int getOutputSignal(RedstoneOutputFilterContext context, DyeColor control);
}
