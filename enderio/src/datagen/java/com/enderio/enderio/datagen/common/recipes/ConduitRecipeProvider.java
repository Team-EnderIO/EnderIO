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
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class ConduitRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        var item = registries.lookupOrThrow(Registries.ITEM);
        
        // We know that the registries are now available.
        HolderGetter<Conduit<?, ?>> conduitRegistry = registries
                .lookupOrThrow(EnderIORegistries.Keys.CONDUIT);

        var itemConduit = conduitRegistry.getOrThrow(EIOConduits.ITEM);
        var energeticItemConduit = conduitRegistry.getOrThrow(EIOConduits.ENERGETIC_ITEM);
        var vibrantItemConduit = conduitRegistry.getOrThrow(EIOConduits.VIBRANT_ITEM);

        var fluidConduit = conduitRegistry.getOrThrow(EIOConduits.FLUID);
        var energeticFluidConduit = conduitRegistry.getOrThrow(EIOConduits.ENERGETIC_FLUID);
        var vibrantFluidConduit = conduitRegistry.getOrThrow(EIOConduits.VIBRANT_FLUID);

        var energyConduit = conduitRegistry.getOrThrow(EIOConduits.ENERGY);
        var energeticEnergyConduit = conduitRegistry.getOrThrow(EIOConduits.ENERGETIC_ENERGY);
        var vibrantEnergyConduit = conduitRegistry.getOrThrow(EIOConduits.VIBRANT_ENERGY);

        var redstoneConduit = conduitRegistry.getOrThrow(EIOConduits.REDSTONE);

        buildFilterRecipes(item, recipeOutput);
        buildFilterConversionRecipes(item, recipeOutput);
        buildFilterErasureRecipes(item, recipeOutput);
        buildFacadeCraftingRecipes(item, recipeOutput);
        buildFacadePaintingRecipes(item, recipeOutput);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(itemConduit, 8))
                .pattern("BBB")
                .pattern("CIC")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_PULSATING_ALLOY)
                .define('C', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("item_conduit").toString());

        ShapedRecipeBuilder
                .shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(energeticItemConduit, 8))
                .pattern("BBB")
                .pattern("IAI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('A', EIOTags.Items.INGOTS_PULSATING_ALLOY)
                .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("energetic_item_conduit").toString());

        ShapedRecipeBuilder
            .shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(energeticItemConduit, 8))
            .pattern("BBB")
            .pattern("ICI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .define('C', ConduitIngredient.of(itemConduit))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.id("energetic_item_conduit_upgrade").toString());

        ShapedRecipeBuilder.shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(vibrantItemConduit, 8))
            .pattern("BBB")
            .pattern("IAI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('A', EIOTags.Items.INGOTS_PULSATING_ALLOY)
            .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.id("vibrant_item_conduit").toString());

        ShapedRecipeBuilder.shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(vibrantItemConduit, 8))
            .pattern("BBB")
            .pattern("ICI")
            .pattern("BBB")
            .define('B', EIOItems.CONDUIT_BINDER)
            .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
            .define('C', ConduitIngredient.of(energeticItemConduit))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
            .save(recipeOutput, EnderIO.id("vibrant_item_conduit_upgrade").toString());

        ShapedRecipeBuilder.shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(fluidConduit, 8))
                .pattern("BBB")
                .pattern("CGC")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('G', EIOTags.Items.CLEAR_GLASS)
                .define('C', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("fluid_conduit").toString());

        ShapedRecipeBuilder
                .shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(energeticFluidConduit, 8))
                .pattern("BBB")
                .pattern("IGI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('G', EIOTags.Items.FUSED_QUARTZ)
                .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("pressurized_fluid_conduit").toString());

        ShapedRecipeBuilder
                .shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(energeticFluidConduit, 8))
                .pattern("BBB")
                .pattern("ICI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .define('C', ConduitIngredient.of(fluidConduit))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("pressurized_fluid_conduit_upgrade").toString());

        ShapedRecipeBuilder.shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(vibrantFluidConduit, 8))
                .pattern("BBB")
                .pattern("IGI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('G', EIOTags.Items.FUSED_QUARTZ)
                .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("ender_fluid").toString());

        ShapedRecipeBuilder.shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(vibrantFluidConduit, 8))
                .pattern("BBB")
                .pattern("ICI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .define('C', ConduitIngredient.of(energeticFluidConduit))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("ender_fluid_conduit_upgrade").toString());

        ShapedRecipeBuilder.shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(energyConduit, 8))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("energy_conduit").toString());

        ShapedRecipeBuilder
                .shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(energeticEnergyConduit, 8))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("enhanced_energy_conduit").toString());

        ShapedRecipeBuilder
                .shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(energeticEnergyConduit, 8))
                .pattern("BBB")
                .pattern("ICI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('C', ConduitIngredient.of(energyConduit))
                .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("enhanced_energy_conduit_upgrade").toString());

        ShapedRecipeBuilder.shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(vibrantEnergyConduit, 8))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("ender_energy_conduit").toString());

        ShapedRecipeBuilder
                .shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(vibrantEnergyConduit, 8))
                .pattern("BBB")
                .pattern("ICI")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('C', ConduitIngredient.of(energeticEnergyConduit))
                .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("ender_energy_conduit_upgrade").toString());

        ShapedRecipeBuilder.shaped(item, RecipeCategory.BUILDING_BLOCKS, ConduitBlockItem.getStackTemplateFor(redstoneConduit, 8))
                .pattern("BBB")
                .pattern("III")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("redstone_conduit").toString());

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.CONDUIT_PROBE)
                .pattern("ARA")
                .pattern("PCP")
                .pattern("RIR")
                .define('P', Tags.Items.GLASS_BLOCKS)
                .define('I', ConduitIngredient.of(itemConduit))
                .define('A', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('C', Items.COMPARATOR)
                .define('R', ConduitIngredient.of(redstoneConduit))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput);
    }

    private void buildFilterErasureRecipes(HolderLookup.RegistryLookup<Item> item, RecipeOutput recipeOutput) {
        // List of all filter items to create erasure recipes for
        ItemLike[] filterItems = { EIOItems.OR_FILTER, EIOItems.NOR_FILTER, EIOItems.AND_FILTER,
            EIOItems.NAND_FILTER, EIOItems.XOR_FILTER, EIOItems.XNOR_FILTER, EIOItems.COUNT_FILTER,
            EIOItems.TIMER_FILTER };

        // Create erasure recipe for each filter
        for (ItemLike filter : filterItems) {
            String path = BuiltInRegistries.ITEM.getKey(filter.asItem()).getPath();
            ShapelessRecipeBuilder.shapeless(item, RecipeCategory.MISC, filter)
                    .requires(filter)
                    .unlockedBy("has_ingredient", has(item, filter))
                    .save(recipeOutput, EnderIO.id(path + "_erasure").toString());
        }
    }

    private void buildFilterRecipes(HolderLookup.RegistryLookup<Item> item, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.NOT_FILTER)
                .define('T', Items.REDSTONE_TORCH)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .pattern("TBI")
                .unlockedBy("has_ingredient", has(item, EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.OR_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .pattern(" I ")
                .pattern(" B ")
                .pattern(" I ")
                .unlockedBy("has_ingredient", has(item, EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.AND_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('T', Items.REDSTONE_TORCH)
                .pattern(" T ")
                .pattern(" B ")
                .pattern(" T ")
                .unlockedBy("has_ingredient", has(item, EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.XOR_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('T', Items.REDSTONE_TORCH)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .pattern(" T ")
                .pattern("IBI")
                .pattern(" T ")
                .unlockedBy("has_ingredient", has(item, EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.TLATCH_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('L', Items.LEVER)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .pattern("LBI")
                .unlockedBy("has_ingredient", has(item, EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.COUNT_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .pattern("I  ")
                .pattern("IBI")
                .pattern("I  ")
                .unlockedBy("has_ingredient", has(item, EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.SENSOR_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .define('C', Items.COMPARATOR)
                .pattern("CBI")
                .unlockedBy("has_ingredient", has(item, EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.TIMER_FILTER)
                .define('B', EIOItems.REDSTONE_FILTER_BASE)
                .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .define('C', Items.CLOCK)
                .pattern("IBC")
                .unlockedBy("has_ingredient", has(item, EIOItems.REDSTONE_FILTER_BASE))
                .save(recipeOutput);
    }

    private void buildFilterConversionRecipes(HolderLookup.RegistryLookup<Item> item, RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(item, RecipeCategory.MISC, EIOItems.OR_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.NOR_FILTER)
                .unlockedBy("has_ingredient", has(item, EIOItems.NOR_FILTER))
                .save(recipeOutput, EnderIO.id("or_filter_from_nor_filter").toString());

        ShapelessRecipeBuilder.shapeless(item, RecipeCategory.MISC, EIOItems.NOR_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.OR_FILTER)
                .unlockedBy("has_ingredient", has(item, EIOItems.OR_FILTER))
                .save(recipeOutput, EnderIO.id("nor_filter_from_or_filter").toString());

        ShapelessRecipeBuilder.shapeless(item, RecipeCategory.MISC, EIOItems.AND_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.NAND_FILTER)
                .unlockedBy("has_ingredient", has(item, EIOItems.NAND_FILTER))
                .save(recipeOutput, EnderIO.id("and_filter_from_nand_filter").toString());

        ShapelessRecipeBuilder.shapeless(item, RecipeCategory.MISC, EIOItems.NAND_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.AND_FILTER)
                .unlockedBy("has_ingredient", has(item, EIOItems.AND_FILTER))
                .save(recipeOutput, EnderIO.id("nand_filter_from_and_filter").toString());

        ShapelessRecipeBuilder.shapeless(item, RecipeCategory.MISC, EIOItems.XOR_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.XNOR_FILTER)
                .unlockedBy("has_ingredient", has(item, EIOItems.XNOR_FILTER))
                .save(recipeOutput, EnderIO.id("xor_filter_from_xnor_filter").toString());

        ShapelessRecipeBuilder.shapeless(item, RecipeCategory.MISC, EIOItems.XNOR_FILTER)
                .requires(Items.REDSTONE_TORCH)
                .requires(EIOItems.XOR_FILTER)
                .unlockedBy("has_ingredient", has(item, EIOItems.XOR_FILTER))
                .save(recipeOutput, EnderIO.id("xnor_filter_from_xor_filter").toString());
    }

    private void buildFacadeCraftingRecipes(HolderLookup.RegistryLookup<Item> item, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.CONDUIT_FACADE)
                .pattern("BBB")
                .pattern("B B")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.HARDENED_CONDUIT_FACADE)
                .pattern(" O ")
                .pattern("OFO")
                .pattern(" O ")
                .define('O', EIOTags.Items.DUSTS_OBSIDIAN)
                .define('F', EIOItems.CONDUIT_FACADE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.TRANSPARENT_CONDUIT_FACADE)
                .pattern("BBB")
                .pattern("BGB")
                .pattern("BBB")
                .define('B', EIOItems.CONDUIT_BINDER)
                .define('G', EIOTags.Items.CLEAR_GLASS)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(item, RecipeCategory.MISC, EIOItems.TRANSPARENT_CONDUIT_FACADE)
                .requires(EIOItems.CONDUIT_FACADE)
                .requires(EIOTags.Items.CLEAR_GLASS)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("transparent_conduit_facade_from_conduit_facade").toString());

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE)
                .pattern(" O ")
                .pattern("OFO")
                .pattern(" O ")
                .define('O', EIOTags.Items.DUSTS_OBSIDIAN)
                .define('F', EIOItems.TRANSPARENT_CONDUIT_FACADE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(item, RecipeCategory.MISC, EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE)
                .requires(EIOItems.HARDENED_CONDUIT_FACADE)
                .requires(EIOTags.Items.CLEAR_GLASS)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER))
                .save(recipeOutput, EnderIO.id("transparent_hardened_conduit_facade_from_hardened_conduit_facade").toString());
    }

    private void buildFacadePaintingRecipes(HolderLookup.RegistryLookup<Item> item, RecipeOutput recipeOutput) {
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
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE, EnderIO.id("painting/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath())),
                new PaintingRecipe(input, new ItemStackTemplate(output.asItem())), null,
                new ModLoadedCondition(EnderIO.MOD_ID));
    }

}
