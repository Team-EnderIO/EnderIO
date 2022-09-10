package com.enderio.machines.data.recipes;

import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.machines.common.init.MachineBlocks;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class MachineRecipeProvider extends RecipeProvider {
    public MachineRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        ShapedRecipeBuilder
            .shaped(MachineBlocks.PRIMITIVE_ALLOY_SMELTER.get())
            .define('F', Blocks.FURNACE)
            .define('D', Blocks.DEEPSLATE)
            .define('G', EIOItems.GRAINS_OF_INFINITY.get())
            .pattern("FFF")
            .pattern("DGD")
            .pattern("DDD")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(MachineBlocks.ALLOY_SMELTER.get())
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .define('F', Blocks.FURNACE)
            .define('C', EIOBlocks.VOID_CHASSIS.get())
            .define('G', EIOItems.GEAR_DARK_STEEL.get())
            .define('B', Blocks.CAULDRON)
            .pattern("IFI")
            .pattern("FCF")
            .pattern("GBG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(finishedRecipeConsumer);

        ShapedRecipeBuilder
            .shaped(MachineBlocks.SAG_MILL.get())
            .define('F', Items.FLINT)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .define('C', EIOBlocks.VOID_CHASSIS.get())
            .define('G', EIOItems.GEAR_DARK_STEEL.get())
            .define('P', Blocks.PISTON)
            .pattern("FFF")
            .pattern("ICI")
            .pattern("GPG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOBlocks.VOID_CHASSIS.get()))
            .save(finishedRecipeConsumer);
    }
}
