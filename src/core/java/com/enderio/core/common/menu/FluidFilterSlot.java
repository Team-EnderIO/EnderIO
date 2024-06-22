package com.enderio.core.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

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
        LazyOptional<IFluidHandlerItem> capability = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        capability.ifPresent(fluidHandler -> {
            if (!stack.isEmpty() && mayPlace(stack)) {
                FluidStack ghost = fluidHandler.getFluidInTank(0).copy();
                consumer.accept(ghost);
            }
        });

        return stack;
    }
}
