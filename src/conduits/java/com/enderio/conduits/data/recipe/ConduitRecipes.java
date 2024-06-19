package com.enderio.conduits.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.integrations.Integrations;
import com.enderio.conduits.common.integrations.ae2.AE2Integration;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.concurrent.CompletableFuture;

public class ConduitRecipes extends RecipeProvider {

    public ConduitRecipes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        buildUpgradeRecipes(recipeOutput);

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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.NOT_FILTER, 1)
            .pattern("T")
            .pattern("B")
            .pattern("A")
            .define('T', Items.REDSTONE_TORCH)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.OR_FILTER, 1)
            .pattern("ABA")
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.AND_FILTER, 1)
            .pattern("TBT")
            .define('T', Items.REDSTONE_TORCH)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.XOR_FILTER, 1)
            .pattern(" T ")
            .pattern("ABA")
            .pattern(" A ")
            .define('T', Items.REDSTONE_TORCH)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.NAND_FILTER, 1)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.AND_FILTER.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.AND_FILTER.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.AND_FILTER, 1)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.NAND_FILTER.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.NAND_FILTER.get()))
            .save(recipeOutput, "nand_to_and_filter");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.NOR_FILTER, 1)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.OR_FILTER.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.NOR_FILTER.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.OR_FILTER, 1)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.NOR_FILTER.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.OR_FILTER.get()))
            .save(recipeOutput, "nor_to_or_filter");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.XNOR_FILTER, 1)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.XOR_FILTER.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.XOR_FILTER.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.XOR_FILTER, 1)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.XNOR_FILTER.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ConduitItems.XNOR_FILTER.get()))
            .save(recipeOutput, "xnor_to_xor_filter");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.TLATCH_FILTER, 1)
            .pattern("L")
            .pattern("B")
            .pattern("A")
            .define('L', Items.LEVER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.SENSOR_FILTER, 1)
            .pattern("C")
            .pattern("B")
            .pattern("A")
            .define('C', Items.COMPARATOR)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.TIMER_FILTER, 1)
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .define('C', Items.CLOCK)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.COUNT_FILTER, 1)
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_FILTER_BASE))
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

        /*if (Integrations.MEKANISM_INTEGRATION.isPresent()) {
            var mekRecipeOutput = recipeOutput.withConditions(new ModLoadedCondition("mekanism"));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MekanismIntegration.CHEMICAL_ITEM, 3)
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', BuiltInRegistries.ITEM.get(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "basic_pressurized_tube")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, EnderIO.loc("mek_basic_pressurized_tube"));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MekanismIntegration.PRESSURIZED_CHEMICAL_ITEM, 3)
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', BuiltInRegistries.ITEM.get(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "advanced_pressurized_tube")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, EnderIO.loc("mek_advanced_pressurized_tube"));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MekanismIntegration.ENDER_CHEMICAL_ITEM, 3)
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', BuiltInRegistries.ITEM.get(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "elite_pressurized_tube")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, EnderIO.loc("mek_elite_pressurized_tube"));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MekanismIntegration.PRESSURIZED_CHEMICAL_ITEM, 8)
                .pattern("CCC")
                .pattern("CUC")
                .pattern("CCC")
                .define('C', MekanismIntegration.CHEMICAL_ITEM)
                .define('U', BuiltInRegistries.ITEM.get(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "alloy_infused")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(MekanismIntegration.CHEMICAL_ITEM))
                .save(mekRecipeOutput, EnderIO.loc("mek_basic_pressurized_tube_upgrade"));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MekanismIntegration.ENDER_CHEMICAL_ITEM, 8)
                .pattern("CCC")
                .pattern("CUC")
                .pattern("CCC")
                .define('C', MekanismIntegration.PRESSURIZED_CHEMICAL_ITEM)
                .define('U', BuiltInRegistries.ITEM.get(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "alloy_reinforced")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(MekanismIntegration.PRESSURIZED_CHEMICAL_ITEM))
                .save(mekRecipeOutput, EnderIO.loc("mek_advanced_pressurized_tube_upgrade"));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MekanismIntegration.HEAT_ITEM, 3)
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', BuiltInRegistries.ITEM.get(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "advanced_thermodynamic_conductor")))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(mekRecipeOutput, EnderIO.loc("mek_advanced_thermodynamic_conductor"));
        }*/
    }

    private void buildUpgradeRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_1.get(), 2)
            .pattern("III")
            .pattern("APA")
            .pattern("ATA")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('P', Items.PISTON)
            .define('T', Items.REDSTONE_TORCH)
            .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_ALLOY_INGOT))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_2.get(), 2)
            .pattern("III")
            .pattern("APA")
            .pattern("ATA")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('P', Items.PISTON)
            .define('T', Items.REDSTONE_TORCH)
            .define('A', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUCTIVE_ALLOY_INGOT))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_2)
            .requires(ConduitItems.EXTRACTION_SPEED_UPGRADE_1)
            .requires(Ingredient.of(EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY), 2)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUCTIVE_ALLOY_INGOT))
            .save(recipeOutput, EnderIO.loc("extraction_speed_upgrade_1_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_3.get(), 2)
            .pattern("III")
            .pattern("APA")
            .pattern("ATA")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('P', Items.PISTON)
            .define('T', Items.REDSTONE_TORCH)
            .define('A', EIOTags.Items.INGOTS_SOULARIUM)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_3)
            .requires(ConduitItems.EXTRACTION_SPEED_UPGRADE_2)
            .requires(Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), 2)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUCTIVE_ALLOY_INGOT))
            .save(recipeOutput, EnderIO.loc("extraction_speed_upgrade_2_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_4.get(), 2)
            .pattern("III")
            .pattern("APA")
            .pattern("ATA")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('P', Items.PISTON)
            .define('T', Items.REDSTONE_TORCH)
            .define('A', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ENERGETIC_ALLOY_INGOT))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_4)
            .requires(ConduitItems.EXTRACTION_SPEED_UPGRADE_3)
            .requires(Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), 2)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ENERGETIC_ALLOY_INGOT))
            .save(recipeOutput, EnderIO.loc("extraction_speed_upgrade_3_upgrade"));
    }
}
