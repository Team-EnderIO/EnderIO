package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

/**
 * Regression tests for issue #1191 - Fluid tank infinitely produces / dupes xp juice from two Bottles o' Enchanting.
 * https://github.com/Team-EnderIO/EnderIO/issues/1191
 */
@ForEachTest(groups = "regression.issue1191")
public class Issue1191 {
    @GameTest
    @TestHolder(description = "Ensures that experience cannot be duped using the fluid tank.")
    public static void testIssue1191(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 3)
            .set(0, 0, 0, Blocks.CHEST.defaultBlockState())
            .set(0, 0, 1, EIOBlocks.FLUID_TANK.get().defaultBlockState())
            .set(0, 0, 2, EIOBlocks.XP_OBELISK.get().defaultBlockState()));

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                // Setup fluid tank as described by the issue
                .thenExecute(() -> helper.changeIoConfig(0, 0, 1, ioConfigurable -> {
                    ioConfigurable.setIOMode(Direction.NORTH, IOMode.BOTH);
                    ioConfigurable.setIOMode(Direction.SOUTH, IOMode.PUSH);
                }))
                // Insert 2 Bottles o' Enchanting into the tank
                .thenExecute(() -> helper.insertIntoContainer(0, 0, 1, Items.EXPERIENCE_BOTTLE, 2))
                // Make sure we have exactly 2 Bottles o' Enchanting worth of xp juice in the tank after 5 seconds
                .thenExecuteAfter(20 * 5, () -> helper.assertContainerHasExactly(0, 0, 2, EIOFluids.XP_JUICE.source().get(), 500))
                .thenSucceed();
        });
    }
}
