package com.enderio.core.common.capability;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

/**
 * FluidHandler for Items which only accept specific fluids
 */
public class StrictFluidHandlerItemStack extends FluidHandlerItemStack {

    private final Predicate<Fluid> fluidPredicate;

    public StrictFluidHandlerItemStack(Supplier<DataComponentType<SimpleFluidContent>> componentType,
            ItemStack container, int capacity, Fluid validFluid) {

        this(componentType, container, capacity, fluid -> fluid == validFluid);
    }

    public StrictFluidHandlerItemStack(Supplier<DataComponentType<SimpleFluidContent>> componentType,
            ItemStack container, int capacity, TagKey<Fluid> validFluid) {

        this(componentType, container, capacity, fluid -> fluid.is(validFluid));
    }

    public StrictFluidHandlerItemStack(Supplier<DataComponentType<SimpleFluidContent>> componentType,
            ItemStack container, int capacity, Predicate<Fluid> isFluidValid) {

        super(componentType, container, capacity);
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
