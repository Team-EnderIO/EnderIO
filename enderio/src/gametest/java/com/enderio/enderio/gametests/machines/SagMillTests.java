package com.enderio.enderio.gametests.machines;

import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
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

@ForEachTest(groups = "machines.sag_mill")
public class SagMillTests {

    @GameTest
    @TestHolder(description = "Tests that the SAG Mill can process basic ore grinding.")
    public static void testBasicOreGrinding(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.SAG_MILL.get().defaultBlockState()));

        int energyToAdd = 10000;
        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                .thenExecute(() -> {
                    // Insert the capacitor and fill with energy.
                    helper.insertIntoContainer(0, 1, 0, EIOItems.OCTADIC_CAPACITOR.get(), 1);
                    helper.provideEnergy(0, 1, 0, energyToAdd);
                })
                // Insert iron ore for grinding
                .thenExecute(() -> helper.insertIntoContainer(0, 1, 0, Items.IRON_ORE, 1))
                // Wait for the recipe to process
                .thenExecuteAfter(20, () -> {
                    // Verify input has been extracted
                    helper.assertContainerHasExactly(0, 1, 0, Items.IRON_ORE, 0);

                    // Verify that we have iron dust output (2 iron raw from ore)
                    helper.assertContainerHasExactly(0, 1, 0, Items.RAW_IRON, 2);

                    // Ensure energy was consumed correctly
                    var input = new SagMillingRecipe.Input(new ItemStack(Items.IRON_ORE, 1), GrindingBallData.IDENTITY);
                    var recipe = helper.getLevel().getRecipeManager().getRecipeFor(EIORecipes.SAG_MILLING.type().get(), input, helper.getLevel()).orElseThrow();
                    int expectedEnergy = energyToAdd - recipe.value().getBaseEnergyCost();
                    helper.assertEnergyStored(0, 1, 0, expectedEnergy);
                })
                .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests that the SAG Mill can process stone to cobblestone.")
    public static void testStoneGrinding(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.SAG_MILL.get().defaultBlockState()));

        int energyToAdd = 10000;
        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                .thenExecute(() -> {
                    // Insert the capacitor and fill with energy.
                    helper.insertIntoContainer(0, 1, 0, EIOItems.OCTADIC_CAPACITOR.get(), 1);
                    helper.provideEnergy(0, 1, 0, energyToAdd);
                })
                // Insert cobblestone for grinding
                .thenExecute(() -> helper.insertIntoContainer(0, 1, 0, Items.STONE, 2))
                // Wait for processing
                .thenExecuteAfter(40, () -> {
                    // Verify input was consumed
                    helper.assertContainerHasExactly(0, 1, 0, Items.STONE, 0);

                    // Should get gravel output from cobblestone
                    helper.assertContainerHasExactly(0, 1, 0, Items.COBBLESTONE, 2);

                    // Ensure energy was consumed correctly
                    var input = new SagMillingRecipe.Input(new ItemStack(Items.STONE, 1), GrindingBallData.IDENTITY);
                    var recipe = helper.getLevel().getRecipeManager().getRecipeFor(EIORecipes.SAG_MILLING.type().get(), input, helper.getLevel()).orElseThrow();
                    int expectedEnergy = energyToAdd - recipe.value().getBaseEnergyCost() * 2;
                    helper.assertEnergyStored(0, 1, 0, expectedEnergy);
                })
                .thenSucceed();
        });
    }
}

