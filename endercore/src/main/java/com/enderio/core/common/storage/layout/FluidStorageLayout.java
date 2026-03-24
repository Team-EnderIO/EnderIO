package com.enderio.core.common.storage.layout;

import com.enderio.core.common.storage.slot.ResourceSlotKey;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;
import java.util.Map;

public class FluidStorageLayout extends ResourceStorageLayout<FluidResource> {
    protected FluidStorageLayout(List<SlotConfig<FluidResource>> slots, Map<ResourceSlotKey, List<Integer>> keyMap) {
        super(slots, keyMap);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ResourceStorageLayout.Builder<Builder, FluidResource> {
        private Builder() {
        }

        public FluidStorageLayout build() {
            return new FluidStorageLayout(slots, keyMap);
        }
    }
}
