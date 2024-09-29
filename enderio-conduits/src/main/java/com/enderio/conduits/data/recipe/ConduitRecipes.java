package com.enderio.conduits.data.recipe;

import com.enderio.EnderIOBase;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.api.Conduit;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.init.Conduits;
import com.enderio.conduits.common.recipe.ConduitIngredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
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

import java.util.concurrent.CompletableFuture;

public class ConduitRecipes extends RecipeProvider {

    private final CompletableFuture<HolderLookup.Provider> registries;

    public ConduitRecipes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
        this.registries = registries;
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // We know that the registries are now available.
        HolderLookup.Provider lookupProvider = registries.resultNow();
        HolderGetter<Conduit<?>> conduitRegistry = lookupProvider.lookupOrThrow(EnderIOConduitsRegistries.Keys.CONDUIT);

        var itemConduit = conduitRegistry.getOrThrow(Conduits.ITEM);
        var fluidConduit = conduitRegistry.getOrThrow(Conduits.FLUID);
        var pressurizedFluidConduit = conduitRegistry.getOrThrow(Conduits.PRESSURIZED_FLUID);
        var enderFluidConduit = conduitRegistry.getOrThrow(Conduits.ENDER_FLUID);
        var energyConduit = conduitRegistry.getOrThrow(Conduits.ENERGY);
        var enhancedEnergyConduit = conduitRegistry.getOrThrow(Conduits.ENHANCED_ENERGY);
        var enderEnergyConduit = conduitRegistry.getOrThrow(Conduits.ENDER_ENERGY);
        var redstoneConduit = conduitRegistry.getOrThrow(Conduits.REDSTONE);

        buildUpgradeRecipes(recipeOutput);
        buildFilterRecipes(recipeOutput);
        buildFilterConversionRecipes(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(itemConduit, 8))
            .pattern("BBB")
            .pattern("PPP")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('P', EIOTags.Items.NUGGETS_PULSATING_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIOBase.loc("item_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(fluidConduit, 8))
            .pattern("BBB")
            .pattern("GGG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.CLEAR_GLASS)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIOBase.loc("fluid_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(pressurizedFluidConduit, 8))
            .pattern("BBB")
            .pattern("GGG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIOBase.loc("pressurized_fluid_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(pressurizedFluidConduit, 8))
            .pattern("BBB")
            .pattern("GCG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .define('C', ConduitIngredient.of(fluidConduit))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIOBase.loc("pressurized_fluid_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enderFluidConduit, 8))
            .pattern("BBB")
            .pattern("IGI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIOBase.loc("ender_fluid"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enderFluidConduit, 8))
            .pattern("BBB")
            .pattern("ICI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOItems.VIBRANT_ALLOY_INGOT)
            .define('C', ConduitIngredient.of(pressurizedFluidConduit))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIOBase.loc("ender_fluid_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(energyConduit, 8))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIOBase.loc("energy_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enhancedEnergyConduit, 8))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIOBase.loc("enhanced_energy_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enderEnergyConduit, 8))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIOBase.loc("ender_energy_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(redstoneConduit, 8))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIOBase.loc("redstone_conduit"));

//        if (Integrations.MEKANISM_INTEGRATION.isPresent()) {
//            var mekRecipeOutput = recipeOutput.withConditions(new ModLoadedCondition("mekanism"));
//
//            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(MekanismIntegration.CHEMICAL, 3))
//                .pattern("BBB")
//                .pattern("III")
//                .pattern("BBB")
//                .define('B', EIOItems.CONDUIT_BINDER)
//                .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "basic_pressurized_tube")))
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
//                .save(mekRecipeOutput, EnderIO.loc("mek_basic_pressurized_tube"));
//
//            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(MekanismIntegration.CHEMICAL2, 3))
//                .pattern("BBB")
//                .pattern("III")
//                .pattern("BBB")
//                .define('B', EIOItems.CONDUIT_BINDER)
//                .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "advanced_pressurized_tube")))
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
//                .save(mekRecipeOutput, EnderIO.loc("mek_advanced_pressurized_tube"));
//
//            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(MekanismIntegration.CHEMICAL3, 3))
//                .pattern("BBB")
//                .pattern("III")
//                .pattern("BBB")
//                .define('B', EIOItems.CONDUIT_BINDER)
//                .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "elite_pressurized_tube")))
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
//                .save(mekRecipeOutput, EnderIO.loc("mek_elite_pressurized_tube"));
//
//            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(MekanismIntegration.CHEMICAL2, 8))
//                .pattern("CCC")
//                .pattern("CUC")
//                .pattern("CCC")
//                .define('C', ConduitIngredient.of(MekanismIntegration.CHEMICAL.get()))
//                .define('U', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "alloy_infused")))
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
//                .save(mekRecipeOutput, EnderIO.loc("mek_basic_pressurized_tube_upgrade"));
//
//            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(MekanismIntegration.CHEMICAL3, 8))
//                .pattern("CCC")
//                .pattern("CUC")
//                .pattern("CCC")
//                .define('C', ConduitIngredient.of(MekanismIntegration.CHEMICAL2.get()))
//                .define('U', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "alloy_reinforced")))
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
//                .save(mekRecipeOutput, EnderIO.loc("mek_advanced_pressurized_tube_upgrade"));
//
//            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(MekanismIntegration.HEAT_TYPE, 3))
//                .pattern("BBB")
//                .pattern("III")
//                .pattern("BBB")
//                .define('B', EIOItems.CONDUIT_BINDER)
//                .define('I', BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "advanced_thermodynamic_conductor")))
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
//                .save(mekRecipeOutput, EnderIO.loc("mek_advanced_thermodynamic_conductor"));
//        }
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
            .save(recipeOutput, EnderIOBase.loc("extraction_speed_upgrade_1_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_3.get(), 2)
            .pattern("III")
            .pattern("APA")
            .pattern("ATA")
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('P', Items.PISTON)
            .define('T', Items.REDSTONE_TORCH)
            .define('A', EIOTags.Items.INGOTS_SOULARIUM)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_3)
            .requires(ConduitItems.EXTRACTION_SPEED_UPGRADE_2)
            .requires(Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), 2)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUCTIVE_ALLOY_INGOT))
            .save(recipeOutput, EnderIOBase.loc("extraction_speed_upgrade_2_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_4.get(), 2)
            .pattern("III")
            .pattern("APA")
            .pattern("ATA")
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('P', Items.PISTON)
            .define('T', Items.REDSTONE_TORCH)
            .define('A', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ENERGETIC_ALLOY_INGOT))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.EXTRACTION_SPEED_UPGRADE_4)
            .requires(ConduitItems.EXTRACTION_SPEED_UPGRADE_3)
            .requires(Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), 2)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ENERGETIC_ALLOY_INGOT))
            .save(recipeOutput, EnderIOBase.loc("extraction_speed_upgrade_3_upgrade"));
    }

    private void buildFilterRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.NOT_FILTER)
            .define('T', Items.REDSTONE_TORCH)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .pattern("TBI")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.OR_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .pattern(" I ")
            .pattern(" B ")
            .pattern(" I ")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.AND_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('T', Items.REDSTONE_TORCH)
            .pattern(" T ")
            .pattern(" B ")
            .pattern(" T ")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.XOR_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('T', Items.REDSTONE_TORCH)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .pattern(" T ")
            .pattern("IBI")
            .pattern(" T ")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.TLATCH_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('L', Items.LEVER)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .pattern("LBI")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.COUNT_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .pattern("I  ")
            .pattern("IBI")
            .pattern("I  ")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.SENSOR_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .define('C', Items.COMPARATOR)
            .pattern("CBI")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.TIMER_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .define('C', Items.CLOCK)
            .pattern("IBC")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);
    }

    private void buildFilterConversionRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.OR_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.NOR_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.NOR_FILTER))
            .save(recipeOutput, EnderIOBase.loc("or_filter_from_nor_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.NOR_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.OR_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.OR_FILTER))
            .save(recipeOutput, EnderIOBase.loc("nor_filter_from_or_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.AND_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.NAND_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.NAND_FILTER))
            .save(recipeOutput, EnderIOBase.loc("and_filter_from_nand_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.NAND_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.AND_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.AND_FILTER))
            .save(recipeOutput, EnderIOBase.loc("nand_filter_from_and_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.XOR_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.XNOR_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.XNOR_FILTER))
            .save(recipeOutput, EnderIOBase.loc("xor_filter_from_xnor_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.XNOR_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.XOR_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.XOR_FILTER))
            .save(recipeOutput, EnderIOBase.loc("xnor_filter_from_xor_filter"));
    }
}
