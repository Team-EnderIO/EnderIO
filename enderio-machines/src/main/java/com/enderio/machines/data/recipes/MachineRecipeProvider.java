package com.enderio.machines.data.recipes;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.recipe.ShapedEntityStorageRecipeBuilder;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorTier;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.machines.common.init.MachineBlocks;
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

import java.util.concurrent.CompletableFuture;

public class MachineRecipeProvider extends RecipeProvider {

    public MachineRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', EIOItems.BASIC_CAPACITOR.get())
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .pattern("ICI")
            .pattern("CRC")
            .pattern("ICI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get())
            .requires(MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC)).build()))
            .save(recipeOutput, EnderIO.loc("erase_basic_capacitor_bank"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
            .define('A', EIOTags.Items.INGOTS_COPPER_ALLOY)
            .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .pattern("ACA")
            .pattern("CRC")
            .pattern("ACA")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
            .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
            .define('B', MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get())
            .pattern("EEE")
            .pattern("BCB")
            .pattern("EEE")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.DOUBLE_LAYER_CAPACITOR).build()))
            .save(recipeOutput, EnderIO.loc("advanced_capacitor_bank_upgrade"));

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
            .requires(MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED)).build()))
            .save(recipeOutput, EnderIO.loc("erase_advanced_capacitor_bank"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get())
            .define('V', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
            .define('O', EIOItems.OCTADIC_CAPACITOR.get())
            .define('C', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
            .define('B', MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
            .pattern("VOV")
            .pattern("BCB")
            .pattern("VOV")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.OCTADIC_CAPACITOR).build()))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get())
            .define('A', EIOTags.Items.INGOTS_COPPER_ALLOY)
            .define('O', EIOItems.OCTADIC_CAPACITOR.get())
            .define('C', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
            .pattern("AOA")
            .pattern("OCO")
            .pattern("AOA")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
            .save(recipeOutput, EnderIO.loc("vibrant_capacitor_bank_upgrade"));

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get())
            .requires(MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT)).build()))
            .save(recipeOutput, EnderIO.loc("erase_vibrant_capacitor_bank"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.FLUID_TANK.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('B', Blocks.IRON_BARS)
            .define('G', Tags.Items.GLASS_BLOCKS)
            .pattern("IBI")
            .pattern("BGB")
            .pattern("IBI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.FLUID_TANK.get())
            .requires(MachineBlocks.FLUID_TANK)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.FLUID_TANK).build()))
            .save(recipeOutput, EnderIO.loc("erase_fluid_tank"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.PRESSURIZED_FLUID_TANK.get())
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('B', EIOBlocks.DARK_STEEL_BARS.get())
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .pattern("IBI")
            .pattern("BGB")
            .pattern("IBI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.PRESSURIZED_FLUID_TANK.get())
            .requires(MachineBlocks.PRESSURIZED_FLUID_TANK)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.PRESSURIZED_FLUID_TANK).build()))
            .save(recipeOutput, EnderIO.loc("erase_pressurised_fluid_tank"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.ENCHANTER.get())
            .define('B', Items.BOOK)
            .define('D', Tags.Items.GEMS_DIAMOND)
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .pattern("DBD")
            .pattern("III")
            .pattern(" I ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BOOK))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.PRIMITIVE_ALLOY_SMELTER.get())
            .define('F', Blocks.FURNACE)
            .define('D', Blocks.DEEPSLATE)
            .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
            .pattern("FFF")
            .pattern("DGD")
            .pattern("DDD")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.ALLOY_SMELTER.get())
            .define('F', Blocks.FURNACE)
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('G', EIOTags.Items.GEARS_IRON)
            .define('C', Items.CAULDRON)
            .define('V', EIOBlocks.VOID_CHASSIS.get())
            .pattern("IFI")
            .pattern("FVF")
            .pattern("GCG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.ALLOY_SMELTER.get())
            .requires(MachineBlocks.ALLOY_SMELTER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.ALLOY_SMELTER).build()))
            .save(recipeOutput, EnderIO.loc("erase_alloy_smelter"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.STIRLING_GENERATOR.get())
            .define('B', Blocks.STONE_BRICKS)
            .define('F', Blocks.FURNACE)
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('G', EIOTags.Items.GEARS_IRON)
            .define('V', EIOBlocks.VOID_CHASSIS.get())
            .define('P', Items.PISTON)
            .pattern("BFB")
            .pattern("IVI")
            .pattern("GPG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.STIRLING_GENERATOR.get())
            .requires(MachineBlocks.STIRLING_GENERATOR)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.STIRLING_GENERATOR).build()))
            .save(recipeOutput, EnderIO.loc("erase_sterling_generator"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SAG_MILL.get())
            .define('F', Items.FLINT)
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('G', EIOTags.Items.GEARS_IRON)
            .define('V', EIOBlocks.VOID_CHASSIS.get())
            .define('P', Items.PISTON)
            .pattern("FFF")
            .pattern("IVI")
            .pattern("GPG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.SAG_MILL.get())
            .requires(MachineBlocks.SAG_MILL)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.SAG_MILL).build()))
            .save(recipeOutput, EnderIO.loc("erase_sag_mill"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SLICE_AND_SPLICE.get())
            .define('I', EIOTags.Items.INGOTS_SOULARIUM)
            .define('G', EIOTags.Items.GEARS_ENERGIZED)
            .define('C', EIOBlocks.ENSOULED_CHASSIS.get())
            .define('B', Items.IRON_BARS)
            .define('H', ItemTags.SKULLS)
            .pattern("IHI")
            .pattern("ICI")
            .pattern("GBG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.SLICE_AND_SPLICE.get())
            .requires(MachineBlocks.SLICE_AND_SPLICE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.SLICE_AND_SPLICE).build()))
            .save(recipeOutput, EnderIO.loc("erase_slice_and_splice"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.IMPULSE_HOPPER.get())
            .define('I', EIOTags.Items.INGOTS_COPPER_ALLOY)
            .define('R', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .define('G', EIOTags.Items.GEARS_IRON)
            .define('C', EIOBlocks.VOID_CHASSIS.get())
            .define('H', Items.HOPPER)
            .pattern("IHI")
            .pattern("GCG")
            .pattern("IRI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.IMPULSE_HOPPER.get())
            .requires(MachineBlocks.IMPULSE_HOPPER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.IMPULSE_HOPPER).build()))
            .save(recipeOutput, EnderIO.loc("erase_impulse_hopper"));

        // TODO: Not a fan, at all...
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOUL_BINDER.get())
            .define('I', EIOTags.Items.INGOTS_SOULARIUM)
            .define('C', EIOBlocks.ENSOULED_CHASSIS.get())
            .define('G', EIOTags.Items.GEARS_ENERGIZED)
            .define('Z', EIOItems.Z_LOGIC_CONTROLLER)
            .define('V', EIOItems.EMPTY_SOUL_VIAL)
            .pattern("IVI")
            .pattern("GCG")
            .pattern("IZI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.SOUL_BINDER.get())
            .requires(MachineBlocks.SOUL_BINDER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.SOUL_BINDER).build()))
            .save(recipeOutput, EnderIO.loc("erase_soul_binder"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.WIRED_CHARGER.get())
            .define('C', EIOTags.Items.INGOTS_COPPER_ALLOY)
            .define('V', EIOBlocks.VOID_CHASSIS.get())
            .pattern("CCC")
            .pattern("CVC")
            .pattern("CCC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.WIRED_CHARGER.get())
            .requires(MachineBlocks.WIRED_CHARGER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.WIRED_CHARGER).build()))
            .save(recipeOutput, EnderIO.loc("erase_wired_charger"));

        ShapedEntityStorageRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.POWERED_SPAWNER)
            .define('I', EIOTags.Items.INGOTS_SOULARIUM) //TODO Maybe also soulchains?
            .define('B', EIOItems.BROKEN_SPAWNER)
            .define('C', EIOBlocks.ENSOULED_CHASSIS)
            .define('Z', EIOItems.Z_LOGIC_CONTROLLER)
            .define('V', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
            .pattern("IBI")
            .pattern("ICI")
            .pattern("VZV")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.POWERED_SPAWNER.get())
            .requires(MachineBlocks.POWERED_SPAWNER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.POWERED_SPAWNER).build()))
            .save(recipeOutput, EnderIO.loc("erase_powered_spawner"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOUL_ENGINE)
            .define('I', EIOTags.Items.INGOTS_SOULARIUM) //TODO Maybe also soulchains?
            .define('B', Items.BUCKET)
            .define('C', EIOBlocks.ENSOULED_CHASSIS)
            .define('Z', EIOItems.ZOMBIE_ELECTRODE)
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .pattern("IGI")
            .pattern("BCB")
            .pattern("IZI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.SOUL_ENGINE.get())
            .requires(MachineBlocks.SOUL_ENGINE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.SOUL_ENGINE).build()))
            .save(recipeOutput, EnderIO.loc("erase_soul_engine"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.VACUUM_CHEST.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', Tags.Items.CHESTS)
            .define('P', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
            .pattern("III")
            .pattern("ICI")
            .pattern("IPI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.DRAIN.get())
            .define('I', EIOTags.Items.INGOTS_COPPER_ALLOY)
            .define('V', EIOBlocks.VOID_CHASSIS)
            .define('C', EIOTags.Items.CLEAR_GLASS)
            .define('G', EIOTags.Items.GEARS_IRON)
            .define('B', Items.BUCKET)
            .pattern("ICI")
            .pattern("IVI")
            .pattern("GBG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOBlocks.VOID_CHASSIS).build()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.DRAIN.get())
            .requires(MachineBlocks.DRAIN)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.DRAIN).build()))
            .save(recipeOutput, EnderIO.loc("erase_drain"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.XP_VACUUM)
            .pattern("III")
            .pattern("IRI")
            .pattern("IPI")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('R', EIOItems.EXPERIENCE_ROD)
            .define('P', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CRAFTER.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('G', EIOTags.Items.GEARS_IRON)
            .define('C', EIOBlocks.VOID_CHASSIS.get())
            .define('S', EIOTags.Items.SILICON)
            .define('T', Items.CRAFTING_TABLE)
            .pattern("SSS")
            .pattern("ICI")
            .pattern("GTG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.CRAFTER.get())
            .requires(MachineBlocks.CRAFTER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.CRAFTER).build()))
            .save(recipeOutput, EnderIO.loc("erase_crafter"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC))
            .define('E', Tags.Items.INGOTS_GOLD)
            .define('F', Tags.Items.GLASS_BLOCKS)
            .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
            .define('C', EIOItems.BASIC_CAPACITOR)
            .define('D', Items.REDSTONE)
            .pattern("EFE")
            .pattern("PPP")
            .pattern("CDC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PHOTOVOLTAIC_PLATE.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC))
            .requires(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC)).build()))
            .save(recipeOutput, EnderIO.loc("erase_energetic_solar_panels"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING))
            .define('I', EIOTags.Items.INGOTS_PULSATING_ALLOY)
            .define('F', EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ)
            .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
            .define('D', EIOTags.Items.DUSTS_COAL)
            .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR)
            .define('S', MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC))
            .pattern("IFI")
            .pattern("PDP")
            .pattern("CSC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC)))
            .save(recipeOutput, EnderIO.loc(RecipeBuilder.getDefaultRecipeId(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING)).getPath()));

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING))
            .requires(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING)).build()))
            .save(recipeOutput, EnderIO.loc("erase_pulsating_solar_panels"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT))
            .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
            .define('F', EIOTags.Items.DARK_FUSED_QUARTZ)
            .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
            .define('G', Items.GLOWSTONE)
            .define('C', EIOItems.OCTADIC_CAPACITOR)
            .define('S', MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING))
            .pattern("IFI")
            .pattern("PGP")
            .pattern("CSC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING)))
            .save(recipeOutput, EnderIO.loc(RecipeBuilder.getDefaultRecipeId(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT)).getPath()));

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT))
            .requires(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT))
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT)).build()))
            .save(recipeOutput, EnderIO.loc("erase_vibrant_solar_panels"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.PAINTING_MACHINE.get())
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

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.TRAVEL_ANCHOR.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
            .define('B', EIOItems.CONDUIT_BINDER)
            .pattern("IBI")
            .pattern("BCB")
            .pattern("IBI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.PULSATING_CRYSTAL).build()))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.XP_OBELISK.get())
            .define('R', EIOItems.EXPERIENCE_ROD)
            .define('I', EIOTags.Items.INGOTS_SOULARIUM)
            .define('C', EIOBlocks.ENSOULED_CHASSIS)
            .pattern(" R ")
            .pattern(" I ")
            .pattern("ICI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.EXPERIENCE_ROD).build()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.XP_OBELISK.get())
            .requires(MachineBlocks.XP_OBELISK)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.XP_OBELISK).build()))
            .save(recipeOutput, EnderIO.loc("erase_xp_obelisk"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.AVERSION_OBELISK.get())
            .define('H', EIOBlocks.ENDERMAN_HEAD) //TODO Tormented Ender
            .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .define('I', EIOTags.Items.INGOTS_SOULARIUM)
            .define('C', EIOBlocks.ENSOULED_CHASSIS)
            .pattern(" H ")
            .pattern("EIE")
            .pattern("ICI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOBlocks.ENDERMAN_HEAD).build()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.AVERSION_OBELISK.get())
            .requires(MachineBlocks.AVERSION_OBELISK)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.AVERSION_OBELISK).build()))
            .save(recipeOutput, EnderIO.loc("erase_aversion_obelisk"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.INHIBITOR_OBELISK.get())
            .define('M', EIOItems.ENDER_CRYSTAL)
            .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .define('G', EIOItems.GEAR_IRON)
            .define('I', EIOTags.Items.INGOTS_SOULARIUM)
            .define('C', EIOBlocks.ENSOULED_CHASSIS)
            .pattern(" M ")
            .pattern("EGE")
            .pattern("ICI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.ENDER_CRYSTAL).build()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.INHIBITOR_OBELISK.get())
            .requires(MachineBlocks.INHIBITOR_OBELISK)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.INHIBITOR_OBELISK).build()))
            .save(recipeOutput, EnderIO.loc("erase_inhibitor_obelisk"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.RELOCATOR_OBELISK.get())
            .define('P', Items.PRISMARINE)
            .define('A', MachineBlocks.AVERSION_OBELISK)
            .pattern("P")
            .pattern("A")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.AVERSION_OBELISK).build()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, MachineBlocks.RELOCATOR_OBELISK.get())
            .requires(MachineBlocks.RELOCATOR_OBELISK)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(MachineBlocks.RELOCATOR_OBELISK).build()))
            .save(recipeOutput, EnderIO.loc("erase_relocator_obelisk"));
            
        // TODO: Enable once the block detector has a model.
        /*ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.BLOCK_DETECTOR.get())
            .define('D', EIOBlocks.DARK_STEEL_PRESSURE_PLATE)
            .define('N', EIOItems.DARK_STEEL_NUGGET)
            .define('P', Items.PISTON)
            .define('R', Items.REDSTONE_BLOCK)
            .pattern("NDN")
            .pattern("NPN")
            .pattern("NRN")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Items.PISTON).build()))
            .save(recipeOutput);*/
    }
}
