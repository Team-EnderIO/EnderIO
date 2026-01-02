package com.enderio.enderio.foundation.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.Objects;

public record FluidStorageInfo(FluidStack contents, int capacity) {
    public static FluidStorageInfo of(IFluidTank storage) {
        return new FluidStorageInfo(storage.getFluid().copy(), storage.getCapacity());
    }

    public static FluidStorageInfo of(ResourceHandler<FluidResource> handler, int index) {
        var resource = handler.getResource(index);
        return new FluidStorageInfo(resource.toStack(handler.getAmountAsInt(index)), handler.getCapacityAsInt(index, resource));
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
