package com.enderio.conduits.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.integrations.ae2.AE2Integration;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.function.Consumer;

public class ConduitRecipes extends RecipeProvider {
    public ConduitRecipes(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        buildUpgradeRecipes(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.ITEM, 8)
            .pattern("BBB")
            .pattern("PPP")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('P', EIOTags.Items.NUGGETS_PULSATING_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.FLUID, 8)
            .pattern("BBB")
            .pattern("GGG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.CLEAR_GLASS)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.PRESSURIZED_FLUID, 8)
            .pattern("BBB")
            .pattern("GGG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.FLUID))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.PRESSURIZED_FLUID, 8)
            .pattern("BBB")
            .pattern("GCG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .define('C', ConduitItems.FLUID)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.FLUID))
            .save(pWriter, EnderIO.loc("pressurized_fluid_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.ENDER_FLUID, 8)
            .pattern("BBB")
            .pattern("IGI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.PRESSURIZED_FLUID))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.ENDER_FLUID, 8)
            .pattern("BBB")
            .pattern("ICI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOItems.VIBRANT_ALLOY_INGOT)
            .define('C', ConduitItems.PRESSURIZED_FLUID)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.PRESSURIZED_FLUID))
            .save(pWriter, EnderIO.loc("ender_fluid_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.ENERGY, 8)
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitItems.REDSTONE, 8)
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(pWriter);

        ConditionalRecipe.builder()
            .addCondition(new ModLoadedCondition("ae2"))
            .addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AE2Integration.NORMAL_ITEM, 3)
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', ConduitTags.Items.COVERED_CABLE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                ::save)
            .build(pWriter, EnderIO.loc("ae_covered_cable"));

        ConditionalRecipe.builder()
            .addCondition(new ModLoadedCondition("ae2"))
            .addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AE2Integration.NORMAL_ITEM, 3)
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', ConduitTags.Items.GLASS_CABLE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                ::save)
            .build(pWriter, EnderIO.loc("ae_glass_cable"));

        ConditionalRecipe.builder()
            .addCondition(new ModLoadedCondition("ae2"))
            .addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AE2Integration.DENSE_ITEM, 3)
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', ConduitTags.Items.COVERED_DENSE_CABLE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                ::save)
            .build(pWriter, EnderIO.loc("ae_covered_dense_cable"));
    }

    private void buildUpgradeRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_1.get(), 2)
            .pattern("III")
            .pattern("APA")
            .pattern("ATA")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('P', Items.PISTON)
            .define('T', Items.REDSTONE_TORCH)
            .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_ALLOY_INGOT))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_2.get(), 2)
            .pattern("III")
            .pattern("APA")
            .pattern("ATA")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('P', Items.PISTON)
            .define('T', Items.REDSTONE_TORCH)
            .define('A', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUCTIVE_ALLOY_INGOT))
            .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_2)
            .requires(ConduitItems.EXTRACTION_SPEED_UPGRADE_1)
            .requires(Ingredient.of(EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY), 2)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUCTIVE_ALLOY_INGOT))
            .save(pWriter, EnderIO.loc("extraction_speed_upgrade_1_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_3.get(), 2)
            .pattern("III")
            .pattern("APA")
            .pattern("ATA")
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('P', Items.PISTON)
            .define('T', Items.REDSTONE_TORCH)
            .define('A', EIOTags.Items.INGOTS_SOULARIUM)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT))
            .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_3)
            .requires(ConduitItems.EXTRACTION_SPEED_UPGRADE_2)
            .requires(Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), 2)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUCTIVE_ALLOY_INGOT))
            .save(pWriter, EnderIO.loc("extraction_speed_upgrade_2_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_4.get(), 2)
            .pattern("III")
            .pattern("APA")
            .pattern("ATA")
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('P', Items.PISTON)
            .define('T', Items.REDSTONE_TORCH)
            .define('A', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ENERGETIC_ALLOY_INGOT))
            .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_4)
            .requires(ConduitItems.EXTRACTION_SPEED_UPGRADE_3)
            .requires(Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), 2)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ENERGETIC_ALLOY_INGOT))
            .save(pWriter, EnderIO.loc("extraction_speed_upgrade_3_upgrade"));
    }
}
