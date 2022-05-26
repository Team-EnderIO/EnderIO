package com.enderio.machines.common.integration.jei;

import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.integration.jei.helpers.AlloySmelterFurnaceRecipe;
import com.enderio.machines.common.integration.jei.helpers.EnchanterRecipeWrapper;
import com.enderio.api.recipe.AlloySmeltingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class JEIRecipes {
    private final RecipeManager recipeManager;

    public JEIRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel world = minecraft.level;
        this.recipeManager = world.getRecipeManager();
    }

    public List<AlloySmeltingRecipe> getAlloyingRecipes() {
        Stream<AlloySmeltingRecipe> smelting = getRecipes(recipeManager, RecipeType.SMELTING).stream().map(AlloySmelterFurnaceRecipe::new).map(AlloySmeltingRecipe.class::cast);
        return Stream.concat(getRecipes(recipeManager, MachineRecipes.Types.ALLOY_SMELTING).stream(), smelting).toList();
    }

    public List<EnchanterRecipeWrapper> getEnchantingRecipes() {
        List<EnchanterRecipeWrapper> recipes = new ArrayList<>();
        getRecipes(recipeManager, MachineRecipes.Types.ENCHANTING).forEach(recipe -> {
            for (int i = 1; i <= recipe.getEnchantment().getMaxLevel(); i++) {
                recipes.add(new EnchanterRecipeWrapper(recipe, i));
            }
        });
        return recipes;
    }

    // TODO: might need validity checks or something
    private static <C extends Container, T extends Recipe<C>> List<T> getRecipes(
        RecipeManager recipeManager,
        RecipeType<T> recipeType) {
        return recipeManager.getAllRecipesFor(recipeType);
    }
}