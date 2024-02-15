package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.data.recipe.RecipeDataUtil;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TankRecipeProvider extends EnderRecipeProvider {

    public TankRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // TODO: Tag support for tank recipes.
        buildEmptying(Ingredient.of(Items.EXPERIENCE_BOTTLE), Items.GLASS_BOTTLE, new FluidStack(EIOFluids.XP_JUICE.getSource(), 250), recipeOutput);
        buildFilling(Ingredient.of(Items.GLASS_BOTTLE), Items.EXPERIENCE_BOTTLE, new FluidStack(EIOFluids.XP_JUICE.getSource(), 250), recipeOutput);

        buildEmptying(Ingredient.of(Items.WET_SPONGE), Items.SPONGE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.SPONGE), Items.WET_SPONGE, new FluidStack(Fluids.WATER, 1000), recipeOutput);

        buildFilling(Ingredient.of(Items.STICK), EIOItems.NUTRITIOUS_STICK, new FluidStack(EIOFluids.NUTRIENT_DISTILLATION.getSource(), 1000), recipeOutput);

        buildFilling(Ingredient.of(Items.WHITE_CONCRETE_POWDER), Items.WHITE_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.ORANGE_CONCRETE_POWDER), Items.ORANGE_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.MAGENTA_CONCRETE_POWDER), Items.MAGENTA_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.LIGHT_BLUE_CONCRETE_POWDER), Items.LIGHT_BLUE_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.YELLOW_CONCRETE_POWDER), Items.YELLOW_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.LIME_CONCRETE_POWDER), Items.LIME_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.PINK_CONCRETE_POWDER), Items.PINK_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.GRAY_CONCRETE_POWDER), Items.GRAY_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.LIGHT_GRAY_CONCRETE_POWDER), Items.LIGHT_GRAY_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CYAN_CONCRETE_POWDER), Items.CYAN_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.PURPLE_CONCRETE_POWDER), Items.PURPLE_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.BLUE_CONCRETE_POWDER), Items.BLUE_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.BROWN_CONCRETE_POWDER), Items.BROWN_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.GREEN_CONCRETE_POWDER), Items.GREEN_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.RED_CONCRETE_POWDER), Items.RED_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.BLACK_CONCRETE_POWDER), Items.BLACK_CONCRETE, new FluidStack(Fluids.WATER, 1000), recipeOutput);
    }

    protected void buildEmptying(Ingredient input, ItemLike output, FluidStack fluid, RecipeOutput recipeOutput) {
        recipeOutput.accept(new FinishedTankRecipe(EnderIO.loc("tank_empty/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath()), input, output.asItem(), fluid, true));
    }

    protected void buildFilling(Ingredient input, ItemLike output, FluidStack fluid, RecipeOutput recipeOutput) {
        recipeOutput.accept(new FinishedTankRecipe(EnderIO.loc("tank_fill/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath()), input, output.asItem(), fluid, false));
    }

    protected static class FinishedTankRecipe extends EnderFinishedRecipe {

        private final Ingredient input;
        private final Item output;
        private final FluidStack fluid;
        private final boolean isEmptying;

        protected FinishedTankRecipe(ResourceLocation id, Ingredient input, Item output, FluidStack fluid, boolean isEmptying) {
            super(id);
            this.input = input;
            this.output = output;
            this.fluid = fluid;
            this.isEmptying = isEmptying;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("input", input.toJson(false));
            json.addProperty("output", BuiltInRegistries.ITEM.getKey(output).toString());

            JsonObject fluidJson = new JsonObject();
            fluidJson.addProperty("fluid", BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString());
            fluidJson.addProperty("amount", fluid.getAmount());

            json.add("fluid", fluidJson);
            json.addProperty("is_emptying", isEmptying);

            super.serializeRecipeData(json);
        }

        @Override
        protected Set<String> getModDependencies() {
            Set<String> mods = new HashSet<>(RecipeDataUtil.getIngredientModIds(input));
            mods.add(BuiltInRegistries.ITEM.getKey(output).getNamespace());
            mods.add(BuiltInRegistries.FLUID.getKey(fluid.getFluid()).getNamespace());
            return mods;
        }

        @Override
        public RecipeSerializer<?> type() {
            return MachineRecipes.TANK.serializer().get();
        }
    }
}
