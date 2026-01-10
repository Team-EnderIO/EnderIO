package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.content.machines.vat.VatBlockEntity;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

/**
 * Test for Issue #1211 - VAT should be input order independent
 * https://github.com/Team-EnderIO/EnderIO/issues/1211
 */
@ForEachTest(groups = "regression.issue1211")
public class Issue1211 {
    @GameTest
    @TestHolder(description = "Ensures that the VAT accepts reagents in any order.")
    public static void testIssue1211NormalOrder(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.VAT.get().defaultBlockState()));

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                // Insert recipe for Cloud Seed (using test recipe) in the normal order.
                .thenExecute(() -> {
                    helper.fillContainer(0, 0, 0, Fluids.WATER, VatBlockEntity.TANK_CAPACITY);
                    helper.insertIntoContainer(0, 0, 0, Items.DIRT, 1);
                    helper.insertIntoContainer(0, 0, 0, Items.COBBLESTONE, 1);
                })
                // Wait for recipe to complete
                .thenExecuteAfter(20, () -> {
                    // Verify that cloud seed was produced
                    long stored = helper.getAmountInHandler(0, 0, 0, EIOFluids.CLOUD_SEED.source().get());
                    helper.assertValueEqual(stored, 1000L, "output fluid amount");
                })
                .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Ensures that the VAT accepts reagents in reverse order.")
    public static void testIssue1211ReversedOrder(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.VAT.get().defaultBlockState()));

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                // Insert recipe for Cloud Seed (using test recipe) in the opposite order.
                .thenExecute(() -> {
                    helper.fillContainer(0, 0, 0, Fluids.WATER, VatBlockEntity.TANK_CAPACITY);
                    helper.insertIntoContainer(0, 0, 0, Items.COBBLESTONE, 1);
                    helper.insertIntoContainer(0, 0, 0, Items.DIRT, 1);
                })
                .thenExecuteAfter(20, () -> {
                    // Verify that cloud seed was produced
                    long stored = helper.getAmountInHandler(0, 0, 0, EIOFluids.CLOUD_SEED.source().get());
                    helper.assertValueEqual(stored, 1000L, "output fluid amount");
                })
                .thenSucceed();
        });
    }
}
