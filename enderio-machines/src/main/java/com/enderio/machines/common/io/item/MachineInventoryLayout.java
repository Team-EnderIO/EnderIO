package com.enderio.machines.common.io.item;

import com.enderio.base.EnderIO;
import com.enderio.base.common.capacitor.CapacitorUtil;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MachineInventoryLayout {
    public enum SlotType {
        INPUT,
        OUTPUT,
        CAPACITOR,
        MISC
    }

    private final Map<Integer, SlotType> slotTypeMap;
    private final Map<Integer, Predicate<ItemStack>> slotPredicates;
    private final int slotCount;
    private final int capacitorSlot;

    private MachineInventoryLayout(Builder builder) {
        this.slotTypeMap = Map.copyOf(builder.slotTypeMap);
        this.slotPredicates = Map.copyOf(builder.slotPredicates);
        this.slotCount = slotTypeMap.size();
        this.capacitorSlot = builder.capacitorSlot;
    }

    public boolean validateStack(int slot, ItemStack stack) {
        if (slotPredicates.containsKey(slot)) {
            return slotPredicates.get(slot).test(stack);
        }
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getSlotCount() {
        return slotCount;
    }

    public boolean isInput(int slot) {
        return slotTypeMap.containsKey(slot) && slotTypeMap.get(slot) == SlotType.INPUT;
    }

    public boolean isOutput(int slot) {
        return slotTypeMap.containsKey(slot) && slotTypeMap.get(slot) == SlotType.OUTPUT;
    }

    /**
     * Whether the inventory has a capacitor slot
     */
    public boolean hasCapacitorSlot() {
        return getCapacitorSlot() > 0;
    }

    /**
     * Get the slot that the capacitor lies in.
     *
     * @apiNote This can be -1 meaning the machine has no capacitor.
     */
    public int getCapacitorSlot() {
        return capacitorSlot;
    }

    public static class Builder {
        private final Map<Integer, SlotType> slotTypeMap;
        private final Map<Integer, Predicate<ItemStack>> slotPredicates;

        private int slotCounter;

        private int capacitorSlot;

        private Builder() {
            slotTypeMap = new HashMap<>();
            slotPredicates = new HashMap<>();
        }

        public Builder addInput() {
            slotTypeMap.put(slotCounter++, SlotType.INPUT);
            return this;
        }

        public Builder addInput(Predicate<ItemStack> validator) {
            slotTypeMap.put(slotCounter, SlotType.INPUT);
            slotPredicates.put(slotCounter, validator);
            slotCounter++;
            return this;
        }

        public Builder addBasicInputs(int count) {
            for (int i = 0; i < count; i++) {
                slotTypeMap.put(slotCounter++, SlotType.INPUT);
            }
            return this;
        }

        public Builder addOutput() {
            slotTypeMap.put(slotCounter, SlotType.OUTPUT);
            slotCounter++;
            return this;
        }

        public Builder addOutputs(int count) {
            for (int i = 0; i < count; i++) {
                addOutput();
            }
            return this;
        }

        public Builder addMisc() {
            slotTypeMap.put(slotCounter++, SlotType.MISC);
            return this;
        }

        public Builder addMisc(Predicate<ItemStack> validator) {
            slotTypeMap.put(slotCounter, SlotType.MISC);
            slotPredicates.put(slotCounter, validator);
            slotCounter++;
            return this;
        }

        public Builder addBasicMisc(int count) {
            for (int i = 0; i < count; i++) {
                slotTypeMap.put(slotCounter++, SlotType.MISC);
            }
            return this;
        }

        public Builder capacitor() {
            if (slotTypeMap.containsValue(SlotType.CAPACITOR)) {
                EnderIO.LOGGER.error("Attempted to add a second capacitor slot to an inventory.");
                return this;
            }

            // Add slot type and the validator.
            slotTypeMap.put(slotCounter, SlotType.CAPACITOR);
            slotPredicates.put(slotCounter, CapacitorUtil::isCapacitor);
            capacitorSlot = slotCounter;
            slotCounter++;
            return this;
        }

        public Builder capacitor(Supplier<Boolean> present) {
            return present.get() ? capacitor() : this;
        }

        public MachineInventoryLayout build() {
            return new MachineInventoryLayout(this);
        }
    }
}
