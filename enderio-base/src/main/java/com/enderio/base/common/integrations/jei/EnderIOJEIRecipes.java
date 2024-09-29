package com.enderio.base.common.integrations.jei;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.common.integrations.jei.helper.FakeGrindingRecipe;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.Objects;

public class EnderIOJEIRecipes {
    private final RecipeManager recipeManager;

    public EnderIOJEIRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        this.recipeManager = Objects.requireNonNull(level).getRecipeManager();
    }

    public List<RecipeHolder<FireCraftingRecipe>> getAllFireCraftingRecipes() {
        return recipeManager.getAllRecipesFor(EIORecipes.FIRE_CRAFTING.type().get()).stream().toList();
    }

    public List<FakeGrindingRecipe> getAllGrindingRecipes() {
        return List.of(
            new FakeGrindingRecipe(
                new SizedIngredient(Ingredient.of(Items.DEEPSLATE, Items.COBBLED_DEEPSLATE), 1),
                SizedIngredient.of(Items.FLINT, 1),
                new ItemStack(EIOItems.GRAINS_OF_INFINITY.get())),
            new FakeGrindingRecipe(
                SizedIngredient.of(Items.COAL, 3),
                SizedIngredient.of(Items.FLINT, 1),
                new ItemStack(EIOItems.POWDERED_COAL.get()))
        );
    }
}
