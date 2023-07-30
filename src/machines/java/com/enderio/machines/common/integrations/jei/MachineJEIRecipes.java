package com.enderio.machines.common.integrations.jei;

import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.integrations.jei.util.WrappedEnchanterRecipe;
import com.enderio.machines.common.integrations.vanilla.VanillaAlloySmeltingRecipe;
import com.enderio.machines.common.recipe.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MachineJEIRecipes {
    private final RecipeManager recipeManager;

    public MachineJEIRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        this.recipeManager = Objects.requireNonNull(level).getRecipeManager();
    }

    public List<AlloySmeltingRecipe> getAlloySmeltingRecipes() {
        return new ArrayList<>(recipeManager.getAllRecipesFor(MachineRecipes.ALLOY_SMELTING.type().get()));
    }

    public List<AlloySmeltingRecipe> getAlloySmeltingRecipesWithSmelting() {
        List<AlloySmeltingRecipe> recipes = new ArrayList<>();
        recipes.addAll(recipeManager.getAllRecipesFor(MachineRecipes.ALLOY_SMELTING.type().get()));
        recipes.addAll(recipeManager.getAllRecipesFor(RecipeType.SMELTING).stream().map(VanillaAlloySmeltingRecipe::new).toList());
        return recipes;
    }

    public List<SlicingRecipe> getSlicingRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.SLICING.type().get());
    }

    public List<SoulBindingRecipe> getSoulBindingRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.SOUL_BINDING.type().get());
    }

    public List<TankRecipe> getTankRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.TANK.type().get());
    }

    public List<WrappedEnchanterRecipe> getEnchanterRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.ENCHANTING.type().get()).stream().<WrappedEnchanterRecipe>mapMulti((recipe, consumer) -> {
            for (int i = 1; i <= recipe.getEnchantment().getMaxLevel(); i++) {
                consumer.accept(new WrappedEnchanterRecipe(recipe, i));
            }
        }).toList();
    }

    public List<SagMillingRecipe> getSagmillingRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.SAGMILLING.type().get());
    }
}
