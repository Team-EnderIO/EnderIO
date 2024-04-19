package com.enderio.base.common.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilterSlot extends Slot {

    private final NonNullList<ItemStack> items;

    public FilterSlot(NonNullList<ItemStack> items, int pSlot, int pX, int pY) {
        super(null, pSlot, pX, pY);
        this.items = items;
    }

    @Override
    public ItemStack getItem() {
        return items.get(getSlotIndex());
    }

    @Override
    public void set(ItemStack pStack) {
        items.set(getSlotIndex(), pStack);
        setChanged();
    }

    @Override
    public void setChanged() {

    }

    @Override
    public ItemStack remove(int pAmount) {
        set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public int getMaxStackSize() {
        return getItem().getMaxStackSize();
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int amount) {
        // If this stack is valid, set the inventory slot value.
        if (!stack.isEmpty() && mayPlace(stack)) {
            ItemStack ghost = stack.copy();
            ghost.setCount(Math.min(ghost.getCount(), this.getMaxStackSize()));
            set(ghost);
        }

        return stack;
    }
}
