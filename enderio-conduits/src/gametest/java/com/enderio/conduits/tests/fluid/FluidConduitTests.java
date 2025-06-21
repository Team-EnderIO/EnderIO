package com.enderio.conduits.tests.fluid;

import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduit;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitConnectionConfig;
import com.enderio.conduits.common.init.Conduits;
import com.enderio.machines.common.init.MachineBlocks;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.RegisterStructureTemplate;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.function.Supplier;

@ForEachTest(groups = "conduit.item")
public class FluidConduitTests {

    private static final String THREE_TANKS = EnderIOConduits.MODULE_MOD_ID + ":fluid_conduit_three_tanks";

    // @formatter:off
    @RegisterStructureTemplate(THREE_TANKS)
    public static final Supplier<StructureTemplate> THREE_TANKS_TEMPLATE = StructureTemplateBuilder.lazy(3, 1, 3,
        builder -> builder
            // Sender
            .set(2, 0, 0, MachineBlocks.FLUID_TANK.get().defaultBlockState())

            // Receivers
            .set(0, 0, 2, MachineBlocks.FLUID_TANK.get().defaultBlockState())
            .set(2, 0, 2, MachineBlocks.FLUID_TANK.get().defaultBlockState())
    );
    // @formatter:on

    @GameTest
    @TestHolder(description = "Tests basic fluid conduit functionality.")
    public static void fluidConduitBasicTransfer(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder
            .withSize(1, 1, 3)
            .set(0, 0, 0, MachineBlocks.FLUID_TANK.get().defaultBlockState())
            .set(0, 0, 2, MachineBlocks.FLUID_TANK.get().defaultBlockState()));

        test.onGameTest(FluidConduitGameTestHelper.class, helper -> {
            var fluidConduit = helper.getConduit(Conduits.FLUID);
            final int tickRate = fluidConduit.value().networkTickRate();

            helper.startSequence()
                // Destroy any previous conduit
                .thenExecute(() -> helper.setBlock(0, 1, 1, Blocks.AIR))
                // Place conduits between the tanks
                .thenExecute(() -> helper.placeConduit(fluidConduit, 0, 1, 1))
                // Configure the extract end
                .thenExecute(() -> helper
                    .getConduitBundle(0, 1, 1, false)
                    .setConnectionConfig(fluidConduit, Direction.NORTH,
                        FluidConduitConnectionConfig.DEFAULT.withIsExtract(true).withIsInsert(false).withExtractRedstoneControl(RedstoneControl.NEVER_ACTIVE)))
                // Configure the insert end
                .thenExecute(() -> helper
                    .getConduitBundle(0, 1, 1, false)
                    .setConnectionConfig(fluidConduit, Direction.SOUTH, FluidConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
                // Put some water in the tank we'll extract from
                .thenExecute(() -> helper.fillContainer(0, 1, 0, Fluids.WATER, 1))
                // Ensure the fluid is still there
                .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(0, 1, 0, Fluids.WATER, 1))
                // Now enable movement with redstone control
                .thenExecute(() -> helper
                    .getConduitBundle(0, 1, 1, false)
                    .setConnectionConfig(fluidConduit, Direction.NORTH,
                        FluidConduitConnectionConfig.DEFAULT.withIsExtract(true).withIsInsert(false).withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
                // Ensure the fluid moves
                .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(0, 1, 2, Fluids.WATER, 1))
                // Ensure no duplication
                .thenExecute(() -> helper.assertContainerHasExactly(0, 1, 0, Fluids.WATER, 0))
                .thenSucceed();
        });
    }

    @GameTest(template = THREE_TANKS)
    @TestHolder(description = "Ensures fluid conduits prioritise closest container first.")
    public static void fluidConduitDistancePriority(final FluidConduitGameTestHelper helper) {
        var fluidConduit = helper.getConduit(Conduits.FLUID);
        final int tickRate = fluidConduit.value().networkTickRate();

        helper.startSequence()
            // Destroy all previous conduits
            .thenExecute(() -> helper.fillAir(1, 1, 0, 1, 1, 2))
            // Place conduits between the tanks
            .thenExecute(() -> helper.fillConduits(fluidConduit, 1, 1, 0, 1, 1, 2))
            // Configure the insert ends
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 2, false)
                .setConnectionConfig(fluidConduit, Direction.EAST, FluidConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 2, false)
                .setConnectionConfig(fluidConduit, Direction.WEST, FluidConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
            // Configure the extract end
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(fluidConduit, Direction.EAST,
                    FluidConduitConnectionConfig.DEFAULT.withIsExtract(true).withIsInsert(false).withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            // Put some water in the tank we'll extract from
            .thenExecute(() -> helper.fillContainer(2, 1, 0, Fluids.WATER, 1))
            // Ensure the fluid moves to the closer tank
            .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(2, 1, 2, Fluids.WATER, 1))
            .thenExecute(() -> helper.assertContainerHasExactly(0, 1, 2, Fluids.WATER, 0))
            .thenSucceed();
    }

    @GameTest(template = THREE_TANKS)
    @TestHolder(description = "Ensures fluid conduits prioritise highest priority container first, before closest.")
    public static void fluidConduitManualPriority(final FluidConduitGameTestHelper helper) {
        var fluidConduit = helper.getConduit(Conduits.PRESSURIZED_FLUID);
        final int tickRate = fluidConduit.value().networkTickRate();

        if (!((FluidConduit)fluidConduit.value()).doesSupportPriority()) {
            throw new IllegalStateException("Fluid conduit does not support priority, fix the test to use a conduit that does.");
        }

        helper.startSequence()
            // Destroy all previous conduits
            .thenExecute(() -> helper.fillAir(1, 1, 0, 1, 1, 2))
            // Place conduits between the tanks
            .thenExecute(() -> helper.fillConduits(fluidConduit, 1, 1, 0, 1, 1, 2))
            // Configure the insert ends
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 2, false)
                .setConnectionConfig(fluidConduit, Direction.EAST, FluidConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true)))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 2, false)
                .setConnectionConfig(fluidConduit, Direction.WEST, FluidConduitConnectionConfig.DEFAULT.withIsExtract(false).withIsInsert(true).withPriority(2)))
            // Configure the extract end
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(fluidConduit, Direction.EAST,
                    FluidConduitConnectionConfig.DEFAULT.withIsExtract(true).withIsInsert(false).withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            // Put some fluid in the tank we'll extract from
            .thenExecute(() -> helper.fillContainer(2, 1, 0, Fluids.WATER, 1))
            // Ensure the fluid moves to the closer tank
            .thenExecuteAfter(tickRate, () -> helper.assertContainerHasExactly(0, 1, 2, Fluids.WATER, 1))
            .thenExecute(() -> helper.assertContainerHasExactly(2, 1, 2, Fluids.WATER, 0))
            .thenSucceed();
    }

    // TODO: Test round robin when we add it

    // TODO: Test fluid filters?
}
