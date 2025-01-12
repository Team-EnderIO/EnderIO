package com.enderio.machines.common.integrations.jei;

import com.enderio.machines.common.blocks.alloy.AlloySmeltingRecipe;
import com.enderio.machines.common.blocks.fluid_tank.TankRecipe;
import com.enderio.machines.common.blocks.sag_mill.SagMillingRecipe;
import com.enderio.machines.common.blocks.slicer.SlicingRecipe;
import com.enderio.machines.common.blocks.soul_binder.SoulBindingRecipe;
import com.enderio.machines.common.blocks.vat.FermentingRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.integrations.jei.util.WrappedEnchanterRecipe;
import com.enderio.machines.common.souldata.EngineSoul;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

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

    public List<RecipeHolder<AlloySmeltingRecipe>> getAlloySmeltingRecipes() {
        List<RecipeHolder<AlloySmeltingRecipe>> recipes = new ArrayList<>();
        recipes.addAll(recipeManager.getAllRecipesFor(MachineRecipes.ALLOY_SMELTING.type().get()));
        return recipes;
    }

    public List<RecipeHolder<SlicingRecipe>> getSlicingRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.SLICING.type().get());
    }

    public List<RecipeHolder<SoulBindingRecipe>> getSoulBindingRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.SOUL_BINDING.type().get());
    }

    public List<RecipeHolder<TankRecipe>> getTankRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.TANK.type().get());
    }

    public List<WrappedEnchanterRecipe> getEnchanterRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.ENCHANTING.type().get())
                .stream()
                .<WrappedEnchanterRecipe>mapMulti((recipe, consumer) -> {
                    for (int i = 1; i <= recipe.value().enchantment().value().getMaxLevel(); i++) {
                        consumer.accept(new WrappedEnchanterRecipe(recipe, i));
                    }
                })
                .toList();
    }

    public List<RecipeHolder<SagMillingRecipe>> getSagMillingRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.SAG_MILLING.type().get());
    }

    public List<EngineSoul.SoulData> getMobGeneratorRecipes() {
        return EngineSoul.ENGINE.map.values().stream().toList();
    }

    public List<RecipeHolder<FermentingRecipe>> getVATRecipes() {
        return recipeManager.getAllRecipesFor(MachineRecipes.VAT_FERMENTING.type().get());
    }
}
