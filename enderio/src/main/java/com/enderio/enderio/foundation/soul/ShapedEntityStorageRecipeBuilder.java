package com.enderio.enderio.foundation.soul;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class ShapedEntityStorageRecipeBuilder extends ShapedRecipeBuilder {

    public ShapedEntityStorageRecipeBuilder(RecipeCategory category, ItemLike result, int count) {
        super(category, result, count);
    }

    public static ShapedEntityStorageRecipeBuilder shaped(RecipeCategory category, ItemLike result) {
        return shaped(category, result, 1);
    }

    public static ShapedEntityStorageRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count) {
        return new ShapedEntityStorageRecipeBuilder(category, result, count);
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {

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
        }, recipeId);
    }
}
