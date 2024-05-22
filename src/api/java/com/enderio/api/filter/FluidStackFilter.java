package com.enderio.api.filter;

import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidStackFilter extends ResourceFilter {
    boolean test(FluidStack stack);
}
