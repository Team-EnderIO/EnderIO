package com.enderio.core.common.storage;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class FluidStorage extends StacksResourceStorage<FluidResource, FluidStack> implements ValueIOSerializable {
    public FluidStorage(ResourceStorageLayout<FluidResource> layout) {
        super(layout, FluidStack.EMPTY, FluidStack.OPTIONAL_CODEC);
    }

    @Override
    protected FluidResource getResourceFrom(FluidStack stack) {
        return FluidResource.of(stack);
    }

    @Override
    protected int getAmountFrom(FluidStack stack) {
        return stack.getAmount();
    }

    @Override
    protected FluidStack getStackFrom(FluidResource resource, int amount) {
        return resource.toStack(amount);
    }

    @Override
    protected FluidStack copyOf(FluidStack fluidStack) {
        return fluidStack.copy();
    }

    @Override
    protected boolean matches(FluidStack stack, FluidResource resource) {
        return resource.matches(stack);
    }
}
