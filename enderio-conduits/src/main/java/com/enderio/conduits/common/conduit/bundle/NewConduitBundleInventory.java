package com.enderio.conduits.common.conduit.bundle;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.bundle.ConduitBundleReader;
import com.enderio.conduits.common.conduit.SlotData;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewConduitBundleInventory implements INBTSerializable<CompoundTag> {
    private static final String LEGACY_CONDUIT_INV_KEY = "ConduitInv";
    private static final String SAVE_KEY = "ConduitInventory";

    private final ConduitBundleReader conduitBundle;

    private final Map<Holder<Conduit<?, ?>>, ConduitInventory> conduitInventories = new HashMap<>();

    public NewConduitBundleInventory(ConduitBundleReader conduitBundle) {
        this.conduitBundle = conduitBundle;
    }

    @Nullable
    public IItemHandlerModifiable getInventory(Holder<Conduit<?, ?>> conduit, Direction side) {
        if (conduit.value().getInventorySize() <= 0) {
            return null;
        }

        return conduitInventories.computeIfAbsent(conduit, ConduitInventory::new).getInventory(side);
    }

    public void removeConduit(Holder<Conduit<?, ?>> conduit) {
        conduitInventories.remove(conduit);
    }

    public void onChanged() {
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        // TODO: Ender IO 8 - remove support for legacy inventory loading.
        if (compoundTag.contains(LEGACY_CONDUIT_INV_KEY)) {
            // Legacy data loading procedure, a contiguous list of items for each connection and conduit.
            ListTag list = compoundTag.getList(LEGACY_CONDUIT_INV_KEY, Tag.TAG_COMPOUND);
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                items.add(ItemStack.parseOptional(provider, list.getCompound(i)));
            }

            // Unpack the legacy dense item list.
            for (int i = 0; i < items.size(); i++) {
                var slotData = SlotData.of(i);
                var itemStack = items.get(i);

                // TODO: safety
                var conduit = conduitBundle.getConduits().get(slotData.conduitIndex());

                if (conduit.value().getInventorySize() <= 0) {
                    // TODO: Warn if items will be lost?
                    continue;
                }

                int slotIndex = conduit.value().getIndexForLegacySlot(slotData.slotType());
                if (slotIndex < 0) {
                    // TODO: If the item isn't empty, maybe throw a warning?
                    continue;
                }

                var inventory = Objects.requireNonNull(getInventory(conduit, slotData.direction()));
                inventory.setStackInSlot(slotIndex, itemStack);
            }
        }
    }

    private class ConduitInventory {
        private final Holder<Conduit<?, ?>> conduit;
        private final Map<Direction, ConnectionInventory> connectionInventories = new HashMap<>();

        private ConduitInventory(Holder<Conduit<?, ?>> conduit) {
            this.conduit = conduit;
        }

        public IItemHandlerModifiable getInventory(Direction side) {
            return connectionInventories.computeIfAbsent(side, k -> new ConnectionInventory());
        }

        private class ConnectionInventory extends ItemStackHandler {
            public ConnectionInventory() {
                super(conduit.value().getInventorySize());
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return conduit.value().isItemValid(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                onChanged();
            }
        }
    }
}
