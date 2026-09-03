package com.enderio.enderio.gametests.content.storage;

import com.enderio.enderio.gametests.EnderIOTests;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

@ForEachTest(groups = "content.storage")
public class FluidTankTests {
    @GameTest
    @TestHolder(description = "Ensures the fluid tank can store fluids with components")
    public static void fluidTankStoresComponents(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.FLUID_TANK.get().defaultBlockState()));

        var fluidToInsert = new FluidStack(Fluids.WATER, 1000);
        fluidToInsert.set(EnderIOTests.TEST_NUMBER, 2);

        // Ensure that after insertion, we could still extract the same fluid with components in tact.
        test.onGameTest(EnderGameTestHelper.class, helper -> helper.startSequence()
            .thenExecute(() -> helper.fillContainer(0, 1, 0, fluidToInsert))
            .thenExecute(() -> helper.assertContainerHasExactly(0, 1, 0, fluidToInsert))
            .thenSucceed());
    }
}
