package com.enderio.conduits.tests;

import com.enderio.conduits.common.init.Conduits;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

@ForEachTest(groups = "conduit.basic")
public class BasicConduitTests {
    @GameTest
    @TestHolder(description = "Ensures that placing conduits forms a network and breaking the middle splits the network.")
    public static void testPlacementAndNetworkSplitting(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 6)
                .set(0, 0, 0, Blocks.CHEST.defaultBlockState())
                .set(0, 0, 5, Blocks.CHEST.defaultBlockState()));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(Conduits.ITEM);

            helper.startSequence()
                    // Reset in case of repeated runs (in client)
                    .thenExecute(() -> helper.fillAir(0, 1, 1, 0, 1, 4))
                    // Build a row of conduits
                    .thenExecute(() -> helper.fillConduits(itemConduit, 0, 1, 1, 0, 1, 4))
                    // Ensure all conduits are on the same network
                    .thenExecute(() -> helper.assertAllNetworksMatch(itemConduit, 0, 1, 1, 0, 1, 4, false))
                    // Split the network in half
                    .thenExecute(() -> helper.setBlock(0, 1, 2, Blocks.AIR.defaultBlockState()))
                    // Ensure the remainder are all on the same network
                    .thenExecute(() -> helper.assertAllNetworksMatch(itemConduit, 0, 1, 3, 0, 1, 4, false))
                    // Ensure the two separated sets are different networks
                    .thenExecute(() -> helper.assertNetworksDifferBetween(itemConduit, 0, 1, 1, 0, 1, 3))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Ensures that placing a conduit which connects other networks merges correctly.")
    public static void testConduitNetworkMerging(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(3, 1, 3));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(Conduits.ITEM);

            helper.startSequence()
                    // Reset in case of repeated runs (in client)
                    .thenExecute(() -> helper.fillAir(0, 1, 0, 2, 1, 2))
                    // Build four separate conduits
                    .thenExecute(() -> helper.placeConduit(itemConduit, 1, 1, 0))
                    .thenExecute(() -> helper.placeConduit(itemConduit, 1, 1, 2))
                    .thenExecute(() -> helper.placeConduit(itemConduit, 0, 1, 1))
                    .thenExecute(() -> helper.placeConduit(itemConduit, 2, 1, 1))
                    // Ensure all conduits are on different networks
                    .thenExecute(() -> helper.assertAllNetworksDiffer(itemConduit, 0, 1, 1, 2, 1, 2, true))
                    // Now add a conduit in the middle to merge the networks
                    .thenExecute(() -> helper.placeConduit(itemConduit, 1, 1, 1))
                    // Now validate all networks are the same
                    .thenExecute(() -> helper.assertAllNetworksMatch(itemConduit, 0, 1, 1, 2, 1, 2, true))
                    .thenSucceed();
        });
    }

    // TODO: Test redstone signalling

    // TODO: Set up some chests and item conduits and ensure that when the network
    // breaks, transfers stop and re-adding the node resumes.

    // TODO: Add the following
    // - Ensure transfers stop when network is broken + resume when fixed.
    // - Check all yeta wrench interactions
}
