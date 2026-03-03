package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

/**
 * Regression test for issue #1302 - Sponge Dupe.
 * https://github.com/Team-EnderIO/EnderIO/issues/1302
 * Bug: Placing N wet sponges in a fluid tank's fill input yields N+1 dry sponges.
 * Both fillInternal() and tryTankRecipe() process wet sponges, causing a double-consume
 * in the same tick.
 */
@ForEachTest(groups = "regression.issue1302")
public class Issue1302 {

    @GameTest
    @TestHolder(description = "Ensures that wet sponges are not duped when emptied in a fluid tank.")
    public static void testIssue1302(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.FLUID_TANK.get().defaultBlockState()));

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                // Insert 3 wet sponges into the tank (they will go into the fill input slot)
                .thenExecute(() -> helper.insertIntoContainer(0, 1, 0, Items.WET_SPONGE, 3))
                // Wait long enough for all sponges to be processed (tank acts every 5 ticks)
                .thenExecuteAfter(20, () -> {
                    // All 3 wet sponges should have been consumed
                    helper.assertSlotHasNoItem(0, 1, 0, FluidTankBlockEntity.FLUID_FILL_INPUT.getIndex());
                    // Should have produced exactly 3 dry sponges, not 4
                    helper.assertContainerHasExactly(0, 1, 0, Items.SPONGE, 3);
                    // Should have exactly 3000 mB of water in the tank
                    helper.assertContainerHasExactly(0, 1, 0, net.minecraft.world.level.material.Fluids.WATER, 3000);
                })
                .thenSucceed();
        });
    }
}
