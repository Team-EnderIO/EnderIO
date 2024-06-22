package com.enderio.core.common.recipes;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * An output stack for a recipe.
 * This can be either an item or fluid stack.
 */
public record OutputStack(Either<FluidStack, ItemStack> stack) {

    public static Codec<OutputStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
       Codec.either(FluidStack.CODEC, ItemStack.OPTIONAL_CODEC).fieldOf("stack").forGetter(OutputStack::stack)
    ).apply(instance, OutputStack::new));

    /**
     * An empty item stack. Neither an item nor a fluid.
     */
    public static final OutputStack EMPTY = OutputStack.of(ItemStack.EMPTY);

    /**
     * Create an item output stack.
     */
    public static OutputStack of(ItemStack itemStack) {
        return new OutputStack(Either.right(itemStack));
    }

    /**
     * Create a fluid output stack.
     */
    public static OutputStack of(FluidStack fluidStack) {
        return new OutputStack(Either.left(fluidStack));
    }

    /**
     * @return The item output or {@link ItemStack#EMPTY} if this isn't an item.
     */
    public ItemStack getItem() {
        return stack.right().orElse(ItemStack.EMPTY);
    }

    /**
     * @return The fluid output or {@link FluidStack#EMPTY} if this isn't a fluid.
     */
    public FluidStack getFluid() {
        return stack.left().orElse(FluidStack.EMPTY);
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

//    /**
//     * Write to NBT.
//     */
//    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
//        CompoundTag tag = new CompoundTag();
//        if (isItem()) {
//            tag.put(CoreNBTKeys.ITEM, stack.left().get().saveOptional(lookupProvider));
//        } else if (isFluid()) {
//            tag.put(CoreNBTKeys.FLUID, stack.right().get().saveOptional(lookupProvider));
//        }
//        return tag;
//    }
//
//    /**
//     * Read from NBT.
//     */
//    public static OutputStack fromNBT(HolderLookup.Provider lookupProvider, CompoundTag tag) {
//        if (tag.contains(CoreNBTKeys.ITEM)) {
//            return OutputStack.of(ItemStack.parseOptional(lookupProvider, tag.getCompound(CoreNBTKeys.ITEM)));
//        } else if (tag.contains(CoreNBTKeys.FLUID)) {
//            return OutputStack.of(FluidStack.parseOptional(lookupProvider, tag.getCompound(CoreNBTKeys.FLUID)));
//        }
//        return OutputStack.EMPTY;
//    }

    // endregion
}
