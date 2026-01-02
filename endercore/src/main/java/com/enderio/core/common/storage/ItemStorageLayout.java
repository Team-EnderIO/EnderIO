package com.enderio.core.common.storage;

import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;
import java.util.Map;

public class ItemStorageLayout<TContext> extends ResourceStorageLayout<ItemResource, TContext> {
    protected ItemStorageLayout(List<SlotConfig<ItemResource, TContext>> slots, Map<ResourceSlotKey, List<Integer>> keyMap) {
        super(slots, keyMap);
    }

    public static <TContext> Builder<TContext> builder() {
        return new Builder<>();
    }

    public static class Builder<TContext> extends ResourceStorageLayout.Builder<Builder<TContext>, ItemResource, TContext> {
        private Builder() {
        }

        public ItemStorageLayout<TContext> build() {
            return new ItemStorageLayout<>(slots, keyMap);
        }
    }
}
