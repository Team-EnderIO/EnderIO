package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduitConnectionConfig;
import com.enderio.enderio.gametests.conduits.ConduitGameTestHelper;
import com.enderio.enderio.init.EIOConduits;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

/**
 * Regression test for issue #1237 - Redstone conduits don't produce block update when state changes.
 * https://github.com/Team-EnderIO/EnderIO/issues/1237
 */
@ForEachTest(groups = "regression.issue1237")
public class Issue1237 {
    
    @GameTest
    @TestHolder(description = "Tests that pistons extend when receiving redstone signal via conduit")
    public static void testPistonExtensionViaRedstoneConduit(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(6, 2, 1)
            .set(0, 0, 0, Blocks.REDSTONE_BLOCK.defaultBlockState())
            .set(4, 0, 0, Blocks.PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.EAST)));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var redstoneConduit = helper.getConduit(EIOConduits.REDSTONE);
            final int tickRate = redstoneConduit.value().type().getTickRate(redstoneConduit);

            helper.startSequence()
                // Place conduits to form a network
                .thenExecute(() -> {
                    // connects to redstone block
                    helper.placeConduit(redstoneConduit, 1, 1, 0);
                    helper.placeConduit(redstoneConduit, 2, 1, 0);
                    // connects to piston
                    helper.placeConduit(redstoneConduit, 3, 1, 0);
                })
                
                // Configure RED channel input (extract from redstone block)
                .thenExecute(() -> helper
                    .getConduitBundle(1, 1, 0, false)
                    .setConnectionConfig(redstoneConduit, Direction.WEST,
                        RedstoneConduitConnectionConfig.DEFAULT
                            .withIsExtract(true)
                            .withIsInsert(false)
                            .withExtractChannel(DyeColor.RED)))
                
                // Configure RED channel output (insert to piston)
                .thenExecute(() -> helper
                    .getConduitBundle(3, 1, 0, false)
                    .setConnectionConfig(redstoneConduit, Direction.EAST,
                        RedstoneConduitConnectionConfig.DEFAULT
                            .withIsInsert(true)
                            .withIsExtract(false)
                            .withInsertChannel(DyeColor.RED)))
                
                // Wait for network to tick before checking
                .thenExecuteAfter(tickRate * 2, () -> {
                    var pistonState = helper.getBlockState(new BlockPos(4, 1, 0));
                    boolean isExtended = pistonState.getValue(PistonBaseBlock.EXTENDED);
                    
                    if (!isExtended) {
                        throw new GameTestAssertException(
                            "Piston should extend when redstone conduit transmits signal");
                    }
                })
                .thenSucceed();
        });
    }
}
