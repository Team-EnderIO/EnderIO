package com.enderio.core.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.function.Consumer;

public class FluidFilterSlot extends Slot {

    private static Container emptyInventory = new SimpleContainer(0);
    private final Consumer<FluidStack> consumer;

    public FluidFilterSlot(Consumer<FluidStack> consumer, int pSlot, int pX, int pY) {
        super(emptyInventory, pSlot, pX, pY);
        this.consumer = consumer;
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void set(ItemStack pStack) {
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
        IFluidHandlerItem capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (!stack.isEmpty() && mayPlace(stack) && capability != null) {
            FluidStack ghost = capability.getFluidInTank(0).copy();
            consumer.accept(ghost);
        }

        return stack;
    }
}
