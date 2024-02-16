package com.enderio.base.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.recipe.GrindingBallRecipe;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class GrindingBallRecipeProvider extends EnderRecipeProvider {

    public GrindingBallRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        build(Items.FLINT, 1.2F, 1.25F, 0.85F, 24000, recipeOutput);
        build(EIOItems.DARK_STEEL_BALL.get(), 1.35F, 2.00F, 0.7F, 125000, recipeOutput);
        build(EIOItems.COPPER_ALLOY_BALL.get(), 1.2F, 1.65F, 0.8F, 40000, recipeOutput);
        build(EIOItems.ENERGETIC_ALLOY_BALL.get(), 1.6F, 1.1F, 1.1F, 80000, recipeOutput);
        build(EIOItems.VIBRANT_ALLOY_BALL.get(), 1.75F, 1.35F, 1.13F, 80000, recipeOutput);
        build(EIOItems.REDSTONE_ALLOY_BALL.get(), 1.00F, 1.00F, 0.35F, 30000, recipeOutput);
        build(EIOItems.CONDUCTIVE_ALLOY_BALL.get(), 1.35F, 1.00F, 1.0F, 40000, recipeOutput);
        build(EIOItems.PULSATING_ALLOY_BALL.get(), 1.00F, 1.85F, 1.0F, 100000, recipeOutput);
        build(EIOItems.SOULARIUM_BALL.get(), 1.2F, 2.15F, 0.9F, 80000, recipeOutput);
        build(EIOItems.END_STEEL_BALL.get(), 1.4F, 2.4F, 0.7F, 75000, recipeOutput);
    }

    protected void build(Item item, float grinding, float chance, float power, int durability, RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.loc("grindingball/" + BuiltInRegistries.ITEM.getKey(item).getPath()),
            new GrindingBallRecipe(item, grinding, chance, power, durability), null);
    }
}
