package com.enderio.machines.data.recipes;

import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.common.init.MachineBlocks;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
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
            .define('C', EIOBlocks.ENSNARED_CHASSIS.get())
            .define('B', Items.IRON_BARS)
            .define('H', Tags.Items.HEADS)
            .pattern("IHI")
            .pattern("ICI")
            .pattern("GBG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSNARED_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, MachineBlocks.IMPULSE_HOPPER.get())
            .define('I', EIOItems.COPPER_ALLOY_INGOT.get())
            .define('R', EIOItems.REDSTONE_ALLOY_INGOT.get())
            .define('G', EIOItems.GEAR_ENERGIZED.get())
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
            .define('C', EIOBlocks.ENSNARED_CHASSIS.get())
            .define('H', Tags.Items.HEADS)
            .pattern("IHI")
            .pattern("HCH")
            .pattern("IHI")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSNARED_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        // TODO: NBT Crafting with a broken spawner.
//        ShapedRecipeBuilder
//            .shaped(RecipeCategory.MISC, MachineBlocks.POWERED_SPAWNER.get())
//            .define('I', EIOItems.CONDUCTIVE_ALLOY_INGOT.get())
//            .define('H', Tags.Items.HEADS)
//            .define('C', EIOBlocks.ENSNARED_CHASSIS.get())
//            .define('Z', EIOItems.Z_LOGIC_CONTROLLER.get())
//            .define('V', EIOItems.VIBRANT_CRYSTAL.get())
//            .pattern("IHI")
//            .pattern("ICI")
//            .pattern("VZV")
//            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.ENSNARED_CHASSIS.get()))
//            .save(finishedRecipeConsumer);

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
    }
}
