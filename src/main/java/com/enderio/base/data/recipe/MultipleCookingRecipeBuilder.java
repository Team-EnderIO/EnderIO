package com.enderio.base.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MultipleCookingRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final CookingBookCategory bookCategory;
    private final ItemStack result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
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

    public MultipleCookingRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public MultipleCookingRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public Item getResult() {
        return result.getItem();
    }

    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.group == null ? "" : this.group, this.bookCategory, this.ingredient, this.result, this.experience, this.cookingTime, this.advancement, pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.serializer));
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
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final CookingBookCategory category;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

        public Result(ResourceLocation pId, String pGroup, CookingBookCategory pCategory, Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId, RecipeSerializer<? extends AbstractCookingRecipe> pSerializer) {
            this.id = pId;
            this.group = pGroup;
            this.category = pCategory;
            this.ingredient = pIngredient;
            this.result = pResult;
            this.experience = pExperience;
            this.cookingTime = pCookingTime;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
            this.serializer = pSerializer;
        }

        public void serializeRecipeData(JsonObject pJson) {
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }

            pJson.addProperty("category", this.category.getSerializedName());
            pJson.add("ingredient", this.ingredient.toJson());

            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result.getItem()).toString());
            resultObj.addProperty("count", this.result.getCount());
            // TODO: This could support NBT but we likely won't use it.

            pJson.add("result", resultObj);

            pJson.addProperty("experience", this.experience);
            pJson.addProperty("cookingtime", this.cookingTime);
        }

        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.id;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
         * is non-null.
         */
        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
