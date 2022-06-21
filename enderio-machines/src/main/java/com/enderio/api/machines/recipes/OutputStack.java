package com.enderio.api.machines.recipes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * An output stack for a recipe.
 * This can be either an item or fluid stack.
 */
public class OutputStack {
    private final ItemStack itemStack;
    private final FluidStack fluidStack;

    /**
     * An empty item stack. Neither an item nor a fluid.
     */
    public static OutputStack EMPTY = OutputStack.of(ItemStack.EMPTY);

    /**
     * Create an item output stack.
     */
    public static OutputStack of(ItemStack itemStack) {
        return new OutputStack(itemStack, FluidStack.EMPTY);
    }

    /**
     * Create a fluid output stack.
     */
    public static OutputStack of(FluidStack fluidStack) {
        return new OutputStack(ItemStack.EMPTY, fluidStack);
    }

    private OutputStack(ItemStack itemStack, FluidStack fluidStack) {
        this.itemStack = itemStack;
        this.fluidStack = fluidStack;
    }

    /**
     * @return The item output or {@link ItemStack#EMPTY} if this isn't an item.
     */
    public ItemStack getItem() {
        return itemStack;
    }

    /**
     * @return The fluid output or {@link FluidStack#EMPTY} if this isn't a fluid.
     */
    public FluidStack getFluid() {
        return fluidStack;
    }

    /**
     * @return Whether this is an item output.
     */
    public boolean isItem() {
        return !itemStack.isEmpty();
    }

    /**
     * @return Whether this is a fluid output.
     */
    public boolean isFluid() {
        return fluidStack.isEmpty();
    }

    /**
     * @return Whether this output is completely empty.
     */
    public boolean isEmpty() {
        return itemStack.isEmpty() && fluidStack.isEmpty();
    }

    /**
     * Write to NBT.
     */
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (isItem()) {
            tag.put("item", itemStack.serializeNBT());
        } else if (isFluid()) {
            tag.put("fluid", fluidStack.writeToNBT(new CompoundTag()));
        }
        return tag;
    }

    /**
     * Read from NBT.
     */
    public static OutputStack fromNBT(CompoundTag tag) {
        if (tag.contains("item")) {
            return OutputStack.of(ItemStack.of(tag.getCompound("item")));
        } else if (tag.contains("fluid")) {
            return OutputStack.fromNBT(tag.getCompound("fluid"));
        }
        return OutputStack.EMPTY;
    }
}
