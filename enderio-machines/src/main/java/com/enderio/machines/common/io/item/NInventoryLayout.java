package com.enderio.machines.common.io.item;

import com.enderio.base.common.capacitor.CapacitorUtil;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class NInventoryLayout {
    
    private List<SlotConfig> slots;
    private int capacitorSlot;

    private NInventoryLayout(LayoutBuilder builder) {
        this.slots = List.copyOf(builder.slots);

        if (builder.hasCapacitor)
            capacitorSlot = slots.size() - 1;
        else capacitorSlot = -1;
    }
    
    public static LayoutBuilder builder(boolean capacitor) {
        return new LayoutBuilder(capacitor);
    }

    public int getSlotCount() {
        return slots.size();
    }

    public boolean supportsCapacitor() {
        return capacitorSlot >= 0;
    }

    public int getCapacitorSlot() {
        return capacitorSlot;
    }

    public int getStackLimit(int slot) {
        return slots.get(slot).stackLimit;
    }

    public boolean canInsert(int slot) {
        return slots.get(slot).insert;
    }
    
    public boolean canExtract(int slot) {
        return slots.get(slot).extract;
    }
    
    public boolean guiCanExtract(int slot) {
        return slots.get(slot).guiExtract;
    }
    
    public boolean guiCanInsert(int slot) {
        return slots.get(slot).guiInsert;
    }
    
    public boolean test(int slot, ItemStack stack) {
        return slots.get(slot).filter.test(slot, stack);
    }

    public static class LayoutBuilder {

        private final ArrayList<SlotConfig> slots = new ArrayList<>();
        private final boolean hasCapacitor;

        private LayoutBuilder(boolean capacitor) {
            hasCapacitor = capacitor;
        }

        /**
         * Add a custom slot to the inventory layout.
         */
        public LayoutBuilder slot(Function<SlotBuilder, SlotBuilder> builder) {
            slots.add(builder.apply(new SlotBuilder()).build());
            return this;
        }

        // region Preset slot types

        public LayoutBuilder inputSlot() {
            return inputSlot(1, (i,s) -> true);
        }

        public LayoutBuilder inputSlot(int count) {
            return inputSlot(count, (i,s) -> true);
        }

        public LayoutBuilder inputSlot(BiPredicate<Integer, ItemStack> filter) {
            return inputSlot(1, filter);
        }

        public LayoutBuilder inputSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().guiExtract().insert().filter(filter));
            }
            return this;
        }

        public LayoutBuilder outputSlot() {
            return outputSlot(1, (i,s) -> true);
        }

        public LayoutBuilder outputSlot(int count) {
            return outputSlot(count, (i,s) -> true);
        }

        public LayoutBuilder outputSlot(BiPredicate<Integer, ItemStack> filter) {
            return outputSlot(1, filter);
        }

        public LayoutBuilder outputSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiExtract().extract().filter(filter));
            }
            return this;
        }

        public LayoutBuilder storageSlot() {
            return storageSlot(1, (i,s) -> true);
        }

        public LayoutBuilder storageSlot(int count) {
            return storageSlot(count, (i,s) -> true);
        }

        public LayoutBuilder storageSlot(BiPredicate<Integer, ItemStack> filter) {
            return storageSlot(1, filter);
        }

        public LayoutBuilder storageSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().guiExtract().insert().extract().filter(filter));
            }
            return this;
        }

        // endregion

        public NInventoryLayout build() {
            // Add capacitor if it was requested
            if (hasCapacitor) {
                slot(slot -> slot.guiInsert().guiExtract().filter((i, s) -> CapacitorUtil.isCapacitor(s)).stackLimit(1));
            }
            return new NInventoryLayout(this);
        }

        public static class SlotBuilder {
            private boolean insert, extract, guiInsert, guiExtract;
            private int stackLimit = 64;

            private BiPredicate<Integer, ItemStack> filter = (i,s) -> true;

            private SlotBuilder() {}

            public SlotBuilder insert() {
                insert = true;
                return this;
            }

            public SlotBuilder extract() {
                extract = true;
                return this;
            }

            public SlotBuilder guiExtract() {
                guiExtract = true;
                return this;
            }

            public SlotBuilder guiInsert() {
                guiInsert = true;
                return this;
            }

            public SlotBuilder filter(BiPredicate<Integer, ItemStack> filter) {
                this.filter = filter;
                return this;
            }

            public SlotBuilder stackLimit(int limit) {
                this.stackLimit = Math.max(Math.min(limit, 64), 0);
                return this;
            }

            private SlotConfig build() {
                return new SlotConfig(insert, extract, guiInsert, guiExtract, stackLimit, filter);
            }
        }
    }

    private record SlotConfig(boolean insert, boolean extract, boolean guiInsert, boolean guiExtract, int stackLimit, BiPredicate<Integer, ItemStack> filter) {}
}
