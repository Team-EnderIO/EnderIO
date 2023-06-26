package com.enderio.base.common.integrations.jei;

import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;

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
}
