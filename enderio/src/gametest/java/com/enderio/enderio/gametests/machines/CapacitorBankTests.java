package com.enderio.enderio.gametests.machines;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorTier;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.RegisterStructureTemplate;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.function.Supplier;

@ForEachTest(groups = "machines.capacitor_bank")
public class CapacitorBankTests {

    private static final String TWO_CAPACITOR_BANKS = EnderIO.MOD_ID + ":two_capacitor_banks";
    private static final String SPLIT_CAPACITOR_BANKS = EnderIO.MOD_ID + ":split_capacitor_banks";

    // @formatter:off
    @RegisterStructureTemplate(TWO_CAPACITOR_BANKS)
    public static final Supplier<StructureTemplate> TWO_CAPACITOR_BANKS_TEMPLATE = StructureTemplateBuilder.lazy(1, 2, 1,
        builder -> builder
            .set(0, 0, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState())
            .set(0, 1, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState())
    );

    @RegisterStructureTemplate(SPLIT_CAPACITOR_BANKS)
    public static final Supplier<StructureTemplate> SPLIT_CAPACITOR_BANKS_TEMPLATE = StructureTemplateBuilder.lazy(1, 3, 1,
        builder -> builder
            .set(0, 0, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState())
            .set(0, 2, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState())
    );
    // @formatter:on

    @GameTest
    @TestHolder(description = "Tests that the capacitor bank can have power.")
    public static void testSingleCapBank(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState()));

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                .thenExecute(() -> helper.provideEnergy(0,1,0, CapacitorTier.BASIC.getStorageCapacity() / 2))
                .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,1,0, CapacitorTier.BASIC.getStorageCapacity() / 2)) //Test the energy is stored
                .thenExecute(() -> helper.provideEnergy(0,1,0, CapacitorTier.BASIC.getStorageCapacity()))
                .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,1,0, CapacitorTier.BASIC.getStorageCapacity())) //Test the energy is stored
                .thenSucceed();
        });
    }

    @GameTest(template = TWO_CAPACITOR_BANKS)
    @TestHolder(description = "Tests that the double capacitor bank can have power.")
    public static void testDoubleCapBank(final EnderGameTestHelper helper) {
        helper.startSequence()
            .thenExecute(() -> helper.provideEnergy(0,1,0, CapacitorTier.BASIC.getStorageCapacity()))
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,1,0, CapacitorTier.BASIC.getStorageCapacity())) //Test the energy is stored
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,2,0, CapacitorTier.BASIC.getStorageCapacity())) //Test the energy is stored
            .thenExecute(() -> helper.provideEnergy(0,1,0, CapacitorTier.VIBRANT.getStorageCapacity()))
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,1,0, 2 * CapacitorTier.BASIC.getStorageCapacity()))
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,2,0, 2 * CapacitorTier.BASIC.getStorageCapacity()))
            .thenSucceed();
    }

    @GameTest(template = TWO_CAPACITOR_BANKS)
    @TestHolder(description = "Tests that that removing a cap bank does not delete the power.")
    public static void testRemoveCapBank(final EnderGameTestHelper helper) {
        helper.startSequence()
            .thenExecute(() -> helper.provideEnergy(0,1,0, CapacitorTier.BASIC.getStorageCapacity()))
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,1,0, CapacitorTier.BASIC.getStorageCapacity())) //Test the energy is stored
            .thenExecute(() -> helper.destroyBlock(new BlockPos(0,2,0))) //Remove the Block
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,1,0, CapacitorTier.BASIC.getStorageCapacity())) //Test the energy is stored
            .thenExecute(() -> helper.setBlock(new BlockPos(0,2,0), EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState())) //Set the block
            .thenExecute(() -> helper.provideEnergy(0,2,0, CapacitorTier.BASIC.getStorageCapacity()))
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,1,0, 2 * CapacitorTier.BASIC.getStorageCapacity()))
            .thenExecute(() -> helper.destroyBlock(new BlockPos(0,2,0))) //Remove the Block
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,1,0, CapacitorTier.BASIC.getStorageCapacity()))
            .thenSucceed();
    }

    @GameTest(template = SPLIT_CAPACITOR_BANKS)
    @TestHolder(description = "Tests that that cap banks can merge")
    public static void testMergeCapBank(final EnderGameTestHelper helper) {
        helper.startSequence()
            .thenExecute(() -> helper.provideEnergy(0,1,0, CapacitorTier.BASIC.getStorageCapacity()))
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,1,0, CapacitorTier.BASIC.getStorageCapacity())) //Test the energy is stored
            .thenExecute(() -> helper.provideEnergy(0,3,0, CapacitorTier.BASIC.getStorageCapacity()))
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,3,0, CapacitorTier.BASIC.getStorageCapacity())) //Test the energy is stored
            .thenExecute(() -> helper.setBlock(new BlockPos(0,2,0), EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState()))
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,1,0, 2 * CapacitorTier.BASIC.getStorageCapacity())) //Test the energy is stored
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,2,0, 2 * CapacitorTier.BASIC.getStorageCapacity())) //Test the energy is stored
            .thenExecuteAfter(20, () -> helper.assertEnergyStored(0,3,0, 2 * CapacitorTier.BASIC.getStorageCapacity()))
            .thenSucceed();
    }
}
