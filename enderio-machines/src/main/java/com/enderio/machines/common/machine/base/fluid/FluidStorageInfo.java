package com.enderio.machines.common.machine.base.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

public record FluidStorageInfo(FluidStack contents, int capacity) {
    public static FluidStorageInfo of(IFluidTank storage) {
        return new FluidStorageInfo(storage.getFluid(), storage.getCapacity());
    }

    public FluidStorageInfo withContents(FluidStack contents) {
        return new FluidStorageInfo(contents, capacity());
    }
}
