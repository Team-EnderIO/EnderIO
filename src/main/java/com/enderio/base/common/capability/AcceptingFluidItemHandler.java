package com.enderio.base.common.capability;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import java.util.function.Predicate;

/**
 * FluidHandler for Items which only accept specific fluids
 */
public class AcceptingFluidItemHandler extends FluidHandlerItemStack {

    private final Predicate<Fluid> fluidPredicate;

    public AcceptingFluidItemHandler(ItemStack container, int capacity, Fluid validFluid) {
        this(container, capacity, fluid -> fluid == validFluid);
    }

    public AcceptingFluidItemHandler(ItemStack container, int capacity, TagKey<Fluid> validFluid) {
        this(container, capacity, fluid -> fluid.is(validFluid));
    }

    public AcceptingFluidItemHandler(ItemStack container, int capacity, Predicate<Fluid> isFluidValid) {
        super(container, capacity);
        fluidPredicate = isFluidValid;
    }

    @Override
    public void setFluid(FluidStack fluid) {
        super.setFluid(fluid);
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return fluidPredicate.test(fluid.getFluid());
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return fluidPredicate.test(stack.getFluid());
    }
}
