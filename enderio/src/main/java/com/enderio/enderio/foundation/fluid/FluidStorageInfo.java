package com.enderio.enderio.foundation.fluid;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.Objects;

public record FluidStorageInfo(FluidStack contents, int capacity) {
    public static FluidStorageInfo of(ResourceHandler<FluidResource> handler, int index) {
        var resource = handler.getResource(index);
        return new FluidStorageInfo(resource.toStack(handler.getAmountAsInt(index)), handler.getCapacityAsInt(index, resource));
    }

    public static FluidStorageInfo of(ResourceStorage<FluidResource> storage, SingleResourceSlotKey<FluidResource> slot) {
        var resource = storage.getResource(slot);
        return new FluidStorageInfo(resource.toStack(storage.getAmountAsInt(slot)), storage.getCapacityAsInt(slot, resource));
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
