package com.enderio.api.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IEnderRecipe<R extends IEnderRecipe<R, C>, C extends Container> extends Recipe<C> {
    @Override
    DataGenSerializer<R, C> getSerializer();

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    default String getOwningMod() {
        return getId().getNamespace();
    }

    /**
     * Deprecated in favour of craft which supports multiple slots.
     */
    @Deprecated
    @Override
    default ItemStack assemble(C container) {
        throw new UnsupportedOperationException("assemble is disabled on custom recipes in favour of craft");
    }

    //    List<ItemStack> craft(Container container);

    /**
     * Return a list of all possible inputs for each slot.
     * Used by JEI primarily.
     */
    List<List<ItemStack>> getAllInputs();

    // TODO: Fluid ingredient support?

    /**
     * Return a list of each slots output.
     * Used by JEI primarily.
     */
    List<ItemStack> getAllOutputs();

    default List<ResourceLocation> getOtherDependencies() {
        return List.of();
    }

    default List<String> getModDependencies() {
        Set<String> mods = new HashSet<>();
        // TODO!!!! Rework mod dependency system for inputs.
        //        for (EIOIngredient ingredient : getInputs()) {
        //            for (ItemStack item : ingredient.getItems()) {
        //                String mod = item.getItem().getRegistryName().getNamespace();
        //                mods.add(mod);
        //            }
        //        }

        getAllOutputs().forEach(dep -> mods.add(dep.getItem().getRegistryName().getNamespace()));
        getOtherDependencies().forEach(dep -> mods.add(dep.getNamespace()));

        return mods.stream().filter(mod -> !StringUtils.equalsAny(mod, "minecraft", "forge", "enderio", getOwningMod())).toList();
    }
}