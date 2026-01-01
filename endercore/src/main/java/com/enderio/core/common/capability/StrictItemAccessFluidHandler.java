package com.enderio.core.common.capability;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;

import java.util.function.Predicate;

/**
 * FluidHandler for Items which only accept specific fluids
 */
public class StrictItemAccessFluidHandler extends ItemAccessFluidHandler {

    private final Predicate<Fluid> fluidPredicate;

    public StrictItemAccessFluidHandler(ItemAccess itemAccess, DataComponentType<SimpleFluidContent> component, int capacity, Fluid validFluid) {
        this(itemAccess, component, capacity, fluid -> fluid == validFluid);
    }

    public StrictItemAccessFluidHandler(ItemAccess itemAccess, DataComponentType<SimpleFluidContent> component, int capacity, TagKey<Fluid> validFluidTag) {
        this(itemAccess, component, capacity, fluid -> fluid.is(validFluidTag));
    }

    public StrictItemAccessFluidHandler(ItemAccess itemAccess, DataComponentType<SimpleFluidContent> component, int capacity, Predicate<Fluid> isFluidValid) {
        super(itemAccess, component, capacity);
        fluidPredicate = isFluidValid;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return fluidPredicate.test(resource.getFluid()) && super.isValid(index, resource);
    }
}
