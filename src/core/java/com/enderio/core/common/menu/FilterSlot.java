package com.enderio.core.common.menu;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class FilterSlot<T> extends Slot {
    private static final Container EMPTY_INVENTORY = new SimpleContainer(0);
    private final Consumer<T> consumer;

    public FilterSlot(Consumer<T> consumer, int slot, int x, int y) {
        super(EMPTY_INVENTORY, slot, x, y);
        this.consumer = consumer;
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public int getMaxStackSize() {
        return getItem().getMaxStackSize();
    }

    @Override
    public void set(ItemStack stack) {
        setChanged();
    }

    @Override
    public ItemStack remove(int amount) {
        set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public void setChanged() {
    }

    public final void setResource(T resource) {
        consumer.accept(processResource(resource));
    }

    public abstract Optional<T> getResourceFrom(ItemStack itemStack);

    public T processResource(T resource) {
        return resource;
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int increment) {
        if (!stack.isEmpty() && mayPlace(stack)) {
            getResourceFrom(stack).ifPresent(resource -> consumer.accept(processResource(resource)));
        }

        return stack;
    }
}
