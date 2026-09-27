package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.content.machines.alloy.AlloySmelterBlockEntity;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

@ForEachTest(groups = "regression.issue1436")
public class Issue1436 {
    @GameTest(timeoutTicks = 650)
    @TestHolder(description = "Ensures an alloy smelter with 3 slots of standard material generates the right result count")
    public static void testIssue1436(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(2, 1, 2)
            .set(0, 0, 0, EIOBlocks.ALLOY_SMELTER.get().defaultBlockState())
            // TODO: We don't have creative power in 26.1 currently.
            //.set(1, 0, 0, EIOBlocks.CREATIVE_POWER.get().defaultBlockState())
            .set(0, 0, 1, Blocks.CHEST.defaultBlockState()));

        test.onGameTest(EnderGameTestHelper.class, helper ->
            helper.startSequence()
                // Insert raw gold and an octadic capacitor
                .thenExecute(() -> helper.insertIntoContainer(0, 0, 0, Items.RAW_GOLD, 64 * 3))
                .thenExecute(() -> helper.insertIntoContainer(0, 0, 0, EIOItems.OCTADIC_CAPACITOR.get(), 1))
                // Configure the machine to push all items to the chest
                .thenExecute(() -> {
                    var alloySmelter = helper.getBlockEntity(0, 0, 0, AlloySmelterBlockEntity.class);
                    alloySmelter.setIOMode(Direction.SOUTH, IOMode.PUSH);

                    // TODO: Temp, give it energy. Order changed to suit this, when creative power returns put this back before inserts
                    alloySmelter.getEnergyStorage().set(alloySmelter.getMaxEnergyStored());
                })
                // Ensure the right amount of Gold was produced, and all raw gold consumed.
                .thenExecuteAfter(600, () -> {
                    helper.assertContainerHasExactly(0, 0, 1, Items.GOLD_INGOT, 64 * 3);
                    helper.assertContainerHasExactly(0, 0, 0, Items.RAW_GOLD, 0);
                })
                .thenSucceed()
        );
    }
}
