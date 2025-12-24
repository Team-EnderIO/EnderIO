package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;

public class ArmoryRecipeProvider extends SubRecipeProvider {
    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        var item = registries.lookupOrThrow(Registries.ITEM);
//        ShapedRecipeBuilder.shaped(item, RecipeCategory.COMBAT, EIOItems.DARK_STEEL_SWORD.get())
//            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
//            .define('S', Tags.Items.RODS_WOODEN)
//            .pattern(" I ")
//            .pattern(" I ")
//            .pattern(" S ")
//            .unlockedBy("has_ingredient",
//                InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
//            .save(recipeOutput);
    }
}
