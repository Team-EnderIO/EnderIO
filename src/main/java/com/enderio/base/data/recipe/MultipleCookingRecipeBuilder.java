package com.enderio.base.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MultipleCookingRecipeBuilder implements RecipeBuilder {

    private final ItemStack result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    @Nullable
    private String group;
    private final SimpleCookingSerializer<?> serializer;

    private MultipleCookingRecipeBuilder(ItemStack result, Ingredient ingredient, float experience, int cookingTime,
        SimpleCookingSerializer<?> serializer) {
        this.result = result;
        this.ingredient = ingredient;
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.serializer = serializer;
    }

    public static MultipleCookingRecipeBuilder cooking(Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime, SimpleCookingSerializer<?> pSerializer) {
        return new MultipleCookingRecipeBuilder(pResult, pIngredient, pExperience, pCookingTime, pSerializer);
    }

    public static MultipleCookingRecipeBuilder campfireCooking(Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime) {
        return cooking(pIngredient, pResult, pExperience, pCookingTime, RecipeSerializer.CAMPFIRE_COOKING_RECIPE);
    }

    public static MultipleCookingRecipeBuilder blasting(Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime) {
        return cooking(pIngredient, pResult, pExperience, pCookingTime, RecipeSerializer.BLASTING_RECIPE);
    }

    public static MultipleCookingRecipeBuilder smelting(Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime) {
        return cooking(pIngredient, pResult, pExperience, pCookingTime, RecipeSerializer.SMELTING_RECIPE);
    }

    public static MultipleCookingRecipeBuilder smoking(Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime) {
        return cooking(pIngredient, pResult, pExperience, pCookingTime, RecipeSerializer.SMOKING_RECIPE);
    }

    public MultipleCookingRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public MultipleCookingRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(
            RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.group == null ? "" : this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.advancement, new ResourceLocation(pRecipeId.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + pRecipeId.getPath()), this.serializer));
    }

    private void ensureValid(ResourceLocation pId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

        public Result(ResourceLocation pId, String pGroup, Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId, RecipeSerializer<? extends AbstractCookingRecipe> pSerializer) {
            this.id = pId;
            this.group = pGroup;
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
