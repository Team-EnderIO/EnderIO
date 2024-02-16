package com.enderio.base.data.recipe;

import com.enderio.base.common.recipe.ShapedEntityStorageRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class ShapedEntityStorageRecipeBuilder extends ShapedRecipeBuilder {

    public ShapedEntityStorageRecipeBuilder(RecipeCategory pCategory, ItemLike pResult, int pCount) {
        super(pCategory, pResult, pCount);
    }

    public static ShapedEntityStorageRecipeBuilder shaped(RecipeCategory pCategory, ItemLike pResult) {
        return shaped(pCategory, pResult, 1);
    }

    public static ShapedEntityStorageRecipeBuilder shaped(RecipeCategory pCategory, ItemLike pResult, int pCount) {
        return new ShapedEntityStorageRecipeBuilder(pCategory, pResult, pCount);
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation pRecipeId) {

        super.save(new RecipeOutput() {
            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
                if (recipe instanceof ShapedRecipe shapedRecipe) {
                    recipeOutput.accept(id, new ShapedEntityStorageRecipe(shapedRecipe), advancement, conditions);
                }
            }

            @Override
            public Advancement.Builder advancement() {
                return recipeOutput.advancement();
            }
        }, pRecipeId);
    }
}
