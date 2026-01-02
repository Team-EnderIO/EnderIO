package com.enderio.core.common.storage;

import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;
import java.util.Map;

public class FluidStorageLayout<TContext> extends ResourceStorageLayout<FluidResource, TContext> {
    protected FluidStorageLayout(List<SlotConfig<FluidResource, TContext>> slots, Map<ResourceSlotKey, List<Integer>> keyMap) {
        super(slots, keyMap);
    }

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
