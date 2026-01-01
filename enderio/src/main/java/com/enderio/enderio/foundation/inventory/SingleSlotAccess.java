package com.enderio.enderio.foundation.inventory;

import com.enderio.enderio.foundation.block.entity.MachineInventoryHolder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SingleSlotAccess {
    private int index = Integer.MIN_VALUE;

    public ItemStack getItemStack(MachineInventoryHolder blockEntity) {
        if (!blockEntity.hasInventory()) {
            throw new IllegalArgumentException("BlockEntity does not have an inventory");
        }

        return getStack(blockEntity.getInventory());
    }

    public ItemResource getResource(MachineInventory inventory) {
        return inventory.getResource(index);
    }

    public ItemStack getStack(MachineInventory inventory) {
        return inventory.getStack(index);
    }

    public int insert(MachineInventory inventory, ItemResource resource, int count, TransactionContext transaction) {
        return inventory.insert(index, resource, count, transaction);
    }

    public int insert(MachineInventoryHolder machine, ItemResource resource, int count, TransactionContext transaction) {

        if (!machine.hasInventory()) {
            throw new IllegalArgumentException("BlockEntity does not have an inventory");
        }
        return insert(machine.getInventory(), resource, count, transaction);
    }

    public void setStackInSlot(MachineInventory inventory, ItemStack itemStack) {
        inventory.set(index, ItemResource.of(itemStack), itemStack.getCount());
    }

    public void setStackInSlot(MachineInventoryHolder machine, ItemStack itemStack) {
        if (!machine.hasInventory()) {
            throw new IllegalArgumentException("BlockEntity does not have an inventory");
        }

        setStackInSlot(machine.getInventory(), itemStack);
    }

    public boolean isSlot(int slot) {
        return this.index == slot;
    }

    public int getIndex() {
        return index;
    }

    void init(int i) {
        if (index == Integer.MIN_VALUE) {
            index = i;
        } else if (index != i) {
            throw new IllegalArgumentException(
                    "InventoryLayout changed dynamically from " + index + " to " + i + ", don't do that");
        }
    }

    public MultiSlotAccess wrapToMulti() {
        return MultiSlotAccess.wrap(this);
    }
}
