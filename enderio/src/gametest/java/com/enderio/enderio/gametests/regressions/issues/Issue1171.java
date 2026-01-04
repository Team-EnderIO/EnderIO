package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

/**
 * Regression test for issue #1171 - BlockDetector doesn't update neighbors.
 * https://github.com/Team-EnderIO/EnderIO/issues/1171
 * 
 * Test scenario:
 * - BlockDetector faces a block to detect
 * - A stone block is placed in front of the detector
 * - A lamp is on the other side of the stone block and should turn off when the detector is active.
 * - Bug: The lamp stays lit because the detector doesn't notify neighbors
 */
@ForEachTest(groups = "regression.issue1171")
public class Issue1171 {
    
    @GameTest
    @TestHolder(description = "Tests that BlockDetector properly updates neighbors when detected block changes")
    public static void testBlockDetectorUpdatesNeighbors(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(4, 2, 1)
            // Block to detect
            .set(0, 0, 0, Blocks.STONE.defaultBlockState())
            // BlockDetector facing west (toward the stone)
            .set(1, 0, 0, EIOBlocks.BLOCK_DETECTOR.get().defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.WEST))
            .set(2, 0, 0, Blocks.STONE.defaultBlockState())
            // Redstone lamp connected to detector
            .set(3, 0, 0, Blocks.REDSTONE_LAMP.defaultBlockState()));

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                // Ensure the detector and lamp are in the correct state
                .thenExecute(() -> {
                    // Verify detector is powered
                    var detectorState = helper.getBlockState(new BlockPos(1, 1, 0));
                    if (!detectorState.getValue(BlockStateProperties.POWERED)) {
                        throw new GameTestAssertException(
                            "BlockDetector should be powered when detecting a block");
                    }
                    
                    // Verify lamp is lit
                    var lampState = helper.getBlockState(new BlockPos(3, 1, 0));
                    if (!lampState.getValue(RedstoneLampBlock.LIT)) {
                        throw new GameTestAssertException(
                            "Redstone lamp should be lit when BlockDetector is powered");
                    }
                })
                // Remove the detected block
                .thenExecute(() -> helper.setBlock(new BlockPos(0, 1, 0), Blocks.AIR))
                // Ensure the detector stops detecting immediately
                .thenExecute(() -> {
                    // Verify detector is no longer powered
                    var detectorState = helper.getBlockState(new BlockPos(1, 1, 0));
                    if (detectorState.getValue(BlockStateProperties.POWERED)) {
                        throw new GameTestAssertException(
                            "BlockDetector should not be powered when block is removed");
                    }
                })
                // Wait for the lamp to change (4 ticks delay in the redstone lamp block code)
                .thenExecuteAfter(4, () -> {
                    // This is the crux of the test - verify the lamp is now off
                    var lampState = helper.getBlockState(new BlockPos(3, 1, 0));
                    if (lampState.getValue(RedstoneLampBlock.LIT)) {
                        throw new GameTestAssertException(
                            "Redstone lamp should turn off when BlockDetector stops outputting power. " +
                            "This indicates the BlockDetector is not properly notifying neighbors of state changes.");
                    }
                })
                .thenSucceed();
        });
    }
}
