package com.enderio.base.api.filter;

import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Predicate;

public interface FluidStackFilter extends ResourceFilter, Predicate<FluidStack> {
}
