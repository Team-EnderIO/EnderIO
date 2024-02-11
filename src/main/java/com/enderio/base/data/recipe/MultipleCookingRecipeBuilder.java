package com.enderio.base.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class MultipleCookingRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final CookingBookCategory bookCategory;
    private final ItemStack result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap();
    @Nullable
    private String group;
    private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

    private MultipleCookingRecipeBuilder(RecipeCategory pCategory, CookingBookCategory pBookCategory, ItemStack result, Ingredient ingredient, float experience, int cookingTime,
        RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
        this.category = pCategory;
        this.bookCategory = pBookCategory;
        this.result = result;
        this.ingredient = ingredient;
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.serializer = serializer;
    }

    public static MultipleCookingRecipeBuilder generic(Ingredient pIngredient, RecipeCategory pCategory, ItemStack pResult, float pExperience, int pCookingTime, RecipeSerializer<? extends AbstractCookingRecipe> pSerializer) {
        return new MultipleCookingRecipeBuilder(pCategory, determineRecipeCategory(pSerializer, pResult), pResult, pIngredient, pExperience, pCookingTime, pSerializer);
    }

    public static MultipleCookingRecipeBuilder campfireCooking(Ingredient pIngredient, RecipeCategory pCategory, ItemStack pResult, float pExperience, int pCookingTime) {
        return new MultipleCookingRecipeBuilder(pCategory, CookingBookCategory.FOOD, pResult, pIngredient, pExperience, pCookingTime, RecipeSerializer.CAMPFIRE_COOKING_RECIPE);
    }

    public static MultipleCookingRecipeBuilder blasting(Ingredient pIngredient, RecipeCategory pCategory, ItemStack pResult, float pExperience, int pCookingTime) {
        return new MultipleCookingRecipeBuilder(pCategory, determineBlastingRecipeCategory(pResult), pResult, pIngredient, pExperience, pCookingTime, RecipeSerializer.BLASTING_RECIPE);
    }

    public static MultipleCookingRecipeBuilder smelting(Ingredient pIngredient, RecipeCategory pCategory, ItemStack pResult, float pExperience, int pCookingTime) {
        return new MultipleCookingRecipeBuilder(pCategory, determineSmeltingRecipeCategory(pResult), pResult, pIngredient, pExperience, pCookingTime, RecipeSerializer.SMELTING_RECIPE);
    }

    public static MultipleCookingRecipeBuilder smoking(Ingredient pIngredient, RecipeCategory pCategory, ItemStack pResult, float pExperience, int pCookingTime) {
        return new MultipleCookingRecipeBuilder(pCategory, CookingBookCategory.FOOD, pResult, pIngredient, pExperience, pCookingTime, RecipeSerializer.SMOKING_RECIPE);
    }

    public MultipleCookingRecipeBuilder unlockedBy(String p_176792_, Criterion<?> p_300970_) {
        this.criteria.put(p_176792_, p_300970_);
        return this;
    }

    public MultipleCookingRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public Item getResult() {
        return result.getItem();
    }

    public void save(RecipeOutput recipeOutput, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.ensureValid(pRecipeId);
        Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(
            AdvancementRewards.Builder.recipe(pRecipeId)).requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);
        recipeOutput.accept(new Result(pRecipeId, this.group == null ? "" : this.group, this.bookCategory, this.ingredient, this.result, this.experience, this.cookingTime, advancementBuilder.build(pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/")), this.serializer));
    }

    // TODO: 1.19.4: ATs for this
    private static CookingBookCategory determineSmeltingRecipeCategory(ItemStack pResult) {
        if (pResult.getItem().isEdible()) {
            return CookingBookCategory.FOOD;
        } else {
            return pResult.getItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
        }
    }

    private static CookingBookCategory determineBlastingRecipeCategory(ItemStack pResult) {
        return pResult.getItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
    }

    private static CookingBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> pSerializer, ItemStack pResult) {
        if (pSerializer == RecipeSerializer.SMELTING_RECIPE) {
            return determineSmeltingRecipeCategory(pResult);
        } else if (pSerializer == RecipeSerializer.BLASTING_RECIPE) {
            return determineBlastingRecipeCategory(pResult);
        } else if (pSerializer != RecipeSerializer.SMOKING_RECIPE && pSerializer != RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
            throw new IllegalStateException("Unknown cooking recipe type");
        } else {
            return CookingBookCategory.FOOD;
        }
    }

    private void ensureValid(ResourceLocation pId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
    }

    public static record Result(ResourceLocation id, String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime, AdvancementHolder advancement, RecipeSerializer<? extends AbstractCookingRecipe> type) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }

            pJson.addProperty("category", this.category.getSerializedName());
            pJson.add("ingredient", this.ingredient.toJson(false));

            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result.getItem()).toString());
            resultObj.addProperty("count", this.result.getCount());
            // This could support NBT but we likely won't use it.

            pJson.add("result", resultObj);

            pJson.addProperty("experience", this.experience);
            pJson.addProperty("cookingtime", this.cookingTime);
        }

        @Override
        public RecipeSerializer<? extends AbstractCookingRecipe> type() {
            return this.type;
        }

        @Override
        public ResourceLocation id() {
            return this.id;
        }

        @Override
        public AdvancementHolder advancement() {
            return advancement;
        }
    }
}
