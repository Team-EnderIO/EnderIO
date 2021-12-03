package com.enderio.machines.common.recipe;

import com.enderio.base.EnderIO;
import com.enderio.base.common.recipe.DataGenSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.stream.Stream;

public abstract class MachineRecipe<T extends MachineRecipe<T, C>, C extends Container> implements Recipe<C> {

    @Override
    public abstract DependencyAwareDataGenSerializer<T, C> getSerializer();

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
            .filter(string -> !string.equals("minecraft")) //remove minecraft, forge and enderio
            .filter(string -> !string.equals("forge"))
            .filter(string -> !string.equals(EnderIO.DOMAIN))
            .distinct().toList();
    }

    public abstract static class DependencyAwareDataGenSerializer<T extends MachineRecipe<T, C>, C extends Container> extends DataGenSerializer<T, C> {

        @Override
        public void toJson(T recipe, JsonObject json) {
            List<String> modDependencies = recipe.getModDependencies();
            if (!modDependencies.isEmpty()) {
                JsonArray conditions = new JsonArray();
                for (String modDependency : modDependencies) {
                    conditions.add(CraftingHelper.serialize(new ModLoadedCondition(modDependency)));
                }
                json.add("conditions", conditions);
            }
        }
    }
}
