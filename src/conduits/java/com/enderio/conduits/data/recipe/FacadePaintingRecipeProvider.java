package com.enderio.conduits.data.recipe;

import com.enderio.EnderIO;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.core.common.util.JsonUtil;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.function.Consumer;

public class FacadePaintingRecipeProvider extends EnderRecipeProvider {

    public FacadePaintingRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        // Conduit facades - initial painting
        build(ConduitItems.CONDUIT_FACADE, Ingredient.of(ConduitItems.CONDUIT_FACADE), pFinishedRecipeConsumer);
        build(ConduitItems.TRANSPARENT_CONDUIT_FACADE, Ingredient.of(ConduitItems.TRANSPARENT_CONDUIT_FACADE), pFinishedRecipeConsumer);
        build(ConduitItems.HARDENED_CONDUIT_FACADE, Ingredient.of(ConduitItems.HARDENED_CONDUIT_FACADE), pFinishedRecipeConsumer);
        build(ConduitItems.TRANSPARENT_HARDENED_CONDUIT_FACADE, Ingredient.of(ConduitItems.TRANSPARENT_HARDENED_CONDUIT_FACADE), pFinishedRecipeConsumer);
        
        // Conduit facades - repainting
        build(ConduitItems.CONDUIT_FACADE, Ingredient.of(ConduitItems.CONDUIT_FACADE), "_frompainted", pFinishedRecipeConsumer);
        build(ConduitItems.TRANSPARENT_CONDUIT_FACADE, Ingredient.of(ConduitItems.TRANSPARENT_CONDUIT_FACADE), "_frompainted", pFinishedRecipeConsumer);
        build(ConduitItems.HARDENED_CONDUIT_FACADE, Ingredient.of(ConduitItems.HARDENED_CONDUIT_FACADE), "_frompainted", pFinishedRecipeConsumer);
        build(ConduitItems.TRANSPARENT_HARDENED_CONDUIT_FACADE, Ingredient.of(ConduitItems.TRANSPARENT_HARDENED_CONDUIT_FACADE), "_frompainted", pFinishedRecipeConsumer);
    }

    protected void build(ItemLike output, Ingredient input, Consumer<FinishedRecipe> recipeConsumer) {
        build(output, input, "", recipeConsumer);
    }

    protected void build(ItemLike output, Ingredient input, String suffix, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new FinishedPaintingRecipe(EnderIO.loc("painting/" + ForgeRegistries.ITEMS.getKey(output.asItem()).getPath() + suffix), input,
            output.asItem().getDefaultInstance()));
    }

    protected static class FinishedPaintingRecipe extends EnderFinishedRecipe {
        private final Ingredient input;
        private final ItemStack output;

        public FinishedPaintingRecipe(ResourceLocation id, Ingredient input, ItemStack output) {
            super(id);
            this.input = input;
            this.output = output;
        }

        @Override
        protected Set<String> getModDependencies() {
            return Set.of(ForgeRegistries.ITEMS.getKey(output.getItem()).getNamespace());
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("input", input.toJson());
            json.add("output", JsonUtil.serializeItemStackWithoutNBT(output));

            super.serializeRecipeData(json);
        }

        @Override
        public RecipeSerializer<?> getType() {
            // Get the painting recipe serializer by resource location
            RecipeSerializer<?> serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(EnderIO.loc("painting"));
            if (serializer == null) {
                throw new IllegalStateException("Painting recipe serializer not found!");
            }
            return serializer;
        }
    }
}
