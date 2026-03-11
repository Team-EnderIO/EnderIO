package com.enderio.enderio.api.filter;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.AvailableSince("8.0")
public interface FluidFilter {
    /**
     * Test whether the fluid stack passes this filter.
     * @param target The target handler which can be used for comparisons using the destination before moving.
     * @param stack The stack being tested.
     * @return The stack that is permitted (can differ in amount), use {@link FluidStack#EMPTY} to transfer nothing.
     */
    FluidStack test(@Nullable IFluidHandler target, FluidStack stack);
}
