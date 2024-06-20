package com.enderio.api.filter;

import net.minecraftforge.fluids.FluidStack;

import java.util.function.Predicate;

public interface FluidStackFilter extends ResourceFilter, Predicate<FluidStack> {
}
