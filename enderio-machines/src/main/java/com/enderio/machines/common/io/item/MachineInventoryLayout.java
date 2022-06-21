package com.enderio.machines.common.io.item;

import com.enderio.base.common.capacitor.CapacitorUtil;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Describes the slot configuration of an inventory.
 */
public class MachineInventoryLayout {

    /**
     * Slot configurations.
     */
    private final List<SlotConfig> slots;

    /**
     * The index of the capacitor-specific slot.
     */
    private final int capacitorSlot;

    private MachineInventoryLayout(Builder builder) {
        this.slots = List.copyOf(builder.slots);

        if (builder.hasCapacitor)
            capacitorSlot = slots.size() - 1;
        else capacitorSlot = -1;
    }

    /**
     * Get an inventory layout builder.
     * @param capacitor Whether or not the inventory will have a capacitor slot.
     */
    public static Builder builder(boolean capacitor) {
        return new Builder(capacitor);
    }

    /**
     * Get the number of slots the inventory will have.
     */
    public int getSlotCount() {
        return slots.size();
    }

    /**
     * Whether the inventory supports a capacitor.
     */
    public boolean supportsCapacitor() {
        return capacitorSlot >= 0;
    }

    /**
     * Get the capacitor slot.
     * @return The capacitor slot or -1 if there isn't one. Use {@link #supportsCapacitor()} to check.
     */
    public int getCapacitorSlot() {
        return capacitorSlot;
    }

    /**
     * Get the item stack limit for the given slot.
     */
    public int getStackLimit(int slot) {
        return slots.get(slot).stackLimit;
    }

    /**
     * Determine if the slot can be inserted to externally.
     */
    public boolean canInsert(int slot) {
        return slots.get(slot).insert;
    }

    /**
     * Determine if the slot can be extracted from externally.
     */
    public boolean canExtract(int slot) {
        return slots.get(slot).extract;
    }

    /**
     * Determine if the slot can be inserted to in gui.
     */
    public boolean guiCanInsert(int slot) {
        return slots.get(slot).guiInsert;
    }

    /**
     * Determine if the slot can be extracted from in gui.
     */
    public boolean guiCanExtract(int slot) {
        return slots.get(slot).guiExtract;
    }

    /**
     * Test if an item stack is valid for the given slot.
     */
    public boolean isItemValid(int slot, ItemStack stack) {
        return slots.get(slot).filter.test(slot, stack);
    }

    public static class Builder {

        private final ArrayList<SlotConfig> slots = new ArrayList<>();
        private final boolean hasCapacitor;
        private int currentStackLimit = 64;

        private Builder(boolean capacitor) {
            hasCapacitor = capacitor;
        }

        /**
         * Add a custom slot to the inventory layout.
         */
        public Builder slot(Function<SlotBuilder, SlotBuilder> builder) {
            slots.add(builder.apply(new SlotBuilder()).build());
            return this;
        }

        // region Stack limit helper

        /**
         * Set the stack limit for the next slots to be added.
         */
        public Builder setStackLimit(int limit) {
            currentStackLimit = limit;
            return this;
        }

        // endregion

        // region Preset slot types

        /**
         * Add an input slot.
         * This slot can be inserted and extracted via gui but only inserted to externally.
         */
        public Builder inputSlot() {
            return inputSlot(1, (i,s) -> true);
        }

        /**
         * Add some input slots.
         * This slot can be inserted and extracted via gui but only inserted to externally.
         *
         * @param count The number of slots to add.
         */
        public Builder inputSlot(int count) {
            return inputSlot(count, (i,s) -> true);
        }

        /**
         * Add an input slot.
         * This slot can be inserted and extracted via gui but only inserted to externally.
         *
         * @param filter The filter predicate for the slot
         */
        public Builder inputSlot(BiPredicate<Integer, ItemStack> filter) {
            return inputSlot(1, filter);
        }

        /**
         * Add some input slots.
         * This slot can be inserted and extracted via gui but only inserted to externally.
         *
         * @param count The number of slots to add.
         * @param filter The filter predicate for the slot
         */
        public Builder inputSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().guiExtract().insert().filter(filter).stackLimit(currentStackLimit));
            }
            return this;
        }

        /**
         * Add an output slot.
         * This slot can only be extracted from via gui and externally.
         */
        public Builder outputSlot() {
            return outputSlot(1, (i,s) -> true);
        }

        /**
         * Add some output slots.
         * This slot can only be extracted from via gui and externally.
         *
         * @param count The number of slots to add.
         */
        public Builder outputSlot(int count) {
            return outputSlot(count, (i,s) -> true);
        }

        /**
         * Add some output slots.
         * This slot can only be extracted from via gui and externally.
         *
         * @param filter The filter predicate for the slot
         */
        public Builder outputSlot(BiPredicate<Integer, ItemStack> filter) {
            return outputSlot(1, filter);
        }

        /**
         * Add some output slots.
         * This slot can only be extracted from via gui and externally.
         *
         * @param count The number of slots to add.
         * @param filter The filter predicate for the slot
         */
        public Builder outputSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiExtract().extract().filter(filter).stackLimit(currentStackLimit));
            }
            return this;
        }

        /**
         * Add a storage slot.
         * This slot can inserted to or extracted from via gui and externally.
         */
        public Builder storageSlot() {
            return storageSlot(1, (i,s) -> true);
        }

        /**
         * Add a storage slot.
         * This slot can inserted to or extracted from via gui and externally.
         *
         * @param count The number of slots to add.
         */
        public Builder storageSlot(int count) {
            return storageSlot(count, (i,s) -> true);
        }

        /**
         * Add a storage slot.
         * This slot can inserted to or extracted from via gui and externally.
         *
         * @param filter The filter predicate for the slot
         */
        public Builder storageSlot(BiPredicate<Integer, ItemStack> filter) {
            return storageSlot(1, filter);
        }

        /**
         * Add a storage slot.
         * This slot can inserted to or extracted from via gui and externally.
         *
         * @param count The number of slots to add.
         * @param filter The filter predicate for the slot
         */
        public Builder storageSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().guiExtract().insert().extract().filter(filter).stackLimit(currentStackLimit));
            }
            return this;
        }

        /**
         * Add a ghost slot.
         * This slot can only be interacted with via gui.
         */
        public Builder ghostSlot() {
            return ghostSlot(1, (i,s) -> true);
        }

        /**
         * Add a ghost slot.
         * This slot can only be interacted with via gui.
         *
         * @param count The number of slots to add.
         */
        public Builder ghostSlot(int count) {
            return ghostSlot(count, (i,s) -> true);
        }

        /**
         * Add a ghost slot.
         * This slot can only be interacted with via gui.
         *
         * @param filter The filter predicate for the slot
         */
        public Builder ghostSlot(BiPredicate<Integer, ItemStack> filter) {
            return ghostSlot(1, filter);
        }

        /**
         * Add a ghost slot.
         * This slot can only be inserted to (or cleared as per implementation of GhostSlot).
         *
         * @param count The number of slots to add.
         * @param filter The filter predicate for the slot
         */
        public Builder ghostSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().filter(filter).stackLimit(currentStackLimit));
            }
            return this;
        }

        // endregion

        /**
         * Build the inventory layout.
         */
        public MachineInventoryLayout build() {
            // Add capacitor if it was requested
            if (hasCapacitor) {
                slot(slot -> slot.guiInsert().guiExtract().filter((i, s) -> CapacitorUtil.isCapacitor(s)).stackLimit(1));
            }
            return new MachineInventoryLayout(this);
        }

        public static class SlotBuilder {
            private boolean insert, extract, guiInsert, guiExtract;
            private int stackLimit = 64;

            private BiPredicate<Integer, ItemStack> filter = (i,s) -> true;

            private SlotBuilder() {}

            /**
             * Enable external insertion.
             */
            public SlotBuilder insert() {
                insert = true;
                return this;
            }

            /**
             * Enable external extraction.
             */
            public SlotBuilder extract() {
                extract = true;
                return this;
            }

            /**
             * Enable GUI insertion.
             */
            public SlotBuilder guiInsert() {
                guiInsert = true;
                return this;
            }

            /**
             * Enable gui extraction.
             */
            public SlotBuilder guiExtract() {
                guiExtract = true;
                return this;
            }

            /**
             * Set the slot filter.
             */
            public SlotBuilder filter(BiPredicate<Integer, ItemStack> filter) {
                this.filter = filter;
                return this;
            }

            /**
             * Set the stack limit.
             */
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
