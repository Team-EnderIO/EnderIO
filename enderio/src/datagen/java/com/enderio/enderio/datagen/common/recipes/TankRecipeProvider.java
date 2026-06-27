package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.storage.fluid_tank.TankRecipe;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class TankRecipeProvider extends SubRecipeProvider {

    protected SizedFluidIngredient sizedFromTag(HolderLookup.RegistryLookup<Fluid> fluids, TagKey<Fluid> tag, int count) {
        return new SizedFluidIngredient(FluidIngredient.of(fluids.getOrThrow(tag)), count);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        var fluids = registries.lookupOrThrow(Registries.FLUID);

        buildEmptying(Ingredient.of(Items.EXPERIENCE_BOTTLE), Items.GLASS_BOTTLE,
                sizedFromTag(fluids, Tags.Fluids.EXPERIENCE, 250), recipeOutput);
        buildFilling(Ingredient.of(Items.GLASS_BOTTLE), Items.EXPERIENCE_BOTTLE,
            sizedFromTag(fluids, Tags.Fluids.EXPERIENCE, 250), recipeOutput);

        buildEmptying(Ingredient.of(Items.WET_SPONGE), Items.SPONGE, SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.SPONGE), Items.WET_SPONGE, SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);

        buildFilling(Ingredient.of(Items.STICK), EIOItems.NUTRITIOUS_STICK,
                SizedFluidIngredient.of(EIOFluids.NUTRIENT_DISTILLATION.source().get(), 1000), recipeOutput);

        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.WHITE)), Items.CONCRETE.pick(DyeColor.WHITE),
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.ORANGE)), Items.CONCRETE.pick(DyeColor.ORANGE),
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.MAGENTA)), Items.CONCRETE.pick(DyeColor.MAGENTA),
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.LIGHT_BLUE)), Items.CONCRETE.pick(DyeColor.LIGHT_BLUE),
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.YELLOW)), Items.CONCRETE.pick(DyeColor.YELLOW),
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.LIME)), Items.CONCRETE.pick(DyeColor.LIME), SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.PINK)), Items.CONCRETE.pick(DyeColor.PINK), SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.GRAY)), Items.CONCRETE.pick(DyeColor.GRAY), SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.LIGHT_GRAY)), Items.CONCRETE.pick(DyeColor.LIGHT_GRAY),
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.CYAN)), Items.CONCRETE.pick(DyeColor.CYAN), SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.PURPLE)), Items.CONCRETE.pick(DyeColor.PURPLE),
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.BLUE)), Items.CONCRETE.pick(DyeColor.BLUE), SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.BROWN)), Items.CONCRETE.pick(DyeColor.BROWN),
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.GREEN)), Items.CONCRETE.pick(DyeColor.GREEN),
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.RED)), Items.CONCRETE.pick(DyeColor.RED), SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.CONCRETE_POWDER.pick(DyeColor.BLACK)), Items.CONCRETE.pick(DyeColor.BLACK),
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
    }

    protected void buildEmptying(Ingredient input, ItemLike output, SizedFluidIngredient fluid, RecipeOutput recipeOutput) {
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE, EnderIO.id("tank_empty/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath())),
                new TankRecipe(input, new ItemStackTemplate(output.asItem()), fluid, TankRecipe.Mode.EMPTY), null);
    }

    protected void buildFilling(Ingredient input, ItemLike output, SizedFluidIngredient fluid, RecipeOutput recipeOutput) {
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE, EnderIO.id("tank_fill/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath())),
                new TankRecipe(input, new ItemStackTemplate(output.asItem()), fluid, TankRecipe.Mode.FILL), null);
    }

}
