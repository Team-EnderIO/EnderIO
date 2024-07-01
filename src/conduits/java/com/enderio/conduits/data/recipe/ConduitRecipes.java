package com.enderio.conduits.data.recipe;

import com.enderio.EnderIO;
import com.enderio.api.conduit.Conduit;
import com.enderio.api.registry.EnderIORegistries;
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
        HolderGetter<Conduit<?, ?, ?>> conduitRegistry = lookupProvider.lookupOrThrow(EnderIORegistries.Keys.CONDUIT);

        var itemConduit = conduitRegistry.getOrThrow(Conduits.ITEM);
        var fluidConduit = conduitRegistry.getOrThrow(Conduits.FLUID);
        var pressurizedFluidConduit = conduitRegistry.getOrThrow(Conduits.PRESSURIZED_FLUID);
        var enderFluidConduit = conduitRegistry.getOrThrow(Conduits.ENDER_FLUID);
        var energyConduit = conduitRegistry.getOrThrow(Conduits.ENERGY);
        var redstoneConduit = conduitRegistry.getOrThrow(Conduits.REDSTONE);

        buildUpgradeRecipes(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(itemConduit, 8))
            .pattern("BBB")
            .pattern("PPP")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('P', EIOTags.Items.NUGGETS_PULSATING_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.loc("item_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(fluidConduit, 8))
            .pattern("BBB")
            .pattern("GGG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.CLEAR_GLASS)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.loc("fluid_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(pressurizedFluidConduit, 8))
            .pattern("BBB")
            .pattern("GGG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.loc("pressurized_fluid_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(pressurizedFluidConduit, 8))
            .pattern("BBB")
            .pattern("GCG")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .define('C', ConduitIngredient.of(fluidConduit))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.loc("pressurized_fluid_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enderFluidConduit, 8))
            .pattern("BBB")
            .pattern("IGI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.loc("ender_fluid"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enderFluidConduit, 8))
            .pattern("BBB")
            .pattern("ICI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOItems.VIBRANT_ALLOY_INGOT)
            .define('C', ConduitIngredient.of(pressurizedFluidConduit))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.loc("ender_fluid_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(energyConduit, 8))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.loc("energy_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(redstoneConduit, 8))
            .pattern("BBB")
            .pattern("III")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.loc("redstone_conduit"));

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

        // TODO: When re-enabled, these need to move to their own recipe provider.
//        if (Integrations.AE2_INTEGRATION.isPresent()) {
//            var ae2RecipeOutput = recipeOutput.withConditions(new ModLoadedCondition("ae2"));
//
//            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(AE2Integration.NORMAL, 3))
//                .pattern("BBB")
//                .pattern("III")
//                .pattern("BBB")
//                .define('B', EIOItems.CONDUIT_BINDER)
//                .define('I', ConduitTags.Items.COVERED_CABLE)
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
//                .save(ae2RecipeOutput, EnderIO.loc("ae_covered_cable"));
//
//            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(AE2Integration.NORMAL, 3))
//                .pattern("BBB")
//                .pattern("III")
//                .pattern("BBB")
//                .define('B', EIOItems.CONDUIT_BINDER)
//                .define('I', ConduitTags.Items.GLASS_CABLE)
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
//                .save(ae2RecipeOutput, EnderIO.loc("ae_glass_cable"));
//
//            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(AE2Integration.DENSE, 3))
//                .pattern("BBB")
//                .pattern("III")
//                .pattern("BBB")
//                .define('B', EIOItems.CONDUIT_BINDER)
//                .define('I', ConduitTags.Items.COVERED_DENSE_CABLE)
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
//                .save(ae2RecipeOutput, EnderIO.loc("ae_covered_dense_cable"));
//        }
//
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
            .save(recipeOutput, EnderIO.loc("extraction_speed_upgrade_1_upgrade"));

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
            .save(recipeOutput, EnderIO.loc("extraction_speed_upgrade_2_upgrade"));

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
            .save(recipeOutput, EnderIO.loc("extraction_speed_upgrade_3_upgrade"));
    }
}
