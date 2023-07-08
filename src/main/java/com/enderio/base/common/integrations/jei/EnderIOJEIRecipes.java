package com.enderio.base.common.integrations.jei;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.common.integrations.jei.helper.FakeGrindingRecipe;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.core.common.recipes.CountedIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnderIOJEIRecipes {
    private final RecipeManager recipeManager;

    public EnderIOJEIRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        this.recipeManager = Objects.requireNonNull(level).getRecipeManager();
    }

    public List<FireCraftingRecipe> getAllFireCraftingRecipes() {
        return recipeManager.getAllRecipesFor(EIORecipes.FIRE_CRAFTING.type().get());
    }

    public List<FakeGrindingRecipe> getAllGrindingRecipes() {
        return List.of(
            new FakeGrindingRecipe(
                CountedIngredient.of(Items.DEEPSLATE, Items.COBBLED_DEEPSLATE),
                CountedIngredient.of(Items.FLINT),
                new ItemStack(EIOItems.GRAINS_OF_INFINITY)),
            new FakeGrindingRecipe(
                CountedIngredient.of(3, Items.COAL),
                CountedIngredient.of(Items.FLINT),
                new ItemStack(EIOItems.POWDERED_COAL))
        );
    }
}
