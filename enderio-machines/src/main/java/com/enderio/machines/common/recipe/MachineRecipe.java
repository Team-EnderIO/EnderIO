package com.enderio.machines.common.recipe;

import com.enderio.base.EnderIO;
import com.enderio.base.common.recipe.DataGenSerializer;
import com.enderio.machines.EIOMachines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Stream;

public abstract class MachineRecipe<T extends MachineRecipe<T, C>, C extends Container> implements Recipe<C> {

    @Override
    public abstract DataGenSerializer<T, C> getSerializer();

    protected Stream<ItemStack> getItemsInRecipe() {
        return Stream.empty();
    }

    protected Stream<FluidStack> getFluidsInRecipe() {
        return Stream.empty();
    }

    protected Stream<ResourceLocation> getOtherResourceLocations() {
        return Stream.empty();
    }

    public final List<String> getModDependencies() {
        return Stream.concat(Stream.concat(getItemsInRecipe().map(stack -> stack.getItem().getRegistryName()),
            getFluidsInRecipe().map(fluidStack -> fluidStack.getFluid().getRegistryName())),
            getOtherResourceLocations()) //merge all resourcelocations for this recipe
            .map(ResourceLocation::getNamespace) //get their origin
            .filter(string -> !StringUtils.equalsAny(string, "minecraft", "forge", EnderIO.MODID, EIOMachines.MODID)) //remove minecraft, forge and enderio
            .distinct().toList();
    }

    public int getEnergyCost() {
        return 0;
    }
}
