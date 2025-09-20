package com.enderio.machines.data.recipes;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.soul.binding.ingredients.EmptySoulBindableIngredient;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.recipe.ShapedEntityStorageRecipeBuilder;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorTier;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.regilite.holder.RegiliteBlock;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MachineRecipeProvider extends RecipeProvider {

    public MachineRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
                .define('A', EIOTags.Items.INGOTS_COPPER_ALLOY)
                .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .pattern("ACA")
                .pattern("CRC")
                .pattern("ACA")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
                .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
                .define('B', MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get())
                .pattern("EEE")
                .pattern("BCB")
                .pattern("EEE")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.DOUBLE_LAYER_CAPACITOR).build()))
                .save(recipeOutput, EnderIO.loc("advanced_capacitor_bank_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get())
                .define('V', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .define('O', EIOItems.OCTADIC_CAPACITOR.get())
                .define('C', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
                .define('B', MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
                .pattern("VOV")
                .pattern("BCB")
                .pattern("VOV")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.OCTADIC_CAPACITOR).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get())
                .define('A', EIOTags.Items.INGOTS_COPPER_ALLOY)
                .define('O', EIOItems.OCTADIC_CAPACITOR.get())
                .define('C', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
                .pattern("AOA")
                .pattern("OCO")
                .pattern("AOA")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
                .save(recipeOutput, EnderIO.loc("vibrant_capacitor_bank_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.FLUID_TANK.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.PRESSURIZED_FLUID_TANK.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.ENCHANTER.get())
                .define('B', Items.BOOK)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .define('L', Items.LECTERN)
                .pattern(" B ")
                .pattern("DLD")
                .pattern("III")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BOOK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.ALLOY_SMELTER.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.STIRLING_GENERATOR.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SAG_MILL.get())
                .define('F', Items.FLINT)
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .define('G', EIOTags.Items.GEARS_IRON)
                .define('V', EIOBlocks.VOID_CHASSIS.get())
                .define('P', Items.PISTON)
                .pattern("FFF")
                .pattern("IVI")
                .pattern("GPG")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SLICE_AND_SPLICE.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.IMPULSE_HOPPER.get())
                .define('I', EIOTags.Items.INGOTS_COPPER_ALLOY)
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SOUL_BINDER.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.WIRED_CHARGER.get())
                .define('C', EIOTags.Items.INGOTS_COPPER_ALLOY)
                .define('V', EIOBlocks.VOID_CHASSIS.get())
                .pattern("CCC")
                .pattern("CVC")
                .pattern("CCC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
                .save(recipeOutput);

        ShapedEntityStorageRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.POWERED_SPAWNER)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SOUL_ENGINE)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.VACUUM_CHEST.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('C', Tags.Items.CHESTS)
                .define('P', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
                .pattern("III")
                .pattern("ICI")
                .pattern("IPI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.DRAIN.get())
                .define('I', EIOTags.Items.INGOTS_COPPER_ALLOY)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.XP_VACUUM)
                .pattern("III")
                .pattern("IRI")
                .pattern("IPI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', EIOItems.VOID_VIAL)
                .define('P', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.CRAFTER.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC))
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING))
                .define('I', EIOTags.Items.INGOTS_PULSATING_ALLOY)
                .define('F', EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ)
                .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
                .define('D', EIOTags.Items.DUSTS_COAL)
                .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR)
                .define('S', MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC))
                .pattern("IFI")
                .pattern("PDP")
                .pattern("CSC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC)))
                .save(recipeOutput,
                        EnderIO.loc(RecipeBuilder
                                .getDefaultRecipeId(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING))
                                .getPath()));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT))
                .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .define('F', EIOTags.Items.DARK_FUSED_QUARTZ)
                .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
                .define('G', Items.GLOWSTONE)
                .define('C', EIOItems.OCTADIC_CAPACITOR)
                .define('S', MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING))
                .pattern("IFI")
                .pattern("PGP")
                .pattern("CSC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING)))
                .save(recipeOutput,
                        EnderIO.loc(
                                RecipeBuilder.getDefaultRecipeId(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT))
                                        .getPath()));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.PAINTING_MACHINE.get())
                .pattern("RGB")
                .pattern("ICI")
                .pattern("MAM")
                .define('R', Tags.Items.DYES_RED)
                .define('G', Tags.Items.DYES_GREEN)
                .define('B', Tags.Items.DYES_BLUE)
                .define('I', EIOTags.Items.INGOTS_COPPER_ALLOY)
                .define('C', EIOBlocks.VOID_CHASSIS)
                .define('M', EIOTags.Items.GEARS_IRON)
                .define('A', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.TRAVEL_ANCHOR.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.XP_OBELISK.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.AVERSION_OBELISK.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.INHIBITOR_OBELISK.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.RELOCATOR_OBELISK.get())
                .define('P', Items.PRISMARINE)
                .define('A', MachineBlocks.AVERSION_OBELISK)
                .pattern("P")
                .pattern("A")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(MachineBlocks.AVERSION_OBELISK).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.ATTRACTOR_OBELISK.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.WEATHER_OBELISK.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.VAT.get())
                .define('B', Blocks.BARREL)
                .define('C', EIOBlocks.VOID_CHASSIS)
                .define('M', EIOTags.Items.GEARS_IRON)
                .define('A', EIOTags.Items.INGOTS_COPPER_ALLOY)
                .define('R', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .define('S', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern("SBS")
                .pattern("ACA")
                .pattern("MRM")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(Blocks.BARREL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.WIRELESS_CHARGER.get())
                .define('C', EIOTags.Items.INGOTS_COPPER_ALLOY)
                .define('V', EIOBlocks.VOID_CHASSIS.get())
                .define('E', EIOItems.ENDER_RESONATOR.get())
                .pattern("CCC")
                .pattern("CVC")
                .pattern("CEC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.WIRELESS_CHARGER_ANTENNA.get())
                .define('C', EIOTags.Items.INGOTS_COPPER_ALLOY)
                .define('S', EIOTags.Items.INGOTS_PULSATING_ALLOY)
                .define('E', EIOItems.ENDER_RESONATOR.get())
                .pattern(" S ")
                .pattern(" S ")
                .pattern("CEC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ENDER_RESONATOR.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get())
                .define('C', EIOTags.Items.INGOTS_COPPER_ALLOY)
                .define('S', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .define('E', EIOItems.SENTIENT_ENDER.get())
                .pattern(" S ")
                .pattern(" S ")
                .pattern("CEC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SENTIENT_ENDER.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.MIND_KILLER.get())
            .define('S', EIOTags.Items.INGOTS_SOULARIUM)
            .define('Z', EIOItems.ZOMBIE_ELECTRODE)
            .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .pattern(" Z ")
            .pattern("SES")
            .unlockedBy("has_ingredient",
                InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ZOMBIE_ELECTRODE.get()))
            .save(recipeOutput);

        // TODO: Enable once the block detector has a model.
        /*
         * ShapedRecipeBuilder .shaped(RecipeCategory.MISC,
         * MachineBlocks.BLOCK_DETECTOR.get()) .define('D',
         * EIOBlocks.DARK_STEEL_PRESSURE_PLATE) .define('N', EIOItems.DARK_STEEL_NUGGET)
         * .define('P', Items.PISTON) .define('R', Items.REDSTONE_BLOCK) .pattern("NDN")
         * .pattern("NPN") .pattern("NRN") .unlockedBy("has_ingredient",
         * InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().
         * of(Items.PISTON).build())) .save(recipeOutput);
         */
        eraseRecipes(recipeOutput);
    }

    private static final List<RegiliteBlock<?>> MACHINES = Util.make(() -> {
        List<RegiliteBlock<?>> list = new ArrayList<>();
        list.addAll(MachineBlocks.CAPACITOR_BANKS.values());
        list.add(MachineBlocks.FLUID_TANK);
        list.add(MachineBlocks.PRESSURIZED_FLUID_TANK);
        list.add(MachineBlocks.ALLOY_SMELTER);
        list.add(MachineBlocks.STIRLING_GENERATOR);
        list.add(MachineBlocks.SAG_MILL);
        list.add(MachineBlocks.SLICE_AND_SPLICE);
        list.add(MachineBlocks.SOUL_BINDER);
        list.add(MachineBlocks.WIRED_CHARGER);
        list.add(MachineBlocks.POWERED_SPAWNER);
        list.add(MachineBlocks.SOUL_ENGINE);
        list.add(MachineBlocks.DRAIN);
        list.add(MachineBlocks.CRAFTER);
        list.addAll(MachineBlocks.SOLAR_PANELS.values());
        list.add(MachineBlocks.PAINTING_MACHINE);
        list.add(MachineBlocks.IMPULSE_HOPPER);
        list.add(MachineBlocks.XP_OBELISK);
        list.add(MachineBlocks.AVERSION_OBELISK);
        list.add(MachineBlocks.INHIBITOR_OBELISK);
        list.add(MachineBlocks.RELOCATOR_OBELISK);
        list.add(MachineBlocks.ATTRACTOR_OBELISK);
        list.add(MachineBlocks.XP_VACUUM);
        list.add(MachineBlocks.VACUUM_CHEST);
        list.add(MachineBlocks.TRAVEL_ANCHOR);
        list.add(MachineBlocks.WIRELESS_CHARGER);
        return list;
    });

    public void eraseRecipes(RecipeOutput recipeOutput) {
        for (var block : MACHINES) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, block)
                    .requires(block)
                    .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block))
                    .save(recipeOutput, EnderIO.loc("erase_" + block.getId().getPath()));
        }
    }
}
