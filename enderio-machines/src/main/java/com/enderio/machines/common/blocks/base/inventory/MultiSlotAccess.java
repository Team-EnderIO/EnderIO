package com.enderio.machines.common.blocks.base.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.item.ItemStack;

public class MultiSlotAccess {
    private List<SingleSlotAccess> accesses = null;

    void init(int index, int size) {
        List<SingleSlotAccess> accesses = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SingleSlotAccess access = new SingleSlotAccess();
            access.init(i + index);
            accesses.add(access);
        }
        this.accesses = accesses;
    }

    public int size() {
        return accesses.size();
    }

    public SingleSlotAccess get(int index) {
        return accesses.get(index);
    }

    public List<SingleSlotAccess> getAccesses() {
        return accesses;
    }

    public boolean contains(int slotIndex) {
        return slotIndex >= accesses.get(0).getIndex() && slotIndex <= accesses.get(accesses.size() - 1).getIndex();
    }

    public List<ItemStack> getItemStacks(MachineInventory inventory) {
        return accesses.stream().map(access -> access.getItemStack(inventory)).collect(Collectors.toList());
    }

    public static MultiSlotAccess wrap(SingleSlotAccess access) {
        MultiSlotAccess multi = new MultiSlotAccess();
        multi.accesses = List.of(access);
        return multi;
    }
}
