package com.enderio.enderio.gametests.machines;

import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipes;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.List;

@ForEachTest(groups = "machines.alloy_smelter")
public class AlloySmelterTests {

    @GameTest
    @TestHolder(description = "Tests that the Alloy Smelter can process a basic alloy recipe.")
    public static void testAlloySmelterAlloyingRecipe(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.ALLOY_SMELTER.get().defaultBlockState()));

        int energyToAdd = 10000;
        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                .thenExecute(() -> {
                    // Insert the capacitor and fill with energy.
                    helper.insertIntoContainer(0, 1, 0, EIOItems.OCTADIC_CAPACITOR.get(), 1);
                    helper.provideEnergy(0, 1, 0, energyToAdd);
                })
                // Insert recipe ingredients for Dark Steel
                .thenExecute(() -> {
                    helper.insertIntoContainer(0, 1, 0, Items.IRON_INGOT, 1);
                    helper.insertIntoContainer(0, 1, 0, Items.COAL, 2);
                    helper.insertIntoContainer(0, 1, 0, Items.OBSIDIAN, 1);
                })
                // Wait for the recipe to process
                .thenExecuteAfter(20, () -> {
                    // Verify inputs have been extracted
                    helper.assertContainerHasExactly(0, 1, 0, Items.IRON_INGOT, 0);
                    helper.assertContainerHasExactly(0, 1, 0, Items.COAL, 0);
                    helper.assertContainerHasExactly(0, 1, 0, Items.OBSIDIAN, 0);

                    // Verify that we have 1 Dark Steel Ingot in the output
                    helper.assertContainerHasExactly(0, 1, 0, EIOItems.DARK_STEEL_INGOT.get(), 1);

                    // Ensure energy was consumed correctly
                    var input = new AlloySmeltingRecipe.Input(List.of(
                        new ItemStack(Items.IRON_INGOT, 1),
                        new ItemStack(Items.COAL, 2),
                        new ItemStack(Items.OBSIDIAN, 1)
                    ), 1);

                    // Ensure energy was consumed correctly
                    var recipe = helper.getLevel().getRecipeManager().getRecipeFor(EIORecipes.ALLOY_SMELTING.type().get(), input, helper.getLevel()).orElseThrow();
                    int expectedEnergy = energyToAdd - recipe.value().energy();
                    helper.assertEnergyStored(0, 1, 0, expectedEnergy);
                })
                .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests that the Alloy Smelter can process vanilla smelting recipes (Iron Ingot from ore).")
    public static void testAlloySmelterVanillaSmeltingRecipe(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.ALLOY_SMELTER.get().defaultBlockState()));

        int energyToAdd = 10000;
        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                .thenExecute(() -> {
                    // Insert the capacitor and fill with energy.
                    helper.insertIntoContainer(0, 1, 0, EIOItems.OCTADIC_CAPACITOR.get(), 1);
                    helper.provideEnergy(0, 1, 0, energyToAdd);
                })
                // Insert raw iron (3 pieces to test batch smelting)
                .thenExecute(() -> helper.insertIntoContainer(0, 1, 0, Items.RAW_IRON, 3))
                // Wait for processing
                .thenExecuteAfter(20, () -> {
                    // Verify input was consumed
                    helper.assertContainerHasExactly(0, 1, 0, Items.RAW_IRON, 0);

                    // Should get 3 iron ingots from 3 raw iron
                    helper.assertContainerHasExactly(0, 1, 0, Items.IRON_INGOT, 3);

                    // Ensure energy was consumed correctly
                    var input = new AlloySmeltingRecipe.Input(List.of(
                        new ItemStack(Items.RAW_IRON, 3),
                        ItemStack.EMPTY,
                        ItemStack.EMPTY
                    ), 3);

                    var recipe = helper.getLevel().getRecipeManager().getRecipeFor(EIORecipes.ALLOY_SMELTING.type().get(), input, helper.getLevel()).orElseThrow();
                    int expectedEnergy = energyToAdd - recipe.value().energy();
                    helper.assertEnergyStored(0, 1, 0, expectedEnergy);
                })
                .thenSucceed();
        });
    }
}

