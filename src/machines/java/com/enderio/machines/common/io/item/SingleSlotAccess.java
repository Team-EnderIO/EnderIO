package com.enderio.machines.common.io.item;

import net.minecraft.world.item.ItemStack;

public class SingleSlotAccess {
    private int index = Integer.MIN_VALUE;
    public ItemStack getItemStack(MachineInventory inventory) {
        return inventory.getStackInSlot(index);
    }

    void init(int i) {
        index = i;
    }
}
