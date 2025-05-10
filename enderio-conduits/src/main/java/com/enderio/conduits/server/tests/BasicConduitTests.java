package com.enderio.conduits.server.tests;

import com.enderio.conduits.api.EnderIOConduitsRegistries;
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

            var itemConduit = helper.getLevel().registryAccess()
                .registryOrThrow(EnderIOConduitsRegistries.Keys.CONDUIT)
                .getHolderOrThrow(Conduits.ITEM);

            helper.startSequence()
                // Reset in case of repeated runs (in client)
                .thenExecute(() -> helper.fillAir(0, 1, 1, 0, 1, 4))
                // Build a row of conduits
                .thenExecute(() -> helper.fillConduits(itemConduit, 0, 1, 1, 0, 1, 4))
                // Ensure all conduits are on the same network
                .thenExecute(() -> helper.assertAllConduitNodesSameNetwork(itemConduit, 0, 1, 1, 0, 1, 4))
                // Split the network in half
                .thenExecute(() -> helper.setBlock(0, 1, 2, Blocks.AIR.defaultBlockState()))
                // Ensure the remainder are all on the same network
                .thenExecute(() -> helper.assertAllConduitNodesSameNetwork(itemConduit, 0, 1, 3, 0, 1, 4))
                // Ensure the two separated sets are different networks
                .thenExecute(() -> helper.assertConduitNodesDifferentNetwork(itemConduit, 0, 1, 1, 0, 1, 3))
                .thenSucceed();
        });
    }
}
