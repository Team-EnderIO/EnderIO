package com.enderio.core.common.recipes;

import com.enderio.core.CoreNBTKeys;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

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
        if (isItem()) {
            return stack.left().get().isEmpty();
        }

        if (isFluid()) {
            return stack.right().get().isEmpty();
        }

        return true;
    }

    // region Serialization

    /**
     * Write to NBT.
     */
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag tag = new CompoundTag();
        if (isItem()) {
            tag.put(CoreNBTKeys.ITEM, stack.left().get().saveOptional(lookupProvider));
        } else if (isFluid()) {
            tag.put(CoreNBTKeys.FLUID, stack.right().get().saveOptional(lookupProvider));
        }
        return tag;
    }

    /**
     * Read from NBT.
     */
    public static OutputStack fromNBT(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        if (tag.contains(CoreNBTKeys.ITEM)) {
            return OutputStack.of(ItemStack.parseOptional(lookupProvider, tag.getCompound(CoreNBTKeys.ITEM)));
        } else if (tag.contains(CoreNBTKeys.FLUID)) {
            return OutputStack.of(FluidStack.parseOptional(lookupProvider, tag.getCompound(CoreNBTKeys.FLUID)));
        }
        return OutputStack.EMPTY;
    }

    // endregion
}
