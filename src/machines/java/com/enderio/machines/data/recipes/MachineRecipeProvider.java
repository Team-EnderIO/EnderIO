package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.recipe.ShapedEntityStorageRecipeBuilder;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorTier;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.machines.common.init.MachineBlocks;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class MachineRecipeProvider extends RecipeProvider {

    public MachineRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', EIOItems.BASIC_CAPACITOR.get())
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .pattern("ICI")
            .pattern("CRC")
            .pattern("ICI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
            .define('A', EIOItems.COPPER_ALLOY_INGOT.get())
            .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .pattern("ACA")
            .pattern("CRC")
            .pattern("ACA")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
            .define('E', EIOItems.ENERGETIC_ALLOY_INGOT)
            .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
            .define('B', MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get())
            .pattern("EEE")
            .pattern("BCB")
            .pattern("EEE")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.DOUBLE_LAYER_CAPACITOR).build()))
            .save(finishedRecipeConsumer, EnderIO.loc("advanced_capacitor_bank_upgrade"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get())
            .define('V', EIOItems.VIBRANT_ALLOY_INGOT)
            .define('O', EIOItems.OCTADIC_CAPACITOR.get())
            .define('C', EIOItems.VIBRANT_CRYSTAL.get())
            .define('B', MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.ADVANCED).get())
            .pattern("VOV")
            .pattern("BCB")
            .pattern("VOV")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.OCTADIC_CAPACITOR).build()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get())
            .define('A', EIOItems.COPPER_ALLOY_INGOT.get())
            .define('O', EIOItems.OCTADIC_CAPACITOR.get())
            .define('C', EIOItems.VIBRANT_CRYSTAL.get())
            .pattern("AOA")
            .pattern("OCO")
            .pattern("AOA")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOItems.BASIC_CAPACITOR).build()))
            .save(finishedRecipeConsumer, EnderIO.loc("vibrant_capacitor_bank_upgrade"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.FLUID_TANK.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('B', Blocks.IRON_BARS)
            .define('G', Tags.Items.GLASS)
            .pattern("IBI")
            .pattern("BGB")
            .pattern("IBI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.PRESSURIZED_FLUID_TANK.get())
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .define('B', EIOBlocks.DARK_STEEL_BARS.get())
            .define('G', EIOTags.Items.FUSED_QUARTZ)
            .pattern("IBI")
            .pattern("BGB")
            .pattern("IBI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.ENCHANTER.get())
            .define('B', Items.BOOK)
            .define('D', Tags.Items.GEMS_DIAMOND)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .pattern("DBD")
            .pattern("III")
            .pattern(" I ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BOOK))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.PRIMITIVE_ALLOY_SMELTER.get())
            .define('F', Blocks.FURNACE)
            .define('D', Blocks.DEEPSLATE)
            .define('G', EIOItems.GRAINS_OF_INFINITY.get())
            .pattern("FFF")
            .pattern("DGD")
            .pattern("DDD")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.ALLOY_SMELTER.get())
            .define('F', Blocks.FURNACE)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .define('G', EIOItems.GEAR_DARK_STEEL.get())
            .define('C', Items.CAULDRON)
            .define('V', EIOBlocks.VOID_CHASSIS.get())
            .pattern("IFI")
            .pattern("FVF")
            .pattern("GCG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.STIRLING_GENERATOR.get())
            .define('B', Blocks.STONE_BRICKS)
            .define('F', Blocks.FURNACE)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .define('G', EIOItems.GEAR_DARK_STEEL.get())
            .define('V', EIOBlocks.VOID_CHASSIS.get())
            .define('P', Items.PISTON)
            .pattern("BFB")
            .pattern("IVI")
            .pattern("GPG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SAG_MILL.get())
            .define('F', Items.FLINT)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .define('G', EIOItems.GEAR_DARK_STEEL.get())
            .define('V', EIOBlocks.VOID_CHASSIS.get())
            .define('P', Items.PISTON)
            .pattern("FFF")
            .pattern("IVI")
            .pattern("GPG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SLICE_AND_SPLICE.get())
            .define('I', EIOItems.SOULARIUM_INGOT.get())
            .define('G', EIOItems.GEAR_ENERGIZED.get())
            .define('C', EIOBlocks.ENSOULED_CHASSIS.get())
            .define('B', Items.IRON_BARS)
            .define('H', Tags.Items.HEADS)
            .pattern("IHI")
            .pattern("ICI")
            .pattern("GBG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.IMPULSE_HOPPER.get())
            .define('I', EIOItems.COPPER_ALLOY_INGOT.get())
            .define('R', EIOItems.REDSTONE_ALLOY_INGOT.get())
            .define('G', EIOItems.GEAR_IRON.get())
            .define('C', EIOBlocks.VOID_CHASSIS.get())
            .define('H', Items.HOPPER)
            .pattern("IHI")
            .pattern("GCG")
            .pattern("IRI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        // TODO: Not a fan, at all...
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOUL_BINDER.get())
            .define('I', EIOItems.SOULARIUM_INGOT.get())
            .define('C', EIOBlocks.ENSOULED_CHASSIS.get())
            .define('G', EIOItems.GEAR_ENERGIZED)
            .define('Z', EIOItems.Z_LOGIC_CONTROLLER)
            .define('V', EIOItems.EMPTY_SOUL_VIAL)
            .pattern("IVI")
            .pattern("GCG")
            .pattern("IZI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.WIRED_CHARGER.get())
            .define('C', EIOItems.COPPER_ALLOY_INGOT.get())
            .define('V', EIOBlocks.VOID_CHASSIS.get())
            .pattern("CCC")
            .pattern("CVC")
            .pattern("CCC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        ShapedEntityStorageRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.POWERED_SPAWNER)
            .define('I', EIOItems.SOULARIUM_INGOT) //TODO Maybe also soulchains?
            .define('B', EIOItems.BROKEN_SPAWNER)
            .define('C', EIOBlocks.ENSOULED_CHASSIS)
            .define('Z', EIOItems.Z_LOGIC_CONTROLLER)
            .define('V', EIOItems.VIBRANT_CRYSTAL)
            .pattern("IBI")
            .pattern("ICI")
            .pattern("VZV")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSOULED_CHASSIS))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.VACUUM_CHEST.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', Tags.Items.CHESTS)
            .define('P', EIOItems.PULSATING_CRYSTAL.get())
            .pattern("III")
            .pattern("ICI")
            .pattern("IPI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.XP_VACUUM)
            .pattern("III")
            .pattern("IRI")
            .pattern("IPI")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('R', EIOItems.EXPERIENCE_ROD)
            .define('P', EIOItems.PULSATING_CRYSTAL)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.CRAFTER.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('G', EIOItems.GEAR_IRON.get())
            .define('C', EIOBlocks.VOID_CHASSIS.get())
            .define('S', EIOTags.Items.SILICON)
            .define('T', Items.CRAFTING_TABLE)
            .pattern("SSS")
            .pattern("ICI")
            .pattern("GTG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.SIMPLE))
            .define('C', EIOItems.COPPER_ALLOY_INGOT)
            .define('F', EIOTags.Items.CLEAR_GLASS)
            .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
            .define('I', EIOItems.GRAINS_OF_INFINITY)
            .define('G', EIOItems.GEAR_IRON)
            .pattern("CFC")
            .pattern("PPP")
            .pattern("IGI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PHOTOVOLTAIC_PLATE.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.BASIC))
            .define('E', EIOItems.ENERGETIC_ALLOY_INGOT)
            .define('F', EIOTags.Items.FUSED_QUARTZ)
            .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
            .define('C', EIOItems.BASIC_CAPACITOR)
            .define('D', Items.DAYLIGHT_DETECTOR)
            .pattern("EFE")
            .pattern("PPP")
            .pattern("CDC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PHOTOVOLTAIC_PLATE.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.BASIC))
            .define('E', EIOItems.ENERGETIC_ALLOY_INGOT)
            .define('F', EIOTags.Items.FUSED_QUARTZ)
            .define('P', MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.SIMPLE))
            .define('C', EIOItems.BASIC_CAPACITOR)
            .define('D', Items.DAYLIGHT_DETECTOR)
            .pattern("EFE")
            .pattern(" P ")
            .pattern("CDC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.SIMPLE)))
            .save(finishedRecipeConsumer, EnderIO.loc(RecipeBuilder.getDefaultRecipeId(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.BASIC)).getPath() + "_upgrade"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ADVANCED))
            .define('I', EIOItems.PULSATING_ALLOY_INGOT)
            .define('F', EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ)
            .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
            .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR)
            .define('D', Items.DAYLIGHT_DETECTOR)
            .pattern("IFI")
            .pattern("PPP")
            .pattern("CDC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PHOTOVOLTAIC_PLATE))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ADVANCED))
            .define('I', EIOItems.PULSATING_ALLOY_INGOT)
            .define('F', EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ)
            .define('E', EIOItems.ENERGETIC_ALLOY_INGOT)
            .define('P', EIOItems.POWDERED_COAL)
            .define('C', EIOItems.BASIC_CAPACITOR)
            .define('S', MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.BASIC))
            .pattern("IFI")
            .pattern("EPE")
            .pattern("CSC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.BASIC)))
            .save(finishedRecipeConsumer, EnderIO.loc(RecipeBuilder.getDefaultRecipeId(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ADVANCED)).getPath() + "_upgrade"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT))
            .define('I', EIOItems.VIBRANT_ALLOY_INGOT)
            .define('F', EIOTags.Items.DARK_FUSED_QUARTZ)
            .define('P', EIOItems.PHOTOVOLTAIC_PLATE)
            .define('C', EIOItems.OCTADIC_CAPACITOR)
            .define('D', Items.DAYLIGHT_DETECTOR)
            .pattern("IFI")
            .pattern("PPP")
            .pattern("CDC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PHOTOVOLTAIC_PLATE))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT))
            .define('I', EIOItems.VIBRANT_ALLOY_INGOT)
            .define('F', EIOTags.Items.DARK_FUSED_QUARTZ)
            .define('G', Items.GLOWSTONE)
            .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR)
            .define('P', MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ADVANCED))
            .pattern("IFI")
            .pattern("IGI")
            .pattern("CPC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ADVANCED)))
            .save(finishedRecipeConsumer, EnderIO.loc(RecipeBuilder.getDefaultRecipeId(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT)).getPath() + "_upgrade"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.PAINTING_MACHINE.get())
            .pattern("RGB")
            .pattern("ICI")
            .pattern("MAM")
            .define('R', Tags.Items.DYES_RED)
            .define('G', Tags.Items.DYES_GREEN)
            .define('B', Tags.Items.DYES_BLACK)
            .define('I', EIOItems.COPPER_ALLOY_INGOT)
            .define('C', EIOBlocks.VOID_CHASSIS)
            .define('M', EIOItems.GEAR_IRON)
            .define('A', EIOItems.REDSTONE_ALLOY_INGOT)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS))
            .save(finishedRecipeConsumer);
    }
}
