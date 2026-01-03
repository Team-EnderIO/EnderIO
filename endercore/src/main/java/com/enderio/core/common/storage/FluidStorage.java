package com.enderio.core.common.storage;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class FluidStorage<TOwner> extends StacksResourceStorage<FluidResource, FluidStack, TOwner> implements ValueIOSerializable {
    public FluidStorage(ResourceStorageLayout<FluidResource, TOwner> layout, TOwner context) {
        super(layout, context, FluidStack.EMPTY, FluidStack.OPTIONAL_CODEC);
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
