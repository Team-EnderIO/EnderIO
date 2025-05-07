package com.enderio.base.common.filter;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class FilterSlot<T> extends Slot {
    private static final Container EMPTY_INVENTORY = new SimpleContainer(0);
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    public FilterSlot(Supplier<T> getter, Consumer<T> setter, int slot, int x, int y) {
        super(EMPTY_INVENTORY, slot, x, y);
        this.getter = getter;
        this.setter = setter;
    }

    protected abstract T emptyResource();

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
        // TODO: Check this behaviour.
        getResourceFrom(stack).ifPresent(this::setResource);
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

    public final T getResource() {
        return getter.get();
    }

    public final boolean isEmpty() {
        return getResource().equals(emptyResource());
    }

    public final void setResource(T resource) {
        setter.accept(processResource(resource));
    }

    public final void clearResource() {
        setResource(emptyResource());
    }

    public abstract Optional<T> getResourceFrom(ItemStack itemStack);

    public T processResource(T resource) {
        return resource;
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int increment) {
        if (!stack.isEmpty() && mayPlace(stack)) {
            getResourceFrom(stack).ifPresent(this::setResource);
        }

        return stack;
    }
}
