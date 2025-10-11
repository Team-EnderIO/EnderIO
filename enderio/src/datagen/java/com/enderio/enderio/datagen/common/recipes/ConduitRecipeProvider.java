package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitIngredient;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.enderio.content.machines.painting.PaintingRecipe;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOConduits;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class ConduitRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        // We know that the registries are now available.
        HolderGetter<Conduit<?, ?>> conduitRegistry = registries
                .lookupOrThrow(EnderIORegistries.Keys.CONDUIT);

        var itemConduit = conduitRegistry.getOrThrow(EIOConduits.ITEM);
        var fluidConduit = conduitRegistry.getOrThrow(EIOConduits.FLUID);
        var pressurizedFluidConduit = conduitRegistry.getOrThrow(EIOConduits.PRESSURIZED_FLUID);
        var enderFluidConduit = conduitRegistry.getOrThrow(EIOConduits.ENDER_FLUID);
        var energyConduit = conduitRegistry.getOrThrow(EIOConduits.ENERGY);
        var enhancedEnergyConduit = conduitRegistry.getOrThrow(EIOConduits.ENHANCED_ENERGY);
        var enderEnergyConduit = conduitRegistry.getOrThrow(EIOConduits.ENDER_ENERGY);
        var redstoneConduit = conduitRegistry.getOrThrow(EIOConduits.REDSTONE);

        buildFilterRecipes(recipeOutput);
        buildFilterConversionRecipes(recipeOutput);
        buildFilterErasureRecipes(recipeOutput);
        buildFacadeCraftingRecipes(recipeOutput);
        buildFacadePaintingRecipes(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(itemConduit, 8))
                .pattern("BBB")
                .pattern("PPP")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('P', EIOTags.Items.NUGGETS_PULSATING_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("item_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(fluidConduit, 8))
                .pattern("BBB")
                .pattern("GGG")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('G', EIOTags.Items.CLEAR_GLASS)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("fluid_conduit"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(pressurizedFluidConduit, 8))
                .pattern("BBB")
                .pattern("GGG")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('G', EIOTags.Items.FUSED_QUARTZ)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("pressurized_fluid_conduit"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(pressurizedFluidConduit, 8))
                .pattern("BBB")
                .pattern("GCG")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('G', EIOTags.Items.FUSED_QUARTZ)
                .define('C', ConduitIngredient.of(fluidConduit))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("pressurized_fluid_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enderFluidConduit, 8))
                .pattern("BBB")
                .pattern("IGI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('G', EIOTags.Items.FUSED_QUARTZ)
                .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("ender_fluid"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enderFluidConduit, 8))
                .pattern("BBB")
                .pattern("ICI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOItems.VIBRANT_ALLOY_INGOT)
                .define('C', ConduitIngredient.of(pressurizedFluidConduit))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("ender_fluid_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(energyConduit, 8))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("energy_conduit"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enhancedEnergyConduit, 8))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("enhanced_energy_conduit"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enhancedEnergyConduit, 8))
                .pattern("BBB")
                .pattern("ICI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('C', ConduitIngredient.of(energyConduit))
                .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("enhanced_energy_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enderEnergyConduit, 8))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("ender_energy_conduit"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(enderEnergyConduit, 8))
                .pattern("BBB")
                .pattern("ICI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('C', ConduitIngredient.of(enhancedEnergyConduit))
                .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("ender_energy_conduit_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackFor(redstoneConduit, 8))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("redstone_conduit"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.CONDUIT_PROBE)
                .pattern("ARA")
                .pattern("PCP")
                .pattern("RIR")
                .define('P', Tags.Items.GLASS_BLOCKS)
                .define('I', ConduitIngredient.of(itemConduit))
                .define('A', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('C', Items.COMPARATOR)
                .define('R', ConduitIngredient.of(redstoneConduit))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("conduit_probe"));
    }

    private void buildFilterErasureRecipes(RecipeOutput recipeOutput) {
        // List of all filter items to create erasure recipes for
        ItemLike[] filterItems = { EIOItems.OR_FILTER, EIOItems.NOR_FILTER, EIOItems.AND_FILTER,
            EIOItems.NAND_FILTER, EIOItems.XOR_FILTER, EIOItems.XNOR_FILTER, EIOItems.COUNT_FILTER,
            EIOItems.TIMER_FILTER };

        // Create erasure recipe for each filter
        for (ItemLike filter : filterItems) {
            String path = BuiltInRegistries.ITEM.getKey(filter.asItem()).getPath();
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, filter)
                    .requires(filter)
                    .unlockedBy("has_ingredient", has(filter))
                    .save(recipeOutput, EnderIO.rl(path + "_erasure"));
        }
    }

    private void buildFilterRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.NOT_FILTER)
                .define('T', Items.REDSTONE_TORCH)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .pattern("TBI")
                .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.OR_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .pattern(" I ")
                .pattern(" B ")
                .pattern(" I ")
                .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.AND_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('T', Items.REDSTONE_TORCH)
                .pattern(" T ")
                .pattern(" B ")
                .pattern(" T ")
                .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.XOR_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('T', Items.REDSTONE_TORCH)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .pattern(" T ")
                .pattern("IBI")
                .pattern(" T ")
                .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.TLATCH_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('L', Items.LEVER)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .pattern("LBI")
                .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.COUNT_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .pattern("I  ")
                .pattern("IBI")
                .pattern("I  ")
                .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.SENSOR_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .define('C', Items.COMPARATOR)
                .pattern("CBI")
                .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.TIMER_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .define('C', Items.CLOCK)
                .pattern("IBC")
                .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);
    }

    private void buildFilterConversionRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.OR_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.NOR_FILTER)
                .unlockedBy("has_ingredient", has(EIOItems.NOR_FILTER))
                .save(recipeOutput, EnderIO.rl("or_filter_from_nor_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.NOR_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.OR_FILTER)
                .unlockedBy("has_ingredient", has(EIOItems.OR_FILTER))
                .save(recipeOutput, EnderIO.rl("nor_filter_from_or_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.AND_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.NAND_FILTER)
                .unlockedBy("has_ingredient", has(EIOItems.NAND_FILTER))
                .save(recipeOutput, EnderIO.rl("and_filter_from_nand_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.NAND_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.AND_FILTER)
                .unlockedBy("has_ingredient", has(EIOItems.AND_FILTER))
                .save(recipeOutput, EnderIO.rl("nand_filter_from_and_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.XOR_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.XNOR_FILTER)
                .unlockedBy("has_ingredient", has(EIOItems.XNOR_FILTER))
                .save(recipeOutput, EnderIO.rl("xor_filter_from_xnor_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.XNOR_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.XOR_FILTER)
                .unlockedBy("has_ingredient", has(EIOItems.XOR_FILTER))
                .save(recipeOutput, EnderIO.rl("xnor_filter_from_xor_filter"));
    }

    private void buildFacadeCraftingRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.CONDUIT_FACADE)
                .pattern("BBB")
                .pattern("B B")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.HARDENED_CONDUIT_FACADE)
                .pattern(" O ")
                .pattern("OFO")
                .pattern(" O ")
                .define('O', EIOTags.Items.DUSTS_OBSIDIAN)
                .define('F', EIOItems.CONDUIT_FACADE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.TRANSPARENT_CONDUIT_FACADE)
                .pattern("BBB")
                .pattern("BGB")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('G', EIOTags.Items.CLEAR_GLASS)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.TRANSPARENT_CONDUIT_FACADE)
                .requires(EIOItems.CONDUIT_FACADE)
                .requires(EIOTags.Items.CLEAR_GLASS)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("transparent_conduit_facade_from_conduit_facade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE)
                .pattern(" O ")
                .pattern("OFO")
                .pattern(" O ")
                .define('O', EIOTags.Items.DUSTS_OBSIDIAN)
                .define('F', EIOItems.TRANSPARENT_CONDUIT_FACADE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE)
                .requires(EIOItems.HARDENED_CONDUIT_FACADE)
                .requires(EIOTags.Items.CLEAR_GLASS)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.rl("transparent_hardened_conduit_facade_from_hardened_conduit_facade"));
    }

    private void buildFacadePaintingRecipes(RecipeOutput recipeOutput) {
        paintingRecipe(EIOItems.CONDUIT_FACADE, Ingredient.of(EIOItems.CONDUIT_FACADE), recipeOutput);
        paintingRecipe(EIOItems.HARDENED_CONDUIT_FACADE, Ingredient.of(EIOItems.HARDENED_CONDUIT_FACADE),
                recipeOutput);
        paintingRecipe(EIOItems.TRANSPARENT_CONDUIT_FACADE, Ingredient.of(EIOItems.TRANSPARENT_CONDUIT_FACADE),
                recipeOutput);
        paintingRecipe(EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE,
                Ingredient.of(EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE), recipeOutput);
    }

    // TODO: I want to have a builder for all EIO recipes in the API.
    protected void paintingRecipe(ItemLike output, Ingredient input, RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.rl("painting/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath()),
                new PaintingRecipe(input, output.asItem().getDefaultInstance()), null,
                new ModLoadedCondition(EnderIO.MOD_ID));
    }

}
