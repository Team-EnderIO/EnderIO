package com.enderio.machines.common.blockentity.data.sidecontrol.item;

import com.enderio.base.EnderIO;
import com.enderio.base.common.capacitor.CapacitorUtil;
import net.minecraft.world.item.ItemStack;

import javax.naming.OperationNotSupportedException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemSlotLayout {
    public enum SlotType {
        INPUT,
        OUTPUT,
        CAPACITOR,
        UPGRADE,
        MISC
    }

    private record SlotDefinition(SlotType type, Predicate<ItemStack> validator) {}

    private final Map<Integer, SlotType> slotTypeMap;
    private final Map<Integer, Predicate<ItemStack>> slotPredicates;
    private final int slotCount;

    private ItemSlotLayout(Map<Integer, SlotType> slotTypeMap, Map<Integer, Predicate<ItemStack>> slotPredicates) {
        this.slotTypeMap = Map.copyOf(slotTypeMap);
        this.slotPredicates = Map.copyOf(slotPredicates);
        this.slotCount = slotTypeMap.size();
    }

    public boolean validateStack(int slot, ItemStack stack) {
        if (slotPredicates.containsKey(slot)) {
            return slotPredicates.get(slot).test(stack);
        }

        return true;
    }

    @Deprecated
    public static ItemSlotLayout basic(int inputs, int outputs) {
        Map<Integer, SlotType> slotMap = new HashMap<>();
        for (int i = 0; i < inputs + outputs; i++) {
            SlotType type;
            if (i < inputs) {
                type = SlotType.INPUT;
            } else if (i < inputs + outputs) {
                type = SlotType.OUTPUT;
            } else {
                type = SlotType.MISC;
            }

            slotMap.put(i, type);
        }
        return new ItemSlotLayout(slotMap, Map.of());
    }

    @Deprecated
    public static ItemSlotLayout withCapacitor(int inputs, int outputs) {
        Map<Integer, SlotType> slotMap = new HashMap<>();
        for (int i = 0; i < inputs + outputs + 1; i++) {
            SlotType type;
            if (i < inputs) {
                type = SlotType.INPUT;
            } else if (i < inputs + outputs) {
                type = SlotType.OUTPUT;
            } else {
                type = SlotType.CAPACITOR;
            }

            slotMap.put(i, type);
        }
        return new ItemSlotLayout(slotMap, Map.of());
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getSlotCount() {
        return slotCount;
    }

    public boolean isSlotType(int slot, SlotType type) {
        return slotTypeMap.getOrDefault(slot, SlotType.MISC) == type;
    }

    public List<Integer> getAll(SlotType type) {
        return slotTypeMap.entrySet().stream().filter(entry -> entry.getValue() == type).map(Map.Entry::getKey).toList();
    }

    public Optional<Integer> getFirst(SlotType type) {
        if (!getAll(type).isEmpty()) {
            return Optional.of(getAll(type).get(0));
        }
        return Optional.empty();
    }

    public static class Builder {
        private final Map<Integer, SlotType> slotTypeMap;
        private final Map<Integer, Predicate<ItemStack>> slotPredicates;

        private int slotCounter;

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
            slotCounter++;
            return this;
        }

        public ItemSlotLayout build() {
            return new ItemSlotLayout(slotTypeMap, slotPredicates);
        }
    }
}
