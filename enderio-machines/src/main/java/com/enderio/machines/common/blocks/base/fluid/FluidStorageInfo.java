package com.enderio.machines.common.blocks.base.fluid;

import java.util.Objects;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

public record FluidStorageInfo(FluidStack contents, int capacity) {
    public static FluidStorageInfo of(IFluidTank storage) {
        return new FluidStorageInfo(storage.getFluid().copy(), storage.getCapacity());
    }

    public FluidStorageInfo withContents(FluidStack contents) {
        return new FluidStorageInfo(contents, capacity());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FluidStorageInfo that)) {
            return false;
        }

        return capacity == that.capacity && FluidStack.matches(contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(FluidStack.hashFluidAndComponents(contents), capacity);
    }
}
