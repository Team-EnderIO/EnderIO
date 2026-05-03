package com.enderio.core.common.storage.layout;

import com.enderio.core.common.storage.slot.ResourceSlotKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;
import java.util.Map;

public class ItemStorageLayout extends ResourceStorageLayout<ItemResource> {
    protected ItemStorageLayout(List<SlotConfig<ItemResource>> slots, Map<ResourceSlotKey<ItemResource>, List<Integer>> keyMap) {
        super(slots, keyMap);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ResourceStorageLayout.Builder<Builder, ItemResource> {
        private Builder() {
        }

        @Override
        protected SlotBuilder<ItemResource> createSlotBuilder() {
            return super.createSlotBuilder().capacity(Item.ABSOLUTE_MAX_STACK_SIZE);
        }

        public ItemStorageLayout build() {
            return new ItemStorageLayout(slots, keyMap);
        }
    }
}
