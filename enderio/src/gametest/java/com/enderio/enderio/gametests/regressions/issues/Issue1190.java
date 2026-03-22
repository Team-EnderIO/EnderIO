package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduit;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduitConnectionConfig;
import com.enderio.enderio.gametests.conduits.ConduitGameTestHelper;
import com.enderio.enderio.init.EIOConduits;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

/**
 * Regression tests for issue #1190 - Redstone Filters Broken (AND and NAND filters don't work).
 * https://github.com/Team-EnderIO/EnderIO/issues/1190
 * User scenario:
 * - Two redstone blocks with redstone conduits configured as INPUT (extract), one on GREEN channel, one on BROWN channel
 * - A redstone lamp with a redstone conduit configured as OUTPUT (insert) with an AND filter, output channel set to BLUE
 * - Expected: Lamp turns on when both GREEN and BROWN have signals
 * - Actual (bug): Lamp doesn't turn on
 */
@ForEachTest(groups = "regression.issue1190")
public class Issue1190 {
    
    @GameTest
    @TestHolder(description = "Tests that AND filter correctly outputs signal when both input channels are active")
    public static void testAndFilterWithTwoInputs(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(5, 2, 3)
            // Redstone block for GREEN channel input
            .set(0, 0, 0, Blocks.REDSTONE_BLOCK.defaultBlockState())
            // Redstone block for BROWN channel input
            .set(0, 0, 2, Blocks.REDSTONE_BLOCK.defaultBlockState())
            // Redstone lamp for output
            .set(4, 0, 1, Blocks.REDSTONE_LAMP.defaultBlockState()));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var redstoneConduit = helper.getConduit(EIOConduits.REDSTONE);
            final int tickRate = redstoneConduit.value().type().getTickRate(redstoneConduit);

            helper.startSequence()
                // Place conduits to form a network
                .thenExecute(() -> {
                    // (1,1,0) connects to GREEN redstone block
                    helper.placeConduit(redstoneConduit, 1, 0, 0);
                    // (1,1,1) is the center hub
                    helper.placeConduit(redstoneConduit, 1, 0, 1);
                    // (1,1,2) connects to BROWN redstone block
                    helper.placeConduit(redstoneConduit, 1, 0, 2);
                    // (2,1,1) middle conduit
                    helper.placeConduit(redstoneConduit, 2, 0, 1);
                    // (3,1,1) connects to lamp
                    helper.placeConduit(redstoneConduit, 3, 0, 1);
                })
                // Configure GREEN channel input (extract from redstone block)
                .thenExecute(() -> helper
                    .getConduitBundle(1, 0, 0, false)
                    .setConnectionConfig(redstoneConduit, Direction.WEST,
                        RedstoneConduitConnectionConfig.DEFAULT
                            .withIsExtract(true)
                            .withIsInsert(false)
                            .withExtractChannel(DyeColor.GREEN)))
                
                // Configure BROWN channel input (extract from redstone block)
                .thenExecute(() -> helper
                    .getConduitBundle(1, 0, 2, false)
                    .setConnectionConfig(redstoneConduit, Direction.WEST,
                        RedstoneConduitConnectionConfig.DEFAULT
                            .withIsExtract(true)
                            .withIsInsert(false)
                            .withExtractChannel(DyeColor.BROWN)))
                
                // Configure BLUE channel output (insert to lamp)
                .thenExecute(() -> helper
                    .getConduitBundle(3, 0, 1, false)
                    .setConnectionConfig(redstoneConduit, Direction.EAST,
                        RedstoneConduitConnectionConfig.DEFAULT
                            .withIsInsert(true)
                            .withIsExtract(false)
                            .withInsertChannel(DyeColor.BLUE)))
                
                // Insert AND filter into the output connection
                .thenExecute(() -> {
                    var bundle = helper.getConduitBundle(3, 0, 1, false);
                    var inventory = bundle.getConnectionInventory(redstoneConduit, Direction.EAST);
                    var andFilter = new ItemStack(EIOItems.AND_FILTER.get());
                    // The filter should have default channels (GREEN and BROWN) already set
                    inventory.setStackInSlot(RedstoneConduit.INSERT_FILTER_SLOT, andFilter);
                })
                
                // Wait for a few ticks to allow the network to process signals
                .thenExecuteAfter(tickRate * 3, () -> {
                    var lampState = helper.getBlockState(new BlockPos(4, 0, 1));
                    boolean isLit = lampState.getValue(RedstoneLampBlock.LIT);
                    
                    if (!isLit) {
                        throw helper.assertionException(
                            "Redstone lamp should be lit when AND filter receives signals on both GREEN and BROWN channels. " +
                            "This indicates the AND filter is not working correctly.");
                    }
                })
                .thenSucceed();
        });
    }
    
    @GameTest
    @TestHolder(description = "Tests that NAND filter correctly outputs inverse signal when both input channels are active")
    public static void testNandFilterWithTwoInputs(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(5, 2, 3)
            // Redstone block for GREEN channel input
            .set(0, 0, 0, Blocks.REDSTONE_BLOCK.defaultBlockState())
            // Redstone block for BROWN channel input
            .set(0, 0, 2, Blocks.REDSTONE_BLOCK.defaultBlockState())
            // Redstone lamp for output
            .set(4, 0, 1, Blocks.REDSTONE_LAMP.defaultBlockState()));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var redstoneConduit = helper.getConduit(EIOConduits.REDSTONE);
            final int tickRate = redstoneConduit.value().type().getTickRate(redstoneConduit);

            helper.startSequence()
                // Place conduits to form a network
                .thenExecute(() -> {
                    helper.placeConduit(redstoneConduit, 1, 0, 0);
                    helper.placeConduit(redstoneConduit, 1, 0, 1);
                    helper.placeConduit(redstoneConduit, 1, 0, 2);
                    helper.placeConduit(redstoneConduit, 2, 0, 1);
                    helper.placeConduit(redstoneConduit, 3, 0, 1);
                })
                
                // Configure GREEN channel input
                .thenExecute(() -> helper
                    .getConduitBundle(1, 0, 0, false)
                    .setConnectionConfig(redstoneConduit, Direction.WEST,
                        RedstoneConduitConnectionConfig.DEFAULT
                            .withIsExtract(true)
                            .withIsInsert(false)
                            .withExtractChannel(DyeColor.GREEN)))
                
                // Configure BROWN channel input
                .thenExecute(() -> helper
                    .getConduitBundle(1, 0, 2, false)
                    .setConnectionConfig(redstoneConduit, Direction.WEST,
                        RedstoneConduitConnectionConfig.DEFAULT
                            .withIsExtract(true)
                            .withIsInsert(false)
                            .withExtractChannel(DyeColor.BROWN)))
                
                // Configure BLUE channel output
                .thenExecute(() -> helper
                    .getConduitBundle(3, 0, 1, false)
                    .setConnectionConfig(redstoneConduit, Direction.EAST,
                        RedstoneConduitConnectionConfig.DEFAULT
                            .withIsInsert(true)
                            .withIsExtract(false)
                            .withInsertChannel(DyeColor.BLUE)))
                
                // Insert NAND filter into the output connection
                .thenExecute(() -> {
                    var bundle = helper.getConduitBundle(3, 0, 1, false);
                    var inventory = bundle.getConnectionInventory(redstoneConduit, Direction.EAST);
                    var nandFilter = new ItemStack(EIOItems.NAND_FILTER.get());
                    inventory.setStackInSlot(RedstoneConduit.INSERT_FILTER_SLOT, nandFilter);
                })
                
                // Wait for network to process
                .thenExecuteAfter(tickRate * 3, () -> {
                    var lampState = helper.getBlockState(new BlockPos(4, 0, 1));
                    boolean isLit = lampState.getValue(RedstoneLampBlock.LIT);
                    
                    if (isLit) {
                        throw helper.assertionException(
                            "Redstone lamp should NOT be lit when NAND filter receives signals on both GREEN and BROWN channels. " +
                            "NAND should output 0 when both inputs are active. This indicates the NAND filter is not working correctly.");
                    }
                })
                .thenSucceed();
        });
    }
}
