package com.enderio.machines.common.blockentity.data.sidecontrol.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemSlotLayout {
    public enum SlotType {
        INPUT,
        OUTPUT,
        CAPACITOR,
        UPGRADE,
        MISC
    }

    private final Map<Integer, SlotType> slotTypeMap;
    private final int slotCount;

    private ItemSlotLayout(Map<Integer, SlotType> slotTypeMap) {
        this.slotTypeMap = slotTypeMap;
        this.slotCount = slotTypeMap.size();
    }

    public static ItemSlotLayout basic(int inputs, int outputs) {
        return basic(inputs, outputs, 0);
    }

    public static ItemSlotLayout basic(int inputs, int outputs, int upgrades) {
        Map<Integer, SlotType> slotMap = new HashMap<>();
        for (int i = 0; i < inputs + outputs + upgrades; i++) {
            SlotType type;
            if (i < inputs) {
                type = SlotType.INPUT;
            } else if (i < inputs + outputs) {
                type = SlotType.OUTPUT;
            } else {
                type = SlotType.UPGRADE;
            }

            slotMap.put(i, type);
        }
        return new ItemSlotLayout(slotMap);
    }

    public static ItemSlotLayout withCapacitor(int inputs, int outputs) {
        return withCapacitor(inputs, outputs, 0);
    }

    public static ItemSlotLayout withCapacitor(int inputs, int outputs, int upgrades) {
        Map<Integer, SlotType> slotMap = new HashMap<>();
        for (int i = 0; i < inputs + outputs + upgrades + 1; i++) {
            SlotType type;
            if (i < inputs) {
                type = SlotType.INPUT;
            } else if (i < inputs + outputs) {
                type = SlotType.OUTPUT;
            } else if (i < inputs + outputs + upgrades) {
                type = SlotType.UPGRADE;
            } else {
                type = SlotType.CAPACITOR;
            }

            slotMap.put(i, type);
        }
        return new ItemSlotLayout(slotMap);
    }

    public static ItemSlotLayout fromLegacy(List<Integer> inputs, List<Integer> outputs) {
        return fromLegacy(inputs, outputs, List.of());
    }

    public static ItemSlotLayout fromLegacy(List<Integer> inputs, List<Integer> outputs, List<Integer> upgradeSlots) {
        Map<Integer, SlotType> slotMap = new HashMap<>();
        inputs.forEach(slot -> slotMap.put(slot, SlotType.INPUT));
        outputs.forEach(slot -> slotMap.put(slot, SlotType.OUTPUT));
        upgradeSlots.forEach(slot -> slotMap.put(slot, SlotType.UPGRADE));
        return new ItemSlotLayout(slotMap);
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getSlotCount() {
        return slotCount;
    }

    public boolean slotIs(int slot, SlotType type) {
        return slotTypeMap.getOrDefault(slot, SlotType.MISC) == type;
    }

    public List<Integer> getAll(SlotType type) {
        return slotTypeMap.entrySet().stream().filter(entry -> entry.getValue() == type).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public int getFirst(SlotType type) {
        if (getAll(type).size() > 0) {
            return getAll(type).get(0);
        }
        return -1;
    }

    public static class Builder {
        private final Map<Integer, SlotType> slotTypeMap;

        private Builder() {
            slotTypeMap = new HashMap<>();
        }

        public Builder addSlot(int slot, SlotType type) {
            slotTypeMap.put(slot, type);
            return this;
        }

        public ItemSlotLayout build() {
            // Ensure all slots are accounted for
            List<Integer> slots = new ArrayList<>();
            for (Map.Entry<Integer, SlotType> entry : slotTypeMap.entrySet()) {
                slots.add(entry.getKey());
            }

            // Find max id
            int max = 0;
            for (Integer slot : slots) {
                if (slot > max)
                    max = max;
            }

            if (max != slotTypeMap.size() - 1)
                throw new RuntimeException("DEV: Slot count mismatch!");

            return new ItemSlotLayout(slotTypeMap);
        }
    }
}
