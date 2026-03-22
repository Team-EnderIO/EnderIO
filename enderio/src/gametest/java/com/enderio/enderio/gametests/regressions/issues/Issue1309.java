package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.gametests.conduits.ConduitGameTestHelper;
import com.enderio.enderio.content.conduits.type.item.ItemConduitConnectionConfig;
import com.enderio.enderio.init.EIOConduits;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

/**
 * Regression test for issue #1309 - Item Conduits do not transfer if connection is broken with adjacent conduit.
 * https://github.com/Team-EnderIO/EnderIO/issues/1309
 */
@ForEachTest(groups = "regression.issue1309")
public class Issue1309 {

    @GameTest
    @TestHolder(description = "Tests that item conduits can still transfer after breaking connection between adjacent conduits.")
    public static void testIssue1309(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(5, 1, 3)
            .set(1, 0, 2, Blocks.CHEST.defaultBlockState())
            .set(2, 0, 2, Blocks.CHEST.defaultBlockState())
            .set(3, 0, 2, Blocks.CHEST.defaultBlockState())
            .set(0, 0, 1, Blocks.CHEST.defaultBlockState())
            .set(4, 0, 1, Blocks.CHEST.defaultBlockState())
        );

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);
            final int tickRate = itemConduit.value().type().getTickRate(itemConduit);

            helper.startSequence()
                // Destroy all previous conduits
                .thenExecute(() -> helper.fillAir(1, 0, 1, 3, 0, 1))
                // Place a conduit between the two chests
                .thenExecute(() -> helper.fillConduits(itemConduit, 1, 0, 1, 3, 0, 1))
                // Configure inserts
                .thenExecute(() -> helper
                    .getConduitBundle(1, 0, 1, false)
                    .setConnectionConfig(itemConduit, Direction.SOUTH, ItemConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
                .thenExecute(() -> helper
                    .getConduitBundle(2, 0, 1, false)
                    .setConnectionConfig(itemConduit, Direction.SOUTH, ItemConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
                .thenExecute(() -> helper
                    .getConduitBundle(3, 0, 1, false)
                    .setConnectionConfig(itemConduit, Direction.SOUTH, ItemConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
                // Configure extracts
                .thenExecute(() -> helper
                    .getConduitBundle(1, 0, 1, false)
                    .setConnectionConfig(itemConduit, Direction.WEST, ItemConduitConnectionConfig.DEFAULT
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)
                        .withIsExtract(true)
                        .withIsInsert(false)))
                .thenExecute(() -> helper
                    .getConduitBundle(3, 0, 1, false)
                    .setConnectionConfig(itemConduit, Direction.EAST, ItemConduitConnectionConfig.DEFAULT
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)
                        .withIsExtract(true)
                        .withIsInsert(false)))
                .thenExecute(() -> helper
                    .getConduitBundle(2, 0, 1, false)
                    .disableNeighborConnection(Pair.of(Direction.EAST, itemConduit)))
                // Ensure the networks are distinct
                .thenExecute(() ->
                    helper.assertAllNetworksDiffer(itemConduit, 2, 0, 1, 3, 0, 1, false))
                // Ensure first network can transfer items
                .thenExecute(() ->
                    helper.insertIntoContainer(0, 0, 1, Items.DIRT, 1))
                .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(1, 0, 2, Items.DIRT, 1))
                // Ensure second network can transfer items
                .thenExecute(() ->
                    helper.insertIntoContainer(4, 0, 1, Items.DIRT, 1))
                .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(3, 0, 2, Items.DIRT, 1))
                .thenSucceed();
        });
    }
}
