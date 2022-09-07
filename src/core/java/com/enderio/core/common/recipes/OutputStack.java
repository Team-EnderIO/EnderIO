package com.enderio.core.common.recipes;

import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * An output stack for a recipe.
 * This can be either an item or fluid stack.
 */
public record OutputStack(Either<ItemStack, FluidStack> stack) {

    /**
     * An empty item stack. Neither an item nor a fluid.
     */
    public static final OutputStack EMPTY = OutputStack.of(ItemStack.EMPTY);

    /**
     * Create an item output stack.
     */
    public static OutputStack of(ItemStack itemStack) {
        return new OutputStack(Either.left(itemStack));
    }

    /**
     * Create a fluid output stack.
     */
    public static OutputStack of(FluidStack fluidStack) {
        return new OutputStack(Either.right(fluidStack));
    }

    /**
     * @return The item output or {@link ItemStack#EMPTY} if this isn't an item.
     */
    public ItemStack getItem() {
        return stack.left().orElse(ItemStack.EMPTY);
    }

    /**
     * @return The fluid output or {@link FluidStack#EMPTY} if this isn't a fluid.
     */
    public FluidStack getFluid() {
        return stack.right().orElse(FluidStack.EMPTY);
    }

    /**
     * @return Whether this is an item output.
     */
    public boolean isItem() {
        return stack.left().isPresent();
    }

    /**
     * @return Whether this is a fluid output.
     */
    public boolean isFluid() {
        return stack.right().isPresent();
    }

    /**
     * @return Whether this output is completely empty.
     */
    public boolean isEmpty() {
        if (isItem())
            return stack.left().get().isEmpty();
        if (isFluid())
            return stack.right().get().isEmpty();
        return true;
    }

    /**
     * Write to NBT.
     */
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (isItem()) {
            tag.put("item", stack.left().get().serializeNBT());
        } else if (isFluid()) {
            tag.put("fluid", stack.right().get().writeToNBT(new CompoundTag()));
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
