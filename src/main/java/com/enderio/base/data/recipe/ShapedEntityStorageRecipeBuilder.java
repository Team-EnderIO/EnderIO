package com.enderio.base.data.recipe;

import com.enderio.base.common.init.EIORecipes;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
            public void accept(FinishedRecipe finishedRecipe, ICondition... iConditions) {
                recipeOutput.accept(new Result((ShapedRecipeBuilder.Result) finishedRecipe));
            }

            @Override
            public Advancement.Builder advancement() {
                return recipeOutput.advancement();
            }
        });
    }

    public static class Result implements FinishedRecipe {

        private ShapedRecipeBuilder.Result wrapped;

        public Result(ShapedRecipeBuilder.Result wrapped) {
            super();
            this.wrapped = wrapped;
        }

        @Override
        public RecipeSerializer<?> type() {
            return EIORecipes.SHAPED_ENTITY_STORAGE.get();
        }

        @Nullable
        @Override
        public AdvancementHolder advancement() {
            return wrapped.advancement();
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            wrapped.serializeRecipeData(json);
        }

        @Override
        public ResourceLocation id() {
            return wrapped.id();
        }
    }
}
