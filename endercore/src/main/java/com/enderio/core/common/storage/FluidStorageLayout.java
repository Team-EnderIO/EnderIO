package com.enderio.core.common.storage;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;
import java.util.Map;

public class FluidStorageLayout<TContext> extends ResourceStorageLayout<FluidResource, TContext> {
    protected FluidStorageLayout(List<SlotConfig<FluidResource, TContext>> slots, Map<ResourceSlotKey, List<Integer>> keyMap) {
        super(slots, keyMap);
    }

    // region Single Slot Access Stack Helpers

    public FluidStack getStack(ResourceHandler<FluidResource> handler, SingleResourceSlotKey<FluidResource> key) {
        return getResource(handler, key).toStack(getAmountAsInt(handler, key));
    }

    public void setStack(ResourceStorage<FluidResource> storage, SingleResourceSlotKey<FluidResource> key, FluidStack stack) {
        set(storage, key, FluidResource.of(stack), stack.getAmount());
    }

    // endregion

    // region Multi Slot Access Stack Helpers

    public FluidStack getStack(ResourceHandler<FluidResource> handler, MultiResourceSlotKey<FluidResource> key, int index) {
        return getResource(handler, key, index).toStack(getAmountAsInt(handler, key, index));
    }

    public void setStack(ResourceStorage<FluidResource> storage, MultiResourceSlotKey<FluidResource> key, int index, FluidStack stack) {
        set(storage, key, index, FluidResource.of(stack), stack.getAmount());
    }

    // endregion

    public static <TContext> Builder<TContext> builder() {
        return new Builder<>();
    }

    public static class Builder<TContext> extends ResourceStorageLayout.Builder<Builder<TContext>, FluidResource, TContext> {
        private Builder() {
        }

        public FluidStorageLayout<TContext> build() {
            return new FluidStorageLayout<>(slots, keyMap);
        }
    }
}
