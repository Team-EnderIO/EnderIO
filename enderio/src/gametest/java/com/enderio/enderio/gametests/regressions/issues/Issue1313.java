package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.gametests.conduits.ConduitGameTestHelper;
import com.enderio.enderio.init.EIOConduits;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

/**
 * Regression test for issue #1313 - Disconnecting two conduits of differing tiers causes a crash
 * https://github.com/Team-EnderIO/EnderIO/issues/1313
 */
@ForEachTest(groups = "regression.issue1313")
public class Issue1313 {

    @GameTest
    @TestHolder(description = "Tests disconnection of two different conduit types doesn't crash")
    public static void testIssue1313(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(2, 1, 1));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);
            var energeticItemConduit = helper.getConduit(EIOConduits.ENERGETIC_ITEM);

            helper.startSequence()
                // Destroy all previous conduits
                .thenExecute(() -> helper.fillAir(1, 1, 1, 2, 1, 1))
                // Place two conduits
                .thenExecute(() -> helper.placeConduit(itemConduit, 1, 1, 1))
                .thenExecute(() -> helper.placeConduit(energeticItemConduit, 2, 1, 1))
                // Disconnect the two conduits
                .thenExecute(() -> helper
                    .getConduitBundle(1, 1, 1, false)
                    .disableNeighborConnection(Pair.of(Direction.EAST, itemConduit)))
                // Ensure the networks are distinct
                .thenExecute(() ->
                    helper.assertAllNetworksDiffer(itemConduit, 2, 1, 1, 2, 1, 1, false))
                .thenSucceed();
        });
    }
}
