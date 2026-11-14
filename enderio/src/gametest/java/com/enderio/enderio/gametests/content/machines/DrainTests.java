package com.enderio.enderio.gametests.content.machines;

import com.enderio.enderio.content.machines.drain.DrainBlockEntity;
import com.enderio.enderio.foundation.attachment.ActionRange;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

@ForEachTest(groups = "machines.drain")
public class DrainTests {
    @GameTest(timeoutTicks = 1200)
    @TestHolder(description = "The drain must not destroy waterlogged blocks in its range.")
    public static void drainKeepsWaterloggedBlocks(final DynamicTest test) {
        test.registerGameTestTemplate(() -> {
            // A sealed stone basin with one water source (under the drain) and one waterlogged slab next to it.
            var builder = StructureTemplateBuilder.withSize(5, 3, 5);
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    builder.set(x, 0, z, Blocks.STONE.defaultBlockState());
                    builder.set(x, 1, z, Blocks.STONE.defaultBlockState());
                }
            }

            return builder
                .set(2, 1, 2, Blocks.WATER.defaultBlockState())
                .set(2, 1, 3, Blocks.OAK_SLAB.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true))
                .set(2, 2, 2, EIOBlocks.DRAIN.get().defaultBlockState());
        });

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            var drain = helper.requireBlockEntity(2, 3, 2, DrainBlockEntity.class);

            helper.startSequence().thenExecute(() -> {
                    // Insert a capacitor
                    helper.insertIntoContainer(2, 3, 2, EIOItems.OCTADIC_CAPACITOR.get(), 1);

                    // Smallest range that still covers the slab, keeps the scan short.
                    drain.setActionRange(new ActionRange(1, false));
                })
                // Wait until the drain has collected its first bucket, proving it has been working.
                .thenWaitUntil(() -> {
                    // Keep it topped up so energy is never the limiting factor.
                    drain.getEnergyStorage().addEnergy(100_000);

                    if (helper.getAmountInHandler(2, 3, 2, Fluids.WATER) < 1000) {
                        throw new GameTestAssertException("Waiting for the drain to collect a bucket of water");
                    }
                }).thenExecute(() -> helper.assertBlockPresent(Blocks.OAK_SLAB, 2, 2, 3)).thenSucceed();
        });
    }
}
