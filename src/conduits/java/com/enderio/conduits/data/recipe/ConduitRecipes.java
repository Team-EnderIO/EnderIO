package com.enderio.conduits.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.integrations.Integrations;
import com.enderio.conduits.common.integrations.ae2.AE2Integration;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class ConduitRecipes extends RecipeProvider {

    public ConduitRecipes(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.ITEM, 8)
            .pattern("BBB")
            .pattern("PPP")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('P', EIOTags.Items.NUGGETS_PULSATING_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.FLUID, 8)
            .pattern("BBB")
            .pattern("GGG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.CLEAR_GLASS)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.PRESSURIZED_FLUID, 8)
            .pattern("BBB")
            .pattern("GGG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.FLUID))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.PRESSURIZED_FLUID, 8)
            .pattern("BBB")
            .pattern("GCG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .define('C', ConduitItems.FLUID)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.FLUID))
            .save(recipeOutput, EnderIO.loc("pressurized_fluid_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.ENDER_FLUID, 8)
            .pattern("BBB")
            .pattern("IGI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.PRESSURIZED_FLUID))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.ENDER_FLUID, 8)
            .pattern("BBB")
            .pattern("ICI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOItems.VIBRANT_ALLOY_INGOT)
            .define('C', ConduitItems.PRESSURIZED_FLUID)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.PRESSURIZED_FLUID))
            .save(recipeOutput, EnderIO.loc("ender_fluid_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.ENERGY, 8)
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.REDSTONE, 8)
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput);

        if (Integrations.AE2_INTEGRATION.isPresent()) {
            var ae2RecipeOutput = recipeOutput.withConditions(new ModLoadedCondition("ae2"));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AE2Integration.NORMAL_ITEM, 3)
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', ConduitTags.Items.COVERED_CABLE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(ae2RecipeOutput, EnderIO.loc("ae_covered_cable"));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AE2Integration.NORMAL_ITEM, 3)
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', ConduitTags.Items.GLASS_CABLE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(ae2RecipeOutput, EnderIO.loc("ae_glass_cable"));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AE2Integration.DENSE_ITEM, 3)
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', ConduitTags.Items.COVERED_DENSE_CABLE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(ae2RecipeOutput, EnderIO.loc("ae_covered_dense_cable"));
        }
    }
}
