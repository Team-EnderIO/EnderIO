package com.enderio.core.common.attachment;

import net.minecraft.world.level.material.Fluid;

import java.util.function.Predicate;

public interface IStrictItemFluidHandlerConfig extends IItemFluidHandlerConfig {
    Predicate<Fluid> getFluidFilter();
}
