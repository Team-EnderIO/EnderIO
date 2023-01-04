package com.enderio.machines.common.io.item;

import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class SingleSlotAccess {
    private int index = Integer.MIN_VALUE;

    public ItemStack getItemStack(MachineBlockEntity blockEntity) {
        return getItemStack(blockEntity.getInventory());
    }
    public ItemStack getItemStack(MachineInventory inventory) {
        return inventory.getStackInSlot(index);
    }

    public ItemStack getItemStack(Container container) {
        return container.getItem(index);
    }
    public ItemStack insertItem(MachineInventory inventory, ItemStack itemStack, boolean simulate) {
        return inventory.insertItem(index, itemStack, simulate);
    }
    public ItemStack insertItem(MachineBlockEntity machine, ItemStack itemStack, boolean simulate) {
        return insertItem(machine, itemStack, simulate);
    }
    public void setStackInSlot(MachineInventory inventory, ItemStack itemStack) {
        inventory.setStackInSlot(index, itemStack);
    }
    public void setStackInSlot(MachineBlockEntity machine, ItemStack itemStack) {
        setStackInSlot(machine.getInventory(), itemStack);
    }

    public boolean isSlot(int slot) {
        return this.index == slot;
    }
    void init(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }

    public MultiSlotAccess wrapToMulti() {
        return MultiSlotAccess.wrap(this);
    }
}
