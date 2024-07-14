package com.enderio.base.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemFilterSlot extends Slot {

    private static Container emptyInventory = new SimpleContainer(0);
    private final Supplier<ItemStack> item;
    private final Consumer<ItemStack> consumer;

    public ItemFilterSlot(Supplier<ItemStack> item, Consumer<ItemStack> consumer, int pSlot, int pX, int pY) {
        super(emptyInventory, pSlot, pX, pY);
        this.item = item;
        this.consumer = consumer;
    }

    @Override
    public ItemStack getItem() {
        return item.get();
    }

    @Override
    public void set(ItemStack pStack) {
        consumer.accept(pStack);
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
