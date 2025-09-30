package com.enderio.enderio.api.filter;

import com.enderio.enderio.api.conduits.redstone.RedstoneFilterContext;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface RedstoneInsertFilter {

    int getOutputSignal(RedstoneFilterContext context, DyeColor control);
}
