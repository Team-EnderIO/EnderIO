package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.soul.binding.ingredients.EmptySoulBindableIngredient;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorTier;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelTier;
import com.enderio.enderio.foundation.soul.ShapedEntityStorageRecipeBuilder;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.ArrayList;
import java.util.List;

public class MachineRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('C', EIOItems.BASIC_CAPACITOR.get())
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .pattern("ICI")
                .pattern("CRC")
                .pattern("ICI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
                .define('A', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .pattern("ACA")
                .pattern("CRC")
                .pattern("ACA")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
                .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
                .define('B', EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get())
                .pattern("EEE")
                .pattern("BCB")
                .pattern("EEE")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.DOUBLE_LAYER_CAPACITOR).build()))
                .save(recipeOutput, EnderIO.rl("advanced_capacitor_bank_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get())
                .define('V', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .define('O', EIOItems.OCTADIC_CAPACITOR.get())
                .define('C', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
                .define('B', EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
                .pattern("VOV")
                .pattern("BCB")
                .pattern("VOV")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.OCTADIC_CAPACITOR).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get())
                .define('A', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('O', EIOItems.OCTADIC_CAPACITOR.get())
                .define('C', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
                .pattern("AOA")
                .pattern("OCO")
                .pattern("AOA")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
                .save(recipeOutput, EnderIO.rl("vibrant_capacitor_bank_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.FLUID_TANK.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('B', Blocks.IRON_BARS)
                .define('G', Tags.Items.GLASS_BLOCKS)
                .pattern("IBI")
                .pattern("BGB")
                .pattern("IBI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.PRESSURIZED_FLUID_TANK.get())
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .define('B', EIOBlocks.DARK_STEEL_BARS.get())
                .define('G', EIOTags.Items.FUSED_QUARTZ)
                .pattern("IBI")
                .pattern("BGB")
                .pattern("IBI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.ENCHANTER.get())
                .define('B', Items.BOOK)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .define('L', Items.LECTERN)
                .pattern(" B ")
                .pattern("DLD")
                .pattern("III")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BOOK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.ALLOY_SMELTER.get())
                .define('F', Blocks.FURNACE)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', EIOTags.Items.GEARS_IRON)
                .define('V', EIOBlocks.VOID_CHASSIS.get())
                .define('O', Tags.Items.OBSIDIANS)
                .pattern("GFG")
                .pattern("FVF")
                .pattern("IOI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.STIRLING_GENERATOR.get())
                .define('F', Blocks.FURNACE)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', EIOTags.Items.GEARS_IRON)
                .define('V', EIOBlocks.VOID_CHASSIS.get())
                .define('O', Tags.Items.OBSIDIANS)
                .define('D', Items.DEEPSLATE)
                .pattern("GFG")
                .pattern("IVI")
                .pattern("ODO")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.SAG_MILL.get())
                .define('F', Items.FLINT)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', EIOTags.Items.GEARS_IRON)
                .define('V', EIOBlocks.VOID_CHASSIS.get())
                .define('O', Tags.Items.OBSIDIANS)
                .define('P', Items.PISTON)
                .pattern("GFG")
                .pattern("IVI")
                .pattern("OPO")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.SLICE_AND_SPLICE.get())
                .define('I', EIOTags.Items.INGOTS_SOULARIUM)
                .define('G', EIOTags.Items.GEARS_ENERGIZED)
                .define('C', EIOBlocks.ENSOULED_CHASSIS.get())
                .define('B', Items.IRON_BARS)
                .define('H', ItemTags.SKULLS)
                .pattern("IHI")
                .pattern("ICI")
                .pattern("GBG")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.IMPULSE_HOPPER.get())
                .define('I', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('R', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .define('G', EIOTags.Items.GEARS_IRON)
                .define('C', EIOBlocks.VOID_CHASSIS.get())
                .define('H', Items.HOPPER)
                .pattern("IHI")
                .pattern("GCG")
                .pattern("IRI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
                .save(recipeOutput);

        // TODO: Not a fan, at all...
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.SOUL_BINDER.get())
                .define('I', EIOTags.Items.INGOTS_SOULARIUM)
                .define('C', EIOBlocks.ENSOULED_CHASSIS.get())
                .define('G', EIOTags.Items.GEARS_ENERGIZED)
                .define('Z', EIOItems.Z_LOGIC_CONTROLLER)
                .define('V', EmptySoulBindableIngredient.of(EIOItems.SOUL_VIAL))
                .pattern("IVI")
                .pattern("GCG")
                .pattern("IZI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.WIRED_CHARGER.get())
                .define('C', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('V', EIOBlocks.VOID_CHASSIS.get())
                .pattern("CCC")
                .pattern("CVC")
                .pattern("CCC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
                .save(recipeOutput);

        ShapedEntityStorageRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.POWERED_SPAWNER)
                .define('I', EIOTags.Items.INGOTS_SOULARIUM) // TODO Maybe also soulchains?
                .define('B', EIOItems.BROKEN_SPAWNER)
                .define('C', EIOBlocks.ENSOULED_CHASSIS)
                .define('Z', EIOItems.Z_LOGIC_CONTROLLER)
                .define('V', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
                .pattern("IBI")
                .pattern("ICI")
                .pattern("VZV")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.SOUL_ENGINE)
                .define('I', EIOTags.Items.INGOTS_SOULARIUM) // TODO Maybe also soulchains?
                .define('B', Items.BUCKET)
                .define('C', EIOBlocks.ENSOULED_CHASSIS)
                .define('Z', EIOItems.ZOMBIE_ELECTRODE)
                .define('G', EIOTags.Items.FUSED_QUARTZ)
                .pattern("IGI")
                .pattern("BCB")
                .pattern("IZI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.VACUUM_CHEST.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('C', Tags.Items.CHESTS)
                .define('P', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
                .pattern("III")
                .pattern("ICI")
                .pattern("IPI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.DRAIN.get())
                .define('I', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('V', EIOBlocks.VOID_CHASSIS)
                .define('C', EIOTags.Items.CLEAR_GLASS)
                .define('G', EIOTags.Items.GEARS_IRON)
                .define('B', Items.BUCKET)
                .pattern("ICI")
                .pattern("IVI")
                .pattern("GBG")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOBlocks.VOID_CHASSIS).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.NIARD.get())
            .define('P', Items.PISTON)
            .define('B', Items.BUCKET)
            .define('R', EIOBlocks.DARK_STEEL_BARS.get())
            .define('T', EIOBlocks.FLUID_TANK.get())
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('V', EIOBlocks.VOID_CHASSIS)
            .pattern("BTB")
            .pattern("PVP")
            .pattern("IRI")
            .unlockedBy("has_ingredient",
                InventoryChangeTrigger.TriggerInstance
                    .hasItems(ItemPredicate.Builder.item().of(EIOBlocks.VOID_CHASSIS).build()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.XP_VACUUM)
                .pattern("III")
                .pattern("IRI")
                .pattern("IPI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', EIOItems.VOID_VIAL)
                .define('P', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.CRAFTER.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', EIOTags.Items.GEARS_IRON)
                .define('C', EIOBlocks.VOID_CHASSIS.get())
                .define('S', EIOTags.Items.SILICON)
                .define('T', Items.CRAFTING_TABLE)
                .pattern("SSS")
                .pattern("ICI")
                .pattern("GTG")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC))
                .define('E', Tags.Items.INGOTS_GOLD)
                .define('F', Tags.Items.GLASS_BLOCKS)
                .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
                .define('C', EIOItems.BASIC_CAPACITOR)
                .define('D', Items.REDSTONE)
                .pattern("EFE")
                .pattern("PPP")
                .pattern("CDC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PHOTOVOLTAIC_PLATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING))
                .define('I', EIOTags.Items.INGOTS_PULSATING_ALLOY)
                .define('F', EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ)
                .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
                .define('D', EIOTags.Items.DUSTS_COAL)
                .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR)
                .define('S', EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC))
                .pattern("IFI")
                .pattern("PDP")
                .pattern("CSC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC)))
                .save(recipeOutput,
                        EnderIO.rl(RecipeBuilder
                                .getDefaultRecipeId(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING))
                                .getPath()));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT))
                .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .define('F', EIOTags.Items.DARK_FUSED_QUARTZ)
                .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
                .define('G', Items.GLOWSTONE)
                .define('C', EIOItems.OCTADIC_CAPACITOR)
                .define('S', EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING))
                .pattern("IFI")
                .pattern("PGP")
                .pattern("CSC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING)))
                .save(recipeOutput,
                        EnderIO.rl(
                                RecipeBuilder.getDefaultRecipeId(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT))
                                        .getPath()));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.PAINTING_MACHINE.get())
                .pattern("RGB")
                .pattern("ICI")
                .pattern("MAM")
                .define('R', Tags.Items.DYES_RED)
                .define('G', Tags.Items.DYES_GREEN)
                .define('B', Tags.Items.DYES_BLUE)
                .define('I', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('C', EIOBlocks.VOID_CHASSIS)
                .define('M', EIOTags.Items.GEARS_IRON)
                .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.TRAVEL_ANCHOR.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('C', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
                .define('B', EIOItems.CONDUIT_BINDER)
                .pattern("IBI")
                .pattern("BCB")
                .pattern("IBI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.PULSATING_CRYSTAL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.XP_OBELISK.get())
                .define('R', EIOItems.VOID_VIAL)
                .define('I', EIOTags.Items.INGOTS_SOULARIUM)
                .define('C', EIOBlocks.ENSOULED_CHASSIS)
                .pattern(" R ")
                .pattern(" I ")
                .pattern("ICI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.VOID_VIAL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.AVERSION_OBELISK.get())
                .define('H', EIOBlocks.ENDERMAN_HEAD) // TODO Tormented Ender
                .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .define('I', EIOTags.Items.INGOTS_SOULARIUM)
                .define('C', EIOBlocks.ENSOULED_CHASSIS)
                .pattern(" H ")
                .pattern("EIE")
                .pattern("ICI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOBlocks.ENDERMAN_HEAD).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.INHIBITOR_OBELISK.get())
                .define('M', EIOItems.ENDER_CRYSTAL)
                .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .define('G', EIOItems.GEAR_IRON)
                .define('I', EIOTags.Items.INGOTS_SOULARIUM)
                .define('C', EIOBlocks.ENSOULED_CHASSIS)
                .pattern(" M ")
                .pattern("EGE")
                .pattern("ICI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.ENDER_CRYSTAL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.RELOCATOR_OBELISK.get())
                .define('P', Items.PRISMARINE)
                .define('A', EIOBlocks.AVERSION_OBELISK)
                .pattern("P")
                .pattern("A")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOBlocks.AVERSION_OBELISK).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.ATTRACTOR_OBELISK.get())
                .define('M', EIOTags.Items.GEMS_ENTICING_CRYSTAL)
                .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .define('G', EIOItems.GEAR_ENERGIZED)
                .define('I', EIOTags.Items.INGOTS_SOULARIUM)
                .define('C', EIOBlocks.ENSOULED_CHASSIS)
                .pattern(" M ")
                .pattern("EGE")
                .pattern("ICI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.ENTICING_CRYSTAL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.WEATHER_OBELISK.get())
                .define('W', EIOItems.WEATHER_CRYSTAL)
                .define('S', EIOTags.Items.INGOTS_SOULARIUM)
                .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .define('C', EIOBlocks.VOID_CHASSIS)
                .pattern(" W ")
                .pattern("ESE")
                .pattern("SCS")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.WEATHER_CRYSTAL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.VAT.get())
                .define('B', Blocks.BARREL)
                .define('C', EIOBlocks.VOID_CHASSIS)
                .define('M', EIOTags.Items.GEARS_IRON)
                .define('A', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('R', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .define('S', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern("SBS")
                .pattern("ACA")
                .pattern("MRM")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(Blocks.BARREL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.WIRELESS_CHARGER.get())
                .define('C', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('V', EIOBlocks.VOID_CHASSIS.get())
                .define('E', EIOItems.ENDER_RESONATOR.get())
                .pattern("CCC")
                .pattern("CVC")
                .pattern("CEC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.WIRELESS_CHARGER_ANTENNA.get())
                .define('C', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('S', EIOTags.Items.INGOTS_PULSATING_ALLOY)
                .define('E', EIOItems.ENDER_RESONATOR.get())
                .pattern(" S ")
                .pattern(" S ")
                .pattern("CEC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ENDER_RESONATOR.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get())
                .define('C', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('S', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .define('E', EIOItems.SENTIENT_ENDER.get())
                .pattern(" S ")
                .pattern(" S ")
                .pattern("CEC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SENTIENT_ENDER.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.MIND_KILLER.get())
            .define('S', EIOTags.Items.INGOTS_SOULARIUM)
            .define('Z', EIOItems.ZOMBIE_ELECTRODE)
            .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .pattern(" Z ")
            .pattern("SES")
            .unlockedBy("has_ingredient",
                InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ZOMBIE_ELECTRODE.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.FARMING_STATION.get())
            .define('E', EIOTags.Items.GEARS_ENERGIZED)
            .define('G', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
            .define('S', EIOTags.Items.INGOTS_SOULARIUM)
            .define('C', EIOBlocks.ENSOULED_CHASSIS.get())
            .define('D', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('Z', EIOItems.Z_LOGIC_CONTROLLER.get())
            .pattern("EGE")
            .pattern("SCS")
            .pattern("DZD")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT))
            .save(recipeOutput);

        // TODO: Enable once the block detector has a model.
        /*
         * ShapedRecipeBuilder .shaped(RecipeCategory.MISC,
         * EIOBlocks.BLOCK_DETECTOR.get()) .define('D',
         * EIOBlocks.DARK_STEEL_PRESSURE_PLATE) .define('N', EIOItems.DARK_STEEL_NUGGET)
         * .define('P', Items.PISTON) .define('R', Items.REDSTONE_BLOCK) .pattern("NDN")
         * .pattern("NPN") .pattern("NRN") .unlockedBy("has_ingredient",
         * InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().
         * of(Items.PISTON).build())) .save(recipeOutput);
         */
        eraseRecipes(recipeOutput);
    }

    private static final List<DeferredBlock<?>> MACHINES = Util.make(() -> {
        List<DeferredBlock<?>> list = new ArrayList<>();
        list.addAll(EIOBlocks.CAPACITOR_BANKS.values());
        list.add(EIOBlocks.FLUID_TANK);
        list.add(EIOBlocks.PRESSURIZED_FLUID_TANK);
        list.add(EIOBlocks.ALLOY_SMELTER);
        list.add(EIOBlocks.STIRLING_GENERATOR);
        list.add(EIOBlocks.SAG_MILL);
        list.add(EIOBlocks.SLICE_AND_SPLICE);
        list.add(EIOBlocks.SOUL_BINDER);
        list.add(EIOBlocks.WIRED_CHARGER);
        list.add(EIOBlocks.POWERED_SPAWNER);
        list.add(EIOBlocks.SOUL_ENGINE);
        list.add(EIOBlocks.DRAIN);
        list.add(EIOBlocks.NIARD);
        list.add(EIOBlocks.CRAFTER);
        list.addAll(EIOBlocks.SOLAR_PANELS.values());
        list.add(EIOBlocks.PAINTING_MACHINE);
        list.add(EIOBlocks.IMPULSE_HOPPER);
        list.add(EIOBlocks.XP_OBELISK);
        list.add(EIOBlocks.AVERSION_OBELISK);
        list.add(EIOBlocks.INHIBITOR_OBELISK);
        list.add(EIOBlocks.RELOCATOR_OBELISK);
        list.add(EIOBlocks.ATTRACTOR_OBELISK);
        list.add(EIOBlocks.XP_VACUUM);
        list.add(EIOBlocks.VACUUM_CHEST);
        list.add(EIOBlocks.TRAVEL_ANCHOR);
        list.add(EIOBlocks.WIRELESS_CHARGER);
        return list;
    });

    public void eraseRecipes(RecipeOutput recipeOutput) {
        for (var block : MACHINES) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, block)
                    .requires(block)
                    .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block))
                    .save(recipeOutput, EnderIO.rl("erase_" + block.getId().getPath()));
        }
    }
}
