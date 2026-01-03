package com.enderio.enderio.gametests.machines;

import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.List;

@ForEachTest(groups = "machines.slicer")
public class SlicerTests {

    @GameTest
    @TestHolder(description = "Tests that the Slice'N'Splice can process a zombie electrode recipe.")
    public static void testSlicerRecipe(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.SLICE_AND_SPLICE.get().defaultBlockState()));

        int energyToAdd = 10000;
        test.onGameTest(EnderGameTestHelper.class, helper -> helper.startSequence()
            .thenExecute(() -> {
                // Insert the capacitor and fill with energy.
                helper.insertIntoContainer(0, 0, 0, EIOItems.OCTADIC_CAPACITOR.get(), 1);
                helper.provideEnergy(0, 0, 0, energyToAdd);
            })
            // Insert recipe ingredients for Zombie Electrode and axe + shears
            .thenExecute(() -> {
                helper.insertIntoContainer(0, 0, 0, EIOItems.ENERGETIC_ALLOY_INGOT.get(), 2);
                helper.insertIntoContainer(0, 0, 0, Items.ZOMBIE_HEAD, 1);
                helper.insertIntoContainer(0, 0, 0, EIOItems.SILICON.get(), 2);
                helper.insertIntoContainer(0, 0, 0, Items.COBBLESTONE, 1);
                helper.insertIntoContainer(0, 0, 0, Items.DIAMOND_AXE, 1);
                helper.insertIntoContainer(0, 0, 0, Items.SHEARS, 1);
            })
            // Wait for the recipe to process (low energy cost = 100, so should be fast)
            .thenExecuteAfter(20, () -> {
                // Verify inputs have been extracted
                helper.assertContainerHasExactly(0, 0, 0, EIOItems.ENERGETIC_ALLOY_INGOT.get(), 0);
                helper.assertContainerHasExactly(0, 0, 0, Items.ZOMBIE_HEAD, 0);
                helper.assertContainerHasExactly(0, 0, 0, EIOItems.SILICON.get(), 0);
                helper.assertContainerHasExactly(0, 0, 0, Items.COBBLESTONE, 0);

                // Verify that we have 1 Zombie Electrode in the output
                helper.assertContainerHasExactly(0, 0, 0, EIOItems.ZOMBIE_ELECTRODE.get(), 1);

                var input = new SlicingRecipe.Input(List.of(
                    new ItemStack(EIOItems.ENERGETIC_ALLOY_INGOT.get(), 1),
                    new ItemStack(Items.ZOMBIE_HEAD, 1),
                    new ItemStack(EIOItems.ENERGETIC_ALLOY_INGOT.get(), 1),
                    new ItemStack(EIOItems.SILICON.get(), 1),
                    new ItemStack(Items.COBBLESTONE, 1),
                    new ItemStack(EIOItems.SILICON.get(), 1)
                ));

                var recipe = helper.getLevel().recipeAccess().getRecipeFor(EIORecipes.SLICING.type().get(), input, helper.getLevel()).orElseThrow();
                int expectedEnergy = energyToAdd - recipe.value().energy();
                helper.assertEnergyStored(0, 0, 0, expectedEnergy);
            })
            .thenSucceed());
    }
}




