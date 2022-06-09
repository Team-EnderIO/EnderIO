package com.enderio.api.machines.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class OutputStack {
    ItemStack itemStack;
    FluidStack fluidStack;

    public static OutputStack of(ItemStack itemStack) {
        return new OutputStack(itemStack, FluidStack.EMPTY);
    }

    public static OutputStack of(FluidStack fluidStack) {
        return new OutputStack(ItemStack.EMPTY, fluidStack);
    }

    private OutputStack(ItemStack itemStack, FluidStack fluidStack) {
        this.itemStack = itemStack;
        this.fluidStack = fluidStack;
    }

    public ItemStack getItem() {
        return itemStack;
    }

    public FluidStack getFluid() {
        return fluidStack;
    }

    public boolean isItem() {
        return !itemStack.isEmpty();
    }

    public boolean isFluid() {
        return fluidStack.isEmpty();
    }

    public boolean isEmpty() {
        return itemStack.isEmpty() && fluidStack.isEmpty();
    }
}
