package com.enderio.api.recipe;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
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

    @Override
    default ItemStack assemble(C container) {
        throw new UnsupportedOperationException("assemble is disabled on custom recipes in favour of craft");
    }

    /**
     * Return a list of all possible inputs for each slot.
     */
    List<List<ItemStack>> getAllInputs();

    // TODO: Fluid ingredient support?

    /**
     * Return a list of each slots' output.
     */
    List<ItemStack> getAllOutputs();

    /**
     * Get a list of other mod dependencies
     */
    default List<ResourceLocation> getMiscModDependencies() {
        return List.of();
    }

    default List<String> getModDependencies() {
        // Generate a list of mod dependencies
        Set<String> mods = new HashSet<>();

        // Get mods for inputs, outputs and other mods
        getIngredients().forEach(ing -> Arrays.stream(ing.getItems()).forEach(itm -> mods.add(itm.getItem().getRegistryName().getNamespace())));
        getAllOutputs().forEach(dep -> mods.add(dep.getItem().getRegistryName().getNamespace()));
        getMiscModDependencies().forEach(dep -> mods.add(dep.getNamespace()));

        // Get which mod owns the recipe type. Makes sure the mod that deserializes the recipe is ignored in the list.
        String owningMod = Registry.RECIPE_TYPE.getKey(getType()).getNamespace();

        return mods.stream().filter(mod -> !StringUtils.equalsAny(mod, "minecraft", "forge", "enderio", owningMod)).toList();
    }
}