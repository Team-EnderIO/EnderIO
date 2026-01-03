package com.enderio.core.common.storage.layout;

import com.enderio.core.common.storage.slot.ResourceSlotKey;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

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
