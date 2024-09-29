package com.enderio.machines.common.integrations.jei;

import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.integrations.jei.util.WrappedEnchanterRecipe;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import com.enderio.machines.common.recipe.SagMillingRecipe;
import com.enderio.machines.common.recipe.SlicingRecipe;
import com.enderio.machines.common.recipe.SoulBindingRecipe;
import com.enderio.machines.common.recipe.TankRecipe;
import com.enderio.machines.common.souldata.EngineSoul;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;

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
        return recipeManager.getAllRecipesFor(MachineRecipes.ALLOY_SMELTING.type().get()).stream().filter(recipe -> !recipe.isSmelting()).toList();
    }

    public List<AlloySmeltingRecipe> getAlloySmeltingRecipesWithSmelting() {
        return recipeManager.getAllRecipesFor(MachineRecipes.ALLOY_SMELTING.type().get());
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

    public List<SagMillingRecipe> getSagMillingRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.SAG_MILLING.type().get());
    }

    public List<EngineSoul.SoulData> getMobGeneratorRecipes() {
        return EngineSoul.ENGINE.map.values().stream().toList();
    }
}
