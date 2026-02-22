package com.enderio.endergy.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.endergy.common.EndergyConduits;
import com.enderio.endergy.common.init.EndergyItems;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitIngredient;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.neoforged.neoforge.common.Tags;

public class ConduitRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        // We know that the registries are now available.
        HolderGetter<Conduit<?, ?>> conduitRegistry = registries
                .lookupOrThrow(EnderIORegistries.Keys.CONDUIT);

        // TODO: Can't seem to use this here.
//        var vibrantEnergyConduit = conduitRegistry.getOrThrow(EIOConduits.VIBRANT_ENERGY);

        var crudeEnergyConduit = conduitRegistry.getOrThrow(EndergyConduits.CRUDE_ENERGY);
        var copperEnergyConduit = conduitRegistry.getOrThrow(EndergyConduits.COPPER_ENERGY);
        var ironEnergyConduit = conduitRegistry.getOrThrow(EndergyConduits.IRON_ENERGY);
        var goldEnergyConduit = conduitRegistry.getOrThrow(EndergyConduits.GOLD_ENERGY);
        var crystallineEnergyConduit = conduitRegistry.getOrThrow(EndergyConduits.CRYSTALLINE_ENERGY);
        var melodicEnergyConduit = conduitRegistry.getOrThrow(EndergyConduits.MELODIC_ENERGY);
        var stellarEnergyConduit = conduitRegistry.getOrThrow(EndergyConduits.STELLAR_ENERGY);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(crudeEnergyConduit, 8))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EndergyItems.CRUDE_STEEL_INGOT)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.rl("crude_energy_conduit"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(copperEnergyConduit, 8))
            .pattern("BBB")
            .pattern("IGI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
            .define('I', Tags.Items.INGOTS_COPPER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.rl("copper_energy_conduit"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(ironEnergyConduit, 8))
            .pattern("BBB")
            .pattern("IGI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
            .define('I', Tags.Items.INGOTS_IRON)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.rl("iron_energy_conduit"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(goldEnergyConduit, 8))
            .pattern("BBB")
            .pattern("IGI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
            .define('I', Tags.Items.INGOTS_GOLD)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.rl("gold_energy_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(crystallineEnergyConduit, 8))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EndergyItems.CRYSTALLINE_ALLOY_INGOT)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.rl("crystalline_energy_conduit"));

        // TODO: Fix datagen when using EnderIO base conduit type.
//        ShapedRecipeBuilder
//            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(crystallineEnergyConduit, 8))
//            .pattern("BBB")
//            .pattern("ICI")
//            .pattern("BBB")
//            .define('B', EIOItems.CONDUIT_BINDER)
//            .define('I', EndergyItems.CRYSTALLINE_ALLOY_INGOT)
//            .define('C', ConduitIngredient.of(vibrantEnergyConduit))
//            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
//            .save(recipeOutput, EnderIO.rl("crystalline_energy_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(melodicEnergyConduit, 8))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EndergyItems.MELODIC_ALLOY_INGOT)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.rl("melodic_energy_conduit"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(melodicEnergyConduit, 8))
            .pattern("BBB")
            .pattern("ICI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EndergyItems.MELODIC_ALLOY_INGOT)
            .define('C', ConduitIngredient.of(crystallineEnergyConduit))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.rl("melodic_energy_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(stellarEnergyConduit, 8))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EndergyItems.STELLAR_ALLOY_INGOT)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.rl("stellar_energy_conduit"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(stellarEnergyConduit, 8))
            .pattern("BBB")
            .pattern("ICI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EndergyItems.STELLAR_ALLOY_INGOT)
            .define('C', ConduitIngredient.of(melodicEnergyConduit))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.rl("stellar_energy_conduit_upgrade"));
    }
}
