package com.enderio.conduits.tests.item;

import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.common.conduit.type.item.ItemConduit;
import com.enderio.conduits.common.conduit.type.item.ItemConduitConnectionConfig;
import com.enderio.conduits.common.init.Conduits;

import java.util.function.Supplier;

import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.RegisterStructureTemplate;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

@ForEachTest(groups = "conduit.item")
public class ItemConduitTests {

    private static final String THREE_CHESTS = EnderIOConduits.MODULE_MOD_ID + ":item_conduit_three_chests";

    // @formatter:off
    @RegisterStructureTemplate(THREE_CHESTS)
    public static final Supplier<StructureTemplate> THREE_CHESTS_TEMPLATE = StructureTemplateBuilder.lazy(3, 1, 3,
        builder -> builder
            // Sender
            .set(2, 0, 0, Blocks.CHEST.defaultBlockState())

            // Receivers
            .set(0, 0, 2, Blocks.CHEST.defaultBlockState())
            .set(2, 0, 2, Blocks.CHEST.defaultBlockState())
    );
    // @formatter:on

    @GameTest
    @TestHolder(description = "Tests basic item conduit functionality.")
    public static void itemConduitBasicTransfer(final DynamicTest test) {
        test.registerGameTestTemplate(
            () -> StructureTemplateBuilder.withSize(1, 1, 3).set(0, 0, 0, Blocks.CHEST.defaultBlockState()).set(0, 0, 2, Blocks.CHEST.defaultBlockState()));

        test.onGameTest(ItemConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(Conduits.ITEM);
            final int tickRate = itemConduit.value().networkTickRate();

            helper.startSequence()
                // Destroy any previous conduit
                .thenExecute(() -> helper.setBlock(0, 1, 1, Blocks.AIR))
                // Place a conduit between the two chests
                .thenExecute(() -> helper.placeConduit(itemConduit, 0, 1, 1))
                // Configure the extract end
                .thenExecute(() -> helper
                    .getConduitBundle(0, 1, 1, false)
                    .setConnectionConfig(itemConduit, Direction.NORTH,
                        ItemConduitConnectionConfig.DEFAULT.withIsExtract(true).withIsInsert(false).withExtractRedstoneControl(RedstoneControl.NEVER_ACTIVE)))
                // Configure the insert end
                .thenExecute(() -> helper
                    .getConduitBundle(0, 1, 1, false)
                    .setConnectionConfig(itemConduit, Direction.SOUTH, ItemConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
                // Put some dirt in the chest we'll extract from
                .thenExecute(() -> helper.insertIntoContainer(0, 1, 0, Items.DIRT, 1))
                // Ensure the item is still there
                .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(0, 1, 0, Items.DIRT, 1))
                // Now enable movement with redstone control
                .thenExecute(() -> helper
                    .getConduitBundle(0, 1, 1, false)
                    .setConnectionConfig(itemConduit, Direction.NORTH,
                        ItemConduitConnectionConfig.DEFAULT.withIsExtract(true).withIsInsert(false).withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
                // Ensure the item moves
                .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(0, 1, 2, Items.DIRT, 1))
                // Ensure no duplication
                .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(0, 1, 0, Items.DIRT, 0))
                .thenSucceed();
        });
    }

    @GameTest(template = THREE_CHESTS)
    @TestHolder(description = "Ensures item conduits prioritise closest container first.")
    public static void itemConduitDistancePriority(final ItemConduitGameTestHelper helper) {
        var itemConduit = helper.getConduit(Conduits.ITEM);
        final int tickRate = itemConduit.value().networkTickRate();

        helper.startSequence()
            // Destroy all previous conduits
            .thenExecute(() -> helper.fillAir(1, 1, 0, 1, 1, 2))
            // Place a conduit between the two chests
            .thenExecute(() -> helper.fillConduits(itemConduit, 1, 1, 0, 1, 1, 2))
            // Configure the insert ends
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 2, false)
                .setConnectionConfig(itemConduit, Direction.EAST, ItemConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 2, false)
                .setConnectionConfig(itemConduit, Direction.WEST, ItemConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
            // Configure the extract end
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(itemConduit, Direction.EAST,
                    ItemConduitConnectionConfig.DEFAULT.withIsExtract(true).withIsInsert(false).withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            // Put some dirt in the chest we'll extract from
            .thenExecute(() -> helper.insertIntoContainer(2, 1, 0, Items.DIRT, 1))
            // Ensure the item moves to the closer chest
            .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(2, 1, 2, Items.DIRT, 1))
            .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(0, 1, 2, Items.DIRT, 0))
            .thenSucceed();
    }

    @GameTest(template = THREE_CHESTS)
    @TestHolder(description = "Ensures item conduits prioritise highest priority container first, before closest.")
    public static void itemConduitManualPriority(final ItemConduitGameTestHelper helper) {
        var itemConduit = helper.getConduit(Conduits.ITEM);
        final int tickRate = itemConduit.value().networkTickRate();

        helper.startSequence()
            // Destroy all previous conduits
            .thenExecute(() -> helper.fillAir(1, 1, 0, 1, 1, 2))
            // Place a conduit between the two chests
            .thenExecute(() -> helper.fillConduits(itemConduit, 1, 1, 0, 1, 1, 2))
            // Configure the insert ends
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 2, false)
                .setConnectionConfig(itemConduit, Direction.EAST, ItemConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 2, false)
                .setConnectionConfig(itemConduit, Direction.WEST, ItemConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true).withPriority(2)))
            // Configure the extract end
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(itemConduit, Direction.EAST,
                    ItemConduitConnectionConfig.DEFAULT.withIsExtract(true).withIsInsert(false).withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            // Put some dirt in the chest we'll extract from
            .thenExecute(() -> helper.insertIntoContainer(2, 1, 0, Items.DIRT, 1))
            // Ensure the item moves to the closer chest
            .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(0, 1, 2, Items.DIRT, 1))
            // Place an item into the first chest
            .thenSucceed();
    }

    @GameTest(template = THREE_CHESTS)
    @TestHolder(description = "Ensures item conduit round robin works.")
    public static void itemConduitRoundRobin(final ItemConduitGameTestHelper helper) {
        var itemConduit = helper.getConduit(Conduits.ITEM);
        final int tickRate = itemConduit.value().networkTickRate();
        final int transferRate = ((ItemConduit) itemConduit.value()).transferRatePerCycle();

        helper.startSequence()
            // Destroy all previous conduits
            .thenExecute(() -> helper.fillAir(1, 1, 0, 1, 1, 2))
            // Place a conduit between the two chests
            .thenExecute(() -> helper.fillConduits(itemConduit, 1, 1, 0, 1, 1, 2))
            // Configure the insert ends
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 2, false)
                .setConnectionConfig(itemConduit, Direction.EAST, ItemConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 2, false)
                .setConnectionConfig(itemConduit, Direction.WEST, ItemConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
            // Configure the extract end
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(itemConduit, Direction.EAST, ItemConduitConnectionConfig.DEFAULT
                    .withIsExtract(true)
                    .withIsInsert(false)
                    .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)
                    .withIsRoundRobin(true)))
            // Put some dirt in the chest we'll extract from, 2x transfer rate of our
            // conduit so we can follow two network ticks.
            .thenExecute(() -> helper.insertIntoContainer(2, 1, 0, Items.DIRT, transferRate * 2))
            // Ensure the max transfer amount reaches the closest chest first, then the
            // second chest
            .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(2, 1, 2, Items.DIRT, transferRate))
            .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(0, 1, 2, Items.DIRT, transferRate))
            // Place an item into the first chest
            .thenSucceed();
    }

    // TODO: Test item filters?
}
